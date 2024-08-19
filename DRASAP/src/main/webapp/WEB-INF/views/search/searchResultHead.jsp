<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="tyk.drasap.search.*,tyk.drasap.common.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
	<style type="text/css">@import url( <%=request.getContextPath() %>/resources/css/<%=session.getAttribute("default_css")%> );</style>
	<style type="text/css">
		.management {
			position: absolute;
			top:0px;
			right:0px;
			margin-right:10px;
		}
		#list_view, #thumbnail_view {
			width:120px;
		}
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
		// 再表示
		function doRefresh(){
			// 表示属性をresult_bodyフレーム隠し属性にセット
			var parentForm = parent.result_body.document.forms[0];
			var currentForm = document.forms[0];
			for (var j = 1; j <= ${searchResultForm.getViewSelColNum()}; j++) {
				var dispAttr = 'dispAttr' + j;
				parentForm[dispAttr].value = currentForm[dispAttr].value;
			}
			parentForm.outputPrinter.value=currentForm.outputPrinter.value;
			parentForm.act.value="REFRESH";// 隠し属性actにREFRESHをセット
			parentForm.target="_parent";// ターゲットは親
			parentForm.submit();
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
			targetUrl = '<%=request.getContextPath() %>/switch.do?page=/search/management_Login.jsp';
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
		function listViewChange() {
			parent.result_body.document.forms[0].act.value="LIST_VIEW";
			parent.result_body.document.forms[0].target="_parent";
			parent.result_body.document.forms[0].submit();
		}
		function thumbnailViewChange() {
			parent.result_body.document.forms[0].act.value="THUMBNAIL_VIEW";
			parent.result_body.document.forms[0].target="_parent";
			parent.result_body.document.forms[0].submit();
		}
		function thumbnailSizeChange() {
			parent.result_body.document.forms[0].thumbnailSize.value = document.forms[0].elements["thumbnailSize"].value;
			parent.result_body.document.forms[0].act.value="THUMBNAIL_SIZE";
			parent.result_body.document.forms[0].target="_parent";
			parent.result_body.document.forms[0].submit();
		}
	</script>
</head>
<%-- 2013.07.16 yamagishi modified.
<body bgcolor="#CCCCCC" bottommargin="0" leftmargin="0" topmargin="0" rightmargin="0" marginheight="0" marginwidth="0"> --%>
<body style="background-color: #CCCCCC; margin: 0;overflow-y: hidden;height: 100%;" onload="onLoad()">
<form action="<%=request.getContextPath() %>/result"  method = "post">
<!--================ ヘッダ ==================================-->
<c:set var="searchResultForm" value="${sessionScope.searchResultForm}"/>
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td>
			<table border="0" bgcolor="#EEEEEE">
				<tr>
					<td nowrap="nowrap">
						<span class="normal10">${searchResultForm.h_label1 }</span>
					</td>
				</tr>
			</table>
		</td>
		<td><span class="normal10">
			<%	String thumbValue = DrasapPropertiesFactory.getDrasapProperties(this).getProperty("thumbnail.value");
			if ("true".equals(thumbValue)) { %>
			<span style="<c:choose><c:when test="${sessionScope.indication == 'thumbnail_view'}">visibility: visible;</c:when>
											<c:otherwise>visibility: hidden;</c:otherwise></c:choose>">${searchResultForm.h_label9}</span>
			<select name="thumbnailSize" onchange="thumbnailSizeChange()"
					style="<c:choose><c:when test="${sessionScope.indication == 'thumbnail_view'}">visibility: visible;</c:when>
									<c:otherwise>visibility: hidden;</c:otherwise></c:choose>">
				<c:choose>
					<c:when test="${user.language == 'Japanese'}">
						<option value="L" <c:if test="${sessionScope.thumbnailSize == 'L'}">selected</c:if>>大</option>
						<option value="M" <c:if test="${sessionScope.thumbnailSize == 'M'}">selected</c:if>>中</option>
						<option value="S" <c:if test="${sessionScope.thumbnailSize == 'S'}">selected</c:if>>小</option>
					</c:when>
					<c:otherwise>
						<option value="L" <c:if test="${sessionScope.thumbnailSize == 'L'}">selected</c:if>>L</option>
						<option value="M" <c:if test="${sessionScope.thumbnailSize == 'M'}">selected</c:if>>M</option>
						<option value="S" <c:if test="${sessionScope.thumbnailSize == 'S'}">selected</c:if>>S</option>
					</c:otherwise>
				</c:choose>
			</select>
			<input type="button" id="list_view" value="${searchResultForm.h_label8}" onclick="listViewChange()"
					style="<c:choose><c:when test="${sessionScope.indication == 'thumbnail_view'}">display: none;</c:when>
							<c:otherwise>display: inline-block;</c:otherwise></c:choose>"/>
			<input type="button" id="thumbnail_view" value="${searchResultForm.h_label7}" onclick="thumbnailViewChange()"
					style="<c:choose><c:when test="${sessionScope.indication == 'thumbnail_view'}">display: inline-block;</c:when>
							<c:otherwise>display: none;</c:otherwise></c:choose>"/>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<%	} %>
			<input type="button" value="${searchResultForm.h_label2}" onclick="checkOnAll()" />
			<input type="button" value="${searchResultForm.h_label3}" onclick="checkOffAll()" />
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="button" value="${searchResultForm.h_label4}" onclick="doRefresh()" />
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			${searchResultForm.h_label5 }&nbsp;
			<select name="outputPrinter">
				<c:forEach items="${searchResultForm.printerKeyList}"
					var="outputPrinterKey" varStatus="loop">
					<option value="${outputPrinterKey}" selected>${searchResultForm.printerNameList[loop.index]}</option>
				</c:forEach>
			</select>
			</span>
		</td>
	</tr>
</table>
<%// admin_flag='2'のユーザのみ
// ボタンを表示する
User me = (User) session.getAttribute("user");
if (me.isDelAdmin()) { %>
	<input type="button" value="${sessionScope.searchResultForm.h_label10}" onclick="management()" class="management" />
<%} %>
<table border="1">
	<tr>
		<td>
			<span class="normal10">${sessionScope.searchResultForm.h_label6}</span>
		</td>
		<c:forEach begin="1" end="${searchResultForm.getViewSelColNum()}" var="index">
			<td>
				<select name="dispAttr${index}">
					<c:forEach items="${searchResultForm.dispKeyList}" var="dispKey" varStatus="loop">
						<c:choose>
							<c:when test="${dispKey == searchResultForm.getDispAttr(index - 1)}">
								<option value="${dispKey}" selected>${searchResultForm.dispNameList[loop.index]}</option>
							</c:when>
							<c:otherwise>
								<option value="${dispKey}">${searchResultForm.dispNameList[loop.index]}</option>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</select>
			</td>
		</c:forEach>
	</tr>
</table>
</form>
</body>
</html>
