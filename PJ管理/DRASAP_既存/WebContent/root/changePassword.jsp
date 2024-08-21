<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="tyk.drasap.common.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%-- ログイン情報の確認 --%>
<logic:notPresent name="user" scope="session">
	<logic:redirect forward="timeout" />
</logic:notPresent>
<logic:notPresent name="parentPage" scope="session">
	<logic:redirect forward="timeout" />
</logic:notPresent>
<!DOCTYPE html>
<html:html>
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
<!--
	// IE11でモーダルウィンドウでsubmitした際に
	// 別ウィンドウが開かれる問題の対応
	window.name = '_chgPass'
// -->
</script>
</head>
<body>
	<html:img page="/img/DRASAPBanner.JPG" width="333" height="70" />
	<html:img page="/img/CONFIDENTIALBanner.JPG" width="167" height="70" />
	<br />

	<ul>
	<%-- ログイン画面から遷移した場合--%>
	<logic:equal name="parentPage" scope="session" value="Login">
		<%-- ユーザIDとパスワードが同じ場合--%>
		<logic:equal name="samePasswdId" scope="session" value="true">
		<li>
			ユーザーIDと同じパスワードは使用不可です。パスワード変更してください。<br />
			Cannot use the same Password as the user ID. Please change your Password.
		</li>
		</logic:equal>
		<logic:notEqual name="samePasswdId" scope="session" value="true">
		<li>
			パスワードの有効期限が切れました。パスワード変更してください。<br />
			Your Password has expired. Please change your Password.
		</li>
		</logic:notEqual>
	</logic:equal>
		<%-- パスワード制約の表示 --%>
		<% // パスワード制約がある場合のみ表示
		String msg = new UserDef().getPasswordConstraintMessage();
		if (msg != null && msg.length() != 0 ) { %>
		<li>
			<%= msg %>
		</li>
		<% } %>
	</ul>
	<html:form action="/changePasswd" target="_chgPass">
	<table>
		<tr>
			<td>
				<label for="name">ユーザーＩＤ<br/></label>
				<label for="name">User ID</label>
				<br /><br />
			</td>
			<td>
				<bean:write name="user" property="id" scope="session" />
				<br /><br />
			</td>
			<td ></td>
		</tr>
		<tr>
			<td>
				<label for="oldpass">現在のパスワード<br/></label>
				<label for="oldpass">Current Password</label>
				<br /><br />
			</td>
			<td>
				<input type="password" name="oldpass" maxlength="20" style="width:180px;" tabindex="1" />
				<%-- <html:password property="oldpass" maxlength="20" style="width:180px;" tabindex="1" /> --%>
				<br /><br />
			</td>
			<td rowspan="3" class="errMsg">
				<!-- エラーの表示 -->
				<font color="RED">
				<b><html:errors header="false" footer="false" /></b>
				</font>
			</td>
		</tr>
		<tr>
			<td>
				<label for="newpass">新しいパスワード<br/></label>
				<label for="newpass">New Password</label>
				<br /> <br />
			</td>
			<td>
				<input type="password" name="newpass" maxlength="20" style="width:180px;" tabindex="2"
				title="入力可能文字は半角英数記号のみです&#10;Only single-byte alphanumeric characters can be entered"/>
				<% // <html:password property="newpass" size="32" maxlength="32" tabindex="2" />
				%>
				<br /><br />
			</td>
			<td></td>
		</tr>
		<tr>
			<td>
				<label for="newPassConfirm">新しいパスワード（再入力）<br/></label>
				<label for="newPassConfirm">Re-enter New Password</label>
				<br /> <br />
			</td>
			<td>
				<input type="password" name="newPassConfirm" maxlength="20" style="width:180px;" tabindex="3" />
				<% // <html:password property="newPassConfirm" size="32" maxlength="32" tabindex="3" />
				%>
				<br /><br />
			</td>
			<td></td>
		</tr>
		<tr>
			<td>
				<html:submit>更新 / Update</html:submit>
				<br /> <br />
			</td>
			<td>
				<input type="button" value="キャンセル / Cancel" onclick="self.close()" />
				<br /> <br />
			</td>
			<td></td>
		</tr>
	</table>
	</html:form>
</body>

</html:html>