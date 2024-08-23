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
	<title>Drawing Search and Print System [図面登録依頼リスト]</title>
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<style type="text/css">@import url( <%=request.getContextPath() %>/default.css );</style>
</head>

<body bgcolor="#FFFFFF" bottommargin="0" leftmargin="0" topmargin="0" rightmargin="0" marginheight="0" marginwidth="0">
<br />
<font color="red" size="4" >
	<logic:iterate id="errs" name="request_listForm" property="listErrors">
		<li><bean:write name="errs" /></li>
	</logic:iterate>
</font>
<center><span class="normal12">図面登録依頼リスト</span><span class="normal10">&nbsp;&nbsp;<bean:write name="request_listForm" property="time" /></span></center>
<table border="0" align="center">
	<tr bgcolor="#CCCCCC">
		<td align="center"><span class="normal10">依頼ID</span></td>
		<td align="center"><span class="normal10">依頼内容</span></td>
		<td align="center"><span class="normal10">図番</span></td>
<%-- // 2019.10.23 yamamoto modified. start
		<td align="center"><span class="normal10">号口・号機</span></td>
		<td align="center"><span class="normal10">原図内容</span></td>
// 2019.10.23 yamamoto modified. end --%>
		<td align="center"><span class="normal10">依頼者</span></td>
		<td align="left"><span class="normal10">部署名</span></td>
	</tr>
	<logic:iterate id="print" name="request_listForm" property="printList">
<%
		tyk.drasap.genzu_irai.RequestPriElement e = (tyk.drasap.genzu_irai.RequestPriElement)print;
		String job_id = e.getJob_id();//依頼ID
		String job_name = e.getJob_Name();//依頼内容
		String bangou = e.getNumber();//番号
		if(bangou == null){
			bangou = "";
		}
		String gouki = e.getGouki();//号口・号機
		if(gouki == null){
			gouki = "";
		}
		String genzu = e.getGenzu();//原図内容
		if(genzu == null){
			genzu = "";
		}
		String irai = e.getIrai();//依頼者
		String busyo = e.getBusyo();//部署名
%>
	<tr>
		<td align="center"><span class="normal10">&nbsp;<%= job_id %>&nbsp;</span></td>
		<td align="center"><span class="normal10">&nbsp;<%= job_name %>&nbsp;</span></td>
		<td align="center"><span class="normal10">&nbsp;<%= bangou %>&nbsp;</span></td>
<%-- // 2019.10.23 yamamoto modified. start
		<td align="center"><span class="normal10">&nbsp;<%= gouki %>&nbsp;</span></td>
		<td align="center"><span class="normal10">&nbsp;<%= genzu %>&nbsp;</span></td>
// 2019.10.23 yamamoto modified. end --%>
		<td align="center"><span class="normal10">&nbsp;<%= irai %>&nbsp;</span></td>
		<td align="left"><span class="normal10">&nbsp;<%= busyo %>&nbsp;</span></td>
	</tr>
	</logic:iterate>
</table>
</body>
</html:html>
