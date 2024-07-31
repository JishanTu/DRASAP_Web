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
	<title>図面登録依頼リスト</title>
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<style type="text/css">@import url(<%=request.getContextPath()%>/resources/css/default.css );</style>
	<script type="text/javascript">

		function doTouroku(){
			parent.list_body.document.forms[0].action.value="button_update";//完了情報登録ボタンのアクション
			//alert("アクション = " + parent.list_body.document.forms[0].action.value);
			alert("フォーム = " + parent.list_body.document.forms[0]);
			parent.list_body.document.forms[0].submit();
		}
	</script>
</head>
<body bgcolor="#CCCCCC" bottommargin="0" leftmargin="0" topmargin="0" rightmargin="0" marginheight="0" marginwidth="0">
<!--=============== ヘッダ ===============-->
<form action="<%=request.getContextPath() %>/req_result" method="post">
<input type="hidden" name="action" value="${action}" />
<table border="0" width="100%">
	<tr>
		<td align="center">
			<input type="submit" value="完了情報登録" onclick="doTouroku()">
		</td>
		<td align="right">
			<input type="button" value="Close" onclick="parent.window.close()" />
		</td>
	</tr>
</table>
</form>
</body>
</html>
