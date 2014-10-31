/*     */ package com.webbuilder.controls;
/*     */ 
/*     */ import com.webbuilder.utils.StringUtil;
/*     */ 
/*     */ public class ExtWindow extends ExtPanel
/*     */ {
/*   6 */   public String modal = "";
/*   7 */   public String iconCls = "";
/*   8 */   public String closable = "";
/*   9 */   public String resizable = "";
/*  10 */   public String minimizable = "";
/*  11 */   public String maximizable = "";
/*  12 */   public String plain = "";
/*  13 */   public String closeAction = "";
/*  14 */   public String defaultButton = "";
/*  15 */   public String minWidth = "";
/*  16 */   public String minHeight = "";
/*  17 */   public String onOk = "";
/*  18 */   public String onShow = "";
/*  19 */   public String onActivate = "";
/*  20 */   public String onDeactivate = "";
/*  21 */   public String onHide = "";
/*  22 */   public String onClose = "";
/*  23 */   public String enterOk = "";
/*  24 */   public String anchorCenter = "";
/*  25 */   public boolean dialog = true;
/*  26 */   public boolean autoShow = false;
/*     */   private String anchorScript;
/*     */ 
/*     */   protected void descript()
/*     */     throws Exception
/*     */   {
/*  33 */     if (this.dialog) {
/*  34 */       if (StringUtil.isEmpty(this.enterOk))
/*  35 */         this.enterOk = "true";
/*  36 */       if (StringUtil.isEmpty(this.onOk))
/*  37 */         this.onOk = (this.name + ".hide();");
/*  38 */       setHeaderScript("function " + this.name + "__ok(){" + this.onOk + "}");
/*  39 */       if (StringUtil.isEmpty(this.modal))
/*  40 */         this.modal = "true";
/*  41 */       if (StringUtil.isEmpty(this.resizable))
/*  42 */         this.resizable = "false";
/*  43 */       if (!StringUtil.isEmpty(this.buttons))
/*  44 */         this.buttons = ("," + this.buttons);
/*  45 */       this.buttons = 
/*  48 */         ("{text:\"确定\",iconCls:\"icon_ok\",handler:function(){" + 
/*  46 */         this.name + 
/*  47 */         "__ok();}},{text:\"取消\",iconCls:\"icon_cancel\",handler:function(){" + 
/*  48 */         this.name + ".hide();}}" + this.buttons);
/*     */     }
/*  50 */     boolean anchor = ((this.dialog) && (StringUtil.isEmpty(this.anchorCenter))) || 
/*  51 */       (StringUtil.getStringBool(this.anchorCenter));
/*     */     String adjScript;
/*     */    
/*  52 */     if (anchor) {
/*  53 */       this.anchorScript = 
/*  56 */         ("Ext.EventManager.onWindowResize(function(){var o=" + 
/*  54 */         this.name + 
/*  55 */         ";if(o.isVisible()){if(extAdjustWindow(o)){o.el.alignTo(document,\"c-c\");o.setPosition(o.el.getLeft(),o.el.getTop());}}}," + 
/*  56 */         this.name + ");");
/*  57 */       adjScript = "extAdjustWindow(" + this.name + ");";
/*     */     } else {
/*  59 */       this.anchorScript = "";
/*  60 */       adjScript = "";
/*     */     }
/*  62 */     setHeaderScript("var " + this.name + "__on=false;");
/*  63 */     if (StringUtil.getStringBool(this.enterOk))
/*  64 */       this.onShow = 
/*  74 */         (adjScript + 
/*  65 */         "if(!" + 
/*  66 */         this.name + 
/*  67 */         "__on){" + 
/*  68 */         this.name + 
/*  69 */         "__on=true;" + 
/*  70 */         this.anchorScript + 
/*  71 */         "Ext.get(\"" + 
/*  72 */         this.name + 
/*  73 */         "\").addKeyListener([13],function(k,e){if(document.activeElement&&document.activeElement.type==\"textarea\")return;window.event.keyCode=9;/*e.stopEvent();*/" + 
/*  74 */         this.name + "__ok();});}" + this.onShow);
/*  75 */     else if (anchor) {
/*  76 */       this.onShow = 
/*  77 */         (adjScript + "if(!" + this.name + "__on){" + this.name + "__on=true;" + 
/*  77 */         this.anchorScript + "}" + this.onShow);
/*     */     }
/*  79 */     if (this.autoShow)
/*  80 */       setLastExtScript(this.name + ".show();");
/*  81 */     super.descript();
/*     */   }
/*     */ 
/*     */   protected void addChildProperty() {
/*  85 */     setExpress("modal", this.modal);
/*  86 */     setExpressString("iconCls", this.iconCls);
/*  87 */     setExpress("closable", this.closable);
/*  88 */     setExpress("resizable", this.resizable);
/*  89 */     setExpress("minimizable", this.minimizable);
/*  90 */     setExpress("maximizable", this.maximizable);
/*  91 */     setExpress("plain", this.plain);
/*  92 */     setExpressString("closeAction", this.closeAction);
/*  93 */     setExpressAuto("defaultButton", this.defaultButton);
/*  94 */     setExpress("minWidth", this.minWidth);
/*  95 */     setExpress("minHeight", this.minHeight);
/*  96 */     setEventItem("show", this.onShow, "control");
/*  97 */     setEventItem("hide", this.onHide, "control");
/*  98 */     setEventItem("close", this.onClose, "control");
/*  99 */     setEventItem("activate", this.onActivate, "control");
/* 100 */     setEventItem("deactivate", this.onDeactivate, "control");
/*     */   }
/*     */ 
/*     */   protected String getObjectClass() {
/* 104 */     return "Ext.Window";
/*     */   }
/*     */ 
/*     */   protected String getObjectXType() {
/* 108 */     return "window";
/*     */   }
/*     */ }

/* Location:           Z:\EXT\WebBuilderServer (2)\WEB-INF\lib\webbuilder2.jar
 * Qualified Name:     com.webbuilder.controls.ExtWindow
 * JD-Core Version:    0.5.4
 */