package com.xuelang.partmanage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webbuilder.utils.StringUtil;

public class StorageCargoAction {
	public void geSql(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String name = StringUtil.fetchString(request, "name");
		String p = StringUtil.fetchString(request, "p");
		String d = StringUtil.fetchString(request, "d");
		String c = StringUtil.fetchString(request, "cCod");
		String begin = StringUtil.fetchString(request, "begin");
		String end = StringUtil.fetchString(request, "end");
		String wtf = StringUtil.fetchString(request, "wtf");
		String PM_COD = StringUtil.fetchString(request, "PM_COD");
		StringBuffer sql = new StringBuffer("");
		if (!StringUtil.isEmpty(name)) {
			sql.append("  and ( s.C_SHIP_NAM like '%" + name
					+ "%' or s.OUT_VOYAGE_NO like '%" + name + "%')");
		}
		if (!StringUtil.isEmpty(begin)) {
			begin = begin.substring(0, 10);
			sql.append("  and  to_char(STORAGE_DTE,'YYYY-MM-DD') >= '" + begin
					+ "'");
		}
		if (!StringUtil.isEmpty(wtf)) {
			sql.append("  and PAYER_COD = '" + wtf + "'");
		}
		if (!StringUtil.isEmpty(end)) {
			end = end.substring(0, 10);
			sql.append("  and  to_char(STORAGE_DTE,'YYYY-MM-DD') <= '" + end
					+ "'");
		}
		if (!StringUtil.isEmpty(p)) {
			sql.append("  and LOAD_PORT_COD ='" + p + "'");
		}
		if (!StringUtil.isEmpty(d)) {
			sql.append("  and LOAD_DOCK_COD ='" + d + "'");
		}
		if (!StringUtil.isEmpty(c)) {
			// if ("1".equals(c)) {
			// c = "I";
			// }
			// if ("2".equals(c)) {
			// c = "Z";
			// }
			// if ("3".equals(c)) {
			// c = "D";
			// }
			// if ("4".equals(c)) {
			// c = "O";
			// }
			sql.append("  and CARGO_ID ='" + c + "'");
		}
		if (!StringUtil.isEmpty(PM_COD)) {
			sql.append("  and  TENTANTID ='" + PM_COD + "'");
		}
		request.setAttribute("whereSql", sql.toString());

	}
}
