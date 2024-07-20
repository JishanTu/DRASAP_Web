<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="tyk.drasap.search.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

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
<body bgcolor="#FFFFFF" bottommargin="0" leftmargin="0" topmargin="0" rightmargin="0" marginheight="0" marginwidth="0">
<form action="<%=request.getContextPath() %>/aclvChange" method = "post">
<input type="hidden" name="act" value="" />
<!-- メッセージの表示部分 -->
<font color="red" size="3"><ul>
<c:forEach var="errorMessage" items="${aclvChangeForm.errorMessages}">
    <li><c:out value="${errorMessage}" /></li>
</c:forEach>

</ul></font>
<table border="0" cellspacing="1" cellpadding="0" align="center">
	<!-- 見出し部分 -->
	<tr>
		<td rowspan="2"></td>
		<td align="center" bgcolor="#CCCCCC" rowspan="2"><span class="normal12">図番</span></td>
		<td align="center" bgcolor="#CCCCCC" colspan="2"><span class="normal12">アクセスレベル</span></td>
		<td align="center" bgcolor="#CCCCCC" colspan="2"><span class="normal12">使用禁止区分</span></td>
	</tr>
	<tr>
		<td align="center" bgcolor="#CCCCCC" width="60"><span class="normal12">現在</span></td>
		<td align="center" bgcolor="#CCCCCC" width="60"><span class="normal12">変更後</span></td>
		<td align="center" bgcolor="#CCCCCC" width="60"><span class="normal12">現在</span></td>
		<td align="center" bgcolor="#CCCCCC" width="60"><span class="normal12">変更後</span></td>
	</tr>
	<!-- 変更対象を表示する部分 -->
	<c:forEach var="aclvChangeElement" items="${aclvChangeForm.aclvChangeList}" varStatus="loop">
    <tr>
        <td><input type="checkbox" name="aclvChangeForm.aclvChangeList[${loop.index}].selected" value="true" /></td>
        <td><span class="normal12">${aclvChangeElement.drwgNoFormated}</span></td>
        <td align="center"><span class="normal12">${aclvChangeElement.oldAclId}</span></td>
        <td align="center">
            <form:select path="aclvChangeList[${loop.index}].newAclId" style="font-size:12pt">
                <form:options items="${aclvChangeForm.aclvNameList}" itemValue="aclvKeyList" itemLabel="aclvNameList"/>
            </form:select>
        </td>
        <td align="center"><span class="normal12">${aclvChangeElement.oldProhibit eq 'NG' ? '×' : '○'}</span></td>
        <td align="center">
            <form:select path="aclvChangeList[${loop.index}].newProhibit" style="font-size:12pt">
                <form:option value="OK">○</form:option>
                <form:option value="NG">×</form:option>
            </form:select>
        </td>
    </tr>
</c:forEach>

</table>
<c:if test="${not empty aclvChangeForm.aclvNotChangeList}">
    <hr width="80%" color="#FF3333" />
    <center>
        <span class="normal10red">以下の図番には、変更権限が付与されていませんので、対象外となります。</span>
        <ul>
            <c:forEach var="notChangedDrwgNo" items="${aclvChangeForm.aclvNotChangeList}">
                <li><c:out value="${notChangedDrwgNo}" /></li>
            </c:forEach>
        </ul>
    </center>
</c:if>
</form>
</body>
</html>
