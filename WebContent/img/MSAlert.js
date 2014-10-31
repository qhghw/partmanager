//Modified By Cj, 2000/8/28
//All rights reserved. 

function GetRootDir(){
	// Modified By Wesley, 2000/12/7
	// modified by wesley, 2001/06/26 改正了对于 http://servername/index.asp 的问题
	var strLocation, strVirtualDirName;
	var intStartPos, intEndPos;
	strLocation = top.window.location.href;
	// 形式为 http://ServerName/VirtualDirName/xxx/xxx.asp
	// 特别是对于弹出窗口，top.window.location.href的值肯定不是http://ServerName/VirtualDirName/index.asp
	// 所以不能从后往前取
	// 去掉http:
	intStartPos = strLocation.indexOf("//");
	if (intStartPos == -1)	// 不包含//，只好返回"/"
		return "/";		// modified by wesley, 2001/06/26
	// 去掉//ServerName
	intStartPos = strLocation.indexOf("/", intStartPos + 2);
	if (intStartPos == -1)	// 不包含/，只好返回"/"
		return "/";		// modified by wesley, 2001/06/26

	// 得到VirtualDirName
	intEndPos = strLocation.indexOf("/", intStartPos + 1);
	if (intEndPos == -1){
		// 后面没有了，所以目前拿到的就是VirtualDirName
		return "/";	// modified by wesley, 2001/06/26
	}
	// 找到了VirtualDirName后面的那个 /
	// modified by wesley, 2001/06/26
	return strLocation.substr(intStartPos, intEndPos - intStartPos + 1);
}
function MWalert(intType,strMessage){
		var strTemp;
		var strVersion;
		var varReturn;
		strVersion = navigator.appVersion;
		strTemp = GetRootDir() + "MsDialog/alertwindow.asp?intType=" + intType +"&strMessage="+strMessage + "&Rnd=" + Math.random();
		if(strVersion.indexOf("MSIE 5")!=0 && strVersion.indexOf("MSIE 5")!=-1)
			varReturn = top.window.showModalDialog(strTemp,"Dialog Arguments Value","dialogHeight: 200px; dialogWidth: 300px; center: Yes; help: No; resizable: No; status: no;");
		else{
			varReturn = top.window.showModalDialog(strTemp,"Dialog Arguments Value","dialogHeight: 200px; dialogWidth: 300px; center: Yes; help: No; resizable: no; status: no;");
		}
		return varReturn;
}