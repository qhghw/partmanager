<%@ page contentType="text/html; charset=UTF-8" %>
<%
	session.removeAttribute("userInfo");
%>
<HTML>
<HEAD>
<TITLE>高新热力设备管理系统</TITLE>
<STYLE type=text/css>
BODY {
	BACKGROUND-COLOR: #3068b5
}
</STYLE>
<LINK href="img/css.css" type=text/css rel=stylesheet>
<script type="text/javascript">
		function check(){
			var logincode = document.getElementById("logincode").value;
			var password = document.getElementById("password").value;
			var error = document.getElementById("error");
			error.innerHTML="";
			if(logincode==""){
				error.innerHTML="用户名不能为空！";
				return false;
			}
			if(password==""){
				error.innerHTML="密码不能为空！";
				return false;
			}
			return true;
		}
	</script>
	<style type="text/css">
		* { margin:0 auto; padding:0; border:0;font-size:12px;}
		body { background:#0462A5; font:12px "宋体"; color:#004C7E;}
		input { border:1px solid #004C7E;}
		.main { background:url(img/login/bg.jpg) repeat-x;}
		.login { background:#DDF1FE; width:468px; height:262px; border:1px solid #000;}
		.top { background:url(img/login/login_bg.jpg) repeat-x; width:464px; height:113px; border:1px solid #2376B1; margin-top:1px;}
		.logo { background:url(img/login/logo.gif) no-repeat; width:150px; height:42px; float:left; margin:29px 0 0 14px;}
		.lable { background:url(img/login/lable.gif) no-repeat; width:157px; height:32px; float:right; margin:81px 31px 0 0;}
		.submit { background:url(img/login/submit.gif) no-repeat; cursor:hand; width:71px; height:24px; border:0;} 
		.reset { background:url(img/login/reset.gif) no-repeat; cursor:hand; width:71px; height:24px; border:0;} 
	</style>
</HEAD>
<BODY text=#000000 bgColor=#3068b5 leftMargin=0 topMargin=0 onload=fly(); 
marginwidth="0" marginheight="0">
<DIV align=center></DIV>
<TABLE height="100%" cellSpacing=0 cellPadding=0 width="100%" border=0>
  <TBODY>
    <TR>
      <TD align=middle bgColor=#3068b5>
        
        <TABLE cellSpacing=0 cellPadding=0 width="100%" border=0>
          <TBODY>
            <TR>
              <TD align=middle background=img/bg.jpg>
                <TABLE cellSpacing=0 cellPadding=0 width=504 border=0>
                  <TBODY>
                    <TR>
                      <TD background=img/homeTop.gif height=82><IMG height=82 
                  src="img/shim.gif" width=5></TD>
                    </TR>
                    <TR>
                      <TD background=img/main.jpg height=328><IMG height=330 
                  src="img/shim.gif" width=5></TD>
                    </TR>
                    <TR>
                      <TD vAlign=bottom background=img/homeBottom.gif 
                  height=81>
                         <form action="user_login.do" method="post">
             <TABLE height=60 cellSpacing=0 cellPadding=3 width=800 
                  align=center border=0>
                <tr> 
                  <td align="center" height="20" width="250"></td>
                  <td align="right" height="20" style="width: 80px; height: 20px">用户名:</td>
                  <td align="right" width="161" style="width: 106px; ">
                    <input type="text" name="logincode" style="width: 101px;height:20px" />
                  </td>  
                  <td align="center" height="20">
                    <input name="submit" type="submit" value="" onclick="return check()" class="submit" />
                  </td>
                  <td align="center" height="20" width="200"><font id="error" color="red">&nbsp;</font></td>
                </tr>
                <tr>
                  <td align="center" height="20" width="250"> </td>
                  <td align="right" height="20" style="width: 80px; ">密&nbsp;&nbsp;码:</td>
                  <td align="right" width="161" style="width: 106px; ">
                    <input type="password" name="password" style="width:101px;height:20px" />
                  </td>
                  <td align="center" height="20">
                    <input name="reset" type="reset" value="" class="reset" />
                  </td>
                    <td align="center" height="20" width="200"><font id="error" color="red">${error}&nbsp;</font></td>
                </tr>
              </table>
	        </form>
                      </TD>
                    </TR>
                  </TBODY>
                </TABLE>
              </TD>
            </TR>
          </TBODY>
        </TABLE>
      </TD>
    </TR>
  </TBODY>
</TABLE>
</BODY>
</HTML>