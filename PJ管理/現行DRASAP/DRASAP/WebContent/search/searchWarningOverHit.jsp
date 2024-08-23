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
	<title>Drawing Search and Print System [図面検索]</title>
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<style type="text/css">@import url( <%=request.getContextPath() %>/<bean:write name="default_css" scope="session" /> );</style>
	<script type="text/javascript">
	<!--
	// 隠し属性actにセットする
	function setAct(param) {
		if (param == 'continue') {<%-- 2013.09.05 yamagishi.
			if (parent.condition != null) {
				parent.condition.lockButtons();
			} --%>
			nowSearch();
		}
		document.forms[0].act.value=param;// 隠し属性actにセット
	}
	function onLoad() {
		if (parent.condition != null) {
			parent.condition.unLockButtons();
		}
	}
	function nowSearch() {
		var nowSearch;
		nowSearch = document.getElementById("nowSearch");
		nowSearch.style.visibility = "visible";
	}<%-- 2013.09.05 yamagishi add. start --%>
	function lockButtons() {
		if (parent.condition != null) {
			parent.condition.lockButtons();
		}
	}<%-- 2013.09.05 yamagishi add. end --%>
	//-->
	</script>
</head>
<body bgcolor="#FFFFFF" bottommargin="0" leftmargin="0" topmargin="0" rightmargin="0" marginheight="0" marginwidth="0" onload="onLoad();">
<html:form action="/warningOverHit" target="result">
<html:hidden property="act" />
<%-- 次の実際に検索するActionで使用するためのデータを渡す --%>
<%--
	// 何故かWebLogicでは
	// <%= 式 %>を含んだhtml:hiddenタグが input type="hidden"に変換されなかったので
	// 直接<input type="hidden">で記述することにした。 by Hirata 2004.Mar.15
<html:hidden property="sqlWhere" value="<%= (String)request.getAttribute("SQL_WHERE") %>" />
<html:hidden property="sqlOrder" value="<%= (String)request.getAttribute("SQL_ORDER") %>" />
--%>
<input type="hidden" name="sqlWhere" value="<%=(String)request.getAttribute("SQL_WHERE")%>" />
<input type="hidden" name="sqlOrder" value="<%=(String)request.getAttribute("SQL_ORDER")%>" />
<center>
<br />
<logic:equal value="Japanese" name="user" property="language" scope="session">
    <font color="#FF0000" style="font-size:12pt">警告</font>
    <hr width="50%" color="#FF6600" />
    <span class="normal12">
    指定した検索条件にヒットする図番は&nbsp;
    <logic:present name="hit" scope="request" >
    	<bean:write name="hit" scope="request" />
    </logic:present>
    件です。<br />
    この件数の表示には長時間かかります。<br />
    このまま表示を続けますか?<br />
    <br />
    <html:submit onclick="setAct('cancel')">中止する</html:submit>&nbsp;&nbsp;&nbsp;&nbsp;
    <html:submit onclick="setAct('continue')">続ける</html:submit>
    </span>
</logic:equal>
<logic:notEqual value="Japanese" name="user" property="language" scope="session">
    <font color="#FF0000" style="font-size:12pt">Warning</font>
    <hr width="50%" color="#FF6600" />
    <span class="normal12">
    There are &nbsp;
    <logic:present name="hit" scope="request" >
    	<bean:write name="hit" scope="request" />
    </logic:present>
    drawings ｔhat meet the search condition.<br />
    It will take a while to view all of them.<br />
    Do you want to continue ?<br />
    <br />
    <%-- 2013.09.06 yamagishi modified. start
    <html:submit onclick="setAct('cancel')">STOP</html:submit>&nbsp;&nbsp;&nbsp;&nbsp;
    <html:submit onclick="setAct('continue')">CONTINUE</html:submit> --%>
    <html:submit onclick="setAct('cancel');form.submit();">STOP</html:submit>&nbsp;&nbsp;&nbsp;&nbsp;
    <html:submit onclick="setAct('continue');form.submit();lockButtons();">CONTINUE</html:submit>
    <%-- 2013.09.06 yamagishi modified. end --%>
    </span>
</logic:notEqual>

</center>
<table class="nowsearch" id="nowSearch" style="visibility:hidden">
<tr valign="middle">
<td align="center" style="font-size:18pt;color:#0000FF;">
<logic:equal value="Japanese" name="user" property="language" scope="session">
検索中・・・・
</logic:equal>
<logic:notEqual value="Japanese" name="user" property="language" scope="session">
Now Searching...
</logic:notEqual>
</td>
</tr>
</table>
</html:form>
</body>
</html:html>
