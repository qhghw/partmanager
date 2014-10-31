package com.webbuilder.fileupload;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Upload.java Create on 2008-12-15 下午02:40:04
 * 
 * 说明:上传文件
 * 
 * Copyright (c) 2008 by MTA.
 * 
 * @author 廖瀚卿
 * @version 1.0
 */
@SuppressWarnings("serial")
public class Upload extends HttpServlet {

	public Upload() {
		super();
	}

	public void destroy() {
		super.destroy();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		List items = null;
		try {
			items = (List) upload.parseRequest(request);
		} catch (FileUploadException e1) {
			e1.printStackTrace();
		}
		String saveReportPath = "E:/partmanager/upload/";
		 response.setCharacterEncoding("UTF-8");    
		MyUtils.mkDirectory(saveReportPath);
		boolean flag = false;
		Iterator iter = items.iterator();
		while (iter.hasNext()) {
			FileItem item = (FileItem) iter.next();
			if (item.getContentType() != null) {
				String newFileName = MyUtils.randomRename(item.getName(), saveReportPath);
				String reportAbsFilePath = saveReportPath + newFileName; // 文件保存路径
				File file = new File(reportAbsFilePath);
				
				try {
					item.write(file); // 开始上传写入服务器
					flag = true;
				} catch (Exception e) {
					flag = false;
					e.printStackTrace();
				} finally {
					file = null;
					item = null;
					if (flag) {
						response.getWriter().print("{success:true,msg:'成功'}"); 
					} else {
						response.getWriter().write("{success:\"false\"}");
					}
					flag = false;
				}

			}
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}

	public void init() throws ServletException {
	}

}
