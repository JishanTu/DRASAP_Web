<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%-- ログイン情報の確認 --%>
<c:if test="${empty sessionScope.user}">
<script>
	location.replace('<%=request.getContextPath()%>/timeout');
</script>
</c:if>

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<title>Drawing Search and Print System [アクセスレベル変更]</title>
	<script type="text/javascript">
		var browserName = navigator.appName;
		function onLoad() {
                    var w = screen.availWidth;
                    var h = screen.availHeight;
                    mx = 0;
                    my = 0;
                    window.resizeTo(w, h);
                    window.moveTo(mx,my);//画面の位置指定
                    if (browserName != "Netscape") focus();
		}
	</script>
</head>
<frameset rows="75,*,30" framespacing="0" border="0" onload="onLoad()">
<frame name="aclv_change_head" src="switch.do?page=/search/aclvChangeHead.jsp" />
<frame name="aclv_change_body" src="switch.do?page=/search/aclvChangeBody.jsp" />
<frame name="aclv_change_foot" src="switch.do?page=/search/aclvChangeFoot.jsp" />
</frameset>
</html>
