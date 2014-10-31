package com.webbuilder.utils;

import com.webbuilder.controls.DbUpdater;
import com.webbuilder.controls.Query;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.Sheet;
import jxl.Workbook;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class DbUtil {
	public  static  HashMap<String,HashMap<String,String>>  map=new     HashMap<String,HashMap<String,String>> ();
	public static Connection fetchConnection(HttpServletRequest request,
			String jndi) throws Exception {
		Object obj = request.getAttribute("jndi." + jndi);
		Connection conn;
		if ((obj == null) || (!(obj instanceof Connection))) {
			conn = getConnection(jndi);
			request.setAttribute("jndi." + jndi, conn);
		} else {
			conn = (Connection) obj;
		}
		return conn;
	}

	public static void resultSetToFile(ResultSet resultSet, File file,
			String charset, String separator, String longDate, String shortDate)
			throws Exception {
		OutputStream st = new FileOutputStream(file);
		try {
			resultSetToStream(resultSet, st, charset, separator, longDate,
					shortDate);
		} finally {
			st.close();
		}
	}

	public static void resultSetToStream(ResultSet resultSet,
			OutputStream stream, String charset, String separator,
			String longDate, String shortDate) throws Exception {
		int k = 0;

		StringBuilder sb = new StringBuilder();
		ResultSetMetaData meta = resultSet.getMetaData();

		boolean setFormat = (!StringUtil.isEmpty(longDate))
				&& (!StringUtil.isEmpty(shortDate));
		BufferedWriter bw;

		if (StringUtil.isEmpty(charset))
			bw = new BufferedWriter(new OutputStreamWriter(stream));
		else
			bw = new BufferedWriter(new OutputStreamWriter(stream, charset));
		int j;
		int i;
		try {
			j = meta.getColumnCount();
			int[] types = new int[j];
			for (i = 0; i < j; i++) {
				types[i] = meta.getColumnType(i + 1);
				if (i > 0)
					sb.append(separator);
				sb.append(meta.getColumnLabel(i + 1));
			}
			sb.append("\r\n");
			bw.write(sb.toString());
			sb.delete(0, sb.length());
			while (resultSet.next()) {
				k++;
				for (i = 0; i < j; i++) {
					if (i > 0)
						sb.append(separator);
					String s;

					if ((types[i] == -1) || (types[i] == -2)
							|| (types[i] == -3) || (types[i] == -4)
							|| (types[i] == 2004)) {
						s = "";
					} else {

						if ((types[i] == 91) || (types[i] == 93)) {

							if (setFormat)
								s = DateUtil.formatDateString(resultSet
										.getTimestamp(i + 1), longDate,
										shortDate);
							else
								s = DateUtil.dateToString(resultSet
										.getTimestamp(i + 1));
						} else {
							s = resultSet.getString(i + 1);
						}
					}
					if (s == null)
						s = "";
					sb.append(StringUtil.toLineString(s, false));
				}
				sb.append("\r\n");
				if (k == 1000) {
					k = 0;
					bw.write(sb.toString());
					sb.delete(0, sb.length());
				}
			}
			if (k > 0)
				bw.write(sb.toString());
		} finally {
			bw.close();
		}
	}

	public static void getDefaultWhere(HttpServletRequest request,
			HttpServletResponse response, String filterFields, boolean replace)
			throws Exception {
		String sql;
		if (StringUtil.fetchString(request, "cb__9999") != null) {
			sql = getFilterSql(request, (ResultSet) request
					.getAttribute("emptyTable"), filterFields, replace);
			if (!StringUtil.isEmpty(sql))
				sql = " and (" + sql + ")";
		} else {
			/* 150 */sql = "";
			/* 151 */}
		request.setAttribute("whereSql", sql);
	}
	
	public static void getDefaultWhere(HttpServletRequest request,
			HttpServletResponse response, String filterFields, boolean replace,String SqlLink)
			throws Exception {
		String sql;
		if (StringUtil.fetchString(request, "cb__9999") != null) {
			sql = getFilterSql(request, (ResultSet) request
					.getAttribute("emptyTable"), filterFields, replace);
			if (!StringUtil.isEmpty(sql))
				sql = " and (" + sql + ")";
		} else {
			/* 150 */sql = "";
			/* 151 */}
		
		sql+=SqlLink;
		request.setAttribute("whereSql", sql);
	}

	public static void getDefaultWherename(HttpServletRequest request,
			HttpServletResponse response, String filterFields, boolean replace,String SqlLink,String name)
			throws Exception {
		String sql;
		if (StringUtil.fetchString(request, "cb__9999") != null) {
			sql = getFilterSql(request, (ResultSet) request
					.getAttribute("emptyTable"), filterFields, replace);
			if (!StringUtil.isEmpty(sql))
				sql = " and (" + sql + ")";
		} else {
			/* 150 */sql = "";
			/* 151 */}
		
		sql+=SqlLink;
		request.setAttribute(name, sql);
	}
	public static String addTablePrefix(String defines, String field,
			boolean replace) {
		/* 159 */if (StringUtil.isEmpty(defines))
			/* 160 */return field;
		/* 161 */String[] bf = defines.split(";");
		/* 162 */for (String bs : bf) {
			/* 163 */String name = StringUtil.getNamePart(bs);
			/* 164 */String value = StringUtil.getValuePart(bs);
			/* 165 */String[] sf = name.split(",");
			/* 166 */if (StringUtil.stringInList(sf, field) != -1) {
				/* 167 */if (replace) {
					/* 168 */return value;
				}
				/* 170 */return value + "." + field;
			}
		}
		/* 172 */return field;
	}

	public static String getFilterSql(HttpServletRequest request, ResultSet rs,
			String filterFields, boolean replace) throws Exception {
		/* 178 */boolean added = false;
		/* 179 */String jb = "";
		/* 180 */StringBuilder sql = new StringBuilder();
		/* 181 */ResultSetMetaData meta = rs.getMetaData();
		/* 182 */HashMap metaMap = new HashMap();

		/* 184 */int j = meta.getColumnCount() + 1;
		int i;
		/* 185 */for (i = 1; i < j; i++)
			/* 186 */metaMap.put(meta.getColumnLabel(i), Integer.valueOf(meta
					.getColumnType(i)));
		/* 187 */i = 9999;
		String f;
		/* 188 */while ((f = StringUtil.fetchString(request, "cb__" +
		/* 189 */Integer.toString(i))) != null) {

			/* 190 */String eq = StringUtil.fetchString(request, "eq__"
					+ Integer.toString(i));
			/* 191 */String vq = "vq__" + Integer.toString(i);
			/* 192 */if ((!StringUtil.isEmpty(eq)) && (metaMap.containsKey(f))) {
				/* 193 */if (added) {
					/* 194 */if (StringUtil.isEmpty(jb))
						/* 195 */jb = "and";
					/* 196 */sql.append(" " + jb + " ");
				}
				/* 198 */sql.append(addTablePrefix(filterFields, f, replace)
						+ " " + eq +
						/* 199 */" ");
				/* 200 */added = true;
				/* 201 */jb = StringUtil.fetchString(request, "jb__" +
				/* 202 */Integer.toString(i));
				int t = 0;
				/* 203 */if ((eq.indexOf("=") != -1) || (eq.indexOf(">") != -1)
						||
						/* 204 */(eq.indexOf("<") != -1)
						|| (eq.indexOf("like") != -1))
					/* 205 */t = ((Integer) metaMap.get(f)).intValue();
				/* 206 */switch (t) {
				case -6:
				case -5:
				case 4:
				case 5:
					/* 211 */
					sql.append("{?integer." + vq + "?}");
					/* 212 */break;
				case 2:
				case 3:
				case 7:
				case 8:
					/* 217 */
					sql.append("{?double." + vq + "?}");
					/* 218 */break;
				case 6:
					/* 220 */
					sql.append("{?float." + vq + "?}");
					/* 221 */break;
				case 91:
				case 93:
					/* 224 */
					sql.append("{?timestamp." + vq + "?}");
					/* 225 */break;
				case 0:
					sql.append("{#" + vq + "#}");
					break;
				default:
					/* 227 */
					sql.append("{?" + vq + "?}");
					break;

				/* 230 */
				}
			}
			/* 232 */i--;
		}
		/* 234 */return sql.toString();
	}

	public static void executeSqlFile(HttpServletRequest request, String jndi,
			String filename, boolean transProtect, boolean closeConnection,
			String charset) throws Exception {
		/* 240 */String sqls = FileUtil.readText(filename, charset);
		/* 241 */DbUpdater db = new DbUpdater();
		try {
			/* 244 */db.setRequest(request);
			/* 245 */if (!StringUtil.isEmpty(jndi))
				/* 246 */db.jndi = jndi;
			/* 247 */db.sqls = sqls;
			/* 248 */if (transProtect)
				/* 249 */db.transaction = "start";
			/* 250 */db.create();
		} finally {
			/* 252 */if (closeConnection)
				/* 253 */db.closeConnection();
		}
	}
	public static void recordLog(String jndi, String userName, String ip, String msg, String charset, int type) throws Exception {
	    Connection connection = null;
	    PreparedStatement statement = null;
	    try {
	      if (ip == null)
	        ip = "";
	      if (msg == null)
	        msg = "";
	      if (userName == null)
	        userName = "未知";
	      connection = getConnection(jndi);
	      statement = connection
	        .prepareStatement("insert into WB_LOG values(?,?,?,?,?)");
	      statement.setObject(1, new Timestamp(new Date().getTime()));
	      statement.setObject(2, userName);
	      statement.setObject(3, ip);
	
	      byte[] bt1=null;
	      if (StringUtil.isEmpty(charset))
	        bt1 = msg.getBytes();
	      else
	        bt1 = msg.getBytes(charset);
	      if (bt1.length > 252) {
	        if (StringUtil.isEmpty(charset))
	          statement.setObject(4, new String(bt1, 0, 252) + "...");
	        else
	          statement.setObject(4, new String(bt1, 0, 252, charset) + 
	            "...");
	      }
	      else statement.setObject(4, msg);
	      statement.setObject(5, Integer.valueOf(type));
	      statement.executeUpdate();
	    } finally {
	      if (statement != null)
	        statement.close();
	      if (connection != null)
	        connection.close();
	    }
	  }

	public static int getFieldCount(ResultSet resultSet) throws SQLException {
		/* 258 */ResultSetMetaData meta = resultSet.getMetaData();
		/* 259 */return meta.getColumnCount();
	}

	public static Connection getConnection(String jndi) throws NamingException,
			SQLException {
		/* 264 */InitialContext ctx = new InitialContext();
		/* 265 */DataSource ds = (DataSource) ctx.lookup(jndi);
		/* 266 */return ds.getConnection();
	}

	public static void setTransactionIsolation(Connection connection,
			String transaction, String isolation) throws SQLException {
		/* 271 */if (((transaction.equals("start")) || (transaction
				.equals("commit")))
				&&
				/* 272 */(connection.getAutoCommit())) {
			/* 273 */connection.setAutoCommit(false);
			/* 274 */if (!StringUtil.isEmpty(isolation))
				/* 275 */if (isolation.equals("readUncommitted"))
					/* 276 */connection
					/* 277 */.setTransactionIsolation(1);
				/* 278 */else if (isolation.equals("readCommitted"))
					/* 279 */connection
					/* 280 */.setTransactionIsolation(2);
				/* 281 */else if (isolation.equals("repeatableRead"))
					/* 282 */connection
					/* 283 */.setTransactionIsolation(4);
				/* 284 */else if (isolation.equals("serializable"))
					/* 285 */connection
					/* 286 */.setTransactionIsolation(8);
		}
	}

	public static boolean closeResultSet(ResultSet resultSet) {
		/* 294 */boolean status = true;
		try {
			/* 297 */if (resultSet == null)
				/* 298 */return true;
			/* 299 */Statement statement = resultSet.getStatement();
			try {
				/* 301 */resultSet.close();
				/* 302 */resultSet = null;
			} catch (Exception e) {
				/* 304 */status = false;
			}
			/* 306 */if (statement != null) {
				/* 307 */statement.close();
				/* 308 */statement = null;
			}
		} catch (Exception e) {
			/* 311 */status = false;
		}
		/* 313 */return status;
	}

	public static boolean closeStatement(Statement statement) {
		/* 317 */boolean status = true;
		try {
			/* 319 */if (statement != null) {
				/* 320 */statement.close();
				/* 321 */statement = null;
			}
		} catch (Exception e) {
			/* 324 */status = false;
		}
		/* 326 */return status;
	}

	public static boolean closeConnection(Connection connection,
			boolean isExcept) {
		/* 331 */boolean status = true;
		try {
			/* 333 */if ((connection != null) && (!connection.isClosed())) {
				/* 334 */if (!connection.getAutoCommit())
					try {
						/* 336 */if (isExcept)
							/* 337 */connection.rollback();
						else
							/* 339 */connection.commit();
					} catch (Exception e) {
						/* 341 */status = false;
						/* 342 */if (!isExcept)
							/* 343 */connection.rollback();
					} finally {
						/* 345 */connection.setAutoCommit(true);
					}
				/* 347 */connection.close();
				/* 348 */connection = null;
			}
		} catch (Exception e) {
			/* 351 */status = false;
		}
		/* 353 */return status;
	}

	public static String getDataTypeCategory(int type, boolean dateAsString) {
		/* 357 */switch (type) {
		case -6:
		case -5:
		case 4:
		case 5:
			/* 362 */
			return "int";
		case 2:
		case 3:
		case 6:
		case 7:
		case 8:
			/* 368 */
			return "float";
		case 91:
		case 93:
			/* 371 */
			if (dateAsString) {
				/* 372 */return "string";
			}
			/* 374 */
			return "date";
		}
		/* 376 */return "string";
	}

	public static String getFieldsJson(ResultSet resultSet, boolean dateAsString)
			throws SQLException {
		/* 382 */return getFieldsJson(resultSet.getMetaData(), dateAsString);
	}

	public static String getFieldsJson(ResultSetMetaData meta,
			boolean dateAsString) throws SQLException {
		/* 389 */StringBuilder express = new StringBuilder();

		/* 391 */int j = meta.getColumnCount();
		/* 392 */for (int i = 0; i < j; i++) {
			/* 393 */if (i > 0)
				/* 394 */express.append(",");
			/* 395 */express.append("{name:\"");
			/* 396 */express.append(StringUtil.replace(StringUtil
					.toExpress(meta
					/* 397 */.getColumnLabel(i + 1)), ".", "．"));
			/* 398 */express.append("\",type:\"");
			/* 399 */String category = getDataTypeCategory(meta
					.getColumnType(i + 1),
			/* 400 */dateAsString);
			/* 401 */express.append(category);
			/* 402 */if ((!dateAsString) && (category.equals("date")))
				/* 403 */express.append("\",dateFormat:\"Y-m-d H:i:s");
			/* 404 */express.append("\"}");
		}
		/* 406 */return express.toString();
	}

	public static String getResultSetMeta(ResultSet resultSet,
			boolean dateAsString, boolean ignoreBlob) throws SQLException {
		/* 411 */return getResultSetMeta(resultSet.getMetaData(), dateAsString,
		/* 412 */ignoreBlob);
	}

	public static void outputBlob(ResultSet resultSet,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		/* 418 */InputStream inputStream = null;

		/* 420 */int rowCount = resultSet.getMetaData().getColumnCount();
		/* 421 */String name = "file";
		String size = null;

		/* 423 */response.reset();
		/* 424 */if (resultSet.next()) {
			/* 425 */switch (rowCount) {
			case 1:
				/* 427 */
				inputStream = resultSet.getBinaryStream(1);
				/* 428 */break;
			case 2:
				/* 430 */
				name = resultSet.getString(2);
				/* 431 */
				inputStream = resultSet.getBinaryStream(1);
				/* 432 */break;
			case 3:
				/* 434 */
				name = resultSet.getString(2);
				/* 435 */
				size = resultSet.getString(3);
				/* 436 */
				inputStream = resultSet.getBinaryStream(1);
			}

			/* 439 */if (inputStream != null) {
				/* 440 */OutputStream outputStream = response.getOutputStream();
				/* 441 */response
				/* 442 */.setHeader("content-type",
						"application/force-download");
				/* 443 */String charset = (String) request
				/* 444 */.getAttribute("sys.fileCharset");
				/* 445 */response.setHeader("content-disposition",
				/* 446 */"attachment;filename=" +
				/* 447 */WebUtil.getFileName(name, charset));
				/* 448 */if (size != null)
					/* 449 */response.setHeader("content-length", size);
				/* 450 */SysUtil.inputStreamToOutputStream(inputStream,
						outputStream);
				/* 451 */inputStream.close();
			}
		}
	}

	public static void outputImage(ResultSet resultSet,
			HttpServletRequest request, HttpServletResponse response,
			String format) throws Exception {
		/* 459 */InputStream inputStream = null;

		/* 461 */int size = 0;
		/* 462 */boolean except = false;

		/* 464 */response.reset();
		/* 465 */OutputStream outputStream = response.getOutputStream();
		try {
			/* 467 */if (resultSet.next()) {
				/* 468 */response.setContentType("image/" + format);
				/* 469 */inputStream = resultSet.getBinaryStream(1);
				/* 470 */if (inputStream != null) {
					/* 471 */size = SysUtil.inputStreamToOutputStream(
							inputStream,
							/* 472 */outputStream);
					/* 473 */inputStream.close();
				}
			}
		} catch (Exception e) {
			/* 477 */except = true;
		} finally {
			/* 479 */if ((except) || (size == 0)) {
				/* 480 */response.setContentType("image/gif");
				/* 481 */inputStream = new FileInputStream(request
				/* 482 */.getAttribute("sys.path") +
				/* 483 */"webbuilder/images/null.gif");
				/* 484 */SysUtil.inputStreamToOutputStream(inputStream,
						outputStream);
				/* 485 */inputStream.close();
			}
		}
	}

	public static String getResultSetMeta(ResultSetMetaData meta,
			boolean dateAsString, boolean ignoreBlob) throws SQLException {
		/* 494 */StringBuilder express = new StringBuilder();

		/* 496 */int j = meta.getColumnCount();
		/* 497 */for (int i = 0; i < j; i++) {
			/* 498 */if (i > 0)
				/* 499 */express.append(",");
			/* 500 */String cap = meta.getColumnLabel(i + 1);
			/* 501 */String enCap = StringUtil.toExpress(cap);
			/* 502 */express.append("{dataIndex:\"");
			/* 503 */express.append(StringUtil.replace(enCap, ".", "．"));
			/* 504 */express.append("\",header:\"");
			/* 505 */express.append(enCap);
			/* 506 */express.append("\",width:");
			/* 507 */int type = meta.getColumnType(i + 1);
			int len;

			/* 508 */if ((ignoreBlob) && (isBlobField(type)))
				/* 509 */len = 6;
			else
				/* 511 */len = meta.getColumnDisplaySize(i + 1);
			/* 512 */if (len > 30)
				/* 513 */len = 30;
			/* 514 */int k = cap.length() + 1;
			/* 515 */if (len < k)
				/* 516 */len = k;
			/* 517 */if (len < 4)
				/* 518 */len = 4;
			/* 519 */if ((type == 93) || (type == 91)) {
				/* 520 */express.append(150);
				/* 521 */if (!dateAsString)
					/* 522 */express
							/* 523 */.append(",renderer:function(v){if(v){if(v.format('His')=='000000')return v.format('Y-n-j');else return v.format('Y-n-j H:i:s');}else return '';}");
			} else {
				/* 525 */express.append(len * 9);
				/* 526 */}
			if (!StringUtil.isEqual(getDataTypeCategory(type, true), "string"))
				/* 527 */express.append(",align:\"right\"");
			/* 528 */express.append(",sortable:true");
			/* 529 */express.append("}");
		}
		/* 531 */return express.toString();
	}

	public static boolean setResultSetToFirst(ResultSet resultSet)
			throws SQLException {
		/* 536 */if (resultSet == null)
			/* 537 */return false;
		/* 538 */if ((resultSet.getStatement() != null) &&
		/* 539 */(resultSet.getStatement().getResultSetType() != 1003)) {
			/* 540 */return resultSet.first();
		}
		/* 542 */return resultSet.next();
	}

	public static String getFirstFieldValue(ResultSet resultSet)
			throws SQLException {
		/* 547 */if (!setResultSetToFirst(resultSet)) {
			/* 548 */return null;
		}
		/* 550 */return resultSet.getString(1);
	}

	private static boolean isBlobField(int type) {
		/* 554 */switch (type) {
		case -4:
		case -3:
		case -2:
		case -1:
		case 2004:
		case 2005:
		case 2011:
			/* 562 */
			return true;
		}
		/* 564 */return false;
	}

	public static String getFieldValue(ResultSet resultSet,
			ResultSetMetaData meta, int index, boolean ignoreBlob)
			throws SQLException {
		/* 570 */int type = meta.getColumnType(index);

		/* 572 */if ((ignoreBlob) && (isBlobField(type)))
			/* 573 */return "(BLOB)";
		/* 574 */if ((type == 93) || (type == 91)) {
			/* 575 */return DateUtil
					.dateToString(resultSet.getTimestamp(index));
		}
		/* 577 */return resultSet.getString(index);
	}

	public static String getDataXml(ResultSet resultSet, ResultSet total,
			int start, int limit, boolean ignoreBlob, int pagedRecords)
			throws SQLException, IOException {
		/* 583 */int count = 0;
		/* 584 */boolean first = true;

		/* 586 */Document document = DocumentHelper.createDocument();
		/* 587 */document.setXMLEncoding("utf-8");
		/* 588 */Element totalEl = null;

		/* 591 */Element root = document.addElement("root");
		/* 592 */if (!setResultSetToFirst(resultSet)) {
			/* 593 */root.addElement("total").addText("0");
			/* 594 */return document.asXML();
		}
		/* 596 */ResultSetMetaData meta = resultSet.getMetaData();
		/* 597 */int j = meta.getColumnCount();
		/* 598 */if (setResultSetToFirst(total))
			/* 599 */root.addElement("total").addText(total.getString(1));
		else
			/* 601 */totalEl = root.addElement("total");
		/* 602 */int end = start + limit - 1;
		/* 603 */while ((first) || (resultSet.next())) {
			/* 604 */if (first)
				/* 605 */first = false;
			/* 606 */count++;
			/* 607 */if (count < start)
				continue;
			/* 609 */if (count > end) {
				/* 610 */if (totalEl == null)
					break;
				/* 612 */if (count >= pagedRecords)
					/* 613 */break;
			} else {
				/* 616 */Element element = root.addElement("row");
				/* 617 */for (int i = 0; i < j; i++) {
					/* 618 */String text = getFieldValue(resultSet, meta,
							i + 1, ignoreBlob);
					/* 619 */if (text == null)
						/* 620 */text = "";
					/* 621 */element.addElement(meta.getColumnLabel(i + 1))
							.addText(text);
				}
			}
		}
		/* 624 */if (totalEl != null)
			/* 625 */totalEl.addText(Integer.toString(count));
		/* 626 */return document.asXML();
	}

	public static String getDataJson(ResultSet resultSet, ResultSet total,
			int start, int limit, String metaData, String colDefine,
			boolean dateAsString, boolean ignoreBlob, int pagedRecords)
			throws SQLException {
		/* 633 */int count = 0;
		/* 634 */boolean first = true;
		boolean hasTotal = false;
		/* 635 */StringBuilder express = new StringBuilder();

		/* 638 */if (resultSet == null)
			/* 639 */return "{total:0,row:[]}";
		/* 640 */ResultSetMetaData meta = resultSet.getMetaData();
		/* 641 */int j = meta.getColumnCount();
		/* 642 */if (setResultSetToFirst(total)) {
			/* 643 */express.append("{total:");
			/* 644 */express.append(total.getString(1));
		} else {
			/* 646 */hasTotal = true;
			/* 647 */}
		if (!StringUtil.isEmpty(metaData)) {
			/* 648 */express
					/* 649 */.append(",metaData:{totalProperty:\"total\",root:\"row\",fields:[");
			/* 650 */if (metaData.equals("-"))
				/* 651 */express.append(getFieldsJson(meta, dateAsString));
			else
				/* 653 */express.append(metaData);
			/* 654 */express.append("]}");
		}
		/* 656 */if (!StringUtil.isEmpty(colDefine)) {
			/* 657 */express.append(",colDefine:[");
			/* 658 */if (colDefine.equals("-"))
				/* 659 */express
				/* 660 */.append(getResultSetMeta(meta, dateAsString,
						ignoreBlob));
			else
				/* 662 */express.append(colDefine);
			/* 663 */express.append("]");
		}
		/* 665 */express.append(",row:[");
		/* 666 */int end = start + limit - 1;
		/* 667 */if (setResultSetToFirst(resultSet))
			/* 668 */while ((first) || (resultSet.next())) {
				/* 669 */count++;
				/* 670 */if (count < start) {
					/* 671 */if (!resultSet.next()) {
						break;
					}
				}
				/* 675 */else if (count > end) {
					/* 676 */if (!hasTotal)
						break;
					/* 677 */if (first)
						/* 678 */first = false;
					/* 679 */if (count >= pagedRecords) {
						/* 680 */break;
					}
				} else {
					/* 685 */if (first)
						/* 686 */first = false;
					else
						/* 688 */express.append(",");
					/* 689 */express.append("{");
					/* 690 */for (int i = 0; i < j; i++) {
						/* 691 */if (i > 0)
							/* 692 */express.append(",");
						/* 693 */express.append("\"");
						/* 694 */express.append(StringUtil.replace(StringUtil
								.toExpress(meta
								/* 695 */.getColumnLabel(i + 1)), ".", "．"));
						/* 696 */express.append("\":\"");
						/* 697 */express.append(StringUtil
								.toExpress(getFieldValue(
								/* 698 */resultSet, meta, i + 1, ignoreBlob)));
						/* 699 */express.append("\"");
					}
					/* 701 */express.append("}");
				}
			}
		/* 703 */express.append("]}");
		/* 704 */if (hasTotal)
			/* 705 */express.insert(0, "{total:" + Integer.toString(count));
		/* 706 */return express.toString();
	}

	public static String getDataArray(ResultSet resultSet, boolean ignoreBlob)
			throws SQLException {
		/* 711 */ResultSetMetaData meta = resultSet.getMetaData();
		/* 712 */return getDataArray(resultSet, meta, ignoreBlob);
	}

	public static String getDataArray(ResultSet resultSet,
			ResultSetMetaData meta, boolean ignoreBlob) throws SQLException {
		/* 718 */boolean first = true;
		/* 719 */StringBuilder express = new StringBuilder();

		/* 721 */if (!setResultSetToFirst(resultSet))
			/* 722 */return "[]";
		/* 723 */int j = meta.getColumnCount();
		/* 724 */express.append("[");
		/* 725 */while ((first) || (resultSet.next())) {
			/* 726 */if (first)
				/* 727 */first = false;
			else
				/* 729 */express.append(",");
			/* 730 */express.append("[");
			/* 731 */for (int i = 0; i < j; i++) {
				/* 732 */if (i > 0)
					/* 733 */express.append(",");
				/* 734 */express.append("\"");
				/* 735 */express.append(StringUtil.toExpress(getFieldValue(
						resultSet,
						/* 736 */meta, i + 1, ignoreBlob)));
				/* 737 */express.append("\"");
			}
			/* 739 */express.append("]");
		}
		/* 741 */express.append("]");
		/* 742 */return express.toString();
	}

	public static void resultSetToHashMap(ResultSet resultSet,
			HashMap<String, String> hashMap) throws SQLException {
		/* 747 */boolean first = true;

		/* 749 */if (!setResultSetToFirst(resultSet))
			/* 750 */return;
		do {
			/* 752 */if (first)
				/* 753 */first = false;
			/* 754 */hashMap
					.put(resultSet.getString(1), resultSet.getString(2));
		}
		/* 751 */while ((first) || (resultSet.next()));
	}

	public static void resultSetToArrayList(ResultSet resultSet,
			ArrayList<String> arrayList) throws SQLException {
		/* 760 */boolean first = true;

		/* 762 */if (!setResultSetToFirst(resultSet))
			/* 763 */return;
		do {
			/* 765 */if (first)
				/* 766 */first = false;
			/* 767 */arrayList.add(resultSet.getString(1));
		}
		/* 764 */while ((first) || (resultSet.next()));
	}

	/**
	 * 高级查询条件@author shaofq
	 * @param request
	 * @param keyName
	 * @return
	 * @throws Exception
	 */
	public static boolean createKeyQuery_adv(HttpServletRequest request,
			String keyName) throws Exception {

		Query query = new Query();
		query.setRequest(request);
		query.sql=AdvSqlUtil.getSql(keyName);
		query.jndi = ((String) request.getAttribute("sys.sysJndi"));
		query.setName("keytb." + keyName);
		query.create();
		ResultSet rs = (ResultSet) request.getAttribute("keytb."
				+ keyName);
		ResultSetMetaData meta = rs.getMetaData();
		request.setAttribute("keya." + keyName, "var " + keyName
				+ "__kdt=" +
				getDataArray(rs, meta, false) + ";");
		return true;
	}

	public static boolean createKeyQuery(HttpServletRequest request,
			String keyName) throws Exception {
		if (keyName != null && keyName.split("@").length > 1)
			return false;
		if (request.getAttribute("keya." + keyName) == null) {
			Query query = new Query();
			query.setRequest(request);
			query.sql =
			("select KEY_TEXT,KEY_ID from WB_KEY where KEY_TYPE='" +
			keyName + "' order by KEY_ID");
			query.jndi = ((String) request.getAttribute("sys.sysJndi"));
			query.setName("keytb." + keyName);
			query.create();
			ResultSet rs = (ResultSet) request.getAttribute("keytb."
					+ keyName);
			ResultSetMetaData meta = rs.getMetaData();
			request.setAttribute("keya." + keyName, "var " + keyName
					+ "__kdt=" +
					getDataArray(rs, meta, false) + ";");
			return true;
		} else
			return false;
	}

	public static String getCountSql(String sql) {
		/* 791 */if (sql == null)
			/* 792 */return "";
		/* 793 */String regex = "(^select )(.*?)( from .*)";
		/* 794 */Pattern pattern = Pattern.compile(regex, 2);
		/* 795 */Matcher matcher = pattern.matcher(StringUtil.replace(
				/* 797 */StringUtil.replace(StringUtil.replace(sql, "\r", ""),
						"\n", " "),
				/* 798 */"\t", " ").trim());
		/* 799 */boolean rs = matcher.find();

		/* 801 */if ((!rs) || (matcher.groupCount() != 3)) {
			/* 802 */return "";
		}
		/* 804 */return matcher.group(1) + "count(*)" + matcher.group(3);
	}

	public static String getHashArray(ResultSet resultSet, String name,
			int count) throws SQLException {
		/* 809 */boolean first = true;
		/* 810 */StringBuilder text = new StringBuilder();
		/* 811 */StringBuilder value = new StringBuilder();

		/* 813 */if (!setResultSetToFirst(resultSet)) {
			/* 814 */return "var " + name + "__text=new Array()," + name +
			/* 815 */"__value=new Array();";
		}
		/* 817 */boolean hasValue = count > 1;
		/* 818 */text.append("var ");
		/* 819 */text.append(name);
		/* 820 */text.append("__text=new Array(");
		/* 821 */while ((first) || (resultSet.next())) {
			/* 822 */if (first) {
				/* 823 */first = false;
			} else {
				/* 825 */text.append(",");
				/* 826 */if (hasValue)
					/* 827 */value.append(",");
			}
			/* 829 */text.append("\"");
			/* 830 */text.append(StringUtil.toExpress(resultSet.getString(1)));
			/* 831 */text.append("\"");
			/* 832 */if (hasValue) {
				/* 833 */value.append("\"");
				/* 834 */value.append(StringUtil.toExpress(resultSet
						.getString(2)));
				/* 835 */value.append("\"");
			}
		}
		/* 838 */if (hasValue) {
			/* 839 */text.append(");var ");
			/* 840 */text.append(name);
			text.append("__value=new Array(");
			text.append(value.toString());
			text.append(");");
			return text.toString();
		}
		text.append(");");
		System.out.println(text.toString());
		return text.toString();
	}

	public static void getQueryValue(HttpServletRequest request,
			String queryName) throws SQLException {
		ResultSet resultSet = (ResultSet) request.getAttribute(queryName);
		/* 854 */if (!setResultSetToFirst(resultSet))
			/* 855 */return;
		/* 856 */ResultSetMetaData meta = resultSet.getMetaData();
		/* 857 */int j = meta.getColumnCount();

		/* 859 */for (int i = 1; i <= j; i++) {
			/* 860 */int type = meta.getColumnType(i);
			/* 861 */if ((type == -2) || (type == -3) || (type == -4)
					|| (type == 2004))
				/* 862 */request.setAttribute(queryName + "."
						+ meta.getColumnLabel(i),
				/* 863 */resultSet.getBinaryStream(1));
			else
				/* 865 */request.setAttribute(queryName + "."
						+ meta.getColumnLabel(i),
				/* 866 */getFieldValue(resultSet, meta, i, false));
		}
	}

	public static void recordLog(String jndi, String userName, String ip,
			String msg, int type) throws Exception {
		/* 872 */Connection connection = null;
		/* 873 */PreparedStatement statement = null;
		byte[] bt;
		try {
			/* 877 */if (ip == null)
				/* 878 */ip = "";
			/* 879 */if (msg == null)
				/* 880 */msg = "";
			/* 881 */if (userName == null)
				/* 882 */userName = "未知";
			/* 883 */connection = getConnection(jndi);
			/* 884 */statement = connection
			/* 885 */.prepareStatement("insert into WB_LOG values(?,?,?,?,?)");
			/* 886 */statement
					.setObject(1, new Timestamp(new Date().getTime()));
			/* 887 */statement.setObject(2, userName);
			/* 888 */statement.setObject(3, ip);
			/* 889 */bt = msg.getBytes();
			/* 890 */if (bt.length > 250)
				/* 891 */statement.setObject(4, new String(bt, 0, 250) + "~");
			else
				/* 893 */statement.setObject(4, msg);
			/* 894 */statement.setObject(5, Integer.valueOf(type));
			/* 895 */statement.executeUpdate();
		} finally {
			/* 897 */if (statement != null)
				/* 898 */statement.close();
			/* 899 */if (connection != null)
				/* 900 */connection.close();
		}
	}

	public static void recordHistory(String jndi, String action, String userName) {
		/* 905 */if ((StringUtil.isEmpty(userName))
				|| (StringUtil.isEmpty(action)))
			/* 906 */return;
		/* 907 */Connection connection = null;
		/* 908 */PreparedStatement statement = null;
		try {
			/* 911 */connection = getConnection(jndi);
			/* 912 */statement = connection
					/* 913 */.prepareStatement("update WB_USER set PORTAL_PATH=? where USERNAME=?");
			/* 914 */statement.setObject(1, action);
			/* 915 */statement.setObject(2, userName);
			/* 916 */statement.executeUpdate();
		} catch (Exception localException) {
			
			localException.printStackTrace();
			try {
				/* 920 */if (statement != null)
					/* 921 */statement.close();
				/* 922 */if (connection != null)
					/* 923 */connection.close();
			} catch (Exception localException1) {
			}
		} finally {
			try {
				/* 920 */if (statement != null)
					/* 921 */statement.close();
				/* 922 */if (connection != null)
					/* 923 */connection.close();
			} catch (Exception localException2) {
			}
		}
	}

	public static void importTxtStream(Connection conn, String tableName,
			InputStream stream, String charset, String colNames,
			String[] values, Object[] mapList, Integer[] indexList,
			int userIndex, String separator) throws Exception {
		/* 933 */Statement stm = conn.createStatement();
		/* 934 */ResultSet rs = stm.executeQuery("select * from " + tableName +
		/* 935 */" where 1=0");
		/* 936 */ResultSetMetaData meta = rs.getMetaData();

		/* 939 */int[] types = (int[]) null;
		int k = 0;
		/* 940 */boolean isFirst = true;
		boolean hasData = false;
		/* 941 */HashMap metaMap = new HashMap();
		/* 942 */PreparedStatement st = null;

		/* 944 */int j = meta.getColumnCount() + 1;
		int i;
		/* 945 */for (i = 1; i < j; i++)
			/* 946 */metaMap.put(meta.getColumnLabel(i), Integer.valueOf(meta
					.getColumnType(i)));
		/* 947 */closeResultSet(rs);
		BufferedReader br;

		/* 948 */if (StringUtil.isEmpty(charset))
			/* 949 */br = new BufferedReader(new InputStreamReader(stream));
		else
			/* 951 */br = new BufferedReader(new InputStreamReader(stream,
					charset));
		/* 952 */conn.setAutoCommit(false);
		String s;
		try {

			/* 954 */while ((s = br.readLine()) != null) {

				/* 955 */if (isFirst) {
					String[] fields;

					/* 957 */if (StringUtil.isEmpty(colNames))
						/* 958 */fields = StringUtil.split(s, separator);
					else
						/* 960 */fields = StringUtil.split(colNames, ",");
					/* 961 */j = fields.length;
					/* 962 */String[] vals = new String[j];
					/* 963 */types = new int[j];
					/* 964 */for (i = 0; i < j; i++) {
						/* 965 */vals[i] = "?";
						/* 966 */types[i] = ((Integer) metaMap.get(fields[i]))
								.intValue();
					}
					/* 968 */String fieldSql = StringUtil
							.joinArray(fields, ",");
					/* 969 */String valSql = StringUtil.joinArray(vals, ",");
					/* 970 */st = conn.prepareStatement("insert into "
							+ tableName +
							/* 971 */" (" + fieldSql + ") values(" + valSql
							+ ")");
					/* 972 */isFirst = false;
				} else {
					/* 974 */if (StringUtil.isEmpty(s))
						continue;
					/* 976 */String[] vals = StringUtil.split(s, separator);
					/* 977 */int l = vals.length;
					/* 978 */for (i = 0; i < j; i++) {
						String t;
						/* 979 */if (i < l)
							/* 980 */t = vals[i];
						else
							/* 982 */t = values[(i - l)];
						/* 983 */t = getMapValue(mapList, indexList, t, i + 1);
						/* 984 */if (i + 1 == userIndex)
							/* 985 */checkUserName(t);
						/* 986 */if (StringUtil.isEmpty(t)) {
							/* 987 */st.setNull(i + 1, types[i]);
						} else {
							/* 989 */t = StringUtil.parseExpress(t);
							/* 990 */switch (types[i]) {
							case -6:
							case -5:
							case 4:
							case 5:
								/* 995 */
								st.setInt(i + 1, Integer.parseInt(
								/* 996 */StringUtil.replace(t, ",", "")));
								/* 997 */break;
							case 2:
							case 3:
							case 7:
							case 8:
								/* 1002 */
								st.setDouble(i + 1,
								/* 1003 */Double.parseDouble(StringUtil
										.replace(t, ",",
										/* 1004 */"")));
								/* 1005 */break;
							case 6:
								/* 1007 */
								st.setFloat(i + 1, Float.parseFloat(
								/* 1008 */StringUtil.replace(t, ",", "")));
								/* 1009 */break;
							case 91:
							case 93:
								/* 1012 */
								st.setTimestamp(i + 1,
								/* 1013 */new Timestamp(DateUtil
										.stringToStdDate(t).getTime()));
								/* 1014 */break;
							default:
								/* 1016 */
								st.setObject(i + 1, t);
							}
						}
					}
					/* 1020 */hasData = true;
					/* 1021 */st.addBatch();
					/* 1022 */k++;
					/* 1023 */if ((k > 0) && (k % 1000 == 0)) {
						/* 1024 */st.executeBatch();
						/* 1025 */hasData = false;
					}
				}
			}
			/* 1029 */if (hasData)
				/* 1030 */st.executeBatch();
			/* 1031 */conn.commit();
		} catch (Exception e) {
			/* 1033 */conn.rollback();
			/* 1034 */throw new Exception(e);
		} finally {
			/* 1036 */br.close();
			/* 1037 */conn.setAutoCommit(true);
			/* 1038 */if (stm != null)
				/* 1039 */closeStatement(stm);
			/* 1040 */if (st != null)
				/* 1041 */closeStatement(st);
		}
	}

	public static void importTxtFile(Connection conn, File file,
			String charset, String colNames, String[] values, Object[] mapList,
			Integer[] indexList, int userIndex, String separator)
			throws Exception {
		/* 1049 */importTxtStream(conn, FileUtil.extractFileNameNoExt(file
		/* 1050 */.getAbsolutePath()), new FileInputStream(file), charset,
		/* 1051 */colNames, values, mapList, indexList, userIndex, separator);
	}

	/* 1058 */public static void importExcelStream(Connection conn,
			String tableName, InputStream stream, int startRow,
			String colNames, String[] values, Object[] mapList,
			Integer[] indexList, int userIndex) throws Exception {
		Statement stm = conn.createStatement();
		/* 1059 */ResultSet rs = stm.executeQuery("select * from " + tableName +
		/* 1060 */" where 1=0");
		/* 1061 */ResultSetMetaData meta = rs.getMetaData();

		/* 1063 */int[] types = (int[]) null;
		int k = 0;
		/* 1064 */boolean isFirst = true;
		boolean hasData = false;
		/* 1065 */HashMap metaMap = new HashMap();
		/* 1066 */PreparedStatement st = null;

		/* 1068 */Workbook book = null;

		/* 1070 */SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		/* 1072 */conn.setAutoCommit(false);

		try {
			book = Workbook.getWorkbook(stream);
			/* 1075 */Sheet ws = book.getSheet(0);
			/* 1076 */int x = ws.getRows();
			/* 1077 */int y = ws.getColumns();
			/* 1078 */int l = y;
			/* 1079 */int j = meta.getColumnCount() + 1;
			/* 1080 */for (int i = 1; i < j; i++)
				/* 1081 */metaMap.put(meta.getColumnLabel(i), Integer
						.valueOf(meta.getColumnType(i)));
			/* 1082 */closeResultSet(rs);
			/* 1083 */for (int n = startRow; n < x; n++)
				/* 1084 */if (isFirst) {
					String[] fields;

					/* 1086 */if (StringUtil.isEmpty(colNames)) {
						/* 1087 */fields = new String[y];
					} else {
						/* 1089 */fields = colNames.split(",");
						/* 1090 */y = fields.length;
					}
					/* 1092 */String[] vals = new String[y];
					/* 1093 */types = new int[y];
					/* 1094 */for (int m = 0; m < y; m++) {
						/* 1095 */if (StringUtil.isEmpty(colNames))
							/* 1096 */fields[m] = ws.getCell(m, n)
									.getContents();
						/* 1097 */vals[m] = "?";
						/* 1098 */types[m] = ((Integer) metaMap.get(fields[m]))
								.intValue();
					}
					/* 1100 */String fieldSql = StringUtil.joinArray(fields,
							",");
					/* 1101 */String valSql = StringUtil.joinArray(vals, ",");
					/* 1102 */st = conn.prepareStatement("insert into "
							+ tableName +
							/* 1103 */" (" + fieldSql + ") values(" + valSql
							+ ")");
					/* 1104 */isFirst = false;
				} else {
					/* 1106 */for (int i = 0; i < y; i++) {
						String s;
						/* 1107 */if (i < l) {
							/* 1108 */Cell cell = ws.getCell(i, n);

							/* 1109 */if (cell.getType() == CellType.DATE)
								/* 1110 */s = format.format(((DateCell) cell)
										.getDate());
							else
								/* 1112 */s = cell.getContents();
						} else {
							/* 1114 */s = values[(i - l)];
							/* 1115 */}
						s = getMapValue(mapList, indexList, s, i + 1);
						/* 1116 */if (i + 1 == userIndex)
							/* 1117 */checkUserName(s);
						/* 1118 */if (StringUtil.isEmpty(s))
							/* 1119 */st.setNull(i + 1, types[i]);
						else {
							/* 1121 */switch (types[i]) {
							case -6:
							case -5:
							case 4:
							case 5:
								/* 1126 */
								st.setInt(i + 1, Integer.parseInt(
								/* 1127 */StringUtil.replace(s, ",", "")));
								/* 1128 */break;
							case 2:
							case 3:
							case 7:
							case 8:
								/* 1133 */
								st.setDouble(i + 1,
								/* 1134 */Double.parseDouble(StringUtil
										.replace(s, ",",
										/* 1135 */"")));
								/* 1136 */break;
							case 6:
								/* 1138 */
								st.setFloat(i + 1, Float.parseFloat(
								/* 1139 */StringUtil.replace(s, ",", "")));
								/* 1140 */break;
							case 91:
							case 93:
								/* 1143 */
								st.setTimestamp(i + 1,
								/* 1144 */new Timestamp(DateUtil
										.stringToStdDate(s).getTime()));
								/* 1145 */break;
							default:
								/* 1147 */
								st.setObject(i + 1, s);
							}
						}
					}
					/* 1151 */hasData = true;
					/* 1152 */st.addBatch();
					/* 1153 */k++;
					/* 1154 */if ((k > 0) && (k % 1000 == 0)) {
						/* 1155 */st.executeBatch();
						/* 1156 */hasData = false;
					}
				}
			/* 1159 */if (hasData)
				/* 1160 */st.executeBatch();
			/* 1161 */conn.commit();
		} catch (Exception e) {
			/* 1163 */conn.rollback();
			/* 1164 */throw new Exception(e);
		} finally {
			/* 1166 */if (book != null)
				/* 1167 */book.close();
			/* 1168 */conn.setAutoCommit(true);
			/* 1169 */if (stm != null)
				/* 1170 */closeStatement(stm);
			/* 1171 */if (st != null)
				/* 1172 */closeStatement(st);
		}
	}

	public static void importExcelFile(Connection conn, File file,
			int startRow, String colNames, String[] values, Object[] mapList,
			Integer[] indexList, int userIndex) throws Exception {
		/* 1179 */importExcelStream(conn, FileUtil.extractFileNameNoExt(file
		/* 1180 */.getAbsolutePath()), new FileInputStream(file), startRow,
		/* 1181 */colNames, values, mapList, indexList, userIndex);
	}

	public static void checkUserName(String username) throws Exception {
		/* 1185 */int len = username.length();

		/* 1187 */for (int i = 0; i < len; i++) {
			/* 1188 */char c = username.charAt(i);
			/* 1189 */if (((c >= 'a') && (c <= 'z'))
					|| ((c >= '0') && (c <= '9')) || (c == '_') ||
					/* 1190 */(c == '@') || (c == '.'))
				continue;
			/* 1191 */throw new Exception("用户名称\"" + username +
			/* 1192 */"\"非法，只允许小写的邮件地址或小写字母、数字和下划线。");
		}
	}

	public static HashMap<String, String> initTextMaps(
			HttpServletRequest request, String mapKey) throws Exception {
		/* 1198 */String[] m = mapKey.split(",");
		/* 1199 */int j = m.length;
		/* 1200 */HashMap map = new HashMap();

		/* 1203 */for (int i = 0; i < j; i++) {
			/* 1204 */String v = StringUtil.getValuePart(m[i]);
			/* 1205 */Query query = new Query();
			/* 1206 */query.setRequest(request);
			/* 1207 */query.sql =
			/* 1208 */("select KEY_ID,KEY_TEXT from WB_KEY where KEY_TYPE='" +
			/* 1208 */v + "' order by KEY_ID");
			/* 1209 */query.jndi = ((String) request
					.getAttribute("sys.sysJndi"));
			/* 1210 */query.setName("keytd." + v);
			/* 1211 */query.create();
			/* 1212 */ResultSet rs = (ResultSet) request.getAttribute("keytd."
					+ v);
			/* 1213 */String t = StringUtil.getNamePart(m[i]);
			/* 1214 */while (rs.next()) {
				/* 1215 */map.put(t + "_" + rs.getString(1), rs
				/* 1216 */.getString(2));
			}
		}
		/* 1219 */return map;
	}

	public static void initValueMaps(HttpServletRequest request, String mapKey)
			throws Exception {
		/* 1224 */String[] m = mapKey.split(",");
		/* 1225 */int j = m.length;
		/* 1226 */Object[] mapList = new Object[j];
		/* 1227 */Integer[] indexList = new Integer[j];

		/* 1230 */for (int i = 0; i < j; i++) {
			/* 1231 */String v = StringUtil.getValuePart(m[i]);
			/* 1232 */Query query = new Query();
			/* 1233 */query.setRequest(request);
			/* 1234 */query.sql =
			/* 1235 */("select KEY_TEXT,KEY_ID from WB_KEY where KEY_TYPE='" +
			/* 1235 */v + "' order by KEY_ID");
			/* 1236 */query.jndi = ((String) request
					.getAttribute("sys.sysJndi"));
			/* 1237 */query.setName("keymd." + v);
			/* 1238 */query.create();
			/* 1239 */ResultSet rs = (ResultSet) request.getAttribute("keymd."
					+ v);
			/* 1240 */HashMap map = new HashMap();
			/* 1241 */while (rs.next()) {
				/* 1242 */map.put(rs.getString(1), Integer
						.valueOf(rs.getInt(2)));
			}
			/* 1244 */mapList[i] = map;
			/* 1245 */indexList[i] = Integer.valueOf(StringUtil
					.getNamePart(m[i]));
		}
		/* 1247 */request.setAttribute("mapd.obj", mapList);
		/* 1248 */request.setAttribute("mapd.lst", indexList);
	}

	private static String getMapValue(Object[] mapList, Integer[] indexList,
			String value, int col) throws Exception {
		/* 1253 */if (mapList == null)
			/* 1254 */return value;
		/* 1255 */int j = indexList.length;

		/* 1258 */for (int i = 0; i < j; i++)
			/* 1259 */if (col == indexList[i].intValue()) {
				/* 1260 */if (StringUtil.isEmpty(value))
					/* 1261 */return "";
				/* 1262 */Integer it = (Integer) ((HashMap) mapList[i])
						.get(value);
				/* 1263 */if (it == null)
					/* 1264 */throw new Exception("第" + col + "列值" + value
							+ "非法。");
				/* 1265 */return Integer.toString(it.intValue());
			}
		/* 1267 */return value;
	}

	public static void loadDataToSet(ResultSet rs, HashSet<String> dataSet)
			throws Exception {
		/* 1272 */while (rs.next())
			/* 1273 */dataSet.add(rs.getString(1));
	}

	public static void loadMultiSet(Connection conn, String sql,
			String[] params, HashSet<String> dataSet) throws Exception {
		/* 1279 */PreparedStatement stm = null;

		/* 1282 */stm = conn.prepareStatement(sql);
		try {
			/* 1284 */for (String s : params) {
				/* 1285 */stm.setString(1, s);
				/* 1286 */ResultSet rs = stm.executeQuery();
				/* 1287 */loadDataToSet(rs, dataSet);
				/* 1288 */rs.close();
			}
		} finally {
			/* 1291 */closeStatement(stm);
		}
	}

	/*      */   public static HashSet<String> getUserList(Connection conn, String dbType, String scope, String depts, String roles, String users)
	/*      */     throws Exception
	/*      */   {
	/* 1320 */     HashSet userList = new HashSet();
	/*      */ 
	/* 1324 */     if (!StringUtil.isEmpty(depts))
	/* 1325 */       loadMultiSet(conn, "select CODE from WB_USER where SCOPE='" + 
	/* 1326 */         scope + "' and DEPT=?", StringUtil.split(depts, ","), 
	/* 1327 */         userList);
	/* 1328 */     if (!StringUtil.isEmpty(roles))
	/*      */     {
	/*      */       String rname;

	/* 1330 */       if (StringUtil.isSame(dbType, "oracle")) {
	/* 1331 */         rname = "','||ROLENAME||','";
	/*      */       }
	/*      */       else
	/*      */       {
  
	/* 1332 */         if (StringUtil.isSame(dbType, "mysql"))
	/* 1333 */           rname = "concat(',',ROLENAME,',')";
	/*      */         else
	/* 1335 */           rname = "','+ROLENAME+','"; 
	/*      */       }
	/* 1336 */       String[] tempList = StringUtil.split(roles, ",");
	/* 1337 */       int j = tempList.length;
	/* 1338 */       for (int i = 0; i < j; i++)
	/* 1339 */         tempList[i] = ("%," + tempList[i] + ",%");
	/* 1340 */       loadMultiSet(conn, "select CODE from WB_USER where SCOPE='" + 
	/* 1341 */         scope + "' and " + rname + " like ?", tempList, userList);
	/*      */     }
	/* 1343 */     if (!StringUtil.isEmpty(users)) {
	/* 1344 */       String[] tempList = StringUtil.split(users, ",");
	/* 1345 */       int j = tempList.length;
	/* 1346 */       for (int i = 0; i < j; i++)
	/* 1347 */         tempList[i] = StringUtil.getBracketText(tempList[i], true);
	/* 1348 */       loadMultiSet(conn, "select CODE from WB_USER where SCOPE='" + 
	/* 1349 */         scope + "' and CODE=?", tempList, userList);
	/*      */     }
	/* 1351 */     return userList;
	/*      */   }
	public static void importData(String sqlFile, String dir, String jndi,
			String charset, boolean isUpdate, String separator)
			throws Exception {
		/* 1299 */File path = new File(dir);
		File[] files = path.listFiles();
		/* 1300 */Connection conn = getConnection(jndi);
		/* 1301 */Statement st = null;

		/* 1303 */ArrayList fileList = new ArrayList();
		try {
			/* 1306 */if (!StringUtil.isEmpty(sqlFile)) {
				/* 1307 */String[] sqls =
				/* 1308 */FileUtil.readText(path + "/" + sqlFile, charset)
						.split(";");
				/* 1309 */st = conn.createStatement();
				String[] arrayOfString1;
				/* 1310 */int i = (arrayOfString1 = sqls).length;
				for (int str1 = 0; str1 < i; str1++) {
					String sql = arrayOfString1[str1];
					/* 1311 */if (sql != null) {
						/* 1312 */sql = sql.trim();
						/* 1313 */if (!StringUtil.isEmpty(sql)) {
							/* 1314 */if (sql.length() > 10)
								;
							/* 1314 */boolean isDrop =
							/* 1315 */sql.substring(0, 10).toUpperCase()
							/* 1316 */.equals("DROP TABLE");
							/* 1317 */if ((isDrop) &&
							/* 1318 */(isUpdate) &&
							/* 1319 */(!StringUtil.isSame(sql,
							/* 1320 */"DROP TABLE WB_KEY")))
								continue;
							try {
								/* 1323 */st.executeUpdate(sql);
							} catch (Exception e) {
								/* 1325 */int p = sql.indexOf("CREATE TABLE");
								/* 1326 */if ((isUpdate) && (p > -1)) {
									/* 1327 */fileList.add(sql.substring(
											p + 13,
											/* 1328 */sql.indexOf("(")).trim());
								}
								/* 1330 */if ((!isDrop) && (!isUpdate))
									/* 1331 */throw e;
							}
						}
					}
				}
			}
			/* 1336 */File[] arrayOfFile1;
			/* 1336 */int str1 = (arrayOfFile1 = files).length;
			for (int sql = 0; sql < str1; sql++) {
				File f = arrayOfFile1[sql];
				/* 1337 */String s = f.getName();
				/* 1338 */if ((s.length() <= 3)
						||
						/* 1339 */(!s.substring(0, 3).equalsIgnoreCase("WB_"))
						||
						/* 1340 */(fileList.indexOf(FileUtil
								.extractFileNameNoExt(s)) != -1))
					continue;
				/* 1341 */if (StringUtil.isSame(FileUtil.extractFileExt(s),
						"xls"))
					/* 1342 */importExcelFile(conn, f, 1, null, null, null,
							null, -1);
				else
					/* 1344 */importTxtFile(conn, f, charset, null, null, null,
							null,
							/* 1345 */-1, separator);
			}
		} finally {
			/* 1349 */if (st != null)
				/* 1350 */closeStatement(st);
			/* 1351 */conn.close();
		}
	}
}

/*
 * Location: Z:\EXT\webbuilder2.jar Qualified Name: com.webbuilder.utils.DbUtil
 * JD-Core Version: 0.6.0
 */