<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="tyk.drasap.search.*,tyk.drasap.common.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/struts-nested" prefix="nested" %>
<%-- ログイン情報の確認 --%>
<logic:notPresent name="user" scope="session">
	<logic:redirect forward="timeout" />
</logic:notPresent>
<html:html>
<head>
    <meta content="text/html; charset=UTF-8" http-equiv="Content-type" />
    <meta content="no-cache" http-equiv="Pragma" />
    <meta content="no-cache" http-equiv="Cache-Control" />
    <style type="text/css">@import url( <%=request.getContextPath() %>/default.css );</style>
</head>
<body marginwidth="0" marginheight="0" bgcolor="#ffffff" rightmargin="0" topmargin="0" leftmargin="0" bottommargin="0">
<bean:define id="deleteDwgForm" type="tyk.drasap.search.DeleteDwgForm" name="deleteDwgForm" scope="session" />
<html:form action="/deleteDwg">
<nested:root name="deleteDwgForm">
<table cellspacing="0" cellpadding="0" border="0">
<tbody>
<tr>
    <td>
	<html:hidden property="act" />
	<html:hidden property="previewIdx" />
    </td>
</tr>
<tr>
<td>
    <table cellspacing="0" cellpadding="3" border="1" align="left">
    <tbody>
    <tr bgcolor="#cccccc" class="normal10">
		<nested:iterate id="colNameList" type="java.lang.String"
			name="deleteDwgForm" property="colNameJPList" scope="session">
			<th nowrap="nowrap"><nested:write /></th>
		</nested:iterate>
    </tr>
    <nested:iterate id="RecList" type="tyk.drasap.search.DeleteDwgElement" indexId="idx"
	name="deleteDwgForm" property="recList" scope="session">
	<tr class="normal12">
		<nested:iterate id="ValList" type="java.lang.String" indexId="col_Idx"
		 property="valList" scope="session">
			<td nowrap="nowrap">
			<%if(col_Idx.intValue() == 0) {%>
			<html:link forward="preview" name="RecList" property="linkDwgParmMap" target="_parent">
				<nested:write /><br/>
			</html:link>
			<%} else {%>
			<nested:write /><br/>
			<%}%>
			</td>
		</nested:iterate>
	</tr>
    </nested:iterate>
    </tbody>
    </table>
</td>
</tr>
<tr>
<td>
    <br />
    <br />
</td>
</tr>
<tr>
<td>
</td>
</tr>
</tbody>
</table>
    <table cellspacing="0" cellpadding="0" border="0" align="center" class="normal12blue">
    <tbody>
    <tr>
        <td id="msg1"><nested:write property="msg1" /></td>
    </tr>
    <tr>
        <td id="msg2"><nested:write property="msg2" /></td>
    </tr>
    </tbody>
    </table>
</nested:root>
</html:form>
</body>
errors<html:errors />
</html:html>

