/*     */ package com.webbuilder.controls;
/*     */ 
/*     */ import com.webbuilder.utils.DbUtil;
/*     */ import com.webbuilder.utils.JsonUtil;
/*     */ import com.webbuilder.utils.StringUtil;
/*     */ import java.io.InputStream;
/*     */ import java.sql.Connection;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.Statement;
/*     */ import java.util.ArrayList;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import org.json.JSONArray;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class DbUpdater extends Query
/*     */ {
/*  16 */   public String data = "";
/*  17 */   public String sqls = "";
/*  18 */   public String insertSql = "";
/*  19 */   public String updateSql = "";
/*  20 */   public String deleteSql = "";
/*  21 */   public String file = "";
/*  22 */   public String fileFormat = "";
/*  23 */   public String fileLineSeparator = "";
/*  24 */   public String bufferSize = "";
/*     */   public boolean fileHasTitle;
/*  26 */   public boolean sequence = false;
/*     */   private Connection connection;
/*     */   private int bufCount;
/*     */ 
/*     */   protected void descript()
/*     */     throws Exception
/*     */   {
/*  31 */     if (StringUtil.isEmpty(this.bufferSize))
/*  32 */       this.bufCount = 2147483647;
/*     */     else
/*  34 */       this.bufCount = Integer.parseInt(this.bufferSize);
/*  35 */     if (StringUtil.isEmpty(this.fileFormat))
/*  36 */       this.fileFormat = ((String)this.request.getAttribute("sys.charset"));
/*  37 */     if (!StringUtil.isEmpty(this.file)) {
/*  38 */       if (StringUtil.isEmpty(this.fileLineSeparator))
/*  39 */         this.fileLineSeparator = StringUtil.getSeparator(this.request);
/*  40 */       this.data = JsonUtil.getInputStreamJson(
/*  41 */         (InputStream)this.request
/*  41 */         .getAttribute(this.file), this.fileFormat, this.fileLineSeparator, 
/*  42 */         this.fileHasTitle);
/*     */     }
/*     */     JSONArray array;
/*     */    
/*  45 */     if (StringUtil.isEmpty(this.data))
/*  46 */       array = null;
/*     */     else
/*  48 */       array = new JSONArray(this.data);
/*     */     String defaultJndi;
/*     */     
/*  51 */     if (StringUtil.isEmpty(this.jndi))
/*  52 */       defaultJndi = fetchString("sys.jndi");
/*     */     else
/*  54 */       defaultJndi = this.jndi;
/*  55 */     this.connection = fetchConnection("jndi." + defaultJndi);
/*  56 */     if (this.connection == null) {
/*  57 */       this.connection = DbUtil.getConnection(defaultJndi);
/*  58 */       this.request.setAttribute("jndi." + defaultJndi, this.connection);
/*     */     }
/*  60 */     DbUtil.setTransactionIsolation(this.connection, this.transaction, this.isolation);
/*     */     try {
/*  62 */       if (!StringUtil.isEmpty(this.sqls))
/*  63 */         executeSqls(this.connection);
/*  64 */       if ((array != null) && (array.length() > 0)) {
/*  65 */         if (this.sequence) {
/*  66 */           sequenceExecute(this.connection, array);
/*     */         } else {
/*  68 */           if (!StringUtil.isEmpty(this.deleteSql))
/*     */           {
/*     */             String filterWord;
/*     */             
/*  69 */             if ((StringUtil.isEmpty(this.updateSql)) && 
/*  70 */               (StringUtil.isEmpty(this.insertSql)))
/*  71 */               filterWord = "";
/*     */             else
/*  73 */               filterWord = "__delete";
/*  74 */             executeUpdate(this.connection, this.deleteSql, array, filterWord);
/*     */           }
/*  76 */           if (!StringUtil.isEmpty(this.updateSql))
/*     */           {
/*     */             String filterWord;
/*     */            
/*  77 */             if ((StringUtil.isEmpty(this.deleteSql)) && 
/*  78 */               (StringUtil.isEmpty(this.insertSql)))
/*  79 */               filterWord = "";
/*     */             else
/*  81 */               filterWord = "__update";
/*  82 */             executeUpdate(this.connection, this.updateSql, array, filterWord);
/*     */           }
/*  84 */           if (!StringUtil.isEmpty(this.insertSql))
/*     */           {
/*     */             String filterWord;
/*     */         
/*  85 */             if ((StringUtil.isEmpty(this.deleteSql)) && 
/*  86 */               (StringUtil.isEmpty(this.updateSql)))
/*  87 */               filterWord = "";
/*     */             else
/*  89 */               filterWord = "__insert";
/*  90 */             executeUpdate(this.connection, this.insertSql, array, filterWord);
/*     */           }
/*     */         }
/*     */       }
/*  94 */       if (this.transaction.equals("commit"))
/*  95 */         this.connection.commit();
/*     */     } catch (Exception e) {
/*  97 */       if (this.transaction.equals("commit"))
/*  98 */         this.connection.rollback();
/*  99 */       throw new Exception(e);
/*     */     } finally {
/* 101 */       if (this.transaction.equals("commit"))
/* 102 */         this.connection.setAutoCommit(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void closeConnection() {
/* 107 */     DbUtil.closeConnection(this.connection, false);
/*     */   }
/*     */ 
/*     */   private void executeSqls(Connection connection)
/*     */     throws Exception
/*     */   {
/*     */     JSONArray sqlArray;
/*     */   
/* 115 */     if ((this.sqls.substring(0, 1).equals("'")) || 
/* 116 */       (this.sqls.substring(0, 1).equals("\"")))
/* 117 */       sqlArray = new JSONArray("[" + this.sqls + "]");
/*     */     else
/* 119 */       sqlArray = new JSONArray("[\"" + StringUtil.toExpress(this.sqls) + "\"]");
/* 120 */     int j = sqlArray.length();
/* 121 */     for (int i = 0; i < j; i++) {
/* 122 */       PreparedStatement statement = connection
/* 123 */         .prepareStatement(replaceSqlParameters(sqlArray
/* 124 */         .getString(i)));
/* 125 */       regParameters(statement);
/* 126 */       statement.executeUpdate();
/* 127 */       DbUtil.closeStatement(statement);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void sequenceExecute(Connection connection, JSONArray array) throws Exception
/*     */   {
/* 133 */     PreparedStatement insert = null; PreparedStatement update = null; PreparedStatement delete = null;
/* 134 */     String[] insertParams = (String[])null; String[] updateParams = (String[])null; String[] deleteParams = (String[])null;
/*     */ 
/* 137 */     int j = array.length();
/*     */ 
/* 139 */     if (!StringUtil.isEmpty(this.insertSql)) {
/* 140 */       insert = connection
/* 141 */         .prepareStatement(replaceSqlParameters(this.insertSql));
/* 142 */       regParameters(insert);
/* 143 */       insertParams = StringUtil.split(this.paramList, ",");
/*     */     }
/* 145 */     if (!StringUtil.isEmpty(this.updateSql)) {
/* 146 */       update = connection
/* 147 */         .prepareStatement(replaceSqlParameters(this.updateSql));
/* 148 */       regParameters(update);
/* 149 */       updateParams = StringUtil.split(this.paramList, ",");
/*     */     }
/* 151 */     if (!StringUtil.isEmpty(this.deleteSql)) {
/* 152 */       delete = connection
/* 153 */         .prepareStatement(replaceSqlParameters(this.deleteSql));
/* 154 */       regParameters(delete);
/* 155 */       deleteParams = StringUtil.split(this.paramList, ",");
/*     */     }int i;
/*     */     try { for (i = 0; i < j; i++) {
/* 159 */         JSONObject item = array.getJSONObject(i);
/*     */         String[] params;
/*     */         PreparedStatement stm;
/*     */         
/* 160 */         if (item.has("__update")) {
/* 161 */            stm = update;
/* 162 */           params = updateParams;
/*     */         }
/*     */         else
/*     */         {
/*     */         
/* 163 */           if (item.has("__delete")) {
/* 164 */             stm = delete;
/* 165 */             params = deleteParams;
/*     */           } else {
/* 167 */             stm = insert;
/* 168 */             params = insertParams;
/*     */           }
/*     */         }
/* 170 */         for (int k = 0; k < params.length; k++) {
/* 171 */           String param = params[k];
/* 172 */           int dotPos = param.indexOf(".");
/*     */           String name;
/*     */         
/* 173 */           if ((dotPos != -1) && (fetchObject(param) == null))
/* 174 */             name = param.substring(dotPos + 1);
/*     */           else
/* 176 */             name = param;
/* 177 */           if (item.has(name))
/* 178 */             setObjectValue(stm, param, k + 1, item.getString(name));
/*     */         }
/* 180 */         stm.executeUpdate();
/*     */       }
/*     */     } finally {
/* 183 */       if (insert != null)
/* 184 */         DbUtil.closeStatement(insert);
/* 185 */       if (update != null)
/* 186 */         DbUtil.closeStatement(update);
/* 187 */       if (delete != null)
/* 188 */         DbUtil.closeStatement(delete);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void executeUpdate(Connection connection, String sql, JSONArray array, String filterWord)
/*     */     throws Exception
/*     */   {
/* 195 */     int y = 0;
/*     */ 
/* 201 */     ArrayList statements = new ArrayList();
/* 202 */     ArrayList paramsArray = new ArrayList();
/* 203 */     ArrayList lenArray = new ArrayList();
/*     */     JSONArray sqlArray;
/*     */    
/* 205 */     if ((sql.substring(0, 1).equals("'")) || (sql.substring(0, 1).equals("\"")))
/* 206 */       sqlArray = new JSONArray("[" + sql + "]");
/*     */     else
/* 208 */       sqlArray = new JSONArray("[\"" + StringUtil.toExpress(sql) + "\"]");
/* 209 */     boolean isInsert = filterWord.equals("__insert");
/* 210 */     int j = sqlArray.length();
/* 211 */     for (int i = 0; i < j; i++) {
/* 212 */       PreparedStatement statement = connection
/* 213 */         .prepareStatement(replaceSqlParameters(sqlArray
/* 214 */         .getString(i)));
/* 215 */       regParameters(statement);
/* 216 */       statements.add(statement);
/* 217 */       String[] params = StringUtil.split(this.paramList, ",");
/* 218 */       paramsArray.add(params);
/* 219 */       int l = params.length;
/* 220 */       if (StringUtil.isEmpty(this.paramList))
/* 221 */         l = 0;
/* 222 */       lenArray.add(Integer.valueOf(l)); } 
				int l;
/*     */     int m;
/*     */     boolean batchAdded;
/*     */     boolean commitAll;
/*     */     try {  m = array.length();
/* 226 */       commitAll = false;
/* 227 */        batchAdded = false;
/* 228 */       for (int  i = 0; i < m; i++) {
/* 229 */         y = i + 1;
/* 230 */         JSONObject item = array.getJSONObject(i);
/* 231 */         if ((StringUtil.isEmpty(filterWord)) || (item.has(filterWord)) || (
/* 232 */           (isInsert) && (!item.has("__update")) && 
/* 233 */           (!item.has("__delete")))) {
/* 234 */           for ( l = 0; l < j; l++) {
/* 235 */             for (int k = 0; k < ((Integer)lenArray.get(l)).intValue(); k++) {
/* 236 */               String param = ((String[])paramsArray.get(l))[k];
/* 237 */               int dotPos = param.indexOf(".");
/*     */               String name;
/*     */             
/* 238 */               if ((dotPos != -1) && (fetchObject(param) == null))
/* 239 */                 name = param.substring(dotPos + 1);
/*     */               else
/* 241 */                 name = param;
/* 242 */               if (item.has(name))
/* 243 */                 setObjectValue((PreparedStatement)statements.get(l), param, k + 1, 
/* 244 */                   item.getString(name));
/*     */             }
/* 246 */             ((PreparedStatement)statements.get(l)).addBatch();
/* 247 */             if (!batchAdded)
/* 248 */               batchAdded = true;
/*     */           }
/*     */         }
/* 251 */         if (y % this.bufCount == 0) {
/* 252 */           if (y == m)
/* 253 */             commitAll = true;
/* 254 */           for ( l = 0; l < j; l++) {
/* 255 */             ((PreparedStatement)statements.get(l)).executeBatch();
/*     */           }
/*     */         }
/*     */       }
/* 259 */       for (l = 0; l < j; l++)
/* 260 */         if ((batchAdded) && (!commitAll))
/* 261 */           ((PreparedStatement)statements.get(l)).executeBatch();
/*     */     } finally
/*     */     {
/* 264 */       for (l = 0; l < j; l++)
/* 265 */         DbUtil.closeStatement((Statement)statements.get(l));
/*     */     }
/*     */   }
/*     */ }

/* Location:           Z:\EXT\webbuilder2.jar
 * Qualified Name:     com.webbuilder.controls.DbUpdater
 * JD-Core Version:    0.6.0
 */