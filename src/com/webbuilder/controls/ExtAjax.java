/*    */ package com.webbuilder.controls;
/*    */ 
/*    */ import com.webbuilder.utils.StringUtil;
/*    */ 
/*    */ public class ExtAjax extends ExtControl
/*    */ {
/*  6 */   public String url = "";
/*  7 */   public String params = "";
/*  8 */   public String out = "";
/*  9 */   public String in = "";
/* 10 */   public String form = "";
/* 11 */   public String method = "";
/* 12 */   public String isUpload = "";
/* 13 */   public String timeout = "";
/* 14 */   public String waitTitle = "";
/* 15 */   public String waitMsg = "";
/* 16 */   public String onSuccess = "";
/* 17 */   public String onFailure = "";
/*    */   public String showMessage;
/* 19 */   public boolean reset = false;
/*    */ 
/*    */   protected void descript() throws Exception {
/* 22 */     String isReset = "false";
/* 23 */     String waitCode = "";
/* 24 */     boolean isShow = (StringUtil.isEmpty(this.showMessage)) || 
/* 25 */       (StringUtil.getStringBool(this.showMessage));
/*    */ 
/* 27 */     if (isShow) {
/* 28 */       if (StringUtil.isEmpty(this.waitTitle))
/* 29 */         this.waitTitle = "请稍等";
/* 30 */       if (StringUtil.isEmpty(this.waitMsg))
/* 31 */         this.waitMsg = "正在等待服务器响应...";
/* 32 */       if (StringUtil.isEmpty(this.showMessage))
/* 33 */         this.onSuccess = ("Ext.MessageBox.hide();" + this.onSuccess);
/*    */       else
/* 35 */         this.onSuccess = ("extShowMessage(\"操作已经完成。\");" + this.onSuccess);
/* 36 */       //this.onFailure = ("extShowExcept(response.responseText);" + this.onFailure);
/*    */     }
/* 38 */     if (this.reset)
/* 39 */       isReset = "true";
/* 40 */     if (!StringUtil.isEmpty(this.params)) {
/* 41 */       if (StringUtil.isEqual(this.params.substring(0, 1), "$"))
/* 42 */         setExpress("params", this.params.substring(1));
/* 43 */       else if (this.params.indexOf(":") == -1)
/* 44 */         setExpress("params", this.params);
/*    */       else
/* 46 */         setStatement("params", this.params);
/* 47 */     } else if (!StringUtil.isEmpty(this.out))
/* 48 */       setExpress("params", "extGetAllControlsValue(\"" + this.out + "\")");
/* 49 */     if (StringUtil.isEmpty(this.method))
/* 50 */       this.method = "POST";
/* 51 */     setExpressString("method", this.method);
/* 52 */     setExpress("isUpload", this.isUpload);
/* 53 */     if (StringUtil.isEqual(this.timeout, "-1"))
/* 54 */       setExpress("timeout", Integer.toString(2147483647));
/*    */     else
/* 56 */       setExpress("timeout", this.timeout);
/* 57 */     if (!StringUtil.isEmpty(this.in)) {
/* 58 */       this.onSuccess = 
/* 59 */         ("extSetAllControlsValue(\"" + this.in + 
/* 59 */         "\",response.responseText," + isReset + ");" + this.onSuccess);
/*    */     }
/* 61 */     if (!StringUtil.isEmpty(this.form)) {
/* 62 */       if (this.form.substring(0, 1).equals("!"))
/* 63 */         setExpress("form", this.form.substring(1));
/*    */       else
/* 65 */         setExpress("form", this.form + ".getForm().id");
/*    */     }
/* 67 */     if (!StringUtil.isEmpty(this.waitMsg)) {
/* 68 */       if (!StringUtil.isEmpty(this.waitTitle))
/* 69 */         waitCode = "Ext.MessageBox.wait(\"" + 
/* 70 */           StringUtil.toExpress(this.waitMsg) + "\",\"" + 
/* 71 */           StringUtil.toExpress(this.waitTitle) + "\");";
/*    */       else
/* 73 */         waitCode = "Ext.MessageBox.wait(\"" + 
/* 74 */           StringUtil.toExpress(this.waitMsg) + "\");";
/* 75 */       if (!isShow) {
/* 76 */         this.onSuccess = ("Ext.MessageBox.hide();" + this.onSuccess);
/* 77 */        // this.onFailure = ("Ext.MessageBox.hide();" + this.onFailure);
/*    */       }
/*    */     }
/* 80 */     if (StringUtil.getStringBool(this.isUpload)) {
/* 81 */       setFunctionExpress("success", "if (extCheckResponse(response)){" + 
/* 82 */         this.onSuccess + "}else{" + this.onFailure + "}", 
/* 83 */         "response,options");
/*    */     } else {
/* 85 */       setFunctionExpress("success", this.onSuccess, "response,options");
/* 86 */       setFunctionExpress("failure", this.onFailure, "response,options");
/*    */     }
/* 88 */     setHeaderScript("function " + this.name + "(){" + waitCode + "if(" + this.name + 
/* 89 */       ".url==null)" + this.name + ".url=\"" + this.url + 
/* 90 */       "\";Ext.Ajax.request({url:" + this.name + ".url," + this.express + 
/* 91 */       "});}");
/*    */   }
/*    */ }

/* Location:           Z:\EXT\webbuilder2.jar
 * Qualified Name:     com.webbuilder.controls.ExtAjax
 * JD-Core Version:    0.6.0
 */