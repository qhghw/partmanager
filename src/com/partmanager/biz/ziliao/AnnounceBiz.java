package com.partmanager.biz.ziliao;

import java.util.List;

import com.partmanager.flow.entity.AgentProcess;
import com.partmanager.flow.entity.FileDetail;
import com.partmanager.pojo.Announce;

public interface AnnounceBiz {
	

	/**
	 * 公告列表
	 */
	public List<Announce> findAnnounce();
	
	public Announce findAnnounceById(String guid);
	
	public List<AgentProcess> findProcessList(String username,String userrole);
	
	
	public List<FileDetail> findFilesList(Integer userid);
	
}
