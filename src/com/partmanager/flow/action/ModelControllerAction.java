package com.partmanager.flow.action;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
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

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.webbuilder.utils.DbUtil;
import com.webbuilder.utils.StringUtil;
import com.xuelang.common.web.ConnectDB;


/**
 * 流程模型控制器
 * 
 * @author henryyan
 */

public class ModelControllerAction {

  protected Logger logger = LoggerFactory.getLogger(getClass());

  public void Insert(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
				  Map map = request.getParameterMap();
				  String jndi = StringUtil.fetchString(request, "sys.jndi");
			        String tableName = StringUtil.fetchString(request, "tableName");
				  ConnectDB.setConnStr(jndi);
				  Connection conn = ConnectDB.connToDB();
					Statement st = conn.createStatement();
					ResultSet resultSet = st.executeQuery("describe "+tableName);
					String selectSql = "insert  into " + tableName + " ( " ;
					StringBuffer sql = new StringBuffer("");
					StringBuffer sqlValues = new StringBuffer("");
					while (resultSet.next()) {
						String cName = resultSet.getString("field");
						if(map.containsKey(cName)) {
							sql.append(cName+",");
							sqlValues.append("{?"+cName+"?},");
						}
					}
					  /*删除多余的逗号*/
		            sql.delete(sql.length()-1, sql.length());
					sql.append(") values(");
					sqlValues.delete(sqlValues.length()-1, sqlValues.length());
					sql.append(sqlValues.toString()+")");
		           System.out.println("-----------UPDATE-----------  "+selectSql + sql.toString());
		}

  /**
   * 创建模型
   */
  public void create(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
    try {
    	ServletContext sc = request.getSession().getServletContext();
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(sc);
		RepositoryService repositoryService = (RepositoryService) ctx.getBean("repositoryService");
      ObjectMapper objectMapper = new ObjectMapper();
      ObjectNode editorNode = objectMapper.createObjectNode();
      editorNode.put("id", "canvas");
      editorNode.put("resourceId", "canvas");
      ObjectNode stencilSetNode = objectMapper.createObjectNode();
      stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
      editorNode.put("stencilset", stencilSetNode);
      Model modelData = repositoryService.newModel();
      String model_name = StringUtil.fetchString(request, "name_");
      String model_key = StringUtil.fetchString(request, "key_");
      String model_description = StringUtil.fetchString(request, "description");
      ObjectNode modelObjectNode = objectMapper.createObjectNode();
      modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, model_name);
      modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
      String description = StringUtils.defaultString(model_description);
      modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
      modelData.setMetaInfo(modelObjectNode.toString());
      modelData.setName(model_name);
      modelData.setKey(StringUtils.defaultString(model_key));
      repositoryService.saveModel(modelData);
      repositoryService.addModelEditorSource(modelData.getId(), editorNode.toString().getBytes("utf-8"));
      StringBuffer sb = new StringBuffer();
      sb.append("{\"key\": \"");
      sb.append(modelData.getId());
      sb.append("\"}");
      response.setContentType("html/text");
      response.setHeader("Cache-Control", "no-cache");
      response.getWriter().write(sb.toString());
      response.getWriter().flush();
      //Struts2Utils.renderJson(sb);
     // response.sendRedirect(request.getContextPath() + "/service/editor?id=" + modelData.getId());
    } catch (Exception e) {
      logger.error("创建模型失败：", e);
    }
  }
  
  
  public void deploy(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
	  StringBuffer paramBuf=new StringBuffer();
      ServletContext sc =request.getSession().getServletContext(); 
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(sc);
		RepositoryService repositoryService = (RepositoryService) ctx.getBean("repositoryService");
      String grid = StringUtil.fetchString(request, "modelgrid");
      String modelId = null;
      JSONArray array=new JSONArray(grid);
      for(int i=0 ; i < array.length() ;i++)
      {
       //获取每一个JsonObject对象
       JSONObject myjObject = array.getJSONObject(i);
        modelId = myjObject.getString("id");
      }
      
	    try {
	      Model modelData = repositoryService.getModel(modelId);
	      ObjectNode modelNode = (ObjectNode) new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
	      byte[] bpmnBytes = null;

	      BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
	      bpmnBytes = new BpmnXMLConverter().convertToXML(model);
	      String processName = modelData.getName() + ".bpmn20.xml";
	      Deployment deployment = repositoryService.createDeployment().name(modelData.getName()).addString(processName, new String(bpmnBytes)).deploy();
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	paramBuf.append("流程部署失败");
	    }
  }
  
  
  
  public String convertBlobToString(java.sql.Blob blob){
		 String result = "";
		  try {
		   ByteArrayInputStream msgContent =(ByteArrayInputStream) blob.getBinaryStream();
		   byte[] byte_data = new byte[msgContent.available()];
		   msgContent.read(byte_data, 0,byte_data.length);
		   result = new String(byte_data);
		  } catch (SQLException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
		  }
		  return result;
		}
  
  
  //节点设置用户
  public void listNode(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
	  String modelid = StringUtil.fetchString(request, "model_id");
	  String jndi = StringUtil.fetchString(request, "sys.jndi");
	  request.setCharacterEncoding("UTF-8");
	 PrintWriter pw = response.getWriter();
	  ConnectDB.setConnStr(jndi);
	  Connection conn = ConnectDB.connToDB();
			try {
				String sql = "select bytes_ as jmodel from act_ge_bytearray agb join act_re_model arm on arm.EDITOR_SOURCE_VALUE_ID_=agb.id_ where arm.id_='"+modelid+"'";
				Statement st = conn.createStatement();
				ResultSet resultSet = st.executeQuery(sql);
				String jmodel ="";
				while(resultSet.next()){
					jmodel=this.convertBlobToString(resultSet.getBlob("jmodel"));
				}
				StringBuilder buf = new StringBuilder();
				int m=0;
		    	JSONObject  dataJson=new JSONObject(jmodel);
		    	JSONArray  data=dataJson.getJSONArray("childShapes");
		    	for(int i=0;i<data.length();i++){
		    		JSONObject info=data.getJSONObject(i);
		    		JSONObject stencil = info.getJSONObject("stencil");
	    			String userTask=stencil.getString("id");
	    			//过滤开始和结束
	    			/*if("UserTask".equals(userTask)||"StartNoneEvent".equals(userTask)){*/
	    			if("UserTask".equals(userTask)){
	    				String taskkey=info.getString("resourceId");
	    				Map map = new HashMap();
	    				JSONObject propertys = info.getJSONObject("properties");
	    				if(!propertys.get("usertaskassignment").equals("")){
		    				JSONObject usertaskassignment = propertys.getJSONObject("usertaskassignment");
		    				JSONArray  items=usertaskassignment.getJSONArray("items");
		    				for(int j=0;j<items.length();j++){
			    				JSONObject items_data=items.getJSONObject(j);
			    				String assignee =items_data.getString("assignment_type"); 
			    				if(assignee.equals("assignee")){
			    					String assign_user=items_data.getString("resourceassignmentexpr");
			    					map.put("nodeuser", assign_user);
			    					map.put("usertype", "1");
			    				}else if (assignee.equals("candidateUsers")){
			    					String candidate_user=items_data.getString("resourceassignmentexpr");
			    				}else if (assignee.equals("candidateGroup")){
			    					String assign_group=items_data.getString("resourceassignmentexpr");
			    					map.put("nodeuser", assign_group);
			    					map.put("usertype", "2");
			    				}
			    				
			    			}
	    			}
	    				map.put("nodename", String.valueOf(propertys.getString("name")));
	    				map.put("nodekey",taskkey);
	    				net.sf.json.JSONObject json = net.sf.json.JSONObject.fromObject(map);
	    				buf.append(json.toString());
	    				buf.append(",");
		    	}
		    	}
		    	if(buf.length()-1>0){
		    		buf.delete(buf.length()-1, buf.length());
				}
	    		StringBuffer sbf = new StringBuffer();
	    		sbf.append("{total:" + m);
	    		sbf.append(",row:[");
	    		sbf.append(buf.toString());
	    		sbf.append("]}");
		    	response.getWriter().write(sbf.toString());
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
}
  
  public void saveInst(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	  String grid = StringUtil.fetchString(request, "flowinstgrid");
	  JSONArray array=new JSONArray(grid);
	  Connection connect = ConnectDB.getConnection();
	     for(int i=0 ; i < array.length() ;i++)
	     {
	        //获取每一个JsonObject对象
	        JSONObject myjObject = array.getJSONObject(i);
	       String processtypekey = myjObject.getString("processtypekey");
	       String instid = myjObject.getString("instid");
	       
	       String sql = "update act_re_procdef set PROCESSTYPEKEY ='"+processtypekey+"' where id_='"+instid+"'";
	       Statement stt = connect.createStatement();
			stt.executeUpdate(sql);
			stt.close();
	     }   
	     connect.close();
  }
  
  public void setAssignee(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	  String grid = StringUtil.fetchString(request, "usergrid");
      String modelid = StringUtil.fetchString(request, "modelid");;
      Connection connect;
		try {
			 connect = ConnectDB.getConnection();
			String sql = "select bytes_ as jmodel from act_ge_bytearray agb join act_re_model arm on arm.EDITOR_SOURCE_VALUE_ID_=agb.id_ where arm.id_='"+modelid+"'";
			Statement st = connect.createStatement();
			ResultSet resultSet = st.executeQuery(sql);
			String jmodel ="";
			while(resultSet.next()){
				jmodel=this.convertBlobToString(resultSet.getBlob("jmodel"));
			}
	    	
	    	JSONObject  dataJson=new JSONObject(jmodel);
	    	JSONArray  data=dataJson.getJSONArray("childShapes");
	    	JSONArray array=new JSONArray(grid);
	        for(int i=0 ; i < array.length() ;i++)
	        {
	         //获取每一个JsonObject对象
	         JSONObject myjObject = array.getJSONObject(i);
	         String usertype = myjObject.getString("usertype");
	         String nodekey = myjObject.getString("nodekey");
	         String taskname=myjObject.getString("nodename");
	         String nodeuser=myjObject.getString("nodeuser");
	         int n=0;
	    	for(int k=0;k<data.length();k++){
	    		JSONObject info=data.getJSONObject(k);
	    		String taskkey=info.getString("resourceId");
	    		JSONObject stencil = info.getJSONObject("stencil");
  			//过滤开始和结束
	    		//过滤开始和结束
    			if("UserTask".equals(stencil.getString("id"))){
    				JSONObject propertys = info.getJSONObject("properties");
    				if(taskname.equals(propertys.getString("name"))){
    					propertys.remove("usertaskassignment");
    					Map<String,Object> itemsMap=new HashMap<String,Object>();
    					List<Map<String,Object>>itemList= new ArrayList<Map<String,Object>>();
    					Map<String,Object> assignMap;
    					if(usertype.equals("1")){
    						if(nodeuser!=null&&(!nodeuser.equals(""))){
        						assignMap=new HashMap<String,Object>();
        						assignMap.put("assignment_type", "assignee");
        						assignMap.put("resourceassignmentexpr", nodeuser);
        						itemList.add(assignMap);
        						n++;
        					} 
    					} else if(usertype.equals("2")){
    						if(nodeuser!=null&&(!nodeuser.equals(""))){
    						assignMap=new HashMap<String,Object>();
    						assignMap.put("assignment_type", "candidateGroups");
    						assignMap.put("resourceassignmentexpr", nodeuser);
    						itemList.add(assignMap);
    						n++;
    						}
    					}
    					
    					itemsMap.put("items", itemList);
    					itemsMap.put("totalCount", n);
    					propertys.put("usertaskassignment", itemsMap);
    				}
	    			}
	    	
	    		}
	    	
	    	
	        }
	        
	        resultSet.close();
	        st.close();
	        ByteArrayInputStream in = new ByteArrayInputStream(dataJson.toString().getBytes());
	        
	        try {
	        	String id_byte="select agb.id_ from act_ge_bytearray agb join act_re_model arm on arm.EDITOR_SOURCE_VALUE_ID_=agb.id_ where arm.id_='"+modelid+"'";
	        	Statement stt = connect.createStatement();
				ResultSet rs = stt.executeQuery(id_byte);
				String byte_id="";
				while(rs.next()){
					byte_id=rs.getString("id_");
				}
	        	String sql_str = "update act_ge_bytearray set bytes_=? where id_='"+byte_id+"' ";
	            PreparedStatement stmt = connect.prepareStatement(sql_str);
	            stmt.setBinaryStream(1, in, in.available());;
	            stmt.executeUpdate();
                in.close();
                rs.close();
                stmt.close();
                connect.close();
	        } catch (SQLException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        } catch (FileNotFoundException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        }catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
  	
}
  
  public void getPartTypeTreeJeson(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String id = request.getParameter("id");
		StringBuffer json = new StringBuffer("[");
		request.setCharacterEncoding("UTF-8");
		PrintWriter pw = response.getWriter();
		Connection conn = null;
		Statement s = null;
		ResultSet rs = null;
		String myJson = "";
		String jndi = StringUtil.fetchString(request, "sys.jndi");
		try {
			conn = DbUtil.getConnection(jndi);
			s = conn.createStatement();
			if (id == null || "".equals(id) || "tree__root".equals(id)) {
				id = "-1";
			}
			rs = s
					.executeQuery("    select type_id id, PARTS_TYPE text from   c_parts_type  where PARENT_ID='"
							+ id + "'  ");
			while (rs.next()) {
				json.append("{iconCls:\'icon_folder\',id:'" + rs.getString(1)
						+ "',text:'" + rs.getString(2) + "'},");
			}
			myJson = json.toString();
			if (myJson.endsWith(",")) {
				myJson = myJson.substring(0, myJson.lastIndexOf(","));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			if (s != null)
				s.close();
			if (conn != null)
				conn.close();
			myJson += "]";
			pw.write(myJson);
			pw.flush();
			pw.close();
		}

	}
  
  
  //流程发起处理公共类
  public void startFlow(IdentityService identityService,RuntimeService runtimeService,String username,String flowcode,Map<String, Object> variables){
	  try {
		  	Connection connect = ConnectDB.getConnection();
			Statement st = connect.createStatement();
			String sql ="select processinstid from ( select id_ as processinstid ,MAX(version_),PROCESSTYPEKEY from act_re_procdef   group by id_ ) def join c_code ac on def.PROCESSTYPEKEY=ac.sys_cod where ac.SYS_COD='"+flowcode+"' ";
			ResultSet rs = st.executeQuery(sql);
  	
			String processInstanceId = "";
			while(rs.next()){
				processInstanceId =rs.getString("processinstid");
			}
			identityService.setAuthenticatedUserId(username);
			ProcessInstance processInstance = runtimeService.startProcessInstanceById(processInstanceId, variables.get("businessTableKeyValue").toString(), variables);
			String update_sql="update "+variables.get("businessTable")+" set procdefid='"+processInstance.getProcessDefinitionId()+"',WF_STATUE='1',processinstid='"+processInstance.getProcessInstanceId()+"' where "+variables.get("businessTableKey")+"='"+variables.get("businessTableKeyValue")+"'";
			st.executeUpdate(update_sql);
					
		  } catch (ActivitiException e) {
		      if (e.getMessage().indexOf("no processes deployed with key") != -1) {
		        logger.warn("没有部署流程!", e);
		      } else {
		        logger.error("启动请假流程失败：", e);
		      }
		    } catch (Exception e) {
		      logger.error("启动请假流程失败：", e);
		    }
  }
  
  
  /**
   * 启动设备报废流程
   * 
   * @param leave
   */
  public void startWasteApplyFlow(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	  	ServletContext sc =request.getSession().getServletContext();  
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(sc);
		IdentityService identityService = (IdentityService) ctx.getBean("identityService");
		RuntimeService runtimeService =(RuntimeService) ctx.getBean("runtimeService");
	     String grid = StringUtil.fetchString(request, "grid");
	     String purNo = null;
	     JSONArray array=new JSONArray(grid);
	     for(int i=0 ; i < array.length() ;i++)
	     {
	        //获取每一个JsonObject对象
	        JSONObject myjObject = array.getJSONObject(i);
	        purNo = myjObject.getString("PUR_NO");
	     }   
		String username = StringUtil.fetchString(request, "sys.logincode");
		//流程Map
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("businessTable", "t_waste_apply");
		variables.put("businessTableKey", "PUR_NO");
		variables.put("FlowType", "WASTEAPPLY");
		variables.put("businessTableKeyValue", purNo);
		variables.put("businessUrl", "mydesk_wasteApplyReport.do?purno="+purNo);
		//业务处理Map
		Map<String, String> map= new HashMap<String, String>();
		map.put("username", username);
		startFlow(identityService,runtimeService,username,"WASTEAPPLY",variables);
}
  /*设备采购申请流程*/
  public void startApplyFlow(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	  
	     String grid = StringUtil.fetchString(request, "maingrid");
	     String datatype=StringUtil.fetchString(request, "dtType");
	     String purNo = null,outMod=null;
	     if(datatype.equals("设备")){
		 outMod="TAPPLY";
	     }else{
		 outMod="NAPPLY";
	     }
	     JSONArray array=new JSONArray(grid);
	     for(int i=0 ; i < array.length() ;i++)
	     {
	        //获取每一个JsonObject对象
	        JSONObject myjObject = array.getJSONObject(i);
	        purNo = myjObject.getString("PUR_NO");
	     }   
	    ServletContext sc =request.getSession().getServletContext();  
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(sc);
		IdentityService identityService = (IdentityService) ctx.getBean("identityService");
		RuntimeService runtimeService =(RuntimeService) ctx.getBean("runtimeService");
		try {
			Map<String, Object> variables = new HashMap<String, Object>();
			String username = StringUtil.fetchString(request, "sys.username");
				identityService.setAuthenticatedUserId(username);
				variables.put("businessTable", "t_apply");
				variables.put("businessTableKey", "PUR_NO");
				variables.put("businessTableKeyValue", purNo);
				variables.put("FlowType", outMod);
				variables.put("userName", username);
				if(datatype.equals("设备")){
				    	variables.put("businessUrl",  "mydesk_tdeviceReport.do?purno="+purNo);
				     }else{
					variables.put("businessUrl",  "mydesk_tapplyReport.do?purno="+purNo);
				     }
				Map<String, String> map= new HashMap<String, String>();
				map.put("username", username);
				startFlow(identityService,runtimeService,username,outMod,variables);
				
		} catch (ActivitiException e) {
	      if (e.getMessage().indexOf("no processes deployed with key") != -1) {
	        logger.warn("没有部署流程!", e);
	      } else {
	        logger.error("启动请假流程失败：", e);
	      }
	    } catch (Exception e) {
	      logger.error("启动请假流程失败：", e);
	    }
  }
 
  //一般采购流程
  public void startEquipApplyFlow(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	  
	     String grid = StringUtil.fetchString(request, "maingrid");
	     String purNo = null,outMod=null;
	     JSONArray array=new JSONArray(grid);
	     for(int i=0 ; i < array.length() ;i++)
	     {
	        //获取每一个JsonObject对象
	        JSONObject myjObject = array.getJSONObject(i);
	        purNo = myjObject.getString("PUR_NO");
	     }   
	    ServletContext sc =request.getSession().getServletContext();  
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(sc);
		IdentityService identityService = (IdentityService) ctx.getBean("identityService");
		RuntimeService runtimeService =(RuntimeService) ctx.getBean("runtimeService");
		try {
			Map<String, Object> variables = new HashMap<String, Object>();
			String username = StringUtil.fetchString(request, "sys.username");
				identityService.setAuthenticatedUserId(username);
				variables.put("businessTable", "t_apply");
				variables.put("businessTableKey", "PUR_NO");
				variables.put("businessTableKeyValue", purNo);
				variables.put("FlowType", "NAPPLY");
				variables.put("businessUrl",  "mydesk_tapplyReport.do?purno="+purNo);

				Map<String, String> map= new HashMap<String, String>();
				map.put("username", username);
				startFlow(identityService,runtimeService,username,"NAPPLY",variables);
				
		} catch (ActivitiException e) {
	      if (e.getMessage().indexOf("no processes deployed with key") != -1) {
	        logger.warn("没有部署流程!", e);
	      } else {
	        logger.error("启动请假流程失败：", e);
	      }
	    } catch (Exception e) {
	      logger.error("启动请假流程失败：", e);
	    }
}
  	/*出库申请(工具借用/一般领料/办公用品)*/
  public void startOutApplyFlow(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	  
	     String grid = StringUtil.fetchString(request, "maingrid");
	     String purNo = null,outMod=null,isOld=null;
	     JSONArray array=new JSONArray(grid);
	     for(int i=0 ; i < array.length() ;i++)
	     {
	        //获取每一个JsonObject对象
	        JSONObject myjObject = array.getJSONObject(i);
	        purNo = myjObject.getString("PUR_NO");
	        outMod= myjObject.getString("OUT_MOD");
	     }   
	    ServletContext sc =request.getSession().getServletContext();  
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(sc);
		IdentityService identityService = (IdentityService) ctx.getBean("identityService");
		RuntimeService runtimeService =(RuntimeService) ctx.getBean("runtimeService");
		try {
			Map<String, Object> variables = new HashMap<String, Object>();
			String outModCod= "";
			if(outMod.equals("一般领料")){
				outModCod= "OUTAPPLY";
			}else if(outMod.equals("工具领用")){
				outModCod= "EQUIPAPPLY";
			}else if(outMod.equals("办公用品")){
				outModCod= "BAPPLY";
			}else if(outMod.equals("物料申请")){
				outModCod= "MAPPLY";
			}else if(outMod.equals("工具借用")){
				outModCod= "EQUIPBAPPLY";
			}
	
				String username = StringUtil.fetchString(request, "sys.username");
				identityService.setAuthenticatedUserId(username);
				variables.put("businessTable", "t_out_apply");
				variables.put("businessTableKey", "PUR_NO");
				variables.put("businessTableKeyValue", purNo);
				variables.put("FlowType", outMod);
				variables.put("userName", username);
				variables.put("businessUrl", "mydesk_outApplyReport.do?purno="+purNo+"&outmod="+outMod);
				variables.put("isOld", isOld);
				
				Map<String, String> map= new HashMap<String, String>();
				map.put("username", username);
				startFlow(identityService,runtimeService,username,outModCod,variables);
				
		} catch (ActivitiException e) {
	      if (e.getMessage().indexOf("no processes deployed with key") != -1) {
	        logger.warn("没有部署流程!", e);
	      } else {
	        logger.error("启动请假流程失败：", e);
	      }
	    } catch (Exception e) {
	      logger.error("启动请假流程失败：", e);
	    }
  }
  
  /*办公用品申请*/
  public void startOfficeApplyFlow(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	  
	     String grid = StringUtil.fetchString(request, "maingrid");
	     String purNo = null;
	     JSONArray array=new JSONArray(grid);
	     for(int i=0 ; i < array.length() ;i++)
	     {
	        //获取每一个JsonObject对象
	        JSONObject myjObject = array.getJSONObject(i);
	        purNo = myjObject.getString("PUR_NO");
	     }   
	    ServletContext sc =request.getSession().getServletContext();  
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(sc);
		IdentityService identityService = (IdentityService) ctx.getBean("identityService");
		RuntimeService runtimeService =(RuntimeService) ctx.getBean("runtimeService");
		String resultstr = "";
		StringBuffer resultStr = new StringBuffer();
		Map<String,Object> map =new HashMap<String,Object>();
		try {
			Map<String, Object> variables = new HashMap<String, Object>();
			Connection connect = ConnectDB.getConnection();
			Statement st = connect.createStatement();
			String sql ="select processinstid from ( select id_ as processinstid ,MAX(version_),PROCESSTYPEKEY from act_re_procdef   group by id_ ) def join c_code ac on def.PROCESSTYPEKEY=ac.sys_cod where ac.SYS_COD='OFFICEAPPLY' ";
			ResultSet rs = st.executeQuery(sql);
	
			String processInstanceId = "";
			while(rs.next()){
				processInstanceId =rs.getString("processinstid");
			}
			String username = StringUtil.fetchString(request, "sys.username");
				identityService.setAuthenticatedUserId(username);
				ProcessInstance processInstance = runtimeService.startProcessInstanceById(processInstanceId, purNo+"|OFFICEAPPLY|t_out_apply|PUR_NO|", variables);
				String update_sql ="";
				update_sql="update t_out_apply set procdefid='"+processInstance.getProcessDefinitionId()+"',WF_STATUE='1',processinstid='"+processInstance.getProcessInstanceId()+"' where PUR_NO='"+purNo+"'";
				st.executeUpdate(update_sql);
				
				map.put("result_str","流程已启动");
		} catch (ActivitiException e) {
	      if (e.getMessage().indexOf("no processes deployed with key") != -1) {
	        logger.warn("没有部署流程!", e);
	        map.remove("result_str");
			map.put("result_str","没有部署流程");
	      } else {
	        logger.error("启动请假流程失败：", e);
	        map.remove("result_str");
			map.put("result_str","启动流程失败");
	      }
	    } catch (Exception e) {
	      logger.error("启动请假流程失败：", e);
	      map.remove("result_str");
			map.put("result_str","启动流程失败");
	    }
  }
  /*领料申请*/
  public void startMaterialApplyFlow(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	  
	     String grid = StringUtil.fetchString(request, "maingrid");
	     String purNo = null;
	     JSONArray array=new JSONArray(grid);
	     for(int i=0 ; i < array.length() ;i++)
	     {
	        //获取每一个JsonObject对象
	        JSONObject myjObject = array.getJSONObject(i);
	        purNo = myjObject.getString("PUR_NO");
	     }   
	    ServletContext sc =request.getSession().getServletContext();  
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(sc);
		IdentityService identityService = (IdentityService) ctx.getBean("identityService");
		RuntimeService runtimeService =(RuntimeService) ctx.getBean("runtimeService");
		String resultstr = "";
		StringBuffer resultStr = new StringBuffer();
		Map<String,Object> map =new HashMap<String,Object>();
		try {
			Map<String, Object> variables = new HashMap<String, Object>();
			Connection connect = ConnectDB.getConnection();
			Statement st = connect.createStatement();
			String sql ="select processinstid from ( select id_ as processinstid ,MAX(version_),PROCESSTYPEKEY from act_re_procdef   group by id_ ) def join c_code ac on def.PROCESSTYPEKEY=ac.sys_cod where ac.SYS_COD='MATERIALAPPLY' ";
			ResultSet rs = st.executeQuery(sql);
	
			String processInstanceId = "";
			while(rs.next()){
				processInstanceId =rs.getString("processinstid");
			}
			String username = StringUtil.fetchString(request, "sys.username");
				identityService.setAuthenticatedUserId(username);
				ProcessInstance processInstance = runtimeService.startProcessInstanceById(processInstanceId, purNo+"|MATERIALAPPLY|t_out_apply|PUR_NO|", variables);
				String update_sql ="";
				update_sql="update t_out_apply set procdefid='"+processInstance.getProcessDefinitionId()+"',WF_STATUE='1',processinstid='"+processInstance.getProcessInstanceId()+"' where PUR_NO='"+purNo+"'";
				st.executeUpdate(update_sql);
				
				map.put("result_str","流程已启动");
		} catch (ActivitiException e) {
	      if (e.getMessage().indexOf("no processes deployed with key") != -1) {
	        logger.warn("没有部署流程!", e);
	        map.remove("result_str");
			map.put("result_str","没有部署流程");
	      } else {
	        logger.error("启动请假流程失败：", e);
	        map.remove("result_str");
			map.put("result_str","启动流程失败");
	      }
	    } catch (Exception e) {
	      logger.error("启动请假流程失败：", e);
	      map.remove("result_str");
			map.put("result_str","启动流程失败");
	    }
  }
  
  public void startPartsOutFlow(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	  
	     String grid = StringUtil.fetchString(request, "OutMainGrid");
	     String recpNo = null;
	     JSONArray array=new JSONArray(grid);
	     for(int i=0 ; i < array.length() ;i++)
	     {
	        //获取每一个JsonObject对象
	        JSONObject myjObject = array.getJSONObject(i);
	        recpNo = myjObject.getString("RECP_NO");
	     }   
	    ServletContext sc =request.getSession().getServletContext();  
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(sc);
		IdentityService identityService = (IdentityService) ctx.getBean("identityService");
		RuntimeService runtimeService =(RuntimeService) ctx.getBean("runtimeService");
		try {
				Map<String, Object> variables = new HashMap<String, Object>();
				String username = StringUtil.fetchString(request, "sys.username");
				identityService.setAuthenticatedUserId(username);
				variables.put("businessTable", "t_parts_out");
				variables.put("businessTableKey", "RECP_NO");
				variables.put("businessTableKeyValue", recpNo);
				variables.put("FlowType", "POUT");
				variables.put("businessUrl", "");
				
				Map<String, String> map= new HashMap<String, String>();
				map.put("username", username);
				startFlow(identityService,runtimeService,username,"POUT",variables);
				
		} catch (ActivitiException e) {
	      if (e.getMessage().indexOf("no processes deployed with key") != -1) {
	        logger.warn("没有部署流程!", e);
	      } else {
	        logger.error("启动请假流程失败：", e);
	      }
	    } catch (Exception e) {
	      logger.error("启动请假流程失败：", e);
	    }
}
  
  /*入库申请*/
  public void startPartsInFlow(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	  
	     String grid = StringUtil.fetchString(request, "maingrid");
	     String recpNo = null;
	     JSONArray array=new JSONArray(grid);
	     for(int i=0 ; i < array.length() ;i++)
	     {
	        //获取每一个JsonObject对象
	        JSONObject myjObject = array.getJSONObject(i);
	        recpNo = myjObject.getString("RECP_NO");
	     }   
	    ServletContext sc =request.getSession().getServletContext();  
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(sc);
		IdentityService identityService = (IdentityService) ctx.getBean("identityService");
		RuntimeService runtimeService =(RuntimeService) ctx.getBean("runtimeService");
		try {
				Map<String, Object> variables = new HashMap<String, Object>();
				String username = StringUtil.fetchString(request, "sys.username");
				identityService.setAuthenticatedUserId(username);
				variables.put("businessTable", "t_parts_in");
				variables.put("businessTableKey", "RECP_NO");
				variables.put("businessTableKeyValue", recpNo);
				variables.put("FlowType", "PARTSIN");
				variables.put("userName", username);
				variables.put("businessUrl", "mydesk_partsInReport.do?purno="+recpNo);
				
				Map<String, String> map= new HashMap<String, String>();
				map.put("username", username);
				startFlow(identityService,runtimeService,username,"PARTSIN",variables);
				
		} catch (ActivitiException e) {
	      if (e.getMessage().indexOf("no processes deployed with key") != -1) {
	        logger.warn("没有部署流程!", e);
	      } else {
	        logger.error("启动请假流程失败：", e);
	      }
	    } catch (Exception e) {
	      logger.error("启动请假流程失败：", e);
	    }
}
  
  //盘点单流程
  public void startPartsStockFlow(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	  
	     String grid = StringUtil.fetchString(request, "InMainGrid");
	     String recpNo = null;
	     JSONArray array=new JSONArray(grid);
	     for(int i=0 ; i < array.length() ;i++)
	     {
	        //获取每一个JsonObject对象
	        JSONObject myjObject = array.getJSONObject(i);
	        recpNo = myjObject.getString("STOCKH_ID");
	     }   
	    ServletContext sc =request.getSession().getServletContext();  
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(sc);
		IdentityService identityService = (IdentityService) ctx.getBean("identityService");
		RuntimeService runtimeService =(RuntimeService) ctx.getBean("runtimeService");
		try {
				Map<String, Object> variables = new HashMap<String, Object>();
				String username = StringUtil.fetchString(request, "sys.username");
				identityService.setAuthenticatedUserId(username);
				variables.put("businessTable", "t_parts_stock");
				variables.put("businessTableKey", "STOCKH_ID");
				variables.put("businessTableKeyValue", recpNo);
				variables.put("FlowType", "PARTSSTOCK");
				variables.put("businessUrl", "mydesk_partstockReport.do?purno="+recpNo);
				Map<String, String> map= new HashMap<String, String>();
				map.put("username", username);
				startFlow(identityService,runtimeService,username,"PARTSSTOCK",variables);
				
		} catch (ActivitiException e) {
	      if (e.getMessage().indexOf("no processes deployed with key") != -1) {
	        logger.warn("没有部署流程!", e);
	      } else {
	        logger.error("启动请假流程失败：", e);
	      }
	    } catch (Exception e) {
	      logger.error("启动请假流程失败：", e);
	    }
}
  
  //检修计划
  public void startDevicPlanFlow(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	  
	     String grid = StringUtil.fetchString(request, "InMainGrid");
	     String billNo = null;
	     JSONArray array=new JSONArray(grid);
	     for(int i=0 ; i < array.length() ;i++)
	     {
	        //获取每一个JsonObject对象
	        JSONObject myjObject = array.getJSONObject(i);
	        billNo = myjObject.getString("BILL_NO");
	     }   
	    ServletContext sc =request.getSession().getServletContext();  
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(sc);
		IdentityService identityService = (IdentityService) ctx.getBean("identityService");
		RuntimeService runtimeService =(RuntimeService) ctx.getBean("runtimeService");
		try {
				Map<String, Object> variables = new HashMap<String, Object>();
				String username = StringUtil.fetchString(request, "sys.username");
				identityService.setAuthenticatedUserId(username);
				variables.put("businessTable", "t_devic_plan");
				variables.put("businessTableKey", "BILL_NO");
				variables.put("businessTableKeyValue", billNo);
				variables.put("FlowType", "DEVICEPLAN");
				variables.put("businessUrl", "");
				
				Map<String, String> map= new HashMap<String, String>();
				map.put("username", username);
				startFlow(identityService,runtimeService,username,"DEVICEPLAN",variables);
				
		} catch (ActivitiException e) {
	      if (e.getMessage().indexOf("no processes deployed with key") != -1) {
	        logger.warn("没有部署流程!", e);
	      } else {
	        logger.error("启动请假流程失败：", e);
	      }
	    } catch (Exception e) {
	      logger.error("启动请假流程失败：", e);
	    }
}
  
  //设备大修
  public void startDevicRepairRecFlow(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	  
	     String grid = StringUtil.fetchString(request, "grid");
	     String billNo = null;
	     JSONArray array=new JSONArray(grid);
	     for(int i=0 ; i < array.length() ;i++)
	     {
	        //获取每一个JsonObject对象
	        JSONObject myjObject = array.getJSONObject(i);
	        billNo = myjObject.getString("BILL_NO");
	     }   
	    ServletContext sc =request.getSession().getServletContext();  
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(sc);
		IdentityService identityService = (IdentityService) ctx.getBean("identityService");
		RuntimeService runtimeService =(RuntimeService) ctx.getBean("runtimeService");
		try {
				
				Map<String, Object> variables = new HashMap<String, Object>();
				String username = StringUtil.fetchString(request, "sys.username");
				identityService.setAuthenticatedUserId(username);
				variables.put("businessTable", "t_devic_repair_rec");
				variables.put("businessTableKey", "BILL_NO");
				variables.put("businessTableKeyValue", billNo);
				variables.put("FlowType", "DEVICEREPAIRREC");
				variables.put("businessUrl", "mydesk_devicRepair.do?purno="+billNo);
				
				Map<String, String> map= new HashMap<String, String>();
				map.put("username", username);
				startFlow(identityService,runtimeService,username,"DEVICEREPAIRREC",variables);
				
		} catch (ActivitiException e) {
	      if (e.getMessage().indexOf("no processes deployed with key") != -1) {
	        logger.warn("没有部署流程!", e);
	      } else {
	        logger.error("启动请假流程失败：", e);
	      }
	    } catch (Exception e) {
	      logger.error("启动请假流程失败：", e);
	    }
}
  
  
  public void delete(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
	  ServletContext sc =request.getSession().getServletContext(); 
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(sc);
		RepositoryService repositoryService = (RepositoryService) ctx.getBean("repositoryService");
		 String grid = StringUtil.fetchString(request, "modelgrid");
	      String modelId = null;
	      JSONArray array=new JSONArray(grid);
	      for(int i=0 ; i < array.length() ;i++)
	      {
	       //获取每一个JsonObject对象
	       JSONObject myjObject = array.getJSONObject(i);
	        modelId = myjObject.getString("id");
	      }
	  repositoryService.deleteModel(modelId);
  }
  
  public void deleteDeployment(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
	  ServletContext sc =request.getSession().getServletContext(); 
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(sc);
		RepositoryService repositoryService = (RepositoryService) ctx.getBean("repositoryService");
		String instid = StringUtil.fetchString(request, "instid");
		/*String grid = StringUtil.fetchString(request, "flowinstgrid");
	      String instid = null;
	      JSONArray array=new JSONArray(grid);
	      for(int i=0 ; i < array.length() ;i++)
	      {
	       //获取每一个JsonObject对象
	       JSONObject myjObject = array.getJSONObject(i);
	       instid = myjObject.getString("instid");
	      }*/
		repositoryService.deleteDeployment(instid);
  }
  
}
