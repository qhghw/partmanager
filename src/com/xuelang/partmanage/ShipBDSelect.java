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

public class ShipBDSelect {

	//合同主信息查询
	public void getConShip(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	
		StringBuffer sql = new StringBuffer(" and 1=1");
		String mmsi = StringUtil.fetchString(request, "mmsi");
		String bgtime = StringUtil.fetchString(request, "bgtime");
		String edtime = StringUtil.fetchString(request, "edtime");
		String contractnbr = StringUtil.fetchString(request, "contractnbr");
		String handoverid = StringUtil.fetchString(request, "handoverid");
		String partycod = StringUtil.fetchString(request, "partycod");
		if (!StringUtil.isEmpty(bgtime)) {
			sql.append("  and to_char(CONTRACT_SHIP_DTE,'yyyy-mm-dd') >= '" + bgtime + "'");
		}
		if (!StringUtil.isEmpty(edtime)) {
			sql.append("  and to_char(CONTRACT_SHIP_DTE,'yyyy-mm-dd') <= '" + edtime + "'");
		}
		if (!StringUtil.isEmpty(mmsi)) {
			sql.append("  and  c_ship_data.MMSI_COD = '" + mmsi + "'");
		}
		if (!StringUtil.isEmpty(contractnbr)) {
			sql.append("  and  CONTRACT_SHIP_NBR  like  '%" + contractnbr + "%'");
		}
		if (!StringUtil.isEmpty(handoverid)) {
			sql.append("  and  HANDOVER_ID = '" + handoverid + "'");
		}
		if(partycod!=null&&!partycod.equals(""))
		partycod=java.net.URLDecoder.decode(partycod,"UTF-8");
	
		if (!StringUtil.isEmpty(partycod)) {
			sql.append("  and  PARTY_B_COD in  (select  CLIENT_COD from  c_client  where C_CLIENT_NAM like '%"+partycod+"%' or   E_CLIENT_NAM like '%"+partycod+"%' )" );
		}
		String shipnam = StringUtil.fetchString(request, "shipnam");
		if(shipnam!=null&&!shipnam.equals(""))
		shipnam=java.net.URLDecoder.decode(shipnam,"UTF-8");
		if (!StringUtil.isEmpty(shipnam)) {
			sql.append("  and ( c_ship_data.E_SHIP_NAM   like  '%" + shipnam + "%'  " +
					"     or   c_ship_data.C_SHIP_NAM   like  '%" + shipnam + "%'            )    "  );
		}
		request.setAttribute("whereSql", sql.toString());
	}
	

	//合同明细信息查询
	public void getConShipDetail(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	
		StringBuffer sql = new StringBuffer(" and 1=1");
		String mmsi = StringUtil.fetchString(request, "mmsi");
		if (!StringUtil.isEmpty(mmsi)) {
			sql.append("  and  c_ship_data.MMSI_COD = '" + mmsi + "'");
		}
		String shipnam = StringUtil.fetchString(request, "shipnam");
		if(shipnam!=null&&!shipnam.equals(""))
		shipnam=java.net.URLDecoder.decode(shipnam,"UTF-8");

		if (!StringUtil.isEmpty(shipnam)) {
			sql.append("  and ( c_ship_data.E_SHIP_NAM   like  '%" + shipnam + "%'  " +
					"     or   c_ship_data.C_SHIP_NAM   like  '%" + shipnam + "%'            )    "  );
		}
		request.setAttribute("whereSql", sql.toString());
	}
	/*
	保养查询记录
	*/
	public void getSmDev(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	
		StringBuffer sql = new StringBuffer(" and 1=1");
		String mmsi = StringUtil.fetchString(request, "mmsi");
		if (!StringUtil.isEmpty(mmsi)) {
			sql.append("  and  c_ship_data.MMSI_COD = '" + mmsi + "'");
		}
		String shipnam = StringUtil.fetchString(request, "shipnam");
		if(shipnam!=null&&!shipnam.equals(""))
		shipnam=java.net.URLDecoder.decode(shipnam,"UTF-8");

		if (!StringUtil.isEmpty(shipnam)) {
			sql.append("  and ( c_ship_data.E_SHIP_NAM   like  '%" + shipnam + "%'  " +
					"     or   c_ship_data.C_SHIP_NAM   like  '%" + shipnam + "%'            )    "  );
		}
		request.setAttribute("whereSql", sql.toString());
	}
	
	public void getShipDest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	
		StringBuffer sql = new StringBuffer(" 1=1");
		 
		
		
		String  portnam= StringUtil.fetchString(request, "portnam");
		if(portnam!=null&&!portnam.equals(""))
		portnam=java.net.URLDecoder.decode(portnam,"UTF-8");

		if (!StringUtil.isEmpty(portnam)) {
			sql.append("  and  PORT_NAM  like '%" + portnam + "%'");
		}
		String agentnam = StringUtil.fetchString(request, "agentnam");	
		if(agentnam!=null&&!agentnam.equals(""))
		agentnam=java.net.URLDecoder.decode(agentnam,"UTF-8");

		
		if (!StringUtil.isEmpty(agentnam)) {
			sql.append("  and ( AGENT_CORP_NAM   like  '%" + agentnam + "%'  "  );
		}
		String relanam = StringUtil.fetchString(request, "relanam");
		if(relanam!=null&&!relanam.equals(""))
		relanam=java.net.URLDecoder.decode(relanam,"UTF-8");

		if (!StringUtil.isEmpty(relanam)) {
			sql.append("  and ( RELA_MAN   like  '%" + agentnam + "%'  "  );
		}
		
		request.setAttribute("whereSql", sql.toString());

	}

	public void getShipDis(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		StringBuffer sql = new StringBuffer(" 1=1");

		String mmsi = StringUtil.fetchString(request, "mmsi");
		if (!StringUtil.isEmpty(mmsi)) {
			sql.append("  and  MMSI = '" + mmsi + "'");
		}
		String shipnam = StringUtil.fetchString(request, "shipnam");
		if(shipnam!=null&&!shipnam.equals(""))
		shipnam=java.net.URLDecoder.decode(shipnam,"UTF-8");

		if (!StringUtil.isEmpty(shipnam)) {
			sql.append("  and ( E_SHIP_NAM   like  '%" + shipnam + "%'  " +
					"     or   C_SHIP_NAM   like  '%" + shipnam + "%' )"  );
		}
		request.setAttribute("whereSql", sql.toString());
	}
	public void getShipOil(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	
		StringBuffer sql = new StringBuffer(" 1=1");

		request.setAttribute("whereSql", sql.toString());
	}
	/*
	 * s采购申请查询 
	 */
	public void getSmPmApply(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		StringBuffer sql = new StringBuffer(" and 1=1");

		String mmsi = StringUtil.fetchString(request, "mmsi");
		
		
		String bgtime = StringUtil.fetchString(request, "bgtime");
		String edtime = StringUtil.fetchString(request, "edtime");

		
		if (!StringUtil.isEmpty(bgtime)) {
			sql.append("  and to_char(PUR_DTE,'yyyy-mm-dd') >= '" + bgtime + "'");
		}

		if (!StringUtil.isEmpty(edtime)) {
			sql.append("  and to_char(PUR_DTE,'yyyy-mm-dd') <= '" + edtime + "'");
		}
		if (!StringUtil.isEmpty(mmsi)) {
			sql.append("  and c_ship_data.MMSI_COD = '" + mmsi + "'");
		}
		String cpmnam = StringUtil.fetchString(request, "cpmname");
		if(cpmnam!=null&&!cpmnam.equals(""))
			cpmnam=java.net.URLDecoder.decode(cpmnam,"UTF-8");
		if (!StringUtil.isEmpty(cpmnam)) {
			sql.append("  and ( sm_pm_pur_apply.C_PM_NAM   like  '%" + cpmnam + "%'  " +
					"     or   sm_pm_pur_apply.E_PM_NAM   like  '%" + cpmnam + "%' )"  );
		}
		String shipnam = StringUtil.fetchString(request, "shipnam");
		if(shipnam!=null&&!shipnam.equals(""))
		shipnam=java.net.URLDecoder.decode(shipnam,"UTF-8");
		if (!StringUtil.isEmpty(shipnam)) {
			sql.append("  and ( sm_pm_pur_apply_h.E_SHIP_NAM   like  '%" + shipnam + "%'  " +
					"     or   sm_pm_pur_apply_h.C_SHIP_NAM   like  '%" + shipnam + "%' )"  );
		}
		request.setAttribute("whereSql", sql.toString());
	}
	/*
	 * 备件物料出库
	 */
	public void getSmPmOut(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		StringBuffer sql = new StringBuffer(" and 1=1");

		String mmsi = StringUtil.fetchString(request, "mmsi");
		String bgtime = StringUtil.fetchString(request, "bgtime");
		String edtime = StringUtil.fetchString(request, "edtime");
		
		if (!StringUtil.isEmpty(bgtime)) {
			sql.append("  and to_char(OUTDEPOT_DTE,'yyyy-mm-dd') >= '" + bgtime + "'");
		}

		if (!StringUtil.isEmpty(edtime)) {
			sql.append("  and to_char(OUTDEPOT_DTE,'yyyy-mm-dd') <= '" + edtime + "'");
		}
		if (!StringUtil.isEmpty(mmsi)) {
			sql.append("  and c_ship_data.MMSI_COD = '" + mmsi + "'");
		}
		String waretyp = StringUtil.fetchString(request, "waretyp");
		if (!StringUtil.isEmpty(waretyp)) {
			sql.append("  and WARE_TYP = '" + waretyp + "'");
		}
	
		String cpmnam = StringUtil.fetchString(request, "cpmname");
		if(cpmnam!=null&&!cpmnam.equals(""))
			cpmnam=java.net.URLDecoder.decode(cpmnam,"UTF-8");
		if (!StringUtil.isEmpty(cpmnam)) {
			sql.append("  and ( SM_PM_OUT_DT.C_PM_NAM   like  '%" + cpmnam + "%'  " +
					"     or   SM_PM_OUT_DT.E_PM_NAM   like  '%" + cpmnam + "%' )"  );
		}
		String shipnam = StringUtil.fetchString(request, "shipnam");
		if(shipnam!=null&&!shipnam.equals(""))
		shipnam=java.net.URLDecoder.decode(shipnam,"UTF-8");
		if (!StringUtil.isEmpty(shipnam)) {
			sql.append("  and ( SM_PM_OUT.E_SHIP_NAM   like  '%" + shipnam + "%'  " +
					"     or   SM_PM_OUT.C_SHIP_NAM   like  '%" + shipnam + "%' )"  );
		}
		request.setAttribute("whereSql", sql.toString());
	}
	
	/*
	 * 备件物料库存
	 */
	public void getSmPmlist(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		StringBuffer sql = new StringBuffer(" and 1=1");

		String mmsi = StringUtil.fetchString(request, "mmsi");
		

		if (!StringUtil.isEmpty(mmsi)) {
			sql.append("  and c_ship_data.MMSI_COD = '" + mmsi + "'");
		}
		String cpmname = StringUtil.fetchString(request, "cpmname");
		if(cpmname!=null&&!cpmname.equals(""))
			cpmname=java.net.URLDecoder.decode(cpmname,"UTF-8");
		if (!StringUtil.isEmpty(cpmname)) {
			sql.append("  and ( SM_PM_LIST.C_PM_NAM   like  '%" + cpmname + "%'  " +
					"     or   SM_PM_LIST.E_PM_NAM   like  '%" + cpmname + "%' )"  );
		}
		String storagenam = StringUtil.fetchString(request, "storagenam");
		if(storagenam!=null&&!storagenam.equals(""))
			storagenam=java.net.URLDecoder.decode(storagenam,"UTF-8");
		if (!StringUtil.isEmpty(storagenam)) {
			sql.append("  and   SM_PM_LIST.STORAGE_NAM   like  '%" + storagenam + "%'  "  );
		}
		String shipnam = StringUtil.fetchString(request, "shipnam");
		if(shipnam!=null&&!shipnam.equals(""))
		shipnam=java.net.URLDecoder.decode(shipnam,"UTF-8");
		if (!StringUtil.isEmpty(shipnam)) {
			sql.append("  and ( SM_PM_LIST.E_SHIP_NAM   like  '%" + shipnam + "%'  " +
					"     or   SM_PM_LIST.C_SHIP_NAM   like  '%" + shipnam + "%' )"  );
		}
		request.setAttribute("whereSql", sql.toString());
	}
	/*
	 * 加油记录
	 */
	public void getSmOil(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		StringBuffer sql = new StringBuffer(" and 1=1");

		String mmsi = StringUtil.fetchString(request, "mmsi");
		
		String bgtime = StringUtil.fetchString(request, "bgtime");
		String edtime = StringUtil.fetchString(request, "edtime");
		
		if (!StringUtil.isEmpty(bgtime)) {
			sql.append("  and to_char(OIL_TIM,'yyyy-mm-dd') >= '" + bgtime + "'");
		}

		if (!StringUtil.isEmpty(edtime)) {
			sql.append("  and to_char(OIL_TIM,'yyyy-mm-dd') <= '" + edtime + "'");
		}

		if (!StringUtil.isEmpty(mmsi)) {
			sql.append("  and c_ship_data.MMSI_COD = '" + mmsi + "'");
		}
		String oilcompany = StringUtil.fetchString(request, "oilcompany");
		if(oilcompany!=null&&!oilcompany.equals(""))
			oilcompany=java.net.URLDecoder.decode(oilcompany,"UTF-8");

		if (!StringUtil.isEmpty(oilcompany)) {
			sql.append("  and  SM_OIL.OIL_COMPANY  like  '%" + oilcompany + "%'  " 	);
		}
		String shipnam = StringUtil.fetchString(request, "shipnam");
		if(shipnam!=null&&!shipnam.equals(""))
			shipnam=java.net.URLDecoder.decode(shipnam,"UTF-8");

		if (!StringUtil.isEmpty(shipnam)) {
			sql.append("  and ( SM_OIL.E_SHIP_NAM   like  '%" + shipnam + "%'  " +
					"     or   SM_OIL.C_SHIP_NAM   like  '%" + shipnam + "%' )"  );
		}
		request.setAttribute("whereSql", sql.toString());
	}
	/*
	 * 保养记录
	 */
	public void getSmDevPlan(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		StringBuffer sql = new StringBuffer(" and 1=1");//WORK_STA='9'

		String mmsi = StringUtil.fetchString(request, "mmsi");
		String responman = StringUtil.fetchString(request, "responman");
		if(responman!=null&&!responman.equals(""))
			responman=java.net.URLDecoder.decode(responman,"UTF-8");
		if (!StringUtil.isEmpty(responman)) {
			sql.append("  and RESPON_MAN  like '%" + responman + "%'");
		}
		String cpmname = StringUtil.fetchString(request, "cpmname");
		if(cpmname!=null&&!cpmname.equals(""))
			cpmname=java.net.URLDecoder.decode(cpmname,"UTF-8");
		if (!StringUtil.isEmpty(cpmname)) {
			sql.append("  and ( C_DEV_NAM  like  '%" + cpmname + "%'  " +
					"     or   E_DEV_NAM  like  '%" + cpmname + "%' )"  );
		}
		String planmonth = StringUtil.fetchString(request, "planmonth");
		
		if (!StringUtil.isEmpty(planmonth)) {
			sql.append("  and PLAN_MONTH = '" + planmonth + "'");
		}
		if (!StringUtil.isEmpty(mmsi)) {
			sql.append("  and c_ship_data.MMSI_COD = '" + mmsi + "'");
		}
		String shipnam = StringUtil.fetchString(request, "shipnam");
		if(shipnam!=null&&!shipnam.equals(""))
			shipnam=java.net.URLDecoder.decode(shipnam,"UTF-8");

		if (!StringUtil.isEmpty(shipnam)) {
			sql.append("  and ( SM_DEV_PLAN_H.E_SHIP_NAM   like  '%" + shipnam + "%'  " +
					"     or   SM_DEV_PLAN_H.C_SHIP_NAM   like  '%" + shipnam + "%' )"  );
		}
		request.setAttribute("whereSql", sql.toString());
	}
}
