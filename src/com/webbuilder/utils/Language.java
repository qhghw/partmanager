package com.webbuilder.utils;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;

public class Language {
	public  static  HashMap<String,String>  znMap=new     HashMap<String,String>  ();
	public  static  HashMap<String,String>   enMap=new     HashMap<String,String>  ();
	public synchronized static HashMap<String,String>  getZnClient(){
		ResourceBundle bundle=PropertyResourceBundle.getBundle("zn");
	
			try {
				Enumeration<String> key=bundle.getKeys()  ;
				String s="";String value="";
				Set<String>  set=bundle.keySet();
				for(Iterator   it = set.iterator();  it.hasNext(); )
				{
					  // System.out.println("s="+s);            
					   value= bundle.getString(s);
					   byte[] bs = value.getBytes();
					   value= new String(value.getBytes("ISO-8859-1"), "utf-8");   
					   znMap.put(s, value);
				}
				
				//znMap=new znMap(bundle.getString("softwareSerialNo"),bundle.getString("key"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		return znMap;
	}
	
	public synchronized static HashMap<String,String>  getEnClient(){
		ResourceBundle bundle=PropertyResourceBundle.getBundle("en");
		
			try {
				Enumeration<String> key=bundle.getKeys()  ;
				String s="";String value="";
				Set<String>  set=bundle.keySet();
				for(Iterator   it = set.iterator();  it.hasNext(); )
				{
						s=it.next().toString();
					   value= bundle.getString(s);
					   s= new String(s.getBytes("ISO-8859-1"), "utf-8"); 
					   enMap.put(s, value);
				}
				
				//znMap=new znMap(bundle.getString("softwareSerialNo"),bundle.getString("key"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		return enMap;
	}
	

}

/*
 * Location: Z:\EXT\webbuilder2.jar Qualified Name: com.webbuilder.utils.DbUtil
 * JD-Core Version: 0.6.0
 */