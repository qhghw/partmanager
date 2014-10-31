/*    */ package com.webbuilder.controls;
/*    */ 
/*    */ import com.webbuilder.utils.StringUtil;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ 
/*    */ public class DefaultValue extends Control
/*    */ {
/*  6 */   public String key = "";
/*  7 */   public boolean allowBlank = false;
/*  8 */   public boolean initialize = false;
/*    */ 
/*    */   protected void descript() throws Exception {
/* 11 */     Object obj = fetchObject(this.key);
/* 12 */     if ((obj != null) && (!this.initialize) && (((this.allowBlank) || 
/* 13 */       (!StringUtil.isEmpty((String)obj))))) return;
/* 14 */     this.request.setAttribute(this.key, getOriginValue());
/*    */   }
/*    */ }

/* Location:           Z:\EXT\WebBuilderServer (2)\WEB-INF\lib\webbuilder2.jar
 * Qualified Name:     com.webbuilder.controls.DefaultValue
 * JD-Core Version:    0.5.4
 */