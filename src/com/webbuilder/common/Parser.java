/*      */ package com.webbuilder.common;
/*      */ 
/*      */ import com.webbuilder.controls.Body;
/*      */ import com.webbuilder.controls.Button;
/*      */ import com.webbuilder.controls.ChartContent;
/*      */ import com.webbuilder.controls.Control;
/*      */ import com.webbuilder.controls.DataProvider;
/*      */ import com.webbuilder.controls.DbUpdater;
/*      */ import com.webbuilder.controls.DefaultValue;
/*      */ import com.webbuilder.controls.Div;
/*      */ import com.webbuilder.controls.Edit;
/*      */ import com.webbuilder.controls.Excepter;
/*      */ import com.webbuilder.controls.ExtAction;
/*      */ import com.webbuilder.controls.ExtAjax;
/*      */ import com.webbuilder.controls.ExtButton;
/*      */ import com.webbuilder.controls.ExtChart;
/*      */ import com.webbuilder.controls.ExtCheckBox;
/*      */ import com.webbuilder.controls.ExtColumn;
/*      */ import com.webbuilder.controls.ExtColumnModel;
/*      */ import com.webbuilder.controls.ExtComboBox;
/*      */ import com.webbuilder.controls.ExtComboTree;
/*      */ import com.webbuilder.controls.ExtControl;
/*      */ import com.webbuilder.controls.ExtDate;
/*      */ import com.webbuilder.controls.ExtDualList;
/*      */ import com.webbuilder.controls.ExtField;
/*      */ import com.webbuilder.controls.ExtFieldSet;
/*      */ import com.webbuilder.controls.ExtForm;
/*      */ import com.webbuilder.controls.ExtGrid;
/*      */ import com.webbuilder.controls.ExtHtmlEditor;
/*      */ import com.webbuilder.controls.ExtKeyMap;
/*      */ import com.webbuilder.controls.ExtLabel;
/*      */ import com.webbuilder.controls.ExtMenu;
/*      */ import com.webbuilder.controls.ExtMenuItem;
/*      */ import com.webbuilder.controls.ExtNumber;
/*      */ import com.webbuilder.controls.ExtPanel;
/*      */ import com.webbuilder.controls.ExtRadioBox;
/*      */ import com.webbuilder.controls.ExtScript;
/*      */ import com.webbuilder.controls.ExtSlider;
/*      */ import com.webbuilder.controls.ExtStore;
/*      */ import com.webbuilder.controls.ExtTab;
/*      */ import com.webbuilder.controls.ExtTextField;
/*      */ import com.webbuilder.controls.ExtTime;
/*      */ import com.webbuilder.controls.ExtTree;
/*      */ import com.webbuilder.controls.ExtTreeList;
/*      */ import com.webbuilder.controls.ExtValidator;
/*      */ import com.webbuilder.controls.ExtViewPort;
/*      */ import com.webbuilder.controls.ExtWindow;
/*      */ import com.webbuilder.controls.Flash;
/*      */ import com.webbuilder.controls.Form;
/*      */ import com.webbuilder.controls.Iframe;
/*      */ import com.webbuilder.controls.Image;
/*      */ import com.webbuilder.controls.Label;
/*      */ import com.webbuilder.controls.Link;
/*      */ import com.webbuilder.controls.Mailer;
/*      */ import com.webbuilder.controls.Method;
/*      */ import com.webbuilder.controls.Query;
/*      */ import com.webbuilder.controls.Script;
/*      */ import com.webbuilder.controls.Select;
/*      */ import com.webbuilder.controls.Timer;
/*      */ import com.webbuilder.utils.DbUtil;
/*      */ import com.webbuilder.utils.FileUtil;
/*      */ import com.webbuilder.utils.StringUtil;
/*      */ import com.webbuilder.utils.SysUtil;
/*      */ import com.webbuilder.utils.WebUtil;
/*      */ import java.io.File;
/*      */ import java.io.InputStream;
/*      */ import java.io.PrintWriter;
/*      */ import java.sql.Connection;
/*      */ import java.sql.ResultSet;
/*      */ import java.util.Enumeration;
/*      */ import java.util.Iterator;
/*      */ import java.util.Stack;
/*      */ import javax.servlet.RequestDispatcher;
/*      */ import javax.servlet.http.HttpServletRequest;
/*      */ import javax.servlet.http.HttpServletResponse;
/*      */ import org.dom4j.Attribute;
/*      */ import org.dom4j.Document;
/*      */ import org.dom4j.Element;
/*      */ import org.dom4j.io.SAXReader;
/*      */ 
/*      */ public class Parser
/*      */ {
/*   84 */   private String[] controls = { "extTextField", "extLabel", "extPanel", "extAjax", "extStore", "extComboBox", "extNumber", "query", "dataProvider", "extMenuItem", "extMenu", "method", "extButton", "extWindow", "extForm", "extTab", "extScript", "extFieldSet", "extHtmlEditor", "extCheckBox", "extRadioBox", "extTree", "extColumn", "extColumnModel", "extGrid", "extDate", "extTime", "body", "extViewPort", "image", "extTreeList", "extDualList", "extComboTree", "extAction", "dbUpdater", "extChart", "chartContent", "defaultValue", "extKeyMap", "excepter", "extSlider", "extValidator", "form", "script", "label", "edit", "button", "iframe", "div", "select", "link", "timer", "flash", "mailer", "file" };
/*      */   private Document xmlDoc;
/*      */   private Element element;
/*   84 */   private PrintWriter writer = null;
/*      */   private Stack<String> footer;
/*      */   private Stack<String> footerScript;
/*      */   private Stack<String> extFooterScript;
/*      */   private Stack<String> extLastScript;
/*   84 */   private boolean createBody = true; private String webPath = ""; private String methodName = ""; private String finallyMethodName = ""; private String exceptionMethodName = ""; private String needLogin = ""; private String logText = ""; private String exceptionType = ""; private String action = ""; private String dbType = "";
/*      */   private File xmlFile;
/*   84 */   private StringBuilder javaScript = new StringBuilder(); private boolean javaScriptAdded = false; private StringBuilder extJavaScript = new StringBuilder(); private boolean extJavaScriptAdded = false; private StringBuilder extFirstJavaScript = new StringBuilder(); private boolean extFirstJavaScriptAdded = false; private StringBuilder extLastJavaScript = new StringBuilder(); private boolean extLastJavaScriptAdded = false;
/*      */   private HttpServletRequest request;
/*      */   private HttpServletResponse response;
/*      */ 
/*      */   public Parser(String webPath, File xmlFile, HttpServletRequest req, HttpServletResponse resp, String action)
/*      */     throws Exception
/*      */   {
/*   84 */     this.request = req; this.response = resp; this.webPath = webPath; this.action = action; this.xmlFile = xmlFile; this.footer = new Stack(); this.footerScript = new Stack(); this.extFooterScript = new Stack(); this.extLastScript = new Stack(); SAXReader xmlReader = new SAXReader(); this.xmlDoc = xmlReader.read(xmlFile); this.xmlDoc.setXMLEncoding("utf-8"); this.dbType = req.getAttribute("sys.dbType").toString();
/*      */   }
/*      */ 
/*      */   public void parse()
/*      */     throws Exception
/*      */   {
/*  143 */     boolean isClosed = false;
/*      */     try { this.element = this.xmlDoc.getRootElement();
/*  147 */       setSystemVar();
/*  148 */       if (!WebUtil.checkLogin(this.needLogin, this.request, this.response))
                               	return;
/*      */       while (true) {
				
/*  150 */         if (WebUtil.userHasRight(this.needLogin, this.xmlFile, this.webPath, this.request)) 
			          break;
/*  151 */         this.response.setStatus(403);
/*  152 */         this.request.getRequestDispatcher(
/*  153 */           "main?action=webbuilder/system/forbidden.xwl").forward(
/*  154 */           this.request, this.response);
/*      */       }
/*      */ 
/*  157 */       if (!StringUtil.isEqual(this.logText, "-")) {
/*  158 */         if (StringUtil.isEmpty(this.logText))
/*  159 */           WebUtil.recordLog(this.request, this.action, 1);
/*      */         else
/*  161 */           WebUtil.recordLog(this.request, this.logText, 1);
/*      */       }
/*  163 */       if (!StringUtil.isEmpty(this.methodName))
/*  164 */         SysUtil.executeMethod(this.methodName, this.request, this.response);
/*  165 */       parseElements(this.element);
/*  166 */       if (StringUtil.isEqual(this.exceptionType, "mark"))
/*  167 */         getWriter().print("{@ok@}");
/*      */     } catch (Exception e) {
/*      */       try {
/*  170 */         WebUtil.recordLog(this.request, this.action + ":" + 
/*  171 */           SysUtil.getShortError(e), 3);
/*  172 */         if (!StringUtil.isEmpty(this.exceptionMethodName)) {
/*  173 */           this.request.setAttribute("sys.except", e);
/*  174 */           SysUtil.executeMethod(this.exceptionMethodName, this.request, 
/*  175 */             this.response);
/*      */         }
/*      */       } catch (Exception localException1) {
/*      */       }
/*  179 */       closeAllObject(this.request, true);
/*  180 */       isClosed = true;
/*  181 */       WebUtil.showException(this.exceptionType, e, this.request, this.response);
/*      */     } finally {
/*  183 */       if (!isClosed)
/*  184 */         closeAllObject(this.request, false);
/*  185 */       if (!StringUtil.isEmpty(this.finallyMethodName))
/*  186 */         SysUtil.executeMethod(this.finallyMethodName, this.request, this.response);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void parseSimple() throws Exception {
/*      */     try {
/*  192 */       this.element = this.xmlDoc.getRootElement();
/*  193 */       this.methodName = getString("methodName");
/*  194 */       this.finallyMethodName = getString("finallyMethodName");
/*  195 */       if (!StringUtil.isEmpty(this.methodName))
/*  196 */         SysUtil.executeMethod(this.methodName, this.request, this.response);
/*  197 */       parseElements(this.element);
/*      */     } finally {
/*  199 */       if (!StringUtil.isEmpty(this.finallyMethodName))
/*  200 */         SysUtil.executeMethod(this.finallyMethodName, this.request, this.response);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void setSystemVar() {
/*  205 */     this.methodName = getString("methodName");
/*  206 */     this.finallyMethodName = getString("finallyMethodName");
/*  207 */     this.exceptionMethodName = getString("exceptionMethodName");
/*  208 */     this.logText = getString("logText");
/*  209 */     String logType = getString("logType");
/*  210 */     if (!StringUtil.isEmpty(logType))
/*  211 */       this.request.setAttribute("sys.logType", logType);
/*  212 */     this.exceptionType = getString("exceptionType");
/*  213 */     if (StringUtil.isEmpty(this.exceptionType))
/*  214 */       this.exceptionType = this.request.getAttribute("sys.exceptionType")
/*  215 */         .toString();
/*  216 */     this.needLogin = getString("needLogin");
/*  217 */     if (StringUtil.isEmpty(this.needLogin))
/*  218 */       this.needLogin = this.request.getAttribute("sys.needLogin").toString();
/*      */   }
/*      */ 
/*      */   private void addJavaScript(String script) {
/*  222 */     if (!StringUtil.isEmpty(script)) {
/*  223 */       if (this.javaScriptAdded)
/*  224 */         this.javaScript.append("\r\n");
/*      */       else
/*  226 */         this.javaScriptAdded = true;
/*  227 */       this.javaScript.append(script);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void addExtJavaScript(String script) {
/*  232 */     if (!StringUtil.isEmpty(script)) {
/*  233 */       if (this.extJavaScriptAdded)
/*  234 */         this.extJavaScript.append("\r\n");
/*      */       else
/*  236 */         this.extJavaScriptAdded = true;
/*  237 */       this.extJavaScript.append(script);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void addExtFirstJavaScript(String script) {
/*  242 */     if (!StringUtil.isEmpty(script)) {
/*  243 */       if (this.extFirstJavaScriptAdded)
/*  244 */         this.extFirstJavaScript.append("\r\n");
/*      */       else
/*  246 */         this.extFirstJavaScriptAdded = true;
/*  247 */       this.extFirstJavaScript.append(script);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void addExtLastJavaScript(String script) {
/*  252 */     if (!StringUtil.isEmpty(script)) {
/*  253 */       if (this.extLastJavaScriptAdded)
/*  254 */         this.extLastJavaScript.append("\r\n");
/*      */       else
/*  256 */         this.extLastJavaScriptAdded = true;
/*  257 */       this.extLastJavaScript.append(script);
/*      */     }
/*      */   }
/*      */ 
/*      */   private PrintWriter getWriter() throws Exception {
/*  262 */     if (this.writer == null)
/*  263 */       this.writer = this.response.getWriter();
/*  264 */     return this.writer;
/*      */   }
/*      */ 
/*      */   private void outputJavaScript()
/*      */     throws Exception
/*      */   {
/*  270 */     boolean hasExtJs = (this.extJavaScriptAdded) || (this.extFirstJavaScriptAdded) || 
/*  271 */       (this.extLastJavaScriptAdded);
/*  272 */     boolean hasJs = this.javaScriptAdded;
/*  273 */     if ((hasExtJs) || (hasJs)) {
/*  274 */       output("<script language=\"JavaScript\" type=\"text/JavaScript\">");
/*  275 */       if (hasJs)
/*  276 */         output(this.javaScript.toString());
/*  277 */       if (hasExtJs) {
/*  278 */         output("Ext.onReady(function(){");
/*  279 */         output(this.extFirstJavaScript.toString());
/*  280 */         output(this.extJavaScript.toString());
/*  281 */         output(this.extLastJavaScript.toString());
/*  282 */         output("});");
/*      */       }
/*  284 */       output("</script>");
/*      */     }
/*      */   }
/*      */ 
/*      */   private void parseElements(Element element) throws Exception {
/*  289 */     String elementName = element.getName();
/*  290 */     int typeId = StringUtil.stringInList(this.controls, elementName);
/*  291 */     Control control = null;
/*      */ 
/*  293 */     this.element = element;
/*  294 */     switch (typeId) {
/*      */     case 0:
/*  296 */       control = getExtTextField();
/*  297 */       break;
/*      */     case 1:
/*  299 */       control = getExtLabel();
/*  300 */       break;
/*      */     case 2:
/*  302 */       control = getExtPanel();
/*  303 */       break;
/*      */     case 3:
/*  305 */       control = getExtAjax();
/*  306 */       break;
/*      */     case 4:
/*  308 */       control = getExtStore();
/*  309 */       break;
/*      */     case 5:
/*  311 */       control = getExtComboBox();
/*  312 */       break;
/*      */     case 6:
/*  314 */       control = getExtNumber();
/*  315 */       break;
/*      */     case 7:
/*  317 */       control = getQuery();
/*  318 */       break;
/*      */     case 8:
/*  320 */       control = getDataProvider();
/*  321 */       break;
/*      */     case 9:
/*  323 */       control = getExtMenuItem();
/*  324 */       break;
/*      */     case 10:
/*  326 */       control = getExtMenu();
/*  327 */       break;
/*      */     case 11:
/*  329 */       control = getMethod();
/*  330 */       break;
/*      */     case 12:
/*  332 */       control = getExtButton();
/*  333 */       break;
/*      */     case 13:
/*  335 */       control = getExtWindow();
/*  336 */       break;
/*      */     case 14:
/*  338 */       control = getExtForm();
/*  339 */       break;
/*      */     case 15:
/*  341 */       control = getExtTab();
/*  342 */       break;
/*      */     case 16:
/*  344 */       control = getExtScript();
/*  345 */       break;
/*      */     case 17:
/*  347 */       control = getExtFieldSet();
/*  348 */       break;
/*      */     case 18:
/*  350 */       control = getExtHtmlEditor();
/*  351 */       break;
/*      */     case 19:
/*  353 */       control = getExtCheckBox();
/*  354 */       break;
/*      */     case 20:
/*  356 */       control = getExtRadioBox();
/*  357 */       break;
/*      */     case 21:
/*  359 */       control = getExtTree();
/*  360 */       break;
/*      */     case 22:
/*  362 */       control = getExtColumn();
/*  363 */       break;
/*      */     case 23:
/*  365 */       control = getExtColumnModel();
/*  366 */       break;
/*      */     case 24:
/*  368 */       control = getExtGrid();
/*  369 */       break;
/*      */     case 25:
/*  371 */       control = getExtDate();
/*  372 */       break;
/*      */     case 26:
/*  374 */       control = getExtTime();
/*  375 */       break;
/*      */     case 27:
/*  377 */       control = getBody();
/*  378 */       break;
/*      */     case 28:
/*  380 */       control = getExtViewPort();
/*  381 */       break;
/*      */     case 29:
/*  383 */       control = getImage();
/*  384 */       break;
/*      */     case 30:
/*  386 */       control = getExtTreeList();
/*  387 */       break;
/*      */     case 31:
/*  389 */       control = getExtDualList();
/*  390 */       break;
/*      */     case 32:
/*  392 */       control = getExtComboTree();
/*  393 */       break;
/*      */     case 33:
/*  395 */       control = getExtAction();
/*  396 */       break;
/*      */     case 34:
/*  398 */       control = getDbUpdater();
/*  399 */       break;
/*      */     case 35:
/*  401 */       control = getExtChart();
/*  402 */       break;
/*      */     case 36:
/*  404 */       control = getChartContent();
/*  405 */       break;
/*      */     case 37:
/*  407 */       control = getDefaultValue();
/*  408 */       break;
/*      */     case 38:
/*  410 */       control = getExtKeyMap();
/*  411 */       break;
/*      */     case 39:
/*  413 */       control = getExcepter();
/*  414 */       break;
/*      */     case 40:
/*  416 */       control = getExtSlider();
/*  417 */       break;
/*      */     case 41:
/*  419 */       control = getExtValidator();
/*  420 */       break;
/*      */     case 42:
/*  422 */       control = getForm();
/*  423 */       break;
/*      */     case 43:
/*  425 */       control = getScript();
/*  426 */       break;
/*      */     case 44:
/*  428 */       control = getLabel();
/*  429 */       break;
/*      */     case 45:
/*  431 */       control = getEdit();
/*  432 */       break;
/*      */     case 46:
/*  434 */       control = getButton();
/*  435 */       break;
/*      */     case 47:
/*  437 */       control = getIframe();
/*  438 */       break;
/*      */     case 48:
/*  440 */       control = getDiv();
/*  441 */       break;
/*      */     case 49:
/*  443 */       control = getSelect();
/*  444 */       break;
/*      */     case 50:
/*  446 */       control = getLink();
/*  447 */       break;
/*      */     case 51:
/*  449 */       control = getTimer();
/*  450 */       break;
/*      */     case 52:
/*  452 */       control = getFlash();
/*  453 */       break;
/*      */     case 53:
/*  455 */       control = getMailer();
/*  456 */       break;
/*      */     case 54:
/*  458 */       parseFile(getString("fileName"));
/*      */     }
/*      */ 
/*  461 */     if (control != null)
/*  462 */       createControl(control);
/*  463 */     Iterator iterator = element.elementIterator();
/*  464 */     while (iterator.hasNext())
/*  465 */       parseElements((Element)iterator.next());
/*  466 */     if (control != null) {
/*  467 */       addJavaScript((String)this.footerScript.pop());
/*  468 */       addExtJavaScript((String)this.extFooterScript.pop());
/*  469 */       addExtLastJavaScript((String)this.extLastScript.pop());
/*  470 */       if (typeId == 27)
/*  471 */         outputJavaScript();
/*  472 */       output((String)this.footer.pop());
/*      */     }
/*      */   }
/*      */ 
/*      */   private String getString(String property)
/*      */   {
/*  479 */     Attribute attribute = this.element.attribute(property);
/*  480 */     if (attribute != null) {
/*  481 */       return StringUtil.replaceParameters(this.request, attribute.getText());
/*      */     }
/*  483 */     return "";
/*      */   }
/*      */ 
/*      */   private String getNativeString(String property)
/*      */   {
/*  489 */     Attribute attribute = this.element.attribute(property);
/*  490 */     if (attribute != null) {
/*  491 */       return attribute.getText();
/*      */     }
/*  493 */     return "";
/*      */   }
/*      */ 
/*      */   private boolean getBool(String property, boolean defaultValue)
/*      */   {
/*  499 */     Attribute attribute = this.element.attribute(property);
/*  500 */     if (attribute == null) {
/*  501 */       return defaultValue;
/*      */     }
/*  503 */     String value = StringUtil.replaceParameters(this.request, attribute
/*  504 */       .getText());
/*  505 */     if (StringUtil.isEmpty(value)) {
/*  506 */       return defaultValue;
/*      */     }
/*  508 */     return StringUtil.getStringBool(value);
/*      */   }
/*      */ 
/*      */   private Element findFirstChild(Element element, boolean isExt)
/*      */   {
/*  514 */     Iterator iterator = element.elementIterator();
/*      */ 
/*  517 */     while (iterator.hasNext()) {
/*  518 */       Element child = (Element)iterator.next();
/*  519 */       boolean isExtChild = isExtControl(child.getName());
/*  520 */       if (((isExt) && (isExtChild)) || ((!isExt) && (!isExtChild)))
/*  521 */         return child;
/*      */     }
/*  523 */     return null;
/*      */   }
/*      */ 
/*      */   private void createControl(Control control) throws Exception {
/*  527 */     if ((control instanceof ExtControl))
/*  528 */       setExtControlProperty((ExtControl)control);
/*  529 */     String name = getString("name");
/*  530 */     control.setName(name);
/*  531 */     control.setCaption(getString("caption"));
/*  532 */     control.setValue(getString("value"));
/*  533 */     control.setHint(getString("hint"));
/*  534 */     control.setMargin(getString("margin"));
/*  535 */     control.setPadding(getString("padding"));
/*  536 */     control.setClassTag(getString("class"));
/*  537 */     control.setTag(getString("tag"));
/*  538 */     control.setWidth(getString("width"));
/*  539 */     control.setHeight(getString("height"));
/*  540 */     control.setColor(getString("color"));
/*  541 */     control.setBgColor(getString("bgColor"));
/*  542 */     control.setBgImage(getString("bgImage"));
/*  543 */     control.setStyle(getString("style"));
/*  544 */     control.setRequest(this.request);
/*  545 */     control.setResponse(this.response);
/*  546 */     control.create();
/*  547 */     output(control.getHeader());
/*  548 */     this.footer.push(control.getFooter());
/*  549 */     addJavaScript(control.getHeaderScript());
/*  550 */     this.footerScript.push(control.getFooterScript());
/*  551 */     addExtJavaScript(control.getExtHeaderScript());
/*  552 */     addExtFirstJavaScript(control.getFirstExtScript());
/*  553 */     this.extFooterScript.push(control.getExtFooterScript());
/*  554 */     this.extLastScript.push(control.getLastExtScript());
/*      */   }
/*      */ 
/*      */   private void setExtControlProperty(ExtControl extControl)
/*      */   {
/*  560 */     Element parentElement = this.element.getParent();
/*  561 */     if (parentElement != null) {
/*  562 */       extControl.setClassName(this.element.getName());
/*  563 */       extControl.setBodyStyle(getString("bodyStyle"));
/*  564 */       extControl.setParentClassName(parentElement.getName());
/*  565 */       extControl.setHasExtChild(findFirstChild(this.element, true) != null);
/*  566 */       extControl
/*  567 */         .setHasNoneExtChild(findFirstChild(this.element, false) != null);
/*  568 */       extControl
/*  569 */         .setIsFirstExtChild(findFirstChild(parentElement, true) == this.element);
/*  570 */       extControl.setDefaultValue(getString("defaults"));
/*  571 */       extControl.left = getString("left");
/*  572 */       extControl.top = getString("top");
/*  573 */       extControl.anchor = getString("anchor");
/*  574 */       extControl.disabled = getString("disabled");
/*  575 */       extControl.readOnly = getString("readOnly");
/*  576 */       extControl.hidden = getString("hidden");
/*  577 */       extControl.rowspan = getString("rowspan");
/*  578 */       extControl.colspan = getString("colspan");
/*      */     }
/*      */   }
/*      */ 
/*      */   private Body getBody() throws Exception {
/*  583 */     Body body = new Body();
/*  584 */     String cb = getString("createBody");
/*  585 */     if ((StringUtil.isEmpty(cb)) && (StringUtil.isEqual(this.exceptionType, "mark")))
/*  586 */       cb = "false";
/*  587 */     body.createBody = ((StringUtil.getStringBool(cb)) || 
/*  588 */       (StringUtil.isEmpty(cb)));
/*  589 */     this.createBody = body.createBody;
/*  590 */     body.head = getString("head");
/*  591 */     body.prefix = getString("prefix");
/*  592 */     body.userCssFiles = getString("cssFiles");
/*  593 */     body.userJsFiles = getString("jsFiles");
/*  594 */     body.onResize = getString("onResize");
/*  595 */     body.onBeforeUnload = getString("onBeforeUnload");
/*  596 */     body.onUnload = getString("onUnload");
/*  597 */     body.hdScript = getString("headerScript");
/*  598 */     body.ftScript = getString("footerScript");
/*  599 */     body.extHdScript = getString("extHeaderScript");
/*  600 */     body.extFtScript = getString("extFooterScript");
/*  601 */     body.firstExtScript = getString("extFirstScript");
/*  602 */     body.lastExtScript = getString("extLastScript");
/*  603 */     body.loadLib = getBool("loadLib", true);
/*  604 */     body.showHint = getBool("showHint", true);
/*  605 */     return body;
/*      */   }
/*      */ 
/*      */   private Form getForm() {
/*  609 */     Form form = new Form();
/*  610 */     form.action = getString("action");
/*  611 */     form.enctype = getString("enctype");
/*  612 */     form.target = getString("target");
/*  613 */     return form;
/*      */   }
/*      */ 
/*      */   private Iframe getIframe() {
/*  617 */     Iframe iframe = new Iframe();
/*  618 */     iframe.src = getString("src");
/*  619 */     iframe.scrolling = getString("scrolling");
/*  620 */     iframe.frameborder = getString("frameborder");
/*  621 */     return iframe;
/*      */   }
/*      */ 
/*      */   private Div getDiv() {
/*  625 */     Div div = new Div();
/*  626 */     div.align = getString("align");
/*  627 */     div.overflow = getString("overflow");
/*  628 */     return div;
/*      */   }
/*      */ 
/*      */   private Query getQuery() {
/*  632 */     String dt = getString("dbType");
/*  633 */     if ((!StringUtil.isEmpty(dt)) && (!StringUtil.isSame(dt, this.dbType)))
/*  634 */       return null;
/*  635 */     Query query = new Query();
/*  636 */     query.jndi = getString("jndi");
/*  637 */     query.sql = getNativeString("sql");
/*  638 */     query.type = getString("type");
/*  639 */     query.transaction = getString("transaction");
/*  640 */     query.isolation = getString("isolation");
/*  641 */     query.loadData = getBool("loadData", false);
/*  642 */     query.fastMode = getBool("fastMode", true);
/*  643 */     return query;
/*      */   }
/*      */ 
/*      */   private DbUpdater getDbUpdater() {
/*  647 */     String dt = getString("dbType");
/*  648 */     if ((!StringUtil.isEmpty(dt)) && (!StringUtil.isSame(dt, this.dbType)))
/*  649 */       return null;
/*  650 */     DbUpdater dbUpdater = new DbUpdater();
/*  651 */     dbUpdater.jndi = getString("jndi");
/*  652 */     dbUpdater.data = getString("data");
/*  653 */     dbUpdater.file = getString("file");
/*  654 */     dbUpdater.fileHasTitle = getBool("fileHasTitle", true);
/*  655 */     dbUpdater.sequence = getBool("sequence", false);
/*  656 */     dbUpdater.fileFormat = getString("fileFormat");
/*  657 */     dbUpdater.fileLineSeparator = getString("fileLineSeparator");
/*  658 */     dbUpdater.sqls = getNativeString("sqls");
/*  659 */     dbUpdater.insertSql = getNativeString("insertSql");
/*  660 */     dbUpdater.updateSql = getNativeString("updateSql");
/*  661 */     dbUpdater.deleteSql = getNativeString("deleteSql");
/*  662 */     dbUpdater.transaction = getString("transaction");
/*  663 */     dbUpdater.isolation = getString("isolation");
/*  664 */     dbUpdater.bufferSize = getString("bufferSize");
/*  665 */     return dbUpdater;
/*      */   }
/*      */ 
/*      */   private void setExtPanelProperty(ExtPanel extPanel) {
/*  669 */     extPanel.content = getString("content");
/*  670 */     extPanel.layout = getString("layout");
/*  671 */     extPanel.region = getString("region");
/*  672 */     extPanel.autoScroll = getString("autoScroll");
/*  673 */     extPanel.collapsible = getString("collapsible");
/*  674 */     extPanel.collapsed = getString("collapsed");
/*  675 */     extPanel.titleCollapse = getString("titleCollapse");
/*  676 */     extPanel.frame = getString("frame");
/*  677 */     extPanel.floating = getString("floating");
/*  678 */     extPanel.border = getString("border");
/*  679 */     extPanel.split = getString("split");
/*  680 */     extPanel.minSize = getString("minSize");
/*  681 */     extPanel.maxSize = getString("maxSize");
/*  682 */     extPanel.hideMode = getString("hideMode");
/*  683 */     extPanel.activeItem = getString("activeItem");
/*  684 */     extPanel.buttons = getString("buttons");
/*  685 */     extPanel.minButtonWidth = getString("minButtonWidth");
/*  686 */     extPanel.buttonAlign = getString("buttonAlign");
/*  687 */     extPanel.tools = getString("tools");
/*  688 */     extPanel.tbar = getString("tbar");
/*  689 */     extPanel.bbar = getString("bbar");
/*  690 */     extPanel.labelAlign = getString("labelAlign");
/*  691 */     extPanel.labelWidth = getString("labelWidth");
/*  692 */     extPanel.columns = getString("columns");
/*  693 */     extPanel.layoutConfig = getString("layoutConfig");
/*  694 */     extPanel.onResize = getString("onResize");
/*      */   }
/*      */ 
/*      */   private ExtPanel getExtPanel() {
/*  698 */     ExtPanel extPanel = new ExtPanel();
/*  699 */     setExtPanelProperty(extPanel);
/*  700 */     return extPanel;
/*      */   }
/*      */ 
/*      */   private ExtForm getExtForm() {
/*  704 */     ExtForm extForm = new ExtForm();
/*  705 */     setExtPanelProperty(extForm);
/*  706 */     return extForm;
/*      */   }
/*      */ 
/*      */   private ExtWindow getExtWindow() {
/*  710 */     ExtWindow extWindow = new ExtWindow();
/*  711 */     setExtPanelProperty(extWindow);
/*  712 */     extWindow.modal = getString("modal");
/*  713 */     extWindow.iconCls = getString("iconCls");
/*  714 */     extWindow.dialog = getBool("dialog", true);
/*  715 */     extWindow.enterOk = getString("enterOk");
/*  716 */     extWindow.anchorCenter = getString("anchorCenter");
/*  717 */     extWindow.onOk = getString("onOk");
/*  718 */     extWindow.closable = getString("closable");
/*  719 */     extWindow.resizable = getString("resizable");
/*  720 */     extWindow.minimizable = getString("minimizable");
/*  721 */     extWindow.maximizable = getString("maximizable");
/*  722 */     extWindow.plain = getString("plain");
/*  723 */     extWindow.closeAction = getString("closeAction");
/*  724 */     extWindow.defaultButton = getString("defaultButton");
/*  725 */     extWindow.minWidth = getString("minWidth");
/*  726 */     extWindow.minHeight = getString("minHeight");
/*  727 */     extWindow.autoShow = getBool("autoShow", false);
/*  728 */     extWindow.onClose = getString("onClose");
/*  729 */     extWindow.onActivate = getString("onActivate");
/*  730 */     extWindow.onDeactivate = getString("onDeactivate");
/*  731 */     extWindow.onHide = getString("onHide");
/*  732 */     extWindow.onShow = getString("onShow");
/*  733 */     return extWindow;
/*      */   }
/*      */ 
/*      */   private ExtFieldSet getExtFieldSet() {
/*  737 */     ExtFieldSet extFieldSet = new ExtFieldSet();
/*  738 */     setExtPanelProperty(extFieldSet);
/*  739 */     extFieldSet.autoHeight = getString("autoHeight");
/*  740 */     extFieldSet.collapsed = getString("collapsed");
/*  741 */     extFieldSet.checkboxToggle = getString("checkboxToggle");
/*  742 */     return extFieldSet;
/*      */   }
/*      */ 
/*      */   private ExtAction getExtAction() {
/*  746 */     ExtAction extAction = new ExtAction();
/*  747 */     extAction.handler = getString("onAction");
/*  748 */     extAction.minWidth = getString("minWidth");
/*  749 */     extAction.hidden = getString("hidden");
/*  750 */     extAction.icon = getString("icon");
/*  751 */     extAction.iconCls = getString("iconCls");
/*  752 */     return extAction;
/*      */   }
/*      */ 
/*      */   private ExtColumnModel getExtColumnModel() {
/*  756 */     ExtColumnModel extColumnModel = new ExtColumnModel();
/*  757 */     extColumnModel.columnsDefine = getString("columnsDefine");
/*  758 */     extColumnModel.asComponent = (!this.createBody);
/*  759 */     return extColumnModel;
/*      */   }
/*      */ 
/*      */   private ExtColumn getExtColumn() {
/*  763 */     ExtColumn extColumn = new ExtColumn();
/*  764 */     extColumn.type = getString("type");
/*  765 */     extColumn.fieldName = getString("fieldName");
/*  766 */     extColumn.mapKey = getString("mapKey");
/*  767 */     extColumn.emptyLabel = getString("emptyLabel");
/*  768 */     extColumn.editor = getString("editor");
/*  769 */     extColumn.align = getString("align");
/*  770 */     extColumn.fixed = getString("fixed");
/*  771 */     extColumn.format = getString("format");
/*  772 */     extColumn.hideable = getString("hideable");
/*  773 */     extColumn.resizable = getString("resizable");
/*  774 */     extColumn.sortable = getString("sortable");
/*  775 */     extColumn.renderer = getString("renderer");
/*  776 */     extColumn.asComponent = (!this.createBody);
/*  777 */     return extColumn;
/*      */   }
/*      */ 
/*      */   private ExtMenu getExtMenu() {
/*  781 */     ExtMenu extMenu = new ExtMenu();
/*  782 */     extMenu.autoShow = getString("autoShow");
/*  783 */     extMenu.innerTool = getBool("innerTool", false);
/*  784 */     return extMenu;
/*      */   }
/*      */ 
/*      */   private ExtAjax getExtAjax() {
/*  788 */     ExtAjax extAjax = new ExtAjax();
/*  789 */     extAjax.url = getString("url");
/*  790 */     extAjax.params = getString("params");
/*  791 */     extAjax.out = getString("out");
/*  792 */     extAjax.in = getString("in");
/*  793 */     extAjax.form = getString("form");
/*  794 */     extAjax.reset = getBool("reset", false);
/*  795 */     extAjax.method = getString("method");
/*  796 */     extAjax.isUpload = getString("isUpload");
/*  797 */     extAjax.timeout = getString("timeout");
/*  798 */     extAjax.waitTitle = getString("waitTitle");
/*  799 */     extAjax.waitMsg = getString("waitMsg");
/*  800 */     extAjax.showMessage = getString("showMessage");
/*  801 */     extAjax.onSuccess = getString("onSuccess");
/*  802 */     extAjax.onFailure = getString("onFailure");
/*  803 */     return extAjax;
/*      */   }
/*      */ 
/*      */   private ExtKeyMap getExtKeyMap() {
/*  807 */     ExtKeyMap extKeyMap = new ExtKeyMap();
/*  808 */     extKeyMap.key = getString("key");
/*  809 */     extKeyMap.scope = getString("scope");
/*  810 */     extKeyMap.ctrl = getString("ctrl");
/*  811 */     extKeyMap.alt = getString("alt");
/*  812 */     extKeyMap.shift = getString("shift");
/*  813 */     extKeyMap.handler = getString("onKeyPress");
/*  814 */     return extKeyMap;
/*      */   }
/*      */ 
/*      */   private ExtStore getExtStore() {
/*  818 */     ExtStore extStore = new ExtStore();
/*  819 */     extStore.url = getString("url");
/*  820 */     extStore.baseParams = getString("baseParams");
/*  821 */     extStore.fields = getString("fields");
/*  822 */     extStore.autoLoad = getString("autoLoad");
/*  823 */     extStore.remoteSort = getString("remoteSort");
/*  824 */     extStore.format = getString("format");
/*  825 */     extStore.record = getString("record");
/*  826 */     extStore.total = getString("total");
/*  827 */     extStore.id = getString("id");
/*  828 */     extStore.timeout = getString("timeout");
/*  829 */     extStore.mapGrid = getString("mapGrid");
/*  830 */     extStore.waitMsg = getString("waitMsg");
/*  831 */     extStore.waitTitle = getString("waitTitle");
/*  832 */     extStore.showMessage = getString("showMessage");
/*  833 */     extStore.onBeforeLoad = getString("onBeforeLoad");
/*  834 */     extStore.onAfterLoad = getString("onAfterLoad");
/*  835 */     extStore.onLoadException = getString("onLoadException");
/*  836 */     extStore.onAdd = getString("onAdd");
/*  837 */     extStore.onRemove = getString("onRemove");
/*  838 */     extStore.onUpdate = getString("onUpdate");
/*  839 */     extStore.onDataChanged = getString("onDataChanged");
/*  840 */     return extStore;
/*      */   }
/*      */ 
/*      */   private ExtMenuItem getExtMenuItem() {
/*  844 */     ExtMenuItem extMenuItem = new ExtMenuItem();
/*  845 */     extMenuItem.checked = getString("checked");
/*  846 */     extMenuItem.handler = getString("onClick");
/*  847 */     extMenuItem.icon = getString("icon");
/*  848 */     extMenuItem.iconCls = getString("iconCls");
/*  849 */     extMenuItem.ignoreParentClicks = getString("ignoreParentClicks");
/*  850 */     extMenuItem.minWidth = getString("minWidth");
/*  851 */     extMenuItem.shadow = getString("shadow");
/*  852 */     extMenuItem.buttonType = getString("buttonType");
/*  853 */     extMenuItem.action = getString("action");
/*  854 */     extMenuItem.menuType = getString("menuType");
/*  855 */     extMenuItem.group = getString("group");
/*  856 */     extMenuItem.enableToggle = getString("enableToggle");
/*  857 */     extMenuItem.onToggle = getString("onToggle");
/*  858 */     return extMenuItem;
/*      */   }
/*      */ 
/*      */   private ExtButton getExtButton() {
/*  862 */     ExtButton extButton = new ExtButton();
/*  863 */     extButton.handler = getString("onClick");
/*  864 */     extButton.autoShow = getBool("autoShow", true);
/*  865 */     extButton.icon = getString("icon");
/*  866 */     extButton.iconCls = getString("iconCls");
/*  867 */     extButton.minWidth = getString("minWidth");
/*  868 */     extButton.type = getString("type");
/*  869 */     extButton.action = getString("action");
/*  870 */     extButton.menu = getString("menu");
/*  871 */     return extButton;
/*      */   }
/*      */ 
/*      */   private ExtTab getExtTab() {
/*  875 */     ExtTab extTab = new ExtTab();
/*  876 */     setExtPanelProperty(extTab);
/*  877 */     extTab.activeTab = getString("activeTab");
/*  878 */     extTab.enableTabScroll = getString("enableTabScroll");
/*  879 */     extTab.tabPosition = getString("tabPosition");
/*  880 */     extTab.deferredRender = getString("deferredRender");
/*  881 */     extTab.onBeforeTabChange = getString("onBeforeTabChange");
/*  882 */     extTab.onTabChange = getString("onTabChange");
/*  883 */     return extTab;
/*      */   }
/*      */ 
/*      */   private ExtTreeList getExtTreeList() {
/*  887 */     ExtTreeList extTreeList = new ExtTreeList();
/*  888 */     setExtPanelProperty(extTreeList);
/*  889 */     extTreeList.query = getString("query");
/*  890 */     extTreeList.hasValue = getBool("hasValue", false);
/*  891 */     extTreeList.sorted = getString("sorted");
/*  892 */     extTreeList.showTool = getBool("showTool", false);
/*  893 */     extTreeList.showAddress = getBool("showAddress", false);
/*  894 */     extTreeList.treeWidth = getString("treeWidth");
/*  895 */     extTreeList.submitMode = getString("submitMode");
/*  896 */     extTreeList.fieldCount = getString("fieldCount");
/*  897 */     extTreeList.sourceType = getString("sourceType");
/*  898 */     extTreeList.expandAll = getBool("expandAll", false);
/*  899 */     return extTreeList;
/*      */   }
/*      */ 
/*      */   private ExtDualList getExtDualList() {
/*  903 */     ExtDualList extDualList = new ExtDualList();
/*  904 */     setExtPanelProperty(extDualList);
/*  905 */     extDualList.query = getString("query");
/*  906 */     extDualList.sorted = getString("sorted");
/*  907 */     extDualList.submitMode = getString("submitMode");
/*  908 */     return extDualList;
/*      */   }
/*      */ 
/*      */   private ExtChart getExtChart() {
/*  912 */     ExtChart extChart = new ExtChart();
/*  913 */     extChart.type = getString("type");
/*  914 */     extChart.chartData = getString("chartData");
/*  915 */     extChart.chartContent = getString("chartContent");
/*  916 */     extChart.autoShow = getString("autoShow");
/*  917 */     extChart.backColor = getString("backColor");
/*  918 */     extChart.scaleMode = getString("scaleMode");
/*  919 */     extChart.transparent = getString("transparent");
/*  920 */     extChart.onResize = getString("onResize");
/*  921 */     return extChart;
/*      */   }
/*      */ 
/*      */   private Mailer getMailer() {
/*  925 */     Mailer mailer = new Mailer();
/*  926 */     mailer.from = getString("from");
/*  927 */     mailer.to = getString("to");
/*  928 */     mailer.cc = getString("cc");
/*  929 */     mailer.bcc = getString("bcc");
/*  930 */     mailer.smtp = getString("smtp");
/*  931 */     mailer.username = getString("username");
/*  932 */     mailer.password = getString("password");
/*  933 */     mailer.content = getString("content");
/*  934 */     mailer.attachFiles = getString("attachFiles");
/*  935 */     mailer.attachObjects = getString("attachObjects");
/*  936 */     mailer.attachObjectNames = getString("attachObjectNames");
/*  937 */     mailer.needAuth = getString("needAuth");
/*  938 */     return mailer;
/*      */   }
/*      */ 
/*      */   private Excepter getExcepter() {
/*  942 */     Excepter excepter = new Excepter();
/*  943 */     excepter.nameList = getString("nameList");
/*  944 */     excepter.valueList = getString("valueList");
/*  945 */     excepter.message = getString("message");
/*  946 */     excepter.condition = getString("condition");
/*  947 */     return excepter;
/*      */   }
/*      */ 
/*      */   private DefaultValue getDefaultValue() {
/*  951 */     DefaultValue defaultValue = new DefaultValue();
/*  952 */     defaultValue.key = getString("key");
/*  953 */     defaultValue.allowBlank = getBool("allowBlank", false);
/*  954 */     defaultValue.initialize = getBool("initialize", false);
/*  955 */     return defaultValue;
/*      */   }
/*      */ 
/*      */   private ChartContent getChartContent() {
/*  959 */     ChartContent chartContent = new ChartContent();
/*  960 */     chartContent.asComponent = this.createBody;
/*  961 */     chartContent.mapKey = getString("mapKey");
/*  962 */     chartContent.emptyLabel = getString("emptyLabel");
/*  963 */     chartContent.subCaption = getString("subCaption");
/*  964 */     chartContent.xAxisName = getString("xAxisName");
/*  965 */     chartContent.yAxisName = getString("yAxisName");
/*  966 */     chartContent.query = getString("query");
/*  967 */     chartContent.palette = getString("palette");
/*  968 */     chartContent.simpleSet = getString("simpleSet");
/*  969 */     chartContent.fontName = getString("fontName");
/*  970 */     chartContent.fontColor = getString("fontColor");
/*  971 */     chartContent.fontSize = getString("fontSize");
/*  972 */     chartContent.showLabels = getString("showLabels");
/*  973 */     chartContent.labelDisplay = getString("labelDisplay");
/*  974 */     chartContent.animation = getString("animation");
/*  975 */     chartContent.rotateLabels = getString("rotateLabels");
/*  976 */     chartContent.slantLabels = getString("slantLabels");
/*  977 */     chartContent.labelStep = getString("labelStep");
/*  978 */     chartContent.staggerLines = getString("staggerLines");
/*  979 */     chartContent.showValues = getString("showValues");
/*  980 */     chartContent.imageSave = getString("imageSave");
/*  981 */     chartContent.showPercentageValues = getString("showPercentageValues");
/*  982 */     chartContent.rotateValues = getString("rotateValues");
/*  983 */     chartContent.placeValuesInside = getString("placeValuesInside");
/*  984 */     chartContent.showYAxisValues = getString("showYAxisValues");
/*  985 */     chartContent.showLimits = getString("showLimits");
/*  986 */     chartContent.showDivLineValues = getString("showDivLineValues");
/*  987 */     chartContent.yAxisValuesStep = getString("yAxisValuesStep");
/*  988 */     chartContent.yAxisMinValue = getString("yAxisMinValue");
/*  989 */     chartContent.yAxisMaxValue = getString("yAxisMaxValue");
/*  990 */     chartContent.setAdaptiveYMin = getString("setAdaptiveYMin");
/*  991 */     chartContent.showShadow = getString("showShadow");
/*  992 */     chartContent.adjustDiv = getString("adjustDiv");
/*  993 */     chartContent.rotateYAxisName = getString("rotateYAxisName");
/*  994 */     chartContent.yAxisNameWidth = getString("yAxisNameWidth");
/*  995 */     chartContent.clickURL = getString("clickURL");
/*  996 */     chartContent.defaultAnimation = getString("defaultAnimation");
/*  997 */     chartContent.bgSWF = getString("bgSWF");
/*  998 */     chartContent.bgSWFAlpha = getString("bgSWFAlpha");
/*  999 */     chartContent.bgColor = getString("bgColor");
/* 1000 */     chartContent.bgAlpha = getString("bgAlpha");
/* 1001 */     chartContent.canvasbgColor = getString("canvasbgColor");
/* 1002 */     chartContent.canvasbgAlpha = getString("canvasbgAlpha");
/* 1003 */     chartContent.plotGradientColor = getString("plotGradientColor");
/* 1004 */     chartContent.useRoundEdges = getString("useRoundEdges");
/* 1005 */     chartContent.numberPrefix = getString("numberPrefix");
/* 1006 */     chartContent.numberSuffix = getString("numberSuffix");
/* 1007 */     chartContent.showToolTip = getString("showToolTip");
/* 1008 */     chartContent.enableSmartLabels = getString("enableSmartLabels");
/* 1009 */     chartContent.showPercentValues = getString("showPercentValues");
/* 1010 */     chartContent.decimals = getString("decimals");
/* 1011 */     chartContent.formatNumber = getString("formatNumber");
/* 1012 */     chartContent.formatNumberScale = getString("formatNumberScale");
/* 1013 */     chartContent.yAxisValueDecimals = getString("yAxisValueDecimals");
/* 1014 */     chartContent.numDivLines = getString("numDivLines");
/* 1015 */     chartContent.defaultNumberScale = getString("defaultNumberScale");
/* 1016 */     chartContent.numberScaleValue = getString("numberScaleValue");
/* 1017 */     chartContent.numberScaleUnit = getString("numberScaleUnit");
/* 1018 */     chartContent.itemProperty = getString("itemProperty");
/* 1019 */     chartContent.itemFieldCount = getString("itemFieldCount");
/* 1020 */     chartContent.showLegend = getString("showLegend");
/* 1021 */     chartContent.legendPosition = getString("legendPosition");
/* 1022 */     chartContent.tagXML = getString("tagXML");
/* 1023 */     return chartContent;
/*      */   }
/*      */ 
/*      */   private ExtComboTree getExtComboTree() {
/* 1027 */     ExtComboTree extComboTree = new ExtComboTree();
/* 1028 */     setExtFieldProperty(extComboTree);
/* 1029 */     setExtTextFieldProperty(extComboTree);
/* 1030 */     setExtComboBoxProperty(extComboTree);
/* 1031 */     extComboTree.animate = getString("animate");
/* 1032 */     extComboTree.fieldCount = getString("fieldCount");
/* 1033 */     extComboTree.treeWidth = getString("treeWidth");
/* 1034 */     extComboTree.treeHeight = getString("treeHeight");
/* 1035 */     extComboTree.sourceType = getString("sourceType");
/* 1036 */     extComboTree.autoAddress = getBool("autoAddress", false);
/* 1037 */     extComboTree.showToolType = getString("showToolType");
/* 1038 */     extComboTree.onClick = getString("onClick");
/* 1039 */     extComboTree.onDblClick = getString("onDblClick");
/* 1040 */     extComboTree.onCheckChange = getString("onCheckChange");
/* 1041 */     extComboTree.onTextChange = getString("onTextChange");
/* 1042 */     extComboTree.onDragdrop = getString("onDragdrop");
/* 1043 */     extComboTree.onAppend = getString("onAppend");
/* 1044 */     extComboTree.onRemove = getString("onRemove");
/* 1045 */     return extComboTree;
/*      */   }
/*      */ 
/*      */   private ExtGrid getExtGrid() {
/* 1049 */     ExtGrid extGrid = new ExtGrid();
/* 1050 */     setExtPanelProperty(extGrid);
/* 1051 */     extGrid.data = getString("data");
/* 1052 */     extGrid.store = getString("store");
/* 1053 */     extGrid.canEdit = getBool("canEdit", false);
/* 1054 */     extGrid.downloadAll = getString("downloadAll");
/* 1055 */     extGrid.autoEncode = getString("autoEncode");
/* 1056 */     extGrid.clicksToEdit = getString("clicksToEdit");
/* 1057 */     extGrid.loadStore = getString("loadStore");
/* 1058 */     extGrid.query = getString("query");
/* 1059 */     extGrid.fields = getString("fields");
/* 1060 */     extGrid.dateAsString = getBool("dateAsString", true);
/* 1061 */     extGrid.columnsModel = getString("columnsModel");
/* 1062 */     extGrid.columnsDefine = getString("columnsDefine");
/* 1063 */     extGrid.pageSize = getString("pageSize");
/* 1064 */     extGrid.autoExpandColumn = getString("autoExpandColumn");
/* 1065 */     extGrid.disableSelection = getString("disableSelection");
/* 1066 */     extGrid.enableColumnHide = getString("enableColumnHide");
/* 1067 */     extGrid.enableColumnMove = getString("enableColumnMove");
/* 1068 */     extGrid.enableColumnResize = getString("enableColumnResize");
/* 1069 */     extGrid.enableHdMenu = getString("enableHdMenu");
/* 1070 */     extGrid.hideHeaders = getString("hideHeaders");
/* 1071 */     extGrid.stripeRows = getString("stripeRows");
/* 1072 */     extGrid.trackMouseOver = getString("trackMouseOver");
/* 1073 */     extGrid.downloadMode = getString("downloadMode");
/* 1074 */     extGrid.downloadFilename = getString("downloadFilename");
/* 1075 */     extGrid.submitMode = getString("submitMode");
/* 1076 */     extGrid.onCellClick = getString("onCellClick");
/* 1077 */     extGrid.onCellDblClick = getString("onCellDblClick");
/* 1078 */     extGrid.onRowClick = getString("onRowClick");
/* 1079 */     extGrid.onRowDblClick = getString("onRowDblClick");
/* 1080 */     extGrid.onClick = getString("onClick");
/* 1081 */     extGrid.onDoubleClick = getString("onDoubleClick");
/* 1082 */     return extGrid;
/*      */   }
/*      */ 
/*      */   private ExtTree getExtTree() {
/* 1086 */     ExtTree extTree = new ExtTree();
/* 1087 */     setExtPanelProperty(extTree);
/* 1088 */     extTree.singleExpand = getString("singleExpand");
/* 1089 */     extTree.lines = getString("lines");
/* 1090 */     extTree.enableDD = getString("enableDD");
/* 1091 */     extTree.useArrows = getString("useArrows");
/* 1092 */     extTree.animate = getString("animate");
/* 1093 */     extTree.data = getString("data");
/* 1094 */     extTree.query = getString("query");
/* 1095 */     extTree.submitMode = getString("submitMode");
/* 1096 */     extTree.submitDisable = getString("submitDisable");
/* 1097 */     extTree.canEdit = getBool("canEdit", false);
/* 1098 */     extTree.expandAll = getBool("expandAll", false);
/* 1099 */     extTree.remoteTree = (!this.createBody);
/* 1100 */     extTree.remoteUrl = getString("remoteUrl");
/* 1101 */     extTree.remoteParams = getString("remoteParams");
/* 1102 */     extTree.showMessage = getString("showMessage");
/* 1103 */     extTree.sourceType = getString("sourceType");
/* 1104 */     extTree.fieldCount = getString("fieldCount");
/* 1105 */     extTree.onClick = getString("onClick");
/* 1106 */     extTree.onDblClick = getString("onDblClick");
/* 1107 */     extTree.onCheckChange = getString("onCheckChange");
/* 1108 */     extTree.onTextChange = getString("onTextChange");
/* 1109 */     extTree.onDragdrop = getString("onDragdrop");
/* 1110 */     extTree.onAppend = getString("onAppend");
/* 1111 */     extTree.onRemove = getString("onRemove");
/* 1112 */     extTree.onBeforeLoad = getString("onBeforeLoad");
/* 1113 */     extTree.onAfterLoad = getString("onAfterLoad");
/* 1114 */     extTree.onLoadException = getString("onLoadException");
/* 1115 */     extTree.showAddress = getBool("showAddress", false);
/* 1116 */     extTree.showTool = getBool("showTool", false);
/* 1117 */     return extTree;
/*      */   }
/*      */ 
/*      */   private ExtViewPort getExtViewPort() {
/* 1121 */     ExtViewPort extViewPort = new ExtViewPort();
/* 1122 */     extViewPort.layout = getString("layout");
/* 1123 */     extViewPort.onResize = getString("onResize");
/* 1124 */     return extViewPort;
/*      */   }
/*      */ 
/*      */   private void setExtFieldProperty(ExtField extField) {
/* 1128 */     extField.hideLabel = getString("hideLabel");
/* 1129 */     extField.labelSeparator = getString("labelSeparator");
/* 1130 */     extField.autoShow = getBool("autoShow", true);
/* 1131 */     extField.msgTarget = getString("msgTarget");
/* 1132 */     extField.onSpecialkey = getString("onSpecialkey");
/* 1133 */     extField.onChange = getString("onChange");
/* 1134 */     extField.onResize = getString("onResize");
/* 1135 */     extField.onBlur = getString("onBlur");
/* 1136 */     extField.onFocus = getString("onFocus");
/*      */   }
/*      */ 
/*      */   private void setExtTextFieldProperty(ExtTextField extTextField) {
/* 1140 */     extTextField.emptyText = getString("emptyText");
/* 1141 */     extTextField.allowBlank = getString("allowBlank");
/* 1142 */     extTextField.blankText = getString("blankText");
/* 1143 */     extTextField.inputType = getString("inputType");
/* 1144 */     extTextField.maxLength = getString("maxLength");
/* 1145 */     extTextField.maxLengthText = getString("maxLengthText");
/* 1146 */     extTextField.minLength = getString("minLength");
/* 1147 */     extTextField.minLengthText = getString("minLengthText");
/* 1148 */     extTextField.vtype = getString("vtype");
/* 1149 */     extTextField.vtypeText = getString("vtypeText");
/* 1150 */     extTextField.validator = getString("validator");
/* 1151 */     extTextField.invalidText = getString("invalidText");
/* 1152 */     extTextField.selectOnFocus = getString("selectOnFocus");
/*      */   }
/*      */ 
/*      */   private ExtTextField getExtTextField() {
/* 1156 */     ExtTextField extTextField = new ExtTextField();
/* 1157 */     setExtFieldProperty(extTextField);
/* 1158 */     setExtTextFieldProperty(extTextField);
/* 1159 */     return extTextField;
/*      */   }
/*      */ 
/*      */   private ExtHtmlEditor getExtHtmlEditor() {
/* 1163 */     ExtHtmlEditor extHtmlEditor = new ExtHtmlEditor();
/* 1164 */     setExtFieldProperty(extHtmlEditor);
/* 1165 */     return extHtmlEditor;
/*      */   }
/*      */ 
/*      */   private void setExtComboBoxProperty(ExtComboBox extComboBox) {
/* 1169 */     extComboBox.data = getString("data");
/* 1170 */     extComboBox.store = getString("store");
/* 1171 */     extComboBox.loadStore = getString("loadStore");
/* 1172 */     extComboBox.query = getString("query");
/* 1173 */     extComboBox.mapKey = getString("mapKey");
/* 1174 */     extComboBox.fields = getString("fields");
/* 1175 */     extComboBox.dateAsString = getBool("dateAsString", true);
/* 1176 */     extComboBox.createHidden = getBool("createHidden", false);
/* 1177 */     extComboBox.displayField = getString("displayField");
/* 1178 */     extComboBox.hintExpress = getString("hintExpress");
/* 1179 */     extComboBox.mustSelection = getString("mustSelection");
/* 1180 */     extComboBox.forceSelection = getString("forceSelection");
/* 1181 */     extComboBox.typeAhead = getString("typeAhead");
/* 1182 */     extComboBox.hideTrigger = getString("hideTrigger");
/* 1183 */     extComboBox.minChars = getString("minChars");
/* 1184 */     extComboBox.pageSize = getString("pageSize");
/* 1185 */     extComboBox.loadingText = getString("loadingText");
/* 1186 */     extComboBox.selectOnFocus = getString("selectOnFocus");
/* 1187 */     extComboBox.mode = getString("mode");
/* 1188 */     extComboBox.valueField = getString("valueField");
/* 1189 */     extComboBox.listWidth = getString("listWidth");
/* 1190 */     extComboBox.editable = getString("editable");
/* 1191 */     extComboBox.resizable = getString("resizable");
/* 1192 */     extComboBox.onBeforeSelect = getString("onBeforeSelect");
/* 1193 */     extComboBox.onSelect = getString("onSelect");
/* 1194 */     extComboBox.onBeforeQuery = getString("onBeforeQuery");
/* 1195 */     extComboBox.onExpand = getString("onExpand");
/* 1196 */     extComboBox.onCollapse = getString("onCollapse");
/*      */   }
/*      */ 
/*      */   private ExtComboBox getExtComboBox() {
/* 1200 */     ExtComboBox extComboBox = new ExtComboBox();
/* 1201 */     setExtFieldProperty(extComboBox);
/* 1202 */     setExtTextFieldProperty(extComboBox);
/* 1203 */     setExtComboBoxProperty(extComboBox);
/* 1204 */     return extComboBox;
/*      */   }
/*      */ 
/*      */   private ExtNumber getExtNumber() {
/* 1208 */     ExtNumber extNumber = new ExtNumber();
/* 1209 */     setExtFieldProperty(extNumber);
/* 1210 */     setExtTextFieldProperty(extNumber);
/* 1211 */     extNumber.allowDecimals = getString("allowDecimals");
/* 1212 */     extNumber.allowNegative = getString("allowNegative");
/* 1213 */     extNumber.decimalPrecision = getString("decimalPrecision");
/* 1214 */     extNumber.maxValue = getString("maxValue");
/* 1215 */     extNumber.maxText = getString("maxText");
/* 1216 */     extNumber.minValue = getString("minValue");
/* 1217 */     extNumber.minText = getString("minText");
/* 1218 */     return extNumber;
/*      */   }
/*      */ 
/*      */   private ExtTime getExtTime() {
/* 1222 */     ExtTime extTime = new ExtTime();
/* 1223 */     setExtFieldProperty(extTime);
/* 1224 */     setExtTextFieldProperty(extTime);
/* 1225 */     setExtComboBoxProperty(extTime);
/* 1226 */     extTime.increment = getString("increment");
/* 1227 */     extTime.format = getString("format");
/* 1228 */     return extTime;
/*      */   }
/*      */ 
/*      */   private ExtDate getExtDate() {
/* 1232 */     ExtDate extDate = new ExtDate();
/* 1233 */     setExtFieldProperty(extDate);
/* 1234 */     setExtTextFieldProperty(extDate);
/* 1235 */     extDate.startDate = getString("startDate");
/* 1236 */     extDate.endDate = getString("endDate");
/* 1237 */     extDate.disabledDates = getString("disabledDates");
/* 1238 */     extDate.disabledDatesText = getString("disabledDatesText");
/* 1239 */     extDate.disabledDays = getString("disabledDays");
/* 1240 */     extDate.disabledDaysText = getString("disabledDaysText");
/* 1241 */     extDate.timeControl = getString("timeControl");
/* 1242 */     extDate.format = getString("format");
/* 1243 */     extDate.showToday = getString("showToday");
/* 1244 */     extDate.onSelect = getString("onSelect");
/* 1245 */     return extDate;
/*      */   }
/*      */ 
/*      */   private void setExtCheckBoxProperty(ExtCheckBox extCheckBox) {
/* 1249 */     setExtFieldProperty(extCheckBox);
/* 1250 */     extCheckBox.boxLabel = getString("boxLabel");
/* 1251 */     extCheckBox.inputValue = getString("inputValue");
/* 1252 */     extCheckBox.checked = getString("checked");
/* 1253 */     extCheckBox.onCheck = getString("onCheck");
/* 1254 */     extCheckBox.data = getString("data");
/* 1255 */     extCheckBox.query = getString("query");
/* 1256 */     extCheckBox.allowBlank = getString("allowBlank");
/* 1257 */     extCheckBox.blankText = getString("blankText");
/* 1258 */     extCheckBox.columns = getString("columns");
/* 1259 */     extCheckBox.vertical = getString("vertical");
/* 1260 */     extCheckBox.submitMode = getString("submitMode");
/*      */   }
/*      */ 
/*      */   private ExtCheckBox getExtCheckBox() {
/* 1264 */     ExtCheckBox extCheckBox = new ExtCheckBox();
/* 1265 */     setExtCheckBoxProperty(extCheckBox);
/* 1266 */     return extCheckBox;
/*      */   }
/*      */ 
/*      */   private ExtRadioBox getExtRadioBox() {
/* 1270 */     ExtRadioBox extRadioBox = new ExtRadioBox();
/* 1271 */     setExtCheckBoxProperty(extRadioBox);
/* 1272 */     return extRadioBox;
/*      */   }
/*      */ 
/*      */   private ExtValidator getExtValidator() {
/* 1276 */     ExtValidator extValidator = new ExtValidator();
/* 1277 */     extValidator.onValidate = getString("onValidate");
/* 1278 */     return extValidator;
/*      */   }
/*      */ 
/*      */   private ExtLabel getExtLabel() {
/* 1282 */     ExtLabel extLabel = new ExtLabel();
/* 1283 */     extLabel.html = getString("html");
/* 1284 */     extLabel.align = getString("align");
/* 1285 */     extLabel.autoWidth = getString("autoWidth");
/* 1286 */     extLabel.focus = getString("focus");
/* 1287 */     return extLabel;
/*      */   }
/*      */ 
/*      */   private ExtSlider getExtSlider() {
/* 1291 */     ExtSlider extSlider = new ExtSlider();
/* 1292 */     extSlider.minValue = getString("minValue");
/* 1293 */     extSlider.maxValue = getString("maxValue");
/* 1294 */     extSlider.increment = getString("increment");
/* 1295 */     extSlider.keyIncrement = getString("keyIncrement");
/* 1296 */     extSlider.vertical = getString("vertical");
/* 1297 */     extSlider.animate = getString("animate");
/* 1298 */     extSlider.clickToChange = getString("clickToChange");
/* 1299 */     extSlider.onChange = getString("onChange");
/* 1300 */     extSlider.onChangeComplete = getString("onChangeComplete");
/* 1301 */     return extSlider;
/*      */   }
/*      */ 
/*      */   private Edit getEdit() {
/* 1305 */     Edit edit = new Edit();
/* 1306 */     edit.inputType = getString("inputType");
/* 1307 */     edit.setValue(getString("value"));
/* 1308 */     return edit;
/*      */   }
/*      */ 
/*      */   private Button getButton() {
/* 1312 */     Button button = new Button();
/* 1313 */     button.type = getString("type");
/* 1314 */     button.onClick = getString("onClick");
/* 1315 */     return button;
/*      */   }
/*      */ 
/*      */   private Flash getFlash() {
/* 1319 */     Flash flash = new Flash();
/* 1320 */     flash.src = getString("src");
/* 1321 */     flash.params = getString("params");
/* 1322 */     flash.hspace = getString("hspace");
/* 1323 */     flash.vspace = getString("vspace");
/* 1324 */     flash.align = getString("align");
/* 1325 */     flash.quality = getString("quality");
/* 1326 */     flash.loop = getString("loop");
/* 1327 */     flash.play = getString("play");
/* 1328 */     flash.scale = getString("scale");
/* 1329 */     return flash;
/*      */   }
/*      */ 
/*      */   private Script getScript() {
/* 1333 */     Script script = new Script();
/* 1334 */     script.setHeader(getString("header"));
/* 1335 */     script.setFooter(getString("footer"));
/* 1336 */     return script;
/*      */   }
/*      */ 
/*      */   private ExtScript getExtScript() throws Exception {
/* 1340 */     ExtScript extScript = new ExtScript();
/* 1341 */     extScript.setHeaderScript(getString("header"));
/* 1342 */     extScript.setFooterScript(getString("hooter"));
/* 1343 */     extScript.setExtHeaderScript(getString("extHeader"));
/* 1344 */     extScript.setExtFooterScript(getString("extFooter"));
/* 1345 */     extScript.setFirstExtScript(getString("extFirst"));
/* 1346 */     extScript.setLastExtScript(getString("extLast"));
/* 1347 */     return extScript;
/*      */   }
/*      */ 
/*      */   private Label getLabel() {
/* 1351 */     Label label = new Label();
/* 1352 */     label.key = getString("key");
/* 1353 */     label.focus = getString("focus");
/* 1354 */     return label;
/*      */   }
/*      */ 
/*      */   private Method getMethod() {
/* 1358 */     Method method = new Method();
/* 1359 */     method.methodName = getString("methodName");
/* 1360 */     return method;
/*      */   }
/*      */ 
/*      */   private Image getImage() {
/* 1364 */     Image image = new Image();
/* 1365 */     image.src = getString("src");
/* 1366 */     return image;
/*      */   }
/*      */ 
/*      */   private Select getSelect() {
/* 1370 */     Select select = new Select();
/* 1371 */     select.query = getString("query");
/* 1372 */     select.sorted = getString("sorted");
/* 1373 */     select.size = getString("size");
/* 1374 */     select.multiple = getString("multiple");
/* 1375 */     return select;
/*      */   }
/*      */ 
/*      */   private Link getLink() {
/* 1379 */     Link link = new Link();
/* 1380 */     link.href = getString("href");
/* 1381 */     link.target = getString("target");
/* 1382 */     link.key = getString("key");
/* 1383 */     return link;
/*      */   }
/*      */ 
/*      */   private Timer getTimer() {
/* 1387 */     Timer timer = new Timer();
/* 1388 */     timer.interval = getString("interval");
/* 1389 */     timer.timeOut = getString("timeOut");
/* 1390 */     timer.onTimer = getString("onTimer");
/* 1391 */     return timer;
/*      */   }
/*      */ 
/*      */   private DataProvider getDataProvider() {
/* 1395 */     String dt = getString("dbType");
/* 1396 */     if ((!StringUtil.isEmpty(dt)) && (!StringUtil.isSame(dt, this.dbType)))
/* 1397 */       return null;
/* 1398 */     DataProvider dataProvider = new DataProvider();
/* 1399 */     dataProvider.sql = getNativeString("sql");
/* 1400 */     dataProvider.totalSql = getNativeString("totalSql");
/* 1401 */     dataProvider.jndi = getString("jndi");
/* 1402 */     dataProvider.format = getString("format");
/* 1403 */     dataProvider.metaData = getString("metaData");
/* 1404 */     dataProvider.colDefine = getString("colDefine");
/* 1405 */     dataProvider.sortFields = getString("sortFields");
/* 1406 */     dataProvider.paging = getBool("paging", true);
/* 1407 */     dataProvider.simple = getBool("simple", false);
/* 1408 */     dataProvider.htmlStyle = getString("htmlStyle");
/* 1409 */     dataProvider.reportTitle = getString("reportTitle");
/* 1410 */     dataProvider.executeMode = getBool("executeMode", false);
/* 1411 */     dataProvider.ignoreBlob = getBool("ignoreBlob", false);
/* 1412 */     dataProvider.dateAsString = getBool("dateAsString", true);
/* 1413 */     return dataProvider;
/*      */   }
/*      */ 
/*      */   private boolean isExtControl(String name)
/*      */   {
/* 1418 */     return (name != null) && (name.length() > 3) && 
/* 1418 */       (name.substring(0, 3).equals("ext"));
/*      */   }
/*      */ 
/*      */   private void parseFile(String fileName) throws Exception {
/* 1422 */     String ext = FileUtil.extractFileExt(fileName);
/* 1423 */     if (ext.equalsIgnoreCase("xwl"))
/* 1424 */       new Parser(this.webPath, FileUtil.getFullFile(this.webPath, fileName), 
/* 1425 */         this.request, this.response, fileName).parseSimple();
/* 1426 */     else if (StringUtil.stringInList(StringUtil.split(this.request.getAttribute(
/* 1427 */       "sys.webFile").toString(), ","), ext.toLowerCase()) != -1)
/* 1428 */       this.request.getRequestDispatcher(fileName).forward(this.request, this.response);
/*      */     else
/* 1430 */       FileUtil.printFile(getWriter(), FileUtil.getFullFile(this.webPath, 
/* 1431 */         fileName));
/*      */   }
/*      */ 
/*      */   private void output(String text) throws Exception {
/* 1435 */     if (!StringUtil.isEmpty(text))
/* 1436 */       getWriter().println(text);
/*      */   }
/*      */ 
/*      */   private void closeAllObject(HttpServletRequest request, boolean isExcept) {
/* 1440 */     Enumeration enums = request.getAttributeNames();
/*      */ 
/* 1443 */     while (enums.hasMoreElements()) {
/* 1444 */       String attrName = enums.nextElement().toString();
/* 1445 */       Object object = request.getAttribute(attrName);
/* 1446 */       if (object != null)
/* 1447 */         if ((object instanceof ResultSet)) {
/* 1448 */           if (DbUtil.closeResultSet((ResultSet)object))
/* 1449 */             request.removeAttribute(attrName);
/* 1450 */         } else if ((object instanceof Connection)) {
/* 1451 */           if (DbUtil.closeConnection((Connection)object, isExcept))
/* 1452 */             request.removeAttribute(attrName); 
/*      */         } else {
/* 1453 */           if ((!(object instanceof InputStream)) || 
/* 1454 */             (!SysUtil.closeInputStream((InputStream)object))) continue;
/* 1455 */           request.removeAttribute(attrName);
/*      */         }
/*      */     }
/*      */   }
/*      */ }

/* Location:           Z:\EXT\WebBuilderServer (1)\WEB-INF\lib\webbuilder2.jar
 * Qualified Name:     com.webbuilder.common.Parser
 * JD-Core Version:    0.6.0
 */