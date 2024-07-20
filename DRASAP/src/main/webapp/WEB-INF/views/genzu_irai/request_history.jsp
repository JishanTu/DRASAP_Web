<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>


<%-- ログイン情報の確認 --%>
<c:if test="${empty sessionScope.user}">
	<script>
		location.replace('<%=request.getContextPath() %>/timeout');
	</script>
</c:if>

<html>
	<head>
		<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
		<title>Drawing Search and Print System [図面登録依頼履歴]</title>
		<meta http-equiv="Pragma" content="no-cache" />
		<meta http-equiv="Cache-Control" content="no-cache" />
	</head>

<frameset rows="45,*" framespacing="0" border="0">
<frame name="list_head" src="<%=request.getContextPath() %>/switch.do?page=/genzu_irai/request_history_head.jsp" />
<frame name="list_body" src="<%=request.getContextPath() %>/switch.do?page=/genzu_irai/request_history_body.jsp" />
</frameset>
</html>
