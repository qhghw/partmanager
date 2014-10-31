package com.xuelang.partmanage;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.context.WebApplicationContext;

import com.partmanager.biz.power.dto.UserDTO;
import com.partmanager.utils.system.Constants;

public class IkexpressPlat {

	public void getOrgId(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		UserDTO userInfo = (UserDTO) request.getSession().getAttribute(Constants.USERINFO);
		String  deptCod =  userInfo.getDept_cod();
		
	//	request.setAttribute("IKsql", " AND Dept_cod = '"+deptCod+"'");
		request.setAttribute("IKsql", "");
	
	}

	
	/**
	 * Hello World Example
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String data = "a";
		String datas[] = data.split(",");
		System.out.println(datas.length + "===" + datas[0]);
	}
}
