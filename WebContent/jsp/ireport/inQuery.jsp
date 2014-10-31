<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page
    import="java.io.*,
            net.sf.jasperreports.engine.*,
            net.sf.jasperreports.engine.util.*,
            java.util.*,java.sql.*,
            net.sf.jasperreports.engine.export.*,
            com.webbuilder.utils.DbUtil,com.partmanager.biz.power.dto.UserDTO,com.partmanager.utils.system.Constants"%>
            
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<%
    File reportFile = new File(application
            .getRealPath("/jsp/ireport/inQuery.jasper"));
    JasperReport jasperReport = (JasperReport) JRLoader
            .loadObject(reportFile.getPath());
    Map parameters = new HashMap();
    parameters.put("begTim", request.getParameter("begTim")==null?"":request.getParameter("begTim"));
    parameters.put("endTim", request.getParameter("endTim")==null?"":request.getParameter("endTim"));
       
    parameters.put("storeName",  request.getParameter( "storeName")==null?"":request.getParameter( "storeName"));
    UserDTO userInfo = (UserDTO) request.getSession().getAttribute(Constants.USERINFO);
    parameters.put("UserName",  userInfo.getUsername());
    Connection conn = null;
	String jndi = "java:comp/env/jdbc/mysql";
    conn = DbUtil.getConnection(jndi);
    //装载jasper文件application
    File exe_rpt = new File(application.getRealPath("/jsp/ireport/inQuery.jasper"));
   

    try{
     // fill
     JasperPrint jasperPrint = JasperFillManager.fillReport(exe_rpt.getPath(),parameters,conn);
    
     // 生成pdf
     byte[] bytes = JasperRunManager.runReportToPdf(exe_rpt.getPath(),parameters,conn);

     response.reset();
     response.setCharacterEncoding("utf-8");
     response.setContentType("application/pdf"); //文件类型contenttype
     response.setContentLength(bytes.length);
  	 OutputStream ous = new BufferedOutputStream(response.getOutputStream());
     ous.write(bytes,0,bytes.length);	

	 ous.flush();
	 ous.close();
	       
	       
     conn.close();
 }catch(JRException ex){
     out.print("Jasper Output Error:"+ex.getMessage());
    }
%>
</body>
</html>