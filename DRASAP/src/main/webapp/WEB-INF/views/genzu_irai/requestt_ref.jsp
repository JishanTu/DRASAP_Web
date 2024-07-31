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
<html>
<head>
	<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
		<title>Drawing Search and Print System [図面登録依頼詳細]</title>
		<meta http-equiv="Pragma" content="no-cache" />
		<meta http-equiv="Cache-Control" content="no-cache" />
	<script type="text/javascript">
		browserName = navigator.appName;
		var WO1;
		var w = screen.availWidth;
		var h = screen.availHeight;
		var xPos = (screen.availWidth- w)/2;
		var yPos = (screen.availHeight - h)/2;
		window.resizeTo(w, h);
		window.moveTo(xPos,yPos);//画面の位置指定
		if (browserName != "Netscape") focus();
	</script>
</head>
<frameset rows="35,*" framespacing="0" border="0">
	<frame name="ref_head" src="<%=request.getContextPath() %>/switch.do?page=/genzu_irai/requestt_ref_head.jsp" />
	<frame name="ref_body" src="<%=request.getContextPath() %>/req_ref.do" />
</frameset>
</html>