package com.xuelang.partmanage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webbuilder.utils.StringUtil;

public class ShipStatus {
	// 船员计划信息
	public void getStatusSql(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String name = StringUtil.fetchString(request, "name");
		//String C_SHIP_NAM = StringUtil.fetchString(request, "C_SHIP_NAM");
		String begin = StringUtil.fetchString(request, "begin");
		String end = StringUtil.fetchString(request, "end");
		StringBuffer sql = new StringBuffer("");
		if (!StringUtil.isEmpty(name)) {
			sql.append("  and ( C_CREW_NAM like '%" + name
					+ "%' or E_CREW_NAM like '%" + name + "%')");
		}
		// if (!StringUtil.isEmpty(C_SHIP_NAM)) {
		// sql.append("  and C_SHIP_NAM like '%" + C_SHIP_NAM + "%'");
		// }
		if (!StringUtil.isEmpty(begin)) {
			begin = begin.substring(0, 10);
			sql.append("  and  to_char(begin,'YYYY-MM-DD') >= '" + begin + "'");
		}
		if (!StringUtil.isEmpty(end)) {
			end = end.substring(0, 10);
			sql.append("  and  to_char(begin,'YYYY-MM-DD') <= '" + end + "'");
		}
		request.setAttribute("whereSql", sql.toString());

	}
}
