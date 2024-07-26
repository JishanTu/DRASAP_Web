<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="tyk.drasap.search.*,tyk.drasap.common.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page isELIgnored="false"%>

<html>
<head>
<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Cache-Control" content="no-cache" />
<title>Drawing Search and Print System [Failed]</title>
</head>
<body bgcolor="#FFCC99">
	<font style="font-family: 'ＭＳ Ｐゴシック', 'ＭＳ ゴシック';"> 
	 <font color="#FF0000">
		    <% CookieManage langCookie = new CookieManage(); %>
			<% String lanKey = langCookie.getCookie(request, null, "Language"); %>
			<c:choose>
				<c:when test="${lanKey eq 'English'}">
					<h2>DRASAP Connection Error</h2>
					<br />
					<ul>
						<li>NO right to connect.</li>
						<li>There is incomplete information in user registration.
							Please contact your administrator.</li>
						<li>System error is also possible. Please contact your
							administrator.</li>
					</ul>
				</c:when>
				<c:otherwise>
					<h2>DRASAP接続エラー</h2>
            以下の理由で接続できません。<br />
					<ul>
						<li>ユーザーに接続する権限がありません。</li>
						<li>ユーザー登録に不備があります。管理者に連絡ください。</li>
						<li>その他システムエラーが発生した可能性があります。管理者に連絡ください。</li>
					</ul>
				</c:otherwise>
			</c:choose> <input type="button" value="Close" onclick="window.close()" />
			<br/> 
			<br/>
			<hr/ style="border: none; height: 0.5px; background-color: red;">
			<c:if test="${message != null}">
				<c:forEach var="msg" items="${message}">
					<span style="margin-left: 40px; font-weight: bold; line-height: 1.5;">${msg}</span><br/>
				</c:forEach>
			</c:if>
			<hr/ style="border: none; height: 0.5px; background-color: red;">
	 </font>
	</font>
</body>
</html>
