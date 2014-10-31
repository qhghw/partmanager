/*    */ package com.webbuilder.controls;
/*    */ 
/*    */ public class ExtLabel extends ExtControl
/*    */ {
/*  4 */   public String html = "";
/*  5 */   public String align = "";
/*  6 */   public String focus = "";
/*  7 */   public String autoWidth = "";
/*    */ 
/*    */   protected void descript() throws Exception {
/* 10 */     setStyleOption("text-align", this.align==null||this.align.equals("")?"right":this.align);
/* 11 */     setComponentExpress();
/* 12 */     setExpressString("text", getZnorEnCaption());
/* 13 */     setExpressString("html", this.html);
/* 14 */     setExpressString("forId", this.focus);
/* 15 */     setExpress("autoWidth", this.autoWidth);
/* 16 */     createExtControl("Ext.form.Label", "label");
/*    */   }
/*    */ }

/* Location:           Z:\EXT\WebBuilderServer (2)\WEB-INF\lib\webbuilder2.jar
 * Qualified Name:     com.webbuilder.controls.ExtLabel
 * JD-Core Version:    0.5.4
 */