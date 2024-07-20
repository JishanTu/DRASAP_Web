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
<!--
.deleteBtn {
	width:100%;
	position:absolute;
	top:10px;
	text-align:center;
}
.goBackBtn {
	width:100%;
	position:absolute;
	top:10px;
	text-align:right;
	padding-right:30px;
}
-->
</style>
<script type="text/javascript">
<!--
    browserName = navigator.appName;
    function onLoad() {
        parent.parent.condition.document.body.style.cursor="";
    }
    // 遷移する
    function submitFunc(parm){
        if (parm == "DELETE") {
            document.body.style.cursor="wait";
            parent.delete_head.document.body.style.cursor="wait";
            parent.delete_body.document.body.style.cursor="wait";
            parent.parent.condition.document.body.style.cursor="wait";
            var deleteButton = document.getElementById("deleteButton");
            var backButton = document.getElementById("backButton");
            deleteButton.disabled=true;
            backButton.disabled=true;
    	    parent.delete_body.document.forms[0].act.value='delete';// 隠し属性actをセット
	    parent.delete_body.document.forms[0].target="_parent";
    	    parent.delete_body.document.forms[0].submit();
        }
    }
    function backSearchResult(){
	parent.parent.condition.document.forms[0].act.value="search";// 隠し属性actに'search'をセット
	parent.parent.condition.document.forms[0].target="result";// targetは'result'
	parent.parent.condition.document.forms[0].submit();
//        parent.location.href = "switch.do?prefix=/search&amp;page=/searchResult.jsp";
    }
//-->
</script>
</head>
<body marginwidth="0" marginheight="0" bgcolor="#ffffff" rightmargin="0" topmargin="0" leftmargin="0" bottommargin="0" onload="onLoad()">
<bean:define id="deleteDwgForm" type="tyk.drasap.search.DeleteDwgForm" name="deleteDwgForm" scope="session" />
<span class="deleteBtn" ><input type="button" style="width:80px;" onclick="submitFunc('DELETE')" value="削除" id="deleteButton" <%=(deleteDwgForm.isDeleteOK()?"":"disabled") %> /></span>
<span class="goBackBtn" ><input type="button" onclick="backSearchResult()" value="　戻る　" id="backButton" /></span>
</body>
</html:html>

