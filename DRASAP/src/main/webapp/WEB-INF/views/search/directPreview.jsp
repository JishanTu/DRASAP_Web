<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,java.net.URLEncoder"%>
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
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Cache-Control" content="no-cache" />
<title>Drawing Search and Print System [プレビュー]</title>
</head>
<body bgcolor="#FFFFFF">
	<font style="font-family: 'ＭＳ Ｐゴシック', 'ＭＳ ゴシック';"> <br/>
		<div style="text-align: center;">
			<!-- タイトル -->
			<b><font size="5" color="#0033CC">DRASAP</font></b>&nbsp;&nbsp;&nbsp;&nbsp;
			<font color="#0066FF">Drawing Search and Print System</font>
			<!-- エラーの表示 -->
			<c:forEach var="error" items="${errors}">
				<hr color="sandybrown">
				<font color="red" size="4">
					<ul>
						<li>
							<c:out value="${message}" />
						</li>
					</ul>
				</font>
				<hr color="sandybrown">
				<br/>
			</c:forEach>
			<!-- エラーがない場合、水平線を表示 -->
			<c:if test="${empty hasError}">
				<hr/>
			</c:if>
			<br/>
			<!-- 閉じるボタン -->
			<input type="button" value="Close" onclick="window.close()" />
		</div>
	</font>
</body>
</html>