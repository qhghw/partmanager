/*    */ package com.webbuilder.controls;
/*    */ 
/*    */ import com.sun.org.apache.bcel.internal.generic.RETURN;
import com.webbuilder.utils.StringUtil;
/*    */ 
/*    */ public class ExtDate extends ExtTextField
/*    */ {
/*  6 */   public String disabledDates = "";
/*  7 */   public String disabledDatesText = "";
/*  8 */   public String disabledDays = "";
/*  9 */   public String disabledDaysText = "";
/* 10 */   public String showToday = "";
/* 11 */   public String format = "";
/* 12 */   public String startDate = "";
/* 13 */   public String endDate = "";
/* 14 */   public String timeControl = "";
/* 15 */   public String onSelect = "";
/*    */ 
/*    */   protected void addChildProperty() throws Exception {
/* 18 */     if ((!StringUtil.isEmpty(this.startDate)) || (!StringUtil.isEmpty(this.endDate))) {
/* 19 */       setExpressString("startDate", this.startDate);
/* 20 */       setExpressString("endDate", this.endDate);
/* 21 */       this.validator = 
/* 22 */         ("extValidDateBetween(" + this.name + ");" + this.validator + 
/* 22 */         "return true;");
/* 23 */       if (StringUtil.isEmpty(this.startDate))
/* 24 */         this.onChange = 
/* 25 */           ("if(newValue==\"\"){var c=Ext.getCmp(\"" + this.endDate + 
/* 25 */           "\");c.setMinValue(null);c.validate();}" + this.onChange);
/*    */       else
/* 27 */         this.onChange = 
/* 28 */           ("if(newValue==\"\"){var c=Ext.getCmp(\"" + this.startDate + 
/* 28 */           "\");c.setMaxValue(null);c.validate();}" + this.onChange);
/*    */     }
/* 30 */     super.addChildProperty();
/* 31 */     setClause("disabledDates", this.disabledDates);
/* 32 */     setExpressString("disabledDatesText", this.disabledDatesText);
/* 33 */     setClause("disabledDays", this.disabledDays);
/* 34 */     setExpressString("disabledDaysText", this.disabledDaysText);
/* 35 */     setExpressString("format", this.format);
/* 36 */     setExpress("showToday", this.showToday);
/* 37 */     setExpressString("timeControl", this.timeControl);
/* 38 */     setEventItem("select", this.onSelect, "control,date");
/*    */   }
/*    */ 
     protected String getObjectClass() {
	   /* if(this.name!=null&&!this.name.equals("")&&this.name.indexOf("HD")>0)
					return "Ext.boco.DatetimePicker";
				else */
/* 42 */     return  "Ext.wv.DatetimeField" ;// "Ext.form.DateField";
/*    */   }
/*    */ 
    protected String getObjectXType() {
	  /*  if(this.name!=null&&!this.name.equals("")&&this.name.indexOf("HD")>0)
				return  "bocodatetimefield";
				else */

/* 46 */ return "datefield";
/*    */   }
/*    */ }

/* Location:           Z:\EXT\webbuilder2.jar
 * Qualified Name:     com.webbuilder.controls.ExtDate
 * JD-Core Version:    0.6.0
 */