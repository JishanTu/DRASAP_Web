<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="tyk.drasap.search.*,tyk.drasap.common.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<html>
<head>
	<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<title>Drawing Search and Print System [Failed]</title>
</head>
<body bgcolor="#FF9966"><font color="#FFFF00" style="font-family: 'ＭＳ Ｐゴシック','ＭＳ ゴシック';">
<%		CookieManage langCookie = new CookieManage(); %>
<%      String lanKey = langCookie.getCookie (request, null, "Language"); %>
<%		if (lanKey.equals("English")) { %>
	The page you accessed is not currently available.<br />
	Please ask your administrator.<br />
<%		} else { %>
アクセスしたログインページは現在中止となっております。<br />
管理者にご確認ください<br />
<%		} %>
</font></body>
</html>
