package com.xuelang.common.web;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webbuilder.utils.StringUtil;



public class DatabaseServiceAction {

	public void Insert(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
				  Map map = request.getParameterMap();
				  String jndi = StringUtil.fetchString(request, "sys.jndi");
			        String tableName = StringUtil.fetchString(request, "tableName");
				  ConnectDB.setConnStr(jndi);
				  Connection conn = ConnectDB.connToDB();
					Statement st = conn.createStatement();
					ResultSet resultSet = st.executeQuery("describe "+tableName);
					String selectSql = "insert  into " + tableName + " ( " ;
					StringBuffer sql = new StringBuffer("");
					StringBuffer sqlValues = new StringBuffer("");
					while (resultSet.next()) {
						String cName = resultSet.getString("field");
						if(map.containsKey(cName)) {
							sql.append(cName+",");
							sqlValues.append("{?"+cName+"?},");
						}
					}
					sql.append("RECORD_TIM,RECORD_MAN,DEPT_COD,USERID");
					  /*删除多余的逗号*/
		            //sql.delete(sql.length()-1, sql.length());
					sql.append(") values(");
					/*sqlValues.delete(sqlValues.length()-1, sqlValues.length());*/
					sqlValues.append("now(),'{#sys.username#}','{#sys.deptCode#}','{#sys.userid#}'");
					sql.append(sqlValues.toString());
					sql.append(")");
		           System.out.println("-----------UPDATE-----------  "+selectSql + sql.toString());
		}
	  public void Update(HttpServletRequest request, HttpServletResponse response) throws Exception
	    {
		  Map map = request.getParameterMap();
		  String jndi = StringUtil.fetchString(request, "sys.jndi");
	        String tableName = StringUtil.fetchString(request, "tableName");
		  ConnectDB.setConnStr(jndi);
		  Connection conn = ConnectDB.connToDB();
			Statement st = conn.createStatement();
			ResultSet resultSet = st.executeQuery("describe "+tableName);
			String selectSql = "UPDATE  " + tableName + " SET " ;
			StringBuffer sql = new StringBuffer("");
			while (resultSet.next()) {
				String cName = resultSet.getString("field");
				if(map.containsKey(cName)) {
					sql.append(cName);
					sql.append("={?");
					sql.append(cName+"?},");
				}
			}
			sql.append("RECORD_TIM=now(),RECORD_MAN='{#sys.username#}',DEPT_COD='{#sys.deptCode#}',USERID='{#sys.userid#}'");
			  /*删除多余的逗号*/
           // sql.delete(sql.length()-1, sql.length());
            System.out.println("-----------UPDATE-----------  "+selectSql + sql.toString() + " WHERE ");
            Insert( request,  response);
	    }
	    
} 
