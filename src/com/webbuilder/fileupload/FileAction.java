package com.webbuilder.fileupload;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * FileAction.java Create on 2008-12-18 上午09:16:22
 * 
 * 说明:文件处理
 * 
 * Copyright (c) 2008 by yourgame.
 * 
 * @author 
 * @version 1.0
 */
@SuppressWarnings("serial")
public class FileAction extends BaseAction {
	private Map<String, Object> infos = new HashMap<String, Object>();

	public static final String ROOT = "upload\\";

	private File myUpload;

	private String myUploadContentType;

	private String myUploadFileName;

	private String path;

	private boolean success;

	/**
	 * 上传文件
	 * 
	 * @return
	 */
	public String uploadFiles() {
		String rootPath = getSession().getServletContext().getRealPath("/");
		rootPath += ROOT;
		String sp = rootPath + getPath();
		MyUtils.mkDirectory(sp);
		try {
			MyUtils.upload(getMyUploadFileName(), sp, getMyUpload());
			this.success = true;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}

	public File getMyUpload() {
		return myUpload;
	}

	public void setMyUpload(File myUpload) {
		this.myUpload = myUpload;
	}

	public String getMyUploadContentType() {
		return myUploadContentType;
	}

	public void setMyUploadContentType(String myUploadContentType) {
		this.myUploadContentType = myUploadContentType;
	}

	public String getMyUploadFileName() {
		return myUploadFileName;
	}

	public void setMyUploadFileName(String myUploadFileName) {
		this.myUploadFileName = myUploadFileName;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) throws UnsupportedEncodingException {
		this.path = URLDecoder.decode(path, "UTF-8");
	}

	public Map<String, Object> getInfos() {
		return infos;
	}

	public void setInfos(Map<String, Object> infos) {
		this.infos = infos;
	}
}


