package com.xuelang.partmanage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webbuilder.utils.DbUtil;
import com.webbuilder.utils.StringUtil;

public class CommonAction {
	public void dispCrew(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String cid = request.getParameter("cid");
		request.setAttribute("cid", cid);
	}

	public void queryPlan(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String begin = StringUtil.fetchString(request, "begin");
		String end = StringUtil.fetchString(request, "end");
		StringBuffer sql = new StringBuffer("");
		if (!StringUtil.isEmpty(begin)) {
			begin = begin.substring(0, 10);
			sql.append("  and  to_char(PLAN_TIM,'YYYY-MM-DD') >= '" + begin
					+ "'");
		}
		if (!StringUtil.isEmpty(end)) {
			end = end.substring(0, 10);
			sql
					.append("  and  to_char(PLAN_TIM,'YYYY-MM-DD') <= '" + end
							+ "'");
		}
		request.setAttribute("whereSql", sql.toString());
	}

	public void setCheckNoWhereSql(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String SEQ_ID = StringUtil.fetchString(request, "SEQ_ID");
		String opeType = StringUtil.fetchString(request, "opeType");
		StringBuffer sql = new StringBuffer("");
		if ("modify".equals(opeType)) {
			sql.append("  and   SEQ_ID <> '" + SEQ_ID + "'");
		} else {
			sql.append("  and   SEQ_ID is not null ");
		}
		request.setAttribute("whereSql", sql.toString());

	}

	public void setCheckNoWhereSql1(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String SEQ_ID = StringUtil.fetchString(request, "SEQ_ID");
		StringBuffer sql = new StringBuffer("");
		if (!"".equals(SEQ_ID)) {
			sql.append("  and   SEQ_ID <> '" + SEQ_ID + "'");
		} else {
			sql.append("  and   SEQ_ID is not null ");
		}
		request.setAttribute("whereSql", sql.toString());

	}

	public void setCheckNoWhereSql2(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String SEQ_ID = StringUtil.fetchString(request, "SEQ_ID1");
		String opeType = StringUtil.fetchString(request, "opeType");
		StringBuffer sql = new StringBuffer("");
		if ("modify".equals(opeType)) {
			sql.append("  and   SEQ_ID <> '" + SEQ_ID + "'");
		} else {
			sql.append("  and   SEQ_ID is not null ");
		}
		request.setAttribute("whereSql", sql.toString());

	}

	public void setCheckNoWhereSql3(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String SEQ_ID = StringUtil.fetchString(request, "SEQ_ID2");
		String opeType = StringUtil.fetchString(request, "opeType");
		StringBuffer sql = new StringBuffer("");
		if ("modify".equals(opeType)) {
			sql.append("  and   SEQ_ID <> '" + SEQ_ID + "'");
		} else {
			sql.append("  and   SEQ_ID is not null ");
		}
		request.setAttribute("whereSql", sql.toString());

	}

	public void setCheckBox(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String clientCode = request.getParameter("ctc");
		request.setAttribute("clientCode", clientCode);

	}

	public void setDate(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String time1 = sdf.format(date);
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, -30);
		Date date1 = c.getTime();
		String time2 = sdf.format(date1);
		request.setAttribute("time1", time1);
		request.setAttribute("time2", time2);

	}



	public void updateOpertor(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String CLIENT_COD = StringUtil.fetchString(request, "CLIENT_COD");
		String belongId = StringUtil.fetchString(request, "belongId");
		String userId = (String) request.getSession()
				.getAttribute("sys.userId");
		String tentantId = (String) request.getSession().getAttribute(
				"sys.tentantId");
		Connection conn = null;
		try {
			conn = DbUtil.getConnection("java:comp/env/jdbc/orcale_news");
			String strs[] = belongId.trim().split(",");
			if (strs.length > 0) {
				deleteOperator(conn, CLIENT_COD, userId, tentantId);
				insertOperator(conn, CLIENT_COD, strs, userId, tentantId);
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

	public void updateOpertor1(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String CLIENT_COD = StringUtil.fetchString(request, "CLIENT_COD");
		String belongId = StringUtil.fetchString(request, "belongId");
		String userId = (String) request.getSession()
				.getAttribute("sys.userId");
		String tentantId = (String) request.getSession().getAttribute(
				"sys.tentantId");
		Connection conn = null;
		try {
			conn = DbUtil.getConnection("java:comp/env/jdbc/orcale_news");
			String strs[] = belongId.trim().split(",");
			if (strs.length > 0) {
				deleteOperator1(conn, CLIENT_COD, userId, tentantId);
				insertOperator1(conn, CLIENT_COD, strs, userId, tentantId);
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

	public void initRela(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// String cType = StringUtil.fetchString(request, "cType");
		Connection conn = null;
		try {
			conn = DbUtil.getConnection("java:comp/env/jdbc/orcale_news");
			int len = checkRela(conn);
			if (len == 0) {
				List list = getRela(conn);
				String userId = (String) request.getSession().getAttribute(
						"sys.userId");
				String tentantId = (String) request.getSession().getAttribute(
						"sys.tentantId");
				insertRela(conn, list, userId, tentantId);
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

	public void updateRela(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String CLIENT_COD = StringUtil.fetchString(request, "CLIENT_COD");
		String ctc = StringUtil.fetchString(request, "ctc");
		Connection conn = null;
		try {
			conn = DbUtil.getConnection("java:comp/env/jdbc/orcale_news");
			update1(conn, CLIENT_COD);
			update2(conn, CLIENT_COD, ctc);
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

	public void update1(Connection conn, String CLIENT_COD) {
		String sql = "update  C_CLIENT_RELA set CLIENT_COD='" + CLIENT_COD
				+ "' where  CLIENT_COD='clientTemp'";
		Statement st = null;
		try {

			st = conn.createStatement();
			st.executeUpdate(sql);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (st != null) {
					st.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public void update2(Connection conn, String CLIENT_COD, String ctc) {
		String sql = "";
		Statement st = null;
		try {
			st = conn.createStatement();
			String[] strs = ctc.split(",");
			for (int i = 0; i < strs.length; i++) {
				String str = strs[i];
				sql = "update  C_CLIENT_RELA set SURE_ID='1' where  CLIENT_COD='"
						+ CLIENT_COD + "' and CLIENT_TYPE_COD='" + str + "'";
				st.addBatch(sql);

			}
			st.executeBatch();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (st != null) {
					st.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public void insertRela(Connection conn, List list, String userId,
			String tentantId) {
		Statement st = null;
		String sql = "";
		try {
			st = conn.createStatement();
			if (list.size() > 0) {

				for (int i = 0; i < list.size(); i++) {
					String ctc = (String) list.get(i);
					sql = "insert into C_CLIENT_RELA(SEQ_ID,CLIENT_COD,CLIENT_TYPE_COD,SURE_ID,USERID,TENTANTID"
							+ ")values(sys_guid(),'clientTemp','"
							+ ctc
							+ "','0','" + userId + "','" + tentantId + "')";

					st.addBatch(sql);
				}
				st.executeBatch();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (st != null) {
					st.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public void deleteOperator(Connection conn, String CLIENT_COD,
			String userId, String tentantId) {
		Statement st = null;
		String sql = "delete from C_CLIENT_OPERATOR where CLIENT_COD='"
				+ CLIENT_COD + "' and USERID='" + userId + "' and TENTANTID='"
				+ tentantId + "'";
		try {
			st = conn.createStatement();
			st.executeUpdate(sql);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (st != null) {
					st.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public void deleteOperator1(Connection conn, String CLIENT_COD,
			String userId, String tentantId) {
		Statement st = null;
		String sql = "delete from C_CLIENT_SALESMAN where CLIENT_COD='"
				+ CLIENT_COD + "' and USERID='" + userId + "' and TENTANTID='"
				+ tentantId + "'";
		try {
			st = conn.createStatement();
			st.executeUpdate(sql);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (st != null) {
					st.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public void insertOperator(Connection conn, String CLIENT_COD,
			String[] strs, String userId, String tentantId) {
		Statement st = null;
		String sql = "";
		try {
			st = conn.createStatement();
			for (int i = 0; i < strs.length; i++) {
				String ctc = (String) strs[i];
				sql = "insert into C_CLIENT_OPERATOR(SEQ_ID,CLIENT_COD,OPER_COD,USERID,TENTANTID"
						+ ")values(sys_guid(),'"
						+ CLIENT_COD
						+ "','"
						+ ctc
						+ "','" + userId + "','" + tentantId + "')";
				if (!"".equals(ctc)) {
					st.addBatch(sql);
				}
			}
			st.executeBatch();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (st != null) {
					st.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public void insertOperator1(Connection conn, String cType, String[] strs,
			String userId, String tentantId) {
		Statement st = null;
		String sql = "";
		try {
			st = conn.createStatement();
			for (int i = 0; i < strs.length; i++) {
				String ctc = (String) strs[i];
				sql = "insert into C_CLIENT_SALESMAN(SEQ_ID,CLIENT_COD,OPER_COD,USERID,TENTANTID"
						+ ")values(sys_guid(),'"
						+ cType
						+ "','"
						+ ctc
						+ "','"
						+ userId + "','" + tentantId + "')";
				if (!"".equals(ctc)) {
					st.addBatch(sql);
				}
			}
			st.executeBatch();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (st != null) {
					st.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public List getRela(Connection conn) {
		List list = new ArrayList();
		String sql = "select CLIENT_TYPE_COD from C_CLIENT_TYPE";
		Statement st = null;
		ResultSet rs = null;
		try {

			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next()) {
				list.add(rs.getString("CLIENT_TYPE_COD"));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return list;
	}

	public int checkRela(Connection conn) {
		int num = 0;
		String sql = "select count(*)NUMS from C_CLIENT_RELA where  CLIENT_COD='clientTemp'";
		Statement st = null;
		ResultSet rs = null;
		try {
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			if (rs.next()) {
				num = rs.getInt("NUMS");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return num;
	}
}
