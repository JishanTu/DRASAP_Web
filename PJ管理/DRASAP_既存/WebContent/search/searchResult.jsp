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
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<title>Drawing Search and Print System [図面検索]</title>
	<script type="text/javascript">
	<!--
		document.onkeydown = keys;
		function keys(){
			switch (event.keyCode ){
				case 116: // F5
					event.keyCode = 0;
					return false;
					break;
			}
		}
	//-->
	</script>
</head>
<frameset rows="65,*,30" framespacing="0" border="0">
<frame name="result_head" src="switch.do?prefix=/search&amp;page=/searchResultHead.jsp" />
<frame name="result_body" src="switch.do?prefix=/search&amp;page=/searchResultBody.jsp" />
<frame name="result_foot" src="switch.do?prefix=/search&amp;page=/searchResultFoot.jsp" />
</frameset>
</html:html>
