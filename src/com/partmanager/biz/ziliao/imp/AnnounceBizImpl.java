package com.partmanager.biz.ziliao.imp;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.partmanager.biz.ziliao.AnnounceBiz;
import com.partmanager.dao.BaseDAO;
import com.partmanager.flow.entity.AgentProcess;
import com.partmanager.flow.entity.FileDetail;
import com.partmanager.pojo.Announce;
import com.xuelang.common.web.ConnectDB;

@SuppressWarnings("unchecked")
public class AnnounceBizImpl implements AnnounceBiz {
	
	private BaseDAO baseDao;
	
	public void setBaseDao(BaseDAO baseDao) {
		this.baseDao = baseDao;
	}
	
	/**
	 *  公告列表
	 */
	public List<Announce> findAnnounce() {
		List<Announce> list = new ArrayList<Announce>();
		List<Announce> gysList = baseDao.listAll("Announce");
		int i = 0;
		for (Announce ann : gysList) {
			Announce announce = new Announce();
			announce.setGuid(ann.getGuid());
			announce.setTitle(ann.getTitle());
			announce.setActiveMan(ann.getActiveMan());
			announce.setActiveDate(ann.getActiveDate());
			announce.setUnactiveDate(ann.getUnactiveDate());
			String content = ann.getContent();
			/*if(content.length()>20){
				content = content.substring(0, 19)+"...";
			}*/
			announce.setContent(content);
			list.add(announce);
			if(i==4){
				break;
			}
		}
		return list;
	}

	public Announce findAnnounceById(String guid) {
		
		return (Announce)baseDao.loadById(Announce.class, guid);
	}
	
	public List<AgentProcess> findProcessList(String username,String userrole) {
		
		List<AgentProcess> list = new ArrayList<AgentProcess>();
		Connection connect = ConnectDB.getConnection();
		try {
			Statement st = connect.createStatement();
			
			StringBuffer sb = new StringBuffer();
			sb.append("select task.id_ taskid,task.proc_inst_id_ procinstid,task.task_def_key_ taskdefkey,task.proc_def_id_ procdefid,inst.START_USER_ID_ startuser,varurl.TEXT_ businessurl,var.TEXT_ flowtype,varcod.TEXT_ businesscod, "+
				" task.name_ taskname,task.assignee_ assignee,task.create_time_ createtime,inst.business_key_ businesskey ,deploy.NAME_ instname "+
				" from act_ru_task task join act_hi_procinst inst on inst.proc_inst_id_= task.proc_inst_id_   "+
				" join act_re_procdef def on def.id_=inst.PROC_DEF_ID_ join act_re_deployment deploy on deploy.ID_=def.DEPLOYMENT_ID_ "+
				" left join act_hi_varinst var on var.PROC_INST_ID_=inst.ID_ and var.NAME_='FlowType' "+
				"left join act_hi_varinst varcod on varcod.PROC_INST_ID_=inst.ID_ and varcod.NAME_='businessTableKeyValue'"+
				"left join act_hi_varinst varurl on varurl.PROC_INST_ID_=inst.ID_ and varurl.NAME_='businessUrl'"+
				" where task.assignee_ in ('"+username+"','"+userrole+"') order by task.create_time_  desc LIMIT    0,6 ");
			
			ResultSet rs = st.executeQuery(sb.toString());
			while(rs.next()){
				AgentProcess ap = new AgentProcess();
				ap.setTaskid(rs.getString("taskid"));
				ap.setAssignee(rs.getString("assignee"));
				ap.setBusinesskey(rs.getString("businesskey"));
				ap.setProcessinstid(rs.getString("procinstid"));
				ap.setTaskname(rs.getString("taskname"));
				ap.setCreatetime(rs.getString("createtime"));
				ap.setInstname(rs.getString("instname")+"【单号:"+rs.getString("businesscod")+"】");
				ap.setStartuser(rs.getString("startuser"));
				ap.setFlowtype(rs.getString("flowtype"));
				ap.setTaskdefid(rs.getString("taskdefkey"));
				ap.setBusinesscod(rs.getString("businesscod"));
				ap.setBusinessurl(rs.getString("businessurl"));
				list.add(ap);
			}
			st.close();
			connect.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return list;
	}

	public List<FileDetail> findFilesList(Integer userid) {
		List<FileDetail> list = new ArrayList<FileDetail>();
		Connection connect = ConnectDB.getConnection();
		try {
			Statement st = connect.createStatement();
			
			StringBuffer sb = new StringBuffer();
			sb.append("select b.id,b.file_type,b.file_describe,b.record_tim,c.userid from file_manage b join users  c on instr(b.receive_man,c.username)>0"+
					" where c.userid="+userid+" and b.mainshow='1' LIMIT  0,6  ");
			
			ResultSet rs = st.executeQuery(sb.toString());
			while(rs.next()){
				FileDetail fd = new FileDetail();
				fd.setId(rs.getString("id"));
				fd.setFiletype(rs.getString("file_type"));
				fd.setFiledescribe(rs.getString("file_describe"));
				fd.setRecordtim(rs.getString("record_tim"));
				list.add(fd);
			}
			st.close();
			connect.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return list;
	}

}
