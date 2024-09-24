<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="tyk.drasap.genzu_irai.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
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
	<title>図面登録依頼履歴</title>
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<style type="text/css">@import url( <%=request.getContextPath()%>/resources/css/default.css );</style>
</head>
<body bgcolor="#FFFFFF" bottommargin="0" leftmargin="0" topmargin="0" rightmargin="0" marginheight="0" marginwidth="0">
<font color="red" size="4" >
	<ul>
		<c:forEach var="err"
			items="${requestHistoryForm.errors}">
			<li><c:out value="${err}" /></li>
		</c:forEach>
	
		<c:set var="historyListSize" value="${fn:length(requestHistoryForm.historyList)}"/>
		<c:if test="${historyListSize == 0}">
			<li>0件です。</li>
		</c:if>
	</ul>
</font>
<table border="0" align="center">
	<tr bgcolor="#CCCCCC">
		<td align="center"><span class="normal10">完了日時</span></td>
		<td align="center"><span class="normal10">作業者</span></td>
		<td align="center"><span class="normal10">依頼日時</span></td>
		<td align="center"><span class="normal10">依頼内容</span></td>
		<td align="center"><span class="normal10">図番</span></td>
<%-- // 2019.10.23 yamamoto modified. start
		<td align="center"><span class="normal10">号口・号機</span></td>
		<td align="center"><span class="normal10">原図内容</span></td>
		<td align="center"><span class="normal10">部数</span></td>
		<td align="center"><span class="normal10">縮小</span></td>
		<td align="center"><span class="normal10">サイズ</span></td>
// 2019.10.23 yamamoto modified. end --%>
		<td align="center"><span class="normal10">メッセージ</span></td>
		<td align="center"><span class="normal10">依頼者</span></td>
		<td align="center"><span class="normal10">部署名</span></td>
	</tr>
	<c:forEach var="history" items="${requestHistoryForm.historyList}">
	<tr>
		<td nowrap="nowrap">
			<span class="normal10"><c:out value="${history.completeDate}" /></span>
		</td>
		<td nowrap="nowrap" align="center">
			<span class="normal10"><c:out value="${history.completeUser}" /></span>
		</td>
		<td nowrap="nowrap" align="center">
			<span class="normal10"><c:out value="${history.requestDate}" /></span>
		</td>
		<td nowrap="nowrap" align="center">
			<span class="normal10"><c:out value="${history.jobName}" /></span>
		</td>
		<td nowrap="nowrap" align="center">
			<span class="normal10"><c:out value="${history.drwgNo}" /></span>
		</td>
		<!-- 其他属性的输出 -->
		<td nowrap="nowrap" align="center">
			<span class="normal10"><c:out value="${history.message}" /></span>
		</td>
		<td nowrap="nowrap" align="center">
			<span class="normal10"><c:out value="${history.requestUser}" /></span>
		</td>
		<td nowrap="nowrap" align="center">
			<span class="normal10"><c:out value="${history.deptName}" /></span>
		</td>
	</tr>
</c:forEach>
</table>
</body>
</html>
