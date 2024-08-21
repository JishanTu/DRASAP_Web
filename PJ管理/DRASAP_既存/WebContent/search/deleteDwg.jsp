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
<style type="text/css">
.deleteBtn {
	width:100%;
/*	2022.04.13 Windows Edge対応. 削除ボタンが押せない障害を修正.
	position:absolute;
*/
	text-align:center;
}
.goBackBtn {
	width:100%;
	position:absolute;
	text-align:right;
	padding-right:30px;
	padding-bottom:30px;
}
-->
</style>
<script type="text/javascript">
<!--
browserName = navigator.appName;
// 遷移する
function submitFunc(parm){
    if (parm == "DELETE") {
	var msg1 = document.getElementById("msg1");
	var msg2 = document.getElementById("msg2");
	msg1.innerHTML = "削除しています。しばらくお待ちください。";
	msg2.innerHTML = "";

        document.body.style.cursor="wait";
        var deleteButton = document.getElementById("deleteButton");
        var backButton = document.getElementById("backButton");
        deleteButton.disabled=true;
        backButton.disabled=true;
	document.forms[0].act.value='delete';// 隠し属性actをセット
	document.forms[0].submit();
    }
}
// ログアウト処理
function doLogout(){
}
    function backSearchResult(){
	parent.condition.document.forms[0].act.value="search";// 隠し属性actに'search'をセット
	parent.condition.document.forms[0].target="result";// targetは'result'
	parent.condition.document.forms[0].submit();
//        parent.location.href = "switch.do?prefix=/search&amp;page=/searchResult.jsp";
    }
//-->
</script>
</head>
<body marginwidth="0" marginheight="0" bgcolor="#ffffff" rightmargin="0" topmargin="0" leftmargin="0" bottommargin="0">
<bean:define id="deleteDwgForm" type="tyk.drasap.search.DeleteDwgForm" name="deleteDwgForm" scope="session" />
<html:form action="/deleteDwg">
<nested:root name="deleteDwgForm">
<table cellspacing="0" cellpadding="0" border="0">
<tbody>
<tr>
    <td>
	<html:hidden property="act" /><br/>
	<html:hidden property="previewIdx" />
    </td>
</tr>
<tr>
    <td>削除図面</td>
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
    <html:errors />
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
	<%-- 2022.04.13 Windows Edge対応. 削除ボタンが押せない障害を修正. --%>
    <tr><td align="center">
    <input type="button" style="width: 80px; margin-right:20px;" onclick="submitFunc('DELETE')" value="削除" id="deleteButton" <%=(deleteDwgForm.isDeleteOK()?"":"disabled") %> />
    <input type="button" onclick="backSearchResult()" value="　戻る　" id="backButton"  />
    </td></tr>

    </tbody>
    </table>


<%-- 2022.04.13 Windows Edge対応. 削除ボタンが押せない障害を修正.
<span class="deleteBtn" ><input type="button" style="width: 80px;" onclick="submitFunc('DELETE')" value="削除" id="deleteButton" <%=(deleteDwgForm.isDeleteOK()?"":"disabled") %> /></span>
<span class="goBackBtn" ><input type="button" onclick="backSearchResult()" value="　戻る　" id="backButton"  /></span>
 --%>    
</nested:root>
</html:form>
</body>
</html:html>

