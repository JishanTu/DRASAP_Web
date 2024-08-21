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
	<script type="text/javascript">
	<!--
	document.onkeydown = keys;
	function keys() {
		switch (event.keyCode) {
			case 116: // F5
				event.keyCode = 0;
				return false;
				break;
		}
	}
	<%-- 2013.09.06 yamagishi add. start --%>
	var userAgent;
	var appVersion;<%-- end --%>
	function onLoad() {
		if (parent.parent.condition.unLockButtons != null) {
			parent.parent.condition.unLockButtons();
		}
		<%-- 2013.07.16 yamagishi add. start --%>
		if (navigator.appName == "Netscape" && !(navigator.platform.indexOf("Mac") != -1)) {
			document.captureEvents(Event.MOUSEDOWN);
		}
		// 右クリック禁止
		document.onmousedown = disableContextMenu;
		document.oncontextmenu = disableOnContextMenu;
		<%-- 2013.07.16 yamagishi add. end --%>
		userAgent = window.navigator.userAgent.toLowerCase();<%-- 2013.09.06 yamagishi add. --%>
		appVersion = window.navigator.appVersion.toLowerCase();
	}
	function nowSearch(){
		var nowSearch;
		nowSearch = document.getElementById("nowSearch");
		nowSearch.style.visibility = "visible";
	}
	<%-- 2019.10.28 yamamoto add. start --%>
	// 画面ロック
	function screenLock(){
		var screen;
		screen = document.getElementById("screenLock");
		screen.style.visibility = "visible";
	}
	// 画面ロック解除
	function screenUnLock(){
		var screen;
		screen = document.getElementById("screenLock");
		screen.style.visibility = "hidden";
	}
	<%-- 2019.10.28 yamamoto add. end --%>
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
	<%-- 2013.07.16 yamagishi add. end --%>
	<%-- 2013.09.06 yamagishi add. start --%>
	var dialogFlag = false; // 二重起動防止
	function openDLManagerDialog(idx) {
<%		// DLマネージャが利用可能な場合
		User me = (User) session.getAttribute("user");
		if (me.isDLManagerAvailable()) { %>
		if (!dialogFlag) {
			dialogFlag = true;
			var drwgNoLink = document.getElementById("drwgNoLink[" + idx + "]");
			drwgNoLink.href = void(0);
			var targetUrl = '<%= request.getContextPath() %>/switch.do?prefix=/search&page=/DLManagerDialog.jsp';
			targetUrl = targetUrl + '&searchIndex=' + idx;
			var w = 300;
			var h = 100;
			if (userAgent.indexOf('msie') != -1 && appVersion.indexOf("msie 6.") != -1) {
				// IE6用設定
				w = 305;
				h = 130;
			}
			window.showModalDialog(targetUrl, null, 'center=yes;status=no;scroll=no;resizable=no;dialogWidth=' + w + 'px;dialogHeight=' + h + 'px;'); // ※IEのみ
			dialogFlag = false;
		}
		// hrefをキャンセル
		return false;
<%		} %>
	}
	<%-- 2013.09.06 yamagishi add. end --%>
	//-->
	</script>
</head>
<body bgcolor="#FFFFFF" style="margin: 0;" onload="onLoad();">
<html:form action="/result" >
<html:hidden property="act" />
<html:hidden property="dispAttr1" /><%-- この隠し属性がないと、次のXX件をクリックすると、表示属性が消えてしまう --%>
<html:hidden property="dispAttr2" />
<html:hidden property="dispAttr3" />
<html:hidden property="dispAttr4" />
<html:hidden property="dispAttr5" />
<html:hidden property="dispAttr6" />
<html:hidden property="outputPrinter" />
<html:hidden property="outCsvAll" /><%-- ファイル出力で全属性か --%>
<bean:define id="resultList" type="java.util.ArrayList"
			name="searchResultForm" property="searchResultList" scope="session" />
<%	// 検索結果がなければ表示する
	if (((SearchResultForm)session.getAttribute("searchResultForm")).getSearchResultList().size() == 0) { %>
		<logic:equal value="Japanese" name="user" property="language" scope="session">
		<ul style="color: red; font-size: 12pt;">
			<li>検索結果は0件です。</li>
		</ul>
		</logic:equal>
		<logic:notEqual value="Japanese" name="user" property="language" scope="session">
		<ul style="color: red; font-size: 12pt;">
			<li>No items Found.</li>
		</ul>
		</logic:notEqual>
<%	} %>
<table border="0" cellspacing="1" cellpadding="0">
	<%-- userを定義する --%>
	<bean:define id="user" type="User" name="user" scope="session" />
	<%-- iterateに必要なoffsetとlengthを定義する --%>
	<bean:define id="iterateLength" type="java.lang.String"
				name="searchResultForm" property="dispNumberPerPage" scope="session" />
	<bean:define id="iterateOffest" type="java.lang.String"
				name="searchResultForm" property="dispNumberOffest" scope="session" />
	<bean:define id="dispAttr1Key" type="java.lang.String"
				name="searchResultForm" property="dispAttr1" scope="session" />
	<bean:define id="dispAttr2Key" type="java.lang.String"
				name="searchResultForm" property="dispAttr2" scope="session" />
	<bean:define id="dispAttr3Key" type="java.lang.String"
				name="searchResultForm" property="dispAttr3" scope="session" />
	<bean:define id="dispAttr4Key" type="java.lang.String"
				name="searchResultForm" property="dispAttr4" scope="session" />
	<bean:define id="dispAttr5Key" type="java.lang.String"
				name="searchResultForm" property="dispAttr5" scope="session" />
	<bean:define id="dispAttr6Key" type="java.lang.String"
				name="searchResultForm" property="dispAttr6" scope="session" />
	<logic:iterate id="searchResultElement" type="SearchResultElement" indexId="idx"
					name="searchResultForm" property="searchResultList" scope="session"
					offset="<%=iterateOffest%>" length="<%=iterateLength%>">
		<% // 見出し部分を 15件毎につける
			if(((idx.intValue() - Integer.parseInt(iterateOffest)) % 15)==0){ %>
			<tr>
				<td></td>
				<!--
				アクセスレベル1でもプリンタへ印刷指示は出来るように変更。by Hirata at '04.May.6
				<td></td>
				-->
				<td nowrap="nowrap" align="center" bgcolor="#CCCCCC"><span class="normal10"><bean:write name="searchResultForm" property="dispOutputSizeName"/></span></td>
				<td nowrap="nowrap" align="center" bgcolor="#CCCCCC"><span class="normal10"><bean:write name="searchResultForm" property="dispCopiesName"/></span></td>
				<td nowrap="nowrap" align="center" bgcolor="#CCCCCC"><span class="normal10"><bean:write name="searchResultForm" property="dispDwgNoName"/></span></td>
				<td nowrap="nowrap" align="center" bgcolor="#CCCCCC"><span class="normal10">
					<bean:write name="searchResultForm" property="dispAttr1Name" scope="session" /></span></td>
				<td nowrap="nowrap" align="center" bgcolor="#CCCCCC"><span class="normal10">
					<bean:write name="searchResultForm" property="dispAttr2Name" scope="session" /></span></td>
				<td nowrap="nowrap" align="center" bgcolor="#CCCCCC"><span class="normal10">
					<bean:write name="searchResultForm" property="dispAttr3Name" scope="session" /></span></td>
				<td nowrap="nowrap" align="center" bgcolor="#CCCCCC"><span class="normal10">
					<bean:write name="searchResultForm" property="dispAttr4Name" scope="session" /></span></td>
				<td nowrap="nowrap" align="center" bgcolor="#CCCCCC"><span class="normal10">
					<bean:write name="searchResultForm" property="dispAttr5Name" scope="session" /></span></td>
				<td nowrap="nowrap" align="center" bgcolor="#CCCCCC"><span class="normal10">
					<bean:write name="searchResultForm" property="dispAttr6Name" scope="session" /></span></td>
			</tr>
		<% } %>
		<% // 使用禁止なら行の色を変更する
			String bgcolor1 = "#FFFFFF";
			if("NG".equals(searchResultElement.getAttrMap().get("PROHIBIT"))){
				bgcolor1 = "#FF66FF";
			} else if (searchResultElement.getAttrMap().get("TWIN_DRWG_NO") != null && ((String)searchResultElement.getAttrMap().get("TWIN_DRWG_NO")).length() > 0){
				bgcolor1 = "#FFFFA4";
			}
		%>
		<tr bgcolor="<%=bgcolor1%>">
			<%-- 2013.08.21 yamagishi modified. start
			<td><html:checkbox name="searchResultElement" property="selected" indexed="true" /></td> --%>
			<logic:equal name="searchResultElement" property="aclFlag" value="1">
			<td><html:checkbox name="searchResultElement" property="selected" indexed="true" /></td>
			</logic:equal>
			<logic:notEqual name="searchResultElement" property="aclFlag" value="1">
			<td />
			</logic:notEqual>
			<%-- 2013.08.21 yamagishi modified. end --%>
			<!--
			アクセスレベル1でもプリンタへ印刷指示は出来るように変更。by Hirata at '04.May.6
			<%-- // この図面が、このユーザーにとって印刷可能なら
				if(user.isPrintable(searchResultElement)) { --%>
				<td></td>
			<%-- } else { --%>
				<td bgcolor="#FF0000"><span class="normal10white">×</span></td>
			<%-- } --%>
			-->
			<td align="center"><html:select name="searchResultElement" property="printSize" indexed="true">
						<html:option value="ORG">
						<logic:equal value="Japanese" name="user" property="language" scope="session">
						原寸
						</logic:equal>
						<logic:notEqual value="Japanese" name="user" property="language" scope="session">
						ORIGINAL
						</logic:notEqual>
						</html:option>
						<html:option value="A0">A0</html:option>
						<html:option value="A1">A1</html:option>
						<html:option value="A2">A2</html:option>
						<html:option value="A3">A3</html:option>
						<html:option value="A4">A4</html:option>
						<html:option value="70.7%">70.7%</html:option>
						<html:option value="50%">50%</html:option>
						<html:option value="35.4%">35.4%</html:option>
						<html:option value="25%">25%</html:option>
					</html:select></td>
			<td><html:select name="searchResultElement" property="copies" indexed="true">
						<html:option value="1">1</html:option>
						<html:option value="2">2</html:option>
						<html:option value="3">3</html:option>
						<html:option value="4">4</html:option>
						<html:option value="5">5</html:option>
						<html:option value="6">6</html:option>
						<html:option value="7">7</html:option>
						<html:option value="8">8</html:option>
						<html:option value="9">9</html:option>
						<html:option value="10">10</html:option>
					</html:select></td>
			<%-- 2013.06.24 yamagishi modified. start
			<td nowrap="nowrap"><span class="normal12blue">
				<html:link forward="preview" name="searchResultElement" property="linkParmMap">
					<bean:write name="searchResultElement" property="drwgNoFormated" />
				</html:link></span></td> --%>
			<logic:equal name="searchResultElement" property="aclFlag" value="1">
			<td nowrap="nowrap"><span class="normal12blue">
				<html:link styleId='<%="drwgNoLink["+idx+"]"%>' forward="preview" name="searchResultElement" property="linkParmMap" title="<%=searchResultElement.getAclBalloon()%>" onclick='<%="return openDLManagerDialog("+ idx +");"%>'>
					<bean:write name="searchResultElement" property="drwgNoFormated" />
				</html:link></span></td>
			</logic:equal>
			<logic:notEqual name="searchResultElement" property="aclFlag" value="1">
			<td nowrap="nowrap"><span class="normal12" title="<%=searchResultElement.getAclBalloon()%>">
				<bean:write name="searchResultElement" property="drwgNoFormated" /></span></td>
			</logic:notEqual>
			<%-- 2013.06.24 yamagishi modified. end --%>
			<td nowrap="nowrap"><span class="normal12">&nbsp;
			<%=searchResultElement.getAttr(dispAttr1Key)%>&nbsp;</span></td>
			<td nowrap="nowrap"><span class="normal12">&nbsp;
			<%=searchResultElement.getAttr(dispAttr2Key)%>&nbsp;</span></td>
			<td nowrap="nowrap"><span class="normal12">&nbsp;
			<%=searchResultElement.getAttr(dispAttr3Key)%>&nbsp;</span></td>
			<td nowrap="nowrap"><span class="normal12">&nbsp;
			<%=searchResultElement.getAttr(dispAttr4Key)%>&nbsp;</span></td>
			<td nowrap="nowrap"><span class="normal12">&nbsp;
			<%=searchResultElement.getAttr(dispAttr5Key)%>&nbsp;</span></td>
			<td nowrap="nowrap"><span class="normal12">&nbsp;
			<%=searchResultElement.getAttr(dispAttr6Key)%>&nbsp;</span></td>
		</tr>
	</logic:iterate>
</table>
<table class="nowsearch" id="nowSearch" style="visibility:hidden">
<tr valign="middle">
<td align="center" style="font-size:18pt;color:#0000FF;">
<logic:equal value="Japanese" name="user" property="language" scope="session">
検索中・・・・
</logic:equal>
<logic:notEqual value="Japanese" name="user" property="language" scope="session">
Now Searching...
</logic:notEqual>
</td>
</tr>
</table>
<%-- IE互換性表示対策--%>
<%-- パスワード変更画面起動中のクリック防止--%>
<table class="nowsearch" id="screenLock" style="visibility:hidden">
<tr><td></td></tr>
</table>
</html:form>
</body>
</html:html>
