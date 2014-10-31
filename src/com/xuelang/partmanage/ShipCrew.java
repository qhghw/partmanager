package com.xuelang.partmanage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webbuilder.utils.StringUtil;

public class ShipCrew {
	// 船员与合同关联
	public void crewContact(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String begin = StringUtil.fetchString(request, "begin");
		String end = StringUtil.fetchString(request, "end");
		String p = StringUtil.fetchString(request, "p");
		String name = StringUtil.fetchString(request, "name");
		StringBuffer sql = new StringBuffer("");
		if (!StringUtil.isEmpty(begin)) {
			begin = begin.substring(0, 10);
			sql.append("  and  to_char(SIGN_DTE,'YYYY-MM-DD') >= '" + begin
					+ "'");
		}
		if (!StringUtil.isEmpty(end)) {
			end = end.substring(0, 10);
			sql
					.append("  and  to_char(SIGN_DTE,'YYYY-MM-DD') <= '" + end
							+ "'");
		}
		if (!StringUtil.isEmpty(p)) {
			sql.append("  and  DP.BUSINESS='" + p + "'");
		}
		if (!StringUtil.isEmpty(name)) {
			sql.append("  and  DP.C_CREW_NAM like '%" + name + "%'");
		}
		request.setAttribute("whereSql", sql.toString());
	}

	// 船员基本信息
	public void getCrewSql(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String name = StringUtil.fetchString(request, "name");
		String zt = StringUtil.fetchString(request, "zt");
		// String DEV_NO = StringUtil.fetchString(request, "DEV_NO");
		// String C_SHIP_NAM = StringUtil.fetchString(request, "C_SHIP_NAM");
		StringBuffer sql = new StringBuffer("");
		if (!StringUtil.isEmpty(name)) {
			sql.append("  and ( C_CREW_NAM like '%" + name
					+ "%' or E_CREW_NAM like '%" + name + "%')");
		}
		if (!StringUtil.isEmpty(zt)) {
			if ("1".equals(zt)) {
				zt = "预备";
			}
			if ("2".equals(zt)) {
				zt = "计划上船";
			}
			if ("3".equals(zt)) {
				zt = "在船";
			}
			if ("4".equals(zt)) {
				zt = "计划下船";
			}
			if ("5".equals(zt)) {
				zt = "历史";
			}
			sql.append("  and STATUS = '" + zt + "'");
		}

		// if (!StringUtil.isEmpty(DEV_NO)) {
		// sql.append("  and DEV_NO like '%" + DEV_NO + "%'");
		// }
		//
		request.setAttribute("whereSql", sql.toString());

	}

	public void getCrewSql1(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String p = StringUtil.fetchString(request, "p");
		String ht = StringUtil.fetchString(request, "ht");
		StringBuffer sql = new StringBuffer("");
		if (!StringUtil.isEmpty(p)) {
			sql.append("  and BUSINESS='" + p + "'");
		}
		if (!StringUtil.isEmpty(ht)) {
			sql.append("  and HOME_TOWN='" + ht + "'");
		}
		request.setAttribute("WhereSql", sql.toString());

	}
}
