/*    */ package com.webbuilder.controls;
/*    */ 
/*    */ import com.webbuilder.utils.StringUtil;
/*    */ 
/*    */ public class ExtTreeList extends ExtPanel
/*    */ {
/*  6 */   public String query = "";
/*  7 */   public String sorted = "";
/*  8 */   public String treeWidth = "";
/*  9 */   public String submitMode = "";
/* 10 */   public String fieldCount = "";
/* 11 */   public String sourceType = "";
/* 12 */   public boolean showAddress = false;
/* 13 */   public boolean showTool = false;
/* 14 */   public boolean hasValue = false;
/* 15 */   public boolean expandAll = false;
/*    */ 
/*    */   protected void descript() throws Exception {
/* 18 */     if (StringUtil.isEmpty(this.sorted))
/* 19 */       this.sorted = "false";
/* 20 */     addDualBox();
/* 21 */     this.layout = "border";
/* 22 */     this.hasExtChild = true;
/* 23 */     this.className = "extPanel";
/* 24 */     if ((((this.showAddress) || (this.showTool))) && (StringUtil.isEmpty(getCaption())))
/* 25 */       setExpressString("title", "&nbsp;");
/* 26 */     setExpressString("submitMode", this.submitMode);
/* 27 */     super.descript();
/* 28 */     ExtTree tree = new ExtTree();
/* 29 */     tree.name = (this.name + "__tree");
/* 30 */     tree.setRequest(this.request);
/* 31 */     tree.query = this.query;
/* 32 */     tree.sourceType = this.sourceType;
/* 33 */     tree.region = "west";
/* 34 */     tree.split = "true";
/* 35 */     tree.expandAll = this.expandAll;
/* 36 */     if (StringUtil.isEmpty(this.treeWidth))
/* 37 */       this.treeWidth = "150";
/* 38 */     tree.width = this.treeWidth;
/* 39 */     tree.canPostValue = false;
/* 40 */     tree.isFirstExtChild = true;
/* 41 */     tree.showAddress = this.showAddress;
/* 42 */     tree.showTool = this.showTool;
/* 43 */     tree.parentClassName = "extPanel";
/* 44 */     tree.treeListMode = true;
/* 45 */     tree.treeListHasValue = this.hasValue;
/* 46 */     tree.fieldCount = this.fieldCount;
/* 47 */     tree.autoScroll = "true";
/* 48 */     tree.classTag = this.classTag;
/*    */     String valueList;

/* 50 */     if (this.hasValue)
/* 51 */       valueList = this.name + "__tree__value";
/*    */     else
/* 53 */       valueList = "null";
/* 54 */     if (this.showAddress)
/*    */     {
/*    */       String cap;

/* 57 */       if (!StringUtil.isEmpty(getCaption()))
/* 58 */         cap = getCaption();
/*    */       else
/* 60 */         cap = "";
/* 61 */       tree.onClick = 
/* 62 */         (this.name + ".setTitle(\"" + cap + 
/* 62 */         "\"+extGetTreeNodeDisplayPath(node));" + tree.onClick);
/*    */     }
/*    */     ExtTree tmp359_358 = tree; tmp359_358.onClick = 
/* 68 */       (tmp359_358.onClick + "ClearOptions(\"" + this.name + 
/* 65 */       "__src\");AddOptionsFromArray(\"" + this.name + "__src\",\"" + 
/* 66 */       this.name + "__dst\"," + this.name + "__tree__text," + valueList + 
/* 67 */       ",node.attributes.begin,extGetNodeEndAttribute(node)," + 
/* 68 */       this.sorted + ");");
/* 69 */     tree.create();
/* 70 */     setExtHeaderScript(tree.getExtHeaderScript());
/* 71 */     setExtFooterScript(tree.getExtFooterScript());
/* 72 */     setHeaderScript(tree.getHeaderScript());
/* 73 */     setFooterScript(tree.getFooterScript());
/* 74 */     setFirstExtScript(tree.getFirstExtScript());
/* 75 */     setLastExtScript(tree.getLastExtScript());
/* 76 */     ExtPanel panel = new ExtPanel();
/* 77 */     panel.name = (this.name + "__panel");
/* 78 */     panel.region = "center";
/* 79 */     panel.content = (this.name + "__list");
/* 80 */     panel.parentClassName = "extPanel";
/* 81 */     panel.create();
/* 82 */     setHeaderScript(panel.getHeaderScript());
/* 83 */     setExtHeaderScript(panel.getExtHeaderScript());
/*    */   }
/*    */ }

/* Location:           Z:\EXT\WebBuilderServer (2)\WEB-INF\lib\webbuilder2.jar
 * Qualified Name:     com.webbuilder.controls.ExtTreeList
 * JD-Core Version:    0.5.4
 */