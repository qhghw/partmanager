package com.webbuilder.fileupload;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xuelang.common.web.ConnectDB;
public class DownLoad extends HttpServlet {
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		
		String fileId = request.getParameter("id");
		Connection connect = ConnectDB.getConnection();
		Statement st;
		String filePath = "";
		String fileName = ""; // 文件名，输出到用户的下载对话框
		try {
			st = connect.createStatement();
			StringBuffer sb = new StringBuffer();
			sb.append("select filename,url from file_detail where id='"+fileId+"'");
			ResultSet rs = st.executeQuery(sb.toString());
			while(rs.next()){
				fileName = rs.getString("filename");
				filePath = rs.getString("url");
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		// 打开指定文件的流信息
		java.io.FileInputStream fs = null;
		try {
		    java.io.File file = new java.io.File(filePath+fileName);
		    file.setReadOnly(); 
		    file.setWritable(true);  
			fs = new java.io.FileInputStream(file);
		} catch (java.io.FileNotFoundException e) {
			e.printStackTrace();
		}
		// 设置响应头和保存文件名
		//response.setContentType("APPLICATION/OCTET-STREAM");
		//response.setHeader("Content-Disposition", "attachment; filename=\""
		//+ fileName + "\"");
		response.setHeader("Pragma", "public");
		response.setHeader("Expires", "0");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
		response.setHeader("Content-Type", "application/force-download");
		response.setHeader("Content-Disposition", (new StringBuilder("attachment;  filename=")).append(new String(fileName.getBytes(), "UTF-8")).toString());
		
		// 写出流信息
		int b = 0;
		try {
			java.io.PrintWriter out = response.getWriter();
			while ((b = fs.read()) != -1) {
			out.write(b);
		}
		fs.close();
		out.close();
		System.out.println("文件下载完毕.");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("下载文件失败!");
		}
	}
}	