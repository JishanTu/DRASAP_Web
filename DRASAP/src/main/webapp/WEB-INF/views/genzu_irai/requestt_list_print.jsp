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

<html>
<head>
	<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
	<title>Drawing Search and Print System [図面登録依頼リスト]</title>
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<style type="text/css">@import url( <%=request.getContextPath()%>/resources/css/default.css );</style>
</head>

<body bgcolor="#FFFFFF" bottommargin="0" leftmargin="0" topmargin="0" rightmargin="0" marginheight="0" marginwidth="0">
<br />
<font color="red" size="4" >
	<c:forEach var="error" items="${request_listForm.listErrors}">
       <li><c:out value="${error}" /></li>
    </c:forEach>
</font>
<div style="text-align: center;">
    <span class="normal12">図面登録依頼リスト</span>
    <span class="normal10">&nbsp;&nbsp;${request_listForm.time}</span>
</div>
<table border="0" align="center">
	<tr bgcolor="#CCCCCC">
		<td align="center"><span class="normal10">依頼ID</span></td>
		<td align="center"><span class="normal10">依頼内容</span></td>
		<td align="center"><span class="normal10">図番</span></td>
<%-- // 2019.10.23 yamamoto modified. start
		<td align="center"><span class="normal10">号口・号機</span></td>
		<td align="center"><span class="normal10">原図内容</span></td>
// 2019.10.23 yamamoto modified. end --%>
		<td align="center"><span class="normal10">依頼者</span></td>
		<td align="left"><span class="normal10">部署名</span></td>
	</tr>
	<c:forEach var="item" items="${request_listForm.printList}">
	<c:set var="e" value="${item}"/>
	<c:set var="job_id" value="${e.job_id}"/>
	<c:set var="job_name" value="${e.job_Name}"/>
	<c:set var="bangou" value="${e.number}"/>
	<c:if test="${empty bangou}">
	   <c:set var="bangou" value=""/>
    </c:if>
    <c:set var="gouki" value="${e.gouki}"/>
    <c:if test="${empty gouki}">
	   <c:set var="gouki" value=""/>
    </c:if>
    <c:set var="genzu" value="${e.genzu}"/>
    <c:if test="${empty genzu}">
	   <c:set var="genzu" value=""/>
    </c:if>
    <c:set var="irai" value="${e.irai}"/>
    <c:set var="busyo" value="${e.busyo}"/>
	
	<tr>
		<td align="center"><span class="normal10">&nbsp;${job_id}&nbsp;</span></td>
        <td align="center"><span class="normal10">&nbsp;${job_name}&nbsp;</span></td>
        <td align="center"><span class="normal10">&nbsp;${bangou}&nbsp;</span></td>
        <!-- <td align="center"><span class="normal10">&nbsp;${gouki}&nbsp;</span></td>
        <td align="center"><span class="normal10">&nbsp;${genzu}&nbsp;</span></td> -->
        <td align="center"><span class="normal10">&nbsp;${irai}&nbsp;</span></td>
        <td align="left"><span class="normal10">&nbsp;${busyo}&nbsp;</span></td>
	</tr>
	</c:forEach>
</table>
</body>
</html>
