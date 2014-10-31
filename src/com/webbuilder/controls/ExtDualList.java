/*    */ package com.webbuilder.controls;
/*    */ 
/*    */ import com.webbuilder.utils.DbUtil;
/*    */ import com.webbuilder.utils.StringUtil;
/*    */ import java.sql.ResultSet;
/*    */ 
/*    */ public class ExtDualList extends ExtPanel
/*    */ {
/*  9 */   public String query = "";
/* 10 */   public String sorted = "";
/* 11 */   public String submitMode = "";
/*    */ 
/*    */   protected void descript() throws Exception {
/* 14 */     if (StringUtil.isEmpty(this.height))
/* 15 */       this.height = "150";
/* 16 */     if (StringUtil.isEmpty(this.sorted))
/* 17 */       this.sorted = "false";
/* 18 */     addDualBox();
/* 19 */     this.className = "extPanel";
/* 20 */     this.content = (this.name + "__list");
/* 21 */     if (!StringUtil.isEmpty(this.query)) {
/* 22 */       ResultSet rs = fetchResultSet(this.query);
/*    */ 
/* 24 */       int count = DbUtil.getFieldCount(rs);
/* 25 */       setHeaderScript(DbUtil.getHashArray(rs, this.name, count));
/*    */       String arrayName;

/* 26 */       if (count > 1)
/* 27 */         arrayName = this.name + "__value";
/*    */       else
/* 29 */         arrayName = "null";
/* 30 */       setHeaderScript("AddSimpleOptions(\"" + this.name + "__src\"," + this.name + 
/* 31 */         "__text," + arrayName + "," + this.sorted + ");");
/*    */     }
/* 33 */     setExpressString("submitMode", this.submitMode);
/* 34 */     super.descript();
/*    */   }
/*    */ }

/* Location:           Z:\EXT\WebBuilderServer (2)\WEB-INF\lib\webbuilder2.jar
 * Qualified Name:     com.webbuilder.controls.ExtDualList
 * JD-Core Version:    0.5.4
 */