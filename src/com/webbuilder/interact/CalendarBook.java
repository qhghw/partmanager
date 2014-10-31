/*     */ package com.webbuilder.interact;
/*     */ 
/*     */ import com.webbuilder.utils.DateUtil;
/*     */ import com.webbuilder.utils.LunarCalendar;
/*     */ import com.webbuilder.utils.StringUtil;
/*     */ import java.io.PrintWriter;
/*     */ import java.sql.ResultSet;
/*     */ import java.util.HashMap;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ 
/*     */ public class CalendarBook
/*     */ {
/*     */   private String[] festival1;
/*     */   private String[] festival2;
/*     */   private String[] festival3;
/*     */ 
/*     */   public CalendarBook()
/*     */   {
/*  13 */     this.festival1 = 
/*  14 */       new String[] { "0101<font color='#FF0000'>元旦</font>", "0202世界湿地日", "0210国际气象节", "0214<font color='#800040'>情人节</font>", "0221国际母语日", "0303爱耳日", "0305中国青年志愿者服务日", "0308妇女节", "0312植树节", "0314国际警察日", "0315<font color='#800040'>消费者权益日</font>", "0318全国科技人才活动日", "0321世界森林日、世界睡眠日", "0322世界水日", "0323世界气象日", "0401愚人节", "0407世界卫生日", "0421全国企业家活动日", "0422世界地球日、世界法律日", "0423世界图书和版权日", "0426世界知识产权日", "0430全国交通安全反思日", "0501<font color='#FF0000'>国际劳动节</font>", "0503世界新闻自由日", "0504青年节", "0508世界红十字日、世界微笑日", "0512国际护士节", "0515国际家庭日", "0517世界电信日", "0518国际博物馆日", "0520全国学生营养日", "0522生物多样性国际日", "0531世界无烟日", "0601儿童节", "0605世界环境日", "0606全国爱眼日", "0611中国人口日", "0617防治荒漠化和干旱日", "0620世界难民日", "0622中国儿童慈善活动日", "0623国际奥林匹克日", "0625全国土地日", "0626国际禁毒日、国际宪章日", "0701建党节、香港回归纪念、国际建筑日", "0707中国人民抗日战争纪念日", "0711世界人口日、中国航海节", "0726世界语创立日", "0801建军节", "0806国际电影节", "0808全民健身日、爸爸节", "0812国际青年日", "0826全国律师咨询日", "0901全国中小学开学日", "0903中国抗日战争胜利纪念日", "0908国际新闻工作者日、国际扫盲日", "0910<font color='#800040'>教师节</font>", "0914世界清洁地球日", "0916国际臭氧层保护日", "0918九·一八事变纪念日", "0920国际爱牙日", "0921国际和平日", "0927世界旅游日", "1001<font color='#FF0000'>国庆节</font>、国际音乐日、国际老人节", "1004世界动物日", "1005世界教师日", "1008全国高血压日、世界视觉日", "1009世界邮政日", "1010辛亥革命纪念日、世界精神卫生日", "1011世界镇痛日", "1014世界标准日", "1015国际盲人节", "1016世界粮食日", "1017世界消除贫困日", "1022世界传统医药日", "1024联合国日、世界发展宣传日", "1031世界勤俭日", "1108中国记者日", "1109消防宣传日", "1114世界糖尿病日", "1116国际宽容日", "1117国际大学生节", "1121世界电视日、世界问候日", "1125消除对妇女的暴力行为国际日", "1201世界艾滋病日", "1203世界残疾人日", "1204中国法制宣传日", "1205世界强化免疫日", "1207国际民航日", "1209国际反腐败日、世界足球日", "1210世界人权日", "1213南京大屠杀纪念日", "1220澳门回归纪念", "1221国际篮球日", "1224平安夜", "1225<font color='#800040'>圣诞节</font>", "1229国际生物多样性日" }; this.festival2 = new String[] { "0101<font color='#FF0000'>春节</font>", "0115<font color='#800040'>元宵节</font>", "0505<font color='#FF0000'>端午节</font>", "0707七夕情人节", "0715中元节", "0815<font color='#FF0000'>中秋节</font>", "0909重阳节", "1208腊八节", "1223小年" }; this.festival3 = new String[] { "0521<font color='#800040'>国际母亲节</font>", "0531全国助残日", "0631<font color='#800040'>国际父亲节</font>", "0717国际合作节", "0933国际和平日", "0941国际聋人节", "1012国际住房日", "1024国际减轻自然灾害日", "1025世界视觉日", "1145<font color='#800040'>感恩节</font>" };
/*     */   }
/*     */ 
/*     */   public void getBook(HttpServletRequest request, HttpServletResponse response)
/*     */     throws Exception
/*     */   {
/*  54 */     String nowDate = (String)request.getAttribute("sys.nowdate");
/*  55 */     StringBuilder buf = new StringBuilder();
/*  56 */     String[] weekday = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };
/*  57 */     String[] dayColor1 = { "#FFEEEE", "#EEFFEE", "#EEEEFF", "#EEFFEE", 
/*  58 */       "#EEEEFF", "#EEFFEE", "#FFEEEE" };
/*  59 */     String[] dayColor2 = { "#EEDDDD", "#DDEEDD", "#DDDDEE", "#DDEEDD", 
/*  60 */       "#DDDDEE", "#DDEEDD", "#EEDDDD" };
/*  61 */     String dt = request.getParameter("calDate");
/*  62 */     int ht = Integer.parseInt(request.getParameter("bookHeight")) - 60;
/*  63 */     LunarCalendar c = new LunarCalendar(DateUtil.stringToStdDate(dt)); LunarCalendar curDate = new LunarCalendar();
/*  64 */     buf.append("<div style='line-height:22px;'><p><center>");
/*  65 */     buf.append(c.getSimpleGregorianDateString()).append(" ").append(
/*  66 */       c.getChinese(7)).append(" 农历").append(
/*  67 */       c.getChineseDateString()).append(" ").append(
/*  68 */       c.getChinese(808)).append("年 ");
/*  69 */     buf.append("</center></p></div>");
/*  70 */     int t1 = c.get(804);
/*  71 */     int t2 = c
/*  71 */       .get(805);
/*  72 */     String ts1 = c.getChinese(804);
/*  73 */     String ts2 = c
/*  73 */       .getChinese(805);
/*  74 */     c.set(5, 1);
/*  75 */     int j = c.getActualMaximum(5); int w = 0;
/*  76 */     int l = c.get(7);
/*  77 */     boolean newRow = false;
/*  78 */     HashMap map = new HashMap();
/*  79 */     ResultSet rs = (ResultSet)request.getAttribute("query");
/*  80 */     while (rs.next())
/*  81 */       map
/*  82 */         .put(DateUtil.formatStdDate(rs.getTimestamp(1)), rs
/*  83 */         .getString(2));
/*     */     int rows;

/*  86 */     if ((l == 1) && (j == 28)) {
/*  87 */       rows = 4;
/*     */     }
/*     */     else
/*     */     {

/*  88 */       if (j + l - 7 > 29)
/*  89 */         rows = 6;
/*     */       else
/*  91 */         rows = 5; 
/*     */     }
/*  92 */     ht /= rows;
/*  93 */     if (ht < 50)
/*  94 */       ht = 50;
/*  95 */     String hts = Integer.toString(ht);
/*  96 */     buf
/*  97 */       .append("<table class='sys_normal' style='cursor:pointer;line-height:18px;' id='bookTable' width='100%' border='0'><tr height='20px'>");
/*  98 */     for (int i = 0; i < 7; ++i) {
/*  99 */       buf.append("<td width='14%' class='font_gray' align='center'");
/* 100 */       buf.append(" style='background-color:" + dayColor2[i] + "'");
/* 101 */       buf.append(">");
/* 102 */       buf.append(weekday[i]);
/* 103 */       buf.append("</td>");
/*     */     }
/* 105 */     buf.append("</tr><tr>");
/* 106 */     boolean afterDay = c.compareTo(curDate) < 0;
/* 107 */     for (int k = 1; k < l; ++k)
/*     */     {
/*     */       String dayColor;

/* 108 */       if (afterDay)
/* 109 */         dayColor = dayColor2[w];
/*     */       else
/* 111 */         dayColor = dayColor1[w];
/* 112 */       if (k == 1)
/* 113 */         buf.append("<td height='" + hts + 
/* 114 */           "px' style='background-color:" + dayColor + 
/* 115 */           "'>&nbsp;</td>");
/*     */       else
/* 117 */         buf.append("<td style='background-color:" + dayColor + 
/* 118 */           "'>&nbsp;</td>");
/* 119 */       ++w;
/*     */     }
/* 121 */     for ( int i = 0; i < j; ++i) {
/* 122 */       ++w;
/* 123 */       c.set(5, i + 1);
/* 124 */       buf.append("<td");
/* 125 */       if (w == 1)
/* 126 */         buf.append(" height=\"" + hts + "px\"");
/* 127 */       String dateVal = DateUtil.formatStdDate(c.getTime());
/* 128 */       String content = (String)map.get(dateVal);
/* 129 */       if (StringUtil.isEqual(dateVal, nowDate))
/* 130 */         buf.append(" style=\"background-color:#FFFF80\"");
/* 131 */       else if (c.compareTo(curDate) > 0)
/* 132 */         buf.append(" style=\"background-color:" + dayColor1[(w - 1)] + 
/* 133 */           "\"");
/*     */       else
/* 135 */         buf.append(" style=\"background-color:" + dayColor2[(w - 1)] + 
/* 136 */           "\"");
/* 137 */       buf.append(" dateCell=\"1\" calDate=\"" + dateVal + "\"");
/* 138 */       if (content != null) {
/* 139 */         buf.append(" calMemo=\"");
/* 140 */         buf.append(StringUtil.toHTML(content));
/* 141 */         buf.append("\"");
/*     */       } else {
/* 143 */         buf.append(" calMemo=\"\"");
/* 144 */       }buf.append("><p class='font_gray'>");
/* 145 */       buf.append(Integer.toString(i + 1));
/* 146 */       buf.append(" ");
/* 147 */       buf.append(c.getChinese(803));
/* 148 */       if (i + 1 == t1) {
/* 149 */         buf.append(" ");
/* 150 */         if (ts1.equals("清明")) {
/* 151 */           buf.append("<font color='#FF0000'>");
/* 152 */           buf.append(ts1);
/* 153 */           buf.append("</font>");
/*     */         } else {
/* 155 */           buf.append("<font color='#0000FF'>");
/* 156 */           buf.append(ts1);
/* 157 */           buf.append("</font>");
/*     */         }
/*     */       }
/* 160 */       if (i + 1 == t2) {
/* 161 */         buf.append(" ");
/* 162 */         if (ts2.equals("清明")) {
/* 163 */           buf.append("<font color='#FF0000'>");
/* 164 */           buf.append(ts2);
/* 165 */           buf.append("</font>");
/*     */         } else {
/* 167 */           buf.append("<font color='#0000FF'>");
/* 168 */           buf.append(ts2);
/* 169 */           buf.append("</font>");
/*     */         }
/*     */       }
/* 172 */       buf.append(getFestival(c));
/* 173 */       buf.append("</p>");
/* 174 */       if ((content != null) && (!StringUtil.isSame(content, "<p>&nbsp;</p>")) && 
/* 175 */         (!StringUtil.isSame(content, "<br>")) && 
/* 176 */         (!StringUtil.isSame(content, "&nbsp;")) && 
/* 177 */         (!StringUtil.isSame(content, "<p><br></p>")))
/* 178 */         buf.append(content);
/* 179 */       buf.append("</td>");
/* 180 */       if (w == 7) {
/* 181 */         w = 0;
/* 182 */         if (i == j - 1)
/* 183 */           buf.append("</tr>");
/*     */         else
/* 185 */           buf.append("</tr><tr>");
/*     */       }
/*     */     }
/* 188 */     afterDay = (c.compareTo(curDate) > 0) || 
/* 189 */       (StringUtil.isEqual(DateUtil.formatStdDate(c.getTime()), 
/* 190 */       nowDate));
/* 191 */     l = c.get(7);
/* 192 */     for (int k = l; k < 7; ++k) {
/* 193 */       newRow = true;
/* 194 */       if (afterDay)
/* 195 */         buf.append("<td style='background-color:" + dayColor1[k] + "'");
/*     */       else
/* 197 */         buf.append("<td style='background-color:" + dayColor2[k] + "'");
/* 198 */       buf.append(">&nbsp;</td>");
/*     */     }
/* 200 */     if (newRow)
/* 201 */       buf.append("</tr>");
/* 202 */     buf.append("</tr></table>");
/* 203 */     response.getWriter().print(buf.toString());
/*     */   }
/*     */ 
/*     */   private String getFestival(LunarCalendar dt) {
/* 207 */     LunarCalendar c = new LunarCalendar(dt);
/* 208 */     StringBuilder buf = new StringBuilder();
/*     */ 
/* 210 */     String f1 = DateUtil.formatDate(c.getTime(), "MMdd");
/* 211 */     String f2 = 
/* 212 */       StringUtil.formatFloat(c.get(802), 
/* 212 */       "00") + 
/* 213 */       StringUtil.formatFloat(c.get(803), 
/* 214 */       "00");
/* 215 */     String f3 = DateUtil.formatDate(c.getTime(), "MM") + 
/* 216 */       c.get(4) + 
/* 217 */       c.get(7);
/* 218 */     int j = this.festival1.length;
/* 219 */     for (int i = 0; i < j; ++i) {
/* 220 */       if (this.festival1[i].substring(0, 4).equals(f1)) {
/* 221 */         buf.append(this.festival1[i].substring(4));
/* 222 */         break;
/*     */       }
/*     */     }
/* 225 */     j = this.festival2.length;
/* 226 */     for (int  i = 0; i < j; ++i) {
/* 227 */       if (this.festival2[i].substring(0, 4).equals(f2)) {
/* 228 */         if (buf.length() != 0)
/* 229 */           buf.append("、");
/* 230 */         buf.append(this.festival2[i].substring(4));
/* 231 */         break;
/*     */       }
/*     */     }
/* 234 */     j = this.festival3.length;
/* 235 */     for (int i = 0; i < j; ++i) {
/* 236 */       if (this.festival3[i].substring(0, 4).equals(f3)) {
/* 237 */         if (buf.length() != 0)
/* 238 */           buf.append("、");
/* 239 */         buf.append(this.festival3[i].substring(4));
/* 240 */         break;
/*     */       }
/*     */     }
/* 243 */     int y = c.get(801);
/* 244 */     c.add(803, 1);
/* 245 */     if (c.get(801) > y) {
/* 246 */       if (buf.length() != 0)
/* 247 */         buf.append("、");
/* 248 */       buf.append("<font color='#FF0000'>除夕</font>");
/*     */     }
/* 250 */     if (buf.length() == 0) {
/* 251 */       return "";
/*     */     }
/* 253 */     return " " + buf.toString();
/*     */   }
/*     */ }

/* Location:           Z:\EXT\WebBuilderServer (2)\WEB-INF\lib\webbuilder2.jar
 * Qualified Name:     com.webbuilder.interact.CalendarBook
 * JD-Core Version:    0.5.4
 */