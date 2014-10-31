/*    */ package com.webbuilder.controls;
/*    */ 
/*    */ import com.webbuilder.utils.StringUtil;
/*    */ 
/*    */ public class Excepter extends Control
/*    */ {
/*  6 */   public String nameList = "";
/*  7 */   public String valueList = "";
/*  8 */   public String message = "";
/*  9 */   public String condition = "";
/*    */ 
/*    */   protected void descript() throws Exception {
/* 12 */     if (StringUtil.isEmpty(this.nameList))
/* 13 */       return;
/* 14 */     String[] list = StringUtil.split(this.nameList, ","); String[] values = 
/* 15 */       StringUtil.split(this.valueList, ","); String[] cond = StringUtil.split(this.condition, 
/* 16 */       ",");
/* 17 */     int i = 0;
/*    */ 
/* 20 */     for (String t : list) {
/* 21 */       String s = fetchString(t);
/*    */       boolean canExcept;

/* 22 */       if ((StringUtil.isEmpty(cond[i])) || 
/* 23 */         (StringUtil.isSame(cond[i], "notExists"))) {
/* 24 */         canExcept = StringUtil.isEmpty(s);
/*    */       }
/*    */       else
/*    */       {

/* 25 */         if (StringUtil.isSame(cond[i], "exists")) {
/* 26 */           canExcept = !StringUtil.isEmpty(s);
/*    */         }
/*    */         else
/*    */         {

/* 27 */           if (StringUtil.isSame(cond[i], "=")) {
/* 28 */             canExcept = StringUtil.isEqual(s, values[i]);
/*    */           } else {
/* 30 */             if (StringUtil.isEmpty(s))
/* 31 */               s = "0";

/* 32 */             if (StringUtil.isSame(cond[i], ">")) {
/* 33 */               canExcept = Double.parseDouble(s) > 
/* 34 */                 Double.parseDouble(values[i]);
/*    */             }
/*    */             else
/*    */             {

/* 35 */               if (StringUtil.isSame(cond[i], "<")) {
/* 36 */                 canExcept = Double.parseDouble(s) < 
/* 37 */                   Double.parseDouble(values[i]);
/*    */               }
/*    */               else
/*    */               {

/* 38 */                 if (StringUtil.isSame(cond[i], "<>")) {
/* 39 */                   canExcept = !StringUtil.isSame(s, values[i]);
/*    */                 }
/*    */                 else
/*    */                 {

/* 40 */                   if (StringUtil.isSame(cond[i], ">=")) {
/* 41 */                     canExcept = Double.parseDouble(s) >= 
/* 42 */                       Double.parseDouble(values[i]);
/*    */                   }
/*    */                   else
/*    */                   {

/* 43 */                     if (StringUtil.isSame(cond[i], "<="))
/* 44 */                       canExcept = Double.parseDouble(s) <= 
/* 45 */                         Double.parseDouble(values[i]);
/*    */                     else
/* 47 */                       canExcept = false; 
/*    */                   }
/*    */                 }
/*    */               }
/*    */             }
/*    */           }
/*    */         }
/*    */       }
/* 49 */       ++i;
/* 50 */       if (canExcept) {
/* 51 */         if (StringUtil.isEmpty(this.message)) {
/* 52 */           throw new Exception("\"" + t + "\"不能为空。");
/*    */         }
/* 54 */         throw new Exception(this.message);
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           Z:\EXT\WebBuilderServer (2)\WEB-INF\lib\webbuilder2.jar
 * Qualified Name:     com.webbuilder.controls.Excepter
 * JD-Core Version:    0.5.4
 */