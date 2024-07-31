<%@ page contentType="text/html;charset=UTF-8" %>
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

<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<title>Drawing Search and Print System [アクセスレベル変更前確認]</title>
</head>
<frameset rows="75,*,30" framespacing="0" border="0">
<frame name="aclv_changeConfirm_head" src="switch.do?page=/search/aclvChangeConfirmHead.jsp" />
<frame name="aclv_changeConfirm_body" src="switch.do?page=/search/aclvChangeConfirmBody.jsp" />
<frame name="aclv_changeConfirm_foot" src="switch.do?page=/search/aclvChangeConfirmFoot.jsp" />
</frameset>
</html>
