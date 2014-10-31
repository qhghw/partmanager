/*    */ package com.webbuilder.controls;
/*    */ 
/*    */ import com.webbuilder.utils.StringUtil;
/*    */ 
/*    */ public class ExtComboTree extends ExtComboBox
/*    */ {
/*  6 */   public String fieldCount = "";
/*  7 */   public String treeWidth = "";
/*  8 */   public String treeHeight = "";
/*  9 */   public String sourceType = "";
/* 10 */   public String onClick = "";
/* 11 */   public String onDblClick = "";
/* 12 */   public String onCheckChange = "";
/* 13 */   public String onTextChange = "";
/* 14 */   public String onAppend = "";
/* 15 */   public String onRemove = "";
/* 16 */   public String onDragdrop = "";
/* 17 */   public String animate = "";
/* 18 */   public String showToolType = "";
/* 19 */   public boolean autoAddress = false;
/*    */ 
/*    */   protected void addChildProperty()
/*    */     throws Exception
/*    */   {
/* 24 */     this.loadStore = "false";
/* 25 */     String saveQuery = this.query;
/* 26 */     this.query = "";
/* 27 */     this.listWidth = this.treeWidth;
/* 28 */     this.store = "new Ext.data.SimpleStore({fields:[],data:[[]]})";
/* 29 */     this.mode = "local";
/* 30 */     this.editable = "false";
/* 31 */     setExpressString("tpl", "<div id=\\\"" + this.name + 
/* 32 */       "__treeRender\\\"></div>");
/* 33 */     setExpressString("selectedClass", "\\\"\\\"");
/* 34 */     setHeaderScript("var " + this.name + "__isRender=false;");
/* 35 */     this.onExpand = 
/* 37 */       ("if(!" + this.name + "__isRender){" + this.name + "__tree.render(\"" + 
/* 36 */       this.name + "__treeRender\");" + this.name + "__isRender=true;}" + 
/* 37 */       this.onExpand);
/* 38 */     super.addChildProperty();
/* 39 */     ExtTree tree = new ExtTree();
/* 40 */     tree.name = (this.name + "__tree");
/* 41 */     tree.setRequest(this.request);
/* 42 */     tree.query = saveQuery;
/* 43 */     if (this.autoAddress)
/* 44 */       tree.onClick = 
/* 46 */         ("if(node.isLeaf()){event.stopEvent();" + this.name + 
/* 45 */         ".setValue(node.getPath(\"text\").substring(2));" + this.name + 
/* 46 */         ".collapse();}");
/* 47 */     tree.fieldCount = this.fieldCount;
/* 48 */     tree.animate = this.animate;
/* 49 */     tree.sourceType = this.sourceType;
/* 50 */     tree.border = "false";
/* 51 */     tree.autoShow = false;
/* 52 */     tree.data = this.data;
/* 53 */     tree.autoScroll = "true";
/* 54 */     tree.canPostValue = false;
/* 55 */     if (!StringUtil.isEmpty(this.showToolType)) {
/* 56 */       if (StringUtil.isSame(this.showToolType, "all"))
/* 57 */         tree.submitMode = "checked";
/* 58 */       tree.showTool = true;
/*    */     }
/* 60 */     tree.onClick = this.onClick;
/* 61 */     tree.onDblClick = this.onDblClick;
/* 62 */     tree.onCheckChange = this.onCheckChange;
/* 63 */     tree.onTextChange = this.onTextChange;
/* 64 */     tree.onAppend = this.onAppend;
/* 65 */     tree.onRemove = this.onRemove;
/* 66 */     tree.onDragdrop = this.onDragdrop;
/* 67 */     tree.create();
/* 68 */     setHeaderScript(tree.getHeaderScript());
/* 69 */     setFirstExtScript(tree.getFirstExtScript());
/*    */   }
/*    */ }

/* Location:           Z:\EXT\WebBuilderServer (2)\WEB-INF\lib\webbuilder2.jar
 * Qualified Name:     com.webbuilder.controls.ExtComboTree
 * JD-Core Version:    0.5.4
 */