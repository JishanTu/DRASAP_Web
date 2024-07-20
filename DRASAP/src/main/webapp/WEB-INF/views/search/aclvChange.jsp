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
	<title>Drawing Search and Print System [アクセスレベル変更]</title>
	<script type="text/javascript">
	<!--
		var browserName = navigator.appName;
		function onLoad() {
                    var w = screen.availWidth;
                    var h = screen.availHeight;
                    mx = 0;
                    my = 0;
                    window.resizeTo(w, h);
                    window.moveTo(mx,my);//画面の位置指定
                    if (browserName != "Netscape") focus();
		}
	//-->
	</script>
</head>
<frameset rows="75,*,30" framespacing="0" border="0" onload="onLoad()">
<frame name="aclv_change_head" src="switch.do?prefix=/search&amp;page=/aclvChangeHead.jsp" />
<frame name="aclv_change_body" src="switch.do?prefix=/search&amp;page=/aclvChangeBody.jsp" />
<frame name="aclv_change_foot" src="switch.do?prefix=/search&amp;page=/aclvChangeFoot.jsp" />
</frameset>
</html:html>
