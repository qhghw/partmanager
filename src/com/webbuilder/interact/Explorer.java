/*     */ package com.webbuilder.interact;
/*     */ 
/*     */ import com.webbuilder.controls.Query;
/*     */ import com.webbuilder.utils.CompressUtil;
/*     */ import com.webbuilder.utils.DateUtil;
/*     */ import com.webbuilder.utils.DbUtil;
/*     */ import com.webbuilder.utils.FileUtil;
/*     */ import com.webbuilder.utils.StringUtil;
/*     */ import com.webbuilder.utils.SysUtil;
/*     */ import com.webbuilder.utils.WebUtil;
/*     */ import com.webbuilder.utils.XMLParser;
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.sql.Connection;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import javax.imageio.ImageIO;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import javax.servlet.http.HttpSession;
/*     */ import javax.swing.Icon;
/*     */ import javax.swing.filechooser.FileSystemView;
/*     */ import org.dom4j.Attribute;
/*     */ import org.dom4j.Document;
/*     */ import org.dom4j.Element;
/*     */ import org.json.JSONArray;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class Explorer
/*     */ {
/*     */   public void getRcvFilter(HttpServletRequest request, HttpServletResponse response)
/*     */     throws Exception
/*     */   {
/*  42 */     String find = StringUtil.fetchString(request, "findCombo");
/*     */ 
/*  44 */     if (!StringUtil.isEmpty(find)) {
/*  45 */       request.setAttribute("findValue", "%" + find + "%");
/*  46 */       String sql = " and WB_NAME like {?findValue?}";
/*  47 */       request.setAttribute("whereSql", sql);
/*     */     } else {
/*  49 */       DbUtil.getDefaultWhere(request, response, "WB_DATE,WB_CODE=b", 
/*  50 */         false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void sendFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
/*  55 */     Connection conn = DbUtil.fetchConnection(request, request.getAttribute(
/*  56 */       "sys.jndi").toString());
/*  57 */     String depts = request.getAttribute("WB_RDEPT").toString();
/*  58 */     String roles = request.getAttribute("WB_RROLE").toString();
/*  59 */     String users = request.getAttribute("WB_RUSER").toString();
/*  60 */     String scope = request.getAttribute("sys.scope").toString();
/*  61 */     String dbType = request.getAttribute("sys.dbType").toString();
/*  62 */     HashSet userList = new HashSet();
/*     */ 
/*  64 */     userList = DbUtil.getUserList(conn, dbType, scope, depts, roles, users);
/*  65 */     conn.setAutoCommit(false);
/*     */     try {
/*  67 */       PreparedStatement stm = null;
/*  68 */       int k = 0; int l = userList.size();
/*  69 */       boolean commitAll = false; boolean added = false;
/*  70 */       stm = conn
/*  71 */         .prepareStatement("insert into WB_FILERECEIVE values(?,?,?,?,null)");
/*     */       try {
/*  73 */         stm.setString(1, scope);
/*  74 */         stm.setTimestamp(2, 
/*  75 */           new Timestamp(DateUtil.stringToStdDate(
/*  75 */           request.getAttribute("sys.now").toString()).getTime()));
/*  76 */         stm.setString(3, request.getAttribute("sys.code").toString());
/*  77 */         while ( userList.iterator().hasNext()) {
	              String  s=userList.iterator().next().toString();
/*  78 */           k++;
/*  79 */           stm.setString(4, s);
/*  80 */           stm.addBatch();
/*  81 */           if (!added)
/*  82 */             added = true;
/*  83 */           if (k % 1000 == 0) {
/*  84 */             if (k == l)
/*  85 */               commitAll = true;
/*  86 */             stm.executeBatch();
/*     */           }
/*     */         }
/*  89 */         if ((added) && (!commitAll))
/*  90 */           stm.executeBatch();
/*     */       } finally {
/*  92 */         DbUtil.closeStatement(stm);
/*     */       }
/*  94 */       conn.commit();
/*     */     } catch (Exception e) {
/*  96 */       conn.rollback();
/*  97 */       throw new Exception(e);
/*     */     } finally {
/*  99 */       conn.setAutoCommit(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   private String createUserDir(String root) throws Exception {
/* 104 */     Date dt = new Date();
/* 105 */     String y = "y" + Integer.toString(DateUtil.yearOf(dt));
/* 106 */     String d = "d" + Integer.toString(DateUtil.dayOfYear(dt));
/* 107 */     String h = "h" + Integer.toString(DateUtil.hourOfDay(dt));
/* 108 */     Calendar cal = Calendar.getInstance();
/* 109 */     cal.setTime(dt);
/* 110 */     int m = cal.get(12);
/* 111 */     h = h + "m" + Integer.toString(m / 10);
/* 112 */     String rel = y + "/" + d + "/" + h + "/";
/* 113 */     File file = FileUtil.getUniqueFile(new File(root + "/" + rel + "s" + 
/* 114 */       DateUtil.formatDate(dt, "ssSSS")));
/* 115 */     rel = rel + file.getName();
/* 116 */     File dir = new File(root + "/" + rel);
/* 117 */     if (!dir.mkdirs())
/* 118 */       throw new Exception("不能创建目录。");
/* 119 */     return rel;
/*     */   }
/*     */ 
/*     */   public void createPubDir(HttpServletRequest request, HttpServletResponse response) throws Exception
/*     */   {
/* 124 */     String root = request.getAttribute("sys.path").toString() + 
/* 125 */       "WEB-INF/myfile";
/* 126 */     String scope = request.getAttribute("sys.scope").toString();
/*     */ 
/* 128 */     String sysPubDir = FileUtil.fetchPubDir(root, scope);
/* 129 */     request.setAttribute("sysPubDir", sysPubDir);
/*     */   }
/*     */ 
/*     */   public void createUserDir(HttpServletRequest request, HttpServletResponse response) throws Exception
/*     */   {
/* 134 */     String userPath = request.getAttribute("sys.rootpath").toString();
/*     */ 
/* 136 */     if (StringUtil.isEmpty(userPath)) {
/* 137 */       String root = request.getAttribute("sys.path").toString() + 
/* 138 */         "WEB-INF/myfile";
/* 139 */       String path = createUserDir(root);
/* 140 */       Query query = new Query();
/* 141 */       query.setRequest(request);
/* 142 */       query.type = "update";
/* 143 */       request.setAttribute("rootPath", path);
/* 144 */       query.sql = "update WB_USER set ROOT_PATH={?rootPath?} where USERNAME={?sys.user?}";
/* 145 */       query.jndi = StringUtil.fetchString(request, "sys.jndi");
/* 146 */       query.setName("query.updateUser");
/* 147 */       query.create();
/* 148 */       request.getSession(false).setAttribute("sys.rootpath", 
/* 149 */         root + "/" + path);
/* 150 */       request.setAttribute("sys.rootpath", root + "/" + path);
/*     */     } else {
/* 152 */       File dir = new File(userPath);
/* 153 */       if ((!dir.exists()) && (!dir.mkdirs()))
/* 154 */         throw new Exception("不能创建用户目录。");
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setOrder(HttpServletRequest request, HttpServletResponse response) throws Exception
/*     */   {
/* 160 */     JSONArray files = new JSONArray(request.getParameter("orderTree"));
/* 161 */     int j = files.length();
/* 162 */     if (j == 0)
/* 163 */       return;
/* 164 */     File dir = new File(request.getParameter("orderDir"));
/*     */ 
/* 168 */     HashMap hashMap = new HashMap();
/*     */ 
/* 170 */     XMLParser mapXml = new XMLParser(FileUtil.getUserIndex(dir, request.getAttribute(
/* 171 */       "sys.scope").toString(), false));
/* 172 */     Element root = mapXml.document.getRootElement();
/* 173 */     Iterator iterator = root.elementIterator();
/* 174 */     while (iterator.hasNext()) {
/* 175 */       Element el = (Element)iterator.next();
/* 176 */       hashMap.put(el.attribute("name").getText(), el);
/*     */     }
/* 178 */     for (int i = 0; i < j; i++) {
/* 179 */       String name = new JSONObject(files.getString(i)).getString("filename");
/* 180 */       Element el = (Element)hashMap.get(name);
/* 181 */       if (el != null) {
/* 182 */         root.add(el.createCopy());
/* 183 */         root.remove(el);
/*     */       }
/*     */     }
/* 186 */     mapXml.save();
/*     */   }
/*     */ 
/*     */   public void getOrder(HttpServletRequest request, HttpServletResponse response)
/*     */     throws Exception
/*     */   {
/* 195 */     StringBuilder buf = new StringBuilder();
/* 196 */     boolean added = false;
/*     */ 
/* 198 */     buf.append("[");
/* 199 */     File file = new File(request.getParameter("dir"));
/* 200 */     File mapFile = FileUtil.getUserIndex(file, request.getAttribute("sys.scope")
/* 201 */       .toString(), false);
/* 202 */     if (mapFile.exists()) {
/* 203 */       XMLParser mapXml = new XMLParser(mapFile);
/* 204 */       Element el = mapXml.document.getRootElement();
/* 205 */       if (el != null) {
/* 206 */         Iterator iterator = el.elementIterator();
/* 207 */         while (iterator.hasNext()) {
/* 208 */           el = (Element)iterator.next();
/* 209 */           if (added)
/* 210 */             buf.append(",");
/*     */           else
/* 212 */             added = true;
/* 213 */           buf.append("{text:\"");
/* 214 */           String text = StringUtil.replaceParameters(request, el.attribute(
/* 215 */             "caption").getValue());
/* 216 */           String name = el.attribute("name").getValue();
/* 217 */           if (StringUtil.isEmpty(text))
/* 218 */             text = name;
/* 219 */           buf.append(StringUtil.toExpress(text));
/* 220 */           text = el.attribute("icon").getValue();
/* 221 */           if (!StringUtil.isEmpty(text)) {
/* 222 */             buf.append("\",iconCls:\"");
/* 223 */             buf.append(text);
/*     */           }
/* 225 */           buf.append("\",filename:\"");
/* 226 */           buf.append(name);
/* 227 */           buf.append("\",leaf:true}");
/*     */         }
/*     */       }
/*     */     }
/* 231 */     buf.append("]");
/* 232 */     response.getWriter().print(buf);
/*     */   }
/*     */ 
/*     */   public void getProperty(HttpServletRequest request, HttpServletResponse response)
/*     */     throws Exception
/*     */   {
/* 243 */     File file = new File(request.getParameter("fileName"));
/* 244 */     File mapFile = FileUtil.getUserIndex(file.getParentFile(), request
/* 245 */       .getAttribute("sys.scope").toString(), false);
/* 246 */     if (mapFile.exists()) {
/* 247 */       String fileName = file.getName();
/* 248 */       XMLParser mapXml = new XMLParser(mapFile);
/* 249 */       Element el = mapXml.document.getRootElement();
/* 250 */       if (el != null) {
/* 251 */         Iterator iterator = el.elementIterator();
/* 252 */         while (iterator.hasNext()) {
/* 253 */           el = (Element)iterator.next();
/* 254 */           if (!StringUtil.isSame(el.attribute("name").getText(), 
/* 255 */             fileName)) continue;
/* 256 */           StringBuilder buf = new StringBuilder();
/* 257 */           buf.append("{fileCaption:\"");
/* 258 */           Attribute attr = el.attribute("caption");
/* 259 */           if (attr != null)
/* 260 */             buf.append(StringUtil.toExpress(attr.getText()));
/* 261 */           buf.append("\",fileRole:\"");
/* 262 */           attr = el.attribute("role");
/* 263 */           if (attr != null)
/* 264 */             buf.append(StringUtil.toExpress(attr.getText()));
/* 265 */           buf.append("\",fileIcon:\"");
/* 266 */           attr = el.attribute("icon");
/* 267 */           if (attr != null)
/* 268 */             buf.append(attr.getText());
/* 269 */           buf.append("\",fileHint:\"");
/* 270 */           attr = el.attribute("hint");
/* 271 */           if (attr != null)
/* 272 */             buf.append(attr.getText());
/* 273 */           buf.append("\",fileHidden:\"");
/* 274 */           attr = el.attribute("hidden");
/* 275 */           if (attr != null)
/* 276 */             buf.append(StringUtil.toExpress(attr.getText()));
/*     */           else
/* 278 */             buf.append("0");
/* 279 */           buf.append("\"}");
/* 280 */           response.getWriter().print(buf);
/* 281 */           return;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setPropertyCopy(HttpServletRequest request, HttpServletResponse response)
/*     */     throws Exception
/*     */   {
/* 290 */     innerSetProperty(request, response, true);
/*     */   }
/*     */ 
/*     */   public void setProperty(HttpServletRequest request, HttpServletResponse response) throws Exception
/*     */   {
/* 295 */     innerSetProperty(request, response, false);
/*     */   }
/*     */ 
/*     */   private void innerSetProperty(HttpServletRequest request, HttpServletResponse response, boolean createCopy)
/*     */     throws Exception
/*     */   {
/* 304 */     String caption = request.getParameter("fileCaption");
/* 305 */     String role = request.getParameter("fileRole");
/* 306 */     String icon = request.getParameter("fileIcon");
/* 307 */     String hint = request.getParameter("fileHint");
/* 308 */     String hidden = request.getParameter("fileHidden");
/* 309 */     JSONArray files = new JSONArray(request.getParameter("setFile"));
/* 310 */     HashMap map = new HashMap();
/*     */ 
/* 313 */     File file = new File(files.getString(0));
/* 314 */     File dir = file.getParentFile();
/* 315 */     XMLParser mapXml = new XMLParser(FileUtil.getUserIndex(dir, request.getAttribute(
/* 316 */       "sys.scope").toString(), createCopy));
/* 317 */     Element root = mapXml.document.getRootElement();
/* 318 */     if (root != null) {
/* 319 */       Iterator iterator = root.elementIterator();
/* 320 */       while (iterator.hasNext()) {
/* 321 */         Element el = (Element)iterator.next();
/* 322 */         String name = el.attribute("name").getText();
/* 323 */         file = new File(dir, name);
/* 324 */         if ((!file.exists()) || (map.containsKey(name)))
/* 325 */           root.remove(el);
/*     */         else
/* 327 */           map.put(name, el);
/*     */       }
/*     */     } else {
/* 330 */       root = mapXml.document.addElement("map");
/* 331 */     }int j = files.length();
/* 332 */     for (int i = 0; i < j; i++) {
/* 333 */       String name = FileUtil.extractFileName(files.getString(i));
/* 334 */       Element el = (Element)map.get(name);
/* 335 */       if (el == null) {
/* 336 */         el = root.addElement("file");
/* 337 */         el.addAttribute("name", name);
/* 338 */         el.addAttribute("caption", caption);
/* 339 */         el.addAttribute("role", role);
/* 340 */         el.addAttribute("icon", icon);
/* 341 */         el.addAttribute("hint", hint);
/* 342 */         el.addAttribute("hidden", hidden);
/*     */       } else {
/* 344 */         el.attribute("name").setText(name);
/* 345 */         el.attribute("caption").setText(caption);
/* 346 */         el.attribute("role").setText(role);
/* 347 */         el.attribute("icon").setText(icon);
/* 348 */         el.attribute("hint").setText(hint);
/* 349 */         el.attribute("hidden").setText(hidden);
/*     */       }
/*     */     }
/* 352 */     mapXml.save();
/*     */   }
/*     */ 
/*     */   public void importFile(HttpServletRequest request, HttpServletResponse response) throws Exception
/*     */   {
/* 357 */     String importDir = request.getAttribute("importDir").toString();
/* 358 */     FileUtil.checkRight(request, new File(importDir));
/* 359 */     InputStream stream = (InputStream)request.getAttribute("importFile");
/* 360 */     String fn = request.getAttribute("importFile__file").toString();
/*     */ 
/* 362 */     if (StringUtil.isEqual(request.getAttribute("importType").toString(), 
/* 363 */       "1")) {
/* 364 */       if (StringUtil.isSame(FileUtil.extractFileExt(fn), "zip"))
/* 365 */         CompressUtil.unzip(stream, new File(importDir), 
/* 366 */           (String)request.getAttribute("sys.fileCharset"));
/*     */       else
/* 368 */         throw new Exception("请选择一个zip格式的压缩文件。");
/*     */     }
/* 370 */     else FileUtil.saveInputStreamToFile(stream, new File(importDir, fn)); 
/*     */   }
/*     */ 
/*     */   public void exportFile(HttpServletRequest request, HttpServletResponse response)
/*     */     throws Exception
/*     */   {
/* 375 */     String[] list = StringUtil.split(request.getParameter("exportFiles"), 
/* 376 */       "|");

/* 378 */     int i = 0; int j = list.length;
/*     */ 
/* 380 */     File[] files = new File[j];
/*     */ 
/* 382 */     for (i = 0; i < j; i++) {
/* 383 */       WebUtil.recordLog(request, "explorer导出:" + list[i], 1);
/* 384 */       files[i] = new File(list[i]);
/* 385 */       FileUtil.checkRight(request, files[i]);
/*     */     }
/*     */     String fileName;

/* 387 */     if (j == 1) {
/* 388 */       fileName = FileUtil.extractFileNameNoExt(files[0].getName());
/*     */     } else {
/* 390 */       File parentFile = files[0].getParentFile();
/* 391 */       fileName = "";
/* 392 */       if (parentFile != null)
/* 393 */         fileName = FileUtil.extractFileNameNoExt(parentFile.getName());
/* 394 */       if (StringUtil.isEmpty(fileName))
/* 395 */         fileName = "data";
/*     */     }
/* 397 */     boolean useZip = (StringUtil.isEqual(request.getParameter("exportType"), "1")) || 
/* 398 */       (j > 1) || (files[0].isDirectory());
/* 399 */     response.reset();
/* 400 */     if (!useZip) {
/* 401 */       response.setHeader("content-length", Long.toString(files[0]
/* 402 */         .length()));
/* 403 */       fileName = files[0].getName();
/*     */     } else {
/* 405 */       fileName = fileName + ".zip";
/* 406 */     }response.setHeader("content-type", "application/force-download");
/* 407 */     String charset = (String)request.getAttribute("sys.fileCharset");
/* 408 */     response.setHeader("content-disposition", "attachment;filename=" + 
/* 409 */       WebUtil.getFileName(fileName, charset));
/* 410 */     if (useZip) {
/* 411 */       CompressUtil.zip(files, response.getOutputStream(), 
/* 412 */         (String)request.getAttribute("sys.fileCharset"));
/*     */     } else {
/* 414 */       FileInputStream inputStream = new FileInputStream(files[0]);
/* 415 */       SysUtil.inputStreamToOutputStream(inputStream, response
/* 416 */         .getOutputStream());
/* 417 */       inputStream.close();
/*     */     }
/*     */   }
/*     */ 


/*     */   public void exportFile2(HttpServletRequest request, HttpServletResponse response)
/*     */     throws Exception
/*     */   {
/*     */   String root = request.getAttribute("sys.path").toString() + 
					"WEB-INF/myfile.doc";
/* 378 */     int i = 0; int j = 1;
/*     */ 
/* 380 */     File[] files = new File[j];
/*     */ 
/* 382 */     
/* 383 */       WebUtil.recordLog(request, "explorer导出:" + root, 1);
/* 384 */       files[i] = new File(root);
/* 385 */       FileUtil.checkRight(request, files[i]);
/*     */     
/*     */     String fileName;

/* 387 */     if (j == 1) {
/* 388 */       fileName = FileUtil.extractFileNameNoExt(files[0].getName());
/*     */     } else {
/* 390 */       File parentFile = files[0].getParentFile();
/* 391 */       fileName = "";
/* 392 */       if (parentFile != null)
/* 393 */         fileName = FileUtil.extractFileNameNoExt(parentFile.getName());
/* 394 */       if (StringUtil.isEmpty(fileName))
/* 395 */         fileName = "data";
/*     */     }
/* 397 */     boolean useZip = (StringUtil.isEqual(request.getParameter("exportType"), "1")) || 
/* 398 */       (j > 1) || (files[0].isDirectory());
/* 399 */     response.reset();
/* 400 */     if (!useZip) {
/* 401 */       response.setHeader("content-length", Long.toString(files[0]
/* 402 */         .length()));
/* 403 */       fileName = files[0].getName();
/*     */     } else {
/* 405 */       fileName = fileName + ".zip";
/* 406 */     }response.setHeader("content-type", "application/force-download");
/* 407 */     String charset = (String)request.getAttribute("sys.fileCharset");
/* 408 */     response.setHeader("content-disposition", "attachment;filename=" + 
/* 409 */       WebUtil.getFileName(fileName, charset));
/* 410 */     if (useZip) {
/* 411 */       CompressUtil.zip(files, response.getOutputStream(), 
/* 412 */         (String)request.getAttribute("sys.fileCharset"));
/*     */     } else {
/* 414 */       FileInputStream inputStream = new FileInputStream(files[0]);
/* 415 */       SysUtil.inputStreamToOutputStream(inputStream, response
/* 416 */         .getOutputStream());
/* 417 */       inputStream.close();
					
/*     */     }
/*     */   }
/*     */ 
/*     */   public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception
/*     */   {
/* 423 */     String fileName = request.getParameter("file");
/*     */     try {
/* 425 */       Runtime.getRuntime().exec(fileName);
/*     */     } catch (Exception e) {
/* 427 */       throw new Exception("执行 \"" + FileUtil.extractFileName(fileName) + 
/* 428 */         "\"错误。");
/*     */     }
/*     */   }
/*     */ 
/*     */   public void openFile(HttpServletRequest request, HttpServletResponse response) throws Exception
/*     */   {
/* 434 */     FileUtil.checkRight(request, new File(request.getParameter("file")));
/* 435 */     String charset = request.getParameter("charset");
/*     */ 
/* 437 */     if (StringUtil.isEmpty(charset))
/* 438 */       charset = (String)request.getAttribute("sys.charset");
/* 439 */     response.getWriter().print(
/* 440 */       FileUtil.readText(request.getParameter("file"), charset));
/*     */   }
/*     */ 
/*     */   public void saveFile(HttpServletRequest request, HttpServletResponse response) throws Exception
/*     */   {
/* 445 */     FileUtil.checkRight(request, new File(request.getParameter("file")));
/* 446 */     String charset = request.getParameter("charset");
/* 447 */     if (StringUtil.isEmpty(charset))
/* 448 */       charset = (String)request.getAttribute("sys.charset");
/* 449 */     FileUtil.writeText(request.getParameter("file"), request
/* 450 */       .getParameter("text"), charset);
/*     */   }
/*     */ 
/*     */   public void deleteFiles(HttpServletRequest request, HttpServletResponse response)
/*     */     throws Exception
/*     */   {
/* 457 */     JSONArray files = new JSONArray(request.getParameter("files"));
/* 458 */     int j = files.length();
/*     */ 
/* 460 */     for (int i = 0; i < j; i++) {
/* 461 */       String fileName = files.getString(i);
/* 462 */       File file = new File(fileName);
/* 463 */       FileUtil.checkRight(request, file);
/* 464 */       WebUtil.recordLog(request, "explorer删除:" + fileName, 1);
/* 465 */       if (file.isDirectory())
/* 466 */         FileUtil.deleteFolder(file);
/* 467 */       else if (!file.delete())
/* 468 */         throw new Exception("不能删除文件 \"" + file.getName() + "\"。");
/*     */     }
/*     */   }
/*     */ 
/*     */   public void pasteFiles(HttpServletRequest request, HttpServletResponse response) throws Exception
/*     */   {
/* 474 */     String filesParam = request.getParameter("files");
/* 475 */     String dir = request.getParameter("dir") + "/";
/* 476 */     File destFile = new File(dir);
/* 477 */     JSONArray files = new JSONArray(filesParam);
/* 478 */     boolean isCut = StringUtil.getStringBool(request.getParameter("isCut"));
/* 479 */     int j = files.length();
/*     */ 
/* 481 */     for (int i = 0; i < j; i++) {
/* 482 */       File file = new File(files.getString(i));
/* 483 */       File dest = new File(dir + file.getName());
/* 484 */       FileUtil.checkRight(request, file);
/* 485 */       FileUtil.checkRight(request, dest);
/* 486 */       WebUtil.recordLog(request, "explorer贴粘:" + (isCut ? "剪切" : "复制") + 
/* 487 */         "," + files.getString(i) + "至" + dir, 1);
/* 488 */       if (file.isDirectory()) {
/* 489 */         if (FileUtil.isSubFolder(file, destFile))
/* 490 */           throw new Exception("不能复制相同的文件夹。");
/* 491 */         FileUtil.copyFolder(file, dest, true, isCut);
/*     */       } else {
/* 493 */         FileUtil.copyFile(file, dest, true, isCut);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void rename(HttpServletRequest request, HttpServletResponse response) throws Exception {
/* 499 */     String fileName = request.getParameter("fileName");
/* 500 */     String rename = request.getParameter("fileValue");
/* 501 */     File file = new File(fileName);
/* 502 */     FileUtil.checkRight(request, file);
/* 503 */     if ((rename.indexOf("/") > -1) || 
/* 504 */       (rename.indexOf("\\") > -1) || 
/* 505 */       (!file.renameTo(
/* 506 */       new File(FileUtil.extractFilePath(fileName) + 
/* 506 */       rename))))
/* 507 */       throw new Exception("重命名失败。");
/*     */   }
/*     */ 
/*     */   public void newFile(HttpServletRequest request, HttpServletResponse response) throws Exception
/*     */   {
/* 512 */     String name = request.getParameter("fileValue");
/* 513 */     String fileName = request.getParameter("dir") + "/" + name;
/* 514 */     String type = request.getParameter("type");
/*     */ 
/* 517 */     File file = new File(fileName);
/* 518 */     FileUtil.checkRight(request, file);
/*     */     boolean flag;
/*     */
/* 519 */     if (type.equals("dir"))
/* 520 */       flag = file.mkdir();
/*     */     else
/* 522 */       flag = file.createNewFile();
/* 523 */     if (!flag)
/* 524 */       throw new Exception("不能创建\"" + name + "\"");
/*     */   }
/*     */ 
/*     */   public void getPubDir(HttpServletRequest request, HttpServletResponse response) throws Exception
/*     */   {
/* 529 */     String dir = request.getParameter("dir");
/* 530 */     StringBuilder buf = new StringBuilder();
/* 531 */     String root = request.getAttribute("sys.path").toString() + 
/* 532 */       "WEB-INF/myfile";
/* 533 */     String scope = request.getAttribute("sys.scope").toString();
/*     */ 
/* 535 */     if (StringUtil.isEmpty(dir)) {
/* 536 */       loadPubDir(FileUtil.fetchPubDir(root, scope), buf);
/*     */     } else {
/* 538 */       File fl = new File(dir);
/* 539 */       FileUtil.checkRight(request, fl);
/* 540 */       File[] files = fl.listFiles();
/* 541 */       FileUtil.sortFiles(files);
/* 542 */       loadFilesBuf(files, buf);
/*     */     }
/* 544 */     response.getWriter().print(buf);
/*     */   }
/*     */ 
/*     */   public void getUserDir(HttpServletRequest request, HttpServletResponse response) throws Exception
/*     */   {
/* 549 */     String dir = request.getParameter("dir");
/* 550 */     StringBuilder buf = new StringBuilder();
/*     */ 
/* 552 */     if (StringUtil.isEmpty(dir)) {
/* 553 */       loadUserDir((String)request.getAttribute("sys.rootpath"), buf);
/*     */     } else {
/* 555 */       File fl = new File(dir);
/* 556 */       FileUtil.checkRight(request, fl);
/* 557 */       File[] files = fl.listFiles();
/* 558 */       FileUtil.sortFiles(files);
/* 559 */       loadFilesBuf(files, buf);
/*     */     }
/* 561 */     response.getWriter().print(buf);
/*     */   }
/*     */ 
/*     */   public void getDir(HttpServletRequest request, HttpServletResponse response) throws Exception
/*     */   {
/* 566 */     String dir = request.getParameter("dir");
/* 567 */     boolean appRoot = StringUtil.getStringBool(request
/* 568 */       .getParameter("setAppRoot"));
/* 569 */     StringBuilder buf = new StringBuilder();
/*     */ 
/* 571 */     if (StringUtil.isEmpty(dir)) {
/* 572 */       if ((appRoot) || (!loadFilesBuf(File.listRoots(), buf))) {
/* 573 */         buf = new StringBuilder();
/* 574 */         loadAppDir((String)request.getAttribute("sys.path"), buf);
/*     */       }
/*     */     } else {
/* 577 */       File[] files = new File(dir).listFiles();
/* 578 */       FileUtil.sortFiles(files);
/* 579 */       loadFilesBuf(files, buf);
/*     */     }
/* 581 */     response.getWriter().print(buf);
/*     */   }
/*     */ 
/*     */   public void getFile(HttpServletRequest request, HttpServletResponse response) throws Exception
/*     */   {
/* 586 */     String dir = request.getParameter("dir");
/* 587 */     File dirFile = new File(dir);
/* 588 */     FileUtil.checkRight(request, dirFile);
/* 589 */     File[] files = dirFile.listFiles();
/* 590 */     if (files == null) {
/* 591 */       response.getWriter().print("{total:0,row:[]}");
/* 592 */       return;
/*     */     }
/* 594 */     FileUtil.sortFiles(files);
/* 595 */     StringBuilder buf = new StringBuilder();
/* 596 */     FileSystemView fileView = FileSystemView.getFileSystemView();
/* 597 */     boolean isFirst = true;
/* 598 */     String start = request.getParameter("start");
/* 599 */     String limit = request.getParameter("limit");
/* 600 */     int count = 0;
/*     */     int startValue;
/*     */ 
/* 602 */     if (start == null)
/* 603 */       startValue = 1;
/*     */     else
/* 605 */       startValue = Integer.parseInt(start) + 1;
/*     */     int limitValue;
/*     */ 
/* 606 */     if (limit == null)
/* 607 */       limitValue = 2147483647 - startValue;
/*     */     else
/* 609 */       limitValue = Integer.parseInt(limit);
/* 610 */     int end = startValue + limitValue - 1;
/* 611 */     buf.append("{total:");
/* 612 */     buf.append(files.length);
/* 613 */     buf.append(",row:[");
/* 614 */     for (File file : files) {
/* 615 */       count++;
/* 616 */       if (count < startValue)
/*     */         continue;
/* 618 */       if (count > end)
/*     */         break;
/* 620 */       if (isFirst)
/* 621 */         isFirst = false;
/*     */       else
/* 623 */         buf.append(",");
/* 624 */       boolean isDir = file.isDirectory();
/* 625 */       buf.append("{filename:\"");
/* 626 */       if (isDir)
/* 627 */         buf.append("0");
/*     */       else
/* 629 */         buf.append("1");
/* 630 */       String fileName = file.getName();
/* 631 */       buf.append(fileName);
/* 632 */       buf.append("\",size:");
/* 633 */       if (isDir)
/* 634 */         buf.append(-1);
/*     */       else
/* 636 */         buf.append(file.length());
/* 637 */       buf.append(",file:\"");
/* 638 */       buf.append(StringUtil.replace(file.getAbsolutePath(), "\\", "/"));
/* 639 */       buf.append("\",type:\"");
/* 640 */       if (isDir) {
/* 641 */         buf.append("0文件夹"); } else {
/*     */         String type;
/*     */         try { type = fileView.getSystemTypeDescription(file);
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/*     */          
/* 646 */           type = null;
/*     */         }
/* 648 */         if (type != null) {
/* 649 */           buf.append("1" + StringUtil.toExpress(type));
/*     */         } else {
/* 651 */           String ext = FileUtil.extractFileExt(fileName);
/* 652 */           if (StringUtil.isEmpty(ext)) {
/* 653 */             buf.append("1文件");
/*     */           } else {
/* 655 */             buf.append("1" + ext);
/* 656 */             buf.append(" 文件");
/*     */           }
/*     */         }
/*     */       }
/* 660 */       buf.append("\",modifyTime:\"");
/* 661 */       buf.append(DateUtil.dateToString(new Date(file.lastModified())));
/* 662 */       buf.append("\"}");
/*     */     }
/* 664 */     buf.append("]}");
/* 665 */     response.getWriter().print(buf);
/*     */   }
/*     */ 
/*     */   public void getIcon(HttpServletRequest request, HttpServletResponse response)
/*     */     throws Exception
/*     */   {
/* 671 */     File file = new File(new String(request.getParameter("file")
/* 671 */       .getBytes("ISO-8859-1"), "utf-8")); FileSystemView fileView = FileSystemView.getFileSystemView(); Icon icon = fileView.getSystemIcon(file); response.reset(); if (icon == null) { response.setContentType("image/gif"); InputStream is = new FileInputStream(request.getAttribute("sys.path") + "webbuilder/images/file.gif"); SysUtil.inputStreamToOutputStream(is, response.getOutputStream()); is.close(); } else { response.setContentType("image/jpeg"); int width = icon.getIconWidth(); int height = icon.getIconHeight(); BufferedImage image = new BufferedImage(width, height, 1); Graphics graphics = image.getGraphics(); graphics.setColor(Color.white); graphics.fillRect(0, 0, width, height); icon.paintIcon(null, graphics, 0, 0); ImageIO.write(image, "jpeg", response.getOutputStream()); graphics.dispose();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void loadPubDir(String pubDir, StringBuilder buf)
/*     */   {
/* 697 */     buf.append("[");
/* 698 */     buf.append("{text:\"公共文件\",dir:\"");
/* 699 */     buf.append(StringUtil.toExpress(pubDir));
/* 700 */     buf.append("\"}");
/* 701 */     buf.append("]");
/*     */   }
/*     */ 
/*     */   private void loadUserDir(String userDir, StringBuilder buf) {
/* 705 */     buf.append("[");
/* 706 */     buf.append("{text:\"我的文件\",dir:\"");
/* 707 */     buf.append(StringUtil.toExpress(userDir));
/* 708 */     buf.append("\"}");
/* 709 */     buf.append("]");
/*     */   }
/*     */ 
/*     */   private void loadAppDir(String appDir, StringBuilder buf) {
/* 713 */     String s = FileUtil.extractFileDir(appDir);
/* 714 */     buf.append("[");
/* 715 */     buf.append("{text:\"");
/* 716 */     buf.append(StringUtil.toExpress(s));
/* 717 */     buf.append("\",dir:\"");
/* 718 */     buf.append(StringUtil.toExpress(s));
/* 719 */     buf.append("\"}");
/* 720 */     buf.append("]");
/*     */   }
/*     */ 
/*     */   private boolean loadFilesBuf(File[] files, StringBuilder buf) {
/* 724 */     boolean isOk = false; boolean isFirst = true;
/*     */ 
/* 727 */     buf.append("[");
/* 728 */     for (File file : files) {
/* 729 */       if (file.isDirectory()) {
/* 730 */         isOk = true;
/* 731 */         if (isFirst)
/* 732 */           isFirst = false;
/*     */         else
/* 734 */           buf.append(",");
/* 735 */         buf.append("{text:\"");
/* 736 */         String name = file.getName();
/* 737 */         String dir = StringUtil.replace(file.getAbsolutePath(), "\\", "/");
/* 738 */         if (StringUtil.isEmpty(name))
/* 739 */           name = FileUtil.extractFileDir(dir);
/* 740 */         buf.append(StringUtil.toExpress(name));
/* 741 */         buf.append("\",dir:\"");
/* 742 */         buf.append(StringUtil.replace(dir, "\\", "/"));
/* 743 */         if (FileUtil.hasSubFile(file, true))
/* 744 */           buf.append("\"}");
/*     */         else
/* 746 */           buf.append("\",leaf:true,iconCls:\"icon_folder\"}");
/*     */       }
/*     */     }
/* 749 */     buf.append("]");
/* 750 */     return isOk;
/*     */   }
/*     */ }

/* Location:           Z:\EXT\WebBuilderServer (1)\WEB-INF\lib\webbuilder2.jar
 * Qualified Name:     com.webbuilder.interact.Explorer
 * JD-Core Version:    0.6.0
 */