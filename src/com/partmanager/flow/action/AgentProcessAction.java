package com.partmanager.flow.action;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.partmanager.biz.ziliao.AnnounceBiz;
import com.partmanager.flow.entity.AgentProcess;
import com.partmanager.flow.entity.Variable;
import com.webbuilder.utils.StringUtil;
import com.xuelang.common.web.ConnectDB;


/**
 * 流程模型控制器
 * 
 * @author henryyan
 */

public class AgentProcessAction {
	
	private AnnounceBiz announceBiz;

	protected Logger logger = LoggerFactory.getLogger(getClass());

  //节点设置用户
	public void listProcess(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		List<AgentProcess> list = new ArrayList<AgentProcess>();
		Connection connect = ConnectDB.getConnection();
		try {
			Statement st = connect.createStatement();
			String username = StringUtil.fetchString(request, "sys.username");
			String userrole = StringUtil.fetchString(request, "sys.userrole");
			StringBuffer sb = new StringBuffer();
			sb.append("select task.id_ taskid,task.proc_inst_id_ procinstid,task.task_def_key_ taskdefkey,task.proc_def_id_ procdefid,inst.START_USER_ID_ startuser,varurl.TEXT_ businessurl,var.TEXT_ flowtype,varcod.TEXT_ businesscod, "+
					" task.name_ taskname,task.assignee_ assignee,task.create_time_ createtime,inst.business_key_ businesskey ,deploy.NAME_ instname "+
					" from act_ru_task task join act_hi_procinst inst on inst.proc_inst_id_= task.proc_inst_id_   "+
					" join act_re_procdef def on def.id_=inst.PROC_DEF_ID_ join act_re_deployment deploy on deploy.ID_=def.DEPLOYMENT_ID_ "+
					" left join act_hi_varinst var on var.PROC_INST_ID_=inst.ID_ and var.NAME_='FlowType' "+
					"left join act_hi_varinst varcod on varcod.PROC_INST_ID_=inst.ID_ and varcod.NAME_='businessTableKeyValue'"+
					"left join act_hi_varinst varurl on varurl.PROC_INST_ID_=inst.ID_ and varurl.NAME_='businessUrl'"+
					" where task.assignee_ in ('"+username+"','"+userrole+"') order by task.create_time_  desc ");
			
			ResultSet rs = st.executeQuery(sb.toString());
			while(rs.next()){
				AgentProcess ap = new AgentProcess();
				ap.setTaskid(rs.getString("taskid"));
				ap.setAssignee(rs.getString("assignee"));
				ap.setBusinesskey(rs.getString("businesskey"));
				ap.setProcessinstid(rs.getString("procinstid"));
				ap.setTaskname(rs.getString("taskname"));
				ap.setCreatetime(rs.getString("createtime"));
				ap.setInstname(rs.getString("instname"));
				ap.setStartuser(rs.getString("startuser"));
				ap.setFlowtype(rs.getString("flowtype"));
				ap.setTaskdefid(rs.getString("taskdefkey"));
				ap.setBusinesscod(rs.getString("businesscod"));
				ap.setBusinessurl(rs.getString("businessurl"));
				list.add(ap);
			}
			st.close();
			connect.close();
			
			StringBuilder buf = new StringBuilder();
			if(list.size()==0){
				buf.append("{total:0,row:[]}");
			}else{
				buf.append("{total:" + list.size());
				buf.append(",row:[");
				for(int i=0;i<list.size();i++){
					Map map = new HashMap();
					map.put("taskid", list.get(i).getTaskid());
					map.put("assignee", list.get(i).getAssignee());
					map.put("businesskey", list.get(i).getBusinesskey());
					map.put("assigneename",list.get(i).getInstname()+"【单号:"+list.get(i).getBusinesscod()+"】");
					map.put("processinstid", list.get(i).getProcessinstid());
					map.put("taskname", list.get(i).getTaskname());
					map.put("createtime", list.get(i).getCreatetime());
					map.put("flowtype", list.get(i).getFlowtype());
					map.put("taskdefid", list.get(i).getTaskdefid());
					map.put("businessurl", list.get(i).getBusinessurl());
					net.sf.json.JSONObject json = net.sf.json.JSONObject.fromObject(map);
					buf.append(json.toString());
					buf.append(",");
				}
				buf.delete(buf.lastIndexOf(","),buf.length());
				buf.append("]}");
			}
	    	response.getWriter().write(buf.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
  	}
	
	//获取流程实例业务数据
	public Map<String,String> getInstVar(HistoryService historyService,String processinstid){
		 Map<String,String> map = new HashMap<String,String>();
		//List<HistoricDetail> formProperties = historyService.createHistoricDetailQuery().processInstanceId(processinstid).formProperties().list();
		Connection connect = ConnectDB.getConnection();
		try {
			String sql ="SELECT NAME_ instname,TEXT_ instvalue FROM act_hi_varinst where PROC_INST_ID_='"+processinstid+"' and task_id_ is null";
			Statement st = connect.createStatement();
			ResultSet rs = st.executeQuery(sql);
			while(rs.next()){
			    	 map.put(rs.getString("instname"),rs.getString("instvalue"));
			    }
			st.close();
			connect.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				connect.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		 return map;
	}
	
	
	public void agreeApply(HttpServletRequest request, HttpServletResponse response)
			throws Exception{
		String flowtype = StringUtil.fetchString(request, "flowtype");
		if(flowtype.equals("MAPPLY")||flowtype.equals("NAPPLY")||flowtype.equals("TAPPLY")||flowtype.equals("PARTSSTOCK")||flowtype.equals("WASTEAPPLY")||flowtype.equals("DEVICEPLAN")){
			agreeOutApply(request,response);
		}else{
			//需要进一步处理
			agreeOutApply(request,response);
		}
	}
	
	
	//工具借用。设备采购。一般采购。盘点流程。设备报废。检修计划
	public void agreeOutApply(HttpServletRequest request, HttpServletResponse response)
			throws Exception{
			ServletContext sc = request.getSession().getServletContext();
			WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(sc);
			//定义业务表数据 
			HistoryService historyService = (HistoryService) ctx.getBean("historyService");
			Variable variable = new Variable();
		 	Map<String, Object> variables = variable.getVariableMap();
		 	//定义查询参数
		 	String taskid = StringUtil.fetchString(request, "taskid");
		 	String taskdefid = StringUtil.fetchString(request, "taskdefid");
		 	String opinion = StringUtil.fetchString(request, "opinion");
		 	String processinstid = StringUtil.fetchString(request, "processinstid");
		 	String startUser = StringUtil.fetchString(request, "startUser");
		 	String isold = StringUtil.fetchString(request, "isold");
		 	if("tostore".equals(taskdefid)){
		 		if(isold.equals("1")){
			 		variables.put("isold", true);
			 	}else{
			 		variables.put("isold", false);
			 	}
		 	}
		 	variables.put("startUser", startUser);
		 	if("tobackstore".equals(taskdefid)){
			 		variables.put("isback", false);
		 	}
		 	variables.put("isback", false);
			 Map<String,String> map = new HashMap<String,String>();
			 //获取业务信息
			 map=getInstVar(historyService,processinstid);
			//流程参数信息
			 map.put("taskid", taskid);
			 map.put("processinstid", processinstid);
			 //流程办理意见
			 map.put("opinion", opinion);
			
			 doApply(request,response,map,variables);
	}
	
	//通用同意处理
	public void doApply(HttpServletRequest request, HttpServletResponse response, Map<String,String> map,Map<String, Object> variables)
			throws Exception {
		ServletContext sc = request.getSession().getServletContext();
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(sc);
		TaskService taskService = (TaskService) ctx.getBean("taskService");
		String username = StringUtil.fetchString(request, "sys.username");
		String taskid=map.get("taskid");
		 taskService.claim(taskid, username);
		Task task = taskService.createTaskQuery().taskId(taskid).list().get(0);
		//处理通过意见
		taskService.addComment(taskid, task.getProcessInstanceId(), map.get("opinion"));
		String taskcode = task.getTaskDefinitionKey();
			//设置通过
			variables.put(taskcode, true);
			taskService.setVariablesLocal(taskid, variables);
			
			Connection connect = ConnectDB.getConnection();
			try {
				taskService.complete(taskid, variables);
				String sql ="select id_ from act_hi_procinst where end_time_ is not null and proc_inst_id_='"+map.get("processinstid")+"'";
				Statement st = connect.createStatement();
				ResultSet rs = st.executeQuery(sql);
				if(map.get("businessTable").equals("t_parts_in")&&(task.getTaskDefinitionKey().equals("bmbzsh")||task.getTaskDefinitionKey().equals("rkbgszr"))){
					CallableStatement    cstmt = connect.prepareCall("{call PROC_PARTS_IN(?)}");
				   cstmt.setString(1, map.get("businessTableKeyValue"));
				   cstmt.execute();//执行
				   cstmt.close();
				}
				while(rs.next()){
					Statement st_ = connect.createStatement();
					/*	if(map.get("businessTable").equals("t_parts_in")&&task.getTaskDefinitionKey().equals("bmbzsh")){
						CallableStatement    cstmt = connect.prepareCall("{call PROC_PARTS_IN(?)}");
					   cstmt.setString(1, map.get("businessTableKeyValue"));
					   cstmt.execute();//执行
					   cstmt.close();
					}*/
			    	String update = "update "+map.get("businessTable")+" set WF_STATUE='2' where "+map.get("businessTableKey")+"='"+map.get("businessTableKeyValue")+"'";
			    	st_.executeUpdate(update);
			    	
			    	st_.close();
				    }
				st.close();
				connect.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	
	//通用驳回处理
	public void disagreeApply(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ServletContext sc = request.getSession().getServletContext();
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(sc);
		TaskService taskService = (TaskService) ctx.getBean("taskService");
		RepositoryService repositoryService = (RepositoryService) ctx.getBean("repositoryService");
		RuntimeService runtimeService = (RuntimeService) ctx.getBean("runtimeService");
		ProcessBackService processBackService=(ProcessBackService) ctx.getBean("processBackService");
		processBackService.setBeans(repositoryService, runtimeService, taskService);
		String username = StringUtil.fetchString(request, "sys.username");
		Variable variable = new Variable();
		 	Map<String, Object> variables = variable.getVariableMap();
		 	String taskdefid = StringUtil.fetchString(request, "taskdefid");
		 	String taskid = StringUtil.fetchString(request, "taskid");
		 	String backtaskid = StringUtil.fetchString(request, "backtaskid");
		 	String opinion = StringUtil.fetchString(request, "opinion");
		 	taskService.claim(taskid, username);
		 	if(taskdefid.equals("tobackstore")){
		 		variables.put("isback", true);
		 	}
		 	Task task = taskService.createTaskQuery().taskId(taskid).list().get(0);
		 	//处理驳回意见
		 	taskService.addComment(taskid, task.getProcessInstanceId(), opinion);
		 	String taskcode = task.getTaskDefinitionKey();
				variables.put(taskcode, false);
				taskService.setVariablesLocal(taskid, variables);
				 //获取业务信息
				try {
					processBackService.backPreProcess(taskid,backtaskid,variables);
			} catch (Exception e) {
				e.printStackTrace();
			}
				
	}
	
	//通用驳回处理
		public void disagreeEndApply(HttpServletRequest request, HttpServletResponse response)
				throws Exception {
			ServletContext sc = request.getSession().getServletContext();
			WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(sc);
			TaskService taskService = (TaskService) ctx.getBean("taskService");
			RepositoryService repositoryService = (RepositoryService) ctx.getBean("repositoryService");
			RuntimeService runtimeService = (RuntimeService) ctx.getBean("runtimeService");
			HistoryService historyService = (HistoryService) ctx.getBean("historyService");
			ProcessBackService processBackService=(ProcessBackService) ctx.getBean("processBackService");
			processBackService.setBeans(repositoryService, runtimeService, taskService);
			String username = StringUtil.fetchString(request, "sys.username");
			Variable variable = new Variable();
			 	Map<String, Object> variables = variable.getVariableMap();
			 	String taskid = StringUtil.fetchString(request, "taskid");
			 	String opinion = StringUtil.fetchString(request, "opinion");
			 	String processinstid = StringUtil.fetchString(request, "processinstid");
			 	taskService.claim(taskid, username);
			 	Task task = taskService.createTaskQuery().taskId(taskid).list().get(0);
			 	//处理驳回意见
			 	taskService.addComment(taskid, task.getProcessInstanceId(), opinion);
			 	String taskcode = task.getTaskDefinitionKey();
					variables.put(taskcode, false);
					taskService.setVariablesLocal(taskid, variables);
					Map<String,String> map = new HashMap<String,String>();
					 //获取业务信息
					 map=getInstVar(historyService,processinstid);
					try {
				  		try {
				  			processBackService.rejectAndEnd(taskid);
						} catch (Exception e) {
							e.printStackTrace();
						}
						Connection connect = ConnectDB.getConnection();
						Statement st_ = connect.createStatement();
						String update = "update "+map.get("businessTable")+" set WF_STATUE='3' where "+map.get("businessTableKey")+"='"+map.get("businessTableKeyValue")+"'";
						st_.executeUpdate(update);
						st_.close();
						connect.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
					
		}

	public void setAnnounceBiz(AnnounceBiz announceBiz) {
		this.announceBiz = announceBiz;
	}
	
	public AnnounceBiz getAnnounceBiz() {
		return announceBiz;
	}
  
}
