//Modified By Cj, 2000/8/28
//All rights reserved. 

function GetRootDir(){
	// Modified By Wesley, 2000/12/7
	// modified by wesley, 2001/06/26 �����˶��� http://servername/index.asp ������
	var strLocation, strVirtualDirName;
	var intStartPos, intEndPos;
	strLocation = top.window.location.href;
	// ��ʽΪ http://ServerName/VirtualDirName/xxx/xxx.asp
	// �ر��Ƕ��ڵ������ڣ�top.window.location.href��ֵ�϶�����http://ServerName/VirtualDirName/index.asp
	// ���Բ��ܴӺ���ǰȡ
	// ȥ��http:
	intStartPos = strLocation.indexOf("//");
	if (intStartPos == -1)	// ������//��ֻ�÷���"/"
		return "/";		// modified by wesley, 2001/06/26
	// ȥ��//ServerName
	intStartPos = strLocation.indexOf("/", intStartPos + 2);
	if (intStartPos == -1)	// ������/��ֻ�÷���"/"
		return "/";		// modified by wesley, 2001/06/26

	// �õ�VirtualDirName
	intEndPos = strLocation.indexOf("/", intStartPos + 1);
	if (intEndPos == -1){
		// ����û���ˣ�����Ŀǰ�õ��ľ���VirtualDirName
		return "/";	// modified by wesley, 2001/06/26
	}
	// �ҵ���VirtualDirName������Ǹ� /
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