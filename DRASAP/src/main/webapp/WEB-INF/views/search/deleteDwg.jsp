<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="tyk.drasap.search.*,tyk.drasap.common.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%-- ログイン情報の確認 --%>
<c:if test="${empty sessionScope.user}">
<script>
	location.replace('<%=request.getContextPath()%>/timeout');
</script>
</c:if>

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta content="text/html; charset=UTF-8" http-equiv="Content-type" />
	<meta content="no-cache" http-equiv="Pragma" />
	<meta content="no-cache" http-equiv="Cache-Control" />
	<style type="text/css">@import url( <%=request.getContextPath()%>/resources/css/default.css );</style>
	<style type="text/css">
		.deleteBtn {
			width: 100%;
		/* 2022.04.13 Windows Edge対応. 削除ボタンが押せない障害を修正.
		    position:absolute;
		*/
			text-align: center;
		}
		
		.goBackBtn {
			width: 100%;
			position: absolute;
			text-align: right;
			padding-right: 30px;
			padding-bottom: 30px;
		}
	</style>
	<script type="text/javascript">
		browserName = navigator.appName;
		// 遷移する
		function submitFunc(parm){
		    if (parm == "DELETE") {
                var msg1 = document.getElementById("msg1");
                var msg2 = document.getElementById("msg2");
                msg1.innerHTML = "削除しています。しばらくお待ちください。";
                msg2.innerHTML = "";

                document.body.style.cursor="wait";
                var deleteButton = document.getElementById("deleteButton");
                var backButton = document.getElementById("backButton");
                deleteButton.disabled=true;
                backButton.disabled=true;
                document.forms[0].act.value='delete';// 隠し属性actをセット
                document.forms[0].submit();
		    }
		}
		// ログアウト処理
		function doLogout(){
		}
	    function backSearchResult(){
			parent.condition.document.forms[0].act.value="search";// 隠し属性actに'search'をセット
			parent.condition.document.forms[0].target="result";// targetは'result'
			parent.condition.document.forms[0].submit();
	    }
	</script>
</head>
<body marginwidth="0" marginheight="0" bgcolor="#ffffff" rightmargin="0" topmargin="0" leftmargin="0" bottommargin="0">
	<c:set var="deleteDwgForm" scope="session" value="${sessionScope.deleteDwgForm}" />

	<form action="<%=request.getContextPath() %>/deleteDwg" method="post">
		<!--
		<c:set var="deleteDwgForm" scope="session" value="${sessionScope.deleteDwgForm}" />
		<c:set var="deleteDwgForm" value="${sessionScope.deleteDwgForm}" />
		-->

		<table cellspacing="0" cellpadding="0" border="0">
			<tbody>
				<tr>
					<td>
						<input type="hidden" name="act" value="${deleteDwgForm.act}" />
						<br />
						<input type="hidden" name="previewIdx" value="${deleteDwgForm.previewIdx}" />
					</td>
				</tr>
				<tr>
					<td>削除図面</td>
				</tr>
				<tr>
					<td>
						<table cellspacing="0" cellpadding="3" border="1" align="left">
							<tbody>
								<tr bgcolor="#cccccc" class="normal10">
									<c:forEach var="colName" items="${deleteDwgForm.colNameJPList}">
										<th nowrap="nowrap"><c:out value="${colName}" /></th>
									</c:forEach>
								</tr>
								<c:forEach var="record" items="${deleteDwgForm.recList}">
									<tr class="normal12">
										<c:forEach var="value" items="${record.valList}"
											varStatus="status">
											<td nowrap="nowrap">
												<c:choose>
													<c:when test="${status.index == 0}">
														<a href="<c:url value='preview'/>" target="_parent">
															<c:out value="${value}" />
															<br />
														</a>
													</c:when>
													<c:otherwise>
														<c:out value="${value}" />
														<br />
													</c:otherwise>
												</c:choose></td>
										</c:forEach>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</td>
				</tr>
				<tr>
					<td>
						<br />
						<br />
					</td>
				</tr>
				<tr>
					<td>
						<c:if test="${message != null}">
							<hr style="border: none; height: 1px; background-color: orange;" />
							<c:forEach var="msg" items="${message}" varStatus="status">
								<li style="margin-left: 30px; line-height: 1.5; color: red; border-left: 0px;">${msg}</li>
							</c:forEach>
							<hr style="border: none; height: 1px; background-color: orange;" />
						</c:if>
					</td>
				</tr>
			</tbody>
		</table>

		<table cellspacing="0" cellpadding="0" border="0" align="center"
			class="normal12blue">
			<tbody>
				<tr>
					<td id="msg1"><c:out value="${deleteDwgForm.msg1}" /></td>
				</tr>
				<tr>
					<td id="msg2"><c:out value="${deleteDwgForm.msg2}" /></td>
				</tr>
				 <%-- 2022.04.13 Windows Edge対応. 削除ボタンが押せない障害を修正. --%>
				<tr>
					<td align="center">
						<input type="button" style="width: 80px; margin-right:20px;" onclick="submitFunc('DELETE')" value="削除" id="deleteButton" ${deleteDwgForm.isDeleteOK() ? '' : 'disabled'} />
						<input type="button" onclick="backSearchResult()" value="　戻る　" id="backButton"  />
					</td>
				</tr>
			</tbody>
		</table>
		
		<%-- 2022.04.13 Windows Edge対応. 削除ボタンが押せない障害を修正.
		<span class="deleteBtn"> <input type="button" style="width: 80px;" onclick="submitFunc('DELETE')" value="削除" id="deleteButton" ${deleteDwgForm.deleteOK ? '' : 'disabled'} /></span>
		<span class="goBackBtn"> <input type="button" onclick="backSearchResult()" value="　戻る　" id="backButton" />	</span>
		--%>
	</form>
</body>
</html>

