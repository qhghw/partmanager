/*    */ package com.webbuilder.common;
/*    */ 
/*    */ import com.webbuilder.utils.StringUtil;
/*    */ import java.io.File;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import org.dom4j.Attribute;
/*    */ import org.dom4j.Document;
/*    */ import org.dom4j.Element;
/*    */ import org.dom4j.io.SAXReader;
/*    */ 
/*    */ public class XMLData
/*    */ {
/*    */   public Document document;
/* 16 */   public static String[] buffer = null;
/* 17 */   public static long lastModified = 0L;
/*    */ 
/*    */   public XMLData(String path) throws Exception {
/* 20 */     SAXReader reader = new SAXReader();
/* 21 */     this.document = reader.read(new File(path + "webbuilder/system/main.xml"));
/* 22 */     this.document.setXMLEncoding("utf-8");
/*    */   }
/*    */ 
/*    */   public static String[] getBuffer(HttpServletRequest request, String webPath) throws Exception
/*    */   {
/* 27 */     long lastModi = new File(webPath + "webbuilder/system/main.xml")
/* 28 */       .lastModified();
/*    */ 
/* 30 */     if ((lastModi != lastModified) || (buffer == null)) {
/* 31 */       buffer = null;
/* 32 */       synchronized (XMLData.class) {
/* 33 */         if (buffer != null)
/* 34 */           return buffer;
/* 35 */         XMLData xml = new XMLData(webPath);
/* 36 */         xml.store(request, webPath);
/* 37 */         lastModified = new File(webPath + "webbuilder/system/main.xml")
/* 38 */           .lastModified();
/* 39 */         return buffer;
/*    */       }
/*    */     }
/* 42 */     return buffer;
/*    */   }
/*    */ 
/*    */   public void store(HttpServletRequest request, String webPath) throws Exception
/*    */   {
/* 47 */     Element element = (Element)this.document.selectSingleNode("/main/var");
/* 48 */     buffer = new String[element.elements().size() * 2];
/* 49 */     int i = 0;
/*    */ 
/* 51 */     Iterator iterator = element.elementIterator();
/* 52 */     while (iterator.hasNext()) {
/* 53 */       Element key = (Element)iterator.next();
/* 54 */       buffer[(i++)] = ("sys." + key.getName());
/* 55 */       buffer[i] = replaceParameters(buffer, key.attribute("value")
/* 56 */         .getText(), i);
/* 57 */       i++;
/*    */     }
/*    */   }
/*    */ 
/*    */   private String findString(String[] list, String string, int end)
/*    */   {
/* 64 */     for (int i = 0; i < end; i += 2) {
/* 65 */       if (list[i].equals(string))
/* 66 */         return list[(i + 1)];
/*    */     }
/* 68 */     return null;
/*    */   }
/*    */ 
/*    */   private String replaceParameters(String[] list, String text, int end) {
/* 72 */     String result = text;
/*    */     int startPos;
/*    */     int endPos;
/* 75 */     while (((startPos = result.indexOf("{#")) > -1) && 
/* 76 */       ((endPos = result.indexOf("#}", startPos)) > -1))
/*    */     {
/*    */    
/* 77 */       String paramName = result.substring(startPos + 2, endPos);
/* 78 */       String paramValue = findString(list, paramName, end);
/* 79 */       if (paramValue == null)
/* 80 */         paramValue = "";
/* 81 */       result = StringUtil.replace(result, "{#" + paramName + "#}", 
/* 82 */         paramValue);
/*    */     }
/* 84 */     return result;
/*    */   }
/*    */ }

/* Location:           Z:\EXT\WebBuilderServer (1)\WEB-INF\lib\webbuilder2.jar
 * Qualified Name:     com.webbuilder.common.XMLData
 * JD-Core Version:    0.6.0
 */