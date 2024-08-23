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
	<title>図面登録依頼リスト</title>
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<style type="text/css">@import url(<%=request.getContextPath() %>/default.css );</style>
	<script type="text/javascript">
	<!--
		function doTouroku(){
			parent.list_body.document.forms[0].action.value="button_update";//完了情報登録ボタンのアクション
			//alert("アクション = " + parent.list_body.document.forms[0].action.value);
			parent.list_body.document.forms[0].submit();
		}
	//-->
	</script>
</head>
<body bgcolor="#CCCCCC" bottommargin="0" leftmargin="0" topmargin="0" rightmargin="0" marginheight="0" marginwidth="0">
<!--=============== ヘッダ ===============-->
<form>
<input type="hidden" name="action"/>
<table border="0" width="100%">
	<tr>
		<td align="center">
			<html:submit onclick="doTouroku()">完了情報登録</html:submit>
		</td>
		<td align="right">
			<input type="button" value="Close" onclick="parent.window.close()" />
		</td>
	</tr>
</table>
</form>
</body>
</html:html>
