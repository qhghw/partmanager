package com.partmanager.action.power;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.struts2.ServletActionContext;

import com.partmanager.action.BaseAction;
import com.partmanager.biz.power.dto.UserDTO;
import com.partmanager.biz.ziliao.AnnounceBiz;
import com.partmanager.flow.entity.AgentProcess;
import com.partmanager.flow.entity.FileDetail;
import com.partmanager.pojo.Announce;
import com.partmanager.utils.system.Constants;
import com.webbuilder.utils.DbUtil;

@SuppressWarnings("serial")
public class MydeskAction extends BaseAction  {
	
	private AnnounceBiz announceBiz;
	
	private List<Announce> announces;
	
	private Announce announce;
	
	private List<AgentProcess> agentProcess;
	
	private String purno;
	private String begTim;
	private String endTim;
	private String storeName;
	public String getBegTim() {
		return begTim;
	}



	public void setBegTim(String begTim) {
		this.begTim = begTim;
	}



	public String getEndTim() {
		return endTim;
	}



	public void setEndTim(String endTim) {
		this.endTim = endTim;
	}



	public String getStoreName() {
		return storeName;
	}



	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}



	private String outmod;
	
	private String flowtype;
	
	private List<FileDetail> filedetails;
	
	/**
	 * 首页公告列表
	 */
	public String findDeskList() {
		try {
			
			announces=getAnnounceBiz().findAnnounce();
		} catch (Exception e) {
			 e.printStackTrace();
			 this.outError();
		}
		UserDTO userInfo=(UserDTO) this.getSession().getAttribute(Constants.USERINFO);
		String username= userInfo.getUsername();
		String userrole=userInfo.getRolename();
		agentProcess = announceBiz.findProcessList(username,userrole);
		
		Integer userid = userInfo.getUserid();
		filedetails = announceBiz.findFilesList(userid);
		
		return "maintab";
	}
	
	
	
	public void wasteApplyReport() throws JRException, NamingException, SQLException, IOException{
		String url="/jsp/ireport/flowform/wasteapply.jasper";
		makereport(url);
	}
	
	public void partsInReport() throws JRException, NamingException, SQLException, IOException{
		String url="/jsp/ireport/flowform/partsin.jasper";
		makereport(url);
	}
	public void outApplyReport() throws JRException, NamingException, SQLException, IOException{
	  String   outmode = new String(outmod.getBytes("iso8859-1"), "UTF-8"); 
	  String tempMod = "";
	    String ireportmod="";
	    if(outmode.equals("一般领料")||getFlowtype().equals("outapply")){
	    	    ireportmod= "outapply";
	    	    tempMod = "一般领料";
        	}else if(outmode.equals("工具领用")||getFlowtype().equals("toolapply")){
        	    ireportmod= "toolapply";
        	    tempMod = "工具领用";
        	}else if(outmode.equals("办公用品")||getFlowtype().equals("officeapply")){
        	    ireportmod= "officeapply";
        	    tempMod = "办公用品";
        	}else if(outmode.equals("物料申请")||getFlowtype().equals("maiterialapply")){
        	    ireportmod= "maiterialapply";
        	    tempMod = "物料申请";
        	}else if(outmode.equals("工具借用")||getFlowtype().equals("toolbapply")){
        	    ireportmod= "toolbapply";
        	    tempMod = "一工具借用";
        	}
		String url="/jsp/ireport/flowform/"+ireportmod+".jasper";
		makereport(url,tempMod);
	}
	public void tapplyReport() throws JRException, NamingException, SQLException, IOException{
		String url="/jsp/ireport/flowform/tapply.jasper";
		makereport(url);
	}
	public void stockInReport() throws JRException, NamingException, SQLException, IOException{
		String url="/jsp/ireport/flowform/stockInReport.jasper";
		makereport(url);
	}
	public void stockOutReport() throws JRException, NamingException, SQLException, IOException{
		String url="/jsp/ireport/flowform/stockOutReport.jasper";
		makereport(url);
	}
	public void tdeviceReport() throws JRException, NamingException, SQLException, IOException{
		String url="/jsp/ireport/flowform/tdeviceapply.jasper";
		makereport(url);
	}
	
	public void partstockReport() throws JRException, NamingException, SQLException, IOException{
		String url="/jsp/ireport/flowform/partstock.jasper";
		makereport(url);
	}
	
	public void devicRepair() throws JRException, NamingException, SQLException, IOException{
		String url="/jsp/ireport/flowform/devicRepairRec.jasper";
		makereport(url);
	}
	
	public void outReport() throws JRException, NamingException, SQLException, IOException{
		String url="/jsp/ireport/outQuery.jasper";
		 HttpServletRequest request = ServletActionContext.getRequest();
		 Map parameters = new HashMap();
		    parameters.put("begTim",  begTim);
		    parameters.put("endTim",  endTim);
		    parameters.put("storeName",   storeName);
		    UserDTO userInfo = (UserDTO) request.getSession().getAttribute(Constants.USERINFO);
		    parameters.put("UserName",  userInfo.getUsername());
		makereport(url,parameters);
	}
	public void inReport() throws JRException, NamingException, SQLException, IOException{
		String url="/jsp/ireport/inQuery.jasper";
		 HttpServletRequest request = ServletActionContext.getRequest();
		 Map parameters = new HashMap();
		    parameters.put("begTim",  begTim);
		    parameters.put("endTim",  endTim);
		    parameters.put("storeName",   storeName);
		    UserDTO userInfo = (UserDTO) request.getSession().getAttribute(Constants.USERINFO);
		    parameters.put("UserName",  userInfo.getUsername());
		makereport(url,parameters);
	}
	
	public void stockOutExcel() throws JRException, NamingException, SQLException, IOException{
		String url="/jsp/ireport/stockExcelNoZero.jasper";
		makereportExcel(url);
	}
	
	public void tooloutReportAll() throws JRException, NamingException, SQLException, IOException{
		String url="/jsp/ireport/flowform/toolapplyTime.jasper";
		 HttpServletRequest request = ServletActionContext.getRequest();
		 Map parameters = new HashMap();
		    parameters.put("bgTim",  begTim);
		    parameters.put("edTim",  endTim);
		    parameters.put("outmod",  "工具领用");
		    UserDTO userInfo = (UserDTO) request.getSession().getAttribute(Constants.USERINFO);
		    parameters.put("UserName",  userInfo.getUsername());
		makereport(url,parameters);
	}
	
	public void makereportExcel(String url) throws JRException, NamingException, SQLException, IOException {
		 HttpServletRequest request = ServletActionContext.getRequest();
		 HttpServletResponse response = ServletActionContext.getResponse();
	     HttpSession session = request.getSession();
	      ServletContext application = ServletActionContext.getServletContext();
		File reportFile = new File(application.getRealPath(url));
	    JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportFile.getPath());
	    Map parameters = new HashMap();
	    parameters.put("purno",  purno);
	    Connection conn = null;
		String jndi = "java:comp/env/jdbc/mysql";
	    conn = DbUtil.getConnection(jndi);
	    //装载jasper文件application
	    File exe_rpt = new File(application.getRealPath(url));
	   

	    try{
	     // fill
	     JasperPrint jasperPrint = JasperFillManager.fillReport(exe_rpt.getPath(),parameters,conn);
	    
	  
	   
	     /* 
          * 设置头信息 
          */  
         response.setContentType("application/vnd.ms-excel");  
         String defaultname=null;
         defaultname="export.xls";  

         String fileName = new String(defaultname.getBytes("gbk"), "utf-8");  
         response.setHeader("Content-disposition", "attachment; filename="  
           + fileName);  
         
         ServletOutputStream ouputStream = response.getOutputStream();  
         JRXlsExporter exporter = new JRXlsExporter();  
         exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);  
         exporter.setParameter(JRExporterParameter.OUTPUT_STREAM,ouputStream);  
         exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,Boolean.TRUE);  
         exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET,Boolean.FALSE);  
         exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND,Boolean.FALSE);  
         exporter.exportReport();  
         ouputStream.flush();  
         ouputStream.close();  
		       
		       
	     conn.close();
	 }catch(JRException ex){
	     ex.printStackTrace();
	    }
	}

	
	public void makereport(String url) throws JRException, NamingException, SQLException, IOException {
		 HttpServletRequest request = ServletActionContext.getRequest();
		 HttpServletResponse response = ServletActionContext.getResponse();
	     HttpSession session = request.getSession();
	      ServletContext application = ServletActionContext.getServletContext();
		File reportFile = new File(application.getRealPath(url));
	    JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportFile.getPath());
	    Map parameters = new HashMap();
	    parameters.put("purno",  purno);
	    Connection conn = null;
		String jndi = "java:comp/env/jdbc/mysql";
	    conn = DbUtil.getConnection(jndi);
	    //装载jasper文件application
	    File exe_rpt = new File(application.getRealPath(url));
	   

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
	     ex.printStackTrace();
	    }
	}

	public void makereport(String url,String outMod) throws JRException, NamingException, SQLException, IOException {
		 HttpServletRequest request = ServletActionContext.getRequest();
		 HttpServletResponse response = ServletActionContext.getResponse();
	     HttpSession session = request.getSession();
	      ServletContext application = ServletActionContext.getServletContext();
		File reportFile = new File(application.getRealPath(url));
	    JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportFile.getPath());
	    Map parameters = new HashMap();
	    parameters.put("purno",  purno);
	    parameters.put("outmod",  outMod);
	    Connection conn = null;
		String jndi = "java:comp/env/jdbc/mysql";
	    conn = DbUtil.getConnection(jndi);
	    //装载jasper文件application
	    File exe_rpt = new File(application.getRealPath(url));
	   

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
	     ex.printStackTrace();
	    }
	}
	
	public void makereport(String url,Map parameters) throws JRException, NamingException, SQLException, IOException {
		 HttpServletRequest request = ServletActionContext.getRequest();
		 HttpServletResponse response = ServletActionContext.getResponse();
	     HttpSession session = request.getSession();
	     ServletContext application = ServletActionContext.getServletContext();
		File reportFile = new File(application.getRealPath(url));
	    JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportFile.getPath());
	    Connection conn = null;
		String jndi = "java:comp/env/jdbc/mysql";
	    conn = DbUtil.getConnection(jndi);
	    //装载jasper文件application
	    File exe_rpt = new File(application.getRealPath(url));
	   

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
	     ex.printStackTrace();
	    }
	}
	public String findAnnounceById(){
		announce = announceBiz.findAnnounceById(announce.getGuid());
		this.outObjectString(announce);
		return null;
	}
	
	
	public void setAnnounces(List<Announce> announces) {
		this.announces = announces;
	}

	public List<Announce> getAnnounces() {
		return announces;
	}

	public void setAnnounceBiz(AnnounceBiz announceBiz) {
		this.announceBiz = announceBiz;
	}

	public AnnounceBiz getAnnounceBiz() {
		return announceBiz;
	}


	public void setAnnounce(Announce announce) {
		this.announce = announce;
	}


	public Announce getAnnounce() {
		return announce;
	}

	/**
	 * @param agentProcess the agentProcess to set
	 */
	public void setAgentProcess(List<AgentProcess> agentProcess) {
		this.agentProcess = agentProcess;
	}


	/**
	 * @return the agentProcess
	 */
	public List<AgentProcess> getAgentProcess() {
		return agentProcess;
	}

	public String getPurno() {
		return purno;
	}

	public void setPurno(String purno) {
		this.purno = purno;
	}



	public void setFiledetails(List<FileDetail> filedetails) {
		this.filedetails = filedetails;
	}



	public List<FileDetail> getFiledetails() {
		return filedetails;
	}



	public String getOutmod() {
	    return outmod;
	}



	public void setOutmod(String outmod) {
	    this.outmod = outmod;
	}



	public String getFlowtype() {
	    return flowtype;
	}



	public void setFlowtype(String flowtype) {
	    this.flowtype = flowtype;
	}

	
}
