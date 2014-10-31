package com.xuelang.partmanage;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.webbuilder.utils.DbUtil;
import com.webbuilder.utils.StringUtil;

public class ShipPm {


	// 获得设备保养计划
	public void getDevPlanSQL(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String nowMonth = StringUtil.fetchString(request, "nowMonth");
		String shipCod = StringUtil.fetchString(request, "shipCod");
		StringBuffer sql = new StringBuffer(" 1=1 ");
		String devNo = StringUtil.fetchString(request, "devNo");
		if (!StringUtil.isEmpty(devNo)) {
			sql.append("  and DEV_NO='" + devNo + "'");
		}
		
		if (!StringUtil.isEmpty(shipCod)) {
			sql.append("  and SHIP_DEV_NO='" + shipCod + "'");
		}
		if (!StringUtil.isEmpty(nowMonth)) {
			nowMonth = nowMonth.substring(0, 7).replace("-", "");
			sql
					.append("  and PLAN_MONTH='" + nowMonth+ "'");
		}
		request.setAttribute("whereSql", sql.toString());
	}
	// 获得设备保养记录
	public void getDevRecordSQL(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String nowMonth = StringUtil.fetchString(request, "nowMonth");
		String shipCod = StringUtil.fetchString(request, "shipCod");
		StringBuffer sql = new StringBuffer(" 1=1 ");
		String devNo = StringUtil.fetchString(request, "devNo");
		if (!StringUtil.isEmpty(devNo)) {
			sql.append("  and DEV_NO='" + devNo + "'");
		}
		
		if (!StringUtil.isEmpty(shipCod)) {
			sql.append("  and SHIP_DEV_NO='" + shipCod + "'");
		}
		if (!StringUtil.isEmpty(nowMonth)) {
			nowMonth = nowMonth.substring(0,7).replace("-", "");
			sql
					.append("  and PLAN_MONTH='" + nowMonth+ "'");
		}
		request.setAttribute("whereSql", sql.toString());
	}
	// 获得设备保养记录
	public void getShipJCSQL(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String shipNam = StringUtil.fetchString(request, "shipNam");
		StringBuffer sql = new StringBuffer(" 1=1 ");
		if (!StringUtil.isEmpty(shipNam)) {
			sql.append("  and SHIP_DEV_NO  like  '%" + shipNam + "%'");
		}
		request.setAttribute("whereSql", sql.toString());
	}
}
