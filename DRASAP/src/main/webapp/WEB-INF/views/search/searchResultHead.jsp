<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="tyk.drasap.search.*,tyk.drasap.common.*" %>
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
	<title>Drawing Search and Print System [図面検索]</title>
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<style type="text/css">@import url( <%=request.getContextPath() %>/<bean:write name="default_css" scope="session" /> );</style>
	<style type="text/css">
	.management {
	    position: absolute;
	    top:0px;
	    right:0px;
	    margin-right:10px;
	}
	</style>
	<script type="text/javascript">
	<!--
		document.onkeydown = keys;
		function keys(){
			switch (event.keyCode ){
				case 116: // F5
					event.keyCode = 0;
					return false;
					break;
			}
		}
		// 再表示
		function doRefresh(){
			// 表示属性をresult_bodyフレーム隠し属性にセット
			parent.result_body.document.forms[0].dispAttr1.value=document.forms[0].dispAttr1.value;
			parent.result_body.document.forms[0].dispAttr2.value=document.forms[0].dispAttr2.value;
			parent.result_body.document.forms[0].dispAttr3.value=document.forms[0].dispAttr3.value;
			parent.result_body.document.forms[0].dispAttr4.value=document.forms[0].dispAttr4.value;
			parent.result_body.document.forms[0].dispAttr5.value=document.forms[0].dispAttr5.value;
			parent.result_body.document.forms[0].dispAttr6.value=document.forms[0].dispAttr6.value;
			parent.result_body.document.forms[0].outputPrinter.value=document.forms[0].outputPrinter.value;
			parent.result_body.document.forms[0].act.value="REFRESH";// 隠し属性actにREFRESHをセット
			parent.result_body.document.forms[0].target="_parent";// ターゲットは親
			parent.result_body.document.forms[0].submit();
		}
		// 全てチェック
		function checkOnAll(){
			parent.result_body.document.forms[0].outputPrinter.value=document.forms[0].outputPrinter.value;
			parent.result_body.document.forms[0].act.value="CHECK_ON";// 隠し属性actにCHECK_ONをセット
			parent.result_body.document.forms[0].target="_parent";// ターゲットは親
			parent.result_body.document.forms[0].submit();
		}
		// 全てのチェックを外す
		function checkOffAll(){
			parent.result_body.document.forms[0].outputPrinter.value=document.forms[0].outputPrinter.value;
			parent.result_body.document.forms[0].act.value="CHECK_OFF";// 隠し属性actにCHECK_OFFをセット
			parent.result_body.document.forms[0].target="_parent";// ターゲットは親
			parent.result_body.document.forms[0].submit();
		}
		function management() {
			targetName = '_drasap_management_login';
			targetUrl = '<%=request.getContextPath() %>/switch.do?prefix=/search&page=/management_Login.jsp';
			var WO1;
			var w = screen.availWidth;
			var h = screen.availHeight-50;

			WO1=window.open(targetUrl, targetName,
						'toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes,width=' + w + ',height=' + h);
			WO1.window.moveTo(0,0);//画面の位置指定
			WO1.focus();
		}
		<%-- 2013.07.16 yamagishi add. start --%>
		function disableOnContextMenu() {
			return false;
		}
		function disableContextMenu(ev) {
			if (ev) {
				if (ev.button && ev.button == 2) { // W3C DOM2
					return false;
				} else if (!ev.button && ev.which == 3) { // N4
					return false;
				} else if (navigator.platform.indexOf("Mac") != -1 && navigator.appName == "Netscape") {
					return false;
				}
			} else {
				if (event && event.button && event.button == 2) { // IE
					return false;
				}
			}
		}
		function onLoad() {
			if (navigator.appName == "Netscape" && !(navigator.platform.indexOf("Mac") != -1)) {
				document.captureEvents(Event.MOUSEDOWN);
			}
			// 右クリック禁止
			document.onmousedown = disableContextMenu;
			document.oncontextmenu = disableOnContextMenu;
		}
		<%-- 2013.07.16 yamagishi add. end --%>
	//-->
	</script>
</head>
<%-- 2013.07.16 yamagishi modified.
<body bgcolor="#CCCCCC" bottommargin="0" leftmargin="0" topmargin="0" rightmargin="0" marginheight="0" marginwidth="0"> --%>
<body style="background-color: #CCCCCC; margin: 0;" onload="onLoad()">
<html:form action="/result" >
<!--================ ヘッダ ==================================-->
<bean:define id="searchResultForm" type="SearchResultForm" name="searchResultForm" scope="session" />
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td>
			<table border="0" bgcolor="#EEEEEE">
				<tr><td nowrap="nowrap"><span class="normal10">
					<b><bean:write name="searchResultForm" property="h_label1"/></b></span></td></tr>
			</table></td>
		<td><span class="normal10">
			<input type="button" value="<%=searchResultForm.getH_label2()%>" onclick="checkOnAll()" />
			<input type="button" value="<%=searchResultForm.getH_label3()%>" onclick="checkOffAll()" />
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="button" value="<%=searchResultForm.getH_label4()%>" onclick="doRefresh()" />
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<%=searchResultForm.getH_label5()%>&nbsp;<html:select property="outputPrinter">
				<html:options labelName="searchResultForm" labelProperty="printerNameList"
								name="searchResultForm" property="printerKeyList" />
			</html:select></span></td>
	</tr>
</table>
<%// admin_flag='2'のユーザのみ
// ボタンを表示する
User me = (User) session.getAttribute("user");
if (me.isDelAdmin()) { %>
	<input type="button" value="運用支援" onclick="management()" class="management" />
<%} %>
<table border="1">
	<tr>
		<td>
			<span class="normal10"><%=searchResultForm.getH_label6()%></span></td>
		<td><html:select property="dispAttr1">
					<html:options labelName="searchResultForm" labelProperty="dispNameList"
									name="searchResultForm" property="dispKeyList" />
				</html:select></td>
		<td><html:select property="dispAttr2">
					<html:options labelName="searchResultForm" labelProperty="dispNameList"
									name="searchResultForm" property="dispKeyList" />
				</html:select></td>
		<td><html:select property="dispAttr3">
					<html:options labelName="searchResultForm" labelProperty="dispNameList"
									name="searchResultForm" property="dispKeyList" />
				</html:select></td>
		<td><html:select property="dispAttr4">
					<html:options labelName="searchResultForm" labelProperty="dispNameList"
									name="searchResultForm" property="dispKeyList" />
				</html:select></td>
		<td><html:select property="dispAttr5">
					<html:options labelName="searchResultForm" labelProperty="dispNameList"
									name="searchResultForm" property="dispKeyList" />
				</html:select></td>
		<td><html:select property="dispAttr6">
					<html:options labelName="searchResultForm" labelProperty="dispNameList"
									name="searchResultForm" property="dispKeyList" />
				</html:select></td>
	</tr>
</table>
</html:form>
</body>
</html:html>
