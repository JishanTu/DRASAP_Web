<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.net.*, tyk.drasap.common.*, tyk.drasap.search.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%-- ログイン情報の確認 --%>
<logic:notPresent name="user" scope="session">
	<logic:redirect forward="timeout" />
</logic:notPresent>
<bean:define id="user" type="User" name="user" scope="session" />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
<logic:equal name="user" property="language" value="Japanese">
<title>DLマネージャ</title>
</logic:equal>
<logic:notEqual name="user" property="language" value="Japanese">
<title>DL Manager</title>
</logic:notEqual>
<meta http-equiv="Pragma" content="no-cache"></meta>
<meta http-equiv="Cache-Control" content="no-cache"></meta>
<style type="text/css">
<!--
body {
	margin:0px;
	padding:0px;
}
applet {
	margin:0px;
	padding:0px;
}
-->
</style>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/deployJava.js"></script>
<script type="text/javascript">
<!--
	// ロード時に実行する。
	runDLManager();

	function onLoad() {
		moveWindow();
	}
	function moveWindow(){
		try {
			window.name = '_DLManager';
			var w = 300.0;
			var h = 100.0;
			window.resizeTo(w, h);
		} catch (e) {<%-- 二重起動対策として、以下のエラーは無視 --%>
			if (e.number != -2147024891) { // アクセスが拒否されました
				throw e;
			}
		}
	}
	<%	int searchIndex = Integer.parseInt(request.getParameter("searchIndex"));
		SearchResultElement dlmInfo = ((SearchResultForm) session.getAttribute("searchResultForm")).getSearchResultElement(searchIndex);
		URL url = new URL(request.getRequestURL().toString()); %>
	function runDLManager() {
		var attributes = {
			code:'oce.applet.DLManager.class',
			archive:'../DLManager/DLManager.jar, ../DLManager/httpclient-4.0.jar, ../DLManager/httpcore-4.0.1.jar, ../DLManager/httpmime-4.0.jar, ../DLManager/apache-mime4j-0.6.jar, ../DLManager/commons-codec-1.3.jar, ../DLManager/commons-logging-1.1.1.jar',
			width:'300',
			height:'100',
			style:'margin:0px;padding:0px;'
		};
		var parameters = {
			jnlp_href:'<%=request.getContextPath() %>/DLManager/DLManager.jnlp',
			downloadUrl:'<%= (url.getProtocol() + "://" + url.getHost() + ":" + url.getPort() + request.getContextPath() + "/search/switch.do") %>',
			prefix:'/search',
			page:'/preview.do',
			drwgSize:'<%= dlmInfo.getPrintSize() %>',
			pdf:'<%= dlmInfo.getLinkParmMap().get("PDF") %>',
			fileName:'<%= dlmInfo.getFileName() %>',
			pathName:'<%= URLEncoder.encode(dlmInfo.getPathName(), "UTF-8") %>',
			drwgNo:'<%= dlmInfo.getDrwgNo() %>',
			saveFlag:'<%= user.getDLManagerSaveEnabledFlag() %>',
			language:'<%= user.getLanguage() %>',
			domain:'<%= url.getHost() %>',
			sid:'<%= session.getId() %>'
		};
		var version = '1.6';
		deployJava.setInstallerType('kernel');
		deployJava.runApplet(attributes, parameters, version);
	}
//-->
</script>
</head>
<body onload="onLoad()" style='background-color: #D4D0C8;'>
</body>
</html>
