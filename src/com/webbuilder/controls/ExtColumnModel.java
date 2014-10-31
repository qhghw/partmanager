/*    */ package com.webbuilder.controls;
/*    */ 
/*    */ import com.webbuilder.utils.StringUtil;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ 
/*    */ public class ExtColumnModel extends ExtControl
/*    */ {
/*  6 */   public String columnsDefine = "";
/*  7 */   public boolean asComponent = false;
/*    */ 
/*    */   protected void descript() throws Exception {
/* 10 */     if (this.asComponent) {
/* 11 */       this.request.setAttribute("__cmname", this.name);
/* 12 */       this.request.setAttribute(this.name, this.columnsDefine);
/*    */     } else {
/* 14 */       setHeaderScript("var " + this.name + ";");
/* 15 */       setExtHeaderScript(this.name + "=new Ext.grid.ColumnModel([");
/* 16 */       if (!StringUtil.isEmpty(this.columnsDefine))
/* 17 */         setExtHeaderScript(this.columnsDefine);
/* 18 */       setExtFooterScript("]);");
/*    */     }
/*    */   }
/*    */ }

/* Location:           Z:\EXT\webbuilder2.jar
 * Qualified Name:     com.webbuilder.controls.ExtColumnModel
 * JD-Core Version:    0.6.0
 */