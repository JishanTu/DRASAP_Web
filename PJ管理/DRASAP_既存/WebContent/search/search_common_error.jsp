<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<html:html>
<head>
<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Cache-Control" content="no-cache" />
<title>Drawing Search and Print System [エラー]</title>
<style type="text/css">@import url( <%=request.getContextPath() %>/<bean:write name="default_css" scope="session" /> );</style>
<script type="text/javascript">
<!--
	function backSearchResult(){
		//alert(parent.location.href);
		location.href = "./searchResult.jsp";
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
<body onload="onLoad();">
<!-- エラーの表示 -->
<html:errors />
<div align="center">
<input type="button" value="戻る" onclick="backSearchResult()"  style="font-size:12pt;" />
</div>
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
