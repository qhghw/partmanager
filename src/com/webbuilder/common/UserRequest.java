/*     */ package com.webbuilder.common;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.security.Principal;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import javax.servlet.RequestDispatcher;
/*     */ import javax.servlet.ServletInputStream;
/*     */ import javax.servlet.http.Cookie;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpSession;
/*     */ 
/*     */ public class UserRequest
/*     */   implements HttpServletRequest
/*     */ {
/*  19 */   HashMap<String, Object> hashMap = new HashMap();
/*     */ 
/*     */   public String getAuthType() {
/*  22 */     return null;
/*     */   }
/*     */ 
/*     */   public String getContextPath() {
/*  26 */     return null;
/*     */   }
/*     */ 
/*     */   public Cookie[] getCookies() {
/*  30 */     return null;
/*     */   }
/*     */ 
/*     */   public long getDateHeader(String arg0) {
/*  34 */     return 0L;
/*     */   }
/*     */ 
/*     */   public String getHeader(String arg0) {
/*  38 */     return null;
/*     */   }
/*     */ 
/*     */   public Enumeration<?> getHeaderNames() {
/*  42 */     return null;
/*     */   }
/*     */ 
/*     */   public Enumeration<?> getHeaders(String arg0) {
/*  46 */     return null;
/*     */   }
/*     */ 
/*     */   public int getIntHeader(String arg0) {
/*  50 */     return 0;
/*     */   }
/*     */ 
/*     */   public String getMethod() {
/*  54 */     return null;
/*     */   }
/*     */ 
/*     */   public String getPathInfo() {
/*  58 */     return null;
/*     */   }
/*     */ 
/*     */   public String getPathTranslated() {
/*  62 */     return null;
/*     */   }
/*     */ 
/*     */   public String getQueryString() {
/*  66 */     return null;
/*     */   }
/*     */ 
/*     */   public String getRemoteUser() {
/*  70 */     return null;
/*     */   }
/*     */ 
/*     */   public String getRequestURI() {
/*  74 */     return null;
/*     */   }
/*     */ 
/*     */   public StringBuffer getRequestURL() {
/*  78 */     return null;
/*     */   }
/*     */ 
/*     */   public String getRequestedSessionId() {
/*  82 */     return null;
/*     */   }
/*     */ 
/*     */   public String getServletPath() {
/*  86 */     return null;
/*     */   }
/*     */ 
/*     */   public HttpSession getSession() {
/*  90 */     return null;
/*     */   }
/*     */ 
/*     */   public HttpSession getSession(boolean arg0) {
/*  94 */     return null;
/*     */   }
/*     */ 
/*     */   public Principal getUserPrincipal() {
/*  98 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean isRequestedSessionIdFromCookie() {
/* 102 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isRequestedSessionIdFromURL() {
/* 106 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isRequestedSessionIdFromUrl() {
/* 110 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isRequestedSessionIdValid() {
/* 114 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isUserInRole(String arg0) {
/* 118 */     return false;
/*     */   }
/*     */ 
/*     */   public Object getAttribute(String arg0) {
/* 122 */     return this.hashMap.get(arg0);
/*     */   }
/*     */ 
/*     */   public Enumeration<?> getAttributeNames() {
/* 126 */     return null;
/*     */   }
/*     */ 
/*     */   public String getCharacterEncoding() {
/* 130 */     return null;
/*     */   }
/*     */ 
/*     */   public int getContentLength() {
/* 134 */     return 0;
/*     */   }
/*     */ 
/*     */   public String getContentType() {
/* 138 */     return null;
/*     */   }
/*     */ 
/*     */   public ServletInputStream getInputStream() throws IOException {
/* 142 */     return null;
/*     */   }
/*     */ 
/*     */   public String getLocalAddr() {
/* 146 */     return null;
/*     */   }
/*     */ 
/*     */   public String getLocalName() {
/* 150 */     return null;
/*     */   }
/*     */ 
/*     */   public int getLocalPort() {
/* 154 */     return 0;
/*     */   }
/*     */ 
/*     */   public Locale getLocale() {
/* 158 */     return null;
/*     */   }
/*     */ 
/*     */   public Enumeration<?> getLocales() {
/* 162 */     return null;
/*     */   }
/*     */ 
/*     */   public String getParameter(String arg0) {
/* 166 */     return null;
/*     */   }
/*     */ 
/*     */   public Map<?, ?> getParameterMap() {
/* 170 */     return null;
/*     */   }
/*     */ 
/*     */   public Enumeration<?> getParameterNames() {
/* 174 */     return null;
/*     */   }
/*     */ 
/*     */   public String[] getParameterValues(String arg0) {
/* 178 */     return null;
/*     */   }
/*     */ 
/*     */   public String getProtocol() {
/* 182 */     return null;
/*     */   }
/*     */ 
/*     */   public BufferedReader getReader() throws IOException {
/* 186 */     return null;
/*     */   }
/*     */ 
/*     */   public String getRealPath(String arg0) {
/* 190 */     return null;
/*     */   }
/*     */ 
/*     */   public String getRemoteAddr() {
/* 194 */     return null;
/*     */   }
/*     */ 
/*     */   public String getRemoteHost() {
/* 198 */     return null;
/*     */   }
/*     */ 
/*     */   public int getRemotePort() {
/* 202 */     return 0;
/*     */   }
/*     */ 
/*     */   public RequestDispatcher getRequestDispatcher(String arg0) {
/* 206 */     return null;
/*     */   }
/*     */ 
/*     */   public String getScheme() {
/* 210 */     return null;
/*     */   }
/*     */ 
/*     */   public String getServerName() {
/* 214 */     return null;
/*     */   }
/*     */ 
/*     */   public int getServerPort() {
/* 218 */     return 0;
/*     */   }
/*     */ 
/*     */   public boolean isSecure() {
/* 222 */     return false;
/*     */   }
/*     */ 
/*     */   public void removeAttribute(String arg0) {
/* 226 */     this.hashMap.remove(arg0);
/*     */   }
/*     */ 
/*     */   public void setAttribute(String arg0, Object arg1) {
/* 230 */     this.hashMap.put(arg0, arg1);
/*     */   }
/*     */ 
/*     */   public void setCharacterEncoding(String arg0)
/*     */     throws UnsupportedEncodingException
/*     */   {
/*     */   }
/*     */ }

/* Location:           Z:\EXT\WebBuilderServer (1)\WEB-INF\lib\webbuilder2.jar
 * Qualified Name:     com.webbuilder.common.UserRequest
 * JD-Core Version:    0.6.0
 */