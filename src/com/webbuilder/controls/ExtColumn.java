/*     */ package com.webbuilder.controls;
/*     */ 
/*     */ import com.webbuilder.utils.DbUtil;
/*     */ import com.webbuilder.utils.StringUtil;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ 
/*     */ public class ExtColumn extends ExtControl
/*     */ {
/*   7 */   public String fieldName = "";
/*   8 */   public String editor = "";
/*   9 */   public String align = "";
/*  10 */   public String fixed = "";
/*  11 */   public String hideable = "";
/*  12 */   public String resizable = "";
/*  13 */   public String sortable = "";
/*  14 */   public String renderer = "";
/*  15 */   public String format = "";
/*  16 */   public String type = "";
/*  17 */   public String mapKey = "";
/*  18 */   public String emptyLabel = "";
/*  19 */   public boolean asComponent = false;
/*     */   private String cmName;
/*     */ 
/*     */   protected void descript()
/*     */     throws Exception
/*     */   {
/*  23 */     if (this.asComponent)
/*  24 */       this.cmName = ((String)this.request.getAttribute("__cmname"));
/*  25 */     setComma();
/*  26 */     if (StringUtil.isEqual(this.type, "rowNum"))
/*     */     {
/*  28 */       setExpressString("header", getCaption());
/*  29 */       setExpress("width", this.width);
/*     */       String exp;
/*     */
/*  30 */       if (StringUtil.isEmpty(this.express.toString()))
/*  31 */         exp = "";
/*     */       else
/*  33 */         exp = "{" + this.express.toString() + "}";
/*  34 */       if (this.asComponent)
/*  35 */         addAttribute(this.comma + "new Ext.grid.RowNumberer(" + exp + ")");
/*     */       else
/*  37 */         setExtHeaderScript(this.comma + "new Ext.grid.RowNumberer(" + exp + 
/*  38 */           ")");
/*     */     } else {
/*  40 */       setExpressString("id", this.name);
/*  41 */       setExpress("editor", this.editor);
/*  42 */       setExpressString("dataIndex", this.fieldName);
				String caption=getZnorEnCaption();
//				if(caption.indexOf("div")==-1)
//				{
//					caption="<div align='center'>"+getCaption()+"</div>";
//				}
/*  43 */       setExpressString("header", caption);
/*  44 */       setExpressString("align", this.align==null||this.align.equals("")?"left":this.align);
/*  45 */       setExpress("fixed", this.fixed==null||this.fixed.equals("")?"false":this.fixed);
/*  46 */       setExpress("hidden", this.hidden);
/*  47 */       setExpressString("format", this.format);
/*  48 */       if (StringUtil.isEmpty(this.renderer)) {
/*  49 */         if (StringUtil.isEqual(this.type, "datetime"))
/*  50 */           this.renderer = "if(value)return value.format('Y-n-j H:i');else return '';";
/*  51 */         else if (StringUtil.isEqual(this.type, "date"))
/*  52 */           this.renderer = "if(value)return value.format('Y-n-j');else return '';";
/*  53 */         else if (StringUtil.isEqual(this.type, "time"))
/*  54 */           this.renderer = "if(value)return value.format('H:i:s');else return '';";
/*     */       }
/*  56 */       if ((StringUtil.isEmpty(this.renderer)) && (!StringUtil.isEmpty(this.format))) {
/*  57 */         this.renderer = 
/*  62 */           ("if(IsEmpty(value))return '';else return '" + 
/*  58 */           getFormatHeader(this.format, 1) + 
/*  59 */           "'+FormatNumber(value,'" + 
/*  60 */           getFormatHeader(this.format, 2) + 
/*  61 */           "')+'" + 
/*  62 */           getFormatHeader(this.format, 3) + "';");
/*     */       }
/*  64 */       if (StringUtil.isEmpty(this.renderer)) {
/*  65 */         setExpressString("emptyLabel", this.emptyLabel);
/*  66 */         setExpressString("mapKey", this.mapKey);
/*  67 */         if ((!StringUtil.isEmpty(this.mapKey)) || 
/*  68 */           (!StringUtil.isEmpty(this.emptyLabel)))
/*  69 */           setMap();
/*     */       }
/*  71 */       setExpress("hideable", this.hideable);
/*  72 */       setExpress("resizable", this.resizable);
/*  73 */       if (StringUtil.isEmpty(this.sortable))
/*  74 */         this.sortable = "true";
/*  75 */       setExpress("sortable", this.sortable);
/*  76 */       setExpress("width", this.width);
/*  77 */       setExpressString("tooltip", this.hint);

/*  78 */       setTagExpress();
/*  79 */       setFunctionExpress("renderer", this.renderer, 
/*  80 */         "value,metadata,record,rowIndex,colIndex,store");
/*  81 */       if (this.asComponent)
/*  82 */         addAttribute(this.comma + "{" + this.express + "}");
/*     */       else
/*  84 */         setExtHeaderScript(this.comma + "{" + this.express + "}");
/*     */     }
/*     */   }
/*     */ 
/*     */   private void addAttribute(String value) {
/*  89 */     this.request.setAttribute(this.cmName, this.request.getAttribute(this.cmName) + value);
/*     */   }
/*     */ 
/*     */   private void setMap() throws Exception {
/*  93 */     if (StringUtil.isEmpty(this.mapKey)) {
/*  94 */       this.renderer = 
/*  95 */         ("if(IsEmpty(value))return \"" + this.emptyLabel + 
/*  95 */         "\";else return value;");
/*     */     } else {
/*  97 */       if (DbUtil.createKeyQuery(this.request, this.mapKey))
/*  98 */         setHeaderScript((String)this.request.getAttribute("keya." + this.mapKey));
/*     */       String other;
/*     */   
/* 100 */       if (StringUtil.isEmpty(this.emptyLabel))
/* 101 */         other = "value";
/*     */       else
/* 103 */         other = "\"" + this.emptyLabel + "\"";
/* 104 */       this.renderer = 
/* 107 */         ("if(!IsEmpty(value)){var v=GetArrayValue(" + 
/* 105 */         this.mapKey + 
/* 106 */         "__kdt,value);if(v!=null)return v;else return value;}else return " + 
/* 107 */         other + ";");
/*     */     }
/*     */   }
/*     */ 
/*     */   private String getFormatHeader(String express, int segment)
/*     */   {
/* 114 */     if (StringUtil.isEmpty(express))
/* 115 */       return "";
/* 116 */     int i1 = express.indexOf("0");
/* 117 */     int i2 = express.indexOf("#");
/* 118 */     if ((i1 == -1) || ((i2 != -1) && (i2 < i1)))
/* 119 */       i1 = i2;
/* 120 */     int l1 = express.lastIndexOf("0");
/* 121 */     int l2 = express.lastIndexOf("#");
/* 122 */     if (l2 > l1)
/* 123 */       l1 = l2;
/* 124 */     if (i1 == -1) {
/* 125 */       if (segment == 1) {
/* 126 */         return express;
/*     */       }
/* 128 */       return "";
/* 129 */     }if (segment == 1)
/* 130 */       return express.substring(0, i1);
/* 131 */     if (segment == 2) {
/* 132 */       return express.substring(i1, l1 + 1);
/*     */     }
/* 134 */     return express.substring(l1 + 1);
/*     */   }
/*     */ }

/* Location:           Z:\EXT\webbuilder2.jar
 * Qualified Name:     com.webbuilder.controls.ExtColumn
 * JD-Core Version:    0.6.0
 */