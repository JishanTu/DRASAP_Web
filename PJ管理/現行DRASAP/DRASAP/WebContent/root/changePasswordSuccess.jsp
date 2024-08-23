<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<html:html>
<%-- ログイン情報の確認 --%>
<logic:notPresent name="user" scope="session">
	<logic:redirect forward="timeout" />
</logic:notPresent>
<logic:notPresent name="parentPage" scope="session">
	<logic:redirect forward="timeout" />
</logic:notPresent>
<!DOCTYPE html>
<head>
	<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<title>Drawing Search and Print System [パスワード変更]</title>
	<script type="text/javascript">
	<!--
	// 直リンク禁止
	var refinfo=document.referrer;
	if (!refinfo){
		location.replace('<%=request.getContextPath() %>/root/timeout.jsp');
	}

	function onLoad() {
		alert("パスワード変更が成功しました\nPassword change succeeded.");

		<%-- ログイン画面から遷移した場合--%>
		<logic:equal name="parentPage" scope="session" value="Login">
		<%-- 図面検索画面へ遷移 --%>
			<%-- 2022.04.13 Windows Edge対応. ログイン画面からのパスワード変更時の画面遷移不具合対応. --%>
			<logic:redirect forward="start_change_passwd_success" />
			<%-- location.replace('<%=request.getContextPath() %>/switch.do?prefix=/search&amp;page=/searchMain.jsp'); --%>
		</logic:equal>
		<%-- 図面検索画面から呼び出した場合 --%>
		<logic:notEqual name="parentPage" scope="session" value="Login">
			// ウィンドウを閉じる
			self.close();
		</logic:notEqual>
	}
	//-->
	</script>
</head>
<body onload="onLoad()" />
</html:html>