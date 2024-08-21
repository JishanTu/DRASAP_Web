<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="tyk.drasap.search.*,tyk.drasap.common.*"%>
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
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
	<title>アクセスレベル一括更新</title>
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<style type="text/css">@import url( <%=request.getContextPath() %>/resources/css/<%=session.getAttribute("default_css")%> );</style>
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
		function onLoad() {
			if (parent.acl_condition.unLockButtons != null) {
				parent.acl_condition.unLockButtons();
			}
		}
		function nowProcessing() {
			var nowProcessing;
			nowProcessing = document.getElementById("nowProcessing");
			nowProcessing.style.visibility = "visible";
		}
	</script>
</head>
<body bgcolor="#FFFFFF" style="margin: 0;" onload="onLoad();">
	<form action="<%=request.getContextPath() %>/accessLevelBatchUpdate"
		method="post">
		<input type="hidden" name="act" value="" />
		<c:set var="itemNoCount" value="${sessionScope.accessLevelBatchUpdateForm.itemNoCount}" />
		<c:if test="${errors.info != null}">
			<ul style="color: blue; font-size: 12pt;">
			<c:forEach var="msg" items="${errors.info}">
				<li><c:out value="${msg}" /></li>
			</c:forEach>
			</ul>
		</c:if>
		<% session.removeAttribute("accessLevelBatchUpdate.info"); %>
		<%-- <c:if test="${itemNoCount == 0}"> --%>
		<c:if test="${itemNoCount == 0 or empty itemNoCount}">
			<ul style="color: red; font-size: 12pt;">
				<li>検索結果は0件です。</li>
			</ul>
		</c:if>
		<c:if test="${errors.message != null}">
			<c:forEach var="msg" items="${errors.message}">
				<span style="margin-left: 40px; font-weight: bold; line-height: 1.5;color: #FF0000">${msg}</span>
			</c:forEach>
		</c:if>
		<table border="0" cellspacing="1" cellpadding="0">
			<%-- userを定義する --%>
			<c:set var="user" value="${sessionScope.user}" />
			<c:set var="idx" value="0" />
			<c:forEach var="aclUpload"
				items="${sessionScope.accessLevelBatchUpdateForm.uploadList}"
				varStatus="status">
				<c:set var="idx" value="${status.index + 1}" />

				<%-- 見出し部分を 15件毎につける --%>
				<c:if test="${idx % 15 == 1}">
					<tr>
						<td />
						<td nowrap="nowrap" align="center" bgcolor="#CCCCCC"><span
							class="normal10">品番</span></td>
						<td nowrap="nowrap" align="center" bgcolor="#CCCCCC"><span
							class="normal10">品名（規格型式）</span></td>
						<td nowrap="nowrap" align="center" bgcolor="#CCCCCC"><span
							class="normal10">グループ</span></td>
						<td nowrap="nowrap" align="center" bgcolor="#CCCCCC"><span
							class="normal10">該当図</span></td>
						<td nowrap="nowrap" align="center" bgcolor="#CCCCCC"><span
							class="normal10">機密管理図</span></td>
						<td nowrap="nowrap" align="center" bgcolor="#CCCCCC"><span
							class="normal10">変更前ACL</span></td>
						<td nowrap="nowrap" align="center" bgcolor="#CCCCCC"><span
							class="normal10">変更後ACL</span></td>
						<td nowrap="nowrap" align="center" bgcolor="#CCCCCC"><span
							class="normal10">メッセージ</span></td>
					</tr>
				</c:if>

				<%-- 使用禁止なら行の色を変更する --%>
				<c:set var="bgcolor1" value="#FFFFFF" />
				<c:if test="${not empty aclUpload.message}">
					<c:set var="bgcolor1" value="#FF66FF" />
				</c:if>

				<tr bgcolor="<c:out value="${bgcolor1}" />">
					<td style="background-color: #FFFFFF;"><div style="width: 10;"></div></td>
					<td nowrap="nowrap"><span class="normal12">&nbsp;<c:out
								value="${aclUpload.itemNo}" />&nbsp;
					</span></td>
					<td nowrap="nowrap"><span class="normal12">&nbsp;<c:out
								value="${aclUpload.itemName}" />&nbsp;
					</span></td>
					<td nowrap="nowrap"><span class="normal12">&nbsp;<c:out
								value="${aclUpload.grpCode}" />&nbsp;
					</span></td>
					<td nowrap="nowrap" align="center"><span class="normal12">&nbsp;<c:out
								value="${aclUpload.correspondingValue}" />&nbsp;
					</span></td>
					<td nowrap="nowrap" align="center"><span class="normal12">&nbsp;<c:out
								value="${aclUpload.confidentialValue}" />&nbsp;
					</span></td>
					<td nowrap="nowrap"><span class="normal12">&nbsp;<c:out
								value="${aclUpload.preUpdateAclName}" />&nbsp;
					</span></td>
					<td nowrap="nowrap"><span class="normal12">&nbsp;<c:out
								value="${aclUpload.postUpdateAclName}" />&nbsp;
					</span></td>
					<td nowrap="nowrap"><span class="normal12">&nbsp;<c:out
								value="${aclUpload.message}" />&nbsp;
					</span></td>
				</tr>
			</c:forEach>
		</table>
		<table class="nowsearch" id="nowProcessing"
			style="visibility: hidden;">
			<tr valign="middle">
				<td align="center" style="font-size: 18pt; color: #0000FF;">
					処理中・・・・</td>
			</tr>
		</table>

	</form>
</body>
</html>
