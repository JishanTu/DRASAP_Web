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
	<title>図面登録依頼詳細</title>
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<style type="text/css">@import url( <%=request.getContextPath() %>/default.css );</style>
</head>
<body bgcolor="#CCCCCC" bottommargin="0" leftmargin="0" topmargin="0" rightmargin="0" marginheight="0" marginwidth="0">
<!--================== ヘッダ =======================-->
<table border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td>
			<table border="0" bgcolor="#EEEEEE">
				<tr><td nowrap="nowrap"><span class="normal18">図面登録依頼詳細</span></td></tr>
			</table></td>
		<!--=================== 職番などの表示 ===========================-->
		<td align="left">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><table border="1">
						<tr>
							<td><span class="normal12">職番：<bean:write name="user" property="id" /></span></td>
							<td><span class="normal12">氏名：<bean:write name="user" property="name" /></span></td>
							<td><span class="normal12">部署名：<bean:write name="user" property="deptName" /></span></td>
						</tr>
					</table></td>
					<td align="right">&nbsp;&nbsp;&nbsp;&nbsp;
						<input type="button" value="Close" style="font-size: 12px;" onclick="parent.window.close()" /></td>
				</tr>
			</table>
		</td>
	</tr>
</table>
</body>
</html:html>
