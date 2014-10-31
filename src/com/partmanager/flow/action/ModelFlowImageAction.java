package com.partmanager.flow.action;

import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.bpmn.diagram.ProcessDiagramGenerator;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.struts2.ServletActionContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.partmanager.action.BaseAction;
import com.webbuilder.utils.StringUtil;
@SuppressWarnings("serial")
public class ModelFlowImageAction extends BaseAction {
	  /**
	   * 读取带跟踪的图片
	   */
	  public void getProcessTask() throws Exception {
		   HttpServletRequest request = ServletActionContext.getRequest();
		   HttpServletResponse response = ServletActionContext.getResponse();
		  	ServletContext sc = request.getSession().getServletContext();
			WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(sc);
			RuntimeService runtimeService =(RuntimeService) ctx.getBean("runtimeService");
			HistoryService historyService =(HistoryService) ctx.getBean("historyService");
			RepositoryService repositoryService =(RepositoryService) ctx.getBean("repositoryService");
			String processInstanceId = StringUtil.fetchString(request, "processinstid");
		  ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		  BpmnModel bpmnModel;
		  InputStream imageStream;
		  if(processInstance==null){
			  HistoricProcessInstance  hisProcessInstance=historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
			   bpmnModel = repositoryService.getBpmnModel(hisProcessInstance.getProcessDefinitionId());
			   imageStream=ProcessDiagramGenerator.generatePngDiagram(bpmnModel);
		  }else{
			  bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
			  List<String> activeActivityIds = runtimeService.getActiveActivityIds(processInstanceId);
			  imageStream = ProcessDiagramGenerator.generateDiagram(bpmnModel, "png", activeActivityIds);
		  }
		  
	    // 输出资源内容到相应对象
		  byte[] b = new byte[1024];
		  int len;
		  while ((len = imageStream.read(b, 0, 1024)) != -1) {
			  response.getOutputStream().write(b, 0, len);
		  }
	  }
}
