<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="tyk.drasap.search.*" %>
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
	<script type="text/javascript">
	<!--
		// 属性actをセットして、サブミット
		function setActSubmit(parm){
			parent.aclv_change_body.document.forms[0].act.value=parm;// 隠し属性actにをセット
			parent.aclv_change_body.document.forms[0].target="_top";// ターゲットは画面全体
			parent.aclv_change_body.document.forms[0].submit();
		}
	//-->
	</script>
</head>
<body bgcolor="#CCCCCC" bottommargin="0" leftmargin="0" topmargin="0" rightmargin="0" marginheight="0" marginwidth="0">
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td>&nbsp;&nbsp;</td>
		<%	// アクセスレベルの変更対象がなければ
			String disabledString = "";
			if(((AclvChangeForm)session.getAttribute("aclvChangeForm")).getAclvChangeList().size() == 0){
				disabledString = "disabled=\"disabled\"";
			}
		%>
		<td align="center"><input type="button" value="　次へ...　" onclick="setActSubmit('NEXT')" <%=disabledString%> /></td>
		<td align="right"><input type="button" value="Cancel" onclick="setActSubmit('SEARCH')" /></td>
	</tr>
</table>
</body>
</html:html>
