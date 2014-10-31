package com.partmanager.biz.ziliao.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.partmanager.flow.entity.AgentProcess;
import com.partmanager.pojo.Gys;

public class AgentProcessDTO {

	private String taskid;
	private String assignee;
	private String processinstid;
	private String taskname;
	private String startuser;
	private String username;
	private String starttime;
	private String createtime;
	private String businesskey;

	public AgentProcessDTO() {
		super();
	}

	public AgentProcessDTO(String taskid, String assignee, String processinstid, String taskname,
			String startuser, String username,String starttime,String createtime,String businesskey) {
		super();
		this.taskid = taskid;
		this.assignee = assignee;
		this.processinstid = processinstid;
		this.taskname = taskname;
		this.startuser = startuser;
		this.username = username;
		this.starttime = starttime;
		this.createtime = createtime;
		this.businesskey = businesskey;
	}

	public static AgentProcessDTO createDto(AgentProcess pojo) {
		AgentProcessDTO dto = null;
		if (pojo != null) {
			dto = new AgentProcessDTO(pojo.getTaskid(), pojo.getAssignee(),
					pojo.getProcessinstid(), pojo.getTaskname(),
					pojo.getStartuser(),pojo.getUsername(),pojo.getStarttime(),pojo.getCreatetime(),
					pojo.getBusinesskey());
		}
		return dto;
	}

	@SuppressWarnings("unchecked")
	public static List createDtos(Collection pojos) {
		List<AgentProcessDTO> list = new ArrayList<AgentProcessDTO>();
		if (pojos != null) {
			Iterator iterator = pojos.iterator();
			while (iterator.hasNext()) {
				list.add(createDto((AgentProcess) iterator.next()));
			}
		}
		return list;
	}
	
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

	public String getStartuser() {
		return startuser;
	}

	public void setStartuser(String startuser) {
		this.startuser = startuser;
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

}
