package com.xuelang.partmanage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webbuilder.utils.StringUtil;

public class ShipManCod {
	// 备件物料代码维护过渡字段
	public void getSmPmCodTyp(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String PM_TYP = StringUtil.fetchString(request, "PM_TYP");
		request.setAttribute("PM_TYP", PM_TYP);

	}
}
