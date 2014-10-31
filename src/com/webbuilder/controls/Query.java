/*     */ package com.webbuilder.controls;
/*     */ 
/*     */ import com.itextpdf.text.log.SysoLogger;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.webbuilder.utils.DateUtil;
/*     */ import com.webbuilder.utils.DbUtil;
/*     */ import com.webbuilder.utils.StringUtil;
/*     */ import java.io.InputStream;
/*     */ import java.io.StringReader;
/*     */ import java.sql.CallableStatement;
/*     */ import java.sql.Connection;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Time;
import java.sql.Timestamp;
/*     */ 
/*     */ public class Query extends Control
/*     */ {
			protected static Logger logger =LoggerFactory.getLogger(Query.class);
/*  18 */   public String jndi = "";
/*  19 */   public String sql = "";
/*  20 */   public String type = "";
/*  21 */   public String transaction = "";
/*  22 */   public String isolation = "";
/*  23 */   public boolean loadData = false;
/*  24 */   public boolean fastMode = true;
/*  25 */   protected String paramList = "";
/*  26 */   private String outParamList = "";
/*  27 */   private String[] sqlTypes = { "CHAR=1", "VARCHAR=12", "LONGVARCHAR=-1", 
/*  28 */     "NUMERIC=2", "DECIMAL=3", "INTEGER=4", "SMALLINT=5", "BIGINT=-5", 
/*  29 */     "TINYINT=-6", "FLOAT=6", "REAL=7", "DOUBLE=8", "BIT=-7", "DATE=91", 
/*  30 */     "TIME=92", "TIMESTAMP=93", "BINARY=-2", "VARBINARY=-3", 
/*  31 */     "LONGVARBINARY=-4", "NULL=0", "OTHER=1111", "JAVA_OBJECT=2000", 
/*  32 */     "DISTINCT=2001", "STRUCT=2002", "ARRAY=2003", "BLOB=2004", 
/*  33 */     "CLOB=2005", "REF=2006", "DATALINK=70", "BOOLEAN=16", "ROWID=-8", 
/*  34 */     "NCHAR=-15", "NVARCHAR=-9", "LONGNVARCHAR=-16", "NCLOB=2011", 
/*  35 */     "SQLXML=2009" };
/*     */ 
/*     */   protected void descript()
/*     */     throws Exception
/*     */   {
/*  38 */     this.sql = StringUtil.replaceParameters(this.request, this.sql);
/*  39 */     if (StringUtil.isEmpty(this.sql))
/*  40 */       return;
/*  41 */     boolean isCall = false;
/*     */ 
/*  44 */     PreparedStatement statement = null;
/*     */ 
/*  46 */     if (StringUtil.isEmpty(this.type))
/*  47 */       this.type = "query";
/*     */     String defaultJndi;
/*     */ 
/*  48 */     if (StringUtil.isEmpty(this.jndi))
/*  49 */       defaultJndi = fetchString("sys.jndi");
/*     */     else
/*  51 */       defaultJndi = this.jndi;
/*  52 */     Connection connection = fetchConnection("jndi." + defaultJndi);
/*  53 */     if (connection == null) {
/*  54 */       connection = DbUtil.getConnection(defaultJndi);
/*  55 */       this.request.setAttribute("jndi." + defaultJndi, connection); } 
			DbUtil.setTransactionIsolation(connection, this.transaction, this.isolation);
/*     */     boolean execMode;
/*     */     try { this.sql = replaceSqlParameters(this.sql);
/*  60 */      execMode = StringUtil.isSame(this.type, "execute");
/*  61 */       if (this.fastMode) {
/*  62 */         if ((execMode) && (this.sql.trim().substring(0, 1).equals("{"))) {
/*  63 */           statement = connection.prepareCall(this.sql);
					logger.info("执行SQL", this.sql.toString());
/*  64 */           isCall = true;
/*     */         } else {
					logger.info("执行SQL", this.sql.toString());
/*  66 */           statement = connection.prepareStatement(this.sql);
					
/*     */         }
/*     */       } else statement = connection.prepareStatement(this.sql, 
/*  69 */           1004, 
/*  70 */           1007);
				logger.info("执行SQL", this.sql.toString());
/*  71 */       regParameters(statement);
/*  72 */       if (StringUtil.isSame(this.type, "query")) {
					System.out.println( this.sql.toString()+"  query 查询");
/*  73 */         this.request.setAttribute(this.name, statement.executeQuery());
/*  74 */         if (this.loadData)
/*  75 */           DbUtil.getQueryValue(this.request, this.name);
/*  76 */       } else if (StringUtil.isSame(this.type, "update")) {
/*  77 */         this.request.setAttribute(this.name, Integer.valueOf(statement.executeUpdate()));
/*  78 */         DbUtil.closeStatement(statement);
/*  79 */       } else if (execMode) {
/*  80 */         if (statement.execute()) {
/*  81 */           this.request.setAttribute(this.name, statement.getResultSet());
/*     */         } else {
/*  83 */           this.request.setAttribute(this.name, Integer.valueOf(statement.getUpdateCount()));
/*  84 */           if (isCall)
/*  85 */             getOutParameter((CallableStatement)statement);
/*  86 */           DbUtil.closeStatement(statement);
/*     */         }
/*     */       }
/*  89 */       if (this.transaction.equals("commit"))
/*  90 */         connection.commit();
/*     */     } catch (Exception e) {
/*  92 */       if (this.transaction.equals("commit"))
/*  93 */         connection.rollback();
/*  94 */       if (statement != null)
/*  95 */         DbUtil.closeStatement(statement);
/*  96 */       throw new Exception(e);
/*     */     } finally {
/*  98 */       if (this.transaction.equals("commit"))
/*  99 */         connection.setAutoCommit(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   private int getSqlType(String type) {
/* 104 */     int j = this.sqlTypes.length;
/*     */ 
/* 106 */     for (int i = 0; i < j; i++)
/* 107 */       if (StringUtil.isSame(StringUtil.getNamePart(this.sqlTypes[i]), type))
/* 108 */         return Integer.parseInt(StringUtil.getValuePart(this.sqlTypes[i]));
/* 109 */     if (StringUtil.isNumeric(type, false)) {
/* 110 */       return Integer.parseInt(type);
/*     */     }
/* 112 */     return 12;
/*     */   }
/*     */ 
/*     */   protected void regParameters(PreparedStatement statement) throws Exception {
/* 116 */     if (StringUtil.isEmpty(this.paramList))
/* 117 */       return;
/* 118 */     String[] params = StringUtil.split(this.paramList, ",");
/*     */ 
/* 120 */     int j = params.length;
/*     */ 
/* 123 */     this.outParamList = "";
/*     */     CallableStatement callStatement;
/*     */   
/* 124 */     if ((statement instanceof CallableStatement))
/* 125 */       callStatement = (CallableStatement)statement;
/*     */     else
/* 127 */       callStatement = null;
/* 128 */     for (int i = 0; i < j; i++) {
/* 129 */       String param = params[i];
/* 130 */       if (i > 0)
/* 131 */         this.outParamList += ",";
/* 132 */       if ((callStatement != null) && (param.length() > 1) && 
/* 133 */         (StringUtil.isEqual(param.substring(0, 1), "@"))) {
/* 134 */         param = param.substring(1);
/* 135 */         int dotPos = param.indexOf(".");
/* 136 */         if (dotPos != -1) {
/* 137 */           this.outParamList += param.substring(param.lastIndexOf(".") + 1);
/* 138 */           int type = getSqlType(param.substring(0, dotPos));
/* 139 */           param = param.substring(dotPos + 1);
/* 140 */           dotPos = param.indexOf(".");
/*     */           String subType;
/* 141 */           if ((dotPos != -1) && 
/* 142 */             (StringUtil.isNumeric(subType = param.substring(
/* 143 */             0, dotPos), false))) {
/* 144 */             param = param.substring(dotPos + 1);
/* 145 */             callStatement.registerOutParameter(i + 1, type, 
/* 146 */               Integer.parseInt(subType));
/*     */           } else {
/* 148 */             callStatement.registerOutParameter(i + 1, type);
/*     */           }
/*     */         } else {
/* 150 */           this.outParamList += param;
/* 151 */           callStatement.registerOutParameter(i + 1, 12);
/*     */         }
/*     */       } else {
/* 154 */         setObjectValue(statement, param, i + 1, null);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void setObjectValue(PreparedStatement statement, String param, int index, String defaultValue)
/*     */     throws Exception
/*     */   {
/* 164 */     Object object = fetchObject(param);
/*     */     String name;
/*     */     String type;
/*     */    
/* 165 */     if (object != null) {
/* 166 */        type = "";
/* 167 */       name = param;
/*     */     } else {
/* 169 */       int dotPos = param.indexOf(".");
/*     */     
/* 170 */       if (dotPos == -1) {
/* 171 */        type = "";
/* 172 */         name = param;
/*     */       } else {
/* 174 */         type = param.substring(0, dotPos);
/* 175 */         name = param.substring(dotPos + 1);
/*     */       }
/*     */     }
/* 178 */     if (defaultValue != null)
/* 179 */       object = defaultValue;
/* 180 */     else if (object == null)
/* 181 */       object = fetchObject(name);
/* 182 */     if (object == null) {
/* 183 */       if (StringUtil.isSame(type, "charstream"))
/* 184 */         type = "varchar";
/* 185 */       statement.setNull(index, getSqlType(type));
/* 186 */       return;
/*     */     }
/* 188 */     if (type.equalsIgnoreCase("charstream")) {
/* 189 */       String value = object.toString();
/* 190 */       if (StringUtil.isEmpty(value))
/* 191 */         statement.setNull(index, 12);
/*     */       else
/* 193 */         statement.setCharacterStream(index, new StringReader(value), 
/* 194 */           value.length());
/* 195 */     } else if ((object instanceof InputStream)) {
/* 196 */       statement.setBinaryStream(index, (InputStream)object, 
/* 197 */         ((InputStream)object).available());
/* 198 */     } else if (!(object instanceof String)) {
/* 199 */       statement.setObject(index, object);
/*     */     } else {
/* 201 */       String value = object.toString();
/* 202 */       if (StringUtil.isEmpty(value)) {
/* 203 */         statement.setNull(index, getSqlType(type));
/* 204 */         return;
/*     */       }
/* 206 */       if ((StringUtil.isEmpty(type)) || (StringUtil.isSame(type, "VARCHAR")))
/* 207 */         object = value;
/* 208 */       else if (StringUtil.isSame(type, "INTEGER"))
/* 209 */         object = Integer.valueOf(Integer.parseInt(value));
/* 210 */       else if (StringUtil.isSame(type, "SMALLINT"))
/* 211 */         object = Short.valueOf(Short.parseShort(value));
/* 212 */       else if (StringUtil.isSame(type, "BIGINT"))
/* 213 */         object = Long.valueOf(Long.parseLong(value));
/* 214 */       else if (StringUtil.isSame(type, "FLOAT"))
/* 215 */         object = Float.valueOf(Float.parseFloat(value));
/* 216 */       else if (StringUtil.isSame(type, "DOUBLE"))
/* 217 */         object = Double.valueOf(Double.parseDouble(value));
/* 218 */       else if ((StringUtil.isSame(type, "TIMESTAMP")) || 
/* 219 */         (StringUtil.isSame(type, "DATE")))
/* 220 */         object = new Timestamp(DateUtil.stringToStdDate(value)
/* 221 */           .getTime());
/* 222 */       else if (StringUtil.isSame(type, "TIME"))
/* 223 */         object = new Time(DateUtil.stringToDate(
/* 224 */           DateUtil.fullTimeValue(value), "HH:mm:ss").getTime());
/* 225 */       statement.setObject(index, object);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void getOutParameter(CallableStatement statement) throws SQLException
/*     */   {
/* 231 */     if (StringUtil.isEmpty(this.outParamList))
/* 232 */       return;
/* 233 */     String[] params = StringUtil.split(this.outParamList, ",");
/* 234 */     int j = params.length;
/*     */ 
/* 236 */     for (int i = 0; i < j; i++)
/* 237 */       if (!StringUtil.isEmpty(params[i]))
/* 238 */         this.request.setAttribute(params[i], statement.getObject(i + 1));
/*     */   }
/*     */ 
/*     */   protected String replaceSqlParameters(String sql) {
/* 242 */     String result = StringUtil.replaceParameters(this.request, sql);
/* 243 */     boolean isFirst = true;
/*     */ 
/* 245 */     StringBuilder paramsBuf = new StringBuilder();
/*     */ 
/* 247 */     this.paramList = "";
/*     */     int startPos;
/*     */     int endPos;
/* 248 */     while (((startPos = result.indexOf("{?")) > -1) && 
/* 249 */       ((endPos = result.indexOf("?}")) > -1))
/*     */     {
/*     */     
/* 250 */       String param = result.substring(startPos + 2, endPos);
/* 251 */       result = StringUtil.replaceFirst(result, "{?" + param + "?}", "?");
/* 252 */       if (isFirst)
/* 253 */         isFirst = false;
/*     */       else
/* 255 */         paramsBuf.append(",");
/* 256 */       paramsBuf.append(param);
/*     */     }
/* 258 */     this.paramList = paramsBuf.toString();
/* 259 */     return result;
/*     */   }
/*     */ }

/* Location:           Z:\EXT\webbuilder2.jar
 * Qualified Name:     com.webbuilder.controls.Query
 * JD-Core Version:    0.6.0
 */