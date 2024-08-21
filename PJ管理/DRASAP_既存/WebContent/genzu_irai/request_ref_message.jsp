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
	<title>原図庫作業者からのメッセージ</title>
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<style type="text/css">@import url( <%=request.getContextPath() %>/default.css );</style>
</head>
<body bgcolor="#FFFFFF" bottommargin="0" leftmargin="0" topmargin="0" rightmargin="0" marginheight="0" marginwidth="0">
<font color="red" size="4" >
	<logic:iterate id="errs" name="request_refForm" property="listErrors">
		<li><bean:write name="errs" /></li>
	</logic:iterate>
</font>

<bean:define id="touroku" name="request_refForm" property="touroku" type="java.lang.String"/>
<br />
<table border="0" align="center">
	<tr bgcolor="#FFFF99">
		<td align="center"><span class="normal12">依頼ID</span></td>
		<td align="center"><span class="normal12">依頼内容</span></td>
	</tr>
	<tr>
		<td><span class="normal12">&nbsp;<bean:write name="request_refForm" property="job_id"/>&nbsp;</span></td>
		<td><span class="normal12">&nbsp;<bean:write name="request_refForm" property="job_name"/>&nbsp;</span></td>
	</tr>
</table>
<hr width="80%" />
<table border="0" align="center">
	<tr bgcolor="#CCCCCC">
		<td bgcolor="#FFFFFF"/>
		<td align="center"><span class="normal12">番号</span></td>
		<td align="center"><span class="normal12">号口・号機</span></td>
		<td align="center"><span class="normal12">原図内容</span></td>
		<td align="center"><span class="normal12">メッセージ</span></td>
	</tr>

<logic:iterate id="item" name="request_refForm" property="iraiList">
<%
	tyk.drasap.genzu_irai.Request_RefElement e = (tyk.drasap.genzu_irai.Request_RefElement)item;
	String irai = e.getJob_name();
	String start_no = e.getStart();//番号
	String gouki = e.getGouki();//号口・号機
	if(gouki == null){
		gouki = "";
	}
	String genzu = e.getGenzu();//原図内容
	if(genzu == null){
		genzu = "";
	}
	String messege = e.getMessege();//メッセージ
	if(messege == null){
		messege = "";
	}
	String gensi = "";
	String exist = e.getExist();//登録有無
	if("0".equals(exist)){
		gensi = "1";
	}
%>


	<tr>
<%	if("1".equals(gensi)){ %>
		<td bgcolor="#FF3300"><span class="normal12white">原紙なし</span></td>
<%	}else{ %>
		<td>&nbsp;</td>
<%	} %>
		<td><span class="normal12">&nbsp;<%= start_no %>&nbsp;</span></td>
		<td><span class="normal12">&nbsp;<%= gouki %>&nbsp;</span></td>
		<td><span class="normal12">&nbsp;<%= genzu %>&nbsp;</span></td>
		<td><span class="normal12">&nbsp;<%= messege %>&nbsp;</span></td>
	</tr>
</logic:iterate>
</table>
<br />
<hr width="80%" />
<center><input type="button" value="Close" onclick="javascript:window.close()" /></center>
</body>
</html:html>
