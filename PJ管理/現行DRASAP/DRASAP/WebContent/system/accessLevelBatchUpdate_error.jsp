<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<% session.removeAttribute("accessLevelBatchUpdate.erros"); %>
<html:html>
<head>
<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Cache-Control" content="no-cache" />
<title>Drawing Search and Print System [エラー]</title>
<style type="text/css">@import url( <%=request.getContextPath() %>/<bean:write name="default_css" scope="session" /> );</style>
<script type="text/javascript">
<!--
	function onLoad() {
		if (parent.acl_condition != null) {
			parent.acl_condition.unLockButtons();
		}
	}
	function nowProcessing() {
		var nowProcessing;
		nowProcessing = document.getElementById("nowProcessing");
		nowProcessing.style.visibility = "visible";
	}
//-->
</script>
</head>
<body onload="onLoad();">
<!-- エラーの表示 -->
<html:errors />
<table class="nowsearch" id="nowSearch" style="visibility:hidden">
<tr valign="middle">
<td align="center" style="font-size:18pt;color:#0000FF;">
処理中・・・・
</td>
</tr>
</table>
</body>
</html:html>
