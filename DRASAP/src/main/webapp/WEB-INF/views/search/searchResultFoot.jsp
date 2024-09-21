<%@ page contentType="text/html;rset=UTF-8" %>
<%@ page import="tyk.drasap.search.*,tyk.drasap.common.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page isELIgnored="false"%>

<%-- ログイン情報の確認 --%>
<c:if test="${empty sessionScope.user}">
<script>
	location.replace('<%=request.getContextPath()%>/timeout');
</script>
</c:if>

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
	<title>Drawing Search and Print System [図面検索]</title>
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<style type="text/css">
	
	@import url( <%=request.getContextPath() %>/resources/css/<%=session.getAttribute("default_css")%> );
	
	
	</style>
	<script type="text/javascript">
		document.onkeydown = keys;
		function keys(){
			switch (event.keyCode ){
				case 116: // F5
					event.keyCode = 0;
					return false;
					break;
			}
		}
		
// 		 function setTableWidth() {
// 	            var availWidth = screen.availWidth;

// 	            var table = document.getElementById('footTable');

// 	            table.style.width = availWidth + 'px';
// 	        }

		// 検索結果リスト画面の属性actをセットして、サブミット
		// 前のXX件、後ろのXX件
		// 印刷指示
		function setActSubmit(parm){
			if(parm == "PRINT"){
				if(!confirm("出力を行いますか?")){
					return;
				}
			} else if(parm == "PREV" || parm == "NEXT"){
				links = document.getElementsByTagName("a");
				for (var i=0;i<links.length;i++){
					if (links[i].disabled == true) return;
				}
			}
			if(parm == "SEARCH_THUMBNAIL"){
				parent.result_body.document.forms[0].outputPrinter.value=parent.result_head.document.forms[0].outputPrinter.value;
				parent.result_body.document.forms[0].act.value=parm;// 隠し属性actにをセット
				parent.result_body.document.forms[0].target="_top";// ターゲットはtop
				parent.result_body.document.forms[0].submit();
			} else{
				parent.result_body.document.forms[0].outputPrinter.value=parent.result_head.document.forms[0].outputPrinter.value;
				parent.result_body.document.forms[0].act.value=parm;// 隠し属性actにをセット
				parent.result_body.document.forms[0].target="_parent";// ターゲットは親
				parent.result_body.document.forms[0].submit();
			}
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
				alert("削除する図面が選択されていません。");
				return;
			}
			parent.result_body.document.forms[0].target="_parent";//
			parent.result_body.document.forms[0].act.value="DELETEDWG";
			parent.result_body.document.forms[0].submit();
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
			//setTableWidth();
			document.onmousedown = disableContextMenu;
			document.oncontextmenu = disableOnContextMenu;
		}
		<%-- 2013.07.16 yamagishi add. end --%>
		<%-- 2019.10.17 yamamoto add. start --%>
		function multiPDF() {
			if (selectChk() == 0) {
				alert("図面を１つ以上選択してください。");
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
				<c:choose>
					<c:when test="${sessionScope.user.language == 'Japanese'}">
						alert("図面を１つ以上選択してください。");
					</c:when>
					<c:otherwise>
						alert("Please select one or more drawings.");
					</c:otherwise>
				</c:choose>
				return;
			 }
			parent.result_body.document.forms[0].target="_parent"; //ターゲットは親
			parent.result_body.document.forms[0].act.value="PDF_ZIP"; // 隠し属性actにセット
			parent.result_body.document.forms[0].submit();
		}
		<%-- 2020.03.17 yamamoto add. end --%>
	</script>
</head>
<%-- 2013.07.16 yamagishi modified.
<body bgcolor="#CCCCCC" bottommargin="0" leftmargin="0" topmargin="0" rightmargin="0" marginheight="0" marginwidth="0"> --%>
<body style="bacground-color: #CCCCCC; margin: 0;padding-bottom:20px;" onload="onLoad()" >
<form action="<%=request.getContextPath() %>/result" method = "post">
<c:set var="searchResultForm" value="${sessionScope.searchResultForm}"/>
<c:set var="resultList" value="${sessionScope.searchResultForm.searchResultList}"/>
<c:set var="offset" value="${sessionScope.searchResultForm.dispNumberOffest}"/>
<c:set var="dispNumber" value="${sessionScope.searchResultForm.dispNumberPerPage}"/>
<c:set var="cntStart" value="${offset +1 }"/>
<c:if test="${ fn:length(resultList) == 0} ">
	<c:set var="cntStart" value="0"/>
</c:if>
<c:choose>
<c:when test="${offset+ dispNumber < fn:length(resultList)}">
<c:set var="cntEnd" value="${offset+ dispNumber}"/>
</c:when>
<c:otherwise>
<c:set var="cntEnd" value="${ fn:length(resultList)}"/>
</c:otherwise>
</c:choose>
<table border="0" cellspacing="0"  cellpadding="0" style = "width:100%;">
	<tr>
		<td>&nbsp;&nbsp;</td>
		<td valign="top">
			<span class="normal12">
				${searchResultForm.f_label1}
			</span>&nbsp;&nbsp;&nbsp;&nbsp;
			<a href="javascript:setActSubmit('PREV')">
				<span class="normal12blue">&lt;&lt;&nbsp;
				${searchResultForm.f_label2}
				</span>
			</a>
			<span class="normal12">｜</span>
			<a href="javascript:setActSubmit('NEXT')">
				<span class="normal12blue">
				${searchResultForm.f_label3}
				&gt;&gt;</span>
			</a>
		</td>
		<td align="left">
		<c:choose>
			<c:when test="${sessionScope.resultDispMode == 'list_view' || empty sessionScope.resultDispMode}">
				<input type="button" value="　${searchResultForm.f_label4}　" onclick="setActSubmit('PRINT')" />
			</c:when>
			<c:when test="${sessionScope.resultDispMode == 'thumbnail_view'}">
				<input type="button" value="　${searchResultForm.f_label4}　" onclick="setActSubmit('SEARCH_THUMBNAIL')" />
			</c:when>
		</c:choose>
		</td>
		<td align="right"><span class="normal10">
			<%	// admin_flag='2'のユーザのみ
				// ボタンを表示する
				User me = (User) session.getAttribute("user");
				if (me.isDelAdmin()) { %>
					<input type="button" value="${searchResultForm.f_label10}" onclick="deleteDwg()" style="margin-right:10px;" />
			<%      } %>
			<input type="checkbox" name="outAttrAll" />${searchResultForm.f_label5}
			<input type="button" value="${searchResultForm.f_label6}" onclick="outAttrCsv()" />
			&nbsp;&nbsp;&nbsp;&nbsp;
<% // 2019.12.04 yamamoto modified. start
			// PDF出力可能なユーザのみボタンを表示する
			if (me.isMultiPdf() || me.isAdmin()){ %>
				<input type="button" value="${searchResultForm.f_label8}" onclick="multiPDF()" />
			<% } %>
<% // 2019.12.04 yamamoto modified. end %>
<% // 2020.03.10 yamamoto modified. start
            // PDF出力可能なユーザのみボタンを表示する
            if (me.isMultiPdf() || me.isAdmin()){ %>
                <input type="button" value="${searchResultForm.f_label9}" onclick="pdfZip()" />
            <% } %>
<% // 2020.03.10 yamamoto modified. end %>
			<%	// アクセスレベルを変更可能なユーザーのみ
				// ボタンを表示する
// 2013.07.24 yamagishi modified. start
				if ("1".equals(me.getAclUpdateFlag())) {
// 2013.07.24 yamagishi modified. end %>
					<input type="button" value="${searchResultForm.f_label7}" onclick="goAclvChg()" />
			<%      } %>
		</span></td>
	</tr>
</table>
</form>
</body>
</html>
