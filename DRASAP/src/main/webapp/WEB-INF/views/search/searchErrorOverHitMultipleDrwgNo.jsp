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
	function setAct(param){
		document.forms[0].act.value=param;// 隠し属性actにセット
	}
	function onLoad() {
		if (parent.condition != null) {
			parent.condition.unLockButtons();
		}
	}
	function nowSearch(){
		var nowSearch;
		nowSearch = document.getElementById("nowSearch");
		nowSearch.style.visibility = "visible";
	}
	//-->
	</script>
</head>
<body style="background-color: #FFFFFF; margin: 0;" onload="onLoad();">
<center>
<br />
<logic:equal value="Japanese" name="user" property="language" scope="session">
    <font color="#FF0000" style="font-size:12pt">警告</font>
    <hr style="width: 50%; color: #FF6600" />
    <span class="normal12">
    1度の検索で指定可能な図番の件数は
    <logic:present name="drasapInfo" scope="session" >
	    <bean:write name="drasapInfo" property="multipleDrwgNoMax" scope="session" />
    </logic:present>
    件です。<br />
    図番の件数を
    <logic:present name="drasapInfo" scope="session" >
	    <bean:write name="drasapInfo" property="multipleDrwgNoMax" scope="session" />
    </logic:present>
    件以下にしてください。<br />
    </span>
</logic:equal>
<logic:notEqual value="Japanese" name="user" property="language" scope="session">
    <font color="#FF0000" style="font-size:12pt">Warning</font>
    <hr style="width: 50%; color: #FF6600;" />
    <span class="normal12">
    Avaiable number of drawings for each search are
    <logic:present name="drasapInfo" scope="session" >
	    <bean:write name="drasapInfo" property="multipleDrwgNoMax" scope="session" />
    </logic:present>
   .<br />
    Please specific multiple drowing no under
    <logic:present name="drasapInfo" scope="session" >
	    <bean:write name="drasapInfo" property="multipleDrwgNoMax" scope="session" />
    </logic:present>
    .<br />
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
</body>
</html:html>
