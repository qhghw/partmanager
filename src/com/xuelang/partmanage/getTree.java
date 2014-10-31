package com.xuelang.partmanage;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.webbuilder.utils.DbUtil;
import com.webbuilder.utils.StringUtil;

public class getTree {

	// 船舶预报查询
	public void getTreeJson(HttpServletRequest request,
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
				rs = s
				.executeQuery("select CWBT_COD id,C_EQUIPMENT_NAM  text  from   SP_SHIPMANAGE_CWBT  where "
						+ " PARENT_CODE= CWBT_COD");
			}
			else
			{
			rs = s
					.executeQuery("select CWBT_COD id,C_EQUIPMENT_NAM  text  from   SP_SHIPMANAGE_CWBT  where "
							+ " PARENT_CODE= '" + id + "'  and  PARENT_CODE<>CWBT_COD");
			}
			while (rs.next()) {
							json.append("{iconCls:\'icon_folder\',id:'" + rs.getString(1) + "',text:'"
						+ rs.getString(2) + "'},");

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

	// 部门查询
	public void getDeptJson(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String id = request.getParameter("id");
		StringBuffer json = new StringBuffer("[");
		request.setCharacterEncoding("UTF-8");
		PrintWriter pw = response.getWriter();
		Connection conn = null;
		Statement s = null;
		ResultSet rs = null;
		String myJson = "";
		String jndi = "java:comp/env/jdbc/orcale_news";
		try {
			conn = DbUtil.getConnection(jndi);
			s = conn.createStatement();
			if (id == null || "".equals(id) || "tree__root".equals(id)) {
				rs = s
						.executeQuery("select dept_id id,org_na  text  from   hd_portlsp_2.c_dept  where "
								+ " org_parent_id is null");
			} else {
				rs = s
						.executeQuery("select dept_id id,org_na  text  from   hd_portlsp_2.c_dept  where "
								+ " org_parent_id= '" + id + "'");
			}		while (rs.next()) {
				json.append("{iconCls:\'icon_folder\',id:'" + rs.getString(1) + "',text:'"
						+ rs.getString(2) + "'},");
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
		request.setAttribute("deptQuery.KEY_VALUE", myJson);

	}

	// 部门comboTree
	public void getDeptJson1(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String id = request.getParameter("id");
		StringBuffer json = new StringBuffer("[");
		request.setCharacterEncoding("UTF-8");
	
		Connection conn = null;
		Statement s = null;
		ResultSet rs = null;
		String myJson = "";
		String jndi = "java:comp/env/jdbc/orcale_news";
		try {
			conn = DbUtil.getConnection(jndi);
			s = conn.createStatement();
			if (id == null || "".equals(id) || "tree__root".equals(id)) {
				id = "-1";
			}
			rs = s
					.executeQuery("select dept_id id,org_na  text  from   hd_portlsp_2.c_dept  where "
							+ " org_parent_id= '" + id + "'");
			while (rs.next()) {
				json.append("{iconCls:\'icon_folder\',id:'" + rs.getString(1) + "',text:'"
						+ rs.getString(2) + "'},");
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
		}
		System.out.println(myJson);
		request.setAttribute("deptQuery.KEY_VALUE", myJson);

	}


	// 物料查询
	public void getWuLiaoTreeJson(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String id = request.getParameter("id");
		String PM_TYP=request.getParameter("PM_TYP");
		
		StringBuffer json = new StringBuffer("[");
		request.setCharacterEncoding("UTF-8");
		PrintWriter pw = response.getWriter();
		Connection conn = null;
		Statement s = null;
		ResultSet rs = null;
		String myJson = "";
		String jndi = "java:comp/env/jdbc/orcale_news";
		try {
			conn = DbUtil.getConnection(jndi);
			s = conn.createStatement();
			if (id == null || "".equals(id) || "tree__root".equals(id)) {
				id = "-1";
			}
			rs = s
					.executeQuery(" select PM_COD id,C_PM_NAM||decode(MODEL_TXT,'','','-'||MODEL_TXT)|| decode(STAND_TXT,'','','-'||STAND_TXT) text from  SM_PM_CODE  where PARENT_COD='"+id+"'   and PM_TYP='"+PM_TYP+"'");
			while (rs.next()) {
				json.append("{iconCls:\'icon_folder\',id:'" + rs.getString(1) + "',text:'"
						+ rs.getString(2) + "'},");
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
	

	
	

}
