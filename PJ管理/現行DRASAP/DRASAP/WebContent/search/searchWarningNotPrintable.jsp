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
<html:form action="/warningNotPrintable" >
<html:hidden property="act" />
<center>
<br />
<logic:equal value="Japanese" name="user" property="language" scope="session">
    <font color="#FF0000" style="font-size:12pt">警告</font>
    <hr style="width: 50%; color: #FF6600;" />
    <span class="normal10">
    指定した図番の中に、出力できない図番(Tiffでない、印刷権を持たない)が含まれます。<br />
    出力可能な図番のみ出力しますか?<br />
    図番の指定をやり直す場合は「戻る」をクリックして下さい。<br />
    <br />
    <html:submit onclick="setAct('continue')" >出力する</html:submit>
    &nbsp;&nbsp;&nbsp;&nbsp;
    <html:submit onclick="setAct('backResult')" >戻る</html:submit>
    </span>
</logic:equal>
<logic:notEqual value="Japanese" name="user" property="language" scope="session">
    <font color="#FF0000" style="font-size:12pt">Warning</font>
    <hr style="width: 50%; color: #FF6600;" />
    <span class="normal10">
    Among the requested drawings.<br />
    Unavailable drawings(non-TIFF or no printing right)are included.<br />
    Do you want available drawing only ?<br />
    Please click "return" if you want to start again.<br />
    <br />
    <html:submit onclick="setAct('continue')" >OUTPUT</html:submit>
    &nbsp;&nbsp;&nbsp;&nbsp;
    <html:submit onclick="setAct('backResult')" >RETURN</html:submit>
    </span>
</logic:notEqual>

</center>
</html:form>
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
