package com.xuelang.common.web;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.webbuilder.utils.DbUtil;
 
public class ConnectDB
{
	  /**数据库连接对象*/
    public static Connection conn = null;
    public static String connStr = null;
    private ConnectDB()
    { 

    }
    
    //获取jndi数据源
    public static Connection getConnection() {  
    	Connection con = null;
        try {
            Context ic = new InitialContext();
            DataSource source = (DataSource)ic.lookup("java:comp/env/jdbc/mysql");         
           con = source.getConnection();      
       } catch (NamingException e) {
            System.out.println("数据源没找到！");
           e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("获取数连接对象失败！");
            e.printStackTrace();
        }
        return con;
    }

    /**
     * 连接数据库,并返回数据库连接对象
     * @return
     */
    public static Connection connToDB()
    {
        try {
			try {
				conn = DbUtil.getConnection(connStr);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return conn;
    }

	public static String getConnStr() {
		return connStr;
	}

	public static void setConnStr(String connStr) {
		ConnectDB.connStr = connStr;
	}

	public static void releaseDB(ResultSet resultSet, Statement statement,
            Connection conn)
    {

        try
        {
            if (resultSet != null)
            {
                resultSet.close();
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                if(statement != null)
                {
                    statement.close();
                }
            } catch (SQLException e)
            {
                e.printStackTrace();
            } finally
            {
                try
                {
                    conn.close();
                } catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
    
