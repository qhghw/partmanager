package com.xuelang.partmanage;

import java.sql.Connection;
import java.sql.PreparedStatement;
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

public class CrewContact {
	public void createSalary(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String C_SHIP_NAM = StringUtil.fetchString(request, "csn");
		String month = StringUtil.fetchString(request, "month");
		String cIds = StringUtil.fetchString(request, "cIds");
		int year = Calendar.getInstance().get(Calendar.YEAR);
		String userId = (String) request.getSession()
				.getAttribute("sys.userId");
		String tId = (String) request.getSession()
				.getAttribute("sys.tentantId");
		String dc = (String) request.getSession().getAttribute("dept_cod");
		Connection conn = null;
		try {
			conn = DbUtil.getConnection("java:comp/env/jdbc/orcale_news");
			int m = getMonth2(month);
			List list = getCrewInfo(conn, cIds);
			doCreate(conn, m, list, Integer.parseInt(month), String
					.valueOf(year), C_SHIP_NAM, userId, tId, dc);
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

	public void doCreate(Connection conn, int m, List list, int month,
			String year, String C_SHIP_NAM, String userId, String tId, String dc) {
		int day = 0;// 考勤天数
		double gp = 0.0;// 应发工资
		double s = 0.0;// 实发工资
		String ym = "";
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for (int i = 0; i < list.size(); i++) {
			Contact c = (Contact) list.get(i);

			String uTime = c.getUpTime();
			String dTime = c.getDownTime();
			if (null != uTime && !"".equals(uTime)) {
				uTime = uTime.substring(0, 10);
			}
			if (null != dTime && !"".equals(dTime)) {
				dTime = dTime.substring(0, 10);
			} else {
				dTime = sdf.format(date);

			}

			String cId = c.getCId();
			double salary = c.getSalary();
			int m1 = getMonth1(uTime);
			int m2 = getMonth1(dTime);
			if (month > m1 && month < m2) {
				gp = salary;
				s = salary;
				day = m;
			} else if (month == m1 && month < m2) {
				int d1 = day(uTime);
				day = (m - d1);
				s = salary / m * day;
				gp = salary;
			} else if (month > m1 && month == m2) {
				int d1 = day(dTime);
				day = d1;
				s = salary / m * d1;
				gp = salary;
			} else if (month == m1 && month == m2) {
				int d1 = day(uTime);
				int d2 = day(dTime);
				day = (d2 - d1);
				s = salary / m * day;
				gp = salary;
			} else {
				day = 0;
				s = 0;
				gp = 0;
			}

			if (String.valueOf(month).length() < 2) {
				ym = year + "0" + month;
			} else {
				ym = year + month;

			}
			int sum = query(conn, cId, ym);
			if (sum == 0) {
				insert(conn, c, month, day, gp, s, C_SHIP_NAM, userId, tId, ym,
						dc);
			} else {
				update(conn, day, gp, s, ym, Integer.parseInt(c.getCId()));
			}
		}

	}

	public int query(Connection conn, String cId, String SALARY_MONTH) {
		String sql = "select count(CREW_ID) from SM_CREW_SALARY where CREW_ID="
				+ cId + " and SALARY_MONTH='" + SALARY_MONTH + "'";
		int num = 0;
		Statement st = null;
		ResultSet rs = null;
		try {
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			if (rs.next()) {
				num = rs.getInt(1);
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

	public void insert(Connection conn, Contact c, int month, int day,
			double gp, double s, String C_SHIP_NAM, String userId, String tId,
			String ym, String dc) {
		String sql = "insert into SM_CREW_SALARY(SALARY_ID,CREW_ID,WORK_COD,DEPT_COD,C_CREW_NAM,SALARY_MONTH,"
				+ "WORK_DTE,GROSS_PAY,SALARY,C_SHIP_NAM,USERID,TENTANTID)values(SEQ_GET_KEY_ID.nextVal,'"
				+ c.getCId()
				+ "','"
				+ c.getWc()
				+ "','"
				+ dc
				+ "','"
				+ c.getCsn()
				+ "',"
				+ ym
				+ ","
				+ day
				+ ","
				+ gp
				+ ","
				+ s
				+ ",'" + C_SHIP_NAM + "','" + userId + "','" + tId + "')";
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

	public void update(Connection conn, int day, double gp, double s,
			String SALARY_MONTH, int cId) {
		String sql = "update  SM_CREW_SALARY set WORK_DTE=" + day
				+ " ,GROSS_PAY=" + gp + ",SALARY=" + s + " where CREW_ID="
				+ cId + " and SALARY_MONTH='" + SALARY_MONTH + "'";

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

	public int getMonth1(String date) {
		int m = 0;
		if (null != date && date.length() > 0) {
			String[] dates = date.split("-");
			if (dates.length >= 2) {
				m = Integer.parseInt(dates[1]);
			}
		}

		return m;
	}

	public int day(String date) {
		int m = 0;
		if (null != date && date.length() > 0) {
			String[] dates = date.split("-");
			if (dates.length >= 2) {
				m = Integer.parseInt(dates[2]);
			}
		}

		return m;
	}

	public int getMonth2(String month) {
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int m = 0;
		if ("1".equals(month) || "3".equals(month) || "5".equals(month)
				|| "7".equals(month) || "8".equals(month) || "10".equals(month)
				|| "12".equals(month)) {
			m = 31;
		}
		if ("4".equals(month) || "6".equals(month) || "9".equals(month)
				|| "11".equals(month)) {
			m = 30;
		}
		if ("2".equals(month)) {
			if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
				m = 29;
			} else {
				m = 28;
			}

		}
		return m;
	}

	public List getCrewInfo(Connection conn, String cIds) {
		List list = new ArrayList();
		String sql = "select sc.CREW_ID,UP_TIM,DOWN_TIM,SALARY_STD,WORK_COD,DEPT_COD,sc.C_CREW_NAM "
				+ "from SM_CREW sc,SM_CREW_CONTRACT scc "
				+ "where sc.crew_id= scc.crew_id and sc.crew_id in ("
				+ cIds
				+ ")";
		Statement st = null;
		ResultSet rs = null;
		try {
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next()) {
				Contact c = new Contact();
				c.setCId(rs.getString("CREW_ID"));
				c.setUpTime(rs.getString("UP_TIM"));
				c.setDownTime(rs.getString("DOWN_TIM"));
				c.setSalary(rs.getDouble("SALARY_STD"));
				c.setWc(rs.getString("WORK_COD"));
				c.setDc(rs.getString("DEPT_COD"));
				c.setCsn(rs.getString("C_CREW_NAM"));
				list.add(c);
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
}
