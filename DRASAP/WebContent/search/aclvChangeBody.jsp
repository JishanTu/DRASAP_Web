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
<body bgcolor="#FFFFFF" bottommargin="0" leftmargin="0" topmargin="0" rightmargin="0" marginheight="0" marginwidth="0">
<html:form action="/aclvChange" >
<html:hidden property="act" />
<!-- メッセージの表示部分 -->
<font color="red" size="3"><ul>
<logic:iterate id="errorMessage" type="java.lang.String"
				name="aclvChangeForm" property="errorMessages" scope="session" >
	<li><bean:write name="errorMessage" /></li>
</logic:iterate>
</ul></font>
<table border="0" cellspacing="1" cellpadding="0" align="center">
	<!-- 見出し部分 -->
	<tr>
		<td rowspan="2"></td>
		<td align="center" bgcolor="#CCCCCC" rowspan="2"><span class="normal12">図番</span></td>
		<td align="center" bgcolor="#CCCCCC" colspan="2"><span class="normal12">アクセスレベル</span></td>
		<td align="center" bgcolor="#CCCCCC" colspan="2"><span class="normal12">使用禁止区分</span></td>
	</tr>
	<tr>
		<td align="center" bgcolor="#CCCCCC" width="60"><span class="normal12">現在</span></td>
		<td align="center" bgcolor="#CCCCCC" width="60"><span class="normal12">変更後</span></td>
		<td align="center" bgcolor="#CCCCCC" width="60"><span class="normal12">現在</span></td>
		<td align="center" bgcolor="#CCCCCC" width="60"><span class="normal12">変更後</span></td>
	</tr>
	<!-- 変更対象を表示する部分 -->
	<logic:iterate id="aclvChangeElement" type="AclvChangeElement"
					name="aclvChangeForm" property="aclvChangeList" scope="session" >
		<tr>
			<td><html:checkbox name="aclvChangeElement" property="selected" indexed="true" /></td>
			<td><span class="normal12"><bean:write name="aclvChangeElement" property="drwgNoFormated" /></span></td>
			<td align="center"><span class="normal12"><bean:write name="aclvChangeElement" property="oldAclId" /></span></td>
			<td align="center">
				<html:select name="aclvChangeElement" property="newAclId" indexed="true" style="font-size:12pt">
					<html:options labelName="aclvChangeForm" labelProperty="aclvNameList"
											name="aclvChangeForm" property="aclvKeyList" />
				</html:select></td>
			<td align="center"><span class="normal12">
					<%="NG".equals(aclvChangeElement.getOldProhibit())?"×":"○"%></span></td>
			<td align="center">
				<html:select name="aclvChangeElement" property="newProhibit" indexed="true" style="font-size:12pt">
					<html:option value="OK">○</html:option>
					<html:option value="NG">×</html:option>
				</html:select></td>
		</tr>
	</logic:iterate>
</table>
<%	// 変更できない図番があれば表示する
	if(((AclvChangeForm)session.getAttribute("aclvChangeForm")).getAclvNotChangeList().size() > 0){ %>
		<hr width="80%" color="#FF3333" />
		<center>
		<span class="normal10red">以下の図番には、変更権限が付与されていませんので、対象外となります。
		<ul>
			<logic:iterate id="notChangedDrwgNo" type="java.lang.String"
							name="aclvChangeForm" property="aclvNotChangeList" scope="session" >
				<li><bean:write name="notChangedDrwgNo" /></li>
			</logic:iterate>
		</ul>
		</span><br />
		</center>
<% } %>
</html:form>
</body>
</html:html>
