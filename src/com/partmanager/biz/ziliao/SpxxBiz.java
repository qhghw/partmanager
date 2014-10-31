package com.partmanager.biz.ziliao;

import com.partmanager.pojo.Spxx;
import com.partmanager.utils.pubutil.Page;

public interface SpxxBiz {
	
	/**
	 * 商品编号
	 */
	public String getSpxxCode();
	
	/**
	 * 分页查询商品列表
	 */
	public void findPageSpxx(Page page);
	
	/**
	 * 保存商品
	 */
	public void save(Spxx dto);
	
	/**
	 * 修改改商品
	 */
	public void updateSpxx(Spxx dto);
	
	/**
	 * 删除商品
	 */
	public boolean deleteSpxx(String spid);


}
