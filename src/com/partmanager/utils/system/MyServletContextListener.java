package com.partmanager.utils.system;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.json.JSONObject;

import net.sf.json.JSON;

import com.webbuilder.utils.DbUtil;
import com.webbuilder.utils.Language;

public class MyServletContextListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		Connection con = null;
		ResultSet rs=null;
		Statement s=null;
		Language.getEnClient();
		Language.getZnClient();
		HashMap<String,String>  hMap=new   HashMap<String,String> ();
		try {
			con = DbUtil.getConnection("java:comp/env/jdbc/orcale_news");
			s=con.createStatement();
			String  fldBj="";//过度标志  记录上次数据
			rs=s.executeQuery("select FLD_ID,FLD_NAM, SYS_COD, C_MEAN_STR,E_MEAN_STR ,TENTANTID from   S_SYS  order  by FLD_ID ");
			while(rs.next())
			{
				String  fldId=rs.getString(1);
				
				String  fldNam=rs.getString(2);
				String  sysCod=rs.getString(3);
				String  cName=rs.getString(4);
				String  eName=rs.getString(5);
				String  tentantId=rs.getString(6);//暂时不用，为了区分多租户的数据
				if(!fldBj.equals(fldId)&&!"".equals(fldBj)){//判断本次fldId和上次的数据是否一致。
													//如果是不一致需要重新购造hMap 并且把数据放到 
					DbUtil.map.put(fldId, hMap);
					hMap= new   HashMap<String,String> ();
				}
				hMap.put(sysCod, cName);			
				fldBj=fldId;//过度标志  记录上次数据
			}
			DbUtil.map.put(fldBj, hMap);//为了记录最后一次的数据。
			

		}
		catch(Exception ex){
			ex.printStackTrace();
		} 
		finally
		{
			try{
				if(rs!=null)
					rs.close();
				if(s!=null)
					s.close();
				if(con!=null)
					con.close();
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
			
		}
	}

}
