package com.partmanager;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.TimerTask;



public class MyTask extends TimerTask {

  public void run() {
	  //
	     String url="jdbc:mysql://localhost:3306/partmanager?useUnicode=true&amp;characterEncoding=UTF-8"; //orcl为数据库的SID 
		 String user="root"; 
		 String password="root"; 
		 Connection conn = null;
		try {
			conn = DriverManager.getConnection(url,user,password);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 if (conn != null)
		 {
			 CallableStatement c =null;
			  try{  
	            c=conn.prepareCall("{call Proc_AAmz713(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");    
	            c.execute();  
			   }catch(Exception e){  
	
		        }finally{  
		            if(c==null)
						try {
							c.close();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}  
		        }  
            }
  }

}

