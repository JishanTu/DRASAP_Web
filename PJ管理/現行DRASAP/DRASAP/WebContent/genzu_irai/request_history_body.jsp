<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="tyk.drasap.genzu_irai.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%-- ログイン情報の確認 --%>
<logic:notPresent name="user" scope="session">
	<logic:redirect forward="timeout" />
</logic:notPresent>

<html>
<head>
	<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
	<title>図面登録依頼履歴</title>
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<style type="text/css">@import url( <%=request.getContextPath() %>/default.css );</style>
</head>
<body bgcolor="#FFFFFF" bottommargin="0" leftmargin="0" topmargin="0" rightmargin="0" marginheight="0" marginwidth="0">
<font color="red" size="4" ><ul>
	<logic:iterate id="error" name="requestHistoryForm" property="errors" scope="session">
		<li><bean:write name="error"/></li>
	</logic:iterate>
	<%	// 検索結果がなければ表示する
		if(((RequestHistoryForm)session.getAttribute("requestHistoryForm")).getHistoryList().size() == 0){ %>
		<li>0件です。</li>
	<% } %>
</ul></font>
<table border="0" align="center">
	<tr bgcolor="#CCCCCC">
		<td align="center"><span class="normal10">完了日時</span></td>
		<td align="center"><span class="normal10">作業者</span></td>
		<td align="center"><span class="normal10">依頼日時</span></td>
		<td align="center"><span class="normal10">依頼内容</span></td>
		<td align="center"><span class="normal10">図番</span></td>
<%-- // 2019.10.23 yamamoto modified. start
		<td align="center"><span class="normal10">号口・号機</span></td>
		<td align="center"><span class="normal10">原図内容</span></td>
		<td align="center"><span class="normal10">部数</span></td>
		<td align="center"><span class="normal10">縮小</span></td>
		<td align="center"><span class="normal10">サイズ</span></td>
// 2019.10.23 yamamoto modified. end --%>
		<td align="center"><span class="normal10">メッセージ</span></td>
		<td align="center"><span class="normal10">依頼者</span></td>
		<td align="center"><span class="normal10">部署名</span></td>
	</tr>
	<logic:iterate id="history" name="requestHistoryForm" property="historyList" scope="session">
		<tr>
			<td nowrap="nowrap"><span class="normal10"><bean:write name="history" property="completeDate" /></span></td>
			<td nowrap="nowrap" align="center"><span class="normal10"><bean:write name="history" property="completeUser" /></span></td>
			<td nowrap="nowrap" align="center"><span class="normal10"><bean:write name="history" property="requestDate" /></span></td>
			<td nowrap="nowrap" align="center"><span class="normal10"><bean:write name="history" property="jobName" /></span></td>
			<td nowrap="nowrap" align="center"><span class="normal10"><bean:write name="history" property="drwgNo" /></span></td>
<%-- // 2019.10.23 yamamoto modified. start
			<td nowrap="nowrap" align="center"><span class="normal10"><bean:write name="history" property="goukiNo" /></span></td>
			<td nowrap="nowrap" align="center"><span class="normal10"><bean:write name="history" property="genzuContent" /></span></td>
			<td nowrap="nowrap" align="center"><span class="normal10"><bean:write name="history" property="copies" /></span></td>
			<td nowrap="nowrap" align="center"><span class="normal10"><bean:write name="history" property="scaleMode" /></span></td>
			<td nowrap="nowrap" align="center"><span class="normal10"><bean:write name="history" property="scaleSize" /></span></td>
// 2019.10.23 yamamoto modified. end --%>
			<td nowrap="nowrap" align="center"><span class="normal10"><bean:write name="history" property="message" /></span></td>
			<td nowrap="nowrap" align="center"><span class="normal10"><bean:write name="history" property="requestUser" /></span></td>
			<td nowrap="nowrap" align="center"><span class="normal10"><bean:write name="history" property="deptName" /></span></td>
		</tr>
	</logic:iterate>
</table>
</body>
</html>
