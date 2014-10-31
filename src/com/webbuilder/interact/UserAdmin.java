/*     */ package com.webbuilder.interact;
/*     */ 
/*     */ import com.webbuilder.utils.DateUtil;
/*     */ import com.webbuilder.utils.DbUtil;
/*     */ import com.webbuilder.utils.Encrypter;
/*     */ import com.webbuilder.utils.FileUtil;
/*     */ import com.webbuilder.utils.JsonUtil;
/*     */ import com.webbuilder.utils.StringUtil;
/*     */ import java.io.File;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.sql.Connection;
/*     */ import java.sql.ResultSet;
/*     */ import java.util.HashMap;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import javax.servlet.http.HttpSession;
/*     */ import org.json.JSONArray;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class UserAdmin
/*     */ {
/*     */   public void verifyUpdateRole(HttpServletRequest request, HttpServletResponse response)
/*     */     throws Exception
/*     */   {
/*  26 */     verifyRole(true, request);
/*     */   }
/*     */ 
/*     */   public void verifyInsertRole(HttpServletRequest request, HttpServletResponse response) throws Exception
/*     */   {
/*  31 */     verifyRole(false, request);
/*     */   }
/*     */ 
/*     */   private void verifyRole(boolean isUpdate, HttpServletRequest request) throws Exception
/*     */   {
/*  36 */     boolean isAdmin = StringUtil.stringInList(request
/*  37 */       .getAttribute("sys.role").toString().split(","), "管理员") != -1;
/*  38 */     if (!isAdmin) {
/*  39 */       String adminRole = JsonUtil.getTreeSubText((String)request
/*  40 */         .getAttribute("roleQuery.KEY_VALUE"), "管理员");
/*  41 */       String setRoles = (String)request.getAttribute("ROLENAME");
/*  42 */       String[] adminList = StringUtil.split(adminRole, ",");
/*  43 */       String[] setList = StringUtil.split(setRoles, ",");
/*     */ 
/*  45 */       if (isUpdate) {
/*  46 */         String oldRoles = (String)request
/*  47 */           .getAttribute("getRole.ROLENAME");
/*  48 */         String[] oldList = StringUtil.split(oldRoles, ",");
/*  49 */         String[] reservedRole = StringUtil.andJoin(adminList, oldList);
/*  50 */         if (reservedRole.length > 0) {
/*  51 */           String password = StringUtil.fetchString(request, 
/*  52 */             "PASSWORD");
/*  53 */           String oldPass = (String)request
/*  54 */             .getAttribute("getRole.PASSWORD");
/*  55 */           if ((!StringUtil.isEqual(password, "{@!init!@}")) && 
/*  56 */             (!StringUtil.isEqual(Encrypter.getMD5(password), 
/*  57 */             Encrypter.getMD5(oldPass))))
/*  58 */             throw new Exception("权限不足，不能更改管理类角色用户密码。");
/*  59 */           if (!StringUtil.isEqual((String)request
/*  60 */             .getAttribute("USERNAME"), (String)request
/*  61 */             .getAttribute("oldName")))
/*  62 */             throw new Exception("权限不足，不能更改管理类角色用户名称。");
/*     */         }
/*  64 */         if (StringUtil.notJoin(setList, reservedRole).length != setList.length - reservedRole.length)
/*  65 */           throw new Exception("权限不足，不能移去员工如下角色中的任意一项：" + 
/*  66 */             StringUtil.joinArray(reservedRole, ","));
/*  67 */         adminList = StringUtil.notJoin(adminList, oldList);
/*     */       }
/*  69 */       boolean excepted = StringUtil.stringsCross(adminList, setList, false);
/*  70 */       if (excepted)
/*  71 */         throw new Exception("权限不足，不能设置员工包含如下角色中的任意一项：" + 
/*  72 */           StringUtil.joinArray(adminList, ","));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void checkSelUser(HttpServletRequest request, HttpServletResponse response) throws Exception
/*     */   {
/*  78 */     String s = (String)request.getAttribute("query.USERNAME");
/*  79 */     request.setAttribute("USERNAME", s);
/*     */   }
/*     */ 
/*     */   public void delUserFolder(HttpServletRequest request, HttpServletResponse response) throws Exception
/*     */   {
/*  84 */     String fn = (String)request.getAttribute("query.ROOT_PATH");
/*  85 */     String root = request.getAttribute("sys.path").toString() + 
/*  86 */       "WEB-INF/myfile";
/*     */ 
/*  88 */     if (!StringUtil.isEmpty(fn)) {
/*  89 */       File file = new File(root, fn);
/*  90 */       if (file.exists())
/*  91 */         FileUtil.deleteFolder(file);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void generateRoleTree(HttpServletRequest request, HttpServletResponse response) throws Exception
/*     */   {
/*  97 */     String s = (String)request.getAttribute("roleQuery.KEY_VALUE");
/*  98 */     if (!StringUtil.isEmpty(s))
/*  99 */       request.setAttribute("roleQuery.KEY_VALUE", 
/* 100 */         JsonUtil.addCheckBoxToTree(new JSONArray(s)));
/*     */   }
/*     */ 
/*     */   public void initScope(HttpServletRequest request, HttpServletResponse response) throws Exception
/*     */   {
/* 105 */     DbUtil.executeSqlFile(request, null, request.getAttribute("sys.path")
/* 106 */       .toString() + 
/* 107 */       "webbuilder/data/sql/initScope.txt", false, true, "utf-8");
/*     */   }
/*     */ 
/*     */   public void clearUserFile(HttpServletRequest request, HttpServletResponse response) throws Exception
/*     */   {
/* 112 */     ResultSet rs = (ResultSet)request.getAttribute("query");
/* 113 */     String root = request.getAttribute("sys.path").toString() + 
/* 114 */       "WEB-INF/myfile";
/*     */ 
/* 116 */     while (rs.next()) {
/* 117 */       String fn = rs.getString(1);
/* 118 */       if (!StringUtil.isEmpty(fn)) {
/* 119 */         File file = new File(root, fn);
/* 120 */         if (file.exists())
/* 121 */           FileUtil.deleteFolder(file);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void verifyRegTestUser(HttpServletRequest request, HttpServletResponse response) throws Exception
/*     */   {
/* 128 */     boolean isAdmin = StringUtil.stringInList(request.getAttribute(
/* 129 */       "sys.role").toString().split(","), "管理员") != -1;
/* 130 */     String[] adminList = (String[])null;
/*     */ 
/* 132 */     if (!isAdmin) {
/* 133 */       String adminRole = JsonUtil.getTreeSubText((String)request
/* 134 */         .getAttribute("roleQuery.KEY_VALUE"), "管理员");
/* 135 */       adminList = StringUtil.split(adminRole, ",");
/*     */     }
/* 137 */     String t = request.getParameter("grid");
/* 138 */     StringBuilder delUserList = new StringBuilder();
/* 139 */     JSONArray array = new JSONArray(t);
/*     */ 
/* 141 */     int j = array.length();
/* 142 */     for (int i = 0; i < j; i++) {
/* 143 */       JSONObject item = array.getJSONObject(i);
/* 144 */       String s = item.getString("USERNAME");
/* 145 */       String r = item.getString("ROLENAME");
/* 146 */       if (i > 0)
/* 147 */         delUserList.append(",");
/* 148 */       delUserList.append("'" + s + "'");
/* 149 */       if ((adminList == null) || 
/* 150 */         (!StringUtil.stringsCross(adminList, StringUtil.split(r, 
/* 151 */         ","), false))) continue;
/* 152 */       throw new Exception("权限不足，不允许删除管理类角色用户。");
/*     */     }
/* 154 */     String userList = delUserList.toString();
/* 155 */     if ((userList.indexOf("(") != -1) || (userList.indexOf(")") != -1))
/* 156 */       throw new Exception("非法的用户名称。");
/* 157 */     request.setAttribute("delUserList", userList);
/*     */   }
/*     */ 
/*     */   public void getPreviewSql(HttpServletRequest request, HttpServletResponse response) throws Exception
/*     */   {
/* 162 */     if (!StringUtil.isEmpty(request.getParameter("codeList"))) {
/* 163 */       String codeList = request.getParameter("codeList");
/* 164 */       if ((codeList.indexOf("(") != -1) || (codeList.indexOf(")") != -1))
/* 165 */         throw new Exception("非法的员工编号。");
/* 166 */       request.setAttribute("whereSql", " and CODE in ({#codeList#})");
/*     */     } else {
/* 168 */       if (!StringUtil.isEmpty(request.getParameter("userCombo")))
/* 169 */         request.setAttribute("findUserCombo", request
/* 170 */           .getParameter("userCombo"));
/* 171 */       String json = request.getParameter("jsonCondi");
/* 172 */       if (!StringUtil.isEmpty(json))
/* 173 */         JsonUtil.jsonToRequest(json, request);
/* 174 */       getWhereSql(request, response);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void getWhereSql(HttpServletRequest request, HttpServletResponse response) throws Exception
/*     */   {
/* 180 */     String f = request.getParameter("trialFlag");
/* 181 */     String user = StringUtil.fetchString(request, "findUserCombo");
/* 182 */     String dbType = request.getAttribute("sys.dbType").toString();
/*     */     String sql;
/*     */ 
/* 184 */     if (StringUtil.isEqual(f, "1")) {
/* 185 */       sql = " and TRIAL_LEN>0 and NUM7 is null";
/*     */     }
/*     */     else
/*     */     {
/*     */   
/* 186 */       if (StringUtil.isEqual(f, "2"))
/*     */       {
/*     */      
/* 187 */         if (StringUtil.isSame(dbType, "oracle")) {
/* 188 */           sql = " and TRIAL_LEN>0 and NUM7 is null and {?timestamp.sys.now?}>add_months(ENTRY_DATE,TRIAL_LEN)-10";
/*     */         }
/*     */         else
/*     */         {
/*     */     
/* 189 */           if (StringUtil.isSame(dbType, "mysql"))
/* 190 */             sql = " and TRIAL_LEN>0 and NUM7 is null and {?timestamp.sys.now?}>adddate(adddate(ENTRY_DATE,INTERVAL TRIAL_LEN month),INTERVAL-10 day)";
/*     */           else
/* 192 */             sql = " and TRIAL_LEN>0 and NUM7 is null and {?timestamp.sys.now?}>dateadd(day,-10,dateadd(month,TRIAL_LEN,ENTRY_DATE))";
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/*     */    
/* 193 */         if (!StringUtil.isEmpty(user)) {
/* 194 */           request.setAttribute("fucValue", "%" + user + "%");
/*     */     
/* 195 */           if (StringUtil.isSame(dbType, "oracle")) {
/* 196 */             sql = " and REAL_NAME||'('||CODE||')' like {?fucValue?}";
/*     */           }
/*     */           else
/*     */           {
/*     */     
/* 197 */             if (StringUtil.isSame(dbType, "mysql"))
/* 198 */               sql = " and concat(REAL_NAME,'(',CODE,')') like {?fucValue?}";
/*     */             else
/* 200 */               sql = " and REAL_NAME+'('+CODE+')' like {?fucValue?}"; 
/*     */           }
/* 201 */         } else if (StringUtil.fetchString(request, "cb__9999") != null) {
/* 202 */         sql = DbUtil.getFilterSql(request, 
/* 203 */             (ResultSet)request
/* 203 */             .getAttribute("emptyTable"), null, false);
/* 204 */           if (!StringUtil.isEmpty(sql))
/* 205 */             sql = " and (" + sql + ")";
/*     */         } else {
/* 207 */           sql = "";
/*     */         }
/*     */       }
/* 208 */     }request.setAttribute("whereSql", sql);
/*     */   }
/*     */ 
/*     */   public void getDocHtml(HttpServletRequest request, HttpServletResponse response) throws Exception
/*     */   {
/* 213 */     ResultSet rs = (ResultSet)request.getAttribute("query");
/* 214 */     StringBuilder buf = new StringBuilder();
/* 215 */     String[] ds = new String[29];
/*     */ 
/* 218 */     int j = 0;
/*     */ 
/* 220 */     HashMap map = DbUtil.initTextMaps(request, 
/* 221 */       "2=SEX,6=EDUCATION,9=MAR,11=POL,12=HUJI");
/* 222 */     while (rs.next()) {
/* 223 */       j++;
/* 224 */       for (int i = 0; i < 29; i++)
/* 225 */         switch (i) {
/*     */         case 2:
/*     */         case 14:
/* 228 */           ds[i] = StringUtil.toHTML(DateUtil.formatStdDate(rs
/* 229 */             .getTimestamp(i + 1)));
/* 230 */           break;
/*     */         case 1:
/*     */         case 5:
/*     */         case 8:
/*     */         case 10:
/*     */         case 11:
/* 236 */           String t = (String)map.get(Integer.toString(i + 1) + "_" + 
/* 237 */             rs.getString(i + 1));
/* 238 */           ds[i] = StringUtil.toHTML(t);
/* 239 */           break;
/*     */         case 3:
/*     */         case 4:
/*     */         case 6:
/*     */         case 7:
/*     */         case 9:
/*     */         case 12:
/*     */         case 13:
/*     */         default:
/* 241 */           ds[i] = StringUtil.toHTML(rs.getString(i + 1));
/*     */         }
/* 243 */       buf
/* 244 */         .append("<table width='647' cellpadding='0' class='sys_normal' cellspacing='0'>");
/* 245 */       buf.append("<tr>");
/* 246 */       buf.append("<td height='22' width='80'>员工编号</td>");
/* 247 */       buf.append("<td width='290'>" + ds[16] + "</td>");
/* 248 */       buf.append("<td width='80'>部门</td>");
/* 249 */       buf.append("<td colspan='2'>" + ds[17] + "</td>");
/*     */       String t;
/*     */     
/* 250 */       if (StringUtil.isEqual(ds[27], "1"))
/* 251 */         t = "main?action=webbuilder/application/hr/user/getImage.xwl&username=" + 
/* 252 */           ds[28];
/*     */       else
/* 254 */         t = "webbuilder/images/nophoto.gif";
/* 255 */       buf.append("<td rowspan='5' align='center'><img src='" + t + 
/* 256 */         "'></td>");
/* 257 */       buf.append("</tr>");
/* 258 */       buf.append("<tr>");
/* 259 */       buf.append("<td height='22'>姓名</td>");
/* 260 */       buf.append("<td>" + ds[0] + "</td>");
/* 261 */       buf.append("<td>性别</td>");
/* 262 */       buf.append("<td colspan='2'>" + ds[1] + "</td>");
/* 263 */       buf.append("</tr>");
/* 264 */       buf.append("<tr>");
/* 265 */       buf.append("<td height='22'>出生日期</td>");
/* 266 */       buf.append("<td>" + ds[2] + "</td>");
/* 267 */       buf.append("<td>籍贯</td>");
/* 268 */       buf.append("<td colspan='2'>" + ds[3] + "</td>");
/* 269 */       buf.append("</tr>");
/* 270 */       buf.append("<tr>");
/* 271 */       buf.append("<td height='22'>身份证号码</td>");
/* 272 */       buf.append("<td>" + ds[4] + "</td>");
/* 273 */       buf.append("<td>学历</td>");
/* 274 */       buf.append("<td colspan='2'>" + ds[5] + "</td>");
/* 275 */       buf.append("</tr>");
/* 276 */       buf.append("<tr>");
/* 277 */       buf.append("<td height='22'>毕业院校</td>");
/* 278 */       buf.append("<td>" + ds[6] + "</td>");
/* 279 */       buf.append("<td>专业</td>");
/* 280 */       buf.append("<td colspan='2'>" + ds[7] + "</td>");
/* 281 */       buf.append("</tr>");
/* 282 */       buf.append("<tr>");
/* 283 */       buf.append("<td height='22'>婚否</td>");
/* 284 */       buf.append("<td>" + ds[8] + "</td>");
/* 285 */       buf.append("<td>民族</td>");
/* 286 */       buf.append("<td colspan='3'>" + ds[9] + "</td>");
/* 287 */       buf.append("</tr>");
/* 288 */       buf.append("<tr>");
/* 289 */       buf.append("<td height='22'>政治面貌</td>");
/* 290 */       buf.append("<td>" + ds[10] + "</td>");
/* 291 */       buf.append("<td>户籍性质</td>");
/* 292 */       buf.append("<td colspan='3'>" + ds[11] + "</td>");
/* 293 */       buf.append("</tr>");
/* 294 */       buf.append("<tr>");
/* 295 */       buf.append("<td height='22'>身高 (cm)</td>");
/* 296 */       buf.append("<td>" + ds[12] + "</td>");
/* 297 */       buf.append("<td>体重 (kg)</td>");
/* 298 */       buf.append("<td colspan='3'>" + ds[13] + "</td>");
/* 299 */       buf.append("</tr>");
/* 300 */       buf.append("<tr>");
/* 301 */       buf.append("<td height='22'>职位</td>");
/* 302 */       buf.append("<td>" + ds[15] + "</td>");
/* 303 */       buf.append("<td>入职日期</td>");
/* 304 */       buf.append("<td width='94'>" + ds[14] + "</td>");
/* 305 */       buf.append("<td width='95'>试用期 (月)</td>");
/* 306 */       buf.append("<td width='108'>" + ds[18] + "</td>");
/* 307 */       buf.append("</tr>");
/* 308 */       buf.append("<tr>");
/* 309 */       buf.append("<td height='22'>开户银行</td>");
/* 310 */       buf.append("<td>" + ds[19] + "</td>");
/* 311 */       buf.append("<td>银行账号</td>");
/* 312 */       buf.append("<td colspan='3'>" + ds[20] + "</td>");
/* 313 */       buf.append("</tr>");
/* 314 */       buf.append("<tr>");
/* 315 */       buf.append("<td height='22'>电子邮件</td>");
/* 316 */       buf.append("<td>" + ds[21] + "</td>");
/* 317 */       buf.append("<td>电话号码</td>");
/* 318 */       buf.append("<td colspan='3'>" + ds[22] + "</td>");
/* 319 */       buf.append(" </tr>");
/* 320 */       buf.append("<tr>");
/* 321 */       buf.append("<td height='22'>手机号码</td>");
/* 322 */       buf.append("<td>" + ds[23] + "</td>");
/* 323 */       buf.append("<td>即时通讯</td>");
/* 324 */       buf.append("<td colspan='3'>" + ds[24] + "</td>");
/* 325 */       buf.append("</tr>");
/* 326 */       buf.append("<tr>");
/* 327 */       buf.append("<td height='22'>工作地址</td>");
/* 328 */       buf.append("<td colspan='5'>" + ds[25] + "</td>");
/* 329 */       buf.append("</tr>");
/* 330 */       buf.append("<tr>");
/* 331 */       buf.append("<td height='22'>家庭地址</td>");
/* 332 */       buf.append("<td colspan='5'>" + ds[26] + "</td>");
/* 333 */       buf.append("</tr>");
/* 334 */       buf.append("</table>");
/* 335 */       if (j % 2 == 0) {
/* 336 */         buf
/* 337 */           .append("<div class='noprint'><br><hr size='1' noshade='noshade' style='border:1px #cccccc dotted;'><br></div><div class='nextpage'></div>");
/*     */       }
/*     */       else
/* 340 */         buf
/* 341 */           .append("<br><br><div class='noscreen'><br><br><br><br><br><br><br><br><br><br><br><br><br></div>");
/*     */     }
/* 343 */     response.getWriter().print(buf.toString());
/*     */   }
/*     */ 
/*     */   public void importData(HttpServletRequest request, HttpServletResponse response) throws Exception
/*     */   {
/* 348 */     String jndi = request.getAttribute("sys.jndi").toString();
/* 349 */     InputStream data = (InputStream)request.getAttribute("importFile");
/* 350 */     String fileName = request.getAttribute("importFile__file").toString();
/* 351 */     Connection conn = DbUtil.getConnection(jndi);
/* 352 */     String[] values = { request.getAttribute("sys.scope").toString(), 
/* 353 */       "f379eaf3c831b04de153469d1bec345e", 
/* 354 */       request.getAttribute("sys.now").toString(), 
/* 355 */       request.getAttribute("sys.now").toString(), "员工" };
/*     */     try {
/* 357 */       DbUtil.initValueMaps(request, 
/* 358 */         "3=SEX,9=EDUCATION,12=MAR,14=POL,15=HUJI");
/* 359 */       if (StringUtil.isSame(FileUtil.extractFileExt(fileName), "xls"))
/*     */       {
/* 361 */         DbUtil.importExcelStream(
/* 362 */           conn, 
/* 363 */           "WB_USER", 
/* 364 */           data, 
/* 365 */           0, 
/* 366 */           "CODE,REAL_NAME,SEX,DEPT,USERNAME,BIRTH_DATE,NATIVE_PLACE,ID,EDUCATION,UNIV,PROF,MARITAL,NATIONS,POLITICAL,HUJI,HEIGHT,WEIGHT,POST,ENTRY_DATE,TRIAL_LEN,BANK_NAME,BANK_CODE,EMAIL,TEL,MP,IM,WORK_ADDR,HOME_ADDR,DESCRIPTION,SCOPE,PASSWORD,REG_DATE,LAST_ACCESS,ROLENAME", 
/* 367 */           values, 
/* 368 */           (Object[])request
/* 368 */           .getAttribute("mapd.obj"), 
/* 369 */           (Integer[])request.getAttribute("mapd.lst"), 5);
/*     */       }
/*     */       else
/* 372 */         DbUtil.importTxtStream(
/* 373 */           conn, 
/* 374 */           "WB_USER", 
/* 375 */           data, 
/* 376 */           request.getAttribute("sys.charset").toString(), 
/* 377 */           "CODE,REAL_NAME,SEX,DEPT,USERNAME,BIRTH_DATE,NATIVE_PLACE,ID,EDUCATION,UNIV,PROF,MARITAL,NATIONS,POLITICAL,HUJI,HEIGHT,WEIGHT,POST,ENTRY_DATE,TRIAL_LEN,BANK_NAME,BANK_CODE,EMAIL,TEL,MP,IM,WORK_ADDR,HOME_ADDR,DESCRIPTION,SCOPE,PASSWORD,REG_DATE,LAST_ACCESS,ROLENAME", 
/* 378 */           values, 
/* 379 */           (Object[])request
/* 379 */           .getAttribute("mapd.obj"), 
/* 380 */           (Integer[])request.getAttribute("mapd.lst"), 
/* 381 */           5, StringUtil.getSeparator(request));
/*     */     } finally {
/* 383 */       conn.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setUpdateSql(HttpServletRequest request, HttpServletResponse response)
/*     */     throws Exception
/*     */   {
	
				String CHAR1 = StringUtil.fetchString(request, "CHAR1");
				if(StringUtil.isEmpty(CHAR1)){
					request.setAttribute("whereSql2", " ");
					}
				else if(CHAR1.getBytes().length == CHAR1.length()) {
						request.setAttribute("whereSql2", " CHAR1='"+CHAR1+"' , ");
				}
	
/* 390 */     String newuser = StringUtil.fetchString(request, "USERNAME");
/* 391 */     DbUtil.checkUserName(newuser);
/* 392 */     if ((StringUtil.getStringBool(StringUtil.fetchString(request, 
/* 393 */       "delPicFlag"))) || 
/* 394 */       (request.getAttribute("PIC") != null))
/* 395 */       request.setAttribute("picSql", ",PIC={?blob.PIC?}");
/*     */     else
/* 397 */       request.setAttribute("picSql", "");
/* 398 */     String password = StringUtil.fetchString(request, "PASSWORD");
/* 399 */     if (StringUtil.isEqual(password, "{@!init!@}")) {
/* 400 */       request.setAttribute("passSql", "");
/*     */     } else {
/* 402 */       if (password != null)
/* 403 */         request.setAttribute("PASSWORD", Encrypter.getMD5(password));
/* 404 */       request.setAttribute("passSql", ",PASSWORD={?PASSWORD?}");
/*     */     }
/* 406 */     if (StringUtil.isEqual(StringUtil.fetchString(request, "USERNAME"), 
/* 407 */       StringUtil.fetchString(request, "oldName")))
/* 408 */       request.setAttribute("notRunUserSql", "notRunUserSql");
/*     */     else
/* 410 */       request.setAttribute("notRunUserSql", "");
/* 411 */     if (StringUtil.isEqual(StringUtil.fetchString(request, "CODE"), 
/* 412 */       StringUtil.fetchString(request, "oldCode")))
/* 413 */       if (StringUtil.isEqual(StringUtil.fetchString(request, 
/* 414 */         "REAL_NAME"), StringUtil.fetchString(request, 
/* 415 */         "oldRealName"))) {
/* 416 */         request.setAttribute("notRunCodeSql", "notRunCodeSql"); return;
/*     */       }
/* 418 */     request.setAttribute("notRunCodeSql", "");
/*     */   }
/*     */ 
/*     */   public void resetUserCode(HttpServletRequest request, HttpServletResponse response) throws Exception
/*     */   {
/* 423 */     HttpSession session = request.getSession(false);
/* 424 */     if (session != null) {
/* 425 */       if (StringUtil.isEmpty((String)request
/* 426 */         .getAttribute("notRunUserSql")))
/* 427 */         if (StringUtil.isEqual(request.getAttribute("sys.user")
/* 428 */           .toString(), StringUtil.fetchString(request, 
/* 429 */           "oldName"))) {
/* 430 */           String user = StringUtil.fetchString(request, "USERNAME");
/* 431 */           session.setAttribute("sys.user", user);
/* 432 */           request.setAttribute("sys.user", user);
/*     */         }
/* 434 */       if (StringUtil.isEmpty((String)request
/* 435 */         .getAttribute("notRunCodeSql")))
/* 436 */         if (StringUtil.isEqual(request.getAttribute("sys.code")
/* 437 */           .toString(), StringUtil.fetchString(request, 
/* 438 */           "oldCode"))) {
/* 439 */           String code = StringUtil.fetchString(request, "CODE");
/* 440 */           String realname = StringUtil.fetchString(request, "REAL_NAME");
/* 441 */           session.setAttribute("sys.code", code);
/* 442 */           request.setAttribute("sys.code", code);
/* 443 */           session.setAttribute("sys.realname", realname);
/* 444 */           request.setAttribute("sys.realname", realname);
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void resetUser(HttpServletRequest request, HttpServletResponse response) throws Exception
/*     */   {
/* 451 */     HttpSession session = request.getSession(false);
/* 452 */     if ((session != null) && 
/* 453 */       (StringUtil.isEmpty((String)request
/* 454 */       .getAttribute("notRunUserSql")))) {
/* 455 */       String user = StringUtil.fetchString(request, "USERNAME");
/* 456 */       session.setAttribute("sys.user", user);
/* 457 */       request.setAttribute("sys.user", user);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setInsertSql(HttpServletRequest request, HttpServletResponse response)
/*     */     throws Exception
/*     */   {
/* 464 */     String username = StringUtil.fetchString(request, "USERNAME");
/* 465 */     DbUtil.checkUserName(username);
/* 466 */     String password = StringUtil.fetchString(request, "PASSWORD");
/* 467 */     if (password == null)
/* 468 */       password = "";
/* 469 */     request.setAttribute("PASSWORD", Encrypter.getMD5(password));
/*     */   }
/*     */ 
/*     */   public void updatePassSql(HttpServletRequest request, HttpServletResponse response)
/*     */     throws Exception
/*     */   {
/* 476 */     String password = request.getParameter("PASSWORD");
/* 477 */     if (StringUtil.isEqual(password, "{@!init!@}")) {
/* 478 */       request.setAttribute("passSql", "");
/*     */     } else {
/* 480 */       if (password != null)
/* 481 */         request.setAttribute("PASSWORD", Encrypter.getMD5(password));
/* 482 */       request.setAttribute("passSql", "PASSWORD={?PASSWORD?},");
/*     */     }
/*     */   }
/*     */ }

/* Location:           Z:\EXT\WebBuilderServer (1)\WEB-INF\lib\webbuilder2.jar
 * Qualified Name:     com.webbuilder.interact.UserAdmin
 * JD-Core Version:    0.6.0
 */