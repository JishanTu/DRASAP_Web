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
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
	<title>図面登録依頼詳細</title>
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<style type="text/css">
	
	@import url( <%=request.getContextPath()%>/resources/css/default.css );
	
	td {
	white-space: nowrap;
	}


	</style>
</head>
<body bgcolor="#CCCCCC" bottommargin="0" leftmargin="0" topmargin="0"
	rightmargin="0" marginheight="0" marginwidth="0">
<!--================== ヘッダ =======================-->
<table border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td>
			<table border="0" bgcolor="#EEEEEE">
				<tr>
					<td><span class="normal18">図面登録依頼詳細</span></td>
				</tr>
			</table>
		</td>
		<!--=================== 職番などの表示 ===========================-->
		<td align="left">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><table border="1">
						<tr>
							<td><span class="normal12">職番：<c:out value="${user.id}" /></span></td>
							<td><span class="normal12">氏名：<c:out value="${user.name}" /></span></td>
							<td><span class="normal12">部署名：<c:out value="${user.deptName}" /></span></td>
						</tr>
					</table></td>
					<td align="right">&nbsp;&nbsp;&nbsp;&nbsp; <input type="button" value="Close" style="font-size: 12px;" onclick="parent.window.close()" /></td>
				</tr>
			</table>
		</td>
	</tr>
</table>
</body>
</html>
