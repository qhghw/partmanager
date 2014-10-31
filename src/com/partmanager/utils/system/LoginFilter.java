package com.partmanager.utils.system;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.partmanager.utils.system.UserHttpService;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class LoginFilter implements Filter {
	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		String key = "";
		String val = "";

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		HttpSession session = req.getSession();

		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "com/partmanager/utils/system/client.xml" });
		UserHttpService client = (UserHttpService) context
				.getBean("httpInvokeProxy");
		System.out.println("key=====" + request.getParameter("key"));
		// String userName = req.getParameter("userName");
		// session.setAttribute("userName", userName);
		// System.out.println("userName-----------------" + userName);
		String val1 = "", val2 = "", val3 = "", val4 = "", val5 = "";
		if (request.getParameter("key") != null) {
			key = request.getParameter("key");
			val = client.getUserAndtenat(key);
			System.out.println("val-----------------" + val);
			// if("".equals(session.getAttribute("key"))||session.getAttribute("key")==null)
			// val=client.getUserAndtenat("1");
			// else

			if (session.getAttribute("user") == null) {
				if ((val != null) && !"".equals(val)) {
					if (val.length() > 0) {
						String[] ar = val.split("@");
						val1 = ar[0];
						val2 = ar[1];
						val3 = ar[2];
						val4 = ar[3];
						val5 = ar[4];

					}
				}
			}
			// val1 = request.getParameter("userid");
		/*	System.out.println("val1-----------------" + val1);
			System.out.println("val2-----------------" + val2);
			System.out.println("val2-----------------" + val3);
			System.out.println("val4-----------------" + val4);
			System.out.println("val5-----------------" + val5);*/
			// System.out.println("zuhu "+val1);
			
			request.setAttribute("sys.userId", "0");
			request.setAttribute("sys.tentantId", "0");
			request.setAttribute("sys.username", "admin");
			//session.setAttribute("zuhuID", key);
			GlobalUtil.User_key = val1;

			System.out.println("GlobalUtil.User_key----------------");
			/*
			 * if ("0".equals(key)) {// 管理员 WebUtil.ZUHU_ID = true; } else
			 * WebUtil.ZUHU_ID = false;
			 */
		}

		// session.setAttribute("zuhuID", "0");
		// WebUtil.ZUHU_ID = false;
		chain.doFilter(request, response);
	}

	public void init(FilterConfig filterConfig) throws ServletException {
	}
}
