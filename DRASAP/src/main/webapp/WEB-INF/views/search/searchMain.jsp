<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page isELIgnored="false"%>

<%-- ログイン情報の確認 --%>
<c:if test="${empty sessionScope.user}">
<script>
		location.replace('<%=request.getContextPath()%>/timeout');
</script>
</c:if>

<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<title>Drawing Search and Print System [図面検索]</title>
	<script type="text/javascript">
		browserName = navigator.appName;
		//var WO1;
		var w = screen.availWidth;
		var h = screen.availHeight;
		var xPos = (screen.availWidth - w)/2;
		var yPos = (screen.availHeight - h)/2;
		//window.resizeTo(w, h);
		window.name = "_drasap_search"
		window.moveTo(xPos,yPos);//画面の位置指定
		//if (browserName != "Netscape") focus();
		document.onkeydown = keys;
		function keys(){
			switch (event.keyCode ){
				case 116: // F5
					event.keyCode = 0;
					return false;
					break;
			}
		}
	</script>
	<script type="text/javascript">
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
    </script>
</head>
<!-- FRAMESET rows="155,*" -->
<frameset rows="180,*">
<%	// リクエストパラメータを確認し task=continueであれば、
	// 検索条件や検索結果を元のまま、画面復帰する
	if("continue".equals(request.getAttribute("task"))){ %>
	<frame name="condition" src="searchCondition.do?act=search" scrolling="no" />
	<frame name="result" src="switch.do?page=/search/searchResult.jsp" scrolling="yes" />
<% } else if("clear_result".equals(request.getAttribute("task"))){
	// リクエストパラメータを確認し task=clear_resultであれば、
	// 検索条件はそのまま、検索結果をクリアして、画面復帰する %>
	<frame name="condition" src="searchCondition.do?act=search" scrolling="no" />
	<frame name="result" src="resultPre.do?task=init" scrolling="yes" />
<% } else if("changeLanguage".equals(request.getAttribute("task"))){
	// リクエストパラメータを確認し task=clear_resultであれば、
	// 検索条件はそのまま、検索結果をクリアして、画面復帰する %>
	<frame name="condition" src="switch.do?page=/search/searchCondition.jsp" scrolling="no" />
	<frame name="result" src="resultPre.do?task=language" scrolling="yes" />
<% } else if("multipreview".equals(request.getAttribute("task"))){
	// リクエストパラメータを確認し task=clear_resultであれば、
	// 検索条件はそのまま、検索結果をクリアして、画面復帰する %>
	<frame name="condition" src="searchCondition.do?act=multipreview" scrolling="no" />
	<frame name="result" />
<% } else {
	// それ以外の場合は、検索条件や検索結果を初期化する%>
	<frame name="condition" src="searchCondition.do" scrolling="no" />
	<frame name="result" src="resultPre.do?task=init" scrolling="yes" />
<% } %>
</frameset>
</html>
