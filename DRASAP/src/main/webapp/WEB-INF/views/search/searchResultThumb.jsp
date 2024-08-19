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
	<title>Drawing Search and Print System [印刷指示画面]</title>
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<style type="text/css">@import url( <%=request.getContextPath() %>/resources/css/<%=session.getAttribute("default_css")%> );</style>
	<style>
		html, body {
		height: 100%;
			margin: 0;
		}
		.page {
			display: flex;
			flex-direction: column;
			height: 100%;
		}
		.header {
			text-align: center;
			margin-top: 20px;
		}
		.content {
			flex: 1;
		}
		.footer {
			position: fixed;
			bottom: 0;
			right: 0;
			padding: 10px;
			min-height: 10px;
		}
	</style>
	<script type="text/javascript">
		// 属性actをセットして、サブミット
		function setActSubmit(parm){
			document.forms[0].act.value=parm;// 隠し属性actにをセット
			document.forms[0].target="_top";// ターゲットは画面全体
			document.forms[0].submit();
		}

		// 印刷指示
		function printerIndicationOut(parm){
			if(!confirm("出力を行いますか?")){
				return;
			}
			document.forms[0].act.value=parm;// 隠し属性actにをセット
			document.forms[0].target="_parent";// ターゲットは画面全体
			document.forms[0].submit();
		}

		function isChecked(index) {
			var ischecked = document.getElementById('checkbox' + index).checked;
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
	</script>
</head>
<body>
	<c:if test="${message != null}">
		<font color="RED">
			<ul>
				<c:forEach var="msg" items="${message}">
					<li>${msg}</li>
				</c:forEach>
			</ul>
		</font>
		<div align="center">
			<input type="button" value="${sessionScope.searchResultForm.f_label12}" onclick="setActSubmit('SEARCH')" style="font-size:12pt;" />
		</div>
	</c:if>
	<div class="page">
		<div class="content">
			<form action="<%=request.getContextPath() %>/result" method="post">
				<input type="hidden" name="act" value="" />
				<c:if test="${message == null}">
					<div class="header"><span class="normal10"><h2>${sessionScope.searchResultForm.h_label11}</h2></span></div>
					<table border="0" cellspacing="1" cellpadding="0" align="center">	
						<%-- userを定義する --%>
						<c:set var="user" value="${sessionScope.user}" />
						<tr>
							<td></td>
							<td nowrap="nowrap" align="center" bgcolor="#CCCCCC">
								<span class="normal10">${sessionScope.searchResultForm.dispOutputSizeName}</span>
							</td>
							<td nowrap="nowrap" align="center" bgcolor="#CCCCCC">
								<span class="normal10">${sessionScope.searchResultForm.dispCopiesName}</span>
							</td>
							<td nowrap="nowrap" align="center" bgcolor="#CCCCCC">
								<span class="normal10">${sessionScope.searchResultForm.dispDwgNoName}</span>
							</td>
							<c:forEach begin="1" end="${sessionScope.searchResultForm.getViewSelColNum()}" var="index">
								<td nowrap="nowrap" align="center" bgcolor="#CCCCCC">
									<span class="normal10">${sessionScope.searchResultForm.getDispAttrName(index - 1)}</span>
								</td>
							</c:forEach>
						</tr>
						<c:forEach var="item" items="${sessionScope.searchResultForm.getSearchResultList()}" varStatus="status">
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
							<tr bgcolor="${bgcolor1}" style="<c:if test="${!item.selected}">display: none;</c:if>">
								<c:choose>
									<c:when test="${item.aclFlag == 1}">
										<td>
											<input type="hidden" id="checkboxHidden${status.index}" value="${item.selected}"/>
											<input type="checkbox"id="checkbox${status.index}" value="true" onclick="isChecked(${status.index})" <c:if test="${item.selected}">checked="checked"</c:if>/>
										</td>
									</c:when>
									<c:otherwise>
										<td />
									</c:otherwise>
								</c:choose>
								<td align="center">
									<input type="hidden" id="printSizeHidden${status.index}" value="${item.printSize}"/>
									<select name="printSize" id="printSizeSelect${status.index}" onchange="updateHiddenInput(${status.index}, 'printSize')">
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
									</select>
								</td>
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
									</select>
								</td>
								<td nowrap="nowrap">
									<span class="normal12">${item.drwgNoFormated}</span>
								</td>
								<c:forEach begin="1" end="${sessionScope.searchResultForm.getViewSelColNum()}" var="index">
									<td nowrap="nowrap">
										<span class="normal12">&nbsp; ${item.getAttr(sessionScope.searchResultForm.getDispAttr(index - 1))}&nbsp;</span>
									</td>
								</c:forEach>
							</tr>
						</c:forEach>
					</table>
				</c:if>
			</form>
		</div>
		<c:if test="${message == null}">
			<div class="footer">
				<span class="normal10">
					${searchResultForm.h_label5 }&nbsp;
					<select name="outputPrinter">
						<c:forEach items="${searchResultForm.printerKeyList}" var="outputPrinterKey" varStatus="loop">
							<option value="${outputPrinterKey}" <c:if test="${outputPrinterKey == searchResultForm.outputPrinter}">selected</c:if>>${searchResultForm.printerNameList[loop.index]}</option>
						</c:forEach>
					</select>
				</span>
				<span class="normal10">
					&nbsp;&nbsp;
					<input type="button" value="　${searchResultForm.f_label4}　" onclick="printerIndicationOut('PRIENTER_THUMBNAIL')" />
				</span>
				<span class="normal10">
					&nbsp;&nbsp;
					<input type="button" value="${searchResultForm.f_label13}" onclick="setActSubmit('SEARCH')" />
				</span>
			</div>
		</c:if>
	</div>
</body>