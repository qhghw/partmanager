/*     */ package com.webbuilder.controls;
/*     */ 
/*     */ import com.webbuilder.utils.StringUtil;
/*     */ 
/*     */ public class ExtStore extends ExtControl
/*     */ {
/*   6 */   public String url = "";
/*   7 */   public String baseParams = "";
/*   8 */   public String fields = "";
/*   9 */   public String autoLoad = "";
/*  10 */   public String format = "";
/*  11 */   public String record = "";
/*  12 */   public String total = "";
/*  13 */   public String id = "";
/*  14 */   public String timeout = "";
/*  15 */   public String remoteSort = "";
/*  16 */   public String waitTitle = "";
/*  17 */   public String waitMsg = "";
/*  18 */   public String mapGrid = "";
/*     */   public String showMessage;
/*  20 */   public String onBeforeLoad = "";
/*  21 */   public String onAfterLoad = "";
/*  22 */   public String onLoadException = "";
/*  23 */   public String onDataChanged = "";
/*  24 */   public String onAdd = "";
/*  25 */   public String onRemove = "";
/*  26 */   public String onUpdate = "";
/*     */ 
/*     */   protected void descript() throws Exception {
/*  29 */     String waitCode = "";
/*  30 */     boolean isShow = (StringUtil.isEmpty(this.showMessage)) || 
/*  31 */       (StringUtil.getStringBool(this.showMessage));
/*     */ 
/*  33 */     if (isShow) {
/*  34 */       if (StringUtil.isEmpty(this.waitTitle))
/*  35 */         this.waitTitle = "请稍等";
/*  36 */       if (StringUtil.isEmpty(this.waitMsg))
/*  37 */         this.waitMsg = "正在等待服务器响应...";
/*  38 */       this.onLoadException = 
/*  39 */         ("extShowExcept(response.responseText);" + 
/*  39 */         this.onLoadException);
/*     */     }
/*  41 */     if (StringUtil.isEmpty(this.record))
/*  42 */       this.record = "row";
/*  43 */     if (StringUtil.isEmpty(this.total))
/*  44 */       this.total = "total";
/*  45 */     if (StringUtil.isSame(this.format, "xml")) {
/*  46 */       setExpressString("totalRecords", this.total);
/*  47 */       setExpressString("record", this.record);
/*  48 */       setExpressString("id", this.id);
/*  49 */       setHeaderScript("var " + this.name + "__reader;");
/*  50 */       setFirstExtScript(this.name + "__reader=new Ext.data.XmlReader({" + 
/*  51 */         this.express + "},[" + this.fields + "]);");
/*  52 */       this.express.delete(0, this.express.length());
/*  53 */     } else if (StringUtil.isSame(this.format, "array")) {
/*  54 */       setHeaderScript("var " + this.name + "__reader;");
/*  55 */       setFirstExtScript(this.name + "__reader=new Ext.data.ArrayReader({},[" + 
/*  56 */         this.fields + "]);");
/*     */     } else {
/*  58 */       setExpressString("totalProperty", this.total);
/*  59 */       setExpressString("root", this.record);
/*  60 */       setExpressString("id", this.id);
/*  61 */       setHeaderScript("var " + this.name + "__reader;");
/*  62 */       setFirstExtScript(this.name + "__reader=new Ext.data.JsonReader({" + 
/*  63 */         this.express + "},[" + this.fields + "]);");
/*  64 */       this.express.delete(0, this.express.length());
/*     */     }
/*  66 */     if (!StringUtil.isEmpty(this.mapGrid)) {
/*  67 */       this.onBeforeLoad = 
/*  68 */         (this.name + ".removeAll();" + this.mapGrid + 
/*  68 */         ".getColumnModel().setConfig([{}]);" + this.onBeforeLoad);
/*  69 */       this.onAfterLoad = 
/*  71 */         (this.mapGrid + 
/*  70 */         ".getColumnModel().setConfig(control.reader.jsonData.colDefine);" + 
/*  71 */         this.onAfterLoad);
/*     */     }
/*  73 */     if (!StringUtil.isEmpty(this.waitMsg)) {
/*  74 */       if (!StringUtil.isEmpty(this.waitTitle))
/*  75 */         waitCode = "Ext.MessageBox.wait(\"" + 
/*  76 */           StringUtil.toExpress(this.waitMsg) + "\",\"" + 
/*  77 */           StringUtil.toExpress(this.waitTitle) + "\");";
/*     */       else
/*  79 */         waitCode = "Ext.MessageBox.wait(\"" + 
/*  80 */           StringUtil.toExpress(this.waitMsg) + "\");";
/*  81 */       this.onBeforeLoad = (waitCode + this.onBeforeLoad);
/*  82 */       this.onAfterLoad = ("Ext.MessageBox.hide();" + this.onAfterLoad);
/*  83 */       if (!isShow)
/*  84 */         this.onLoadException = ("Ext.MessageBox.hide();" + this.onLoadException);
/*     */     }
/*  86 */     setExpress("remoteSort", this.remoteSort);
/*  87 */     if (StringUtil.getStringBool(this.remoteSort))
/*  88 */       this.onBeforeLoad = 
/*  90 */         ("{var s=" + this.name + 
/*  89 */         ";var o=s.getSortState();if(o!=null){s.baseParams.sort=" + 
/*  90 */         "o.field;s.baseParams.dir=o.direction;}}" + this.onBeforeLoad);
/*  91 */     if (!StringUtil.isEmpty(this.baseParams)) {
/*  92 */       if (StringUtil.isEqual(this.baseParams.substring(0, 1), "$"))
/*  93 */         setExpress("baseParams", this.baseParams.substring(1));
/*  94 */       else if (this.baseParams.indexOf(":") == -1)
/*  95 */         setExpress("baseParams", this.baseParams);
/*     */       else
/*  97 */         setStatement("baseParams", this.baseParams);
/*     */     }
/*  99 */     setEventItem("beforeload", this.onBeforeLoad, "control,options");
/* 100 */     setEventItem("load", this.onAfterLoad, "control,records,options");
/* 101 */     setEventItem("loadexception", this.onLoadException, 
/* 102 */       "event,options,response,error");
/* 103 */     setEventItem("datachanged", this.onDataChanged, "control");
/* 104 */     setEventItem("add", this.onAdd, "control,records,index");
/* 105 */     setEventItem("remove", this.onRemove, "control,record,index");
/* 106 */     setEventItem("update", this.onUpdate, "control,record,operation");
/* 107 */     applyEventExpress();
/* 108 */     setTagExpress();
/* 109 */     if (StringUtil.isEqual(this.timeout, "-1"))
/* 110 */       this.timeout = Integer.toString(2147483647);
/* 111 */     if (!StringUtil.isEmpty(this.timeout))
/* 112 */       this.timeout = (",timeout:" + this.timeout);
/* 113 */     setExpress("proxy", "new Ext.data.HttpProxy({url:\"" + this.url + 
/* 114 */       "\",method:\"POST\"" + this.timeout + "})");
/* 115 */     setExpress("reader", this.name + "__reader");
/* 116 */     setExpress("autoLoad", this.autoLoad);
/* 117 */     createExtStatement("Ext.data.Store");
/*     */   }
/*     */ }

/* Location:           Z:\EXT\WebBuilderServer (1)\WEB-INF\lib\webbuilder2.jar
 * Qualified Name:     com.webbuilder.controls.ExtStore
 * JD-Core Version:    0.6.0
 */