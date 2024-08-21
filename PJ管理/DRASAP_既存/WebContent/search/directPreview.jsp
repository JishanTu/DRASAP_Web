<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.*,java.net.URLEncoder" %>
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
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<title>Drawing Search and Print System [プレビュー]</title>
</head>
<body bgcolor="#FFFFFF"><font style="font-family: 'ＭＳ Ｐゴシック','ＭＳ ゴシック';">
<br />
<center>
<!-- タイトル -->
<b><font size="5" color="#0033CC">DRASAP</font></b>&nbsp;&nbsp;&nbsp;&nbsp;
<font color="#0066FF">Drawing Search and Print System</font>
<!-- エラーの表示 -->
<html:errors />
<logic:notPresent name="hasError" scope="request"><!-- エラーでないときに表示 -->
	<hr />
</logic:notPresent>
<br />
<input type="button" value="Close" onclick="window.close()" />
</center>
</font></body>
</html>
