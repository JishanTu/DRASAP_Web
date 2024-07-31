<%@ page contentType="text/html;charset=UTF-8"%>
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
	<title>図面登録依頼履歴</title>
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<style type="text/css">@import url( <%=request.getContextPath()%>/resources/css/default.css );</style>
</head>
<body bgcolor="#CCCCCC" bottommargin="0" leftmargin="0" topmargin="0" rightmargin="0" marginheight="0" marginwidth="0">
<!--=============== ヘッダ ===============-->
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td>
			<table border="0" bgcolor="#EEEEEE">
				<tr><td nowrap="nowrap"><span class="normal18">図面登録依頼履歴</span></td></tr>
			</table></td>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td>
			<table border="0" bgcolor="#EEEEEE" align="left">
				<tr><td nowrap="nowrap"><span class="normal10">完了した依頼が、過去10日分表示されています。<br />
					表示は完了日時の降順です。</span></td></tr>
			</table></td>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td width="100%" valign="bottom">
			<input type="button" value="Close"  onclick="parent.window.close()" /></td>
	</tr>
</table>
</body>
</html>
