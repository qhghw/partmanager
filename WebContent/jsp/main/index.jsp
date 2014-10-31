<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<%@page import="com.partmanager.biz.power.dto.UserDTO;"%>
<%
  UserDTO userInfo=(UserDTO)session.getAttribute("userInfo");
%> 
<html>
  <head>
  	<title>设备管理系统</title>
    <script type="text/javascript">
	     window.log_id="<%=userInfo.getUserid()%>";
	     window.log_name="<%=userInfo.getUsername()%>";
	</script>
    <link rel="stylesheet" type="text/css" href="${ctx}ext/resources/css/ext-all.css">
    <link rel="stylesheet" type="text/css" href="${ctx}css/ext-icon.css">
    <script type="text/javascript" src="${ctx}ext/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="${ctx}ext/ext-all.js"></script>
    <script type="text/javascript" src="${ctx}ext/ext-lang-zh_CN.js"></script>
    <script type="text/javascript" src="${ctx}js/Clock.js"></script>
    <script type="text/javascript" src="index.js"></script>
    <script type="text/javascript" src="App.js"></script>
  </head>
  
  <body>
  </body>
</html>