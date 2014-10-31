/*     */ package com.webbuilder.controls;
/*     */ 
/*     */ import com.webbuilder.utils.DbUtil;
/*     */ import com.webbuilder.utils.StringUtil;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.ResultSetMetaData;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Stack;
/*     */ 
/*     */ public class ExtTree extends ExtPanel
/*     */ {
/*  12 */   public String singleExpand = "";
/*  13 */   public String lines = "";
/*  14 */   public String enableDD = "";
/*  15 */   public String useArrows = "";
/*  16 */   public String animate = "";
/*  17 */   public String data = "";
/*  18 */   public String query = "";
/*  19 */   public String onClick = "";
/*  20 */   public String onDblClick = "";
/*  21 */   public String onCheckChange = "";
/*  22 */   public String onTextChange = "";
/*  23 */   public String onAppend = "";
/*  24 */   public String onRemove = "";
/*  25 */   public String onDragdrop = "";
/*  26 */   public String onBeforeLoad = "";
/*  27 */   public String onAfterLoad = "";
/*  28 */   public String onLoadException = "";
/*  29 */   public String fieldCount = "";
/*  30 */   public String submitMode = "";
/*  31 */   public String submitDisable = "";
/*  32 */   public String remoteUrl = "";
/*  33 */   public String remoteParams = "";
/*  34 */   public String sourceType = "";
/*  35 */   public String showMessage = "";
/*  36 */   public boolean canEdit = false;
/*  37 */   public boolean expandAll = false;
/*  38 */   public boolean remoteTree = false;
/*  39 */   public boolean treeListMode = false;
/*  40 */   public boolean treeListHasValue = false;
/*  41 */   public boolean showTool = false;
/*  42 */   public boolean showAddress = false;
/*  43 */   public boolean canPostValue = true;
/*     */ 
/*     */   protected void descript() throws Exception {
/*  46 */     if (this.remoteTree) {
/*  47 */       if ((!StringUtil.isEmpty(this.query)) && (StringUtil.isEmpty(this.data))) {
/*  48 */         if (this.sourceType.equalsIgnoreCase("single"))
/*  49 */           setHeader(getSimpleString());
/*     */         else
/*  51 */           setHeader(getMultiString());
/*     */       } else {
/*  53 */         if (this.data.length() == 0)
/*  54 */           this.data = "[]";
/*  55 */         setHeader(this.data);
/*     */       }
/*  57 */       return;
/*     */     }
/*  59 */     if (!this.canPostValue)
/*  60 */       setExpress("canPostValue", "1");
/*  61 */     setExpress("useArrows", this.useArrows);
/*  62 */     setExpress("animate", this.animate);
/*  63 */     setExpress("enableDD", this.enableDD);
/*  64 */     setExpress("lines", this.lines);
/*  65 */     setExpress("rootVisible", "false");
/*  66 */     setExpress("singleExpand", this.singleExpand);
/*  67 */     if (this.canEdit) {
/*  68 */       setHeaderScript("var " + this.name + "__editor;");
/*  69 */       setLastExtScript(this.name + "__editor=new Ext.tree.TreeEditor(" + this.name + 
/*  70 */         ",{id:\"" + this.name + "__editor\",allowBlank:false});");
/*     */     }
/*  72 */     if ((this.showAddress) && (!this.treeListMode))
/*     */     {
/*     */       String cap;

/*  75 */       if (!StringUtil.isEmpty(getCaption())) {
/*  76 */         cap = getCaption();
/*     */       } else {
/*  78 */         setExpressString("title", "&nbsp;");
/*  79 */         cap = "";
/*     */       }
/*  81 */       this.onClick = 
/*  82 */         (this.name + ".setTitle(\"" + cap + 
/*  82 */         "\"+extGetTreeNodeDisplayPath(node));" + this.onClick);
/*     */     }
/*  84 */     if (this.expandAll)
/*  85 */       setLastExtScript(this.name + ".getRootNode().expand(true,false);");
/*  86 */     setEventItem("click", this.onClick, "node,event");
/*  87 */     setEventItem("dblclick", this.onDblClick, "node,event");
/*  88 */     setEventItem("checkchange", this.onCheckChange, "node,checked");
/*  89 */     setEventItem("textchange", this.onTextChange, "node,text,oldText");
/*  90 */     setEventItem("append", this.onAppend, "control,parent,node,refNode");
/*  91 */     setEventItem("remove", this.onRemove, "control,parent,node");
/*  92 */     setEventItem("dragdrop", this.onDragdrop, "control,node,dd,event");
/*  93 */     if ((!StringUtil.isEmpty(this.query)) && (StringUtil.isEmpty(this.data)))
/*  94 */       if (this.sourceType.equalsIgnoreCase("single"))
/*  95 */         this.data = getSimpleString();
/*     */       else
/*  97 */         this.data = getMultiString();
/*  98 */     setExpressString("submitMode", this.submitMode);
/*  99 */     setExpress("submitDisable", this.submitDisable);
/* 100 */     if (this.showTool)
/*     */     {
/*     */       String checkBtn;

/* 102 */       if ((this.submitMode != null) && (this.submitMode.indexOf("checked") > -1))
/* 103 */         checkBtn = "{id:\"plus\",qtip:\"选中所有子节点\",handler:function(){var n=extGetTreeSelectedNode(" + 
/* 105 */           this.name + 
/* 106 */           ");if (n == null) n=" + 
/* 107 */           this.name + 
/* 108 */           ".getRootNode();extToggleNodeCheck(n,true);}}," + 
/* 109 */           "{id:\"minus\",qtip:\"取消选中所有子节点\",handler:function(){" + 
/* 110 */           "var n=extGetTreeSelectedNode(" + this.name + 
/* 111 */           ");if (n == null) n=" + this.name + 
/* 112 */           ".getRootNode();extToggleNodeCheck(n,false);}}," + 
/* 113 */           "{id:\"pin\",qtip:\"选中所有节点\",handler:function(){" + 
/* 114 */           "var n=" + this.name + 
/* 115 */           ".getRootNode();extToggleNodeCheck(n,true);}}," + 
/* 116 */           "{id:\"unpin\",qtip:\"取消选中所有节点\",handler:function(){" + 
/* 117 */           "var n=" + this.name + 
/* 118 */           ".getRootNode();extToggleNodeCheck(n,false);}},";
/*     */       else
/* 120 */         checkBtn = "";
/* 121 */       if (StringUtil.isEmpty(getCaption()))
/* 122 */         setCaption("&nbsp;");
/* 123 */       setExpress("tools", StringUtil.replaceParameters(this.request, "[" + 
/* 124 */         checkBtn + 
/* 125 */         "{id:\"right\",qtip:\"全部展开子节点\",handler:function(){" + 
/* 126 */         "var n=extGetTreeSelectedNode(" + this.name + 
/* 127 */         ");if(n==null)n=" + this.name + 
/* 128 */         ".getRootNode();n.expand(true,false);}}," + 
/* 129 */         "{id:\"left\",qtip:\"全部收缩子节点\",handler:function(){" + 
/* 130 */         "var n=extGetTreeSelectedNode(" + this.name + 
/* 131 */         ");if(n==null)n=" + this.name + 
/* 132 */         ".getRootNode();n.collapse(true,false);}}," + 
/* 133 */         "{id:\"up\",qtip:\"全部展开节点\",handler:function(){" + 
/* 134 */         "var n=" + this.name + 
/* 135 */         ".getRootNode();n.expand(true,false);}}," + 
/* 136 */         "{id:\"down\",qtip:\"全部收缩节点\",handler:function(){" + 
/* 137 */         "var n=" + this.name + 
/* 138 */         ".getRootNode();n.collapse(true,false);}}]"));
/*     */     }
/* 140 */     if (!StringUtil.isEmpty(this.data))
/* 141 */       this.data = (",children:" + this.data);
/* 142 */     setExpress("root", "{id:\"" + this.name + "__root\",text:\"\"" + this.data + "}");
/* 143 */     if (!StringUtil.isEmpty(this.remoteUrl)) {
/* 144 */       String e = "";
/* 145 */       if (StringUtil.getStringBool(this.showMessage)) {
/* 146 */         this.onBeforeLoad = 
/* 147 */           ("Ext.MessageBox.wait(\"正在等待服务器响应...\",\"请稍等\");" + 
/* 147 */           this.onBeforeLoad);
/* 148 */         this.onAfterLoad = ("Ext.MessageBox.hide();" + this.onAfterLoad);
/*     */       }
/* 150 */       if (!StringUtil.isSame(this.showMessage, "false"))
/* 151 */         this.onLoadException = 
/* 152 */           ("extShowExcept(response.responseText);" + 
/* 152 */           this.onLoadException);
/*     */       String p;

/* 153 */       if (!StringUtil.isEmpty(this.remoteParams))
/* 154 */         p = ",baseParams:" + this.remoteParams;
/*     */       else
/* 156 */         p = "";
/* 157 */       if (!StringUtil.isEmpty(this.onBeforeLoad))
/* 158 */         e = "beforeload:function(control,node){" + this.onBeforeLoad + "}";
/* 159 */       if (!StringUtil.isEmpty(this.onAfterLoad)) {
/* 160 */         if (!StringUtil.isEmpty(e))
/* 161 */           e = e + ",";
/* 162 */         e = e + "load:function(control,node,response){" + this.onAfterLoad + 
/* 163 */           "}";
/*     */       }
/* 165 */       if (!StringUtil.isEmpty(this.onLoadException)) {
/* 166 */         if (!StringUtil.isEmpty(e))
/* 167 */           e = e + ",";
/* 168 */         e = e + "loadexception:function(control,node,response){" + 
/* 169 */           this.onLoadException + "}";
/*     */       }
/* 171 */       if (!StringUtil.isEmpty(e))
/* 172 */         e = ",listeners:{" + e + "}";
/* 173 */       setExpress("loader", "new Ext.tree.TreeLoader({url:\"" + this.remoteUrl + 
/* 174 */         "\"" + p + e + "})");
/*     */     }
/* 176 */     super.descript();
/*     */   }
/*     */ 
/*     */   private String getMultiString() throws SQLException {
/* 180 */     int count = 0; int priorLevel = 0;
/* 181 */     boolean first = true; boolean newItem = true; boolean canAddComma = false;
/* 182 */     StringBuilder express = new StringBuilder();
/* 183 */     StringBuilder treeListText = new StringBuilder();
/* 184 */     StringBuilder treeListValue = new StringBuilder();
/* 185 */     Stack ends = new Stack();
/* 186 */     ResultSet resultSet = fetchResultSet(this.query);
/* 187 */     ResultSetMetaData meta = resultSet.getMetaData();
/*     */ 
/* 189 */     if (!DbUtil.setResultSetToFirst(resultSet))
/* 190 */       return "[]";
/* 191 */     int fieldRealCount = meta.getColumnCount();
/* 192 */     express.append("[");
/* 193 */     int fieldCount = fieldRealCount;
/* 194 */     if (!StringUtil.isEmpty(this.fieldCount)) {
/* 195 */       int i = Integer.parseInt(this.fieldCount);
/* 196 */       if (i < fieldCount)
/* 197 */         fieldCount = i;
/*     */     }
/* 199 */     if ((!this.treeListMode) && (StringUtil.isEmpty(this.submitMode)))
/* 200 */       for (int i = fieldCount; i < fieldRealCount; ++i)
/* 201 */         if (StringUtil.isEqual(meta.getColumnLabel(i + 1), "checked")) {
/* 202 */           this.submitMode = "checkedSimple";
/* 203 */           break;
/*     */         }
/* 205 */     if ((this.treeListMode) && (StringUtil.isEmpty(this.fieldCount)))
/* 206 */       if (this.treeListHasValue)
/* 207 */         fieldCount = fieldRealCount - 2;
/*     */       else
/* 209 */         fieldCount = fieldRealCount - 1;
/*     */     int k;

/* 210 */     if (this.treeListMode)
/*     */     {

/* 211 */       if (this.treeListHasValue)
/* 212 */         k = fieldRealCount - 2;
/*     */       else
/* 214 */         k = fieldRealCount - 1;
/*     */     } else {
/* 216 */       k = fieldRealCount;
/* 217 */     }String[] priorRecord = new String[fieldRealCount];
/* 218 */     String[] currentRecord = new String[fieldRealCount];
/* 219 */     while ((first) || (resultSet.next())) {
/* 220 */       for (int i = 0; i < fieldRealCount; ++i)
/* 221 */         currentRecord[i] = resultSet.getString(i + 1);
/* 222 */       if (this.treeListMode) {
/* 223 */         if (!first) {
/* 224 */           treeListText.append(",");
/* 225 */           if (this.treeListHasValue)
/* 226 */             treeListValue.append(",");
/*     */         }
/* 228 */         treeListText.append("\"");
/* 229 */         treeListText.append(
/* 230 */           StringUtil.toExpress(currentRecord[(fieldRealCount - 1)]));
/* 231 */         treeListText.append("\"");
/* 232 */         if (this.treeListHasValue) {
/* 233 */           treeListValue.append("\"");
/* 234 */           treeListValue.append(
/* 235 */             StringUtil.toExpress(currentRecord[(fieldRealCount - 2)]));
/* 236 */           treeListValue.append("\"");
/*     */         }
/*     */       }
/* 239 */       for (int i = 0; i < fieldCount; ++i) {
/* 240 */         newItem = false;
/* 241 */         if (!first) {
/* 242 */           for (int j = 0; j <= i; ++j) {
/* 243 */             if (StringUtil.isEqual(priorRecord[j], 
/* 244 */               currentRecord[j])) continue;
/* 245 */             newItem = true;
/* 246 */             break;
/*     */           }
/*     */         }
/* 249 */         if ((first) || (newItem)) {
/* 250 */           if (i < priorLevel)
/* 251 */             for (int j = i; j < priorLevel; ++j)
/* 252 */               if (this.treeListMode) {
/* 253 */                 express.append((String)ends.pop());
/* 254 */                 express.append(",end:");
/* 255 */                 express.append(count - 1);
/* 256 */                 express.append("}");
/*     */               } else {
/* 258 */                 express.append((String)ends.pop());
/*     */               }
/* 259 */           if (canAddComma)
/* 260 */             express.append(",");
/*     */           else
/* 262 */             canAddComma = true;
/* 263 */           express.append("{text:\"");
/* 264 */           express.append(StringUtil.toExpress(currentRecord[i]));
/* 265 */           express.append("\"");
/* 266 */           if (i == fieldCount - 1) {
/* 267 */             if (this.treeListMode) {
/* 268 */               express.append(",begin:");
/* 269 */               express.append(count);
/*     */             }
/* 271 */             for (int j = fieldCount; j < k; ++j) {
/* 272 */               express.append(",");
/* 273 */               express.append(meta.getColumnLabel(j + 1));
/* 274 */               if ((StringUtil.isSame(currentRecord[j], "true")) || 
/* 275 */                 (StringUtil.isSame(currentRecord[j], 
/* 276 */                 "false"))) {
/* 277 */                 express.append(":");
/* 278 */                 express.append(
/* 279 */                   StringUtil.toExpress(currentRecord[j]));
/*     */               } else {
/* 281 */                 express.append(":\"");
/* 282 */                 express.append(
/* 283 */                   StringUtil.toExpress(currentRecord[j]));
/* 284 */                 express.append("\"");
/*     */               }
/*     */             }
/* 287 */             express.append(",leaf:true}");
/*     */           } else {
/* 289 */             if (this.treeListMode) {
/* 290 */               express.append(",begin:");
/* 291 */               express.append(count);
/*     */             }
/* 293 */             express.append(",children:[");
/* 294 */             if (this.treeListMode)
/* 295 */               ends.push("]");
/*     */             else
/* 297 */               ends.push("]}");
/* 298 */             canAddComma = false;
/*     */           }
/* 300 */           priorLevel = i;
/*     */         }
/*     */       }
/* 303 */       for (int i = 0; i < fieldRealCount; ++i)
/* 304 */         priorRecord[i] = currentRecord[i];
/* 305 */       if (first)
/* 306 */         first = false;
/* 307 */       ++count;
/*     */     }
/* 309 */     while (!ends.isEmpty())
/* 310 */       if (this.treeListMode) {
/* 311 */         express.append((String)ends.pop());
/* 312 */         express.append(",end:");
/* 313 */         express.append(count - 1);
/* 314 */         express.append("}");
/*     */       } else {
/* 316 */         express.append((String)ends.pop());
/*     */       }
/* 318 */     if (this.treeListMode) {
/* 319 */       if (treeListText.length() > 0) {
/* 320 */         setHeaderScript("var " + this.name + "__text;");
/* 321 */         setHeaderScript(this.name + "__text=new Array(" + 
/* 322 */           treeListText.toString() + ");");
/*     */       }
/* 324 */       if ((this.treeListHasValue) && (treeListValue.length() > 0)) {
/* 325 */         setHeaderScript("var " + this.name + "__value;");
/* 326 */         setHeaderScript(this.name + "__value=new Array(" + 
/* 327 */           treeListValue.toString() + ");");
/*     */       }
/*     */     }
/* 330 */     express.append("]");
/* 331 */     return express.toString();
/*     */   }
/*     */ 
/*     */   private String getSimpleString() throws SQLException {
/* 335 */     int count = 0; int priorLevel = 0;
/* 336 */     boolean first = true; boolean canAddComma = false;
/* 337 */     StringBuilder express = new StringBuilder();
/* 338 */     StringBuilder treeListText = new StringBuilder();
/* 339 */     StringBuilder treeListValue = new StringBuilder();
/* 340 */     Stack ends = new Stack();
/* 341 */     ResultSet resultSet = fetchResultSet(this.query);
/* 342 */     ResultSetMetaData meta = resultSet.getMetaData();
/*     */ 
/* 344 */     if (!DbUtil.setResultSetToFirst(resultSet))
/* 345 */       return "[]";
/* 346 */     express.append("[");
/* 347 */     int fieldRealCount = meta.getColumnCount();
/* 348 */     int fieldCount = 1;
/* 349 */     if ((!this.treeListMode) && (StringUtil.isEmpty(this.submitMode)))
/* 350 */       for (int i = fieldCount; i < fieldRealCount; ++i)
/* 351 */         if (StringUtil.isEqual(meta.getColumnLabel(i + 1), "checked")) {
/* 352 */           this.submitMode = "checkedSimple";
/* 353 */           break;
/*     */         }
/*     */     int k;

/* 355 */     if (this.treeListMode)
/*     */     {

/* 356 */       if (this.treeListHasValue)
/* 357 */         k = fieldRealCount - 2;
/*     */       else
/* 359 */         k = fieldRealCount - 1;
/*     */     } else {
/* 361 */       k = fieldRealCount;
/* 362 */     }String[] currentRecord = new String[fieldRealCount];
/*     */ 
/* 364 */     while ((first) || (resultSet.next())) {
/* 365 */       for (int i = 0; i < fieldRealCount; ++i)
/* 366 */         currentRecord[i] = resultSet.getString(i + 1);
/* 367 */       if (this.treeListMode) {
/* 368 */         if (!first) {
/* 369 */           treeListText.append(",");
/* 370 */           if (this.treeListHasValue)
/* 371 */             treeListValue.append(",");
/*     */         }
/* 373 */         treeListText.append("\"");
/* 374 */         treeListText.append(
/* 375 */           StringUtil.toExpress(currentRecord[(fieldRealCount - 1)]));
/* 376 */         treeListText.append("\"");
/* 377 */         if (this.treeListHasValue) {
/* 378 */           treeListValue.append("\"");
/* 379 */           treeListValue.append(
/* 380 */             StringUtil.toExpress(currentRecord[(fieldRealCount - 2)]));
/* 381 */           treeListValue.append("\"");
/*     */         }
/*     */       }
/* 384 */       String[] data = StringUtil.split(currentRecord[0], "/");
/* 385 */       int level = data.length;
/* 386 */       if (level <= priorLevel) {
/* 387 */         express.append(",leaf:true}");
/* 388 */         for (int j = level; j < priorLevel; ++j)
/* 389 */           if (this.treeListMode) {
/* 390 */             express.append((String)ends.pop());
/* 391 */             express.append(",end:");
/* 392 */             express.append(count - 1);
/* 393 */             express.append("}");
/*     */           } else {
/* 395 */             express.append((String)ends.pop());
/*     */           }
/* 396 */       } else if (!first) {
/* 397 */         express.append(",children:[");
/* 398 */         if (this.treeListMode)
/* 399 */           ends.push("]");
/*     */         else
/* 401 */           ends.push("]}");
/* 402 */         canAddComma = false;
/*     */       }
/* 404 */       if (canAddComma)
/* 405 */         express.append(",");
/*     */       else
/* 407 */         canAddComma = true;
/* 408 */       express.append("{text:\"");
/* 409 */       express.append(StringUtil.toExpress(data[(level - 1)]));
/* 410 */       express.append("\"");
/* 411 */       if (this.treeListMode) {
/* 412 */         express.append(",begin:");
/* 413 */         express.append(count);
/*     */       }
/* 415 */       for (int j = fieldCount; j < k; ++j) {
/* 416 */         express.append(",");
/* 417 */         express.append(meta.getColumnLabel(j + 1));
/* 418 */         if ((StringUtil.isSame(currentRecord[j], "true")) || 
/* 419 */           (StringUtil.isSame(currentRecord[j], "false"))) {
/* 420 */           express.append(":");
/* 421 */           express.append(StringUtil.toExpress(currentRecord[j]));
/*     */         } else {
/* 423 */           express.append(":\"");
/* 424 */           express.append(StringUtil.toExpress(currentRecord[j]));
/* 425 */           express.append("\"");
/*     */         }
/*     */       }
/* 428 */       priorLevel = level;
/* 429 */       if (first)
/* 430 */         first = false;
/* 431 */       ++count;
/*     */     }
/* 433 */     express.append(",leaf:true}");
/* 434 */     while (!ends.isEmpty())
/* 435 */       if (this.treeListMode) {
/* 436 */         express.append((String)ends.pop());
/* 437 */         express.append(",end:");
/* 438 */         express.append(count - 1);
/* 439 */         express.append("}");
/*     */       } else {
/* 441 */         express.append((String)ends.pop());
/*     */       }
/* 443 */     if (this.treeListMode) {
/* 444 */       if (treeListText.length() > 0) {
/* 445 */         setHeaderScript("var " + this.name + "__text;");
/* 446 */         setHeaderScript(this.name + "__text=new Array(" + 
/* 447 */           treeListText.toString() + ");");
/*     */       }
/* 449 */       if ((this.treeListHasValue) && (treeListValue.length() > 0)) {
/* 450 */         setHeaderScript("var " + this.name + "__value;");
/* 451 */         setHeaderScript(this.name + "__value=new Array(" + 
/* 452 */           treeListValue.toString() + ");");
/*     */       }
/*     */     }
/* 455 */     express.append("]");
/* 456 */     return express.toString();
/*     */   }
/*     */ 
/*     */   protected String getObjectClass() {
/* 460 */     return "Ext.tree.TreePanel";
/*     */   }
/*     */ 
/*     */   protected String getObjectXType() {
/* 464 */     return "treepanel";
/*     */   }
/*     */ }

/* Location:           Z:\EXT\WebBuilderServer (2)\WEB-INF\lib\webbuilder2.jar
 * Qualified Name:     com.webbuilder.controls.ExtTree
 * JD-Core Version:    0.5.4
 */