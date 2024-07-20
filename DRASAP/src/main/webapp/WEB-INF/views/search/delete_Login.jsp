<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%-- ログイン情報の確認 --%>
<logic:notPresent name="user" scope="session">
	<logic:redirect forward="timeout" />
</logic:notPresent>
<html:html>
<head>
	<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
	<title>Drawing Search and Print System [削除ツールログイン]</title>
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<style type="text/css">@import url( <%=request.getContextPath() %>/default.css );</style>
	<script type="text/javascript">
	<!--
		var browserName = navigator.appName;
		function cancel(){
			// キャンセル
			// 2022.04.13 Windows Edge対応. 削除ツールログインの「キャンセル」で戻れない不具合修正.
			//location.href = "switch.do?prefix=/search&amp;page=/searchResult.jsp";
			parent.condition.document.forms[0].act.value="search";// 隠し属性actに'search'をセット
			parent.condition.document.forms[0].target="result";// targetは'result'
			parent.condition.document.forms[0].submit();
		}
		// 初期フォーカス位置
		function onInitFocus(){
			document.delete_LoginForm.passwd.focus();
		}
	//-->
	</script>
</head>
<body onload="onInitFocus()">
<html:errors />
<html:form action="/delete_Login" >

<table align="center" border="0" cellspacing="0" cellpadding="5">
    <caption align="center"><b>削除ツールログイン</b></caption>
    <tr>
	<td align="center"colspan="2">図番削除用のパスワードを入力してください。</td>
    </tr>
    <tr style="background-color:#EEEEEE;">
	<td align="right">passwd : </td><td><input type="password" name="passwd" value="" style="width:180px;" /></td>
    </tr>
    <tr>
    <td align="center" colspan="2" style="background-color:#FFFFFF;">
	<html:submit style="margin-right:20px;">ログイン</html:submit>
	<input type="button" value="キャンセル" onclick="cancel()" />
    </td>
    </tr>
</table>
</html:form>
</body>
</html:html>
