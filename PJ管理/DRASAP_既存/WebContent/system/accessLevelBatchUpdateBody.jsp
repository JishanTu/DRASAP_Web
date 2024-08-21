<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%-- ログイン情報の確認 --%>
<logic:notPresent name="user" scope="session">
	<logic:redirect forward="timeout" />
</logic:notPresent>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN" "http://www.w3.org/tr/xhtml1/DTD/xhtml1-frameset.dtd">
<html:html>
<head>
	<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<title>アクセスレベル一括更新</title>
	<script type="text/javascript">
	<!--
		document.onkeydown = keys;
		function keys() {
			switch (event.keyCode ) {
				case 116: // F5
					event.keyCode = 0;
					return false;
					break;
			}
		}
	//-->
	</script>
</head>
<frameset rows="100,*" framespacing="0" border="0">
<frame name="acl_condition" src="<%=request.getContextPath() %>/system/accessLevelBatchUpdateCondition.jsp" />
<logic:present name="accessLevelBatchUpdate.erros" scope="session">
<frame name="acl_list" src="switch.do?prefix=&amp;page=/system/accessLevelBatchUpdate_error.jsp" />
</logic:present>
<logic:notPresent name="accessLevelBatchUpdate.erros" scope="session">
<frame name="acl_list" src="<%=request.getContextPath() %>/system//accessLevelBatchUpdateList.jsp" />
</logic:notPresent>
</frameset>
</html:html>
