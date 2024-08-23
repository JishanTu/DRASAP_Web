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
	<title>Drawing Search and Print System [運用支援ツールログイン]</title>
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<style type="text/css">@import url( <%=request.getContextPath() %>/default.css );</style>
	<script type="text/javascript">
	<!--
		var browserName = navigator.appName;
		function onLoad() {
                    var w = screen.availWidth;
                    var h = screen.availHeight;
                    mx = 0;
                    my = 0;
                    window.resizeTo(w, h);
                    window.moveTo(mx,my);//画面の位置指定
                    onInitFocus();
		}
		// 遷移する
		function cancel(){
			// キャンセル
			self.close();
		}
		// 初期フォーカス位置
		function onInitFocus(){
			document.management_LoginForm.passwd.focus();
		}
	//-->
	</script>
</head>
<body onload="onInitFocus()">
<html:errors />
<html:form action="/management_Login" >

<table align="center" border="0" cellspacing="0" cellpadding="5">
    <caption align="center"><b>運用支援ツールログイン</b></caption>
    <tr>
	<td align="center"colspan="2">運用支援ツール用のパスワードを入力してください。</td>
    </tr>
    <tr style="background-color:#EEEEEE;">
	<td align="right">passwd : </td><td><input type="password" name="passwd" value="" style="width:180px;" /></td>
    </tr>
    <tr>
    <td align="center" colspan="2"style="background-color:#FFFFFF;">
	<html:submit style="margin-right:20px;">ログイン</html:submit>
	<input type="button" value="キャンセル" onclick="cancel()" />
    </td>
    </tr>
</table>
</html:form>
</body>
</html:html>
