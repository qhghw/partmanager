package com.partmanager.flow.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.jgroups.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;

import com.webbuilder.utils.StringUtil;


public class ProcessBackService {
	
	protected RepositoryService repositoryService;  
	  
    protected RuntimeService runtimeService;  
  
    protected TaskService taskService;  
    
	public void setBeans(RepositoryService repositoryService,RuntimeService runtimeService,TaskService taskService){
		
		this.repositoryService=repositoryService;
		this.runtimeService=runtimeService;
		this.taskService=taskService;
	}
	
	  /** 
     * 驳回流程 
     *  
     * @param taskId 
     *            当前任务ID 
     * @param activityId 
     *            驳回节点ID 
     * @param variables 
     *            流程存储参数 
     * @throws Exception 
     */  
    public void backProcess(String taskId, String activityId,   Map<String, Object> variables) throws Exception {  
        if (StringUtil.isEmpty(activityId)) {  
            throw new Exception("驳回目标节点ID为空！");  
        }  
  
        // 查询本节点发起的会签任务，并结束  
       /* List<Task> tasks = taskService.createTaskQuery().parentTaskId(taskId)  
                .taskDescription("jointProcess").list();  
        for (Task task : tasks) {  
            commitProcess(task.getId(), null, null);  
        }  */
  
        // 查找所有并行任务节点，同时驳回  
        List<Task> taskList = findTaskListByKey(findProcessInstanceByTaskId(  
                taskId).getId(), findTaskById(taskId).getTaskDefinitionKey());  
        for (Task task : taskList) {  
            commitProcess(task.getId(), variables, activityId);  
        }  
    }  
    
    /** 
     * 驳回流程 
     *  
     * @param taskId 
     *            当前任务ID 
     * @param activityId 
     *            驳回节点ID 
     * @param variables 
     *            流程存储参数 
     * @throws Exception 
     */  
    public void backPreProcess(String taskId,  String parentTaskId, Map<String, Object> variables) throws Exception {  
    	TaskEntity taskentity = (TaskEntity) taskService.createTaskQuery().taskId(taskId).singleResult();  
    	if (taskentity == null) {  
            throw new Exception("任务实例未找到!");  
        }  else{
        	//String activityId=taskentity.getVariableLocal("parentTaskId").toString();
        	String activityId=parentTaskId;
        	 // 查找所有并行任务节点，同时驳回  
            List<Task> taskList = findTaskListByKey(findProcessInstanceByTaskId(  
                    taskId).getId(), findTaskById(taskId).getTaskDefinitionKey());  
            for (Task task : taskList) {  
                commitProcess(task.getId(), variables, activityId);  
            }
        }
  
    }  
    
    /** 
     * 根据任务ID获取对应的流程实例 
     *  
     * @param taskId 
     *            任务ID 
     * @return 
     * @throws Exception 
     */  
    public ProcessInstance findProcessInstanceByTaskId(String taskId)  
            throws Exception {  
        // 找到流程实例  
        ProcessInstance processInstance = runtimeService  
                .createProcessInstanceQuery().processInstanceId(  
                        findTaskById(taskId).getProcessInstanceId())  
                .singleResult();  
        if (processInstance == null) {  
            throw new Exception("流程实例未找到!");  
        }  
        return processInstance;  
    }  
    
    /** 
     * 根据任务ID获得任务实例 
     *  
     * @param taskId 
     *            任务ID 
     * @return 
     * @throws Exception 
     */  
    public TaskEntity findTaskById(String taskId) throws Exception {  
        TaskEntity task = (TaskEntity) taskService.createTaskQuery().taskId(taskId).singleResult();  
        if (task == null) {  
            throw new Exception("任务实例未找到!");  
        }  
        return task;  
    }  
    
    /** 
     * 根据流程实例ID和任务key值查询所有同级任务集合 
     *  
     * @param processInstanceId 
     * @param key 
     * @return 
     */  
    public List<Task> findTaskListByKey(String processInstanceId, String key) {  
        return taskService.createTaskQuery().processInstanceId(  
                processInstanceId).taskDefinitionKey(key).list();  
    }  
    
    /** 
     * @param taskId 
     *            当前任务ID 
     * @param variables 
     *            流程变量 
     * @param activityId 
     *            流程转向执行任务节点ID<br> 
     *            此参数为空，默认为提交操作 
     * @throws Exception 
     */  
    public void commitProcess(String taskId, Map<String, Object> variables,  
            String activityId) throws Exception {  
        if (variables == null) {  
            variables = new HashMap<String, Object>();  
        }  
        // 跳转节点为空，默认提交操作  
        if (StringUtil.isEmpty(activityId)) {  
            taskService.complete(taskId, variables);  
        } else {// 流程转向操作  
            turnTransition(taskId, activityId, variables);  
        }  
    }
    
    /** 
     * 流程转向操作 
     *  
     * @param taskId 
     *            当前任务ID 
     * @param activityId 
     *            目标节点任务ID 
     * @param variables 
     *            流程变量 
     * @throws Exception 
     */  
    public void turnTransition(String taskId, String activityId,  
            Map<String, Object> variables) throws Exception {  
        // 当前节点  
        ActivityImpl currActivity = findActivitiImpl(taskId, null);  
        // 清空当前流向  
        List<PvmTransition> oriPvmTransitionList = clearTransition(currActivity);  
  
        // 创建新流向  
        TransitionImpl newTransition = currActivity.createOutgoingTransition();  
        // 目标节点  
        ActivityImpl pointActivity = findActivitiImpl(taskId, activityId);  
        // 设置新流向的目标节点  
        newTransition.setDestination(pointActivity);  
  
        // 执行转向任务  
        taskService.complete(taskId, variables);  
        // 删除目标节点新流入  
        pointActivity.getIncomingTransitions().remove(newTransition);  
  
        // 还原以前流向  
        restoreTransition(currActivity, oriPvmTransitionList);  
    }  
    
    
    /** 
     * 根据任务ID和节点ID获取活动节点 <br> 
     *  
     * @param taskId 
     *            任务ID 
     * @param activityId 
     *            活动节点ID <br> 
     *            如果为null或""，则默认查询当前活动节点 <br> 
     *            如果为"end"，则查询结束节点 <br> 
     *  
     * @return 
     * @throws Exception 
     */  
    public ActivityImpl findActivitiImpl(String taskId, String activityId)  
            throws Exception {  
        // 取得流程定义  
        ProcessDefinitionEntity processDefinition = findProcessDefinitionEntityByTaskId(taskId);  
  
        // 获取当前活动节点ID  
        if (StringUtil.isEmpty(activityId)) {  
            activityId = findTaskById(taskId).getTaskDefinitionKey();  
        }  
  
        // 根据流程定义，获取该流程实例的结束节点  
        if (activityId.toUpperCase().equals("END")) {  
            for (ActivityImpl activityImpl : processDefinition.getActivities()) {  
                List<PvmTransition> pvmTransitionList = activityImpl  
                        .getOutgoingTransitions();  
                if (pvmTransitionList.isEmpty()) {  
                    return activityImpl;  
                }  
            }  
        }  
  
        // 根据节点ID，获取对应的活动节点  
        ActivityImpl activityImpl = ((ProcessDefinitionImpl) processDefinition)  
                .findActivity(activityId);  
  
        return activityImpl;  
    }  
    
    /** 
     * 还原指定活动节点流向 
     *  
     * @param activityImpl 
     *            活动节点 
     * @param oriPvmTransitionList 
     *            原有节点流向集合 
     */  
    public void restoreTransition(ActivityImpl activityImpl,  
            List<PvmTransition> oriPvmTransitionList) {  
        // 清空现有流向  
        List<PvmTransition> pvmTransitionList = activityImpl  
                .getOutgoingTransitions();  
        pvmTransitionList.clear();  
        // 还原以前流向  
        for (PvmTransition pvmTransition : oriPvmTransitionList) {  
            pvmTransitionList.add(pvmTransition);  
        }  
    }  
    

    /** 
     * 根据任务ID获取流程定义 
     *  
     * @param taskId 
     *            任务ID 
     * @return 
     * @throws Exception 
     */  
    public ProcessDefinitionEntity findProcessDefinitionEntityByTaskId(  
            String taskId) throws Exception {  
        // 取得流程定义  
        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)  
                .getDeployedProcessDefinition(findTaskById(taskId)  
                        .getProcessDefinitionId());  
  
        if (processDefinition == null) {  
            throw new Exception("流程定义未找到!");  
        }  
  
        return processDefinition;  
    }  
    
    /** 
     * 清空指定活动节点流向 
     *  
     * @param activityImpl 
     *            活动节点 
     * @return 节点流向集合 
     */  
    public List<PvmTransition> clearTransition(ActivityImpl activityImpl) {  
        // 存储当前节点所有流向临时变量  
        List<PvmTransition> oriPvmTransitionList = new ArrayList<PvmTransition>();  
        // 获取当前节点所有流向，存储到临时变量，然后清空  
        List<PvmTransition> pvmTransitionList = activityImpl  
                .getOutgoingTransitions();  
        for (PvmTransition pvmTransition : pvmTransitionList) {  
            oriPvmTransitionList.add(pvmTransition);  
        }  
        pvmTransitionList.clear();  
  
        return oriPvmTransitionList;  
    }  
    
    
    public String getNextTask(Task task){
    	String taskid="";
    	ProcessDefinitionEntity def = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(task.getProcessDefinitionId());  
    	List<ActivityImpl> activitiList = def.getActivities();

        String excId = task.getExecutionId();  
        ExecutionEntity execution = (ExecutionEntity) runtimeService.createExecutionQuery().executionId(excId).singleResult();  
        String activitiId = execution.getActivityId();  

    	for(ActivityImpl activityImpl:activitiList){  
    		String id = activityImpl.getId();  
    		if(activitiId.equals(id)){  
    		System.out.println("当前任务："+activityImpl.getProperty("name")); //输出某个节点的某种属性  
    		List<PvmTransition> outTransitions = activityImpl.getOutgoingTransitions();//获取从某个节点出来的所有线路  
    		for(PvmTransition tr:outTransitions){  
    		PvmActivity ac = tr.getDestination(); //获取线路的终点节点  
    		System.out.println("下一步任务任务："+ac.getId());  
    		//ac.getProcessDefinition().getName();
    		taskid=ac.getId();
    		}  
    		break;  
    		}  
    		} 
		return taskid; 
    }
    
    protected SequenceFlow createSequenceFlow(String from, String to) {
    	SequenceFlow flow = new SequenceFlow();
    	flow.setSourceRef(from);
    	flow.setTargetRef(to);
    	return flow;
    }
    
    /** 
     * 会签操作 
     *  
     * @param taskId 
     *            当前任务ID 
     * @param userCodes 
     *            会签人账号集合 
     * @throws Exception 
     */  
    public void jointProcess(Task task_, List<String> userCodes,JdbcTemplate jdbcTemplate,ExecutionEntity executionEntity,Map<String, Object> variables)  {  
        for (String userCode : userCodes) {  
        	String taskid=UUID.randomUUID().toString();
        	
            TaskEntity task = (TaskEntity) taskService.newTask(taskid);  
            try {
				task.setName(task_.getName() + "-会签");
				task.setAssigneeWithoutCascade(userCode);
				task.setParentTaskId(task_.getId());
				task.setExecutionId(task_.getExecutionId());
				task.setProcessDefinitionId(task_.getProcessDefinitionId());
				task.setProcessInstanceId(task_.getProcessInstanceId());
				taskService.saveTask(task);
            } catch (Exception e) {
				e.printStackTrace();
			}  
            task.setDescription("jointProcess"); 
            task_.setParentTaskId(taskid);
            taskService.saveTask(task_);  
            taskService.saveTask(task);  
        }  
        taskService.complete(task_.getId(), variables);
    }
    
 /*   public void testDynamicDeploy() throws Exception {
    	BpmnModel model = new BpmnModel();
    	Process process = new Process();
    	model.addProcess(process);
    	process.setId("my-process");
    	process.addFlowElement(createStartEvent());
    	process.addFlowElement(createUserTask("task1", "First task", "fred"));
    	process.addFlowElement(createUserTask("task2", "Second task", "john"));
    	process.addFlowElement(createEndEvent());
    	process.addFlowElement(createSequenceFlow("start", "task1"));
    	process.addFlowElement(createSequenceFlow("task1", "task2"));
    	process.addFlowElement(createSequenceFlow("task2", "end"));
    	// 2. Generate graphical informationnew 
    	BpmnAutoLayout(model).execute();
    	// 3. Deploy the process to the engine
    	Deployment deployment = activitiRule.getRepositoryService().createDeployment().addBpmnModel("dynamic-model.bpmn", model).name("Dynamic process deployment").deploy();
    	// 4. Start a process instance
    	ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");
    	// 5. Check if task is available
    	List tasks = activitiRule.getTaskService().createTaskQuery().processInstanceId(processInstance.getId()).list();
    	Assert.assertEquals(1, tasks.size());
    	Assert.assertEquals("First task", tasks.get(0).getName());
    	Assert.assertEquals("fred", tasks.get(0).getAssignee());
    	// 6. Save process diagram to a file
    	InputStream processDiagram = activitiRule.getRepositoryService().getProcessDiagram(processInstance.getProcessDefinitionId());
    	FileUtils.copyInputStreamToFile(processDiagram, new File("target/diagram.png"));
    	// 7. Save resulting BPMN xml to a file
    	InputStream processBpmn = activitiRule.getRepositoryService().getResourceAsStream(deployment.getId(), "dynamic-model.bpmn");
    	FileUtils.copyInputStreamToFile(processBpmn,new File("target/process.bpmn20.xml"));
    	}
   }

protected UserTask createUserTask(String id, String name, String assignee) {
	UserTask userTask = new UserTask();
	userTask.setName(name);
	userTask.setId(id);
	userTask.setAssignee(assignee);
	return userTask;
	}

protected SequenceFlow createSequenceFlow(String from, String to) {
		SequenceFlow flow = new SequenceFlow();
		flow.setSourceRef(from);
		flow.setTargetRef(to);return flow;
	}
protected StartEvent createStartEvent() {
		StartEvent startEvent = new StartEvent();startEvent.setId("start");
		return startEvent;
	}
protected EndEvent createEndEvent() {
		EndEvent endEvent = new EndEvent();endEvent.setId("end");
		return endEvent;
}


*/

private List<ActivityImpl> activitiList = new ArrayList();

//根据ActivitiId获取Acitiviti
public ActivityImpl findActivityImpl(List<ActivityImpl> activitiList, String activitiId) {

    for (ActivityImpl activityImpl : activitiList) {
        String id = activityImpl.getId();
        if (id.equals(activitiId)) {
            return activityImpl;
        }
    }
    return null;
}

public List<ActivityImpl> findEndActivityImpls(List<ActivityImpl> activitiList) {
    List<ActivityImpl> activityImpls = new ArrayList<ActivityImpl>();
    for (ActivityImpl activityImpl : activitiList) {
        List<PvmTransition> pvmTransitionList = activityImpl.getOutgoingTransitions();
        if (pvmTransitionList.isEmpty()) {
            activityImpls.add(activityImpl);
        }
    }
    return activityImpls;
}

//通用流程否决接口
public void rejectAndEnd(String taskId) {
   
    TaskEntity taskEntity = (TaskEntity) taskService.createTaskQuery().taskId(taskId).singleResult();
   
    ProcessDefinitionEntity def = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService).getDeployedProcessDefinition(taskEntity.getProcessDefinitionId());
   
   
    ExecutionEntity executionEntity = (ExecutionEntity) runtimeService.createExecutionQuery().executionId(taskEntity.getExecutionId()).singleResult();//执行实例

    String activitiId = executionEntity.getActivityId();//当前实例的执行到哪个节点
    activitiList = def.getActivities();//获得当前任务的所有节点
   
    ActivityImpl activeActivity = findActivityImpl(activitiList, activitiId);
    ActivityImpl endActivity = findEndActivityImpls(activitiList).get(0);


    List<PvmTransition> pvmTransitionList = activeActivity.getOutgoingTransitions();//获取当前节点的所以出口（这个方法做的不好，应该返回List<TransitionImpl>才对的，这样就不用下面的强转换了，我想以后版本会改了这点）
    for (PvmTransition pvmTransition : pvmTransitionList) {
        TransitionImpl transitionImpl = (TransitionImpl) pvmTransition;//强转为TransitionImpl
        transitionImpl.setDestination(endActivity);
    }
   
    taskService.complete(taskId);
}

}


