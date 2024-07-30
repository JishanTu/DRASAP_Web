<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="tyk.drasap.search.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page isELIgnored="false"%>
<%-- ログイン情報の確認 --%>
<c:if test="${sessionScope.user == null}">
	<script>
		location.replace('<%=request.getContextPath()%>/timeout');
	</script>
</c:if>
<html>
<head>
	<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
	<title>Drawing Search and Print System [アクセスレベル変更]</title>
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<style type="text/css">@import url( <%=request.getContextPath()%>/resources/css/default.css );</style>
</head>
<body>
<table border="0" cellspacing="1" cellpadding="0" align="center">
	<!-- 見出し部分 -->
	<tr>
		<td align="center" bgcolor="#CCCCCC" rowspan="2"><span class="normal12">図番</span></td>
		<td align="center" bgcolor="#CCCCCC" colspan="2"><span class="normal12">アクセスレベル</span></td>
		<td align="center" bgcolor="#CCCCCC" colspan="2"><span class="normal12">使用禁止区分</span></td>
	</tr>
	<tr>
		<td align="center" bgcolor="#CCCCCC"><span class="normal12">現在</span></td>
		<td align="center" bgcolor="#CCCCCC"><span class="normal12">変更後</span></td>
		<td align="center" bgcolor="#CCCCCC"><span class="normal12">現在</span></td>
		<td align="center" bgcolor="#CCCCCC"><span class="normal12">変更後</span></td>
	</tr>
	<!-- 変更対象を表示する部分 -->
	<c:forEach var="aclvChangeElement" items="${aclvChangeForm.aclvChangeList}">
    <c:if test="${aclvChangeElement.selected and aclvChangeElement.modified}">
        <tr>
            <td><span class="normal12">${aclvChangeElement.drwgNoFormated}</span></td>
            <td align="center"><span class="normal12">${aclvChangeElement.oldAclId}</span></td>
            <td align="center"><span class="normal12">${aclvChangeElement.newAclId}</span></td>
            <td align="center"><span class="normal12">${aclvChangeElement.oldProhibit eq 'NG' ? '×' : '○'}</span></td>
            <td align="center"><span class="normal12">${aclvChangeElement.newProhibit eq 'NG' ? '×' : '○'}</span></td>
        </tr>
    </c:if>
</c:forEach>
</table>
</body>
</html>
