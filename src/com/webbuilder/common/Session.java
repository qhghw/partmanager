/*     */ package com.webbuilder.common;
/*     */ 
/*     */ import com.webbuilder.controls.Query;
/*     */ import com.webbuilder.utils.DateUtil;
/*     */ import com.webbuilder.utils.DbUtil;
/*     */ import com.webbuilder.utils.Encrypter;
/*     */ import com.webbuilder.utils.StringComparator;
/*     */ import com.webbuilder.utils.StringUtil;
/*     */ import com.webbuilder.utils.SysUtil;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.Date;
/*     */ import java.util.TreeSet;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import javax.servlet.http.HttpSession;
/*     */ import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
/*     */ 
/*     */ public class Session
/*     */ {
/*  25 */   private static ConcurrentHashMap<String, SessionListener> userList = null;
/*     */ 
/*     */   public void getUserList(HttpServletRequest request, HttpServletResponse response) throws IOException
/*     */   {
/*  29 */     if ((request.getSession() == null) || (userList == null)) {
/*  30 */       response.getWriter().print("{total:0,row:[]}");
/*  31 */       return;
/*     */     }
/*  33 */     StringBuilder users = new StringBuilder();
/*     */ 
/*  36 */     int index = 0;
/*  37 */     boolean isFirst = true;
/*  38 */     StringComparator sc = new StringComparator();
/*  39 */     TreeSet orderKeys = new TreeSet(sc);
/*     */ 
/*  41 */     String start = StringUtil.fetchString(request, "start");
/*     */     int startValue;
/*     */     
/*  42 */     if (start == null)
/*  43 */       startValue = 1;
/*     */     else
/*  45 */       startValue = Integer.parseInt(start) + 1;
/*  46 */     String limit = StringUtil.fetchString(request, "limit");
/*     */     int limitValue;

/*  47 */     if (limit == null)
/*  48 */       limitValue = 2147483647 - startValue;
/*     */     else
/*  50 */       limitValue = Integer.parseInt(limit);
/*  51 */     int end = startValue + limitValue - 1;
/*  52 */     users.append("{total:");
/*  53 */     users.append(userList.size());
/*  54 */     users.append(",row:[");
/*  55 */     for (String key : userList.keySet())
/*  56 */    {
/*  57 */   
/*  58 */       index++;
/*  59 */       if (index < startValue)
/*     */         continue;
/*  61 */       if (index > end)
/*     */         break;
/*  63 */       if (isFirst)
/*  64 */         isFirst = false;
/*     */       else
/*  66 */         users.append(",");
/*  67 */       SessionListener listener = (SessionListener)userList.get(key);
/*  68 */       users.append("{userName:\"");
/*  69 */       users.append(listener.userName);
/*  70 */       users.append("\",ip:\"");
/*  71 */       users.append(listener.ip);
/*  72 */       users.append("\",loginTime:\"");
/*  73 */       users.append(DateUtil.dateToString(
/*  74 */         new Date(listener.session
/*  74 */         .getCreationTime())));
/*  75 */       users.append("\",lastAccess:\"");
/*  76 */       users.append(DateUtil.dateToString(
/*  77 */         new Date(listener.session
/*  77 */         .getLastAccessedTime())));
/*  78 */       users.append("\"}");
/*     */     
/*  80 */     users.append("]}");}
/*  81 */     response.getWriter().print(users.toString());
/*     */   }
/*     */ 
/*     */   public void createLoginScript(HttpServletRequest request, HttpServletResponse response) throws Exception
/*     */   {
/*  86 */     boolean needVerify = StringUtil.getStringBool((String)request
/*  87 */       .getAttribute("sys.loginVerify"));
/*     */ 
/*  89 */     if (!needVerify) {
/*  90 */       request.setAttribute("loginHeight", "208");
/*  91 */       return;
/*     */     }
/*  93 */     request.setAttribute("loginHeight", "238");
/*  94 */     String html = "<div id=\"verifyPanel\" class=\"x-hidden\"><img id=\"verifyImage\" src=\"main?action=webbuilder/system/getVerifyImage.xwl\"></div>";
/*  95 */     StringBuilder ext = new StringBuilder();
/*  96 */     ext
/*  97 */       .append(",{text:\"验证码：\",xtype:\"label\",style:\"text-align:right\",width:65,x:20,y:135}");
/*  98 */     ext
/*  99 */       .append(",{id:\"verifyCode\",name:\"verifyCode\",minLength:5,minLengthText:\"验证码长度为5位\",maxLength:5,maxLengthText:\"验证码长度为5位\",allowBlank:false,blankText:\"请输入右图中的验证码\",xtype:\"textfield\",width:70,x:95,y:130}");
/* 100 */     ext
/* 101 */       .append(",{bodyStyle:\"background-color:transparent\",layout:\"absolute\",border:false,xtype:\"panel\",contentEl:\"verifyPanel\",x:174,y:131,width:95,height:25}");
/* 102 */     ext
/* 103 */       .append(",{html:\"<a href='javascript:changeVerifyImage();' title='更换图片中的验证码'>更换</a>\",xtype:\"label\",width:30,x:275,y:135}");
/* 104 */     request.setAttribute("htmlScript", html);
/* 105 */     request.setAttribute("extScript", ext.toString());
/*     */   }
/*     */ 
/*     */   public void verify(HttpServletRequest request, HttpServletResponse response)
/*     */     throws Exception
/*     */   {
/* 111 */     String userName = StringUtil.fetchString(request, "username");
/* 112 */     String referer = request.getHeader("Referer");

/*     */ 
/* 116 */     if ((StringUtil.isEmpty(referer)) || 
/* 117 */       (referer.indexOf("main?action=webbuilder/system/login.xwl") != -1))
/* 118 */       referer = "main?action=webbuilder/application/portal/portal.xwl";
/* 119 */     if (StringUtil.isEmpty(userName)) {
/* 120 */       throw new Exception("请输入用户名称。");
/*     */     }
/* 122 */     userName = userName.toLowerCase();

/* 132 */     Query query = new Query();
/*     */ 
/* 136 */     query.setRequest(request);
/* 137 */     query.sql = "select PASSWORD,ROLENAME,SCOPE,REAL_NAME,CODE,LAST_ACCESS,ROOT_PATH,CHAR1,CHAR2,STATUS,(select  shipper_doc  from  vi_shipper_doc where shipper_COD=char1) from WB_USER where USERNAME={?username?}";
/* 138 */     query.jndi = StringUtil.fetchString(request, "sys.jndi");
/* 139 */     query.setName("query.WB_USER");
/* 140 */     query.create();
/* 141 */     ResultSet resultSet = (ResultSet)request.getAttribute("query.WB_USER");
/* 142 */     if (!DbUtil.setResultSetToFirst(resultSet)) {
/* 143 */       throw new Exception("用户名称 \"" + userName + "\" 不存在。");
/*     */     }
/* 145 */     String password = resultSet.getString(1);
/* 146 */     String inputPass = StringUtil.fetchString(request, "password");
/* 147 */     if (inputPass != null)
/* 148 */       inputPass = Encrypter.getMD5(inputPass);
/* 149 */     if (!StringUtil.isEqual(password, inputPass))
/* 150 */       throw new Exception("密码无效。");
		
/* 151 */     String timeout = StringUtil.fetchString(request, "sys.sessionTimeout");
/* 152 */     String role = resultSet.getString(2); String scope = resultSet.getString(3);
/* 153 */     String realname = resultSet
/* 153 */       .getString(4); String code = resultSet.getString(5);
/* 154 */     Timestamp st = resultSet.getTimestamp(6);
/* 155 */     String rootPath = resultSet.getString(7);
				String clientCod=resultSet.getString(8);
				String clientTyp=resultSet.getString(9);
				int status = resultSet.getInt(10);
				String shipperDoc=resultSet.getString(11);
				if(status==1)
				{
					 throw new Exception("账户冻结。");
				}
/* 156 */     SysUtil.checkRunMode((String)request.getAttribute("sys.runMode"), 
/* 157 */       role, scope);
/* 158 */     HttpSession session = request.getSession(true);
/*     */     try
/*     */     {
/*     */       String lastAccess;

/* 160 */       if (st == null)
/* 161 */         lastAccess = "首次登录";
/*     */       else
/* 163 */         lastAccess = "上次登录时间" + DateUtil.dateToString(st);
/* 164 */       if (rootPath == null)
/* 165 */         rootPath = "";
/*     */       else
/* 167 */         rootPath = request.getAttribute("sys.path").toString() + 
/* 168 */           "WEB-INF/myfile/" + rootPath;
/* 169 */       session.setAttribute("sys.logined", Integer.valueOf(1));
/* 170 */       session.setAttribute("sys.user", userName);
/* 171 */       session.setAttribute("sys.role", role);
/* 172 */       session.setAttribute("sys.scope", scope);
/* 173 */       session.setAttribute("sys.realname", realname);
/* 174 */       session.setAttribute("sys.code", code);
/* 175 */       session.setAttribute("sys.lastaccess", lastAccess);
/* 176 */       session.setAttribute("sys.rootpath", rootPath);
				session.setAttribute("sys.clientCod", clientCod);
				session.setAttribute("sys.shipperDoc", shipperDoc);
				session.setAttribute("sys.clientTyp", clientTyp);
/* 177 */       request.setAttribute("sys.user", userName);
/* 178 */       request.setAttribute("sys.role", role);
/* 179 */       request.setAttribute("sys.scope", scope);
/* 180 */       request.setAttribute("sys.realname", realname);
/* 181 */       request.setAttribute("sys.code", code);
/* 182 */       request.setAttribute("sys.lastaccess", lastAccess);
/* 183 */       request.setAttribute("sys.rootpath", rootPath);
/* 184 */       if (!StringUtil.isEmpty(timeout))
/* 185 */         session.setMaxInactiveInterval(Integer.parseInt(timeout));
/* 186 */       response.getWriter().print(referer);
/* 187 */       addSessionListener(session, request, userName);
/*     */     } catch (Exception e) {
/* 189 */       if (session != null)
/* 190 */         session.invalidate();
/* 191 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
/* 197 */     HttpSession session = request.getSession(false);
/*     */ 
/* 199 */     if (session != null)
/* 200 */       session.invalidate();
/* 201 */     response.sendRedirect("main?action=webbuilder/system/logoutTo.xwl");
/*     */   }
/*     */ 
/*     */   private void addSessionListener(HttpSession session, HttpServletRequest request, String userName)
/*     */   {
/* 206 */     SessionListener sessionListener = new SessionListener();
/*     */ 
/* 208 */     sessionListener.userName = userName;
/* 209 */     sessionListener.session = session;
/* 210 */     sessionListener.needRecord = StringUtil.getStringBool(request
/* 211 */       .getAttribute("sys.recordSession").toString());
/* 212 */     sessionListener.ip = request.getRemoteAddr();
/* 213 */     String logType = request.getAttribute("sys.logType").toString();
/* 214 */     sessionListener.needLog = ((StringUtil.isEqual(logType, "all")) || 
/* 215 */       (StringUtil.isEqual(logType, "access")));
/* 216 */     if (sessionListener.needLog) {
/* 217 */       sessionListener.jndi = ((String)request.getAttribute("sys.jndi"));
/* 218 */       sessionListener.charset = 
/* 219 */         ((String)request
/* 219 */         .getAttribute("sys.dbCharset"));
/*     */     }
/* 221 */     session.setAttribute("sys.listener", sessionListener); } 
/*     */   private class SessionListener implements HttpSessionBindingListener { public String userName;
/*     */     public HttpSession session;
/*     */     public String jndi;
/*     */     public String charset;
/*     */     public String ip;
/*     */     public boolean needLog;
/*     */     public boolean needRecord;
/*     */     private String trueName;
/*     */ 
/*     */     private SessionListener() {  } 
/* 235 */     public void valueBound(HttpSessionBindingEvent event) { recordSession(false);
/*     */       try {
/* 237 */         if (this.needLog)
/* 238 */           DbUtil.recordLog(this.jndi, this.userName, this.ip, "登录", this.charset, 1);
/*     */       }
/*     */       catch (Exception localException) {
/*     */       } }
/*     */ 
/*     */     public void valueUnbound(HttpSessionBindingEvent event) {
/* 244 */       recordSession(true);
/*     */       try {
/* 246 */         if (this.needLog)
/* 247 */           DbUtil.recordLog(this.jndi, this.userName, this.ip, "退出", this.charset, 1);
/*     */       } catch (Exception localException) {
/*     */       }
/*     */     }
/*     */ 
/*     */     private void recordSession(boolean isRemove) {
/* 253 */       if (this.needRecord) {
/* 254 */         if (Session.userList == null)
/* 255 */           Session.userList = new ConcurrentHashMap();
/*     */       }
/* 257 */       else return;
/* 258 */       if (isRemove) {
/* 259 */         if (!StringUtil.isEmpty(this.trueName))
/* 260 */           Session.userList.remove(this.trueName);
/*     */       } else {
/* 262 */         int i = 1;
/* 263 */         this.trueName = this.userName;
/* 264 */         while (Session.userList.containsKey(this.trueName))
/* 265 */           this.trueName = (this.userName + "|" + Integer.toString(i++));
/* 266 */         Session.userList.put(this.trueName, this);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           Z:\EXT\WebBuilderServer (1)\WEB-INF\lib\webbuilder2.jar
 * Qualified Name:     com.webbuilder.common.Session
 * JD-Core Version:    0.6.0
 */