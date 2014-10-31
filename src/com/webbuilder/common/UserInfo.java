package com.webbuilder.common;

import javax.servlet.http.HttpServletRequest;

import com.partmanager.utils.system.UserHttpService;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class UserInfo {
	public String getUserInfo(HttpServletRequest request) {
		String val="",key = "";
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "com/cxstock/utils/system/client.xml" });
		UserHttpService client = (UserHttpService) context
				.getBean("httpInvokeProxy");
		System.out.println("key=====" + request.getParameter("key"));
		// String userName = req.getParameter("userName");
		// session.setAttribute("userName", userName);
		// System.out.println("userName-----------------" + userName);

		if (request.getParameter("key") != null) {
			key = request.getParameter("key");
			val = client.getUserAndtenat(key);
			System.out.println("val-----------------" + val);
			// if("".equals(session.getAttribute("key"))||session.getAttribute("key")==null)
			// val=client.getUserAndtenat("1");
			// else
	}
		return val;
	}
}
