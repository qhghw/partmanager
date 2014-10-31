package com.partmanager.biz.ziliao.imp;

import java.util.List;

import com.partmanager.biz.ziliao.SpdwBiz;
import com.partmanager.biz.ziliao.dto.SpdwDTO;
import com.partmanager.dao.BaseDAO;
import com.partmanager.pojo.Spdw;

@SuppressWarnings("unchecked")
public class SpdwBizImpl implements SpdwBiz {
	
	private BaseDAO baseDao;
	public void setBaseDao(BaseDAO baseDao) {
		this.baseDao = baseDao;
	}

	public List findAllSpdw() {
		return baseDao.listAll("Spdw");
	}

	/*
	 * 保存/修改商品
	 */
	public void saveOrUpdateSpdw(SpdwDTO dto) {
		Spdw spdw = new Spdw();
		if(dto.getDwid()!=null){
			spdw = (Spdw)baseDao.loadById(Spdw.class, dto.getDwid());
		}
		spdw.setDwname(dto.getDwname());
		baseDao.saveOrUpdate(spdw);
	}
	
	/*
	 * 删除商品
	 */
	public void deleteSpdw(Integer dwid) {
		baseDao.deleteById(Spdw.class, dwid);	
	}

}
