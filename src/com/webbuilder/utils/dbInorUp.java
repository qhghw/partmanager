package com.webbuilder.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;



public class dbInorUp {

	public static Connection fetchConnection(HttpServletRequest request,
			String jndi) throws Exception {
		Object obj = request.getAttribute("jndi." + jndi);
		Connection conn;
		if ((obj == null) || (!(obj instanceof Connection))) {
			conn = getConnection(jndi);
			request.setAttribute("jndi." + jndi, conn);
		} else {
			conn = (Connection) obj;
		}
		return conn;
	}
	public static Connection getConnection(String jndi) throws NamingException,
	SQLException {
		InitialContext ctx = new InitialContext();
		DataSource ds = (DataSource) ctx.lookup(jndi);
		return ds.getConnection();
}
	/***********************************************************************   
	 *   
	 *   向表插入数据通用方法    
	 *   @copyright       Copyright:   2012     
	 *   @creator        郭小伟<br/>   
	 *   @create-time  2012-1-5
	 *   @revision         $Id:     *   
	 ***********************************************************************/
	public static void importMapStream(
			String tableName,List<HashMap<String, Object>>  list) throws Exception {
		Connection conn=getConnect();
		Statement stm = conn.createStatement();
		ResultSet rs = stm.executeQuery("select * from " + tableName +
		" where 1=0");
		ResultSetMetaData meta = rs.getMetaData();

		int[] types = (int[]) null;//存储列类型字段。
		
		int k = 0;
		
		boolean hasData = false;
		
		PreparedStatement st = null;
		
	//	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		conn.setAutoCommit(false);
		try {
		
			int x = list.size();//数据行数
			int y = meta.getColumnCount();//求出多少列
		//	int l = y;
			int j = meta.getColumnCount();
			String fieldSql="";
			String valSql="";
			String[] typeName=new String[y]; //存储列名字段。
			types = new int[y];//保存字段类型
			for (int i = 1; i < j+1; i++){
				typeName[i-1]=meta.getColumnName(i);
				types[i-1]=Integer
				.valueOf(meta.getColumnType(i)); 
				if(i!=j){
				fieldSql+=meta.getColumnName(i)+",";
				valSql+="?,";}
				else {
					fieldSql+=meta.getColumnName(i);
					valSql+="?";
				}
			}
				rs.close();//关闭rs
				st = conn.prepareStatement("insert into "
						+ tableName +
						" (" + fieldSql + ") values(" + valSql
						+ ")");
			for (int n = 0; n < x; n++)//循环行数
				{
					for (int i = 0; i < y; i++) {//循环列数
						Object s;
						HashMap<String,Object> map=list.get(n);
						s = getMapValue(map, typeName[i]);
						if (s==null)
							st.setNull(i + 1, types[i]);
						else {
							switch (types[i]) {
							case -6:
							case -5:
							case 4:
							case 5:
								st.setInt(i + 1, Integer.parseInt(
								StringUtil.replace(s.toString(), ",", "")));
								break;
							case 2:
							case 3:
							case 7:
							case 8:	
								st.setDouble(i + 1,
								Double.parseDouble(StringUtil
										.replace(s.toString(), ",",
										"")));
								break;
							case 6:
								st.setFloat(i + 1, Float.parseFloat(
								StringUtil.replace(s.toString(), ",", "")));
								break;
							case 91:
							case 93:		
								st.setTimestamp(i + 1,new Timestamp(((Date)s).getTime()));
								break;
							default:		
								st.setObject(i + 1, s);
							}
						}
					}
					hasData = true;
					st.addBatch();
					k++;
					if ((k > 0) && (k % 1000 == 0)) {
						st.executeBatch();
						hasData = false;
					}
				}
			if (hasData)
				st.executeBatch();
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			throw new Exception(e);
		} finally {
			
		conn.setAutoCommit(true);
			if (stm != null)
				stm.close();
			if (st != null)
				st.close();
			if(conn!=null)
				conn.close();
		}
	}
	public static Connection getConnect() {
		Connection conn = null;
		try {
			Context intitCtx = new InitialContext();
			Context envCtx = (Context) intitCtx.lookup("java:comp/env");
			DataSource ds = (DataSource) envCtx.lookup("jdbc/jt_tg");//唐钢数据
			conn = ds.getConnection();
		} catch (NamingException nex) {
			nex.printStackTrace();
		} catch (SQLException sqlex) {
			sqlex.printStackTrace();
		}
		return conn;
	}
	private static Object getMapValue(HashMap<String,Object> map,String columnName)
	{
		
		if(columnName.equals("CFITNUM")&&map.get(columnName)==null)//在此修改列名字段 ，主要是针对唐钢和京唐港数据库字段不一致。 
			columnName="FITNUM";
		return map.get(columnName);
	}
	
}
