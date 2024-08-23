<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="tyk.drasap.system.*,tyk.drasap.common.*" %>
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
	<title>アクセスレベル更新結果</title>
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<style type="text/css">@import url( <%=request.getContextPath() %>/default.css );</style>
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
	//-->
	</script>
</head>
<body style="background-color: #FFFFFF; margin: 0;">
<html:form action="/accessLevelUpdatedResult">
<html:hidden property="act" />
<bean:define id="fileCount" type="java.lang.Long"
			name="accessLevelUpdatedResultForm" property="fileCount" scope="request" />
<%	// 検索結果がなければ表示する
	if (fileCount == 0) { %>
		<ul style="color: red; font-size: 12pt;">
			<li>ACL更新結果ログファイルが0件です。</li>
		</ul>
<%	} %>
<table border="0" align="center">
	<%-- userを定義する --%>
	<bean:define id="user" type="User" name="user" scope="session" />
	<logic:greaterThan name="fileCount" value="0">
	<tr style="background-color: #CCCCCC;">
		<td nowrap="nowrap" align="center"><span class="normal10">ログファイル</span></td>
		<td nowrap="nowrap" align="center"><span class="normal10">更新日時</span></td>
		<td nowrap="nowrap" align="center"><span class="normal10">ファイル種類</span></td>
	</tr>
	</logic:greaterThan>
	<logic:iterate id="accessLevelUpdatedResultElement" type="AccessLevelUpdatedResultElement" indexId="idx" name="accessLevelUpdatedResultForm" property="accessLevelUpdatedResultList" scope="request">
	<tr>
		<td nowrap="nowrap" align="left">
			<html:link forward="accessLevelLogDownload" target="acl_result_body" name="accessLevelUpdatedResultElement" property="linkParmMap">
				<span class="normal12blue">&nbsp;<bean:write name="accessLevelUpdatedResultElement" property="fileName" />&nbsp;</span>
			</html:link>
		</td>
		<td nowrap="nowrap">
			<span class="normal12">&nbsp;<bean:write name="accessLevelUpdatedResultElement" property="lastModifiedFormatted" />&nbsp;</span>
		</td>
		<td nowrap="nowrap">
			<span class="normal12">&nbsp;<bean:write name="accessLevelUpdatedResultElement" property="fileTypeDescription" />&nbsp;</span>
		</td>
	</tr>
	</logic:iterate>
</table>
</html:form>
</body>
</html:html>
