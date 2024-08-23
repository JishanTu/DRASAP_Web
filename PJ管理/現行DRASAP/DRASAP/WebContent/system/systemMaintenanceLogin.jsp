<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<html:html>
<head>
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Cache-Control" content="no-cache" />
<title>システムメンテナンス [ログイン]</title>
<meta http-equiv="Content-type" content="text/html;charset=UTF-8" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Cache-Control" content="no-cache" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/default.css" />
<style type="text/css">
	.loginFrame {
		background-color:#EEEEEE;
		width:70%;
		border-left:2px solid #FFFFFF;
		border-top:2px solid #FFFFFF;
		border-right:2px solid #777777;
		border-bottom:2px solid #777777;
	}
</style>
<script type="text/javascript">
<!--
	browserName = navigator.appName;

	function doLogin(){
		this.disabled=true;
		document.body.style.cursor = 'wait';
		document.forms[0].act.value='login';// 隠し属性actをセット
		document.forms[0].submit();
		return;
	}
	function moveWindow(){
		var w = 711.1;
		var h = 662.6;
		var xPos = (screen.availWidth- w)/2.0;
		var yPos = (screen.availHeight - h)/2.0;
		window.resizeTo(w, h);
		window.moveTo(xPos,yPos);//画面の位置指定
		if (browserName != "Netscape") focus();
		onInitFocus();
	}
	function onInit(){
		onInitFocus();
	}
	// 初期フォーカス位置
	function onInitFocus(){
		document.forms[0].id.focus();
	}
	//--->
</script>
</head>
<body bgcolor="#F5F5DC" onload="onInit()">
<html:form action="/systemMaintenanceLogin">
<html:hidden property="act" />
<table width="100%" style="height:100%;">
  <tr bgcolor="#ff2233">
  </tr>
  <tr>
    <td valign="top" align="center" style="height:90%;">
      <table style="width:60%;" border="0" cellpadding="0" cellspacing="0" align="center" style="margine-top:100px;">
        <tr>
          <td align="center">
            <table cellpadding="6" class="loginFrame">
              <tr>
                <td colspan="2" style="background-color:#7A79A7;padding-left:10px;color:#FFFFFF;">DRASAP システム管理にログインします&nbsp;</td>
              </tr>
              <tr>
                <td align="right" valign="middle" nowrap="nowrap">user ID :</td>
                <td align="left" valign="middle"><html:text property="id" style="width:180px;"/></td>
              </tr>
              <tr>
                <td align="right" valign="middle" nowrap="nowrap">passwd : </td>
                <td align="left" valign="middle"><html:password property="passwd" style="width:180px;"/></td>
              </tr>
              <tr>
                <td colspan="2" align="left">
			<html:submit onclick="doLogin()">ログイン</html:submit>
		</td>
              </tr>
            </table>
          </td>
        </tr>
  <tr>
    <td><html:errors />
    </td>
  </tr>
      </table>
    </td>
  </tr>
</table>
</html:form>
</body>
</html:html>

