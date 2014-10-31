package com.webbuilder.utils;

public class AdvSqlUtil {

	
	public static String getSql(String keyName){
		String sql="";
		//返回客户sql
		if(keyName=="CLIENT_NAM"||keyName.equals("CLIENT_NAM")){
			sql ="select c_client_nam as KEY_TEXT,c_client_nam as KEY_ID from c_client";
		}
		//返回收货人
		if(keyName=="IN_CLIENT"||keyName.equals("IN_CLIENT")){
			sql ="select c_client_nam as KEY_TEXT,c_client_nam as KEY_ID from c_client";
		}
		//返回发货人
		if(keyName=="OUT_CLIENT"||keyName.equals("OUT_CLIENT")){
			sql ="select c_client_nam as KEY_TEXT,c_client_nam as KEY_ID from c_client";
		}
		//返回货物sql
		if(keyName=="CARGO_NAM"||keyName.equals("CARGO_NAM")){
			sql ="SELECT kind_nam as KEY_TEXT,kind_nam AS KEY_ID FROM C_cargo_kind";
		}
		return sql;
	} 
}
