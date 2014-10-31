/*     */ package com.webbuilder.controls;
/*     */ 
/*     */ import com.webbuilder.utils.StringUtil;
/*     */ 
/*     */ public class ExtPanel extends ExtControl
/*     */ {
/*   6 */   public String collapsible = "";
/*   7 */   public String collapsed = "";
/*   8 */   public String titleCollapse = "";
/*   9 */   public String autoScroll = "";
/*  10 */   public String frame = "";
/*  11 */   public String floating = "";
/*  12 */   public String border = "";
/*  13 */   public String layout = "";
/*  14 */   public String region = "";
/*  15 */   public String split = "";
/*  16 */   public String minSize = "";
/*  17 */   public String maxSize = "";
/*  18 */   public String activeItem = "";
/*  19 */   public String buttons = "";
/*  20 */   public String tools = "";
/*  21 */   public String tbar = "";
/*  22 */   public String bbar = "";
/*  23 */   public String buttonAlign = "";
/*  24 */   public String minButtonWidth = "";
/*  25 */   public String hideMode = "";
/*  26 */   public String labelWidth = "";
/*  27 */   public String labelAlign = "";
/*  28 */   public String content = "";
/*  29 */   public String columns = "";
/*  30 */   public String layoutConfig = "";
/*  31 */   public String onResize = "";
/*  32 */   public boolean autoShow = true;
/*     */ 
/*     */   protected void descript() throws Exception {
/*  35 */     setComponentExpress();
/*  36 */     if ((!StringUtil.isEmpty(this.collapsible)) && 
/*  37 */       (StringUtil.isEmpty(getCaption())))
/*  38 */       setExpressString("title", " ");
/*     */     else
/*  40 */       setExpressString("title", getCaption());
/*  41 */     setExpressString("contentEl", this.content);
/*  42 */     setExpressString("layout", this.layout);
/*  43 */     if ((StringUtil.isSame(this.layout, "absolute")) || 
/*  44 */       (StringUtil.isSame(this.layout, "form")))
/*  45 */       setExpress("fixedLay", "true");
/*  46 */     setExpressString("region", this.region);
/*  47 */     setExpressString("hideMode", this.hideMode);
/*  48 */     setExpress("collapsible", this.collapsible);
/*  49 */     setExpress("collapsed", this.collapsed);
/*  50 */     setExpress("titleCollapse", this.titleCollapse);
/*  51 */     setExpress("frame", this.frame);
/*  52 */     setExpress("floating", this.floating);
/*  53 */     setExpress("border", this.border);
/*  54 */     setExpress("split", this.split);
/*  55 */     if ((StringUtil.isEmpty(this.width)) && ((
/*  56 */       (StringUtil.isEqual(this.region, "east")) || (StringUtil.isEqual(
/*  57 */       this.region, "west")))))
/*  58 */       setExpress("width", "180");
/*  59 */     if ((StringUtil.isEmpty(this.height)) && ((
/*  60 */       (StringUtil.isEqual(this.region, "north")) || (StringUtil.isEqual(
/*  61 */       this.region, "south")))))
/*  62 */       setExpress("height", "100");
/*  63 */     if (!StringUtil.isEmpty(this.columns)) {
/*  64 */       if (!StringUtil.isEmpty(this.layoutConfig))
/*  65 */         this.layoutConfig += ",";
/*  66 */       this.layoutConfig = (this.layoutConfig + "columns:" + this.columns);
/*     */     }
/*  68 */     setStatement("layoutConfig", this.layoutConfig);
/*  69 */     setExpress("minSize", this.minSize);
/*  70 */     setExpress("maxSize", this.maxSize);
/*  71 */     setExpressString("labelAlign", this.labelAlign);
/*  72 */     setExpress("labelWidth", this.labelWidth);
/*  73 */     setExpressAuto("activeItem", this.activeItem);
/*  74 */     setExpress("autoScroll", this.autoScroll);
/*  75 */     setClause("buttons", this.buttons);
/*  76 */     setExpress("minButtonWidth", this.minButtonWidth);
/*  77 */     setExpressString("buttonAlign", this.buttonAlign);
/*  78 */     setExpress("tools", this.tools);
/*  79 */     setExpress("tbar", this.tbar);
/*  80 */     setExpress("bbar", this.bbar);
/*  81 */     addChildProperty();
/*  82 */     setEventItem("resize", this.onResize, 
/*  83 */       "control,adjWidth,adjHeight,rawWidth,rawHeight");
/*  84 */     applyEventExpress();
/*  85 */     if (this.autoShow) {
/*  86 */       createExtControl(getObjectClass(), getObjectXType());
/*     */     } else {
/*  88 */       setHeaderScript("var " + this.name + ";");
/*  89 */       setFirstExtScript(this.name + "=new " + getObjectClass() + "({" + 
/*  90 */         this.express + "});");
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void addDualBox()
/*     */   {
/*     */     String cls;

/*  97 */     if (!StringUtil.isEmpty(this.classTag))
/*  98 */       cls = " class=\"" + this.classTag + "\"";
/*     */     else
/* 100 */       cls = "";
/* 101 */     setHeader("<div id=\"" + 
/* 102 */       this.name + 
/* 103 */       "__list\" class=\"x-hidden\"><table style=\"width:100%;height:100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"" + 
/* 104 */       cls + "><tr><td width=\"50%\">");
/* 105 */     setHeader("<select id=\"" + 
/* 106 */       this.name + 
/* 107 */       "__src\" name=\"" + 
/* 108 */       this.name + 
/* 109 */       "__src\" multiple class=\"sys_normal\" style=\"width:100%;height:" + 
/* 110 */       this.height + "px\" OnDblClick=\"MoveOptions('" + this.name + 
/* 111 */       "__src','" + this.name + "__dst',false)\"></select></td><td>");
/* 112 */     setHeader("<p><input type=\"button\" style=\"width:33px;height:22px;font-size:14px\" value=\"&gt;\" onClick=\"MoveOptions('" + 
/* 113 */       this.name + "__src','" + this.name + "__dst',false)\"></p><br>");
/* 114 */     setHeader("<p><input type=\"button\" style=\"width:33px;height:22px;font-size:14px\" value=\"&gt;&gt;\" onClick=\"MoveOptions('" + 
/* 115 */       this.name + "__src','" + this.name + "__dst',true)\"></p><br>");
/* 116 */     setHeader("<p><input type=\"button\" style=\"width:33px;height:22px;font-size:14px\" value=\"&lt;\" onClick=\"MoveOptions('" + 
/* 117 */       this.name + "__dst','" + this.name + "__src',false)\"></p><br>");
/* 118 */     setHeader("<p><input type=\"button\" style=\"width:33px;height:22px;font-size:14px\" value=\"&lt;&lt;\" onClick=\"MoveOptions('" + 
/* 119 */       this.name + 
/* 120 */       "__dst','" + 
/* 121 */       this.name + 
/* 122 */       "__src',true)\"></p></td><td width=\"50%\">");
/* 123 */     setHeader("<select id=\"" + 
/* 124 */       this.name + 
/* 125 */       "__dst\" name=\"" + 
/* 126 */       this.name + 
/* 127 */       "__dst\" multiple class=\"sys_normal\" style=\"width:100%;height:" + 
/* 128 */       this.height + "px\" OnDblClick=\"MoveOptions('" + this.name + 
/* 129 */       "__dst','" + this.name + 
/* 130 */       "__src',false)\"></select></td></tr></table></div>");
/*     */   }
/*     */ 
/*     */   protected void addChildProperty() throws Exception {
/*     */   }
/*     */ 
/*     */   protected String getObjectClass() {
/* 137 */     return "Ext.Panel";
/*     */   }
/*     */ 
/*     */   protected String getObjectXType() {
/* 141 */     return "panel";
/*     */   }
/*     */ }

/* Location:           Z:\EXT\WebBuilderServer (2)\WEB-INF\lib\webbuilder2.jar
 * Qualified Name:     com.webbuilder.controls.ExtPanel
 * JD-Core Version:    0.5.4
 */