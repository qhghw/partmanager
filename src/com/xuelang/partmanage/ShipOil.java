package com.xuelang.partmanage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webbuilder.utils.StringUtil;

public class ShipOil {
	// 加油记录
	public void getOilSql(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String begin = StringUtil.fetchString(request, "begin");
		String end = StringUtil.fetchString(request, "end");
		StringBuffer sql = new StringBuffer("");

		if (!StringUtil.isEmpty(begin)) {
			begin = begin.substring(0, 10);
			sql.append("  and  to_char(ETD_TIM,'YYYY-MM-DD hh24:mi:ss') >= '" + begin
					+ "'");
		}
		if (!StringUtil.isEmpty(end)) {
			end = end.substring(0, 10);
			sql.append("  and  to_char(ETD_TIM,'YYYY-MM-DD hh24:mi:ss') <= '" + end + "'");
		}
		request.setAttribute("whereSql", sql.toString());

	}

	public void getSql(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String begin = StringUtil.fetchString(request, "begin");
		String end = StringUtil.fetchString(request, "end");
		StringBuffer sql = new StringBuffer("");
		StringBuffer sql1 = new StringBuffer("");
		if (!StringUtil.isEmpty(begin)) {
			begin = begin.substring(0, 10);
			sql.append("  and  to_char(LEAVETIM,'YYYY-MM-DD') >= '" + begin
					+ "'");	
		}
		if (!StringUtil.isEmpty(end)) {
			end = end.substring(0, 10);
			sql.append("  and  to_char(ARRIVETIM,'YYYY-MM-DD') <= '" + end
					+ "'");
		}
		request.setAttribute("whereSql", sql.toString());
	}
}
