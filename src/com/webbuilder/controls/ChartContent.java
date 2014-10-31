/*     */ package com.webbuilder.controls;
/*     */ 
/*     */ import com.webbuilder.utils.DbUtil;
/*     */ import com.webbuilder.utils.StringUtil;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.ResultSetMetaData;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import org.dom4j.Document;
/*     */ import org.dom4j.DocumentHelper;
/*     */ import org.dom4j.Element;
/*     */ import org.json.JSONArray;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class ChartContent extends Control
/*     */ {
/*  21 */   public String subCaption = "";
/*  22 */   public String xAxisName = "";
/*  23 */   public String yAxisName = "";
/*  24 */   public String query = "";
/*  25 */   public String palette = "";
/*  26 */   public String simpleSet = "";
/*  27 */   public String fontName = "";
/*  28 */   public String fontColor = "";
/*  29 */   public String fontSize = "";
/*  30 */   public String labelDisplay = "";
/*  31 */   public String showLabels = "";
/*  32 */   public String animation = "";
/*  33 */   public String rotateLabels = "";
/*  34 */   public String slantLabels = "";
/*  35 */   public String labelStep = "";
/*  36 */   public String staggerLines = "";
/*  37 */   public String showValues = "";
/*  38 */   public String showPercentageValues = "";
/*  39 */   public String rotateValues = "";
/*  40 */   public String placeValuesInside = "";
/*  41 */   public String showYAxisValues = "";
/*  42 */   public String showLimits = "";
/*  43 */   public String showDivLineValues = "";
/*  44 */   public String yAxisValuesStep = "";
/*  45 */   public String yAxisMinValue = "";
/*  46 */   public String yAxisMaxValue = "";
/*  47 */   public String setAdaptiveYMin = "";
/*  48 */   public String showShadow = "";
/*  49 */   public String adjustDiv = "";
/*  50 */   public String rotateYAxisName = "";
/*  51 */   public String yAxisNameWidth = "";
/*  52 */   public String clickURL = "";
/*  53 */   public String defaultAnimation = "";
/*  54 */   public String bgSWF = "";
/*  55 */   public String bgSWFAlpha = "";
/*  56 */   public String bgColor = "";
/*  57 */   public String bgAlpha = "";
/*  58 */   public String canvasbgColor = "";
/*  59 */   public String canvasbgAlpha = "";
/*  60 */   public String plotGradientColor = "";
/*  61 */   public String useRoundEdges = "";
/*  62 */   public String numberPrefix = "";
/*  63 */   public String numberSuffix = "";
/*  64 */   public String showToolTip = "";
/*  65 */   public String enableSmartLabels = "";
/*  66 */   public String showPercentValues = "";
/*  67 */   public String decimals = "";
/*  68 */   public String formatNumber = "";
/*  69 */   public String formatNumberScale = "";
/*  70 */   public String yAxisValueDecimals = "";
/*  71 */   public String numDivLines = "";
/*  72 */   public String defaultNumberScale = "";
/*  73 */   public String numberScaleValue = "";
/*  74 */   public String numberScaleUnit = "";
/*  75 */   public String itemProperty = "";
/*  76 */   public String itemFieldCount = "";
/*  77 */   public String tagXML = "";
/*  78 */   public String showLegend = "";
/*  79 */   public String legendPosition = "";
/*  80 */   public String imageSave = "";
/*  81 */   public String mapKey = "";
/*  82 */   public String emptyLabel = "";
/*  83 */   public boolean asComponent = true;
/*  84 */   private int colPos = 0;
/*  85 */   private Object[] mapList = null;
/*  86 */   private Integer[] indexList = null;
/*  87 */   private HashMap<Integer, String> emptyHash = null;
/*     */   private Element chart;
/*     */ 
/*     */   protected void descript()
/*     */     throws Exception
/*     */   {
/*  91 */     intiMaps(this.request);
/*     */ 
/*  93 */     Document document = DocumentHelper.createDocument();
/*  94 */     document.setXMLEncoding("utf-8");
/*  95 */     this.chart = document.addElement("chart");
/*  96 */     setChart("palette", this.palette);
/*  97 */     setChart("caption", getCaption());
/*  98 */     setChart("subCaption", this.subCaption);
/*  99 */     setChart("xAxisName", this.xAxisName);
/* 100 */     setChart("yAxisName", this.yAxisName);
/* 101 */     if (StringUtil.isEmpty(this.fontName))
/* 102 */       this.fontName = "宋体";
/* 103 */     setChart("baseFont", this.fontName);
/* 104 */     if (StringUtil.isEmpty(this.fontSize))
/* 105 */       this.fontSize = "12";
/* 106 */     setChart("baseFontSize", this.fontSize);
/* 107 */     setChart("baseFontColor", this.fontColor);
/* 108 */     setChart("labelDisplay", this.labelDisplay);
/* 109 */     setChart("showLabels", this.showLabels);
/* 110 */     setChart("animation", this.animation);
/* 111 */     setChart("rotateLabels", this.rotateLabels);
/* 112 */     setChart("slantLabels", this.slantLabels);
/* 113 */     setChart("labelStep", this.labelStep);
/* 114 */     setChart("staggerLines", this.staggerLines);
/* 115 */     setChart("showValues", this.showValues);
/* 116 */     setChart("showPercentageValues", this.showPercentageValues);
/* 117 */     setChart("rotateValues", this.rotateValues);
/* 118 */     setChart("placeValuesInside", this.placeValuesInside);
/* 119 */     setChart("showYAxisValues", this.showYAxisValues);
/* 120 */     setChart("showLimits", this.showLimits);
/* 121 */     setChart("showDivLineValues", this.showDivLineValues);
/* 122 */     setChart("yAxisValuesStep", this.yAxisValuesStep);
/* 123 */     setChart("yAxisMinValue", this.yAxisMinValue);
/* 124 */     setChart("yAxisMaxValue", this.yAxisMaxValue);
/* 125 */     setChart("setAdaptiveYMin", this.setAdaptiveYMin);
/* 126 */     setChart("showShadow", this.showShadow);
/* 127 */     setChart("adjustDiv", this.adjustDiv);
/* 128 */     setChart("rotateYAxisName", this.rotateYAxisName);
/* 129 */     setChart("yAxisNameWidth", this.yAxisNameWidth);
/* 130 */     setChart("clickURL", this.clickURL);
/* 131 */     setChart("defaultAnimation", this.defaultAnimation);
/* 132 */     setChart("bgSWF", this.bgSWF);
/* 133 */     setChart("bgSWFAlpha", this.bgSWFAlpha);
/* 134 */     setChart("bgAlpha", this.bgAlpha);
/* 135 */     setChart("bgColor", this.bgColor);
/* 136 */     setChart("plotGradientColor", this.plotGradientColor);
/* 137 */     setChart("useRoundEdges", this.useRoundEdges);
/* 138 */     setChart("numberPrefix", this.numberPrefix);
/* 139 */     setChart("numberSuffix", this.numberSuffix);
/* 140 */     setChart("showToolTip", this.showToolTip);
/* 141 */     setChart("enableSmartLabels", this.enableSmartLabels);
/* 142 */     setChart("showPercentValues", this.showPercentValues);
/* 143 */     setChart("decimals", this.decimals);
/* 144 */     setChart("formatNumber", this.formatNumber);
/* 145 */     setChart("formatNumberScale", this.formatNumberScale);
/* 146 */     setChart("yAxisValueDecimals", this.yAxisValueDecimals);
/* 147 */     setChart("numDivLines", this.numDivLines);
/* 148 */     setChart("defaultNumberScale", this.defaultNumberScale);
/* 149 */     setChart("numberScaleValue", this.numberScaleValue);
/* 150 */     setChart("numberScaleUnit", this.numberScaleUnit);
/* 151 */     setChart("showLegend", this.showLegend);
/* 152 */     setChart("legendPosition", this.legendPosition);
/* 153 */     setChart("imageSave", this.imageSave);
/* 154 */     if (StringUtil.getStringBool(this.imageSave))
/* 155 */       setChart("imageSaveURL", 
/* 156 */         "main?action=webbuilder/system/downloadChart.xwl");
/* 157 */     if (!StringUtil.isEmpty(this.tag)) {
/* 158 */       JSONObject tags = new JSONObject("{" + this.tag + "}");
/* 159 */       String[] names = JSONObject.getNames(tags);
/* 160 */       for (String name : names)
/* 161 */         setChart(name, tags.getString(name));
/*     */     }
/* 163 */     if ((!StringUtil.isEmpty(this.query)) && (this.query.indexOf(",") != -1))
/* 164 */       setMultiDataXML();
/*     */     else
/* 166 */       setDataXML();
/*     */     String outputXML;

/* 167 */     if (StringUtil.isEmpty(this.tagXML)) {
/* 168 */       outputXML = document.asXML();
/*     */     } else {
/* 170 */       outputXML = document.asXML();
/* 171 */       int pos1 = outputXML.lastIndexOf("</chart>");
/* 172 */       int pos2 = outputXML.lastIndexOf("/>");
/* 173 */       if (pos2 > pos1)
/* 174 */         outputXML = outputXML.substring(0, pos2) + ">" + this.tagXML + 
/* 175 */           "</chart>";
/*     */       else
/* 177 */         outputXML = outputXML.substring(0, pos1) + this.tagXML + 
/* 178 */           outputXML.substring(pos1);
/*     */     }
/* 180 */     if (this.asComponent) {
/* 181 */       this.request.setAttribute(this.name, outputXML);
/*     */     } else {
/* 183 */       this.response.reset();
/* 184 */       this.response.setContentType("text/xml;charset=utf-8");
/* 185 */       setHeader(outputXML);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void setChart(String property, String value) {
/* 190 */     if (!StringUtil.isEmpty(value))
/* 191 */       this.chart.addAttribute(property, value);
/*     */   }
/*     */ 
/*     */   private void setSimpleXML(ResultSet resultSet, ResultSetMetaData meta)
/*     */     throws SQLException
/*     */   {
/* 197 */     boolean first = true;
/*     */ 
/* 201 */     int cols = meta.getColumnCount();
/* 202 */     while ((first) || (resultSet.next())) {
/* 203 */       if (first)
/* 204 */         first = false;
/* 205 */       Element element = this.chart.addElement("set");
/* 206 */       element.addAttribute("label", getMapString(resultSet.getString(1), 
/* 207 */         1));
/* 208 */       element.addAttribute("value", getMapString(resultSet.getString(2), 
/* 209 */         2));
/* 210 */       for (int i = 3; i <= cols; ++i) {
/* 211 */         String value = getMapString(resultSet.getString(i), i);
/* 212 */         if (!StringUtil.isEmpty(value))
/* 213 */           element.addAttribute(meta.getColumnLabel(i), value);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void setMultiXML(ResultSet resultSet, ResultSetMetaData meta)
/*     */     throws Exception
/*     */   {
/* 221 */     boolean first = true;
/*     */ 
/* 224 */     ArrayList list = new ArrayList();
/*     */     JSONArray properties;

/* 228 */     if (StringUtil.isEmpty(this.itemProperty))
/* 229 */       properties = null;
/*     */     else
/* 231 */       properties = new JSONArray("[" + this.itemProperty + "]");
/*     */     int[] count;

/* 232 */     if (StringUtil.isEmpty(this.itemFieldCount)) {
/* 233 */       count = (int[])null;
/*     */     } else {
/* 235 */       String[] ft = StringUtil.split(this.itemFieldCount, ",");
/* 236 */       int j = ft.length;
/* 237 */       count = new int[j];
/* 238 */       for (int i = 0; i < j; ++i) {
/* 239 */         count[i] = Integer.parseInt(ft[i]);
/* 240 */         if (i > 0)
/* 241 */           count[i] += count[(i - 1)];
/*     */       }
/*     */     }
/*     */     int cols;

/* 244 */     if (count == null)
/* 245 */       cols = meta.getColumnCount();
/*     */     else
/* 247 */       cols = count.length;
/* 248 */     for (int i = 0; i < cols; ++i)
/*     */     {
/*     */       Element element;

/* 249 */       if (i == 0)
/* 250 */         element = this.chart.addElement("categories");
/*     */       else
/* 252 */         element = this.chart.addElement("dataset");
/* 253 */       if (properties != null) {
/* 254 */         JSONObject jsonObj = properties.getJSONObject(i);
/* 255 */         String[] names = JSONObject.getNames(jsonObj);
/* 256 */         if (names != null)
/* 257 */           for (String name : names)
/* 258 */             element.addAttribute(name, jsonObj.getString(name));
/* 259 */       } else if (count == null) {
/* 260 */         element.addAttribute("seriesName", meta.getColumnLabel(i + 1));
/* 261 */       }list.add(element);
/*     */     }
/* 263 */     while ((first) || (resultSet.next())) {
/* 264 */       if (first)
/* 265 */         first = false;
/* 266 */       int k = 1;
/* 267 */       for (int i = 0; i < cols; ++i) {
/* 268 */         Element element = (Element)list.get(i);
/* 269 */         if (count == null)
/*     */         {
/*     */           Element sub;

/* 270 */           if (i == 0)
/* 271 */             sub = element.addElement("category");
/*     */           else
/* 273 */             sub = element.addElement("set");
/* 274 */           String value = getMapString(resultSet.getString(i + 1), i + 1);
/* 275 */           if (!StringUtil.isEmpty(value))
/* 276 */             if (i == 0)
/* 277 */               sub.addAttribute("label", value);
/*     */             else
/* 279 */               sub.addAttribute("value", value);
/*     */         }
/*     */         else
/*     */         {
/*     */           Element sub;

/* 281 */           if (i == 0)
/* 282 */             sub = element.addElement("category");
/*     */           else
/* 284 */             sub = element.addElement("set");
/* 285 */           for (int j = k; j <= count[i]; ++j) {
/* 286 */             String value = getMapString(resultSet.getString(j), j);
/* 287 */             if (!StringUtil.isEmpty(value)) {
/* 288 */               String temp = meta.getColumnLabel(j);
/*     */ 
/* 290 */               if (StringUtil.substring(temp, 0, 5)
/* 290 */                 .equalsIgnoreCase("value"))
/* 291 */                 sub.addAttribute("value", value);
/*     */               else
/* 293 */                 sub.addAttribute(temp, value);
/*     */             }
/*     */           }
/* 296 */           k = count[i] + 1;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void setDataXML() throws Exception {
/* 303 */     ResultSet resultSet = fetchResultSet(this.query);
/*     */ 
/* 307 */     if (!DbUtil.setResultSetToFirst(resultSet))
/* 308 */       return;
/* 309 */     ResultSetMetaData meta = resultSet.getMetaData();
/*     */     boolean simple;

/* 310 */     if (StringUtil.isEmpty(this.simpleSet))
/* 311 */       simple = meta.getColumnCount() < 3;
/*     */     else
/* 313 */       simple = StringUtil.getStringBool(this.simpleSet);
/* 314 */     if (simple)
/* 315 */       setSimpleXML(resultSet, meta);
/*     */     else
/* 317 */       setMultiXML(resultSet, meta);
/*     */   }
/*     */ 
/*     */   private void setMultiDataXML() throws Exception {
/* 321 */     String[] querys = StringUtil.split(this.query, ",");
/* 322 */     int len = querys.length;
/*     */     JSONArray properties;

/* 328 */     if (StringUtil.isEmpty(this.itemProperty))
/* 329 */       properties = null;
/*     */     else
/* 331 */       properties = new JSONArray("[" + this.itemProperty + "]");
/* 332 */     for (int i = 0; i < len; ++i)
/*     */     {
/*     */       Element element;

/* 333 */       if (i == 0)
/* 334 */         element = this.chart.addElement("categories");
/*     */       else
/* 336 */         element = this.chart.addElement("dataset");
/* 337 */       if (properties != null) {
/* 338 */         JSONObject jsonObj = properties.getJSONObject(i);
/* 339 */         String[] names = JSONObject.getNames(jsonObj);
/* 340 */         if (names != null)
/* 341 */           for (String name : names)
/* 342 */             element.addAttribute(name, jsonObj.getString(name));
/*     */       }
/* 344 */       ResultSet resultSet = fetchResultSet(querys[i]);
/* 345 */       if (!DbUtil.setResultSetToFirst(resultSet))
/* 346 */         return;
/* 347 */       if (i == 0)
/* 348 */         createSegment(element, "category", resultSet);
/*     */       else
/* 350 */         createSegment(element, "set", resultSet);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void createSegment(Element element, String key, ResultSet resultSet)
/*     */     throws SQLException
/*     */   {
/* 357 */     boolean first = true;
/*     */ 
/* 360 */     ResultSetMetaData meta = resultSet.getMetaData();
/*     */ 
/* 362 */     int cols = meta.getColumnCount();
/* 363 */     while ((first) || (resultSet.next())) {
/* 364 */       if (first)
/* 365 */         first = false;
/* 366 */       Element sub = element.addElement(key);
/* 367 */       for (int i = 1; i <= cols; ++i) {
/* 368 */         String value = getMapString(resultSet.getString(i), this.colPos + i);
/* 369 */         if (!StringUtil.isEmpty(value))
/* 370 */           sub.addAttribute(meta.getColumnLabel(i), value);
/*     */       }
/*     */     }
/* 373 */     this.colPos += cols;
/*     */   }
/*     */ 
/*     */   private void intiMaps(HttpServletRequest request) throws Exception {
/* 377 */     if (!StringUtil.isEmpty(this.emptyLabel)) {
/* 378 */       this.emptyHash = new HashMap();
/* 379 */       String[] emphs = this.emptyLabel.split(",");
/* 380 */       for (String s : emphs)
/* 381 */         this.emptyHash.put(Integer.valueOf(Integer.parseInt(StringUtil.getNamePart(s))), 
/* 382 */           StringUtil.getValuePart(s));
/*     */     }
/* 384 */     if (StringUtil.isEmpty(this.mapKey))
/* 385 */       return;
/* 386 */     String[] m = this.mapKey.split(",");
/* 387 */     int j = m.length;
/* 388 */     this.mapList = new Object[j];
/* 389 */     this.indexList = new Integer[j];
/*     */ 
/* 392 */     for (int i = 0; i < j; ++i) {
/* 393 */       String v = StringUtil.getValuePart(m[i]);
/* 394 */       Object o = request.getAttribute("keyb." + v);
/* 395 */       if (o == null) {
/* 396 */         Query query = new Query();
/* 397 */         query.setRequest(request);
/* 398 */         query.sql = 
/* 399 */           ("select KEY_ID,KEY_TEXT from WB_KEY where KEY_TYPE='" + 
/* 399 */           v + "' order by KEY_ID");
/* 400 */         query.jndi = ((String)request.getAttribute("sys.jndi"));
/* 401 */         query.setName("keyhs." + v);
/* 402 */         query.create();
/* 403 */         ResultSet rs = (ResultSet)request.getAttribute("keyhs." + v);
/* 404 */         HashMap map = new HashMap();
/* 405 */         while (rs.next()) {
/* 406 */           map.put(Integer.valueOf(rs.getInt(1)), rs.getString(2));
/*     */         }
/* 408 */         request.setAttribute("keyb." + v, map);
/* 409 */         o = map;
/*     */       }
/* 411 */       this.mapList[i] = o;
/* 412 */       this.indexList[i] = Integer.valueOf(StringUtil.getNamePart(m[i]));
/*     */     }
/*     */   }
/*     */ 
/*     */   private String getMapString(String value, int col)
/*     */   {
/* 418 */     if (StringUtil.isEmpty(value)) {
/* 419 */       if (this.emptyHash != null) {
/* 420 */         String s = (String)this.emptyHash.get(Integer.valueOf(col));
/* 421 */         if (s != null)
/* 422 */           return s;
/*     */       }
/* 424 */       return value;
/*     */     }
/* 426 */     if (this.mapList == null)
/* 427 */       return value;
/* 428 */     int j = this.indexList.length;
/* 429 */     for (int i = 0; i < j; ++i)
/* 430 */       if (col == this.indexList[i].intValue()) {
/* 431 */         if (StringUtil.isEmpty(value))
/* 432 */           return this.emptyLabel;
/* 433 */         String s = (String)((HashMap)this.mapList[i]).get(
/* 434 */           Integer.valueOf(Integer.parseInt(value)));
/* 435 */         if (s != null)
/* 436 */           return s;
/*     */       }
/* 438 */     return value;
/*     */   }
/*     */ }

/* Location:           Z:\EXT\WebBuilderServer (2)\WEB-INF\lib\webbuilder2.jar
 * Qualified Name:     com.webbuilder.controls.ChartContent
 * JD-Core Version:    0.5.4
 */