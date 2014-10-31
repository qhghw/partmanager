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

public class ShipCode {

	// 获得比价记录查询
	public void getCangKuSql(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String shipCod = StringUtil.fetchString(request, "shipCod");
		String storageNam = StringUtil.fetchString(request, "storageNam");
		StringBuffer sql = new StringBuffer("");
		if (!StringUtil.isEmpty(shipCod)) {
			sql.append(" SHIP_DEV_NO='"+shipCod+"' ");
		}
		if (!StringUtil.isEmpty(storageNam)) {
			sql.append(" and STORAGE_NAM  like '%"+storageNam+"%'");
		}
		request.setAttribute("whereSql", sql.toString());
	}
	
}
