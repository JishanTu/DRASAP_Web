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
</head>
<body marginwidth="0" marginheight="0" bgcolor="#ffffff" rightmargin="0" topmargin="0" leftmargin="0" bottommargin="0">
削除図面
</body>
</html>
