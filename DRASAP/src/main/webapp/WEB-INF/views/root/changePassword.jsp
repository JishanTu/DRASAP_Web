<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="tyk.drasap.common.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page isELIgnored="false"%>
<%-- ログイン情報の確認 --%>
<%--<!-- <logic:notPresent name="user" scope="session"> -->--%>
<%--<!-- 	<logic:redirect forward="timeout" /> -->--%>
<%--<!-- </logic:notPresent> -->--%>
<%--<!-- <logic:notPresent name="parentPage" scope="session"> -->--%>
<%--<!-- 	<logic:redirect forward="timeout" /> -->--%>
<%--<!-- </logic:notPresent> -->--%>
<c:if test="${sessionScope.user == null}">
	<script>
		location.replace('<%=request.getContextPath() %>/timeout');
	</script>
</c:if>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Cache-Control" content="no-cache" />
<title>Drawing Search and Print System [パスワード変更]</title>
<style type="text/css">
.errMsg {
	padding-left: 30px;
	padding-right: 10px;
	vertical-align: top;
}

table {
	padding-right: 10px;
	border-collapse: separate;
	vertical-align: left;
}

td {
	padding-right: 10px;
}

ul {
	color: #ff0000;
	font size: 3;
}
</style>
<script type="text/javascript">
	//<!--
	// IE11でモーダルウィンドウでsubmitした際に
	// 別ウィンドウが開かれる問題の対応
	window.name = '_chgPass';
	// -->
	</script>
</head>
<body>
	<img
		src="<%=request.getContextPath()%>/resources/img/DRASAPBanner.JPG"
		width="333" height="70" />
	<img
		src="<%=request.getContextPath()%>/resources/img/CONFIDENTIALBanner.JPG"
		width="167" height="70" />
	<br />

	<ul>
		<%-- ログイン画面から遷移した場合--%>
		<c:if test="${sessionScope.parentPage == true}">
			<c:choose>
				<c:when test="${sessionScope.samePasswdId == true}">
					<li>ユーザーIDと同じパスワードは使用不可です。パスワード変更してください。<br /> Cannot use the
						same Password as the user ID. Please change your Password.
					</li>
				</c:when>
				<c:otherwise>
					<li>パスワードの有効期限が切れました。パスワード変更してください。<br /> Your Password has
						expired. Please change your Password.
					</li>
				</c:otherwise>
			</c:choose>
		</c:if>
		<%-- パスワード制約の表示 --%>
		<%
		// パスワード制約がある場合のみ表示
		String msg = new UserDef().getPasswordConstraintMessage();
		if (msg != null && msg.length() != 0) {
		%>
		<li><%=msg%></li>
		<%
		}
		%>
	</ul>
	<form action="<%=request.getContextPath()%>/changePasswd"
		method="post" target="_chgPass">
		<table>
			<tr>
				<td><label for="name">ユーザーＩＤ<br /></label> <label for="name">User
						ID</label> <br />
				<br /></td>
				<td>${sessionScope.user.id} <br />
				<br />
				</td>
				<td></td>
			</tr>
			<tr>
				<td><label for="oldpass">現在のパスワード<br /></label> <label
					for="oldpass">Current Password</label> <br />
				<br /></td>
				<td><input type="password" name="oldpass" maxlength="20"
					style="width: 180px;" tabindex="1" /> <%-- <html:password property="oldpass" maxlength="20" style="width:180px;" tabindex="1" /> --%>
					<br />
				<br /></td>
				<td rowspan="3" class="errMsg">
					<!-- エラーの表示 --> <font color="RED"> <b>
							<ul>
							<c:forEach var="entry"
                                    items="${pageContext.request.attributeNames}">
                                    <c:if
                                        test="${!fn:contains(entry, '.') && !fn:contains(entry, 'path') && !fn:containsIgnoreCase(entry, 'form')}">
                                        <c:set var="attributeName" value="${entry}" />
                                        <c:set var="attributeValue"
                                            value="${requestScope[attributeName]}" />
                                        <li>${attributeValue}</li>
                                    </c:if>
                                </c:forEach>
							</ul>
					</b>
				</font>
				</td>
			</tr>
			<tr>
				<td><label for="newpass">新しいパスワード<br /></label> <label
					for="newpass">New Password</label> <br /> <br /></td>
				<td><input type="password" name="newpass" maxlength="20"
					style="width: 180px;" tabindex="2"
					title="入力可能文字は半角英数記号のみです&#10;Only single-byte alphanumeric characters can be entered" />
					<%
					// <html:password property="newpass" size="32" maxlength="32" tabindex="2" />
					%> <br />
				<br /></td>
				<td></td>
			</tr>
			<tr>
				<td><label for="newPassConfirm">新しいパスワード（再入力）<br /></label> <label
					for="newPassConfirm">Re-enter New Password</label> <br /> <br />
				</td>
				<td><input type="password" name="newPassConfirm" maxlength="20"
					style="width: 180px;" tabindex="3" /> <%
 // <html:password property="newPassConfirm" size="32" maxlength="32" tabindex="3" />
 %> <br />
				<br /></td>
				<td></td>
			</tr>
			<tr>
				<td><input type="submit" value="更新 / Update" tabindex="3">
					<br /> <br /></td>
				<td><input type="button" value="キャンセル / Cancel"
					onclick="self.close()" /> <br /> <br /></td>
				<td></td>
			</tr>
		</table>
	</form>
</body>

</html>