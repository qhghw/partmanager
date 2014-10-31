package com.webbuilder.controls;

import com.webbuilder.utils.DateUtil;
import com.webbuilder.utils.DbUtil;
import com.webbuilder.utils.StringUtil;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

public class DataProvider extends Control
{
  public String sql = "";
  public String totalSql = "";
  public String jndi = "";
  public String format = "";
  public String metaData = "";
  public String colDefine = "";
  public String sortFields = "";
  public String htmlStyle = "";
  public String reportTitle = "";
  public boolean simple = false;
  public boolean ignoreBlob = false;
  public boolean executeMode = false;
  public boolean dateAsString = true;
  public boolean paging = true;

  protected void descript() throws Exception
  {
    int pagedRecords = Integer.parseInt(this.request.getAttribute(
      "sys.pagedRecords").toString());
    if (pagedRecords == -1)
      pagedRecords = 2147483647;
    String orderExpress = (String)this.request.getAttribute("sql.orderExpress");
    if (orderExpress == null)
      orderExpress = "";
    if ((this.request.getParameter("sort") != null) && 
      (this.request.getParameter("dir") != null))
    {
      String dir;
      
      if (StringUtil.isSame(this.request.getParameter("dir"), "DESC"))
        dir = " desc";
      else
        dir = "";
      this.request.setAttribute("sql.orderExpress", " order by " + 
        DbUtil.addTablePrefix(this.sortFields, this.request
        .getParameter("sort"), false) + dir);
    } else {
      this.request.setAttribute("sql.orderExpress", orderExpress);
    }this.sql = StringUtil.replaceParameters(this.request, this.sql);
    if (StringUtil.isEmpty(this.sql))
      return;
    String start = null; String limit = null;

    Query query = new Query();
    ResultSet resultSet = null;

    if (StringUtil.isSame(this.format, "xml")) {
      this.response.reset();
      this.response.setContentType("text/xml;charset=utf-8");
    }
    int limitValue;
    int startValue;
    String sqlgxw=this.sql;

    if (this.paging) {
      start = this.request.getParameter("start");
      limit = this.request.getParameter("limit");
      if ((start == null) || (limit == null) || 
        (StringUtil.isSame(this.format, "array")))
        this.paging = false;

      if (start == null)
        startValue = 1;
      else
        startValue = Integer.parseInt(start) + 1;
    
      if (limit == null)
        limitValue = 2147483647 - startValue;
      else
        limitValue = Integer.parseInt(limit);
    } else {
      startValue = 1;
      limitValue = 2147483646;
      String sqlLink=this.sql;
      
      //sqlLink= "  select rownum   rowno ,"+sqlLink.substring(this.sql.toLowerCase().indexOf("select")+6, sqlLink.length());
  
     // sqlLink=sqlLink.replace("SELECT", " SELECT  ROWNUM  ROWNO ,");
     // sqlgxw="select  rownum  rn  ,gxw.*  from (  "+sqlLink+" )  gxw  where ROWNO  between {#start#}+1  and  {#start#}+{#limit#}"; //oracle
      sqlgxw=sqlLink +" LIMIT  {#start#}  , {#limit#}"; //mysql
      System.out.println("---------------------  sqlgxw  "+sqlgxw);
    }
    if (this.executeMode)
      query.type = "execute";
    query.setRequest(this.request);
    query.sql = sqlgxw;
    query.jndi = this.jndi;
    query.setName("query." + this.name);
    if (!StringUtil.isEmpty(this.htmlStyle)) {
      query.create();
      if (StringUtil.isSame(this.htmlStyle, "browse"))
        setHeader(getBrowseHtml(
          (ResultSet)this.request
          .getAttribute("query." + this.name)));
      if (StringUtil.isSame(this.htmlStyle, "report"))
        setHeader(getReportHtml(
          (ResultSet)this.request
          .getAttribute("query." + this.name)));
      return;
    }
    if (this.simple) {
      query.create();
      setHeader(getSimpleJson((ResultSet)this.request.getAttribute("query." + 
        this.name)));
      return;
    }
    ResultSet totalSet;

    if (!this.executeMode)
      totalSet = getTotalSet();
    else
      totalSet = null;
    query.create();

    Object retObj = this.request.getAttribute("return");
    if ((this.executeMode) && (retObj != null)) {
      if (retObj instanceof ResultSet) {
        resultSet = (ResultSet)retObj; 
      }
      setHeader("{total:1,metaData:{totalProperty:\"total\",root:\"row\",fields:[{name:\"result\",type:\"string\"}]},colDefine:[{dataIndex:\"result\",header:\"返回结果\",width:300}],row:[{result:\"" + 
        retObj.toString() + "\"}]}");
      return;
    }

    Object obj = this.request.getAttribute("query." + this.name);
    if (obj instanceof ResultSet) {
      resultSet = (ResultSet)obj;
    } else {
      setHeader("{total:1,metaData:{totalProperty:\"total\",root:\"row\",fields:[{name:\"result\",type:\"string\"}]},colDefine:[{dataIndex:\"result\",header:\"影响记录\",width:130}],row:[{result:" + 
        obj.toString() + "}]}");
      return;
    }

    if ((!StringUtil.isEmpty(this.format)) && 
      (StringUtil.stringInList(StringUtil.split("json,xml,array", 
      ","), this.format) == -1)) {
      if (StringUtil.isSame(this.format, "blob"))
         DbUtil.outputBlob(resultSet, this.request, this.response);
      else
        DbUtil.outputImage(resultSet, this.request, this.response, this.format);
      return;
    }
    if (StringUtil.isSame(this.format, "xml")) {
      setHeader(DbUtil.getDataXml(resultSet, totalSet, startValue, 
        limitValue, this.ignoreBlob, pagedRecords));
    } else if (StringUtil.isSame(this.format, "array")) {
      ResultSetMetaData meta = resultSet.getMetaData();
      setHeader(DbUtil.getDataArray(resultSet, meta, this.ignoreBlob));
    } else {
      Object cm = this.request.getAttribute(this.colDefine);
      if (cm != null)
        this.colDefine = cm.toString();
      setHeader(DbUtil.getDataJson(resultSet, totalSet, startValue, 
        limitValue, this.metaData, this.colDefine, this.dateAsString, this.ignoreBlob, 
        pagedRecords));
    }

  }

  private String getBrowseHtml(ResultSet rs) throws SQLException {
    StringBuilder express = new StringBuilder();
    ResultSetMetaData meta = rs.getMetaData();
    int k = 0;
    boolean isFirst = true;
    String ss = this.request.getParameter("start");
    String se = this.request
      .getParameter("end");
    int start;

    if (!StringUtil.isEmpty(ss))
      start = Integer.parseInt(ss);
    else
      start = 0;
    int end;

    if (!StringUtil.isEmpty(se))
      end = Integer.parseInt(se);
    else
      end = 2147483647;
    int j = meta.getColumnCount();
    express
      .append("<html><head><meta http-equiv=\"content-type\" content=\"text/html;charset=utf-8\"><link rel=\"stylesheet\" href=\"webbuilder/css/wbstyle.css\" type=\"text/css\"></head><body style=\"line-height:1.6\" class=\"sys_normal\">");

    while (rs.next()) {
      ++k;
      if (k < start)
        continue;
      if (k > end)
        break;
      if (isFirst)
        isFirst = false;
      else {
        express.append("<hr size='1' noshade='noshade' style='border:1px #cccccc dotted;'>");
      }
      express.append("<p align='right'>第 " + Integer.toString(k) + " 条");
      for (int i = 0; i < j - 1; ++i) {
        express.append("&nbsp;&nbsp;");
        express.append(StringUtil.toHTML(DbUtil.getFieldValue(rs, meta, 
          i + 1, false)));
      }
      express.append("</p><p>");
      express.append(rs.getString(j));
      express.append("</p>");
    }
    express.append("</body></html>");
    return express.toString();
  }

  private String getReportHtml(ResultSet rs) throws Exception {
    StringBuilder express = new StringBuilder(); StringBuilder data = new StringBuilder(); StringBuilder keys = new StringBuilder();
    ResultSetMetaData meta = rs.getMetaData();

    int w = 0;

    HashMap map = new HashMap();
    JSONArray cols;

    if ((StringUtil.isEmpty(this.colDefine)) || (this.colDefine.equals("-"))) {
      cols = new JSONArray(
        "[" + 
        removeRenderer(DbUtil.getResultSetMeta(meta, 
        true, false)) + "]");
    } else {
      Object cm = this.request.getAttribute(this.colDefine);
      if (cm != null)
        this.colDefine = cm.toString();
      cols = new JSONArray("[" + removeRenderer(this.colDefine) + "]");
    }
    express
      .append("<html><head><meta http-equiv=\"content-type\" content=\"text/html;charset=utf-8\"><title>");
    express.append(this.reportTitle);
    express
      .append("</title><link rel=\"stylesheet\" href=\"webbuilder/css/wbstyle.css\" type=\"text/css\">");
    express
      .append("<style type=\"text/css\"> table {border-collapse:collapse;border:solid #999;border-width:1px 0 0 1px;} table th, table td {border:solid #999;border-width:0 1px 1px 0;padding:2px;}</style>");
    express.append("</head><body class=\"sys_normal\">");
    int j = cols.length();
    boolean[] visiFields = new boolean[j];
    String[] names = new String[j];
    String[] formats = new String[j];
    String[] mks = new String[j];
    String[] align = new String[j];
    for (int i = 0; i < j; ++i) {
      JSONObject obj = cols.getJSONObject(i);
      if (obj.has("hidden")) {
        visiFields[i] = ((StringUtil.getStringBool(obj
          .getString("hidden"))) ? false: true);
      }
      else
        visiFields[i] = true;
      mks[i] = null;
      align[i] = null;
      if (visiFields[i] != false)
      {
        String width;
  
        if (obj.has("width"))
          width = obj.getString("width");
        else
          width = "130";
        w += Integer.parseInt(width);
        data.append("<td height='22' width='" + width + "'>");
        data.append(StringUtil.toHTML(obj.getString("header")));
        data.append("</td>");
        names[i] = obj.getString("dataIndex");
        if (obj.has("format"))
          formats[i] = obj.getString("format");
        else
          formats[i] = null;
        if ((obj.has("align")) && 
          (!StringUtil.isEmpty(obj.getString("align"))))
          align[i] = obj.getString("align");
        if ((!obj.has("mapKey")) || 
          (StringUtil.isEmpty(obj.getString("mapKey")))) continue;
        if (!StringUtil.isEmpty(keys.toString()))
          keys.append(",");
        mks[i] = obj.getString("mapKey");
        keys.append(Integer.toString(i + 1) + "=" + mks[i]);
      }
      else {
        formats[i] = null;
      }
    }
    HashMap valMap;

    if (!StringUtil.isEmpty(keys.toString()))
      valMap = DbUtil.initTextMaps(this.request, keys.toString());
    else
      valMap = null;
    data.append("</tr>");
    int n = meta.getColumnCount();
    while (rs.next()) {
      data.append("<tr>");
      map.clear();
      for (int m = 0; m < n; ++m) {
        int tp = meta.getColumnType(m + 1);
        String label = meta.getColumnLabel(m + 1);
        if ((tp == 93) || (tp == 91)) {
          Date date = rs.getTimestamp(m + 1);
          if (StringUtil.isSame(DateUtil.formatDate(date, "HHmmss"), 
            "000000"))
            map.put(label, DateUtil.formatStdDate(date));
          else
            map.put(label, DateUtil.dateToString(date));
        } else {
          map.put(label, rs.getString(m + 1));
        }
      }
      for (int i = 0; i < j; ++i)
        if (visiFields[i] != false) {
          if (StringUtil.isEmpty(formats[i])) {
            if (StringUtil.isEmpty(align[i]))
              data.append("<td height='22'>");
            else
              data.append("<td height='22' align='" + align[i] + 
                "'>");
            if ((valMap != null) && (mks[i] != null))
              data.append(StringUtil.toHTML((String)valMap.get(
                Integer.toString(i + 1) + 
                "_" + (String)map.get(names[i]))));
            else
              data.append(StringUtil.toHTML((String)map.get(names[i])));
          } else {
            data.append("<td height='22' align='right'>");
            data.append(StringUtil.toHTML(StringUtil.formatFloat(
              (String)map.get(names[i]), formats[i])));
          }
          data.append("</td>");
        }
      data.append("</tr>");
    }
    data.append("</table></body></html>");
    String width = Integer.toString(w);
    express
      .append("<p class=\"font_bigbold\" align=\"center\" style=\"width:" + 
      width + "\">");
    express.append(StringUtil.toHTML(this.reportTitle));
    express.append("</p>");
    express.append("<table width='" + width + 
      "' cellpadding='0' class='sys_normal' cellspacing='0'><tr>");
    express.append(data.toString());
    return express.toString();
  }

  private String getSimpleJson(ResultSet rs) throws SQLException {
    StringBuilder express = new StringBuilder();

    express.append("{");
    if (DbUtil.setResultSetToFirst(rs))
    {
      ResultSetMetaData meta = rs.getMetaData();

      int j = meta.getColumnCount();
      for (int i = 0; i < j; ++i) {
        if (i > 0)
          express.append(",");
        express.append("\"");
        express
          .append(
          StringUtil.toExpress(meta.getColumnLabel(i + 1)));
        express.append("\":\"");
        express.append(StringUtil.toExpress(DbUtil.getFieldValue(rs, 
          meta, i + 1, this.ignoreBlob)));
        express.append("\"");
      }
    }
    express.append("}");
    return express.toString();
  }

  private String removeRenderer(String exp)
  {
    int p;
    while ((p = exp.indexOf(",renderer:function(")) != -1)
    {
   
      int i = exp.indexOf("},{id:", p);
      if (i == -1)
        i = exp.length() - 1;
      exp = exp.substring(0, p) + exp.substring(i);
    }
    return exp;
  }

  private ResultSet getTotalSet() throws Exception {
    if (!StringUtil.isEmpty(this.totalSql)) {
      Query total = new Query();
      total.setRequest(this.request);
      total.jndi = this.jndi;
      total.setName("total." + this.name);
      if (StringUtil.isEqual(this.totalSql, "-")) {
        String tsql = StringUtil.replaceParameters(this.request, this.sql);
        total.sql = DbUtil.getCountSql(tsql);
        try {
          total.create();
        } catch (Exception e) {
          total.sql = ("select count(*) from (" + tsql + ")");
          total.create();
        }
      } else {
        total.sql = this.totalSql;
        total.create();
      }
      return fetchResultSet("total." + this.name);
    }
    return null;
  }
}