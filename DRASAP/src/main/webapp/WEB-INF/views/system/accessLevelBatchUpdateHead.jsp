<%@ page contentType="text/html;charset=UTF-8" %>
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
	<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
	<title>アクセスレベル一括更新</title>
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<style type="text/css">@import url( <%=request.getContextPath()%>/resources/css/default.css );</style>
	<script type="text/javascript" src="<%=request.getContextPath() %>/resources/js/prototype.js" ></script>
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
		function doClose() {
			var docObj = parent.acl_body.acl_condition.document;
			var aclUpdateNoValue = (docObj) ? docObj.getElementsByName("aclUpdateNo")[0].value : null;
			if (aclUpdateNoValue != null && aclUpdateNoValue.length > 0) {
				// HTTP request.
				new Ajax.Request(
						"<%=request.getContextPath() %>/accessLevelBatchUpdate",
						{
							method: "post",
							postBody: $H({act: 'close', aclUpdateNo: aclUpdateNoValue}).toQueryString(),
							onComplete: function (request) {
								parent.window.close();
							}
						});
			} else {
				parent.window.close();
			}
		}
	</script>
</head>
<body bgcolor="#CCCCCC" style="margin: 0; overflow: hidden;">
<!--================== ヘッダ =======================-->
<table border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td>
			<table border="0" bgcolor="#EEEEEE">
				<tr><td nowrap="nowrap"><span class="normal18">アクセスレベル一括更新</span></td></tr>
			</table>
		</td>
		<!--=================== 職番などの表示 ===========================-->
		<td align="left">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td>
						<table border="1">
							<tr>
								<td><span class="normal12">職番：<c:out
									value="${user.id}" /></span></td>
							<td><span class="normal12">氏名：<c:out
									value="${user.name}" /></span></td>
							<td><span class="normal12">部署名：<c:out
									value="${user.deptName}" /></span></td>
							</tr>
						</table>
					</td>
					<td align="right">&nbsp;&nbsp;&nbsp;&nbsp;
						<input type="button" value="Close" style="font-size: 12px;" onclick="doClose()" />
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
</body>
</html>
