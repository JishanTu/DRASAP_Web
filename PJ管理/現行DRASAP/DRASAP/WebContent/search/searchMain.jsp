<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%-- ログイン情報の確認 --%>
<logic:notPresent name="user" scope="session">
	<logic:redirect forward="timeout" />
</logic:notPresent>
<html:html>
<head>
	<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<title>Drawing Search and Print System [図面検索]</title>
	<script type="text/javascript">
	<!--
		browserName = navigator.appName;
		var WO1;
		var w = screen.availWidth;
		var h = screen.availHeight;
		var xPos = (screen.availWidth- w)/2;
		var yPos = (screen.availHeight - h)/2;
		window.resizeTo(w, h);
		window.name = "_drasap_search"
		window.moveTo(xPos,yPos);//画面の位置指定
//		if (browserName != "Netscape") focus();
		document.onkeydown = keys;
		function keys(){
			switch (event.keyCode ){
				case 116: // F5
					event.keyCode = 0;
					return false;
					break;
			}
		}
	//-->
	</script>
		<script type="text/javascript">
	<!--
		// IEの場合、Broadcastはエラーになるので、Edgeのみ処理
		const agent = window.navigator.userAgent.toLowerCase();
		if(agent.indexOf('chrome') !== -1)
		{
			var IP;
			var xhr = new XMLHttpRequest();
			xhr.open('GET', '/DRASAP/getip');
			xhr.send();

			xhr.onreadystatechange = function()
			{
				if(xhr.readyState === 4 && xhr.status === 200)
				{
					IP = xhr.responseText;
				}
			}

			var broadcast = new BroadcastChannel("DRASAP_LOGIN");
			broadcast.addEventListener('message',({data})=>{
				if(data === 'newlogin_' +IP)
				{
					var login = window.open('about:blank','_self');
					login.close();
				 }
   			});

		}
	//-->
	</script>
</head>
<!-- FRAMESET rows="155,*" -->
<frameset rows="180,*">
<%	// リクエストパラメータを確認し task=continueであれば、
	// 検索条件や検索結果を元のまま、画面復帰する
	if("continue".equals(request.getParameter("task"))){ %>
	<frame name="condition" src="switch.do?prefix=/search&amp;page=/searchCondition.jsp" scrolling="no" />
	<frame name="result" src="switch.do?prefix=/search&amp;page=/searchResult.jsp" scrolling="yes" />
<% } else if("clear_result".equals(request.getParameter("task"))){
	// リクエストパラメータを確認し task=clear_resultであれば、
	// 検索条件はそのまま、検索結果をクリアして、画面復帰する %>
	<frame name="condition" src="switch.do?prefix=/search&amp;page=/searchCondition.jsp" scrolling="no" />
	<frame name="result" src="switch.do?prefix=/search&amp;page=/resultPre.do?task=init" scrolling="yes" />
<% } else if("changeLanguage".equals(request.getParameter("task"))){
	// リクエストパラメータを確認し task=clear_resultであれば、
	// 検索条件はそのまま、検索結果をクリアして、画面復帰する %>
	<frame name="condition" src="switch.do?prefix=/search&amp;page=/searchCondition.jsp" scrolling="no" />
	<frame name="result" src="switch.do?prefix=/search&amp;page=/resultPre.do?task=language" scrolling="yes" />
<% } else if("multipreview".equals(request.getParameter("task"))){
	// リクエストパラメータを確認し task=clear_resultであれば、
	// 検索条件はそのまま、検索結果をクリアして、画面復帰する %>
	<frame name="condition" src="switch.do?prefix=/search&amp;page=/searchCondition.do?act=multipreview" scrolling="no" />
	<frame name="result" />

<% } else {
	// それ以外の場合は、検索条件や検索結果を初期化する%>
	<frame name="condition" src="switch.do?prefix=/search&amp;page=/searchCondition.do" scrolling="no" />
	<frame name="result" src="switch.do?prefix=/search&amp;page=/resultPre.do?task=init" scrolling="yes" />
<% } %>
</frameset>
</html:html>
