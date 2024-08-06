<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="tyk.drasap.search.*,tyk.drasap.common.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
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
		body {
			margin: 0;
			overflow-x: hidden;
			overflow-y: auto;
		}

		.container {
		display: inline-block;
			gap: 10px;
		}

		.galleryr {
			display:flex;
			padding: 5px;
			margin: 5px;
			text-align: center; 
			transition: transform 0.3s ease;
			float: left;
			flex-direction: column;
			border: 2px solid black;
		}

		.controls {
			align-items: center;
			justify-content: center; 
			margin-top: 5px;
		}

		.thumbnail {
			border: 1px solid #ccc;
			padding: 5px;
			transition: transform 0.3s ease;
		}

		.thumbnail.small {
			width: 140px;
			height: auto;
		}

		.thumbnail.medium {
		    width: 210px;
		    height: auto;
		}

		.thumbnail.large {
			width: 420px;
			height: auto;
		}

		.checkbox.small {
			transform: scale(0.75);
		}

		.checkbox.medium {
			transform: scale(1);
		}

		.checkbox.large {
			transform: scale(1.5);
		}
		
		.drwgNo.small {
			font-size: 12px;
		}

		.drwgNo.medium {
			font-size: 16px;
		}

		.drwgNo.large {
			font-size: 20px;
		}
		
		.thumbnail:hover {
			transform: scale(1.1);
		}
	</style>
	<script type="text/javascript">
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
			var drwgNoLink = document.getElementById("drwgNoLink[" + idx + "]");
			var DRWG_NO = document.getElementById("DRWG_NO[" + idx + "]").value;
			var FILE_NAME = document.getElementById("FILE_NAME[" + idx + "]").value;
			var PATH_NAME = document.getElementById("PATH_NAME[" + idx + "]").value;
			var DRWG_SIZE = document.getElementById("printSizeSelect" + idx).value;
			var PDF = document.getElementById("PDF[" + idx + "]").value;
			var PRINT_SIZE = document.getElementById("PRINT_SIZE[" + idx + "]").value;
			drwgNoLink.href = drwgNoLink + '?FILE_NAME=' + encodeURIComponent(FILE_NAME)
											+ '&DRWG_NO=' + encodeURIComponent(DRWG_NO)
											+ '&PATH_NAME=' + encodeURIComponent(PATH_NAME)
											+ '&DRWG_SIZE=' + encodeURIComponent(DRWG_SIZE)
											+ '&PDF=' + encodeURIComponent(PDF)
											+ '&PRINT_SIZE=' + encodeURIComponent(PRINT_SIZE);

	<%		// DLマネージャが利用可能な場合
			User me = (User) session.getAttribute("user");
			if (me.isDLManagerAvailable()) { %>
			if (!dialogFlag) {
				dialogFlag = true;
				var drwgNoLink = document.getElementById("drwgNoLink[" + idx + "]");
				drwgNoLink.href = void(0);
				var targetUrl = '<%= request.getContextPath() %>/switch.do?page=/search/DLManagerDialog.jsp';
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

		function updateHiddenInput(index, field) {
			var selectElement = document.getElementById(field + 'Select' + index);
			var hiddenInput = document.getElementById(field + 'Hidden' + index);
			hiddenInput.value = selectElement.value;
			hiddenInput.name = "searchResultList[" + index + "]."+ field;
		}

		function isChecked(index) {
			var ischecked =document.getElementById('checkbox' + index).checked;
			var checkElement = document.getElementById('checkbox' + index);
			var hiddenInput = document.getElementById('checkbox' + 'Hidden' + index);
			if (ischecked == true) {
				// チェックが入っていたら無効化
				checkElement.value = true
				
			} else {
				// チェックが入って無いなら有効化
				checkElement.value = false
			}
			hiddenInput.value = checkElement.value;
			hiddenInput.name = "searchResultList[" + index + "]."+ 'selected';
			
		}

		function changeSize(thumbnailSize) {
			const images = document.querySelectorAll('img.thumbnail');
			images.forEach(img => {
				img.classList.remove('large', 'medium', 'small');
				img.classList.add(thumbnailSize);
			});
			document.querySelectorAll('.checkbox, .drwgNo').forEach(element => {
				element.classList.remove('small', 'medium', 'large');
			});

			document.querySelectorAll('.checkbox').forEach(element => {
				element.classList.add(thumbnailSize);
			});

			document.querySelectorAll('.drwgNo').forEach(element => {
				element.classList.add(thumbnailSize);
			});
		}

		document.addEventListener('DOMContentLoaded', function() {
			<% String thumbnailSize = (String) session.getAttribute("thumbnailSize"); %>
			<% if ("L".equals(thumbnailSize)) { %>
				changeSize('large');
			<% } else if ("M".equals(thumbnailSize)) { %>
				changeSize('medium');
			<% } else if ("S".equals(thumbnailSize)) { %>
				changeSize('small');
			<% } %>
		});
	</script>
</head>
<body bgcolor="#FFFFFF" style="margin: 0;" onload="onLoad();">
	<form action="<%=request.getContextPath() %>/result" method="post" >
		<input type="hidden" name="act" /> 
		<c:forEach begin="1" end="${sessionScope.searchResultForm.getViewSelColNum()}" var="index">
		<%-- この隠し属性がないと、次のXX件をクリックすると、表示属性が消えてしまう --%>
			<input type="hidden"name="dispAttr${index}" value="${sessionScope.searchResultForm.getDispAttr(index - 1)}"/>
		</c:forEach>
		<input type="hidden" name="outputPrinter" />
		<input type="hidden" name="outCsvAll" />
		<input type="hidden" name="thumbnailSize" />

		<%-- ファイル出力で全属性か --%>

		<c:set var="resultList"
			value="${sessionScope.searchResultForm.searchResultList}" />
		<c:choose>
			<c:when test="${empty resultList}">
				<ul style="color: red; font-size: 12pt;">
					<c:choose>
						<c:when test="${sessionScope.user.language == 'Japanese'}">
							<li>検索結果は0件です。</li>
						</c:when>
						<c:otherwise>
							<li>No items Found.</li>
						</c:otherwise>
					</c:choose>
				</ul>
			</c:when>
			<c:otherwise>
				<!-- その他処理 -->
			</c:otherwise>
		</c:choose>

		<table border="0" cellspacing="1" cellpadding="0" style="<c:choose><c:when test="${sessionScope.indication == 'list_view'}">display: block;</c:when>
																<c:when test="${sessionScope.indication == 'thumbnail_view'}">display: none;</c:when>
																<c:otherwise>display: block;</c:otherwise></c:choose>">
			<%-- userを定義する --%>
			<c:set var="user" value="${sessionScope.user}" />
			<%-- iterateに必要なoffsetとlengthを定義する --%>
			<c:set var="iterateLength"
				value="${sessionScope.searchResultForm.dispNumberPerPage}" />
			<c:set var="iterateOffest"
				value="${sessionScope.searchResultForm.dispNumberOffest}" />

			<c:forEach var="item"
				items="${sessionScope.searchResultForm.getSearchResultList()}"
				varStatus="status" begin = "${iterateOffest}" end = "${iterateLength + iterateOffest - 1}">
				<c:if test="${((status.index - iterateOffest) % 15) == 0}">
					<tr>
						<td></td>
						<!--
				アクセスレベル1でもプリンタへ印刷指示は出来るように変更。by Hirata at '04.May.6
				<td></td>
				-->
						<td nowrap="nowrap" align="center" bgcolor="#CCCCCC"><span
							class="normal10">
								${sessionScope.searchResultForm.dispOutputSizeName}</span></td>
						<td nowrap="nowrap" align="center" bgcolor="#CCCCCC"><span
							class="normal10">
								${sessionScope.searchResultForm.dispCopiesName}</span></td>
						<td nowrap="nowrap" align="center" bgcolor="#CCCCCC"><span
							class="normal10">
								${sessionScope.searchResultForm.dispDwgNoName}</span></td>
						<c:forEach begin="1" end="${sessionScope.searchResultForm.getViewSelColNum()}" var="index">
							<td nowrap="nowrap" align="center" bgcolor="#CCCCCC">
								<span class="normal10">${sessionScope.searchResultForm.getDispAttrName(index - 1)}</span>
							</td>
						</c:forEach>
					</tr>
				</c:if>

				<c:set var="bgcolor1" value="#FFFFFF" />
				<c:choose>
					<c:when test="${item.getAttrMap().get('PROHIBIT') == 'NG'}">
						<c:set var="bgcolor1" value="#FF66FF" />
					</c:when>
					<c:when
						test="${fn:length(item.getAttrMap().get('TWIN_DRWG_NO')) > 0}">
						<c:set var="bgcolor1" value="#FFFFA4" />
					</c:when>
				</c:choose>
				<tr bgcolor="${bgcolor1}">
					<%-- 2013.08.21 yamagishi modified. start
			<td><html:checkbox name="searchResultElement" property="selected" indexed="true" /></td> --%>
					<c:choose>
						<c:when test="${item.aclFlag == 1}">
							<td>
							<input type="hidden" id="checkboxHidden${status.index}" value="${item.selected}"/> 
							<input type="checkbox"id="checkbox${status.index}"
								value="true" onclick="isChecked(${status.index})"
								<c:if test="${item.selected}">checked="checked"</c:if> /></td>
						</c:when>
						<c:otherwise>
							<td />
						</c:otherwise>
					</c:choose>
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
					<td align="center">
								<input type="hidden" id="printSizeHidden${status.index}" value="${item.printSize}"/> 
					<select name="printSize"id="printSizeSelect${status.index}" onchange="updateHiddenInput(${status.index}, 'printSize')">
							<option value="ORG">
								<c:choose>
									<c:when test="${user.language == 'Japanese'}">原寸</c:when>
									<c:otherwise>ORIGINAL</c:otherwise>
								</c:choose>
							</option>
							<option value="A0"
								<c:if test="${item.printSize == 'A0'}">selected</c:if>>A0</option>
							<option value="A1"
								<c:if test="${item.printSize == 'A1'}">selected</c:if>>A1</option>
							<option value="A2"
								<c:if test="${item.printSize == 'A2'}">selected</c:if>>A2</option>
							<option value="A3"
								<c:if test="${item.printSize == 'A3'}">selected</c:if>>A3</option>
							<option value="A4"
								<c:if test="${item.printSize == 'A4'}">selected</c:if>>A4</option>
							<option value="70.7%"
								<c:if test="${item.printSize == '70.7%'}">selected</c:if>>70.7%</option>
							<option value="50%"
								<c:if test="${item.printSize == '50%'}">selected</c:if>>50%</option>
							<option value="35.4%"
								<c:if test="${item.printSize == '35.4%'}">selected</c:if>>35.4%</option>
							<option value="25%"
								<c:if test="${item.printSize == '25%'}">selected</c:if>>25%</option>
					</select></td>
					<td>
					<input type="hidden" id="copiesHidden${status.index}" value="${item.copies}"/> 
					<select name="copies" id="copiesSelect${status.index}" onchange="updateHiddenInput(${status.index}, 'copies')">
							<option value="1"<c:if test="${item.copies == '1'}">selected</c:if>>1</option>
							<option value="2"<c:if test="${item.copies == '2'}">selected</c:if>>2</option>
							<option value="3"<c:if test="${item.copies == '3'}">selected</c:if>>3</option>
							<option value="4"<c:if test="${item.copies == '4'}">selected</c:if>>4</option>
							<option value="5"<c:if test="${item.copies == '5'}">selected</c:if>>5</option>
							<option value="6"<c:if test="${item.copies == '6'}">selected</c:if>>6</option>
							<option value="7"<c:if test="${item.copies == '7'}">selected</c:if>>7</option>
							<option value="8"<c:if test="${item.copies == '8'}">selected</c:if>>8</option>
							<option value="9"<c:if test="${item.copies == '9'}">selected</c:if>>9</option>
							<option value="10"<c:if test="${item.copies == '10'}">selected</c:if>>10</option>
					</select></td>

					<%-- 2013.06.24 yamagishi modified. start
			<td nowrap="nowrap"><span class="normal12blue">
				<html:link forward="preview" name="searchResultElement" property="linkParmMap">
					<bean:write name="searchResultElement" property="drwgNoFormated" />
				</html:link></span></td> --%>
					<c:choose>
						<c:when test="${item.aclFlag == 1 }">
							<td nowrap="nowrap"><span class="normal12blue"> <a
									id="drwgNoLink[${status.index}]"
									href='<c:url value="/preview"/>'
									title='<c:out value="${item.aclBalloon}"/>'
									onclick="return openDLManagerDialog(${status.index});">${item.drwgNoFormated}
								</a>

							</span></td>
							<input type="hidden" id="DRWG_NO[${status.index}]" value="${item.drwgNo}"/>
							<input type="hidden" id="FILE_NAME[${status.index}]" value="${item.fileName}"/>
							<input type="hidden" id="PATH_NAME[${status.index}]" value="${item.pathName}"/>
							<input type="hidden" id="PDF[${status.index}]" value="${item.linkParmMap['PDF']}"/>
							<input type="hidden" id="PRINT_SIZE[${status.index}]" value="${item.printSize}"/>
						</c:when>
						<c:otherwise>
							<td nowrap="nowrap"><span class="normal12"
								title="${item.aclBalloon}"> ${item.drwgNoFormated}</span></td>
						</c:otherwise>
					</c:choose>
					<%-- 2013.06.24 yamagishi modified. end --%>
					<c:forEach begin="1" end="${sessionScope.searchResultForm.getViewSelColNum()}" var="index">
						<td nowrap="nowrap">
							<span class="normal12">&nbsp; ${item.getAttr(sessionScope.searchResultForm.getDispAttr(index - 1))}&nbsp;</span>
						</td>
					</c:forEach>
				</tr>
			</c:forEach>
		</table>
		<table border="0" cellspacing="1" cellpadding="0">
			<div class="container"style="<c:choose><c:when test="${sessionScope.indication == 'list_view'}">display: none;</c:when>
													<c:when test="${sessionScope.indication == 'thumbnail_view'}">display: block;</c:when>
													<c:otherwise>display: none;</c:otherwise></c:choose>">
				<c:forEach var="item" items="${sessionScope.searchResultForm.getSearchResultList()}" varStatus="status" begin = "${iterateOffest}" end = "${iterateLength + iterateOffest - 1}">
					<div class="galleryr">
						<c:choose>
							<c:when test="${item.aclFlag == 1 }">
								<img src="<%=request.getContextPath()%>/resources/img/thumb/${item.thumbnailName}" class="thumbnail large"/>
							</c:when>
							<c:otherwise>
								<img src="<%=request.getContextPath()%>/resources/img/thumb/NotAccess_thumb.jpg" class="thumbnail large"/>
							</c:otherwise>
						</c:choose>
						<div class="controls">
							<input type="checkbox" id="checkbox${status.index}" value="true" <c:if test="${item.selected}">checked="checked"</c:if>
								onchange="updateCheckbox(this, ${status.index})" class="checkbox large"/>
							<a id="drwgNoLink[${status.index}]" href='<c:url value="/preview"/>' title='<c:out value="${item.aclBalloon}"/>' 
								onclick="return openDLManagerDialog(${status.index});" class="drwgNo large">${item.drwgNoFormated}</a>
						</div>
					</div>
					<input type="hidden" id="DRWG_NO[${status.index}]" value="${item.drwgNo}"/>
					<input type="hidden" id="FILE_NAME[${status.index}]" value="${item.fileName}"/>
					<input type="hidden" id="PATH_NAME[${status.index}]" value="${item.pathName}"/>
					<input type="hidden" id="PDF[${status.index}]" value="${item.linkParmMap['PDF']}"/>
					<input type="hidden" id="PRINT_SIZE[${status.index}]" value="${item.printSize}"/>
				</c:forEach>
			</div>
		</table>
		<table class="nowsearch" id="nowSearch" style="visibility: hidden">
			<tr valign="middle">
				<td align="center" style="font-size: 18pt; color: #0000FF;"><c:choose>
						<c:when test="${sessionScope.user.language == 'Japanese' }">
検索中・・・・
</c:when>
						<c:otherwise>
Now Searching...
</c:otherwise>
					</c:choose></td>
			</tr>
		</table>
		<%-- IE互換性表示対策--%>
		<%-- パスワード変更画面起動中のクリック防止--%>
		<table class="nowsearch" id="screenLock" style="visibility: hidden">
			<tr>
				<td></td>
			</tr>
		</table>
	</form>
</body>
</html>
