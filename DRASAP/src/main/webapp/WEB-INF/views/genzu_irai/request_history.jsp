<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%-- ログイン情報の確認 --%>
<logic:notPresent name="user" scope="session">
	<logic:redirect forward="timeout" />
</logic:notPresent>

<html>
	<head>
	<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
		<title>Drawing Search and Print System [図面登録依頼履歴]</title>
		<meta http-equiv="Pragma" content="no-cache" />
		<meta http-equiv="Cache-Control" content="no-cache" />
	</head>

<frameset rows="45,*" framespacing="0" border="0">
<frame name="list_head" src="<%=request.getContextPath() %>/genzu_irai/request_history_head.jsp" />
<frame name="list_body" src="<%=request.getContextPath() %>/genzu_irai/request_history_body.jsp" />
</frameset>
</html>
