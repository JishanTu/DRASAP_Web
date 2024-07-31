<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
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
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<style type="text/css">@import url( <%=request.getContextPath()%>/resources/css/default.css );</style>
	<script type="text/javascript">
		browserName = navigator.appName;
		// 遷移する
		function backPage(parm){
			if (parm == "UPDATE"){
				// リスト更新
				document.forms[0].act.value='update';// 隠し属性actをセット
				document.forms[0].submit();
				return;
			} else if (parm == "DELETE"){
				if(! confirm("選択しているアクセスレベルを削除します。\nアクセスレベルを削除すると、関連する権限も削除されます。よろしいですか?")){
					//alert(parm);
					return;
				}
				// リスト削除
				document.forms[0].act.value='delete';// 隠し属性actをセット
				document.forms[0].submit();
				return;
			} else if (parm == "SEARCH"){
				// リスト再表示
				document.forms[0].act.value='search';// 隠し属性actをセット
				document.forms[0].submit();
				return;
			} else if (parm == "ADDRECORD"){
				// リスト再表示
				document.forms[0].act.value='addrecord';// 隠し属性actをセット
				document.forms[0].submit();
				return;
			} else {
				return;
			}
		}
		function changeValue(idx){
			//
			document.forms[0].act.value='onchange';// 隠し属性actをセット
			var chkList=document.getElementsByName('recList['+idx+'].update');
			chkList[0].checked=true;
			return;
		}
		// ログアウト処理
		function doLogout(){
		}
	</script>
</head>
<body bgcolor="#F5F5DC" bottommargin="0" leftmargin="5" topmargin="5"
	rightmargin="5" marginheight="0" marginwidth="0">
	<form
		action="<%=request.getContextPath() %>/accessLevelMasterMaintenance"
		method="post">
		<input type="hidden" name="act" value="" />
		<table name="List" align="center" border="1" cellspacing="0"
			cellpadding="0" class="normal12">
			<tr bgcolor="#A1A0C0">
				<td>アクセスレベルＩＤ</td>
				<td>アクセスレベル名</td>
				<td>更新有／無</td>
			</tr>
			<c:forEach var="accessLevelMasterMaintenanceElement"
				items="${sessionScope.accessLevelMasterMaintenanceForm.recList}"
				varStatus="status">
				<tr>
					<td nowrap="nowrap"><c:choose>
							<c:when test="${accessLevelMasterMaintenanceElement.new == true}">
								<input type="text"
									style="<c:out value='${accessLevelMasterMaintenanceElement.aclIdStyle}' />"
									name="aclId"
									value="<c:out value='${accessLevelMasterMaintenanceElement.aclId}' />"
									onchange="changeValue(<c:out value='${status.index}' />)" />
							</c:when>
							<c:otherwise>
								<c:out value="${accessLevelMasterMaintenanceElement.aclId}" />
							</c:otherwise>
						</c:choose></td>
					<td nowrap="nowrap"><input type="text" name="aclName"
						value="<c:out value='${accessLevelMasterMaintenanceElement.aclName}' />"
						onchange="changeValue(<c:out value='${status.index}' />)" /></td>
					<td nowrap="nowrap"><input type="checkbox" name="update"
						value="true"
						<c:if test="${accessLevelMasterMaintenanceElement.update}">checked</c:if> />
						<input type="hidden" name="new"
						value="<c:out value='${accessLevelMasterMaintenanceElement.new}' />" />
					</td>
				</tr>
			</c:forEach>
		</table>
		<table align="center" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td align="center" nowrap="nowrap"><input type="button"
					value="レコード追加" onclick="backPage('ADDRECORD')" /></td>
				<td>&nbsp;&nbsp;&nbsp;</td>
				<td align="center" nowrap="nowrap"><input type="button"
					value="更新" onclick="backPage('UPDATE')" /></td>
				<td>&nbsp;&nbsp;&nbsp;</td>
				<td align="center" nowrap="nowrap"><input type="button"
					value="削除" onclick="backPage('DELETE')" /></td>
			</tr>
		</table>
		<table align="center" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td><br /></td>
			</tr>
			<tr>
				<td><br /></td>
			</tr>
			<c:forEach var="errorMessage"
				items="${sessionScope.accessLevelMasterMaintenanceForm.errorMsg}">
				<tr>
					<td style="color: #FF0000"><c:out value="${errorMessage}" /></td>
				</tr>
			</c:forEach>
		</table>
	</form>
</body>
</html>


