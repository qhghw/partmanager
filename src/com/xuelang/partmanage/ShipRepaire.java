package com.xuelang.partmanage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webbuilder.utils.StringUtil;

public class ShipRepaire {
	// 船舶维修保养计划
	public void getSql(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String name = StringUtil.fetchString(request, "name");
		// String C_SHIP_NAM = StringUtil.fetchString(request, "C_SHIP_NAM");
		StringBuffer sql = new StringBuffer("");
		if (!StringUtil.isEmpty(name)) {
			sql.append("  and  PLAN_NAM like '%" + name + "%'");
		}
		// if (!StringUtil.isEmpty(C_SHIP_NAM)) {
		// sql.append("  and C_SHIP_NAM like '%" + C_SHIP_NAM + "%'");
		// }

		request.setAttribute("whereSql", sql.toString());

	}
}
