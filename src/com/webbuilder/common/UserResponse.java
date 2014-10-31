/*     */ package com.webbuilder.common;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.util.Locale;
/*     */ import javax.servlet.ServletOutputStream;
/*     */ import javax.servlet.http.Cookie;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ 
/*     */ public class UserResponse
/*     */   implements HttpServletResponse
/*     */ {
/*  13 */   private StringWriter writer = new StringWriter();
/*  14 */   private PrintWriter printWriter = new PrintWriter(this.writer, true);
/*     */ 
/*     */   public void addCookie(Cookie arg0) {
/*     */   }
/*     */ 
/*     */   public void addDateHeader(String arg0, long arg1) {
/*     */   }
/*     */ 
/*     */   public void addHeader(String arg0, String arg1) {
/*     */   }
/*     */ 
/*     */   public void addIntHeader(String arg0, int arg1) {
/*     */   }
/*     */ 
/*     */   public boolean containsHeader(String arg0) {
/*  29 */     return false;
/*     */   }
/*     */ 
/*     */   public String encodeRedirectURL(String arg0) {
/*  33 */     return null;
/*     */   }
/*     */ 
/*     */   public String encodeRedirectUrl(String arg0) {
/*  37 */     return null;
/*     */   }
/*     */ 
/*     */   public String encodeURL(String arg0) {
/*  41 */     return null;
/*     */   }
/*     */ 
/*     */   public String encodeUrl(String arg0) {
/*  45 */     return null;
/*     */   }
/*     */ 
/*     */   public void sendError(int arg0) throws IOException {
/*     */   }
/*     */ 
/*     */   public void sendError(int arg0, String arg1) throws IOException {
/*     */   }
/*     */ 
/*     */   public void sendRedirect(String arg0) throws IOException {
/*     */   }
/*     */ 
/*     */   public void setDateHeader(String arg0, long arg1) {
/*     */   }
/*     */ 
/*     */   public void setHeader(String arg0, String arg1) {
/*     */   }
/*     */ 
/*     */   public void setIntHeader(String arg0, int arg1) {
/*     */   }
/*     */ 
/*     */   public void setStatus(int arg0) {
/*     */   }
/*     */ 
/*     */   public void setStatus(int arg0, String arg1) {
/*     */   }
/*     */ 
/*     */   public void flushBuffer() throws IOException {
/*     */   }
/*     */ 
/*     */   public int getBufferSize() {
/*  76 */     return 0;
/*     */   }
/*     */ 
/*     */   public String getCharacterEncoding() {
/*  80 */     return null;
/*     */   }
/*     */ 
/*     */   public String getContentType() {
/*  84 */     return null;
/*     */   }
/*     */ 
/*     */   public Locale getLocale() {
/*  88 */     return null;
/*     */   }
/*     */ 
/*     */   public ServletOutputStream getOutputStream() throws IOException {
/*  92 */     return null;
/*     */   }
/*     */ 
/*     */   public PrintWriter getWriter() throws IOException {
/*  96 */     return this.printWriter;
/*     */   }
/*     */ 
/*     */   public String getOutputString() {
/* 100 */     return this.writer.toString();
/*     */   }
/*     */ 
/*     */   public boolean isCommitted() {
/* 104 */     return false;
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void resetBuffer()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setBufferSize(int arg0)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setCharacterEncoding(String arg0)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setContentLength(int arg0)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setContentType(String arg0)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setLocale(Locale arg0)
/*     */   {
/*     */   }
/*     */ }

/* Location:           Z:\EXT\WebBuilderServer (1)\WEB-INF\lib\webbuilder2.jar
 * Qualified Name:     com.webbuilder.common.UserResponse
 * JD-Core Version:    0.6.0
 */