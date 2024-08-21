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
<body bgcolor="#FFCC99"><font style="font-family: 'ＭＳ Ｐゴシック','ＭＳ ゴシック';">
<font color="#FF0000">

<%		CookieManage langCookie = new CookieManage(); %>
<%      String lanKey = langCookie.getCookie (request, null, "Language"); %>
<%		if (lanKey.equals("English")) { %>
	<h2>DRASAP Connection Error</h2></font>
	<br />
	<ul>
		<li>NO righit to connect.</li>
		<li>There is incomplete information in user registration. Please contact your administrator.</li>
		<li>System error is also possible. Please contact your administrator.</li>
	</ul>
<%		} else { %>
	<h2>DRASAP接続エラー</h2></font>
	以下の理由で接続できません。<br />
	<ul>
		<li>ユーザーに接続する権限がありません。</li>
		<li>ユーザー登録に不備があります。管理者に連絡ください。</li>
		<li>その他システムエラーが発生した可能性があります。管理者に連絡ください。</li>
	</ul>
<%		} %>

<input type="button" value="Close" onclick="window.close()" />
<html:errors />
</body>
</html>
