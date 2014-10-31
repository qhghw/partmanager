/*    */ package com.webbuilder.controls;
/*    */ 
/*    */ import com.webbuilder.utils.StringUtil;
/*    */ 
/*    */ public class ExtMenuItem extends ExtControl
/*    */ {
/*  6 */   public String handler = "";
/*  7 */   public String icon = "";
/*  8 */   public String iconCls = "";
/*  9 */   public String checked = "";
/* 10 */   public String ignoreParentClicks = "";
/* 11 */   public String minWidth = "";
/* 12 */   public String shadow = "";
/* 13 */   public String buttonType = "";
/* 14 */   public String action = "";
/* 15 */   public String menuType = "";
/* 16 */   public String group = "";
/* 17 */   public String enableToggle = "";
/* 18 */   public String onToggle = "";
/* 19 */   private boolean hasId = true;
/*    */ 
/*    */   protected void descript() throws Exception {
/* 22 */     setComma();
/* 23 */     if (StringUtil.isEqual(this.menuType, "date")) {
				
/* 24 */       setExtHeaderScript(this.comma + "new Ext.menu.DateItem({id:\"" + this.name + 
/* 25 */         "\",handler:function(control, date){" + this.handler + "}})");
/* 26 */     } else if (StringUtil.isEqual(this.menuType, "color")) {
/* 27 */       setExtHeaderScript(this.comma + "new Ext.menu.ColorItem({id:\"" + this.name + 
/* 28 */         "\",selectHandler:function(control, color){" + this.handler + 
/* 29 */         "}})");
/* 30 */     } else if ((StringUtil.isEqual(getCaption(), "-")) || 
/* 31 */       (StringUtil.isEqual(this.menuType, "separator"))) {
/* 32 */       setExtHeaderScript(this.comma + "\"-\"");
/* 33 */       this.hasId = false;
/* 34 */     } else if ((StringUtil.isEqual(getCaption(), "--")) || 
/* 35 */       (StringUtil.isEqual(this.menuType, "fill"))) {
/* 36 */       setExtHeaderScript(this.comma + "{xtype:\"tbfill\"}");
/* 37 */       this.hasId = false;
/* 38 */     } else if ((!StringUtil.isEmpty(this.action)) && (!this.hasExtChild)) {
/* 39 */       setExtHeaderScript(this.comma + this.action);
/* 40 */       this.hasId = false;
/* 41 */     } else if (!StringUtil.isEmpty(this.buttonType)) {
/* 42 */       setExpressString("id", this.buttonType);
/* 43 */       setExpressString("qtip", this.hint);
/* 44 */       setFunctionExpress("handler", this.handler, "");
/* 45 */       setExtHeaderScript(this.comma + "{" + this.express + "}");
/*    */     } else {
/* 47 */       setExpressString("id", this.name);
/* 48 */       setExpressString("text", getZnorEnCaption());
/* 49 */       if ((!StringUtil.isEqual(this.menuType, "subDate")) && 
/* 50 */         (!StringUtil.isEqual(this.menuType, "subColor")))
/* 51 */         setFunctionExpress("handler", this.handler, "control,event");
/* 52 */       setExpressString("icon", this.icon);
/* 53 */       if ((StringUtil.isEmpty(this.iconCls)) && (!StringUtil.isEmpty(this.icon)) && 
/* 54 */         (!StringUtil.isEqual(this.parentClassName, "extMenuItem")))
/* 55 */         this.iconCls = "bmenu";
/* 56 */       setExpressString("iconCls", this.iconCls);
/* 57 */       setExpress("checked", this.checked);
/* 58 */       setExpress("enableToggle", this.enableToggle);
/* 59 */       setExpress("ignoreParentClicks", this.ignoreParentClicks);
/* 60 */       setExpress("minWidth", this.minWidth);
				if(this.getCaption()!=null&&this.getCaption().equals("高级查询"))
					this.hidden="true";
/* 61 */       setExpress("hidden", this.hidden);
/* 62 */       setExpress("disabled", this.disabled);
/* 63 */       setExpressString("shadow", this.shadow);
/* 64 */       setExpressString("group", this.group);
/* 65 */       setExpressString("tooltip", this.hint);
/* 66 */       setEventItem("toggle", this.onToggle, "control,pressed");
/* 67 */       applyEventExpress();
/* 68 */       setTagExpress();
/* 69 */       if (this.hasExtChild) {
/* 70 */         setExpress("menu:{items", "[");
/* 71 */         if (StringUtil.isEqual(this.menuType, "menuButton")) {
/* 72 */           setExtHeaderScript(this.comma + "new Ext.Toolbar.MenuButton({" + 
/* 73 */             this.express);
/* 74 */           setExtFooterScript("]}})");
/*    */         } else {
/* 76 */           setExtHeaderScript(this.comma + "{" + this.express);
/* 77 */           setExtFooterScript("]}}");
/*    */         }
/*    */       }
/* 80 */       else if (StringUtil.isEqual(this.menuType, "subDate")) {
/* 81 */         setExtHeaderScript(this.comma + 
/* 82 */           "{" + 
/* 83 */           this.express + 
/* 84 */           ",menu:new Ext.menu.DateMenu({handler:function(control,date){" + 
/* 85 */           this.handler + "}})}");
/* 86 */       } else if (StringUtil.isEqual(this.menuType, "subColor")) {
/* 87 */         setExtHeaderScript(this.comma + 
/* 88 */           "{" + 
/* 89 */           this.express + 
/* 90 */           ",menu:new Ext.menu.ColorMenu({selectHandler:function(control,color){" + 
/* 91 */           this.handler + "}})}");
/* 92 */       } else if (StringUtil.isEqual(this.menuType, "menuButton")) {
/* 93 */         setExtHeaderScript(this.comma + "new Ext.Toolbar.MenuButton({" + 
/* 94 */           this.express + "})");
/*    */       } else {
/* 96 */         setExtHeaderScript(this.comma + "{" + this.express + "}");
/*    */       }
/*    */     }
/* 99 */     if (this.hasId) {
/* 100 */       setHeaderScript("var " + this.name + ";");
/* 101 */       setLastExtScript(this.name + "=Ext.getCmp(\"" + this.name + "\");");
/*    */     }
/*    */   }
/*    */ }

/* Location:           Z:\EXT\WebBuilderServer (1)\WEB-INF\lib\webbuilder2.jar
 * Qualified Name:     com.webbuilder.controls.ExtMenuItem
 * JD-Core Version:    0.6.0
 */