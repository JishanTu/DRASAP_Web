<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="tyk.drasap.search.*,tyk.drasap.common.*" %>
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
	<title>アクセスレベル一括更新</title>
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<style type="text/css">@import url( <%=request.getContextPath() %>/<bean:write name="default_css" scope="session" /> );</style>
	<script type="text/javascript">
	<!--
	document.onkeydown = keys;
	function keys() {
		switch (event.keyCode) {
			case 116: // F5
				event.keyCode = 0;
				return false;
				break;
		}
	}
	function onLoad() {
		if (parent.acl_condition.unLockButtons != null) {
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
<body bgcolor="#FFFFFF" style="margin: 0;" onload="onLoad();">
<html:form action="/accessLevelBatchUpdate">
<html:hidden property="act" />
<bean:define id="itemNoCount" type="java.lang.Long"
			name="accessLevelBatchUpdateForm" property="itemNoCount" scope="session" />
<logic:present name="accessLevelBatchUpdate.info" scope="session">
		<ul style="color: blue; font-size: 12pt;">
			<li><html:messages id="info"  message="true" /><bean:write name="info" /></li>
		</ul>
</logic:present>
<% session.removeAttribute("accessLevelBatchUpdate.info"); %>
<%	// 検索結果がなければ表示する
	if (itemNoCount == 0) { %>
		<ul style="color: red; font-size: 12pt;">
			<li>検索結果は0件です。</li>
		</ul>
<%	} %>
<table border="0" cellspacing="1" cellpadding="0">
	<%-- userを定義する --%>
	<bean:define id="user" type="User" name="user" scope="session" />
	<logic:iterate id="aclUpload" type="AclUpload" indexId="idx" name="accessLevelBatchUpdateForm" property="uploadList" scope="session">
		<% // 見出し部分を 15件毎につける
		if ((idx.intValue() % 15) == 0) { %>
			<tr>
				<td />
				<td nowrap="nowrap" align="center" bgcolor="#CCCCCC"><span class="normal10">品番</span></td>
				<td nowrap="nowrap" align="center" bgcolor="#CCCCCC"><span class="normal10">品名（規格型式）</span></td>
				<td nowrap="nowrap" align="center" bgcolor="#CCCCCC"><span class="normal10">グループ</span></td>
				<td nowrap="nowrap" align="center" bgcolor="#CCCCCC"><span class="normal10">該当図</span></td>
				<td nowrap="nowrap" align="center" bgcolor="#CCCCCC"><span class="normal10">機密管理図</span></td>
				<td nowrap="nowrap" align="center" bgcolor="#CCCCCC"><span class="normal10">変更前ACL</span></td>
				<td nowrap="nowrap" align="center" bgcolor="#CCCCCC"><span class="normal10">変更後ACL</span></td>
				<td nowrap="nowrap" align="center" bgcolor="#CCCCCC"><span class="normal10">メッセージ</span></td>
			</tr>
		<% } %>
		<% // 使用禁止なら行の色を変更する
			String bgcolor1 = "#FFFFFF";
			if (aclUpload.getMessage() != null && aclUpload.getMessage().length() > 0) {
				bgcolor1 = "#FF66FF";
			}
		%>
		<tr bgcolor="<%= bgcolor1 %>">
			<td style="background-color: #FFFFFF;"><div style="width: 10;" /></td>
			<td nowrap="nowrap">
				<span class="normal12">&nbsp;<bean:write name="aclUpload" property="itemNo" />&nbsp;</span>
			</td>
			<td nowrap="nowrap">
				<span class="normal12">&nbsp;<bean:write name="aclUpload" property="itemName" />&nbsp;</span>
			</td>
			<td nowrap="nowrap">
				<span class="normal12">&nbsp;<bean:write name="aclUpload" property="grpCode" />&nbsp;</span>
			</td>
			<td nowrap="nowrap" align="center">
				<span class="normal12">&nbsp;<bean:write name="aclUpload" property="correspondingValue" />&nbsp;</span>
			</td>
			<td nowrap="nowrap" align="center">
				<span class="normal12">&nbsp;<bean:write name="aclUpload" property="confidentialValue" />&nbsp;</span>
			</td>
			<td nowrap="nowrap">
				<span class="normal12">&nbsp;<bean:write name="aclUpload" property="preUpdateAclName" />&nbsp;</span>
			</td>
			<td nowrap="nowrap">
				<span class="normal12">&nbsp;<bean:write name="aclUpload" property="postUpdateAclName" />&nbsp;</span>
			</td>
			<td nowrap="nowrap">
				<span class="normal12">&nbsp;<bean:write name="aclUpload" property="message" />&nbsp;</span>
			</td>
		</tr>
	</logic:iterate>
</table>
<table class="nowsearch" id="nowProcessing" style="visibility: hidden;">
<tr valign="middle">
<td align="center" style="font-size:18pt; color:#0000FF;">
処理中・・・・
</td>
</tr>
</table>

</html:form>
</body>
</html:html>
