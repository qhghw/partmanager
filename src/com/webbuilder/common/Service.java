/*     */ package com.webbuilder.common;
/*     */ 
/*     */ import com.webbuilder.utils.DateUtil;
/*     */ import com.webbuilder.utils.FileUtil;
/*     */ import com.webbuilder.utils.StringUtil;
/*     */ import com.webbuilder.utils.WebUtil;
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import javax.imageio.ImageIO;
/*     */ import javax.servlet.ServletOutputStream;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import jxl.SheetSettings;
/*     */ import jxl.Workbook;
/*     */ import jxl.format.Alignment;
/*     */ import jxl.format.Border;
/*     */ import jxl.format.BorderLineStyle;
/*     */ import jxl.write.Label;
/*     */ import jxl.write.Number;
/*     */ import jxl.write.NumberFormat;
/*     */ import jxl.write.WritableCellFormat;
/*     */ import jxl.write.WritableFont;
/*     */ import jxl.write.WritableFont.FontName;
/*     */ import jxl.write.WritableSheet;
/*     */ import jxl.write.WritableWorkbook;
/*     */ import org.json.JSONArray;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class Service
/*     */ {
/*     */   public void download(HttpServletRequest request, HttpServletResponse response)
/*     */     throws Exception
/*     */   {
/*  38 */     String type = (String)request.getAttribute("type");
/*  39 */     String filename = (String)request.getAttribute("filename");
/*     */ 
/*  41 */     byte[] data = (byte[])null;
/*     */ 
/*  43 */     if (filename == null)
/*  44 */       filename = "";
/*  45 */     response.reset();
/*  46 */     if (StringUtil.isEqual(type, "xls"))
/*     */     {
/*     */       File file;

/*  47 */       if (!StringUtil.isSame(FileUtil.extractFileExt(filename), "xls"))
/*  48 */         file = null;
/*     */       else
/*  50 */         file = FileUtil.getFullFile((String)request
/*  51 */           .getAttribute("sys.path"), filename);
/*  52 */       data = getExcelBytes(request, file);
/*  53 */     } else if (StringUtil.isEqual(type, "txt")) {
/*  54 */       data = getTextBytes(request);
/*  55 */     }response.setHeader("content-length", Integer.toString(data.length));
/*  56 */     response.setHeader("content-type", "application/force-download");
/*  57 */     filename = FileUtil.extractFileName(filename);
/*  58 */     if (StringUtil.isEmpty(filename))
/*  59 */       filename = "data." + type;
/*  60 */     else if (StringUtil.isEmpty(FileUtil.extractFileExt(filename)))
/*  61 */       filename = filename + "." + type;
/*  62 */     String charset = (String)request.getAttribute("sys.fileCharset");
/*  63 */     response.setHeader("content-disposition", "attachment;filename=" + 
/*  64 */       WebUtil.getFileName(filename, charset));
/*  65 */     response.getOutputStream().write(data);
/*     */   }
/*     */ 
/*     */   public void downloadChart(HttpServletRequest request, HttpServletResponse response) throws Exception
/*     */   {
/*  70 */     String data = "";
/*     */ 
/*  72 */     int width = 0;
/*  73 */     int height = 0;
/*  74 */     String bgcolor = "";
/*     */ 
/*  77 */     width = Integer.parseInt(request.getParameter("width"));
/*  78 */     height = Integer.parseInt(request.getParameter("height"));
/*  79 */     bgcolor = request.getParameter("bgcolor");
/*  80 */     if (StringUtil.isEmpty(bgcolor))
/*  81 */       bgcolor = "FFFFFF";
/*  82 */     Color bgColor = new Color(Integer.parseInt(bgcolor, 16));
/*  83 */     data = request.getParameter("data");
/*  84 */     String[] rows = new String[height + 1];
/*  85 */     rows = StringUtil.split(data, ";");
/*  86 */     BufferedImage chart = new BufferedImage(width, height, 
/*  87 */       5);
/*  88 */     Graphics gr = chart.createGraphics();
/*  89 */     gr.setColor(bgColor);
/*  90 */     gr.fillRect(0, 0, width, height);
/*     */ 
/*  93 */     int ri = 0;
/*  94 */     for (int i = 0; i < rows.length; i++) {
/*  95 */       String[] pixels = StringUtil.split(rows[i], ",");
/*  96 */       ri = 0;
/*  97 */       for (int j = 0; j < pixels.length; j++) {
/*  98 */         String[] clrs = StringUtil.split(pixels[j], "_");
/*  99 */         String c = clrs[0];
/* 100 */         int r = Integer.parseInt(clrs[1]);
/* 101 */         if (!StringUtil.isEmpty(c)) {
/* 102 */           if (c.length() < 6) {
/* 103 */             StringBuffer strs = new StringBuffer(c);
/* 104 */             for (int p = c.length() + 1; p <= 6; p++) {
/* 105 */               strs.insert(0, "0");
/*     */             }
/* 107 */             c = strs.toString();
/*     */           }
/* 109 */           for (int k = 1; k <= r; k++) {
/* 110 */             gr.setColor(new Color(Integer.parseInt(c, 16)));
/* 111 */             gr.fillRect(ri, i, 1, 1);
/* 112 */             ri++;
/*     */           }
/*     */         } else {
/* 115 */           ri += r;
/*     */         }
/*     */       }
/*     */     }
/* 119 */     response.reset();
/* 120 */     response.setContentType("image/jpeg");
/* 121 */     response.addHeader("content-disposition", 
/* 122 */       "attachment; filename=\"chart.jpg\"");
/* 123 */     ImageIO.write(chart, "jpeg", response.getOutputStream());
/*     */   }
/*     */ 
/*     */   private byte[] getExcelBytes(HttpServletRequest request, File file) throws Exception
/*     */   {
/* 128 */     ByteArrayOutputStream os = new ByteArrayOutputStream();
/*     */ 
/* 132 */     JSONArray items = new JSONArray(request.getAttribute("data").toString());
/* 133 */     JSONArray fields = new JSONArray(request.getAttribute("fields")
/* 134 */       .toString());
/*     */ 
/* 136 */     ArrayList mapList = getMapperList(request
/* 137 */       .getAttribute("mapper").toString());
/*     */ 
/* 141 */     int rows = items.length(); int cols = fields.length();
/* 142 */     boolean hasBorder = StringUtil.getStringBool((String)request
/* 143 */       .getAttribute("border"));
/*     */ 
/* 145 */     String[] names = new String[cols]; String[] types = new String[cols];
/* 146 */     WritableCellFormat[] cellFormat = new WritableCellFormat[cols];
/*     */ 
/* 148 */     String ldate = (String)request.getAttribute("sys.longDateFormat");
/* 149 */     String sdate = (String)request.getAttribute("sys.shortDateFormat");
/* 150 */     boolean setFormat = (!StringUtil.isEmpty(ldate)) && 
/* 151 */       (!StringUtil.isEmpty(sdate));
/*     */     int startRow;
/*     */     WritableWorkbook book;
/*     */     WritableSheet ws;
/*  
/* 153 */     if (file == null) {
/* 154 */     book = Workbook.createWorkbook(os);

/* 155 */       if (request.getAttribute("sheetName") == null)
/* 156 */         ws = book.createSheet("Sheet1", 0);
/*     */       else
/* 158 */         ws = book.createSheet(request.getAttribute("sheetName")
/* 159 */           .toString(), 0);
/* 160 */       startRow = 0;
/*     */     } else {
/* 162 */       book = Workbook.createWorkbook(os, Workbook.getWorkbook(file));
/* 163 */       ws = book.getSheet(0);
/* 164 */       startRow = 1;
/*     */     }
/* 166 */     SheetSettings sts = ws.getSettings();
/* 167 */     sts.setDefaultColumnWidth(20);
/* 168 */     WritableFont.FontName fontName = WritableFont.createFont("宋体");
/* 169 */     WritableCellFormat format = new WritableCellFormat(
/* 170 */       new WritableFont(fontName, 9, 
/* 170 */       WritableFont.BOLD));
/* 171 */     format.setAlignment(Alignment.CENTRE);
/* 172 */     if (hasBorder)
/* 173 */       format.setBorder(Border.ALL, BorderLineStyle.THIN);
/* 174 */     boolean[] flag = new boolean[cols];
/* 175 */     for (int i = 0; i < cols; i++) {
/* 176 */       HashMap map = (HashMap)mapList.get(i);
/* 177 */       flag[i] = ((map != null) && (map.size() == 1) && (map.get("-1") != null) ? true : false);
/*     */     }
/* 179 */     for (int i = 0; i < cols; i++) {
/* 180 */       JSONObject json = fields.getJSONObject(i);
/* 181 */       if (file == null)
/* 182 */         ws.addCell(
/* 183 */           new Label(i, 0, json.getString("title"), 
/* 183 */           format));
/* 184 */       types[i] = json.getString("type");
/* 185 */       names[i] = json.getString("name");
/* 186 */       if ((file == null) && (json.has("width")))
/* 187 */         ws.setColumnView(i, 
/* 188 */           Integer.parseInt(json.getString("width")) / 7 + 1);
/*     */       String formatString;

/* 189 */       if (json.has("format"))
/* 190 */         formatString = json.getString("format");
/*     */       else
/* 192 */         formatString = null;
/*     */       Alignment alignment;

/* 193 */       if (json.has("align")) {
/* 194 */         String align = json.getString("align");

/* 195 */         if (StringUtil.isSame(align, "right")) {
/* 196 */           alignment = Alignment.RIGHT;
/*     */         }
/*     */         else
/*     */         {

/* 197 */           if (StringUtil.isSame(align, "center"))
/* 198 */             alignment = Alignment.CENTRE;
/*     */           else
/* 200 */             alignment = Alignment.LEFT; 
/*     */         }
/*     */       } else {
/* 202 */         alignment = null;
/* 203 */       }cellFormat[i] = createCellFormat(alignment, fontName, 9, 
/* 204 */         formatString, hasBorder);
/*     */     }
/* 206 */     if (file == null)
/* 207 */       sts.setVerticalFreeze(1);
/*     */     else
/* 209 */       sts.setVerticalFreeze(2);
/* 210 */     for (int i = 0; i < rows; i++) {
/* 211 */       JSONObject json = items.getJSONObject(i);
/* 212 */       for (int j = 0; j < cols; j++) {
/* 213 */         HashMap map = (HashMap)mapList.get(j);
/* 214 */         String value = json.getString(names[j]);
/*     */         boolean isText;

/* 215 */         if (map == null) {
/* 216 */           isText = false;
/*     */         } else {
/* 218 */           if (flag[j] != false) {
/* 219 */             if (StringUtil.isEmpty(value))
/* 220 */               value = (String)map.get("-1");
/*     */           }
/* 222 */           else if (StringUtil.isEmpty(value)) {
/* 223 */             value = (String)map.get("-1");
/* 224 */             if (value == null)
/* 225 */               value = "";
/*     */           } else {
/* 227 */             String t = (String)map.get(value);
/* 228 */             if (t != null) {
/* 229 */               value = t;
/*     */             }
/*     */           }
/* 232 */           isText = true;
/*     */         }
/*     */         try {
/* 235 */           if (StringUtil.isEmpty(value)) {
/* 236 */             ws.addCell(
/* 237 */               new Label(j, i + 1 + startRow, "", 
/* 237 */               cellFormat[j]));
/* 238 */           } else if ((!isText) && (
/* 239 */             (StringUtil.isSame(types[j], "int")) || 
/* 240 */             (StringUtil.isSame(types[j], "float")))) {
/* 241 */             ws.addCell(
/* 242 */               new Number(j, i + 1 + startRow, 
/* 242 */               Double.parseDouble(value), cellFormat[j]));
/* 243 */           } else if (StringUtil.isSame(types[j], "date")) {
/* 244 */             if (value.indexOf("T") != -1)
/* 245 */               value = StringUtil.replace(value, "T", " ");
/* 246 */             if (setFormat) {
/* 247 */               value = DateUtil.formatDateString(value, ldate, 
/* 248 */                 sdate);
/*     */             } else {
/* 250 */               int p = value.indexOf(" 00:00:00");
/* 251 */               if (p != -1)
/* 252 */                 value = value.substring(0, p);
/*     */             }
/* 254 */             ws.addCell(
/* 255 */               new Label(j, i + 1 + startRow, 
/* 255 */               value, cellFormat[j]));
/*     */           } else {
/* 257 */             ws.addCell(
/* 258 */               new Label(j, i + 1 + startRow, 
/* 258 */               value, cellFormat[j]));
/*     */           }
/*     */         } catch (Exception e) {
/* 260 */           ws.addCell(
/* 261 */             new Label(j, i + 1 + startRow, value, 
/* 261 */             cellFormat[j]));
/*     */         }
/*     */       }
/*     */     }
/* 265 */     book.write();
/* 266 */     book.close();
/* 267 */     return os.toByteArray();
/*     */   }
/*     */ 
/*     */   private byte[] getTextBytes(HttpServletRequest request) throws Exception {
/* 271 */     JSONArray items = new JSONArray(request.getAttribute("data").toString());
/* 272 */     JSONArray fields = new JSONArray(request.getAttribute("fields")
/* 273 */       .toString());
/*     */ 
/* 275 */     ArrayList mapList = getMapperList(request
/* 276 */       .getAttribute("mapper").toString());
/*     */ 
/* 278 */     int rows = items.length(); int cols = fields.length();
/* 279 */     String[] names = new String[cols]; String[] types = new String[cols];
/* 280 */     StringBuilder buf = new StringBuilder();
/* 281 */     String separator = StringUtil.getSeparator(request);
/* 282 */     String ldate = (String)request.getAttribute("sys.longDateFormat");
/* 283 */     String sdate = (String)request.getAttribute("sys.shortDateFormat");
/* 284 */     boolean setFormat = (!StringUtil.isEmpty(ldate)) && 
/* 285 */       (!StringUtil.isEmpty(sdate));
/*     */ 
/* 287 */     for (int i = 0; i < cols; i++) {
/* 288 */       JSONObject json = fields.getJSONObject(i);
/* 289 */       names[i] = json.getString("name");
/* 290 */       types[i] = json.getString("type");
/* 291 */       if (i > 0)
/* 292 */         buf.append(separator);
/* 293 */       buf.append(json.getString("title"));
/*     */     }
/* 295 */     boolean[] flag = new boolean[cols];
/* 296 */     for (int i = 0; i < cols; i++) {
/* 297 */       HashMap map = (HashMap)mapList.get(i);
/* 298 */       flag[i] = ((map != null) && (map.size() == 1) && (map.get("-1") != null) ? true : false);
/*     */     }
/* 300 */     for (int i = 0; i < rows; i++) {
/* 301 */       JSONObject json = items.getJSONObject(i);
/* 302 */       buf.append("\r\n");
/* 303 */       for (int j = 0; j < cols; j++) {
/* 304 */         if (j > 0)
/* 305 */           buf.append(separator);
/* 306 */         HashMap map = (HashMap)mapList.get(j);
/* 307 */         String val = json.getString(names[j]);
/* 308 */         if (map != null) {
/* 309 */           if (flag[j] != false) {
/* 310 */             if (StringUtil.isEmpty(val))
/* 311 */               val = (String)map.get("-1");
/*     */           } else {
/* 313 */             val = json.getString(names[j]);
/* 314 */             if (flag[j] != false) {
/* 315 */               if (StringUtil.isEmpty(val))
/* 316 */                 val = (String)map.get("-1");
/*     */             }
/* 318 */             else if (StringUtil.isEmpty(val)) {
/* 319 */               val = (String)map.get("-1");
/* 320 */               if (val == null)
/* 321 */                 val = "";
/*     */             } else {
/* 323 */               String t = (String)map.get(val);
/* 324 */               if (t != null) {
/* 325 */                 val = t;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/* 330 */         if (StringUtil.isSame(types[j], "date")) {
/* 331 */           if (val.indexOf("T") != -1)
/* 332 */             val = StringUtil.replace(val, "T", " ");
/* 333 */           if (setFormat) {
/* 334 */             val = DateUtil.formatDateString(val, ldate, sdate);
/*     */           } else {
/* 336 */             int p = val.indexOf(" 00:00:00");
/* 337 */             if (p != -1)
/* 338 */               val = val.substring(0, p);
/*     */           }
/*     */         }
/* 341 */         buf.append(StringUtil.toLineString(val, false));
/*     */       }
/*     */     }
/* 344 */     String charset = request.getAttribute("sys.charset").toString();
/* 345 */     if (StringUtil.isEmpty(charset)) {
/* 346 */       return buf.toString().getBytes();
/*     */     }
/* 348 */     return buf.toString().getBytes(charset);
/*     */   }
/*     */ 
/*     */   private ArrayList<HashMap<String, String>> getMapperList(String mapText)
/*     */     throws Exception
/*     */   {
/* 354 */     String[] emptyLabel = StringUtil.split(StringUtil.getNamePart(mapText), 
/* 355 */       ",");
/* 356 */     JSONArray mapper = new JSONArray(StringUtil.getValuePart(mapText));
/* 357 */     ArrayList mapList = new ArrayList();
/*     */ 
/* 359 */     int j = emptyLabel.length;
/*     */ 
/* 362 */     for (int i = 0; i < j; i++) {
/* 363 */       boolean b1 = !StringUtil.isEqual(mapper.getString(i), "0");
/* 364 */       boolean b2 = !StringUtil.isEmpty(emptyLabel[i]);
/* 365 */       if ((b1) || (b2)) {
/* 366 */         HashMap hashMap = new HashMap();
/* 367 */         if (b1) {
/* 368 */           JSONArray arr = mapper.getJSONArray(i);
/* 369 */           int l = arr.length();
/* 370 */           for (int k = 0; k < l; k++) {
/* 371 */             JSONArray sub = arr.getJSONArray(k);
/* 372 */             hashMap.put(sub.getString(1), sub.getString(0));
/*     */           }
/*     */         }
/* 375 */         if (b2)
/* 376 */           hashMap.put("-1", emptyLabel[i]);
/* 377 */         mapList.add(hashMap);
/*     */       } else {
/* 379 */         mapList.add(null);
/*     */       }
/*     */     }
/* 381 */     return mapList;
/*     */   }
/*     */ 
/*     */   private WritableCellFormat createCellFormat(Alignment alignment, WritableFont.FontName fontName, int fontSize, String format, boolean hasBorder)
/*     */     throws Exception
/*     */   {
/*     */     WritableCellFormat cellFormat;

/* 389 */     if (format == null)
/* 390 */       cellFormat = new WritableCellFormat(
/* 391 */         new WritableFont(fontName, 
/* 391 */         fontSize));
/*     */     else
/* 393 */       cellFormat = new WritableCellFormat(
/* 394 */         new WritableFont(fontName, 
/* 394 */         fontSize), new NumberFormat(format));
/* 395 */     if (alignment != null)
/* 396 */       cellFormat.setAlignment(alignment);
/* 397 */     if (hasBorder)
/* 398 */       cellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
/* 399 */     return cellFormat;
/*     */   }
/*     */ }

/* Location:           Z:\EXT\WebBuilderServer (1)\WEB-INF\lib\webbuilder2.jar
 * Qualified Name:     com.webbuilder.common.Service
 * JD-Core Version:    0.6.0
 */