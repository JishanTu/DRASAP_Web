<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="tyk.drasap.system.*,tyk.drasap.common.*"%>
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
<title>アクセスレベル更新結果</title>
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Cache-Control" content="no-cache" />
<style type="text/css">
@import
url(
<%=request.getContextPath()%>/resources/css/default.css
);
</style>
<script type="text/javascript">
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
<body style="background-color: #FFFFFF; margin: 0;">
	<form action="<%=request.getContextPath()%>/accessLevelUpdatedResult"
		method="post">
		<input type="hidden" name="act" value="" />
		<c:set var="fileCount"
			value="${sessionScope.accessLevelUpdatedResultForm.fileCount}" />

		<c:if test="${fileCount == 0}">
			<ul style="color: red; font-size: 12pt;">
				<li>ACL更新結果ログファイルが0件です。</li>
			</ul>
		</c:if>
		<table border="0" align="center">
			<%-- userを定義する --%>
			<c:set var="user" value="${sessionScope.user}" />
			<c:if test="${fileCount > 0}">
				<tr style="background-color: #CCCCCC;">
					<td nowrap="nowrap" align="center"><span class="normal10">ログファイル</span></td>
					<td nowrap="nowrap" align="center"><span class="normal10">更新日時</span></td>
					<td nowrap="nowrap" align="center"><span class="normal10">ファイル種類</span></td>
				</tr>
			</c:if>
			<c:forEach var="accessLevelUpdatedResultElement"
				items="${sessionScope.accessLevelUpdatedResultForm.accessLevelUpdatedResultList}">
				<tr>
					<td nowrap="nowrap" align="left"><a
						href="accessLevelUpdatedResult.do?act=download&FILE_NAME=${accessLevelUpdatedResultElement.fileName}" target="acl_result_body"> <span
							class="normal12blue">&nbsp;${accessLevelUpdatedResultElement.fileName}&nbsp;</span>
					</a></td>
					<td nowrap="nowrap"><span class="normal12">&nbsp;${accessLevelUpdatedResultElement.lastModifiedFormatted}&nbsp;</span>
					</td>
					<td nowrap="nowrap"><span class="normal12">&nbsp;${accessLevelUpdatedResultElement.fileTypeDescription}&nbsp;</span>
					</td>
				</tr>
			</c:forEach>

		</table>
	</form>
</body>
</html>
