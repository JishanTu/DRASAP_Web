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
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<title>アクセスレベル一括更新</title>
	<script type="text/javascript">
		document.onkeydown = keys;
		function keys() {
			switch (event.keyCode ) {
				case 116: // F5
					event.keyCode = 0;
					return false;
					break;
			}
		}
	</script>
</head>
<frameset rows="100,*" framespacing="0" border="0">
<frame name="acl_condition" src="switch.do?page=/system/accessLevelBatchUpdateCondition.jsp" />
<c:if test="${sessionScope.accessLevelBatchUpdateErrors != null}">
<frame name="acl_list" src="switch.do?page=/system/accessLevelBatchUpdate_error.jsp" />
</c:if>

<c:if test="${sessionScope.accessLevelBatchUpdateErrors == null}">
<frame name="acl_list" src="switch.do?page=/system/accessLevelBatchUpdateList.jsp" />
</c:if>

</frameset>
</html>
