package com.webbuilder.controls;

import com.webbuilder.utils.StringUtil;
import java.util.ArrayList;
import java.util.Iterator;
import javax.servlet.http.Cookie;
public class Body extends Control
{
  public String hdScript;
  public String ftScript;
  public String extHdScript;
  public String extFtScript;
  public String firstExtScript;
  public String lastExtScript;
  public String head = "";
  public String prefix = "";
  public String userCssFiles = "";
  public String userJsFiles = "";
  public String onResize = "";
  public String onBeforeUnload = "";
  public String onUnload = "";
  public boolean loadLib = true;
  public boolean showHint = false;
  public boolean createBody = true;
  private ArrayList<String> cssFiles;
  private ArrayList<String> jsFiles;

  public Body()
    throws Exception
  {
    this.cssFiles = new ArrayList();
    this.jsFiles = new ArrayList();
  }

  public void addCSSFile(String fileName) {
    if (this.cssFiles.indexOf(fileName) == -1)
      this.cssFiles.add(fileName);
  }

  public void addJSFile(String fileName) {
    if (this.jsFiles.indexOf(fileName) == -1)
      this.jsFiles.add(fileName);
  }

  protected void descript() throws Exception {
    if (!this.createBody) {
      setHeaderScript(this.hdScript);
      setFooterScript(this.ftScript);
      setExtHeaderScript(this.extHdScript);
      setExtFooterScript(this.extFtScript);
      setLastExtScript(this.lastExtScript);
      setFirstExtScript(this.firstExtScript);
      return;
    }

    setHeader(this.prefix);
    setHeader("<html>");
    setHeader("<head>");
    setHeader("<meta http-equiv=\"content-type\" content=\"text/html;charset=utf-8\">");
    setHeader("<title>" + getCaption() + "</title>");
    if (this.loadLib) {
      addCSSFile("webbuilder/css/wbstyle.css");
      addCSSFile("webbuilder/controls/ext/resources/css/ext-all.css");
     
      Cookie[] myCookie=request.getCookies();
      int n=0;
      String  user=(String)request.getSession().getAttribute("sys.user");
      String path=null;
      int count=1;
      if(myCookie!=null)
      count= myCookie.length;
      	for(;n<count-1;n++)//设立一个循环，来访问Cookie对象数组的每一个元素
      	{
      		Cookie newCookie= myCookie[n];

      		if(newCookie.getName().equals(user)) //判断元素的值是否为username中的值
      		{
      			path=newCookie.getValue();
      			break;
      		}
      	}
      	int io=0;
        if(path==null||path.equals("")) {
      	  io=4;
        }
        else if(path.equals("0"))	
      {addCSSFile("webbuilder/controls/ext/resources/css/gtheme.css");
      	io=0;
      }
      else  if (path.equals("1")) {
       addCSSFile("webbuilder/controls/ext/resources/css/xtheme-gray.css");
       io=1;
      }
      else  if (path.equals("2")) {
          addCSSFile("webbuilder/controls/ext/resources/css/xtheme-slate.css");
          io=2;
       }
      else  if (path.equals("3")) {
          addCSSFile("webbuilder/controls/ext/resources/css/xtheme-slickness.css");
          io=3;
      } else  if (path.equals("4")) {
       
          io=4;
      }
      setHeader("<script> var  oi ="+io+" ;</script>");
      addCSSFile("webbuilder/script/Datetime/datetime.css");
      addJSFile("webbuilder/controls/ext/wbext-all.js");
      addJSFile("webbuilder/script/wbutil.js");
      addJSFile("webbuilder/controls/fc/fc.js");
    }
    StringUtil.mergeStringToList(this.userCssFiles, this.cssFiles);
    StringUtil.mergeStringToList(this.userJsFiles, this.jsFiles);
    Iterator css = this.cssFiles.iterator();
    Iterator js = this.jsFiles.iterator();
    while (css.hasNext()) {
      setHeader("<link rel=\"stylesheet\" href=\"" + (String)css.next() + 
        "\" type=\"text/css\">");
    }
    while (js.hasNext()) {
      setHeader("<script type=\"text/javascript\" src=\"" + (String)js.next() + 
        "\"></script>");
    }
    setHeader("<script type='text/javascript'>  var arr = window.parent.document.cookie.match(new RegExp('(^| )mystock-xtheme=([^;]*)(;|$)'));if (arr != null ){var aTheme =  arr[2].replace('../../','');if (arr != null) Ext.util.CSS.swapStyleSheet('theme', aTheme);}</script>");
    setHeader(this.head);
    setHeader("</head>");
    setStyleProperty();
    setClassProperty();
    setTagProperty();
    if (!StringUtil.isEmpty(this.onResize))
      setHeaderScript("function window__onresize(){" + this.onResize + 
        "}window.onresize=window__onresize;");
    if (!StringUtil.isEmpty(this.onBeforeUnload))
      setHeaderScript("function window__onbeforeunload(){" + 
        this.onBeforeUnload + 
        "}window.onbeforeunload=window__onbeforeunload;");
    if (!StringUtil.isEmpty(this.onUnload))
      setHeaderScript("function window__onunload(){" + this.onUnload + 
        "}window.onunload=window__onunload;");
    setHeaderScript(this.hdScript);
    setFooterScript(this.ftScript);
    setExtHeaderScript(this.extHdScript);
    setExtFooterScript(this.extFtScript);
    setLastExtScript(this.lastExtScript);
    setHeaderProperty("body");
    if (this.loadLib) {
      setFirstExtScript("Ext.BLANK_IMAGE_URL=\"webbuilder/images/s.gif\";");
      if (this.showHint) {
        setFirstExtScript("Ext.QuickTips.init();");
        setFirstExtScript("Ext.form.Field.prototype.msgTarget=\"qtip\";");
      }
      String timeout = fetchString("sys.ajaxTimeout");
      if (!StringUtil.isEmpty(timeout))
        if (timeout.equals("-")) {
          setFirstExtScript("Ext.Ajax.timeout=2147483647;");
        }
        else
          setFirstExtScript("Ext.Ajax.timeout=" + timeout + ";");
    }
    setFirstExtScript(this.firstExtScript);
    setFooter("</html>");
    setFooter("</body>");
  }
}