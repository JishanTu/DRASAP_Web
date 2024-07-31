<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page isELIgnored="false"%>

<%-- ログイン情報の確認 --%>
<c:if test="${empty sessionScope.user}">
<script>
		location.replace('<%=request.getContextPath()%>/timeout');
</script>
</c:if>

<%-- アクセスレベル変更許可フラグがnullの場合、アクセス禁止 --%>
<c:if test="${not empty sessionScope.aclBatchUpdateFlag}">
	<script>
		location.replace('<%=request.getContextPath()%>/accessLevelBatchUpdate');
	</script>
</c:if>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
<title>Drawing Search and Print System [アクセスレベル一括更新]</title>
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Cache-Control" content="no-cache" />
<script type="text/javascript">
	browserName = navigator.appName;
	var WO1;
	var w = screen.availWidth;
	var h = screen.availHeight;
	var xPos = (screen.availWidth - w) / 2;
	var yPos = (screen.availHeight - h) / 2;
	window.resizeTo(w, h);
	window.moveTo(xPos, yPos);//画面の位置指定
	document.onkeydown = keys;
	function keys() {
		switch (event.keyCode) {
		case 116: // F5
			event.keyCode = 0;
			return false;
			break;
		}
	}
</script>
</head>
<frameset rows="35,*" framespacing="0" border="0">
	<%
	// リクエストパラメータを確認し、act=initであれば初期化する
	if (request.getParameter("act") == null || "init".equals(request.getParameter("act"))) {
	%>
		<frame name="acl_head" src="switch.do?page=/system/accessLevelBatchUpdateHead.jsp" scrolling="no" />
		<frame name="acl_body" src="accessLevelBatchUpdate.do?act=init" scrolling="yes" />
	<%
	} else {
	// それ以外の場合は、取得データを表示する
	%>
		<frame name="acl_head" src="switch.do?page=/system/accessLevelBatchUpdateHead.jsp" scrolling="no" />
		<frame name="acl_body" src="switch.do?page=/system/accessLevelBatchUpdateBody.jsp" scrolling="yes" />
	<%
	}
	%>
</frameset>
</html>
