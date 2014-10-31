package com.xuelang.partmanage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webbuilder.utils.StringUtil;

public class SelfCargo {
	public void geSql(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String begin = StringUtil.fetchString(request, "begin");
		String end = StringUtil.fetchString(request, "end");
		String PM_COD = StringUtil.fetchString(request, "PM_COD");
		StringBuffer sql = new StringBuffer("");
		if (!StringUtil.isEmpty(begin)) {
			begin = begin.substring(0, 10);
			sql.append("  and  to_char(SELF_DTE,'YYYY-MM-DD') >= '" + begin
					+ "'");
		}
		if (!StringUtil.isEmpty(end)) {
			end = end.substring(0, 10);
			sql
					.append("  and  to_char(SELF_DTE,'YYYY-MM-DD') <= '" + end
							+ "'");
		}
		if (!StringUtil.isEmpty(PM_COD)) {
			sql.append("  and  TENTANTID ='" + PM_COD + "'");
		}
		request.setAttribute("whereSql", sql.toString());

	}
}