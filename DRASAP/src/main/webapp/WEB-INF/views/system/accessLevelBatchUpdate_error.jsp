<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page isELIgnored="false"%>
<%
session.removeAttribute("accessLevelBatchUpdate.erros");
%>
<html>
<head>
<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Cache-Control" content="no-cache" />
<title>Drawing Search and Print System [エラー]</title>
<style type="text/css">
@import
url(
<%=request.getContextPath()%>/resources/css/<%=session.getAttribute("default_css")%>
);
</style>
<script type="text/javascript">
	function onLoad() {
		if (parent.acl_condition != null) {
			parent.acl_condition.unLockButtons();
		}
		nowProcessing();
	}
	function nowProcessing() {
		var nowProcessing;
		nowProcessing = document.getElementById("nowSearch");
		nowProcessing.style.visibility = "visible";
	}
</script>
</head>
<body onload="onLoad();">
	<!-- エラーの表示 -->
	<form:errors path="*" cssClass="error-message" />
	<table class="nowsearch" id="nowSearch" style="visibility: hidden">
		<tr valign="middle">
			<td align="center" style="font-size: 18pt; color: #0000FF;">
				処理中・・・・</td>
		</tr>
	</table>
</body>
</html>
