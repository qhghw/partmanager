package com.xuelang.partmanage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webbuilder.utils.StringUtil;

public class ShipAction {
	public void geSql(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String name = StringUtil.fetchString(request, "eShipName");
		String begin = StringUtil.fetchString(request, "eatBegdate");
		String end = StringUtil.fetchString(request, "eatEnddate");
		String voyage = StringUtil.fetchString(request, "voyage");
		StringBuffer sql = new StringBuffer("");
		if (!StringUtil.isEmpty(name)) {
			sql.append("  and  E_SHIP_NAM like '%" + name + "%'");
		}
		if (!StringUtil.isEmpty(begin)) {
			begin = begin.substring(0, 10);
			sql.append("  and  to_char(ETA_TIM,'YYYY-MM-DD') >= '" + begin
					+ "'");
		}
		if (!StringUtil.isEmpty(end)) {
			end = end.substring(0, 10);
			sql.append("  and  to_char(ETA_TIM,'YYYY-MM-DD') <= '" + end + "'");
		}
		if (!StringUtil.isEmpty(voyage)) {
			sql.append("  and  OUT_VOYAGE_NO  like '%" + voyage + "%'");
		}
		request.setAttribute("whereSql", sql.toString());

	}
}
