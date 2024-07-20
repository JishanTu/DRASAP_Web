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
</head>
<body>
<table border="0" cellspacing="1" cellpadding="0" align="center">
	<!-- 見出し部分 -->
	<tr>
		<td align="center" bgcolor="#CCCCCC" rowspan="2"><span class="normal12">図番</span></td>
		<td align="center" bgcolor="#CCCCCC" colspan="2"><span class="normal12">アクセスレベル</span></td>
		<td align="center" bgcolor="#CCCCCC" colspan="2"><span class="normal12">使用禁止区分</span></td>
	</tr>
	<tr>
		<td align="center" bgcolor="#CCCCCC"><span class="normal12">現在</span></td>
		<td align="center" bgcolor="#CCCCCC"><span class="normal12">変更後</span></td>
		<td align="center" bgcolor="#CCCCCC"><span class="normal12">現在</span></td>
		<td align="center" bgcolor="#CCCCCC"><span class="normal12">変更後</span></td>
	</tr>
	<!-- 変更対象を表示する部分 -->
	<logic:iterate id="aclvChangeElement" type="AclvChangeElement"
					name="aclvChangeForm" property="aclvChangeList" scope="session" >
		<% // 選択されていて、かつ変更されているもののみ表示する
			if(aclvChangeElement.isSelected() && aclvChangeElement.isModified()){ %>
			<tr>
				<td><span class="normal12"><bean:write name="aclvChangeElement" property="drwgNoFormated" /></span></td>
				<td align="center"><span class="normal12"><bean:write name="aclvChangeElement" property="oldAclId" /></span></td>
				<td align="center"><span class="normal12"><bean:write name="aclvChangeElement" property="newAclId" /></span></td>
				<td align="center"><span class="normal12">
					<%="NG".equals(aclvChangeElement.getOldProhibit())?"×":"○"%></span></td>
				<td align="center"><span class="normal12">
					<%="NG".equals(aclvChangeElement.getNewProhibit())?"×":"○"%></span></td>
			</tr>
		<% } %>
	</logic:iterate>
</table>
</body>
</html:html>
