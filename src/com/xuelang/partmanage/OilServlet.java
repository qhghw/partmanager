package com.xuelang.partmanage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webbuilder.utils.DbUtil;

/**
 * Servlet implementation class OilServlet
 */
public class OilServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public OilServlet() {
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String sdn = request.getParameter("sdn");// 船舶编号
		String mmsi = request.getParameter("mmsi");// MMSI
		String ot = request.getParameter("ot");// 记录时间
		String hon = request.getParameter("hon");// 重油
		String lon = request.getParameter("lon");// 轻油
		String eon = request.getParameter("eon");// 机油
		String wwn = request.getParameter("wwn");// 洗舱水
		String fwn = request.getParameter("fwn");// 淡水
		String uid = "82CA1373807A47A28D1188C1391A8ACE";
		String tid = "O001";
		String jndi = "java:comp/env/jdbc/orcale_news";
		Connection conn = null;
		try {
			conn = DbUtil.getConnection(jndi);
			if (null != sdn && !"".equals(sdn)) {
				insert(conn, sdn, mmsi, ot, hon, lon, eon, wwn, fwn, uid, tid);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	public void insert(Connection conn, String sdn, String mmsi, String ot,
			String hon, String lon, String eon, String wwn, String fwn,
			String uid, String tid) {
		PreparedStatement ps = null;
		String sql = "insert into SM_OIL_STORE(OIL_ID,SHIP_DEV_NO,H_OIL_NUM,L_OIL_NUM,"
				+ "OIL_TIM,E_OIL_NUM,W_WATER_NUM,F_WATER_NUM,MMSI,USERID,TENTANTID,TYPE)values("
				+ "SEQ_GET_KEY_ID.nextval,?,?,?,to_date(?,'yyyy-mm-dd'),?,?,?,?,?,?,'1')";
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, sdn);
			ps.setString(2, hon);
			ps.setString(3, lon);
			ps.setString(4, ot);
			ps.setString(5, eon);
			ps.setString(6, wwn);
			ps.setString(7, fwn);
			ps.setString(8, mmsi);
			ps.setString(9, uid);
			ps.setString(10, tid);
			ps.executeUpdate();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

}
