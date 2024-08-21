<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%-- ログイン情報の確認 --%>
<logic:notPresent name="user" scope="session">
	<logic:redirect forward="timeout" />
</logic:notPresent>
<%-- アクセスレベル変更許可フラグがnullの場合、アクセス禁止 --%>
<logic:empty name="user" property="aclBatchUpdateFlag" scope="session">
	<logic:redirect action="accessLevelUpdatedResult" />
</logic:empty>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">
<html:html>
<head>
	<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
		<title>Drawing Search and Print System [アクセスレベル更新結果]</title>
		<meta http-equiv="Pragma" content="no-cache" />
		<meta http-equiv="Cache-Control" content="no-cache" />
	<script type="text/javascript">
	<!--
		browserName = navigator.appName;
		var WO1;
		var w = screen.availWidth;
		var h = screen.availHeight;
		var xPos = (screen.availWidth- w)/2;
		var yPos = (screen.availHeight - h)/2;
		window.resizeTo(w, h);
		window.moveTo(xPos,yPos);//画面の位置指定
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
<frameset rows="35,*" framespacing="0" border="0">
	<frame name="acl_result_head" src="<%=request.getContextPath() %>/system/accessLevelUpdatedResultHead.jsp" scrolling="no" />
	<frame name="acl_result_body" src="switch.do?prefix=&amp;page=/accessLevelUpdatedResult.do?act=init" scrolling="yes" />
</frameset>
</html:html>
