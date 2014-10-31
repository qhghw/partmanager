package com.xuelang.partmanage;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.webbuilder.utils.Atith;
import com.webbuilder.utils.DbUtil;
import com.webbuilder.utils.StringUtil;
import java.io.File;
import java.io.InputStream;
import com.webbuilder.utils.FileUtil;
public class partDevie {

	public void getStockNum(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		 //SELECT STORAGE_NAM INTO stNAM  FROM T_OUT_APPLY WHERE PUR_NO = NEW.PUR_NO;
		// SELECT  (STORE_NUM - USE_NUM)  INTO useNum  FROM T_PART_LIST  WHERE PARTS_COD = NEW.PARTS_COD and STORAGE_NAM = stNAM  ;
		 response.getWriter().write("122");
		}
	public void getAndSaveFile(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try {
			String path ="D://partmanager//upload//";
			File dir = new File(path);
			if (!dir.exists()) {
				if (!dir.mkdirs())
					throw new Exception("不能创建目录。");
			} 
			String importDir = path;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			String date = sdf.format(new Date());
			InputStream stream = (InputStream) request.getAttribute("importFile");
			BigDecimal fileSize =new BigDecimal( stream.available());
			BigDecimal bSize = fileSize.divide(new BigDecimal(1048576),3,BigDecimal.ROUND_HALF_UP);
			String filename = request.getAttribute("importFile__file").toString();
			String newFilename = filename.split("\\.")[0]+""+"."+filename.split("\\.")[1];
			path += "/" + newFilename;
			FileUtil.saveInputStreamToFile(stream, new File(importDir, newFilename));
			String jndi = StringUtil.fetchString(request, "sys.jndi");	
			String mainId = StringUtil.fetchString(request, "mainId");	
			String userName = StringUtil.fetchString(request, "sys.username");
			Connection conn = null;
			conn = DbUtil.getConnection(jndi);
			conn.setAutoCommit(false);
			Statement stmt = null;  
			try{	
				stmt = conn.createStatement();  
		        String sql = "insert into file_detail (manageid,filename,filesize,url,RECORD_TIM,uploadman,DEPT_COD,USERID) values('"+mainId+"','"+newFilename+"','"+bSize+"M','"+importDir+"',now(),'"+userName+"','1','1')";  
		        stmt.executeUpdate(sql);      
			} catch (Exception ex) {
				ex.printStackTrace();
				 
			} finally {
				conn.commit();
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
	
			}		
		
			
			response.getWriter().write(newFilename);
			
		} catch (Exception ex) {
			ex.printStackTrace();
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
							+ id + "' ");
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
			System.out.println(myJson);
			pw.write(myJson);
			pw.flush();
			pw.close();
		}

	}
	/*
	 * id带类别树
	 */
	public void getPartTypeTreeJson(HttpServletRequest request,
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
		//	System.out.println("aaaaaaaaaaaaaaaaaaa  "+id);
			s = conn.createStatement();
			if (id == null || "".equals(id) || "tree__root".equals(id)) {
				id = "-1";
			}
			else
			{
				id = id.split("$")[0];
			}
			//System.out.println("dddddddddddddddddd  "+id);
			rs = s
					.executeQuery("    select CONCAT(type_id,'$',DATA_TYP) id, PARTS_TYPE text from   c_parts_type  where PARENT_ID='"
							+ id + "' and (DATA_TYP = '物料' or DATA_TYP = '全部') ");
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
			System.out.println(myJson);
			pw.write(myJson);
			pw.flush();
			pw.close();
		}

	}
	
	
	public void getPartTypeTreeJsonDevic(HttpServletRequest request,
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
		//	System.out.println("aaaaaaaaaaaaaaaaaaa  "+id);
			s = conn.createStatement();
			if (id == null || "".equals(id) || "tree__root".equals(id)) {
				id = "-1";
			}
			else
			{
				id = id.split("$")[0];
			}
			//System.out.println("dddddddddddddddddd  "+id);
			rs = s
					.executeQuery("    select CONCAT(type_id,'$',DATA_TYP) id, PARTS_TYPE text from   c_parts_type  where PARENT_ID='"
							+ id + "' and (DATA_TYP = '设备' or DATA_TYP = '全部') ");
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
			System.out.println(myJson);
			pw.write(myJson);
			pw.flush();
			pw.close();
		}

	}
	
	public void getPartDevicTreeJeson(HttpServletRequest request,
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
							+ id + "' and (DATA_TYP = '设备' or DATA_TYP = '全部') ");
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
			System.out.println(myJson);
			pw.write(myJson);
			pw.flush();
			pw.close();
		}

	}
	
	
	
	public void getPartsTreeJeson(HttpServletRequest request,
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
							+ id + "' and (DATA_TYP = '物料' or DATA_TYP = '全部') ");
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
			System.out.println(myJson);
			pw.write(myJson);
			pw.flush();
			pw.close();
		}

	}
	
	public void getPartsTreeJesonTwo(HttpServletRequest request,
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
							+ id + "' and (DATA_TYP = '物料' or DATA_TYP = '全部') ");
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
	
	public void getPartDevTreeJeson(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String id = request.getParameter("id");
		String text = request.getParameter("text");
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
			if(id.indexOf("_C_") > 0)
			{
 
			    rs = s
					.executeQuery("    select PARTS_NUM  id, C_PARTS_NAM text,'true' leaf from   t_device  where DATA_TYP = '台账' AND  DEVICE_TYPE='"
							+ text + "'  ");
			}
			else
			{
				if(id.indexOf("_D") > 0) id="0";
				rs = s
						.executeQuery("    select if((select count(1) from c_parts_type where PARENT_ID = C.TYPE_ID)=0,CONCAT(type_id,'_C_',PARTS_TYPE),TYPE_ID)  id, PARTS_TYPE text,'false' leaf  from   c_parts_type C where PARENT_ID="
								+ id + " and (DATA_TYP = '设备' or DATA_TYP = '全部') ");
			}
	
			while (rs.next()) {
				String iconCls = "";
				if ("true".equals(rs.getString(3)))
					iconCls = "icon_edit";
				else
					iconCls =  "icon_folder";
				json.append("{iconCls:\'"+iconCls+"\',leaf:"+ rs.getString(3)+",id:'" + rs.getString(1)
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

	public void getOrgTreeJeson(HttpServletRequest request,
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
					.executeQuery("    select ORG_COD id, C_ORG_NAM text from   s_organize  where PARENT_COD='"
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
			System.out.println(myJson);
			pw.write(myJson);
			pw.flush();
			pw.close();
		}

	}
	public void getCodeTreeJeson(HttpServletRequest request,
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
			
				rs = s
						.executeQuery("    select KEY_ID id, KEY_TEXT text from   wb_key  where key_type='代码'  ");
				while (rs.next()) {
					json.append("{iconCls:\'icon_folder\',id:'" + rs.getString(1)
							+ "',text:'" + rs.getString(2) + "'},");
				}
				myJson = json.toString();
				if (myJson.endsWith(",")) {
					myJson = myJson.substring(0, myJson.lastIndexOf(","));
				}
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
			if (myJson.length() > 1 )
				myJson += "]";
			else
				myJson += "[]";
			pw.write(myJson);
			pw.flush();
			pw.close();
		}

	}
	
	public void getStoreTreeJeson(HttpServletRequest request,
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
				rs = s
						.executeQuery("    select STORAGE_COD id, STORAGE_NAM text,'0'  leaf from   c_storage_code  ");
				while (rs.next()) {
					json.append("{iconCls:\'icon_folder\',id:'" + rs.getString(1)
							+ "',text:'" + rs.getString(2) + "'},");
				}
				myJson = json.toString();
				if (myJson.endsWith(",")) {
					myJson = myJson.substring(0, myJson.lastIndexOf(","));
				}
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
			if (myJson.length() > 1 )
				myJson += "]";
			else
				myJson += "[]";
			pw.write(myJson);
			pw.flush();
			pw.close();
		}

	}
	
	
	
	// 盘存 查询sql
	public void getPanCunSql(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String STORAGE_COD = StringUtil.fetchString(request, "storeNam");
		String begTim = StringUtil.fetchString(request, "begTim");
		String endTim = StringUtil.fetchString(request, "endTim");

		StringBuffer sql = new StringBuffer("");
		if (!StringUtil.isEmpty(STORAGE_COD)) {
			sql.append("  and STORAGE_NAM='" + STORAGE_COD + "'");
		}
		if (!StringUtil.isEmpty(begTim)) {
			begTim = begTim.substring(0, 10);
			sql
					.append(" and  date_format(CHECK_DTE,'%Y-%m-%d')>='" + begTim
							+ "'");
		}
		if (!StringUtil.isEmpty(endTim)) {
			endTim = endTim.substring(0, 10);
			sql
					.append(" and  date_format(CHECK_DTE,'%Y-%m-%d')<='" + endTim
							+ "'");
		}
		request.setAttribute("whereSql", sql.toString());

	}
	
	public void getUnitChange(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String partCod = StringUtil.fetchString(request, "partCod");
		String unitName = StringUtil.fetchString(request, "unitName");
		Connection conn = null;
		Statement s = null;
		ResultSet rs = null;
		String jndi = StringUtil.fetchString(request, "sys.jndi");
		try {
			conn = DbUtil.getConnection(jndi);
			s = conn.createStatement();
		    ////1、判断是否为最小单位？
			Statement st33 = conn.createStatement();
			ResultSet r3 = st33.executeQuery("select UNIT_CHANGE FROM C_CODE WHERE FLD_ID='1' AND C_MEAN_STR = '"+unitName+"'");
			 int us =  1;	
			if (r3.next()) {
				String isMin = r3.getString("UNIT_CHANGE");
				if(!"否".equals(isMin)) //不等于否 就是 是最小单位
				{
					us = 1;
				}
				else//如果是标准单位
				{
					rs = s.executeQuery("SELECT  dp.STAND_CHANGE FROM c_parts_code AS dp WHERE dp.PARTS_COD =  '"+partCod+"' ");
					if (rs.next()) {
						us =  rs.getInt("STAND_CHANGE");	
					}
					
				}
			}
			
			response.getWriter().write(String.valueOf(us));
			
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			if (s != null)
				s.close();
			if (conn != null)
				conn.close();


		}

	}
	
	public void getUseStore(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String partCod = StringUtil.fetchString(request, "partCod");
		String stName = StringUtil.fetchString(request, "stName");
		String lineNum  =StringUtil.fetchString(request, "lineNum"); 
		Connection conn = null;
		Statement s = null;
		ResultSet rs = null;
		String myJson = "";
		String jndi = StringUtil.fetchString(request, "sys.jndi");
		try {
			conn = DbUtil.getConnection(jndi);
			s = conn.createStatement();
			rs = s.executeQuery("SELECT sum(if(((select UNIT_CHANGE FROM C_CODE WHERE FLD_ID='1' AND C_MEAN_STR =B.MIN_UNIT)='是'), B.OUTDEPOT_NUM,B.OUTDEPOT_NUM * (SELECT  dp.STAND_CHANGE FROM c_parts_code AS dp WHERE dp.PARTS_COD = B.PARTS_COD)))  FROM t_out_apply A,t_out_apply_detail B WHERE  A.PUR_NO = B.PUR_NO AND A.WF_STATUE = '1'  AND B.PARTS_COD =  '"+partCod+"' AND STORAGE_NAM = '"+stName+"'");
		    int us =  0;	
			while (rs.next()) {
				us =  rs.getInt(1);	
			}
			System.out.println("ddddddddddddddddddddddd "+String.valueOf(us));
			response.getWriter().write(String.valueOf(us)+"#"+lineNum);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			if (s != null)
				s.close();
			if (conn != null)
				conn.close();


		}

	}
	
	// 获得物料出库
	public void getWLOutWareJson(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String storeNam = StringUtil.fetchString(request, "storeNam");
		String begTim = StringUtil.fetchString(request, "begTim");
		String endTim = StringUtil.fetchString(request, "endTim");
        String status = StringUtil.fetchString(request, "sta");
        String dteTyp = "";
        if ( "出库".equals(status))
        	dteTyp = "OUTDEPOT_DTE";
        else
        	dteTyp = "PLAN_DTE";
		StringBuffer sql = new StringBuffer("");
		if (!StringUtil.isEmpty(storeNam)) {
			sql.append("  STORAGE_NAM='" + storeNam + "'");
		}
		if (!StringUtil.isEmpty(begTim)) {
			begTim = begTim.substring(0, 10);
			sql.append("  and date_format("+dteTyp+",'%Y-%m-%d')>='" + begTim
					+ "'");
		}
		if (!StringUtil.isEmpty(endTim)) {
			endTim = endTim.substring(0, 10);
			sql.append("  and date_format("+dteTyp+",'%Y-%m-%d')<='" + endTim
					+ "'");
		}
		if (!StringUtil.isEmpty(status)) sql.append("   and  OUT_STATUE='"+status+"' ");
		request.setAttribute("whereSql", sql.toString());

	}
	

	// 获得设备维修保养
	public void getDevicPlanJson(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String devNam = StringUtil.fetchString(request, "devNam");
		String begTim = StringUtil.fetchString(request, "begTim");
		String endTim = StringUtil.fetchString(request, "endTim");
		StringBuffer sql = new StringBuffer("");
		if (!StringUtil.isEmpty(devNam)) {
			sql.append("  and C_DEV_NAM='" + devNam + "'");
		}
		if (!StringUtil.isEmpty(begTim)) {
			begTim = begTim.substring(0, 10);
			sql.append("  and date_format(PLAN_MONTH,'%Y-%m-%d')>='" + begTim
					+ "'");
		}
		if (!StringUtil.isEmpty(endTim)) {
			endTim = endTim.substring(0, 10);
			sql.append("  and date_format(PLAN_MONTH,'%Y-%m-%d')<='" + endTim
					+ "'");
		}
		request.setAttribute("whereSql", sql.toString());
System.out.println("  33333333333333333333 " +sql.toString());
	}
	
	
	// 获得物料入库
		public void getWLInWareJson(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			String storeNam = StringUtil.fetchString(request, "storeNam");
			String begTim = StringUtil.fetchString(request, "begTim");
			String endTim = StringUtil.fetchString(request, "endTim");
	        String status = StringUtil.fetchString(request, "sta");
			StringBuffer sql = new StringBuffer("");
			String dteTyp = "";
		        if ("入库".equals(status))
		        	dteTyp = "INDEPOT_DTE";
		        else
		        	dteTyp = "PLAN_DTE";
			if (!StringUtil.isEmpty(storeNam)) {
				sql.append("  STORAGE_NAM='" + storeNam + "'");
			}
			if (!StringUtil.isEmpty(begTim)) {
				begTim = begTim.substring(0, 10);
				sql.append("  and date_format("+dteTyp+",'%Y-%m-%d')>='" + begTim
						+ "'");
			}
			if (!StringUtil.isEmpty(endTim)) {
				endTim = endTim.substring(0, 10);
				sql.append("  and date_format("+dteTyp+",'%Y-%m-%d')<='" + endTim
						+ "'");
			}
			if (!StringUtil.isEmpty(status)) sql.append("   and  IN_STATUE='"+status+"' ");
			request.setAttribute("whereSql", sql.toString());

		}
	
	 // 获得物料台账
		public void getPartList(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			String storeNam = StringUtil.fetchString(request, "storeNam");
	        String status = StringUtil.fetchString(request, "S_C_PARTS_NAM");
	        String partCod = StringUtil.fetchString(request, "partCod");
			StringBuffer sql = new StringBuffer("");
			if (!StringUtil.isEmpty(storeNam)) {
				sql.append("  STORAGE_NAM= '" + storeNam + "' ");
			}
			if (!StringUtil.isEmpty(status)) {
				sql.append(" and  C_PARTS_NAM  like '%" + status + "%'");
			}
			if (!StringUtil.isEmpty(partCod)) {
				sql.append("  and PARTS_COD  ='" + status + "'");
			}
			request.setAttribute("whereSql", sql.toString());

		}
		
		 // 获得物料台账历史
		public void getPartListHis(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			String storeNam = StringUtil.fetchString(request, "storeNam");
	        String status = StringUtil.fetchString(request, "S_C_PARTS_NAM");
	        String partCod = StringUtil.fetchString(request, "partCod");
	        String begTim = StringUtil.fetchString(request, "bgTim");
			String endTim = StringUtil.fetchString(request, "endTim");
			StringBuffer sql = new StringBuffer("");
			if (!StringUtil.isEmpty(storeNam)) {
				sql.append("  STORAGE_NAM='" + storeNam + "'");
			}
			if (!StringUtil.isEmpty(status)) {
				sql.append("  and C_PARTS_NAM  ='" + status + "'");
			}
			if (!StringUtil.isEmpty(partCod)) {
				sql.append("  and PARTS_COD  ='" + status + "'");
			}
			if (!StringUtil.isEmpty(begTim)) {
				begTim = begTim.substring(0, 10);
				sql.append("  and date_format(HIS_MONTH,'%Y-%m-%d')>='" + begTim
						+ "'");
			}
			if (!StringUtil.isEmpty(endTim)) {
				endTim = endTim.substring(0, 10);
				sql.append("  and date_format(HIS_MONTH,'%Y-%m-%d')<='" + endTim
						+ "'");
			}
			
			request.setAttribute("whereSql", sql.toString());

		}
	
	

	// 出库确认
	public void confirmOutPart(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

    	Connection conn = null;
		PreparedStatement sUpdate = null;
		String jndi = StringUtil.fetchString(request, "sys.jndi");	
		String grid = StringUtil.fetchString(request, "OutMainGrid");	
		
		JSONArray array=new JSONArray(grid);
		try {
			conn = DbUtil.getConnection(jndi);
			conn.setAutoCommit(false);
			//修改出库状态，修改库存数量
			Statement st = conn.createStatement();
			Statement st2 = conn.createStatement();
			Statement st3 = conn.createStatement();
			Statement st4 = conn.createStatement();
			Statement st5 = conn.createStatement();
			ResultSet resultSet = null,rsRecpNo = null;
			for(int i=0;i<array.length();i++)
			{
				String recpInNo = "";
				String isOldUpdateSQL="";
				sUpdate = conn
						.prepareStatement("update t_parts_out set WF_STATUE ='2',OUT_STATUE='出库',OUTDEPOT_DTE = now() where RECP_NO = ?");
				JSONObject item = array.getJSONObject(i);
				String recpNo = item.getString("RECP_NO"); //主键，出库单号
				String stockNam = item.getString("STORAGE_NAM"); //仓库名称
				String purNo = item.getString("PUR_NO");//申请单号，根据申请单号查询申请的数量
				String isOld = item.getString("IS_OLD");//是否旧件出库，如果是则自动生成入库计划，并将物料库存设置为“废件库”
				if ("出库".equals(item.getString("OUT_STATUE"))) continue; //已经出库的就不用在进行出库
				sUpdate.setString(1, recpNo);
				sUpdate.addBatch();
				// 增加旧件入库计划
				if ("1".equals(isOld))
				{
					//修改库存数量	
					rsRecpNo = st.executeQuery("SELECT CONCAT('FI-', left(now()+0,8),'-',nextval('PartIn')) "); //生成入库单号
					if (rsRecpNo.next()) {
						recpInNo = rsRecpNo.getString(1);
					}
					sUpdate.addBatch("insert into t_parts_in(RECP_NO,PUR_NO,PARTS_TYPE,IN_MOD,PLAN_DTE,STORAGE_COD,STORAGE_NAM,RECORD_TIM,RECORD_MAN,DEPT_COD,USERID) select '"+recpInNo+"',PUR_NO,PARTS_TYPE,'2',now(),'FP','废品库',now(),RECORD_MAN,DEPT_COD,USERID from t_parts_out where RECP_NO = '"+recpNo+"'");
					isOldUpdateSQL = ",STORAGE_COD='FP',STORAGE_NAM='废品库'";
				}
				//修改库存数量	
				resultSet = st2.executeQuery("SELECT *  FROM t_parts_out_detail WHERE RECP_NO = '"+recpNo+"'"); //出库明细
				//循环出库明细，然后修改相应台账表中的数据
				while (resultSet.next()) {
					String prCod = resultSet.getString("PARTS_COD");
					BigDecimal outNum = resultSet.getBigDecimal("OUTDEPOT_NUM");
					/////////////////////////////////////////
					//根据单换算成最小单位：根据单位找是不是最小单位，如果不是，则需要转换成最小单位，转换要根据物料代码中的关系转换
			///////////物料信息代码
					Statement st44 = conn.createStatement();
					ResultSet r4 = st44.executeQuery("SELECT dp.PARTS_TYPE,dp.PARTS_CLASS,dp.STAND_CHANGE FROM c_parts_code AS dp WHERE dp.PARTS_COD = '"+prCod+"'");
					BigDecimal changeNum = null ;
					String ptType="",ptClass = "";
					if (r4.next()) {
						changeNum = r4.getBigDecimal("STAND_CHANGE");
						ptType = r4.getString("PARTS_TYPE");
						ptClass = r4.getString("PARTS_CLASS");
					}
					String minUnit = resultSet.getString("MIN_UNIT");
					////1、判断是否为最小单位？
					Statement st33 = conn.createStatement();
					ResultSet r3 = st33.executeQuery("select UNIT_CHANGE FROM C_CODE WHERE FLD_ID='1' AND C_MEAN_STR = '"+minUnit+"'");
					//入库操作更改单价、成本、数量 
					if (r3.next()) {
						String isMin = r3.getString("UNIT_CHANGE");
						if(!"否".equals(isMin))
						{

						}
						else//如果是标准单位
						{
							//2、从物料信息表中找到换算关系,然后换算为最小，实际入库则是乘以换算率
							
							outNum = outNum.multiply(changeNum);
						}
					}
					if(st33 != null)
						   st33.close();
					if(st44 != null)
						   st44.close();
					//////////////////////////根据申请的数量，和出库的数量作对比，要是出库的大于申请的，则不能出库，而且还要判断所有的申请单已经出库的
					ResultSet rOutAlreadY = st3.executeQuery("select sum(d.OUTDEPOT_NUM) as sAlready from t_parts_out m,t_parts_out_detail d where m.OUT_STATUE = '出库' and  m.RECP_NO = d.RECP_NO and m.PUR_NO =  '"+purNo+"' and PARTS_COD = '"+prCod+"'");
					
					//ResultSet rOutApply = st4.executeQuery("SELECT d.OUTDEPOT_NUM FROM t_out_apply AS m , t_out_apply_detail AS d where m.PUR_NO = d.PUR_NO  and m.PUR_NO =  '"+purNo+"' and PARTS_COD = '"+prCod+"'");
					ResultSet rOutApply = st4.executeQuery("SELECT sum(if(((select UNIT_CHANGE FROM C_CODE WHERE FLD_ID='1' AND C_MEAN_STR =B.MIN_UNIT)='是'), B.OUTDEPOT_NUM,B.OUTDEPOT_NUM * (SELECT  dp.STAND_CHANGE FROM c_parts_code AS dp WHERE dp.PARTS_COD = B.PARTS_COD)))  FROM t_out_apply A,t_out_apply_detail B WHERE  A.PUR_NO = B.PUR_NO  AND B.PARTS_COD =  '"+prCod+"' AND A.PUR_NO = '"+purNo+"'");
					
					long applyNum = 0;
					if (rOutApply.next()) {
						applyNum = rOutApply.getLong(1);
					}
					if (rOutAlreadY.next()) {
						Long sAlready = rOutAlreadY.getLong(1);
						if ((outNum.longValue() + sAlready ) > applyNum)
						{
							 response.getWriter().write("物料代码为："+prCod+"的出库数量大于申请的数量，不能出库!");
							 throw new Exception("物料代码为："+prCod+"的出库数量大于申请的数量，不能出库!");
						}
					//	System.out.println("---------------------------- "+outNum +" | "+sAlready+" | "+ applyNum);
						
					}
					/////////////////////////
					ResultSet rPart = st3.executeQuery("SELECT STORE_NUM FROM t_part_list WHERE STORAGE_NAM = '"+stockNam+"' and PARTS_COD = '"+prCod+"'");
					if (rPart.next()) {
					     BigDecimal stockNum = rPart.getBigDecimal(1); 
                        if(stockNum.compareTo(outNum)== -1 ) //库存数量小于出库数量就不能出库
                        {
                        	//  throw new Exception("物料代码为："+prCod+"的数量不够，不能出库!");//抛出异常  
                        	  response.getWriter().write("物料代码为："+prCod+"的数量不够，不能出库!");
                        	  throw new Exception("物料代码为："+prCod+"的数量不够，不能出库!");
                        }
                        else //出库
                        {
                        	sUpdate.addBatch("update t_part_list set STORE_NUM = STORE_NUM - "+outNum +"  WHERE STORAGE_NAM = '"+stockNam+"' and PARTS_COD = '"+prCod+"'");
                        	// 增加旧件入库计划
            				if ("1".equals(isOld))
            				{
            					sUpdate.addBatch("insert into t_parts_in_detail(RECP_NO,PARTS_COD,C_PARTS_NAM,E_PARTS_NAM,STAND_TXT,PRICE_VAL,MIN_UNIT,RECORD_TIM,RECORD_MAN,DEPT_COD,USERID,INDEPOT_NUM) select '"+recpInNo+"',PARTS_COD,C_PARTS_NAM,E_PARTS_NAM,STAND_TXT,PRICE_VAL,MIN_UNIT,RECORD_TIM,RECORD_MAN,DEPT_COD,USERID,'"+outNum+"' from t_parts_out_detail where RECP_NO = '"+recpNo+"' and PARTS_COD = '"+prCod+"'");
            				}
                        }
					}
					else
					{
						 response.getWriter().write("物料代码为："+prCod+"的物料不在库存中，不能出库!");
						 throw new Exception("物料代码为："+prCod+"的物料不在库存中，不能出库!");//抛出异常 
					}
				}
			
				response.getWriter().write("出库成功!");
			}
			sUpdate.executeBatch();
			if(st2 != null) 
				st2.close();
			if(st != null)
			   st.close();
			if(st3 != null)
			   st3.close();
			
			if(st4 != null)
				   st4.close();
		} catch (Exception ex) {
			 response.getWriter().write("出库失败!");
			 conn.rollback();
			ex.printStackTrace();
		} finally {
			// response.getWriter().write("出库成功!");
			conn.commit();
			if (sUpdate != null)
				sUpdate.close();
			if (conn != null)
				conn.close();

		}

	}

	
	
	// 入库确认
	public void confirmInPart(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

    	Connection conn = null;
		PreparedStatement sUpdate = null;
		String jndi = StringUtil.fetchString(request, "sys.jndi");	
		String grid = StringUtil.fetchString(request, "inMainGrid");	
		JSONArray array=new JSONArray(grid);
		try {
			conn = DbUtil.getConnection(jndi);
			conn.setAutoCommit(false);
			//修改入库状态，修改库存数量
			Statement st = conn.createStatement();
			Statement st2 = conn.createStatement();
			ResultSet resultSet = null,rsRecpNo = null;
			for(int i=0;i<array.length();i++)
			{
				sUpdate = conn
						.prepareStatement("update t_parts_in set IN_STATUE ='入库',INDEPOT_DTE = now() where RECP_NO = ?");
				JSONObject item = array.getJSONObject(i);
				String recpNo = item.getString("RECP_NO"); //主键，出库单号
				
				String inMod = item.getString("IN_MOD"); //入库方式为旧件入库，则将仓库改为废件库
				if ("入库".equals(item.getString("IN_STATUE"))) 
				{
					continue; //已经入库的就不用在进行入库
				}
				sUpdate.setString(1, recpNo);
				sUpdate.addBatch();
				//修改库存数量	
				resultSet = st.executeQuery("SELECT *  FROM t_parts_in_detail WHERE RECP_NO = '"+recpNo+"'"); //入库明细
				//循环入库明细，然后修改相应台账表中的数据，如果台账中找不到则需要新增一条数据
			    int iDetail = 0;
				while (resultSet.next()) {
					iDetail ++;
					String prCod = resultSet.getString("PARTS_COD");
					///////////物料信息代码
					Statement st4 = conn.createStatement();
					ResultSet r4 = st4.executeQuery("SELECT dp.PARTS_TYPE,dp.PARTS_CLASS,dp.STAND_CHANGE FROM c_parts_code AS dp WHERE dp.PARTS_COD = '"+prCod+"'");
					BigDecimal changeNum = null ;
					String ptType="",ptClass = "";
					if (r4.next()) {
						changeNum = r4.getBigDecimal("STAND_CHANGE");
						ptType = r4.getString("PARTS_TYPE");
						ptClass = r4.getString("PARTS_CLASS");
					}
					BigDecimal inNum = resultSet.getBigDecimal("INDEPOT_NUM");
					//根据单换算成最小单位：根据单位找是不是最小单位，如果不是，则需要转换成最小单位，转换要根据物料代码中的关系转换
					String minUnit = resultSet.getString("MIN_UNIT");
					////1、判断是否为最小单位？
					Statement st3 = conn.createStatement();
					ResultSet r3 = st3.executeQuery("select UNIT_CHANGE FROM C_CODE WHERE FLD_ID='1' AND C_MEAN_STR = '"+minUnit+"'");
					//入库操作更改单价、成本、数量 
					if (r3.next()) {
						String isMin = r3.getString("UNIT_CHANGE");
						if(!"否".equals(isMin))
						{

						}
						else//如果是标准单位
						{
							//2、从物料信息表中找到换算关系,然后换算为最小，实际入库则是乘以换算率
							
							inNum = inNum.multiply(changeNum);
						}
					}
					if(st3 != null)
						   st3.close();
					if(st4 != null)
						   st4.close();
				    BigDecimal priceVal = resultSet.getBigDecimal("PRICE_VAL"); //单价成本
                    BigDecimal pmountVal = resultSet.getBigDecimal("AMOUNT"); //库存成本
					String stockNam = item.getString("STORAGE_NAM"); //仓库名称
					if ("2".equals(inMod)) //旧件入库，则入废品库 FP 
					{ 
						stockNam = "废品库";
					}
					ResultSet rPart = st2.executeQuery("SELECT *  FROM t_part_list WHERE STORAGE_NAM = '"+stockNam+"' and PARTS_COD = '"+prCod+"'");
					//入库操作更改单价、成本、数量 
					if (rPart.next()) {
                        BigDecimal StockPriceVal = rPart.getBigDecimal("STORE_PRICE"); //库存单价成本
                        if(StockPriceVal == null) StockPriceVal =  new BigDecimal("1");
                        BigDecimal StockAmountVal = rPart.getBigDecimal("STORE_AMOUNT"); //库存成本
                        if(StockAmountVal == null) StockAmountVal =  new BigDecimal("1");
                        BigDecimal StockNum = rPart.getBigDecimal("STORE_NUM");//当前库存量
                        BigDecimal stockPriceNow = StockNum.multiply(StockPriceVal).add(inNum.multiply(priceVal)).divide((StockNum.add(inNum)),4,BigDecimal.ROUND_HALF_UP);
                        //入库后的成本单价=当前库存量+入库量 / 库存成本
                        BigDecimal stockAmountNow = new java.math.BigDecimal(0) ;
                        if (stockPriceNow != null) 
                        	stockAmountNow = StockNum.add(inNum).multiply(stockPriceNow);
                        else
                        	stockAmountNow = new java.math.BigDecimal(0) ;
                        //库存成本计算公式为：当前此物料库存数量×单价成本＋本次入库数量×本次入库计划单价/(当前库存数量＋本次入库数量)
						//入库操作更改单价、成本、数量
                    	sUpdate.addBatch("update t_part_list set STORE_PRICE= "+stockPriceNow+",STORE_AMOUNT = "+stockAmountNow+",STORAGE_NAM = '"+stockNam+"',STORE_NUM = STORE_NUM + "+inNum +"  WHERE STORAGE_NAM = '"+stockNam+"' and PARTS_COD = '"+prCod+"'");
 
 
					}
					else//台账库存中没有相应的库存信息，则需要增加新的记录
					{
						//入库操作更改单价、成本、数量
                    	sUpdate.addBatch("insert into t_part_list (MIN_UNIT,STORAGE_NAM,PARTS_TYPE,PARTS_COD,C_PARTS_NAM,STAND_TXT,STORE_NUM,STORE_PRICE,STORE_AMOUNT,RECORD_TIM,RECORD_MAN,DEPT_COD,USERID,PARTS_CLASS)" +
    							"                         select '"+minUnit+"','"+stockNam+"','"+ptType+"',PARTS_COD,C_PARTS_NAM,STAND_TXT,"+inNum+",PRICE_VAL,AMOUNT,             now(),RECORD_MAN,DEPT_COD,USERID,'"+ptClass+"' from t_parts_in_detail WHERE RECP_NO = '"+recpNo+"' and PARTS_COD = '"+prCod+"'");
					}
				}
			 if(iDetail ==0) 
			 {
				 response.getWriter().write("入库单号为："+recpNo+"的入库单没有明细物料，不能入库!");
				 throw new Exception("入库单号为："+recpNo+"的入库单没有明细物料，不能入库!");//抛出异常  
			 }
				
			}
			sUpdate.executeBatch();
			if(st2 != null) 
				st2.close();
			if(st != null)
			   st.close();
			response.getWriter().write("入库成功!");
		} catch (Exception ex) {
			response.getWriter().write("入库失败!");
			conn.rollback();
			ex.printStackTrace();
			 
		} finally {
		//	 response.getWriter().write("入库成功!");
			conn.commit();
			if (sUpdate != null)
				sUpdate.close();
			if (conn != null)
				conn.close();

		}

	}
}
