package com.xuelang.partmanage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webbuilder.utils.DbUtil;
import com.webbuilder.utils.StringUtil;

public class MultipleTree {

	public static String getJsonTree(List list) {
		List dataList = list;

		// 节点列表（散列表，用于临时存储节点对象）
		HashMap nodeList = new HashMap();
		// 根节点
		Node root = null;
		// 根据结果集构造节点列表（存入散列表）
		for (Iterator it = dataList.iterator(); it.hasNext();) {
			Map dataRecord = (Map) it.next();
			Node node = new Node();
			node.id = (String) dataRecord.get("id");
			node.text = (String) dataRecord.get("text");
			node.parentId = (String) dataRecord.get("parentId");
			if(dataRecord.get("extraStr")!=null){
				node.extraStr=(String) dataRecord.get("extraStr");	
			}
			//iconCls:\'icon_folder\
			node.iconCls = "icon_folder";
			nodeList.put(node.id, node);
		}
		// 构造无序的多叉树
		Set entrySet = nodeList.entrySet();
		for (Iterator it = entrySet.iterator(); it.hasNext();) {
			Node node = (Node) ((Map.Entry) it.next()).getValue();
			if (node.parentId == null || node.parentId.equals("-1")) {
				root = node;
			} else {
				((Node) nodeList.get(node.parentId)).addChild(node);
			}
		}
		// 输出无序的树形菜单的JSON字符串
		System.out.println(root.toString());
		// 对多叉树进行横向排序
		root.sortChildren();
		// 输出有序的树形菜单的JSON字符串
		System.out.println(root.toString());
		return root.toString();
	}

	

	/**
	 * 节点类
	 */
	static class Node {
		/**
		 * 节点编号
		 */
		public String id;
		/**
		 * 节点内容
		 */
		public String text;
		public String extraStr;
		/**
		 * 父节点编号
		 */
		public String parentId;
		public String iconCls;
		/**
		 * 孩子节点列表
		 */
		private Children children = new Children();

		// 先序遍历，拼接JSON字符串
		public String toString() {
			String result = "{" + "id : '" + id + "'" + ", text : '" + text+ "', extraStr : '" + extraStr
					+ "',expanded:true ";
			if (children != null && children.getSize() != 0) {
				result += ", children : " + children.toString();
			} else {
				result += ", leaf : true";
			}

			return result + "}";
		}

		// 兄弟节点横向排序
		public void sortChildren() {
			if (children != null && children.getSize() != 0) {
				children.sortChildren();
			}
		}

		// 添加孩子节点
		public void addChild(Node node) {
			this.children.addChild(node);
		}
	}

	/**
	 * 孩子列表类
	 */
	static class Children {
		private List list = new ArrayList();

		public int getSize() {
			return list.size();
		}

		public void addChild(Node node) {
			list.add(node);
		}

		// 拼接孩子节点的JSON字符串
		public String toString() {
			String result = "[";
			for (Iterator it = list.iterator(); it.hasNext();) {
				result += ((Node) it.next()).toString();
				result += ",";
			}
			result = result.substring(0, result.length() - 1);
			result += "]";
			return result;
		}

		// 孩子节点排序
		public void sortChildren() {
			// 对本层节点进行排序
			// 可根据不同的排序属性，传入不同的比较器，这里传入ID比较器
			Collections.sort(list, new NodeIDComparator());
			// 对每个节点的下一层节点进行排序
			for (Iterator it = list.iterator(); it.hasNext();) {
				((Node) it.next()).sortChildren();
			}
		}
	}

		
	// 获得组织机构树
	public void getOrg(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		request.setCharacterEncoding("UTF-8");
		Connection conn = null;
		Statement s = null;
		ResultSet rs = null;
		String myJson = "";
		String jndi = StringUtil.fetchString(request, "sys.jndi");
		try {
			conn = DbUtil.getConnection(jndi);
			s = conn.createStatement();
			List dataList = new ArrayList();
			rs = s
					.executeQuery(" select  ORG_COD id,C_ORG_NAM text,PARENT_COD  parentId from  s_organize");
			while (rs.next()) {
				HashMap map = new HashMap();
				map.put("id", rs.getString(1));
				map.put("text", rs.getString(2));
				map.put("parentId", rs.getString(3));
				dataList.add(map);
			}
			myJson = getJsonTree(dataList);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			if (s != null)
				s.close();
			if (conn != null)
				conn.close();
		}
		request.setAttribute("orgTree", "[" + myJson + "]");
	}
	
	
	
	// 获得设备树
		public void getDeviceTree(HttpServletRequest request, HttpServletResponse response)
				throws Exception {

			request.setCharacterEncoding("UTF-8");
			Connection conn = null;
			Statement s = null;
			ResultSet rs = null;
			String myJson = "";
			String jndi = StringUtil.fetchString(request, "sys.jndi");
			try {
				conn = DbUtil.getConnection(jndi);
				s = conn.createStatement();
				List dataList = new ArrayList();
				rs = s
						.executeQuery(" select type_id id,parts_type text,'' textdetail,parent_id parentId from c_parts_type "
								+" union SELECT CONCAT_WS('_',d.PARTS_ID,'DEVICE') id, d.C_PARTS_NAM text,CONCAT_WS('|',d.PARTS_NO,d.DEVICE_TYPE,d.E_PARTS_NAM,d.PARTS_NUM) textdetail,"
								+"p.type_id parentId FROM t_device d join c_parts_type p where d.DEVICE_TYPE=p.PARTS_TYPE ");
				while (rs.next()) {
					HashMap map = new HashMap();
					map.put("id", rs.getString(1));
					map.put("text", rs.getString(2));
					map.put("parentId", rs.getString(4));
					map.put("extraStr", rs.getString(3));
					dataList.add(map);
				}
				myJson = getJsonTree(dataList);
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				if (rs != null)
					rs.close();
				if (s != null)
					s.close();
				if (conn != null)
					conn.close();
			}
			request.setAttribute("deviceTree", "[" + myJson + "]");
		}
	public void getStockNum(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
	}
		// 获得物料类型树
				public void getPartsTypeTree(HttpServletRequest request, HttpServletResponse response)
						throws Exception {

					request.setCharacterEncoding("UTF-8");
					Connection conn = null;
					Statement s = null;
					ResultSet rs = null;
					String myJson = "";
					String jndi = StringUtil.fetchString(request, "sys.jndi");
					try {
						conn = DbUtil.getConnection(jndi);
						s = conn.createStatement();
						List dataList = new ArrayList();
						rs = s
								.executeQuery(" select type_id id,parts_type text,'' textdetail,parent_id parentId from c_parts_type  ");
						while (rs.next()) {
							HashMap map = new HashMap();
							map.put("id", rs.getString(1));
							map.put("text", rs.getString(2));
							map.put("parentId", rs.getString(4));
							map.put("extraStr", rs.getString(3));
							dataList.add(map);
						}
						myJson = getJsonTree(dataList);
					} catch (Exception ex) {
						ex.printStackTrace();
					} finally {
						if (rs != null)
							rs.close();
						if (s != null)
							s.close();
						if (conn != null)
							conn.close();
					}
					request.setAttribute("parttypeTree", "[" + myJson + "]");
				}
			
	// 获得组织机构树
		public void getUserTree(HttpServletRequest request, HttpServletResponse response)
				throws Exception {

			request.setCharacterEncoding("UTF-8");
			Connection conn = null;
			Statement s = null;
			ResultSet rs = null;
			String myJson = "";
			String jndi = StringUtil.fetchString(request, "sys.jndi");
			try {
				conn = DbUtil.getConnection(jndi);
				s = conn.createStatement();
				List dataList = new ArrayList();
				rs = s
						.executeQuery(" select   id, text,   parentId from  USERTREE");
				while (rs.next()) {
					HashMap map = new HashMap();
					map.put("id", rs.getString(1));
					map.put("text", rs.getString(2));
					map.put("parentId", rs.getString(3));
					dataList.add(map);
				}
				myJson = getJsonTree(dataList);
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				if (rs != null)
					rs.close();
				if (s != null)
					s.close();
				if (conn != null)
					conn.close();
			}
			request.setAttribute("userTree", "[" + myJson + "]");
		}
		
		// 获得组织机构树
				public void getUserRoleTree(HttpServletRequest request, HttpServletResponse response)
						throws Exception {

					request.setCharacterEncoding("UTF-8");
					Connection conn = null;
					Statement s = null;
					ResultSet rs = null;
					String myJson = "";
					String jndi = StringUtil.fetchString(request, "sys.jndi");
					try {
						conn = DbUtil.getConnection(jndi);
						s = conn.createStatement();
						List dataList = new ArrayList();
						rs = s
								.executeQuery(" select   id, text,   parentId from  USERROLE");
						while (rs.next()) {
							HashMap map = new HashMap();
							map.put("id", rs.getString(1));
							map.put("text", rs.getString(2));
							map.put("parentId", rs.getString(3));
							dataList.add(map);
						}
						myJson = getJsonTree(dataList);
					} catch (Exception ex) {
						ex.printStackTrace();
					} finally {
						if (rs != null)
							rs.close();
						if (s != null)
							s.close();
						if (conn != null)
							conn.close();
					}
					System.out.println(myJson+"121212");
					request.setAttribute("userRoleTree", "[" + myJson + "]");
				}
		
		// 获得设备树
				public void getEquip(HttpServletRequest request, HttpServletResponse response)
						throws Exception {

					request.setCharacterEncoding("UTF-8");
					Connection conn = null;
					Statement s = null;
					ResultSet rs = null;
					String myJson = "";
					String jndi = StringUtil.fetchString(request, "sys.jndi");
					try {
						conn = DbUtil.getConnection(jndi);
						s = conn.createStatement();
						List dataList = new ArrayList();
						rs = s.executeQuery(" select   id, text,   parentId from  USERTREE");
						while (rs.next()) {
							HashMap map = new HashMap();
							map.put("id", rs.getString(1));
							map.put("text", rs.getString(2));
							map.put("parentId", rs.getString(3));
							dataList.add(map);
						}
						myJson = getJsonTree(dataList);
					} catch (Exception ex) {
						ex.printStackTrace();
					} finally {
						if (rs != null)
							rs.close();
						if (s != null)
							s.close();
						if (conn != null)
							conn.close();
					}
					request.setAttribute("equipTree", "[" + myJson + "]");
				}

	
	// 获得物料类型树
	public void getPartTyp(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		request.setCharacterEncoding("UTF-8");
		Connection conn = null;
		Statement s = null;
		ResultSet rs = null;
		String myJson = "";
		String jndi = StringUtil.fetchString(request, "sys.jndi");
		try {
			conn = DbUtil.getConnection(jndi);
			s = conn.createStatement();
			List dataList = new ArrayList();
			rs = s
					.executeQuery(" select  TYPE_ID id,PARTS_TYPE text,PARENT_ID  PARENT_ID from  c_parts_type where DATA_TYP = '物料' or DATA_TYP = '全部'");
			while (rs.next()) {
				HashMap map = new HashMap();
				map.put("id", rs.getString(1));
				map.put("text", rs.getString(2));
				map.put("parentId", rs.getString(3));
				dataList.add(map);
			}
			myJson = getJsonTree(dataList);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			if (s != null)
				s.close();
			if (conn != null)
				conn.close();
		}
		request.setAttribute("orgTree", "[" + myJson + "]");
	}

	// 获得物料类型树
		public void getPartTypWl(HttpServletRequest request, HttpServletResponse response)
				throws Exception {

			request.setCharacterEncoding("UTF-8");
			Connection conn = null;
			Statement s = null;
			ResultSet rs = null;
			String myJson = "";
			String jndi = StringUtil.fetchString(request, "sys.jndi");
			try {
				conn = DbUtil.getConnection(jndi);
				s = conn.createStatement();
				List dataList = new ArrayList();
				rs = s
						.executeQuery(" select  TYPE_ID id,PARTS_TYPE text,PARENT_ID  PARENT_ID from  c_parts_type where DATA_TYP = '物料' or DATA_TYP = '全部'");
				while (rs.next()) {
					HashMap map = new HashMap();
					map.put("id", rs.getString(1));
					map.put("text", rs.getString(2));
					map.put("parentId", rs.getString(3));
					dataList.add(map);
				}
				myJson = getJsonTree(dataList);
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				if (rs != null)
					rs.close();
				if (s != null)
					s.close();
				if (conn != null)
					conn.close();
			}
			request.setAttribute("partsTree", "[" + myJson + "]");
		}
	/**
	 * 节点比较器
	 */
	static class NodeIDComparator implements Comparator {
		// 按照节点编号比较
		public int compare(Object o1, Object o2) {
			String j1 = ((Node) o1).id;
			String j2 = ((Node) o2).id;
			return (j1.compareTo(j2) < 0 ? -1 : (j1 == j2 ? 0 : 1));
		}
	}

	
}
