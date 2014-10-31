/*     */ package com.webbuilder.controls;
/*     */ 
/*     */ import java.sql.Connection;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.webbuilder.utils.Language;
import com.webbuilder.utils.StringUtil;
/*     */ 
/*     */ public abstract class Control
/*     */ {
/*  12 */   private StringBuilder headerScript = new StringBuilder();
/*  13 */   private StringBuilder footerScript = new StringBuilder();
/*  14 */   private StringBuilder extHeaderScript = new StringBuilder();
/*  15 */   private StringBuilder extFooterScript = new StringBuilder();
/*  16 */   private StringBuilder firstExtScript = new StringBuilder();
/*  17 */   private StringBuilder lastExtScript = new StringBuilder();
/*  18 */   private String caption = "";
/*  19 */   private String value = "";
/*  20 */   protected StringBuilder header = new StringBuilder();
/*  21 */   protected StringBuilder footer = new StringBuilder();
/*     */   protected String hint;
/*  23 */   protected String classTag = "";
/*  24 */   protected String tag = "";
/*  25 */   protected StringBuilder property = new StringBuilder();
/*  26 */   protected StringBuilder express = new StringBuilder();
/*  27 */   protected String color = "";
/*  28 */   protected String bgColor = "";
/*  29 */   protected String bgImage = "";
/*  30 */   protected String name = "";
/*  31 */   protected String width = "";
/*  32 */   protected String height = "";
/*  33 */   protected String margin = "";
/*  34 */   protected String padding = "";
/*  35 */   protected String style = "";
/*     */   protected HttpServletRequest request;
/*     */   protected HttpServletResponse response;
/*     */ 
/*     */   protected abstract void descript()
/*     */     throws Exception;
/*     */ 
/*     */   public void create()
/*     */     throws Exception
/*     */   {
/*  42 */     descript();
/*     */   }
/*     */ 
/*     */   public void setName(String value) {
/*  46 */     this.name = value;
/*     */   }
/*     */ 
			public String getZnorEnCaption() {
				Object o=this.request.getSession().getAttribute("sys.username");

				String user=o==null?"admin":o.toString();
				
				if(user.equals("admin"))
				return Language.znMap.get(this.caption)==null?StringUtil.toExpress(this.caption):Language.znMap.get(this.caption);
				else if(user.equals("t1"))
				{
					Language.getEnClient();
					return Language.enMap.get(this.caption)==null?StringUtil.toExpress(this.caption):Language.enMap.get(this.caption);
				}
				else
				return StringUtil.toExpress(this.caption);
				
			}
/*     */   public String getCaption() {
/*  50 */     return StringUtil.toExpress(this.caption);
/*     */   }
/*     */ 
/*     */   public String getOriginCaption() {
/*  54 */     return this.caption;
/*     */   }
/*     */ 
/*     */   public void setCaption(String value) {
/*  58 */     this.caption = value;
/*     */   }
/*     */ 
/*     */   public String getValue() {
/*  62 */     return StringUtil.toExpress(this.value);
/*     */   }
/*     */ 
/*     */   public String getOriginValue() {
/*  66 */     return this.value;
/*     */   }
/*     */ 
/*     */   public void setValue(String value) {
/*  70 */     this.value = value;
/*     */   }
/*     */ 
/*     */   public void setHint(String value) {
/*  74 */     this.hint = value;
/*     */   }
/*     */ 
/*     */   public void setColor(String value) {
/*  78 */     this.color = value;
/*     */   }
/*     */ 
/*     */   public void setBgColor(String value) {
/*  82 */     this.bgColor = value;
/*     */   }
/*     */ 
/*     */   public void setBgImage(String value) {
/*  86 */     this.bgImage = value;
/*     */   }
/*     */ 
/*     */   public void setTag(String value) {
/*  90 */     this.tag = value;
/*     */   }
/*     */ 
/*     */   public void setClassTag(String value) {
/*  94 */     this.classTag = value;
/*     */   }
/*     */ 
/*     */   public void setMargin(String value) {
/*  98 */     this.margin = value;
/*     */   }
/*     */ 
/*     */   public void setPadding(String value) {
/* 102 */     this.padding = value;
/*     */   }
/*     */ 
/*     */   public void setWidth(String value) {
/* 106 */     this.width = value;
/*     */   }
/*     */ 
/*     */   public void setHeight(String value) {
/* 110 */     this.height = value;
/*     */   }
/*     */ 
/*     */   public String getHeader() {
/* 114 */     return this.header.toString();
/*     */   }
/*     */ 
/*     */   public void setHeader(String script) {
/* 118 */     if (!StringUtil.isEmpty(script)) {
/* 119 */       if (this.header.length() > 0)
/* 120 */         this.header.append("\r\n");
/* 121 */       this.header.append(script);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setHeaderProperty(String name) {
/* 126 */     if (this.property.length() == 0)
/* 127 */       setHeader("<" + name + ">");
/*     */     else
/* 129 */       setHeader("<" + name + " " + this.property + ">");
/*     */   }
/*     */ 
/*     */   public String getFooter() {
/* 133 */     return this.footer.toString();
/*     */   }
/*     */ 
/*     */   public void setFooter(String script) {
/* 137 */     if (!StringUtil.isEmpty(script)) {
/* 138 */       if (this.footer.length() > 0)
/* 139 */         this.footer.insert(0, "\r\n");
/* 140 */       this.footer.insert(0, script);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getHeaderScript() {
/* 145 */     return this.headerScript.toString();
/*     */   }
/*     */ 
/*     */   public String getFooterScript() {
/* 149 */     return this.footerScript.toString();
/*     */   }
/*     */ 
/*     */   public void setHeaderScript(String script) {
/* 153 */     if (!StringUtil.isEmpty(script)) {
/* 154 */       if (this.headerScript.length() > 0)
/* 155 */         this.headerScript.append("\r\n");
/* 156 */       this.headerScript.append(script);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setFooterScript(String script) {
/* 161 */     if (!StringUtil.isEmpty(script)) {
/* 162 */       if (this.footerScript.length() > 0)
/* 163 */         this.footerScript.insert(0, "\r\n");
/* 164 */       this.footerScript.insert(0, script);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getExtHeaderScript() {
/* 169 */     return this.extHeaderScript.toString();
/*     */   }
/*     */ 
/*     */   public String getExtFooterScript() {
/* 173 */     return this.extFooterScript.toString();
/*     */   }
/*     */ 
/*     */   public void setFirstExtScript(String script) {
/* 177 */     if (!StringUtil.isEmpty(script)) {
/* 178 */       if (this.firstExtScript.length() > 0)
/* 179 */         this.firstExtScript.append("\r\n");
/* 180 */       this.firstExtScript.append(script);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getFirstExtScript() {
/* 185 */     return this.firstExtScript.toString();
/*     */   }
/*     */ 
/*     */   public void setLastExtScript(String script) {
/* 189 */     if (!StringUtil.isEmpty(script)) {
/* 190 */       if (this.lastExtScript.length() > 0)
/* 191 */         this.lastExtScript.insert(0, "\r\n");
/* 192 */       this.lastExtScript.insert(0, script);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getLastExtScript() {
/* 197 */     return this.lastExtScript.toString();
/*     */   }
/*     */ 
/*     */   public void setExtHeaderScript(String script) {
/* 201 */     if (!StringUtil.isEmpty(script)) {
/* 202 */       if (this.extHeaderScript.length() > 0)
/* 203 */         this.extHeaderScript.append("\r\n");
/* 204 */       this.extHeaderScript.append(script);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setExtFooterScript(String script) {
/* 209 */     if (!StringUtil.isEmpty(script)) {
/* 210 */       if (this.extFooterScript.length() > 0)
/* 211 */         this.extFooterScript.insert(0, "\r\n");
/* 212 */       this.extFooterScript.insert(0, script);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setRequest(HttpServletRequest request) {
/* 217 */     this.request = request;
/*     */   }
/*     */ 
/*     */   public void setResponse(HttpServletResponse response) {
/* 221 */     this.response = response;
/*     */   }
/*     */ 
/*     */   public void setStyle(String value) {
/* 225 */     this.style = value;
/*     */   }
/*     */ 
/*     */   private void setOptionStyles() {
/* 229 */     setStyleOption("width", addValueUnit(this.width));
/* 230 */     setStyleOption("height", addValueUnit(this.height));
/* 231 */     setStyleOption("margin", this.margin);
/* 232 */     setStyleOption("padding", this.padding);
/* 233 */     setStyleOption("color", this.color);
/* 234 */     setStyleOption("background-color", this.bgColor);
/* 235 */     if (!StringUtil.isEmpty(this.bgImage))
/* 236 */       this.bgImage = ("url(" + this.bgImage + ")");
/* 237 */     setStyleOption("background-image", this.bgImage);
/*     */   }
/*     */ 
/*     */   protected void setStyleOption(String name, String value) {
/* 241 */     if (!StringUtil.isEmpty(value))
/* 242 */       if (StringUtil.isEmpty(this.style))
/* 243 */         this.style = (this.style + name + ":" + value);
/*     */       else
/* 245 */         this.style = (this.style + ";" + name + ":" + value);
/*     */   }
/*     */ 
/*     */   protected ResultSet fetchResultSet(String name) {
/* 249 */     if (StringUtil.isEmpty(name))
/* 250 */       return null;
/* 251 */     Object obj = this.request.getAttribute(name);
/*     */ 
/* 253 */     if ((obj == null) || (!(obj instanceof ResultSet))) {
/* 254 */       return null;
/*     */     }
/* 256 */     return (ResultSet)obj;
/*     */   }
/*     */ 
/*     */   protected Connection fetchConnection(String name) {
/* 260 */     if (StringUtil.isEmpty(name))
/* 261 */       return null;
/* 262 */     Object obj = this.request.getAttribute(name);
/*     */ 
/* 264 */     if ((obj == null) || (!(obj instanceof Connection))) {
/* 265 */       return null;
/*     */     }
/* 267 */     return (Connection)obj;
/*     */   }
/*     */ 
/*     */   protected String fetchString(String name) {
/* 271 */     return StringUtil.fetchString(this.request, name);
/*     */   }
/*     */ 
/*     */   protected Object fetchObject(String name) {
/* 275 */     if (StringUtil.isEmpty(name))
/* 276 */       return null;
/* 277 */     Object obj = this.request.getAttribute(name);
/*     */ 
/* 279 */     if (obj == null) {
/* 280 */       return this.request.getParameter(name);
/*     */     }
/* 282 */     return obj;
/*     */   }
/*     */ 
/*     */   protected void setProperty(String name, String value) {
/* 286 */     if (StringUtil.isEmpty(value))
/* 287 */       return;
/* 288 */     if (this.property.length() > 0)
/* 289 */       this.property.append(" ");
/* 290 */     this.property.append(name);
/* 291 */     this.property.append("=\"");
/* 292 */     this.property.append(value);
/* 293 */     this.property.append("\"");
/*     */   }
/*     */ 
/*     */   protected void setExpress(String name, String value) {
/* 297 */     if (StringUtil.isEmpty(value))
/* 298 */       return;
/* 299 */     if (this.express.length() > 0)
/* 300 */       this.express.append(",");
/* 301 */     this.express.append(name);
/* 302 */     this.express.append(":");
/* 303 */     this.express.append(value);
/* 304 */     this.express.append("");
/*     */   }
/*     */ 
/*     */   protected void setStatement(String name, String value) {
/* 308 */     if (StringUtil.isEmpty(value))
/* 309 */       return;
/* 310 */     setExpress(name, "{" + value + "}");
/*     */   }
/*     */ 
/*     */   protected void setClause(String name, String value) {
/* 314 */     if (StringUtil.isEmpty(value))
/* 315 */       return;
/* 316 */     setExpress(name, "[" + value + "]");
/*     */   }
/*     */ 
/*     */   protected void setExpressString(String name, String value) {
/* 320 */     if (StringUtil.isEmpty(value))
/* 321 */       return;
/* 322 */     setExpress(name, "\"" + value + "\"");
/*     */   }
/*     */ 
/*     */   protected void setExpressAuto(String name, String value) {
/* 326 */     if (StringUtil.isNumeric(value, false))
/* 327 */       setExpress(name, value);
/*     */     else
/* 329 */       setExpressString(name, value);
/*     */   }
/*     */ 
/*     */   protected void setNumberExpress(String name, String value) {
/* 333 */     if (StringUtil.isEmpty(value))
/* 334 */       return;
/* 335 */     if (value.indexOf("%") != -1)
/* 336 */       setExpressString(name, value);
/*     */     else
/* 338 */       setExpress(name, value);
/*     */   }
/*     */ 
/*     */   protected void setNameProperty() {
/* 342 */     setProperty("name", this.name);
/*     */   }
/*     */ 
/*     */   protected void setIDProperty() {
/* 346 */     setProperty("id", this.name);
/*     */   }
/*     */ 
/*     */   protected void setValueProperty() {
/* 350 */     setProperty("value", getValue());
/*     */   }
/*     */ 
/*     */   protected void setStyleProperty() {
/* 354 */     setOptionStyles();
/* 355 */     setProperty("style", this.style);
/*     */   }
/*     */ 
/*     */   protected void setClassProperty() {
/* 359 */     setProperty("class", this.classTag);
/*     */   }
/*     */ 
/*     */   protected void setTagProperty() {
/* 363 */     if (StringUtil.isEmpty(this.tag))
/* 364 */       return;
/* 365 */     if (this.property.length() > 0) {
/* 366 */       this.property.append(" ");
/* 367 */       this.property.append(this.tag);
/*     */     } else {
/* 369 */       this.property.append(this.tag);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void setControlProperty() {
/* 373 */     setNameProperty();
/* 374 */     setIDProperty();
/* 375 */     setStyleProperty();
/* 376 */     setClassProperty();
/* 377 */     setTagProperty();
/*     */   }
/*     */ 
/*     */   protected void setComponentProperty() {
/* 381 */     setIDProperty();
/* 382 */     setStyleProperty();
/* 383 */     setClassProperty();
/* 384 */     setTagProperty();
/*     */   }
/*     */ 
/*     */   public String addValueUnit(String value) {
/* 388 */     if (StringUtil.isEmpty(value))
/* 389 */       return "";
/* 390 */     if ((value.indexOf("%") == -1) && 
/* 391 */       (value.toUpperCase().indexOf("PX") == -1)) {
/* 392 */       return value + "px";
/*     */     }
/* 394 */     return value;
/*     */   }
/*     */ }

/* Location:           Z:\EXT\webbuilder2.jar
 * Qualified Name:     com.webbuilder.controls.Control
 * JD-Core Version:    0.6.0
 */