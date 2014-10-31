/*     */package com.webbuilder.common;

/*     */
/*     */import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.partmanager.biz.power.dto.UserDTO;
import com.partmanager.utils.system.Constants;
import com.partmanager.utils.system.UserHttpService;
import com.webbuilder.task.TaskControl;
import com.webbuilder.utils.DateUtil;
import com.webbuilder.utils.DbUtil;
import com.webbuilder.utils.FileUtil;
import com.webbuilder.utils.StringUtil;
import com.webbuilder.utils.SysUtil;
import com.webbuilder.utils.WebUtil;
 
/*     */
/*     */public class Main extends HttpServlet
/*     */implements Servlet
/*     */{
	/*     */private static final long serialVersionUID = 1L;

	/*     */
	/*     */protected void doGet(HttpServletRequest request,
			HttpServletResponse response)
	/*     */throws ServletException, IOException
	/*     */{
		String val = "", val1 = "", val2 = "", val3 = "", val4 = "", val5 = "";
		/*     */try
		/*     */{
			/* 35 */request.setCharacterEncoding("utf-8");
			/* 36 */response.setContentType("text/html;charset=utf-8");
			/* 37 */if (request.getRequestURI().toLowerCase().endsWith(".xwl")) {
				/* 38 */response.setStatus(403);
				/* 39 */return;
				/*     */}

			System.out.println(request.getSession().getAttribute(
					"sys.tentantId"));
			System.out.println(null == request.getSession().getAttribute(
					"sys.userId"));

			ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
					new String[] { "com/partmanager/utils/system/client.xml" });
			UserHttpService client = (UserHttpService) context
					.getBean("httpInvokeProxy");
			String key = request.getParameter("key");
			System.out.println("key=====@@@@@@@@@!!!!!!!!!" + key);
			System.out.println(StringUtil.replace(getServletContext().getRealPath("/"),"\\", "/"));
			System.out.println(getServletContext().getResource("/"));
			UserDTO userInfo = (UserDTO) request.getSession().getAttribute(Constants.USERINFO);
			//验证是否已经登录
			if (userInfo==null){
				//session过期,转向session过期提示页,最终跳转至登录页面
				response.sendRedirect(request.getContextPath()+"/login.jsp");
			}	
			 request.setAttribute("sys.userId", userInfo.getUserid());
			 request.setAttribute("sys.username", userInfo.getUsername());
			 request.setAttribute("sys.deptCode", userInfo.getDept_cod());
			 request.setAttribute("sys.userrole", userInfo.getRolename());
			 request.setAttribute("sys.tentantId", "0");
			 request.getSession().setAttribute("sys.userId", userInfo.getUserid());
			 request.getSession().setAttribute("sys.userrole", userInfo.getRolename());
			 request.getSession().setAttribute("sys.tentantId", "0");
			 request.getSession().setAttribute("sys.username", userInfo.getUsername());
			 request.getSession().setAttribute("sys.logincode", userInfo.getLogincode());
			 request.setAttribute("sys.shipCod", "YANTTAI");
			 request.setAttribute("sys.ip", request.getRemoteAddr());

			/* 41 */String webPath = StringUtil.replace(getServletContext()
					.getRealPath("/"),
			/* 42 */"\\", "/")+"/";
			System.out.println(webPath);
			/* 43 */bufferToRequest(request, webPath);
			/* 44 */FileUtil.saveFileToRequest(request);
			/* 45 */String action = request.getParameter("action");
			/* 46 */File actionFile = FileUtil.getFullFile(webPath, action);
			/* 47 */String type = FileUtil.extractFileExt(action);
			/* 48 */if (actionFile != null) {
				/* 49 */if (type.equalsIgnoreCase("xwl")) {
					/* 50 */new Parser(webPath, actionFile, request, response,
							action)
					/* 51 */.parse();
					/*     */} else {
					/* 53 */String needLogin = request.getAttribute(
							"sys.needLogin")
					/* 54 */.toString();
					/* 55 */if (!WebUtil.checkLogin(needLogin, request,
							response))
						/* 56 */return;
					/* 57 */if (!WebUtil.userHasRight(needLogin, actionFile,
							webPath,
							/* 58 */request)) {
						/* 59 */response.setStatus(403);
						/* 60 */request.getRequestDispatcher(
						/* 61 */"main?action=webbuilder/system/forbidden.xwl")
						/* 62 */.forward(request, response);
						/* 63 */return;
						/*     */}
					/* 65 */WebUtil.recordLog(request, action, 1);
					/* 66 */response.reset();
					/* 67 */if (StringUtil.stringInList(StringUtil.split(
							request
							/* 68 */.getAttribute("sys.webFile").toString(),
							","), type
					/* 69 */.toLowerCase()) != -1) {
						/* 70 */boolean isMark = request.getParameter("__mark") != null;
						/*     */try {
							/* 72 */request.getRequestDispatcher(action)
									.forward(
									/* 73 */request, response);
							/* 74 */if (!isMark)
								return;
							response.getWriter().print("{@ok@}");
							/*     */}
						/*     */catch (Exception e)
						/*     */{
							/*     */String exceptType;
							/*     */
							/* 79 */if (isMark)
								/* 80 */exceptType = "mark";
							/*     */else
								/* 82 */exceptType = request.getAttribute(
								/* 83 */"sys.exceptionType").toString();
							/* 84 */WebUtil.recordLog(request, action + ":" +
							/* 85 */SysUtil.getShortError(e), 3);
							/* 86 */WebUtil.showException(exceptType, e,
									request,
									/* 87 */response);
							/*     */}
						/*     */} else {
						/* 90 */response.setHeader("content-length",
						/* 91 */Long.toString(actionFile.length()));
						/* 92 */response.setHeader("content-type",
						/* 93 */"application/force-download");
						/* 94 */String charset = (String) request
						/* 95 */.getAttribute("sys.fileCharset");
						/* 96 */response.setHeader("content-disposition",
						/* 97 */"attachment;filename=" +
						/* 98 */WebUtil.getFileName(actionFile
						/* 99 */.getName(), charset));
						/* 100 */FileInputStream inputStream = new FileInputStream(
						/* 101 */actionFile);

						/* 102 */SysUtil.inputStreamToOutputStream(inputStream,
								response
								/* 103 */.getOutputStream());
						/* 104 */inputStream.close();
						/*     */}
					/*     */}
				/* 107 */} else if (StringUtil
					.isEmpty(request.getQueryString())) {
				/* 108 */response.sendRedirect(
				/* 109 */(String) request
				/* 109 */.getAttribute("sys.portal"));
				/*     */} else {
				/* 111 */response.setStatus(404);
				/* 112 */request.getRequestDispatcher(
				/* 113 */"main?action=webbuilder/system/invalid.xwl").forward(
				/* 114 */request, response);
				/*     */}
			/*     */} catch (Exception e) {
			/* 117 */throw new ServletException(e);
		}
		File actionFile;
		/*     */String type;
		/*     */String action;
		/*     */String webPath;
	}

	/* 123 */protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	/*     */
	/*     */public void init() throws ServletException
	/*     */{
		/* 127 */super.init();
		/* 128 */startTasks();
		/*     */}

	/*     */
	/*     */private void bufferToRequest(HttpServletRequest request, String webPath)
			throws Exception
	/*     */{
		/* 133 */String[] list = XMLData.getBuffer(request, webPath);
		/* 134 */int j = list.length;
		/*     */
		/* 136 */for (int i = 0; i < j; i += 2)
			/* 137 */request.setAttribute(list[i], list[(i + 1)]);
		/* 138 */request.setAttribute("sys.path", webPath);
		/* 139 */request.setAttribute("sys.ip", request.getRemoteAddr());
		/* 140 */Date dt = new Date();
		/* 141 */request.setAttribute("sys.now", DateUtil.dateToString(dt));
		/* 142 */request
				.setAttribute("sys.nowdate", DateUtil.formatStdDate(dt));
		/* 143 */request.setAttribute("sys.id", DateUtil.formatDate(dt,
		/* 144 */"yyyyMMddHHmmssSSS") +
		/* 145 */StringUtil.formatFloat(Math.random() * 999.0D, "000"));
		/*     */}

	/*     */
	/*     */private void startTasks() {
		/* 149 */UserRequest request = new UserRequest();
		/* 150 */UserResponse response = new UserResponse();
		/*     */try
		/*     */{
			/* 153 */bufferToRequest(request, StringUtil.replace(
					getServletContext()
					/* 154 */.getRealPath("/"), "\\", "/"));
			/* 155 */if (StringUtil.getStringBool(request.getAttribute(
					"sys.startTask")
			/* 156 */.toString()))
				/* 157 */new TaskControl().startTasks(request, response);
			/*     */} catch (Exception e) {
			/*     */try {
				/* 160 */String logType = request.getAttribute("sys.logType")
						.toString();
				/* 161 */if ((StringUtil.isEqual(logType, "all")) ||
				/* 162 */(StringUtil.isEqual(logType, "exception")))
					/* 163 */DbUtil.recordLog(request.getAttribute("sys.jndi")
					/* 164 */.toString(), "system", "127.0.0.1", "启动任务失败",
					/* 165 */(String) request.getAttribute("sys.dbCharset"), 2);
				/*     */}
			/*     */catch (Exception localException1)
			/*     */{
				/*     */}
			/*     */}
		/*     */}
	/*     */
}

/*
 * Location: Z:\EXT\WebBuilderServer (1)\WEB-INF\lib\webbuilder2.jar Qualified
 * Name: com.webbuilder.common.Main JD-Core Version: 0.6.0
 */