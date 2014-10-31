package com.partmanager.flow.entity;

import java.io.Serializable;

public class AgentProcess implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String taskid;
	
	private String assignee;
	
	private String processinstid;
	
	private String taskname;
	
	private String startuser;
	
	private String username;
	
	private String starttime;
	
	private String createtime;
	
	private String businesskey;
	
	private String flowtype;
	
	private String instname;
	
	private String businesscod;
	
	private String taskdefid;
	
	private String businessurl;

	public String getTaskid() {
		return taskid;
	}

	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	public String getProcessinstid() {
		return processinstid;
	}

	public void setProcessinstid(String processinstid) {
		this.processinstid = processinstid;
	}

	public String getTaskname() {
		return taskname;
	}

	public void setTaskname(String taskname) {
		this.taskname = taskname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public String getBusinesskey() {
		return businesskey;
	}

	public void setBusinesskey(String businesskey) {
		this.businesskey = businesskey;
	}

	public String getInstname() {
		return instname;
	}

	public void setInstname(String instname) {
		this.instname = instname;
	}

	public String getStartuser() {
		return startuser;
	}

	public void setStartuser(String startuser) {
		this.startuser = startuser;
	}

	public String getFlowtype() {
		return flowtype;
	}

	public void setFlowtype(String flowtype) {
		this.flowtype = flowtype;
	}

	public String getBusinesscod() {
		return businesscod;
	}

	public void setBusinesscod(String businesscod) {
		this.businesscod = businesscod;
	}

	public String getTaskdefid() {
		return taskdefid;
	}

	public void setTaskdefid(String taskdefid) {
		this.taskdefid = taskdefid;
	}

	public String getBusinessurl() {
		return businessurl;
	}

	public void setBusinessurl(String businessurl) {
		this.businessurl = businessurl;
	}
	
	
}
