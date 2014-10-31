package com.xuelang.partmanage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webbuilder.utils.StringUtil;

public class ContactCargoAction {
	public void geSql(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String name = StringUtil.fetchString(request, "sName");
		String sVoyage = StringUtil.fetchString(request, "sVoyage");
		String wtf = StringUtil.fetchString(request, "wtf");
		String begin = StringUtil.fetchString(request, "begin");
		String end = StringUtil.fetchString(request, "end");
		String PM_COD = StringUtil.fetchString(request, "PM_COD");
		StringBuffer sql = new StringBuffer("");

		if (!StringUtil.isEmpty(name)) {
			sql.append("  and  s.C_SHIP_NAM like '%" + name + "%'");
		}
		if (!StringUtil.isEmpty(sVoyage)) {
			sql.append("  and s.OUT_VOYAGE_NO like '%" + sVoyage + "%'");
		}
		if (!StringUtil.isEmpty(wtf)) {
			sql.append("  and cf.PARTY_A_COD = '" + wtf + "'");
		}

		if (!StringUtil.isEmpty(begin)) {
			begin = begin.substring(0, 10);
			sql.append("  and  to_char(cf.CONTRACT_DTE,'YYYY-MM-DD') >= '"
					+ begin + "'");
		}
		if (!StringUtil.isEmpty(end)) {
			end = end.substring(0, 10);
			sql.append("  and  to_char(cf.CONTRACT_DTE,'YYYY-MM-DD') <= '"
					+ end + "'");
		}
		if (!StringUtil.isEmpty(PM_COD)) {
			sql.append("  and  cf.TENTANTID ='" + PM_COD + "'");
		}
		request.setAttribute("whereSql", sql.toString());

	}
}
