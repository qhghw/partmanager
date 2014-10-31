<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>工作台</title>
 <link rel="stylesheet" type="text/css" href="${ctx}css/common.css">
 <link rel="stylesheet" type="text/css" href="${ctx}ext/resources/css/ext-all.css">
 <link rel="stylesheet" type="text/css" href="${ctx}css/ext-icon.css">
 <script type="text/javascript" src="${ctx}ext/adapter/ext/ext-base.js"></script>
 <script type="text/javascript" src="${ctx}ext/ext-all.js"></script>
 <script type="text/javascript" src="${ctx}ext/ext-all-debug.js"></script>
 <script type="text/javascript" src="${ctx}ext/ext-lang-zh_CN.js"></script>
 <script type="text/javascript">
 var _noticeTitle = new Ext.form.TextField({
		id : "_noticeTitle",
		fieldLabel : '标题',
		readonly:true,
		name : '_noticeTitle',
		maxLength : 250,
		anchor : '95%'
	});
var _noticeContent = new Ext.form.HtmlEditor({
		id : "_noticeContent",
		fieldLabel : '内容',
		readonly:true,
		name : '_noticeContent',
		height:250,
		width:800,
		maxLength : 1000,
		anchor : '95%'
	});
var noticeform_ = new Ext.form.FormPanel({
	id:"noticeform",
	bodyStyle : 'padding:5px 3px 0;',
	labelAlign : 'left',
	labelWidth : 80,
	border : false,	
	editable:false,
	layout : 'form',
	items : [{
	     layout : 'column',	
	     border : false,
		 items:[{  
			columnWidth : 1,  
			layout : 'form',  
			border : false,
			items : [ _noticeTitle] 
		},{  
			columnWidth : 1,  
			layout : 'form',  
			border : false,
			items : [ _noticeContent] 
		}  ]
	}]
});
var noticepanel_ = new Ext.Panel({
 layout:'border',
 items: [{
     title: '详细信息',
     region: 'center',
     height: 300,
     items:[noticeform_]
 }]
});

var win_notice_=new Ext.Window({
 closable:true,      
 closeAction:'hide',
 width:800,
 height:400,
 plain : true,
	modal : true,
 border:false,
 layout : 'fit',
 items:[noticepanel_],
 listeners:{
		hide :function(window){
			noticeform_.getForm().reset();
		}}
});

var showNoticeForm = function(obj){
	_noticeTitle.setValue(obj.name); 
	_noticeContent.setValue(obj.title);
	win_notice_.show();
};



function update_win(instid,taskid,businesscod,businessurl,taskdefid,flowtype,startUser)  {
	var ireportmod;
	if(flowtype=="一般领料"){
    	ireportmod= "outapply";
    	}else if(flowtype=="工具领用"){
    	    ireportmod= "toolapply";
    	}else if(flowtype=="办公用品"){
    	    ireportmod= "officeapply";
    	}else if(flowtype=="物料申请"){
    	    ireportmod= "maiterialapply";
    	}else if(flowtype=="工具借用"){
    	    ireportmod= "toolbapply";
    	}
	flowtype=ireportmod;
var win_dotask = new Ext.Window({  
 title: '办理工作',  
 resizable:      false, //变大小   
 width: 800,  
 height:490,  
 modal:          true,//为模式窗口，后面的内容都不能操作(屏蔽)   
 plain:true,  
 bodyStyle:'padding:0px;',  
 buttonAlign:'center',  
 html:'<iframe id=thisIframe width=790 height=455 frameborder=0 scrolling=auto src=${ctx}main?action=webbuilder/application/partmanager/agentProcess/indexAgentProcess.xwl&processinstid='+instid+'&taskid='+taskid+'&businessurl='+encodeURIComponent(businessurl)+'&taskdefid='+taskdefid+'&flowtype='+flowtype+'&startUser='+startUser+'></iframe>'  
});  
//var iframeObj = document.getElementById("thisIframe");    
//alert(iframeObj);    
 //iframeObj.src="index.jsp";      
 win_dotask.show();
 } 
 
function download_win(id)  {
	var win_dotask = new Ext.Window({  
	 title: '文件下载',  
	 resizable:      false, //变大小   
	 width: 800,  
	 height:490,  
	 modal:          true,//为模式窗口，后面的内容都不能操作(屏蔽)   
	 plain:true,  
	 bodyStyle:'padding:0px;',  
	 buttonAlign:'center',  
	 html:'<iframe id=thisIframe width=790 height=455 frameborder=0 scrolling=auto src=${ctx}/main?action=webbuilder/application/partmanager/filemanage/downloadfile.xwl&id='+id+'></iframe>'  
	});  
//var iframeObj = document.getElementById("thisIframe");    
//alert(iframeObj);    
 //iframeObj.src="index.jsp";      
 win_dotask.show();
 } 
 
 function download_file(id){
	 window.location.href = "servlet/download?id="+id;
 }
 </script>
</head>
<body>
<div id="content" style="width:1002px;height:432px;background:none transparent scroll repeat 0% 0%;margin:10px auto">
			
	<!-- HTML编辑区 -->
	<!--第一块编辑区-->
	<div id="pdv_12174" class="pdv_class" title="" style="width:45%;height:50px;top:0px;left:0; z-index:3">
		<img src="${ctx}img/main/message-notice.jpg" border="0" width="100%"/>
	</div>
	
	<!-- HTML编辑区 -->
	
	<div id="pdv_12513" class="pdv_class" title="" style="width:45%;height:120px;top:48px;left:0px; z-index:14">
	
	<table style="align:center">
		<thead>
			<tr>
				<th style="width:350px;"><font style="font-weight:bold">标题</font></th>
				<!-- <th style="width:100px"><font style="font-weight:bold">内容</font></th> -->
				<th style="width:100px;"><font style="font-weight:bold">发布人</font></th>
				<th style="width:100px"><font style="font-weight:bold">发布日期</font></th>
				<th style="width:100px"><font style="font-weight:bold">截止日期</font></th>
				<th style="width:50px"><font style="font-weight:bold">查看</font></th>
			</tr>
		</thead>
		<tbody>
		<s:iterator value="announces">
		<tr >
			<td style="width:350px">${title}</td>
			<%-- <td style="width:100px">${content}</td> --%>
			<td style="width:100px">${activeman}</td>
			<td style="width:100px">${activedate}</td>
			<td style="width:100px">${unactivedate}</td>
			<td style="width:10%;"><a href="#" title="${title}" name="${content}"  onclick="showNoticeForm(this)"><img src="img/main/enter.png" height="16px" width="16px"/></a></td>
		</tr>
		</s:iterator>
		</tbody>
	</table>
	
	</div>
	
	<div id="pdv_12514" class="pdv_class" title="" style="width:40px;height:10px;top:14px;left:40%; z-index:15">
		<a href="main?action=webbuilder/application/partmanager/announce/announce.xwl"><img src="${ctx}img/main/more.gif" border="0" width="100%" /></a>
	</div>
	<!--第二块编辑区-->
	<div id="pdv_12174" class="pdv_class" title="" style="width:45%;height:50px;top:0px;left:50%; z-index:3">
		<img src="${ctx}img/main/message-usually.jpg" border="0" width="100%"/>
	</div>
	
	<div id="pdv_12513" class="pdv_class" title="" style="width:45%;height:120px;top:48px;left:50%; z-index:14">
	
	<table >
		<thead>
			<tr>
 
				<th style="width:100px;"><font style="font-weight:bold">流程名称</font></th>
 				<th style="width:100px;"><font style="font-weight:bold">发起人</font></th>
				<th style="width:250px;"><font style="font-weight:bold">发起时间</font></th>
				<th style="width:50px"><font style="font-weight:bold">办理</font></th>
			</tr>
		</thead>
		<tbody>
		<s:iterator value="agentProcess">
		<tr>
			<td style="width:45%;border-bottom:1px dashed #686565">${instname}</td>
			<td style="width:10%;border-bottom:1px dashed #686565">${startuser}</td>
			<td style="width:35%;border-bottom:1px dashed #686565">${createtime}</td> 
			<td style="width:10%;border-bottom:1px dashed #686565"><a href="#" onclick='update_win("${processinstid}","${taskid}","${businesscod}","${businessurl}","${taskdefid}","${flowtype}","${startuser}");' style="text-decoration:none;"><img src="img/main/enter.png" height="16px" width="16px"/></a></td>
		</tr>
		 </s:iterator>
		</tbody>
	</table>
	
	</div>
	
	<div id="pdv_12514" class="pdv_class" title="" style="width:40px;height:10px;top:14px;left:90%; z-index:15">
		<a href="main?action=webbuilder/application/partmanager/agentProcess/agentProcess.xwl"><img src="${ctx}img/main/more.gif" border="0" width="100%"/></a>
	</div>
	
	
	<!--第三快编辑区-->
	<div id="pdv_12174" class="pdv_class" title="" style="width:45%;height:50px;top:280px;left:50%; z-index:3">
		<img src="${ctx}img/main/bussiness-help.jpg" border="0" width="100%" />
	</div>
	
	<!-- HTML编辑区 -->
	
	<div id="pdv_12513" class="pdv_class" title="" style="width:45%;height:120px;top:290px;left:50%; z-index:14">
	
	
	
	</div>
	
	<div id="pdv_12514" class="pdv_class" title="" style="width:40px;height:10px;top:290px;left:90%; z-index:15">
		<a href="#"><img src="${ctx}img/main/more.gif" border="0" width="100%" /></a>
	</div>
	
	<!--第四快编辑区-->
	<div id="pdv_12174" class="pdv_class" title="" style="width:45%;height:50px;top:280px;left:0px; z-index:3">
		<img src="${ctx}img/main/message-download.jpg" border="0" width="100%" />
	</div>
	
	<!-- HTML编辑区 -->
	
	<div id="pdv_12513" class="pdv_class" title="" style="width:45%;height:120px;top:72%;left:0px; z-index:14">
	
	<table>
		<thead>
			<tr style="align:center">
				<th style="width:30%"><font style="font-weight:bold">标题</font></th>
				<th style="width:50%"><font style="font-weight:bold">描述</font></th>
				<th style="width:18%"><font style="font-weight:bold">录入时间</font></th>
				
			</tr>
		</thead>
		<tbody>
		<s:iterator value="filedetails">
		<tr>
			<td style="width:30%;border-bottom:1px dashed #686565">${filetype}</td>
			<td style="width:50%;border-bottom:1px dashed #686565">${filedescribe}</td> 
			<td style="width:18%;border-bottom:1px dashed #686565">${recordtim}</td>
			<td style="width:10%;border-bottom:1px dashed #686565"><a href="#" onclick='download_win("${id}");' style="text-decoration:none;"><img src="img/main/enter.png" height="16px" width="16px"/></a></td>
		</tr>
		 </s:iterator>
		</tbody>
	</table>
	
	</div>
	
	<div id="pdv_12514" class="pdv_class" title="" style="width:40px;height:10px;top:290px;left:40%; z-index:15">
		<a href="main?action=webbuilder/application/partmanager/filemanage/filemanageDown.xwl"><img src="${ctx}img/main/more.gif" border="0" width="100%"/></a>
	</div>
	
	</div>
</body>
</html>