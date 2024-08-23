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
	<title>Drawing Search and Print System [アクセスレベル変更前確認]</title>
</head>
<frameset rows="75,*,30" framespacing="0" border="0">
<frame name="aclv_changeConfirm_head" src="switch.do?prefix=/search&amp;page=/aclvChangeConfirmHead.jsp" />
<frame name="aclv_changeConfirm_body" src="switch.do?prefix=/search&amp;page=/aclvChangeConfirmBody.jsp" />
<frame name="aclv_changeConfirm_foot" src="switch.do?prefix=/search&amp;page=/aclvChangeConfirmFoot.jsp" />
</frameset>
</html:html>
