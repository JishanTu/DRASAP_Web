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
	<title>Drawing Search and Print System [アクセスレベル変更]</title>
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<style type="text/css">@import url( <%=request.getContextPath() %>/default.css );</style>
</head>
<body bgcolor="#CCCCCC" bottommargin="0" leftmargin="0" topmargin="0" rightmargin="0" marginheight="0" marginwidth="0">
<!--===================== ヘッダ =====================-->
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td>
			<table border="0" bgcolor="#EEEEEE">
				<tr><td nowrap="nowrap"><span class="normal18">アクセスレベル・使用禁止区分の変更前確認</span></td></tr>
			</table></td>
		<td>
			<table border="0" bgcolor="#EEEEEE">
				<tr><td nowrap="nowrap"><span class="normal10">内容を確認してOKなら「変更」をクリックして下さい。<br />
							もう一度修正したい場合は、「戻る」をクリックして下さい。</span></td></tr>
			</table></td>
	</tr>
</table>
<!--===================== ヘッダ =====================-->
<table border="0" cellspacing="0" cellpadding="0">
	<tr>
		<!--===================== 表示内容の変更 =====================-->
		<td><table border="1">
			<tr>
				<td>
					<span class="normal10">職番：11103-9</span></td>
				<td>
					<span class="normal10">氏名：角野　孝直</span></td>
				<td>
					<span class="normal10">部署名(店名)：カッティングマシン標準機部</span></td>
			</tr>
		</table></td>
	</tr>
</table>
</body>
</html:html>
