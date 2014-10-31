/*     */ package com.webbuilder.interact;
/*     */ 
/*     */ import com.webbuilder.controls.DbUpdater;
/*     */ import com.webbuilder.controls.Query;
/*     */ import com.webbuilder.utils.DateUtil;
/*     */ import com.webbuilder.utils.DbUtil;
/*     */ import com.webbuilder.utils.FileUtil;
/*     */ import com.webbuilder.utils.JsonUtil;
/*     */ import com.webbuilder.utils.StringUtil;
/*     */ import com.webbuilder.utils.XMLParser;
/*     */ import java.io.File;
/*     */ import java.io.PrintWriter;
/*     */ import java.sql.ResultSet;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.Properties;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import org.dom4j.Attribute;
/*     */ import org.dom4j.Element;
/*     */ import org.dom4j.Node;
/*     */ import org.json.JSONArray;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class System
/*     */ {
/*     */   public void setSystemVar(HttpServletRequest request, HttpServletResponse response)
/*     */     throws Exception
/*     */   {
/*  28 */     File file = new File((String)request.getAttribute("sys.path"), 
/*  29 */       "webbuilder/system/main.xml");
/*  30 */     XMLParser config = new XMLParser(file);
/*  31 */     JSONArray array = new JSONArray(request.getParameter("varGrid"));
/*     */ 
/*  33 */     String runMode = request.getParameter("runModeOption");
/*  34 */     String[] runModes = { "open", "normal", "debug" };
/*  35 */     int j = array.length(); int k = Integer.parseInt(runMode);
/*     */ 
/*  37 */     for (int i = 0; i < j; i++) {
/*  38 */       JSONObject obj = array.getJSONObject(i);
/*  39 */       String name = obj.getString("name");
/*     */       String value;

/*  40 */       if (StringUtil.isEqual(name, "runMode"))
/*  41 */         value = runModes[k];
/*     */       else
/*  43 */         value = obj.getString("value");
/*  44 */       config.getNode("/main/var/" + name + "/@value").setText(value);
/*     */     }
/*  46 */     config.save();
/*     */   }
/*     */ 
/*     */   public void getSystemVar(HttpServletRequest request, HttpServletResponse response) throws Exception
/*     */   {
/*  51 */     File file = new File((String)request.getAttribute("sys.path"), 
/*  52 */       "webbuilder/system/main.xml");
/*  53 */     XMLParser config = new XMLParser(file);
/*     */ 
/*  55 */     StringBuilder buf = new StringBuilder();
/*  56 */     int i = 0;
/*     */ 
/*  58 */     Element el = (Element)config.getNode("/main/var");
/*  59 */     Iterator iterator = el.elementIterator();
/*  60 */     while (iterator.hasNext()) {
/*  61 */       Element child = (Element)iterator.next();
/*  62 */       if (i > 0)
/*  63 */         buf.append(",");
/*  64 */       buf.append("{name:\"" + StringUtil.toExpress(child.getName()));
/*  65 */       buf.append("\",value:\"" + 
/*  66 */         StringUtil.toExpress(child.attribute("value").getText()) + 
/*  67 */         "\"}");
/*  68 */       i++;
/*     */     }
/*  70 */     response
/*  71 */       .getWriter()
/*  72 */       .print(
/*  73 */       "{total:" + 
/*  74 */       Integer.toString(i) + 
/*  75 */       ",metaData:{totalProperty:\"total\",root:\"row\",fields:[{name:\"name\",type:\"string\"},{name:\"value\",type:\"string\"}]},row:[" + 
/*  76 */       buf.toString() + "]}");
/*     */   }
/*     */ 
/*     */   private void addSysItem(StringBuilder buf, String name, String value) {
/*  80 */     buf.append(",{name:\"" + StringUtil.toExpress(name) + "\",value:\"" + 
/*  81 */       StringUtil.toExpress(value) + "\"}");
/*     */   }
/*     */ 
/*     */   public void getAdvWhereSqlA(HttpServletRequest request, HttpServletResponse response) throws Exception
/*     */   {
/*  86 */     String find = StringUtil.fetchString(request, "findCombo");
/*  87 */     String dbType = request.getAttribute("sys.dbType").toString();
/*     */ 
/*  89 */     if (!StringUtil.isEmpty(find)) {
/*  90 */       request.setAttribute("findValue", "%" + find + "%");
/*     */       String sql;

/*  91 */       if (StringUtil.isSame(dbType, "oracle")) {
/*  92 */         sql = " and WB_NAME||'('||a.WB_CODE||')' like {?findValue?}";
/*     */       }
/*     */       else
/*     */       {

/*  93 */         if (StringUtil.isSame(dbType, "mysql"))
/*  94 */           sql = " and concat(WB_NAME,'(',a.WB_CODE,')') like {?findValue?}";
/*     */         else
/*  96 */           sql = " and WB_NAME+'('+a.WB_CODE+')' like {?findValue?}"; 
/*     */       }
/*  97 */       request.setAttribute("whereSql", sql);
/*     */     } else {
/*  99 */       DbUtil.getDefaultWhere(request, response, "WB_CODE=a", false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void getAdvWhereSql(HttpServletRequest request, HttpServletResponse response) throws Exception {
/* 104 */     String find = StringUtil.fetchString(request, "findCombo");
/* 105 */     String dbType = request.getAttribute("sys.dbType").toString();
/*     */ 
/* 107 */     if (!StringUtil.isEmpty(find)) {
/* 108 */       request.setAttribute("findValue", "%" + find + "%");
/*     */       String sql;

/* 109 */       if (StringUtil.isSame(dbType, "oracle")) {
/* 110 */         sql = " and WB_NAME||'('||WB_CODE||')' like {?findValue?}";
/*     */       }
/*     */       else
/*     */       {

/* 111 */         if (StringUtil.isSame(dbType, "mysql"))
/* 112 */           sql = " and concat(WB_NAME,'(',WB_CODE,')') like {?findValue?}";
/*     */         else
/* 114 */           sql = " and WB_NAME+'('+WB_CODE+')' like {?findValue?}"; 
/*     */       }
/* 115 */       request.setAttribute("whereSql", sql);
/*     */     } else {
/* 117 */       DbUtil.getDefaultWhere(request, response, null, false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void getCodeWhereSql(HttpServletRequest request, HttpServletResponse response) throws Exception {
/* 122 */     DbUtil.getDefaultWhere(request, response, "CODE=a", false);
/*     */   }
/*     */ 
/*     */   public void getWhereSql(HttpServletRequest request, HttpServletResponse response) throws Exception
/*     */   {
/* 127 */     DbUtil.getDefaultWhere(request, response, null, false);
/*     */   }
/*     */ 
/*     */   public void getSysInfo(HttpServletRequest request, HttpServletResponse response) throws Exception
/*     */   {
/* 132 */     StringBuilder b = new StringBuilder();
/*     */ 
/* 134 */     Runtime rt = Runtime.getRuntime();
/* 135 */     b.append("{row:[{name:\"内存大小\",value:\"" + 
/* 136 */       StringUtil.formatFloat(rt.totalMemory() / 1048576L, "#,##0") + 
/* 137 */       " MB\"}");
/* 138 */     addSysItem(b, "空闲内存", StringUtil.formatFloat(rt.freeMemory() / 1048576L, 
/* 139 */       "#,##0") + 
/* 140 */       " MB");
/* 141 */     addSysItem(b, "占用内存", StringUtil.formatFloat(
/* 142 */       (rt.totalMemory() - rt
/* 142 */       .freeMemory()) / 1048576L, "#,##0") + 
/* 143 */       " MB");
/* 144 */     addSysItem(b, "最大内存容量", StringUtil.formatFloat(
/* 145 */       rt.maxMemory() / 1048576L, "#,##0") + 
/* 146 */       " MB");
/* 147 */     Properties props = java.lang.System.getProperties();
/* 148 */     addSysItem(b, "操作系统的名称", props.getProperty("os.name"));
/* 149 */     addSysItem(b, "操作系统的构架", props.getProperty("os.arch"));
/* 150 */     addSysItem(b, "操作系统的版本", props.getProperty("os.version"));
/* 151 */     addSysItem(b, "应用名称", (String)request.getAttribute("sys.title"));
/* 152 */     addSysItem(b, "应用版本", (String)request.getAttribute("sys.version"));
/* 153 */     addSysItem(b, "Java的运行环境版本", props.getProperty("java.version"));
/* 154 */     addSysItem(b, "Java的运行环境供应商", props.getProperty("java.vendor"));
/* 155 */     addSysItem(b, "Java的安装路径", props.getProperty("java.home"));
/* 156 */     addSysItem(b, "Java的虚拟机规范版本", props
/* 157 */       .getProperty("java.vm.specification.version"));
/* 158 */     addSysItem(b, "Java的虚拟机规范供应商", props
/* 159 */       .getProperty("java.vm.specification.vendor"));
/* 160 */     addSysItem(b, "Java的虚拟机规范名称", props
/* 161 */       .getProperty("java.vm.specification.name"));
/* 162 */     addSysItem(b, "Java的虚拟机实现版本", props.getProperty("java.vm.version"));
/* 163 */     addSysItem(b, "Java的虚拟机实现供应商", props.getProperty("java.vm.vendor"));
/* 164 */     addSysItem(b, "Java的虚拟机实现名称", props.getProperty("java.vm.name"));
/* 165 */     addSysItem(b, "Java运行时环境规范版本", props
/* 166 */       .getProperty("java.specification.version"));
/* 167 */     addSysItem(b, "Java运行时环境规范供应商", props
/* 168 */       .getProperty("java.specification.vender"));
/* 169 */     addSysItem(b, "Java运行时环境规范名称", props
/* 170 */       .getProperty("java.specification.name"));
/* 171 */     addSysItem(b, "Java的类格式版本号", props.getProperty("java.class.version"));
/* 172 */     addSysItem(b, "Java的类路径", props.getProperty("java.class.path"));
/* 173 */     addSysItem(b, "加载库时搜索的路径列表", props.getProperty("java.library.path"));
/* 174 */     addSysItem(b, "默认的临时文件路径", props.getProperty("java.io.tmpdir"));
/* 175 */     addSysItem(b, "扩展目录的路径", props.getProperty("java.ext.dirs"));
/* 176 */     addSysItem(b, "用户的账户名称", props.getProperty("user.name"));
/* 177 */     addSysItem(b, "用户的主目录", props.getProperty("user.home"));
/* 178 */     addSysItem(b, "用户的当前工作目录", props.getProperty("user.dir"));
/* 179 */     File[] roots = File.listRoots();
/* 180 */     for (File file : roots) {
/* 181 */       String t = file.getPath();
/* 182 */       addSysItem(b, t + "的空间大小", StringUtil.formatFloat(file
/* 183 */         .getTotalSpace() / 1048576L, "#,##0") + 
/* 184 */         " MB");
/*     */ 
/* 186 */       addSysItem(b, t + "的空闲空间", StringUtil.formatFloat(file
/* 187 */         .getFreeSpace() / 1048576L, "#,##0") + 
/* 188 */         " MB");
/*     */ 
/* 190 */       addSysItem(b, t + "的可用空间", StringUtil.formatFloat(file
/* 191 */         .getUsableSpace() / 1048576L, "#,##0") + 
/* 192 */         " MB");
/*     */     }
/* 194 */     b.append("]}");
/* 195 */     response.getWriter().print(b);
/*     */   }
/*     */ 
/*     */   public void setLogWhereSql(HttpServletRequest request, HttpServletResponse response) throws Exception
/*     */   {
/* 200 */     String json = request.getParameter("jsonValue");
/* 201 */     if (!StringUtil.isEmpty(json))
/* 202 */       JsonUtil.jsonToRequest(json, request);
/* 203 */     String sql = "";
/* 204 */     String dt = StringUtil.fetchString(request, "LOG_DATE");
/*     */ 
/* 206 */     if (!StringUtil.isEmpty(dt)) {
/* 207 */       request.setAttribute("LOG_DATE_END", DateUtil.dateToString(
/* 208 */         DateUtil.incDay(DateUtil.stringToStdDate(dt), 1L)));
/* 209 */       sql = 
/* 210 */         StringUtil.getSql(sql, dt, 
/* 211 */         "LOG_DATE>={?timestamp.LOG_DATE?} and LOG_DATE<={?timestamp.LOG_DATE_END?}");
/*     */     }
/* 213 */     sql = StringUtil.getSql(sql, 
/* 214 */       StringUtil.fetchString(request, "USERNAME"), 
/* 215 */       "USERNAME like '%{#USERNAME#}%'");
/* 216 */     sql = StringUtil.getSql(sql, StringUtil.fetchString(request, "IP"), 
/* 217 */       "IP like '%{#IP#}%'");
/* 218 */     request.setAttribute("likeMsg", "%" + 
/* 219 */       StringUtil.fetchString(request, "searchMsg") + "%");
/* 220 */     sql = StringUtil.getSql(sql, StringUtil.fetchString(request, 
/* 221 */       "searchMsg"), "MSG like {?likeMsg?}");
/* 222 */     sql = StringUtil.getSql(sql, 
/* 223 */       StringUtil.fetchString(request, "logType"), 
/* 224 */       "LOG_TYPE={?integer.logType?}");
/* 225 */     if (!StringUtil.isEmpty(sql))
/* 226 */       sql = " where " + sql;
/* 227 */     request.setAttribute("whereSql", sql);
/*     */   }
/*     */ 
/*     */   public void savePortal(HttpServletRequest request, HttpServletResponse response) throws Exception
/*     */   {
/* 232 */     DbUpdater db = new DbUpdater();
/*     */ 
/* 234 */     db.setRequest(request);
/* 235 */     db.data = request.getParameter("portalData");
/* 236 */     db.insertSql = "insert into WB_PORTALET values ({?sys.user?},{?TITLE?},{?integer.HEIGHT?},{?FILENAME?},{?ICON_CLS?},{?integer.PX?},{?integer.PY?},{?integer.COLLAPSED?})";
/*     */ 
/* 238 */     db.transaction = "commit";
/* 239 */     db.create();
/*     */   }
/*     */ 
/*     */   public void initPortalets(HttpServletRequest request, HttpServletResponse response) throws Exception
/*     */   {
/* 244 */     int j = 2; int k = 0; int l = 0;
/* 245 */     StringBuilder[] buf = new StringBuilder[j]; StringBuilder tabs = new StringBuilder();
/* 246 */     ResultSet rs = (ResultSet)request.getAttribute("portaletsQuery");
/*     */ 
/* 248 */     boolean[] addComma = new boolean[j];
/*     */ 
/* 250 */     for (int i = 0; i < j; i++) {
/* 251 */       addComma[i] = false;
/* 252 */       buf[i] = new StringBuilder();
/* 253 */       if (i == j - 1)
/* 254 */         buf[i]
/* 255 */           .append("{id:\"ptl" + 
/* 256 */           i + 
/* 257 */           "\",columnWidth:0.4,style:\"padding:3px 18px 3px 3px\"");
/*     */       else
/* 259 */         buf[i]
/* 260 */           .append("{id:\"ptl" + 
/* 261 */           i + 
/* 262 */           "\",columnWidth:0.6,style:\"padding:3px 3px 3px 3px\"");
/*     */     }
/* 264 */     while (rs.next()) {
/* 265 */       String title = rs.getString(1);
/* 266 */       int height = rs.getInt(2);
/* 267 */       String filename = rs.getString(3);
/* 268 */       String iconCls = rs.getString(4);
/* 269 */       int px = rs.getInt(5);
/* 270 */       int py = rs.getInt(6);
/* 271 */       boolean collapsed = rs.getInt(7) == 1;
/* 272 */       l++;
/* 273 */       if (py == -1) {
/* 274 */         tabs
/* 275 */           .append(",{id:\"tbp" + 
/* 276 */           l + 
/* 277 */           "\",iconCls:\"" + 
/* 278 */           iconCls + 
/* 279 */           "\",hideMode:\"offsets\",filename:\"" + 
/* 280 */           filename + 
/* 281 */           "\",title:\"" + 
/* 282 */           StringUtil.toExpress(title) + 
/* 283 */           "\",listeners:{beforedestroy:function(p){try{var o=Get(\"x\"+p.id);" + 
/* 284 */           "o.contentWindow.document.write(\"\");o.contentWindow.document.close();}catch(e){}}}," + 
/* 285 */           "closable:true,html:\"<iframe id='xtbp" + 
/* 286 */           l + 
/* 287 */           "' name='xtbp" + 
/* 288 */           l + 
/* 289 */           "' scrolling='auto' frameborder='0' width='100%' height='100%'></iframe>\"}");
/*     */       }
/*     */       else {
/* 292 */         if (collapsed)
/* 293 */           height = 200;
/* 294 */         if (addComma[px] ) {
/* 295 */           buf[px].append(",");
/*     */         } else {
/* 297 */           addComma[px] = true;
/* 298 */           buf[px].append(",items:[");
/*     */         }
/* 300 */         buf[px].append("{title:\"");
/* 301 */         buf[px].append(StringUtil.toExpress(title));
/* 302 */         buf[px].append("\",tools:portalTools,height:");
/* 303 */         buf[px].append(height);
/* 304 */         k++;
/* 305 */         buf[px].append(",iframeId:\"ptifm__");
/* 306 */         buf[px].append(k);
/* 307 */         if (collapsed)
/* 308 */           buf[px].append("\",collapsed:true,html:\"<iframe id='ptifm__" + 
/* 309 */             k + "' name='ptifm__" + k + "' frameborder='0'>");
/*     */         else {
/* 311 */           buf[px].append("\",html:\"<iframe id='ptifm__" + k + 
/* 312 */             "' name='ptifm__" + k + "' frameborder='0'>");
/*     */         }
/* 314 */         if (!StringUtil.isEmpty(iconCls)) {
/* 315 */           buf[px].append("\",iconCls:\"");
/* 316 */           buf[px].append(iconCls);
/*     */         }
/* 318 */         buf[px].append("\",filename:\"");
/* 319 */         buf[px].append(filename);
/* 320 */         buf[px].append("\"}");
/*     */       }
/*     */     }
/* 322 */     for ( int i = 0; i < j; i++) {
/* 323 */       if (addComma[i])
/* 324 */         buf[i].append("]");
/* 325 */       buf[i].append("}");
/*     */     }
/* 327 */     for ( int i = 1; i < j; i++) {
/* 328 */       buf[0].append(",");
/* 329 */       buf[0].append(buf[i]);
/*     */     }
/* 331 */     request.setAttribute("portalScript", buf[0].toString());
/* 332 */     request.setAttribute("tabsScript", tabs.toString());
/*     */   }
/*     */ 
/*     */   public void getRoleTree(HttpServletRequest request, HttpServletResponse response) throws Exception
/*     */   {
/* 337 */     if ((!StringUtil.getStringBool((String)request
/* 338 */       .getAttribute("sys.needLogin"))) || 
/* 339 */       (!StringUtil.getStringBool((String)request
/* 340 */       .getAttribute("sys.needRole"))))
/* 341 */       return;
/* 342 */     Query query = new Query();
/* 343 */     query.setRequest(request);
/* 344 */     query.sql = "select KEY_VALUE from WB_TEXT where SCOPE={?sys.scope?} and KEY_NAME='sysrole'";
/* 345 */     query.jndi = StringUtil.fetchString(request, "sys.jndi");
/* 346 */     query.loadData = true;
/* 347 */     query.setName("query.roleTree");
/* 348 */     query.create();
/* 349 */     request.setAttribute("roleTree", addRoleTreeCheck(
/* 350 */       new JSONArray(
/* 351 */       (String)request
/* 351 */       .getAttribute("query.roleTree.KEY_VALUE"))).toString());
/*     */   }
/*     */ 
/*     */   private JSONArray addRoleTreeCheck(JSONArray array)
/*     */     throws Exception
/*     */   {
/* 358 */     int j = array.length();
/* 359 */     for (int i = 0; i < j; i++) {
/* 360 */       JSONObject obj = array.getJSONObject(i);
/* 361 */       obj.put("checked", false);
/* 362 */       if (obj.has("children"))
/* 363 */         addRoleTreeCheck(obj.getJSONArray("children"));
/* 364 */       array.put(i, obj);
/*     */     }
/* 366 */     return array;
/*     */   }
/*     */ 
/*     */   public void getFileList(HttpServletRequest request, HttpServletResponse response) throws Exception
/*     */   {
/* 371 */     displayFileList(request, response, false);
/*     */   }
/*     */ 
/*     */   public void getModuleList(HttpServletRequest request, HttpServletResponse response) throws Exception
/*     */   {
/* 376 */     displayFileList(request, response, true);
/*     */   }
/*     */ 
/*     */   private void displayFileList(HttpServletRequest request, HttpServletResponse response, boolean showHidden) throws Exception
/*     */   {
/* 381 */     String dir = request.getParameter("dir");
/* 382 */     response.getWriter().print(
/* 383 */       populateFileList(request, response, dir, 
/* 384 */       StringUtil.getStringBool(request.getParameter("recurse")), 
/* 385 */       showHidden));
/* 386 */     DbUtil.recordHistory((String)request.getAttribute("sys.jndi"), dir, 
/* 387 */       (String)request.getAttribute("sys.user"));
/*     */   }
/*     */ 
/*     */   private String populateFileList(HttpServletRequest request, HttpServletResponse response, String dirPara, boolean recurse, boolean showHidden)
/*     */     throws Exception
/*     */   {
/* 393 */     String blank = "";
/* 394 */     String scope = request.getAttribute("sys.scope")
/* 394 */       .toString();
/* 395 */     String relative = dirPara;
/* 396 */     String root = "webbuilder/application";
/* 397 */     String webPath = request.getAttribute("sys.path").toString();
/* 398 */     if (StringUtil.isEmpty(relative))
/* 399 */       relative = root;
/* 400 */     File dir = new File(webPath + relative);
/* 401 */     File mapFile = FileUtil.getUserIndex(dir, scope, false);
/* 402 */     StringBuilder buf = new StringBuilder();
/* 403 */     String userRoleText = (String)request.getAttribute("sys.role");
/*     */ 
/* 405 */     boolean added = false;
/* 406 */     ArrayList listed = new ArrayList();
/*     */     String[] parentRole;
/*     */     String[] userRole;

/* 408 */     if (userRoleText != null) {
/* 409 */      userRole = StringUtil.split(userRoleText, ",");
/* 410 */       parentRole = FileUtil.getFileRoles(new File(webPath + root), dir, 
/* 411 */         scope);
/*     */     } else {
/* 413 */       userRole = StringUtil.split(blank, ",");
/* 414 */       parentRole = StringUtil.split(blank, ",");
/*     */     }
/* 416 */     buf.append("[");
/* 417 */     if (mapFile.exists())
/*     */     {
/* 421 */       XMLParser mapXml = new XMLParser(mapFile);
/*     */ 
/* 423 */       Element el = (Element)mapXml.getNode("/map");
/* 424 */       Iterator iterator = el.elementIterator();
/* 425 */       while (iterator.hasNext()) {
/* 426 */         el = (Element)iterator.next();
/* 427 */         String name = el.attribute("name").getText();
/* 428 */         File file = new File(dir, name);
/* 429 */         listed.add(file);
/* 430 */         boolean isHidden = StringUtil.getStringBool(el.attribute("hidden")
/* 431 */           .getText());
/* 432 */         if (((!showHidden) && (isHidden)) || (!file.exists()))
/*     */           continue;
/* 434 */         String fileRoleText = el.attribute("role").getText();
/* 435 */         boolean isDir = file.isDirectory();
/*     */         String[] allRole;

/* 436 */         if (StringUtil.isEmpty(fileRoleText))
/* 437 */           allRole = parentRole;
/*     */         else
/* 439 */           allRole = StringUtil.orJoin(parentRole, StringUtil.split(
/* 440 */             fileRoleText, ","));
/* 441 */         if (((isDir) || 
/* 442 */           (!StringUtil.stringsCross(userRole, allRole, true))) && (
/* 443 */           (!isDir) || 
/* 444 */           (!FileUtil.folderCanDisplay(file, userRole, allRole, 
/* 445 */           showHidden, scope, webPath)))) continue;
/* 446 */         String caption = el.attribute("caption").getText();
/* 447 */         if (isHidden) {
/* 448 */           if (StringUtil.isEmpty(caption))
/* 449 */             caption = name;
/* 450 */           caption = "<s>" + caption + "</s>";
/*     */         }
/* 452 */         String icon = el.attribute("icon").getText();
/* 453 */         String hint = el.attribute("hint").getText();
/* 454 */         if (added)
/* 455 */           buf.append(",");
/*     */         else
/* 457 */           added = true;
/* 458 */         buf.append("{text:\"");
/* 459 */         if (StringUtil.isEmpty(caption)) {
/* 460 */           buf.append(name);
/*     */         } else {
/* 462 */           buf.append(StringUtil.toExpress(
/* 463 */             StringUtil.replaceParameters(request, caption)));
/* 464 */           buf.append("\",origText:\"");
/* 465 */           buf.append(StringUtil.toExpress(caption));
/*     */         }
/* 467 */         if (!StringUtil.isEmpty(icon)) {
/* 468 */           buf.append("\",iconCls:\"");
/* 469 */           buf.append(icon);
/*     */         }
/* 471 */         if (!StringUtil.isEmpty(hint)) {
/* 472 */           buf.append("\",qtip:\"");
/* 473 */           buf.append(StringUtil.toExpress(hint));
/*     */         }
/* 475 */         if (isDir) {
/* 476 */           buf.append("\",dir:\"");
/* 477 */           buf.append(relative + "/" + name);
/* 478 */           if (showHidden)
/* 479 */             buf.append("\",moduleFile:\"" + 
/* 480 */               StringUtil.replace(
/* 481 */               file.getAbsolutePath(), "\\", "/"));
/* 482 */           if (recurse) {
/* 483 */             buf.append("\",children:");
/* 484 */             buf
/* 485 */               .append(populateFileList(request, response, 
/* 486 */               relative + "/" + name, recurse, 
/* 487 */               showHidden));
/* 488 */             buf.append("}");
/*     */           } else {
/* 490 */             buf.append("\"}");
/*     */           }
/*     */         } else {
/* 492 */           buf.append("\",leaf:true");
/* 493 */           if (StringUtil.stringInList(StringUtil.split(request
/* 494 */             .getAttribute("sys.webFile").toString(), ","), 
/* 495 */             FileUtil.extractFileExt(name).toLowerCase()) != -1) {
/* 496 */             if (showHidden) {
/* 497 */               buf.append(",moduleFile:\"" + 
/* 498 */                 StringUtil.replace(file
/* 499 */                 .getAbsolutePath(), "\\", "/"));
/* 500 */               buf.append("\",actionModule:\"main?action=");
/*     */             } else {
/* 502 */               buf
/* 503 */                 .append(",hrefTarget:\"_blank\",href:\"main?action=");
/* 504 */             }buf.append(relative + "/" + name);
/*     */           } else {
/* 506 */             if (showHidden) {
/* 507 */               buf.append(",moduleFile:\"" + 
/* 508 */                 StringUtil.replace(file
/* 509 */                 .getAbsolutePath(), "\\", "/"));
/* 510 */               buf.append("\",downFile:\"");
/*     */             }
/*     */             else {
/* 513 */               buf.append(",downFile:\"");
/* 514 */             }buf.append(relative + "/" + name);
/*     */           }
/* 516 */           buf.append("\"}");
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 521 */     addFileToBuffer(request, response, relative, dir, listed, buf, 
/* 522 */       userRole, parentRole, added, recurse, showHidden, webPath);
/* 523 */     buf.append("]");
/* 524 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   private void addFileToBuffer(HttpServletRequest request, HttpServletResponse response, String path, File dir, ArrayList<File> listed, StringBuilder buf, String[] userRole, String[] fileRole, boolean added, boolean recurse, boolean showHidden, String webPath)
/*     */     throws Exception
/*     */   {
/* 532 */     File[] fs = dir.listFiles();
/* 533 */     FileUtil.sortFiles(fs);
/* 534 */     String scope = request.getAttribute("sys.scope").toString();
/*     */ 
/* 537 */     for (File f : fs) {
/* 538 */       if (listed.indexOf(f) != -1)
/*     */         continue;
/* 540 */       boolean isDir = f.isDirectory();
/* 541 */       String name = f.getName();
/* 542 */       if ((!isDir) || 
/* 543 */         (name.equals("useridxf")) || 
/* 544 */         (!FileUtil.folderCanDisplay(f, userRole, fileRole, 
/* 545 */         showHidden, scope, webPath))) continue;
/* 546 */       if (added)
/* 547 */         buf.append(",");
/*     */       else
/* 549 */         added = true;
/* 550 */       buf.append("{text:\"");
/* 551 */       buf.append(name);
/* 552 */       buf.append("\",dir:\"");
/* 553 */       buf.append(path + "/" + name);
/* 554 */       if (showHidden)
/* 555 */         buf.append("\",moduleFile:\"" + 
/* 557 */           StringUtil.replace(f.getAbsolutePath(), "\\", "/"));
/* 558 */       if (recurse) {
/* 559 */         buf.append("\",children:");
/* 560 */         buf.append(populateFileList(request, response, path + "/" + 
/* 561 */           name, recurse, showHidden));
/* 562 */         buf.append("}");
/*     */       } else {
/* 564 */         buf.append("\"}");
/*     */       }
/*     */     }
/* 567 */     boolean displayFile = StringUtil.stringsCross(userRole, fileRole, true);
/* 568 */     for (File f : fs) {
/* 569 */       if (listed.indexOf(f) != -1)
/*     */         continue;
/* 571 */       boolean isDir = f.isDirectory();
/* 572 */       if ((!isDir) && (displayFile)) {
/* 573 */         String name = f.getName();
/* 574 */         if (name.equals("index.map"))
/*     */           continue;
/* 576 */         if (added)
/* 577 */           buf.append(",");
/*     */         else
/* 579 */           added = true;
/* 580 */         buf.append("{text:\"");
/* 581 */         buf.append(name);
/* 582 */         buf.append("\",leaf:true");
/* 583 */         if (StringUtil.stringInList(StringUtil.split(request
/* 584 */           .getAttribute("sys.webFile").toString(), ","), 
/* 585 */           FileUtil.extractFileExt(name).toLowerCase()) != -1) {
/* 586 */           if (showHidden) {
/* 587 */             buf.append(",moduleFile:\"" + 
/* 588 */               StringUtil.replace(f.getAbsolutePath(), "\\", 
/* 589 */               "/"));
/* 590 */             buf.append("\",actionModule:\"main?action=");
/*     */           } else {
/* 592 */             buf
/* 593 */               .append(",hrefTarget:\"_blank\",href:\"main?action=");
/* 594 */           }buf.append(path + "/" + name);
/*     */         } else {
/* 596 */           if (showHidden) {
/* 597 */             buf.append(",moduleFile:\"" + 
/* 598 */               StringUtil.replace(f.getAbsolutePath(), "\\", 
/* 599 */               "/"));
/* 600 */             buf.append("\",downFile:\"");
/*     */           } else {
/* 602 */             buf.append(",downFile:\"");
/* 603 */           }buf.append(path + "/" + name);
/*     */         }
/* 605 */         buf.append("\"}");
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           Z:\EXT\WebBuilderServer (1)\WEB-INF\lib\webbuilder2.jar
 * Qualified Name:     com.webbuilder.interact.System
 * JD-Core Version:    0.6.0
 */