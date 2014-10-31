package com.partmanager.flow.entity;

public class FileDetail {

	private String id;
	
	private String manageid;
	
	private String filetype;
	
	private String filedescribe;
	
	private String filename;
	
	private String filesize;
	
	private String url;
	
	private String recordtim;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getManageid() {
		return manageid;
	}

	public void setManageid(String manageid) {
		this.manageid = manageid;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilesize() {
		return filesize;
	}

	public void setFilesize(String filesize) {
		this.filesize = filesize;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setFiletype(String filetype) {
		this.filetype = filetype;
	}

	public String getFiletype() {
		return filetype;
	}

	public void setFiledescribe(String filedescribe) {
		this.filedescribe = filedescribe;
	}

	public String getFiledescribe() {
		return filedescribe;
	}

	public void setRecordtim(String recordtim) {
		this.recordtim = recordtim;
	}

	public String getRecordtim() {
		return recordtim;
	}
	
	
}
