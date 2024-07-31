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
<title>原図庫作業者からのメッセージ</title>
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Cache-Control" content="no-cache" />
<style type="text/css">
@import
url(
<%=request.getContextPath()
%>/default.css
);
</style>
</head>
<body bgcolor="#FFFFFF" bottommargin="0" leftmargin="0" topmargin="0"
	rightmargin="0" marginheight="0" marginwidth="0">
	<font color="red" size="4"> <c:forEach var="error"
			items="${request_refForm.listErrors}">
			<li><c:out value="${error}" /></li>
		</c:forEach>
	</font>

	<c:set var="touroku" value="${request_refForm.touroku}" />
	<br />
	<table border="0" align="center">
		<tr bgcolor="#FFFF99">
			<td align="center"><span class="normal12">依頼ID</span></td>
			<td align="center"><span class="normal12">依頼内容</span></td>
		</tr>
		<tr>
			<td><span class="normal12">&nbsp;<c:out
						value="${request_refForm.job_id}" />&nbsp;
			</span></td>
			<td><span class="normal12">&nbsp;<c:out
						value="${request_refForm.job_name}" />&nbsp;
			</span></td>
		</tr>
	</table>
	<hr width="80%" />
	<table border="0" align="center">
		<tr bgcolor="#CCCCCC">
			<td bgcolor="#FFFFFF" />
			<td align="center"><span class="normal12">番号</span></td>
			<td align="center"><span class="normal12">号口・号機</span></td>
			<td align="center"><span class="normal12">原図内容</span></td>
			<td align="center"><span class="normal12">メッセージ</span></td>
		</tr>


		<c:forEach var="item" items="${request_refForm.iraiList}">

			<c:set var="e" value="${item}" />

			<c:set var="irai" value="${e.job_name}" />
			<c:set var="start_no" value="${e.zikan}" />
			<c:set var="irai" value="${e.start}" />
			<c:set var="gouki" value="${e.gouki}" />
			<c:if test="${empty gouki}">
				<c:set var="gouki" value="" />
			</c:if>
			<c:set var="genzu" value="${e.genzu}" />
			<c:if test="${empty genzu}">
				<c:set var="genzu" value="" />
			</c:if>

			<c:set var="messege" value="${e.messege}" />
			<c:if test="${empty messege}">
				<c:set var="messege" value="" />
			</c:if>
			<c:set var="gensi" value="" />
			<c:set var="exist" value="${e.exist}" />
			<c:choose>
				<c:when test="${exist eq '0'}">
					<c:set var="gensi" value="1" />
				</c:when>

			</c:choose>


			<tr>
				<c:choose>
					<c:when test="${gensi eq '1'}">
						<td bgcolor="#FF3300"><span class="normal12white">原紙なし</span></td>
					</c:when>
					<c:otherwise>
						<td>&nbsp;</td>
					</c:otherwise>
				</c:choose>
				<td><span class="normal12">&nbsp;${start_no}&nbsp;</span></td>
				<td><span class="normal12">&nbsp;${gouki}&nbsp;</span></td>
				<td><span class="normal12">&nbsp;${genzu}&nbsp;</span></td>
				<td><span class="normal12">&nbsp;${messege}&nbsp;</span></td>
			</tr>
		</c:forEach>
	</table>
	<br />
	<hr width="80%" />
	<div style="text-align: center;">
		<input type="button" value="Close" onclick="javascript:window.close()" />
	</div>
</body>
</html>
