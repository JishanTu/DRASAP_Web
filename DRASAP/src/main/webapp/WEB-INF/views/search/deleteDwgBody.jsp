<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="tyk.drasap.search.*,tyk.drasap.common.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%-- ログイン情報の確認 --%>
<c:if test="${empty sessionScope.user}">
	<script>
        location.replace('<%=request.getContextPath() %>/timeout');
    </script>
</c:if>
<html>
<head>
<meta content="text/html; charset=UTF-8" http-equiv="Content-type" />
<meta content="no-cache" http-equiv="Pragma" />
<meta content="no-cache" http-equiv="Cache-Control" />
<style type="text/css">
@import
url(
<%=request.getContextPath()%>/resources/css/default.css
);
</style>
</head>
<body marginwidth="0" marginheight="0" bgcolor="#ffffff" rightmargin="0"
	topmargin="0" leftmargin="0" bottommargin="0">
	<c:set var="deleteDwgForm" scope="session"
		value="${sessionScope.deleteDwgForm}" />
	<form action="<%=request.getContextPath() %>/deleteDwg">
		<c:set var="deleteDwgForm" scope="session"
			value="${sessionScope.deleteDwgForm}" />

		<c:if test="${not empty deleteDwgForm}">
			<table cellspacing="0" cellpadding="0" border="0">
				<tbody>
					<tr>
						<td><input type="hidden" name="act"
							value="${deleteDwgForm.act}" /> <input type="hidden"
							name="previewIdx" value="${deleteDwgForm.previewIdx}" /></td>
					</tr>
					<tr>
						<td>
							<table cellspacing="0" cellpadding="3" border="1" align="left">
								<tbody>
									<tr bgcolor="#cccccc" class="normal10">
										<c:forEach var="colName"
											items="${deleteDwgForm.colNameJPList}">
											<th nowrap="nowrap"><c:out value="${colName}" /></th>
										</c:forEach>
									</tr>
									<c:forEach var="record" items="${deleteDwgForm.recList}">
										<tr class="normal12">
											<c:forEach var="value" items="${record.valList}"
												varStatus="status">
												<td nowrap="nowrap"><c:choose>
														<c:when test="${status.index == 0}">
															<a href="<c:url value='preview'/>" target="_parent">
																<c:out value="${value}" /><br />
															</a>
														</c:when>
														<c:otherwise>
															<c:out value="${value}" />
															<br />
														</c:otherwise>
													</c:choose></td>
											</c:forEach>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</td>
					</tr>
					<tr>
						<td><br /> <br /></td>
					</tr>
				</tbody>
			</table>
		</c:if>
		<table cellspacing="0" cellpadding="0" border="0" align="center"
			class="normal12blue">
			<tbody>
				<tr>
					<td id="msg1"><c:out value="${deleteDwgForm.msg1}" /></td>
				</tr>
				<tr>
					<td id="msg2"><c:out value="${deleteDwgForm.msg2}" /></td>
				</tr>
			</tbody>
		</table>
	</form>
</body>
<c:forEach var="error" items="${errors}">
	<c:out value="${error}" />
	<br />
</c:forEach>
</html>

