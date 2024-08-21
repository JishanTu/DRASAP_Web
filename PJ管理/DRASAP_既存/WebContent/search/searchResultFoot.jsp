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
		function keys(){
			switch (event.keyCode ){
				case 116: // F5
					event.keyCode = 0;
					return false;
					break;
			}
		}
		// 検索結果リスト画面の属性actをセットして、サブミット
		// 前のXX件、後ろのXX件
		// 印刷指示
		function setActSubmit(parm){
			if(parm == "PRINT"){
			<logic:equal value="Japanese" name="user" property="language" scope="session">
				if(! confirm("出力を行いますか?")){
			</logic:equal>
			<logic:notEqual value="Japanese" name="user" property="language" scope="session">
				if(! confirm("Do you want to output ?")){
			</logic:notEqual>
					//alert(parm);
					return;
				}
			} else if(parm == "PREV" || parm == "NEXT"){
				links = document.getElementsByTagName("a");
				for (var i=0;i<links.length;i++){
						if (links[i].disabled == true) return;
				}
			}

			parent.result_body.document.forms[0].outputPrinter.value=parent.result_head.document.forms[0].outputPrinter.value;
			parent.result_body.document.forms[0].act.value=parm;// 隠し属性actにをセット
			parent.result_body.document.forms[0].target="_parent";// ターゲットは親
			parent.result_body.document.forms[0].submit();
		}
		// ファイル出力に対応
		function outAttrCsv(){
			parent.result_body.document.forms[0].act.value="OUT_CSV";// 隠し属性actにセット
			// 隠し属性outCsvAllにセット
			parent.result_body.document.forms[0].outCsvAll.value=document.forms[0].outAttrAll.checked;
			parent.result_body.document.forms[0].target="_parent";// ターゲットは親
			parent.result_body.document.forms[0].submit();
		}
		// アクセスレベル変更に対応
		function goAclvChg(){
			parent.result_body.document.forms[0].outputPrinter.value=parent.result_head.document.forms[0].outputPrinter.value;
			parent.result_body.document.forms[0].act.value="ACLV_CHG";// 隠し属性actにセット
			parent.result_body.document.forms[0].target="_top";// ターゲットはtop
			parent.result_body.document.forms[0].submit();
		}
		function deleteDwg() {
			if (selectChk() == 0) {
			<logic:equal value="Japanese" name="user" property="language" scope="session">
				alert("削除する図面が選択されていません。");
			</logic:equal>
			<logic:notEqual value="Japanese" name="user" property="language" scope="session">
				alert("The deleting drawings are not selected.");
			</logic:notEqual>
				return;
			}
			parent.result_body.document.forms[0].target="_parent";//
			parent.result_body.document.forms[0].act.value="DELETEDWG";
			parent.result_body.document.forms[0].submit();
//			parent.parent.result.location.href = "switch.do?prefix=/search&amp;page=/delete_Login.jsp";
		}
		function selectChk() {
			if (parent.result_body.document.forms[0] == null) return 0;
			var elm = parent.result_body.document.forms[0].elements;
			var count = 0;
			for (var i = 0; i < elm.length; i++) {
				if (elm[i].type == 'checkbox') {
					if (elm[i].checked == true) count++;
				}
			}
			return count;
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
		<%-- 2019.10.17 yamamoto add. start --%>
		function multiPDF() {
			if (selectChk() == 0) {
			<logic:equal value="Japanese" name="user" property="language" scope="session">
				alert("図面を１つ以上選択してください。");
			</logic:equal>
			<logic:notEqual value="Japanese" name="user" property="language" scope="session">
				alert("Please select one or more drawings.");
			</logic:notEqual>
				return;
			}
			parent.result_body.document.forms[0].target="_parent"; //ターゲットは親
			parent.result_body.document.forms[0].act.value="MULTI_PDF"; // 隠し属性actにセット
			parent.result_body.document.forms[0].submit();
		}
		<%-- 2019.10.17 yamamoto add. end --%>
		<%-- 2020.03.17 yamamoto add. start --%>
		function pdfZip() {
			if (selectChk() == 0) {
			<logic:equal value="Japanese" name="user" property="language" scope="session">
				alert("図面を１つ以上選択してください。");
			</logic:equal>
			<logic:notEqual value="Japanese" name="user" property="language" scope="session">
				alert("Please select one or more drawings.");
			</logic:notEqual>
				return;
			}
			parent.result_body.document.forms[0].target="_parent"; //ターゲットは親
			parent.result_body.document.forms[0].act.value="PDF_ZIP"; // 隠し属性actにセット
			parent.result_body.document.forms[0].submit();
		}
		<%-- 2020.03.17 yamamoto add. end --%>
	//-->
	</script>
</head>
<%-- 2013.07.16 yamagishi modified.
<body bgcolor="#CCCCCC" bottommargin="0" leftmargin="0" topmargin="0" rightmargin="0" marginheight="0" marginwidth="0"> --%>
<body style="bacground-color: #CCCCCC; margin: 0;" onload="onLoad()">
<form>
<bean:define id="searchResultForm" type="SearchResultForm" name="searchResultForm" scope="session" />
<%-- 今表示している件数に関しての情報を取得 --%>
<bean:define id="resultList" type="java.util.ArrayList"
			name="searchResultForm" property="searchResultList" scope="session" />
<bean:define id="offset" type="java.lang.String"
			name="searchResultForm" property="dispNumberOffest" scope="session" />
<bean:define id="dispNumber" type="java.lang.String"
			name="searchResultForm" property="dispNumberPerPage" scope="session" />
<%
	int cntStart = Integer.parseInt(offset) + 1;// offsetに1を加算
	if(resultList.size()==0){
		cntStart = 0;
	}
	int cntEnd = Math.min(Integer.parseInt(offset) + Integer.parseInt(dispNumber),	// offsetに1ページ当たり件数を加算
						resultList.size());	// もしくはresultListのサイズの小さい方
%>
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td>&nbsp;&nbsp;</td>
		<td valign="top">
			<span class="normal12">
			    <%=searchResultForm.getF_label1()%>
			</span>&nbsp;&nbsp;&nbsp;&nbsp;
			<a href="javascript:setActSubmit('PREV')">
			    <span class="normal12blue">&lt;&lt;&nbsp;
			    <%=searchResultForm.getF_label2()%>
			    </span>
			</a>
			<span class="normal12">｜</span>
			<a href="javascript:setActSubmit('NEXT')">
			    <span class="normal12blue">
			    <%=searchResultForm.getF_label3()%>
			    &gt;&gt;</span>
			</a>
		</td>
		<td align="left"><input type="button" value="　<%=searchResultForm.getF_label4()%>　" onclick="setActSubmit('PRINT')" /></td>
		<td align="right"><span class="normal10">
			<%	// admin_flag='2'のユーザのみ
				// ボタンを表示する
				User me = (User) session.getAttribute("user");
				if (me.isDelAdmin()) { %>
					<input type="button" value="図番削除" onclick="deleteDwg()" style="margin-right:10px;" />
			<%      } %>
			<input type="checkbox" name="outAttrAll" /><%=searchResultForm.getF_label5()%>
			<input type="button" value="<%=searchResultForm.getF_label6()%>" onclick="outAttrCsv()" />
			&nbsp;&nbsp;&nbsp;&nbsp;
<% // 2019.12.04 yamamoto modified. start
			// PDF出力可能なユーザのみボタンを表示する
			if (me.isMultiPdf() || me.isAdmin()){ %>
				<input type="button" value="<%=searchResultForm.getF_label8()%>" onclick="multiPDF()" />
			<% } %>
<% // 2019.12.04 yamamoto modified. end %>
<% // 2020.03.10 yamamoto modified. start
			// PDF出力可能なユーザのみボタンを表示する
			if (me.isMultiPdf() || me.isAdmin()){ %>
				<input type="button" value="<%=searchResultForm.getF_label9()%>" onclick="pdfZip()" />
			<% } %>
<% // 2020.03.10 yamamoto modified. end %>
			<%	// アクセスレベルを変更可能なユーザーのみ
				// ボタンを表示する
// 2013.07.24 yamagishi modified. start
				if ("1".equals(me.getAclUpdateFlag())) {
// 2013.07.24 yamagishi modified. end %>
					<input type="button" value="<%=searchResultForm.getF_label7()%>" onclick="goAclvChg()" />
			<%      } %>
		</span></td>
	</tr>
</table>
</form>
</body>
</html:html>
