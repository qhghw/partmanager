/*    */ package com.webbuilder.controls;
/*    */ 
/*    */ import com.webbuilder.utils.StringUtil;
/*    */ 
/*    */ public class ExtMenu extends ExtControl
/*    */ {
/*  6 */   public String autoShow = "";
/*  7 */   public boolean innerTool = false;
/*    */ 
/*    */   protected void descript() throws Exception {
/* 10 */     setComponentExpress();
/* 11 */     if (this.innerTool) {
/* 12 */       setHeaderScript("var " + this.name + ";");
/* 13 */       setExtHeaderScript(this.name + "=[");
/* 14 */       setExtFooterScript("];");
/* 15 */       return;
/* 16 */     }if ((!StringUtil.getStringBool(this.autoShow)) && 
/* 17 */       (!isExtContainer(this.parentClassName)))
/* 18 */       createExtStatement("Ext.Toolbar");
/*    */     else
/* 20 */       createExtControl("Ext.Toolbar", "toolbar");
/*    */   }
/*    */ }

/* Location:           Z:\EXT\WebBuilderServer (1)\WEB-INF\lib\webbuilder2.jar
 * Qualified Name:     com.webbuilder.controls.ExtMenu
 * JD-Core Version:    0.6.0
 */