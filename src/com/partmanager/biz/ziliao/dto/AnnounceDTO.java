package com.partmanager.biz.ziliao.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.partmanager.pojo.Announce;
import com.partmanager.pojo.Gys;

public class AnnounceDTO {

	private String guid;
	private String title;
	private String content;
	private String activeMan;
	private Date activeDate;
	private Date unactiveDate;

	public AnnounceDTO() {
		super();
	}

	public AnnounceDTO(String guid,String title,String content,String activeMan,Date activeDate,Date unactiveDate) {
		super();
		this.guid = guid;
		this.title = title;
		this.content = content;
		this.activeMan = activeMan;
		this.activeDate = activeDate;
		this.unactiveDate = unactiveDate;
	}

	public static AnnounceDTO createDto(Announce pojo) {
		AnnounceDTO dto = null;
		if (pojo != null) {
			dto = new AnnounceDTO(pojo.getGuid(), pojo.getTitle(),
					pojo.getContent(), pojo.getActiveMan(),
					pojo.getActiveDate(),pojo.getUnactiveDate());
		}
		return dto;
	}

	@SuppressWarnings("unchecked")
	public static List createDtos(Collection pojos) {
		List<AnnounceDTO> list = new ArrayList<AnnounceDTO>();
		if (pojos != null) {
			Iterator iterator = pojos.iterator();
			while (iterator.hasNext()) {
				list.add(createDto((Announce) iterator.next()));
			}
		}
		return list;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getGuid() {
		return guid;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setActiveMan(String activeMan) {
		this.activeMan = activeMan;
	}

	public String getActiveMan() {
		return activeMan;
	}

	public void setActiveDate(Date activeDate) {
		this.activeDate = activeDate;
	}

	public Date getActiveDate() {
		return activeDate;
	}

	public void setUnactiveDate(Date unactiveDate) {
		this.unactiveDate = unactiveDate;
	}

	public Date getUnactiveDate() {
		return unactiveDate;
	}


}
