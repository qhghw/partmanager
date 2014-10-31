package com.partmanager.pojo;

import java.util.Date;

public class Announce {

	private String guid;
	private String title;
	private String content;
	private String activeMan;
	private Date activeDate;
	private Date unactiveDate;
	private String status;
	private String scope;
	private Date recordTim;
	private String recordMan;
	private String deptcod;
	private String userid;
	private String remarks;
	
	public Announce(){}
	
	public Announce(String guid,String title,String content,String activeMan,Date activeDate,Date unactiveDate,
			String status,String scope,Date recordTim,String recordMan,String deptcod,String userid,String remarks){
		this.guid = guid;
		this.title = title;
		this.content = content;
		this.activeMan = activeMan;
		this.activeDate = activeDate;
		this.unactiveDate = unactiveDate;
		this.status = status;
		this.scope = scope;
		this.recordTim = recordTim;
		this.recordMan = recordMan;
		this.deptcod = deptcod;
		this.userid = userid;
		this.remarks = remarks;
	}
	
	
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getActiveMan() {
		return activeMan;
	}
	public void setActiveMan(String activeMan) {
		this.activeMan = activeMan;
	}
	public Date getActiveDate() {
		return activeDate;
	}
	public void setActiveDate(Date activeDate) {
		this.activeDate = activeDate;
	}
	public Date getUnactiveDate() {
		return unactiveDate;
	}
	public void setUnactiveDate(Date unactiveDate) {
		this.unactiveDate = unactiveDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public Date getRecordTim() {
		return recordTim;
	}
	public void setRecordTim(Date recordTim) {
		this.recordTim = recordTim;
	}
	public String getRecordMan() {
		return recordMan;
	}
	public void setRecordMan(String recordMan) {
		this.recordMan = recordMan;
	}
	public String getDeptcod() {
		return deptcod;
	}
	public void setDeptcod(String deptcod) {
		this.deptcod = deptcod;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	
	
}
