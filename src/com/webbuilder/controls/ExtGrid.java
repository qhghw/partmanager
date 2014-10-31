/*     */ package com.webbuilder.controls;
/*     */ 
/*     */ import com.webbuilder.utils.DbUtil;
/*     */ import com.webbuilder.utils.StringUtil;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.ResultSetMetaData;
/*     */ 
/*     */ public class ExtGrid extends ExtPanel
/*     */ {
/*  10 */   public String store = "";
/*  11 */   public String data = "";
/*  12 */   public String query = "";
/*  13 */   public String fields = "";
/*  14 */   public String columnsModel = "";
/*  15 */   public String columnsDefine = "";
/*  16 */   public String disableSelection = "";
/*  17 */   public String enableColumnHide = "";
/*  18 */   public String enableColumnMove = "";
/*  19 */   public String enableColumnResize = "";
/*  20 */   public String enableHdMenu = "";
/*  21 */   public String hideHeaders = "";
/*  22 */   public String stripeRows = "";
/*  23 */   public String trackMouseOver = "";
/*  24 */   public String autoExpandColumn = "";
/*  25 */   public String pageSize = "";
/*  26 */   public String loadStore = "";
/*  27 */   public String autoEncode = "";
/*  28 */   public String clicksToEdit = "";
/*  29 */   public String submitMode = "";
/*  30 */   public String downloadMode = "";
/*  31 */   public String downloadFilename = "";
/*  32 */   public String downloadAll = "";
/*  33 */   public String onCellClick = "";
/*  34 */   public String onCellDblClick = "";
/*  35 */   public String onRowClick = "";
/*  36 */   public String onRowDblClick = "";
/*  37 */   public String onClick = "";
/*  38 */   public String onDoubleClick = "";
/*  39 */   public boolean dateAsString = true;
/*  40 */   public boolean canEdit = false;
/*     */ 
/*     */   protected void addChildProperty() throws Exception {
/*  43 */     String pageBar = ""; String storeName = ""; String toolExpress = "";
				this.downloadAll="true";
/*  44 */     if ((StringUtil.isEmpty(this.query)) && (StringUtil.isEmpty(this.columnsModel)) && 
/*  45 */       (StringUtil.isEmpty(this.columnsDefine)))
/*  46 */       this.columnsDefine = "{}";
/*  47 */     if ((StringUtil.isEmpty(this.downloadMode)) && (
/*  48 */       (!StringUtil.isEmpty(this.downloadFilename)) || 
/*  49 */       (!StringUtil.isEmpty(this.downloadAll))))
/*  50 */       this.downloadMode = "toolButton";
/*  51 */     if (!StringUtil.isEmpty(this.downloadMode)) {
/*  52 */       createDownload();
/*  53 */       if (StringUtil.isSame(this.downloadMode, "toolButton")) {
/*  54 */         if ((!StringUtil.isEmpty(this.pageSize)) && 
/*  55 */           (StringUtil.getStringBool(this.downloadAll)))
/*  56 */           toolExpress = ",items:[new Ext.Toolbar.MenuButton({handler:function(control){" + 
/*  57 */             this.name + 
/*  58 */             "__download(\"xls\",0);},iconCls:\"icon_excel\",tooltip:\"导出当前页至Excel\",menu:{items:[{handler:function(control){" + 
/*  59 */             this.name + 
/*  60 */             "__downloadAll(\"xls\",0);},iconCls:\"icon_excel\",text:\"导出所有页至Excel\"}"+//,{handler:function(control){" + 
/*  61 */          //   this.name + 
/*  62 */           //  "__download(\"txt\",0);},iconCls:\"icon_text\",text:\"导出当前页至文本\"},{handler:function(control){" + 
/*  63 */          //   this.name + 
/*  64 */           //  "__downloadAll(\"txt\",0);},iconCls:\"icon_text\",text:\"导出所有页至文本\"}
						"]}})]";
/*     */        // else {
/*  66 */        //   toolExpress = ",items:[new Ext.Toolbar.MenuButton({handler:function(control){" + 
/*  67 */         //    this.name + 
/*  68 */         //    "__download(\"xls\",0);},iconCls:\"icon_excel\",tooltip:\"导出至Excel\",menu:{items:[{handler:function(control){" + 
/*  69 */         //    this.name + 
/*  70 */          //   "__download(\"txt\",0);},iconCls:\"icon_text\",text:\"导出至文本\"}]}})]";
/*     */       //  }
/*     */       }
/*     */     }
/*  74 */     if (!StringUtil.isEmpty(this.store)) {
/*  75 */       setExpress("store", this.store);
/*  76 */       storeName = this.store;
/*     */     } else {
/*  78 */       ResultSet resultSet = fetchResultSet(this.query);
/*  79 */       ResultSetMetaData meta = null;
/*  80 */       storeName = this.name + "__store";
/*  81 */       setExpress("store", storeName);
/*  82 */       setHeaderScript("var " + this.name + "__data;");
/*  83 */       if (!StringUtil.isEmpty(this.data)) {
/*  84 */         setFirstExtScript(this.name + "__data=[" + this.data + "];");
/*  85 */       } else if (resultSet != null) {
/*  86 */         meta = resultSet.getMetaData();
/*  87 */         setFirstExtScript(this.name + "__data=" + 
/*  88 */           DbUtil.getDataArray(resultSet, meta, false) + ";");
/*     */       } else {
/*  90 */         setFirstExtScript(this.name + "__data=[];");
/*  91 */       }setHeaderScript("var " + storeName + ";");
/*  92 */       if ((resultSet != null) && (StringUtil.isEmpty(this.columnsModel)) && 
/*  93 */         (StringUtil.isEmpty(this.columnsDefine)))
/*  94 */         if (meta == null)
/*  95 */           this.columnsDefine = DbUtil.getResultSetMeta(resultSet, 
/*  96 */             this.dateAsString, false);
/*     */         else
/*  98 */           this.columnsDefine = DbUtil.getResultSetMeta(meta, this.dateAsString, 
/*  99 */             false);
/* 100 */       if ((resultSet != null) && (StringUtil.isEmpty(this.fields)))
/* 101 */         if (meta == null)
/* 102 */           this.fields = DbUtil.getFieldsJson(resultSet, this.dateAsString);
/*     */         else
/* 104 */           this.fields = DbUtil.getFieldsJson(meta, this.dateAsString);
/* 105 */       setFirstExtScript(storeName + 
/* 106 */         "=new Ext.data.Store({proxy:new Ext.data.PagingMemoryProxy(" + 
/* 107 */         this.name + "__data),reader:new Ext.data.ArrayReader({},[" + 
/* 108 */         this.fields + "])});");
/*     */     }
/* 110 */     if ((StringUtil.isEmpty(this.loadStore)) || 
/* 111 */       (StringUtil.getStringBool(this.loadStore)))
/* 112 */       if (StringUtil.isEmpty(this.pageSize))
/* 113 */         setLastExtScript(storeName + ".load();");
/*     */       else
/* 115 */         setLastExtScript(storeName + ".load({params:{start:0,limit:" + 
/* 116 */           this.pageSize + "}});");
/* 117 */     if (StringUtil.isEmpty(this.bbar))
/* 118 */       pageBar = "bbar";
/* 119 */     else if (StringUtil.isEmpty(this.tbar))
/* 120 */       pageBar = "tbar";
/* 121 */     if ((!StringUtil.isEmpty(this.pageSize)) && (!StringUtil.isEmpty(pageBar)))
/* 122 */       setExpress(pageBar, "new Ext.PagingToolbar({pageSize:" + this.pageSize + 
/* 123 */         ",store:" + storeName + ",displayInfo:true" + toolExpress + 
/* 124 */         "})");
/* 125 */     else if (!StringUtil.isEmpty(toolExpress))
/* 126 */       setExpress(pageBar, "new Ext.Toolbar({id:\"" + this.name + "__toolbar\"" + 
/* 127 */         toolExpress + "})");
/* 128 */     setExpress("cm", this.columnsModel);
/* 129 */     setClause("columns", this.columnsDefine);
/* 130 */     setExpressString("autoExpandColumn", this.autoExpandColumn);
/* 131 */     setExpress("autoEncode", this.autoEncode);
/* 132 */     setExpress("clicksToEdit", this.clicksToEdit);
/* 133 */     setExpress("disableSelection", this.disableSelection);
/* 134 */     setExpress("enableColumnHide", this.enableColumnHide);
/* 135 */     setExpress("enableColumnMove", this.enableColumnMove);
/* 136 */     setExpress("enableColumnResize", this.enableColumnResize);
/* 137 */     setExpress("enableHdMenu", this.enableHdMenu);
/* 138 */     setExpress("hideHeaders", this.hideHeaders);
/* 139 */     setExpress("stripeRows", this.stripeRows);
/* 140 */     setExpressString("submitMode", this.submitMode);
/* 141 */     if (this.submitMode.equals("modified"))
/* 142 */       setLastExtScript(storeName + 
/* 143 */         ".on(\"remove\",function(o,r,i){extSetDeleteRecords(o,r);});");
/* 144 */     setExpress("trackMouseOver", this.trackMouseOver);
/* 145 */     setEventItem("cellclick ", this.onCellClick, 
/* 146 */       "control,rowIndex,colIndex,event");
/* 147 */     setEventItem("celldblclick ", this.onCellDblClick, 
/* 148 */       "control,rowIndex,colIndex,event");
/* 149 */     setEventItem("rowclick ", this.onRowClick, "control,rowIndex,event");
/* 150 */     setEventItem("rowdblclick ", this.onRowDblClick, "control,rowIndex,event");
/* 151 */     setEventItem("click ", this.onClick, "event");
/* 152 */     setEventItem("dblclick ", this.onDoubleClick, "event");
/*     */   }
/*     */ 
/*     */   protected String getObjectClass() {
/* 156 */     if (this.canEdit) {
/* 157 */       return "Ext.grid.EditorGridPanel";
/*     */     }
/* 159 */     return "Ext.grid.GridPanel";
/*     */   }
/*     */ 
/*     */   protected String getObjectXType() {
/* 163 */     if (this.canEdit) {
/* 164 */       return "editorgrid";
/*     */     }
/* 166 */     return "grid";
/*     */   }
/*     */ 
/*     */   private void createDownload() {
/* 170 */     String formName = this.name + "__down";
/*     */ 
/* 172 */     setHeader("<form id=\"" + 
/* 173 */       this.name + 
/* 174 */       "__form\" method=\"post\" enctype=\"multipart/form-data\" action=\"main?action=webbuilder/system/download.xwl\">" + 
/* 175 */       "<input name=\"type\" type=\"hidden\">" + 
/* 176 */       "<input name=\"filename\" type=\"hidden\" >" + 
/* 177 */       "<input name=\"data\" type=\"hidden\">" + 
/* 178 */       "<input name=\"fields\" type=\"hidden\">" + 
/* 179 */       "<input name=\"mapper\" type=\"hidden\">" + 
/* 180 */       "<input name=\"sheet\" type=\"hidden\">" + 
/* 181 */       "<input name=\"border\" type=\"hidden\" value=\"0\"></form>");
/* 182 */     setHeaderScript("var " + formName + "=Get(\"" + this.name + "__form\");");
/* 183 */     setHeaderScript("function " + this.name + "__download(type){" + formName + 
/* 184 */       ".type.value=type;" + formName + ".filename.value=\"" + 
/* 185 */       this.downloadFilename + "\";" + formName + 
/* 186 */       ".fields.value=extGetGridFields(" + this.name + ");if(" + formName + 
/* 187 */       ".fields.value=='[]'){extShowWarning('导出数据为空。');return;}" + 
/* 188 */       formName + ".mapper.value=extGetColMap(" + this.name + ");" + 
/* 189 */       formName + ".data.value=extGetPageGridData(" + this.name + ");" + 
/* 190 */       formName + ".submit();}");
/* 191 */     if ((!StringUtil.isEmpty(this.pageSize)) && 
/* 192 */       (StringUtil.getStringBool(this.downloadAll))) {
/* 193 */       setHeaderScript("function " + this.name + "__onload(value){" + formName + 
/* 194 */         ".data.value=value;" + formName + ".submit();}");
/* 195 */       setHeaderScript("function " + this.name + "__downloadAll(type){" + 
/* 196 */         formName + ".type.value=type;" + formName + 
/* 197 */         ".filename.value=\"" + this.downloadFilename + "\";" + 
/* 198 */         formName + ".fields.value=extGetGridFields(" + this.name + 
/* 199 */         ");if(" + formName + 
/* 200 */         ".fields.value=='[]'){extShowWarning('导出数据为空。');return;}" + 
/* 201 */         formName + ".mapper.value=extGetColMap(" + this.name + 
/* 202 */         ");extGetAllGridData(" + this.name + "," + this.name + 
/* 203 */         "__onload);}");
/*     */     }
/*     */   }
/*     */ }

/* Location:           Z:\EXT\webbuilder2.jar
 * Qualified Name:     com.webbuilder.controls.ExtGrid
 * JD-Core Version:    0.6.0
 */