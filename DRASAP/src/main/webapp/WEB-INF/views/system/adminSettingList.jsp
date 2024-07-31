<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page import="tyk.drasap.system.AdminSettingListElement" %>


<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>DRASAP [管理者設定変更]</title>
	<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<style type="text/css">@import url( <%=request.getContextPath()%>/resources/css/default.css );</style>
	<style type="text/css">
		.headFrame {
			position: relative;
			margin: 0px;
			padding: 0px;
			height: 100px;
		}
		
		.ListHeadFrame {
			position: absolute;
			width: 100%;
			margin: 0px;
			padding: 0px;
			/*	margin-right:15px;*/
			/*	border:1px solid #FF0000;*/
			bottom: 0px;
		}
		
		.ListHead {
			position: relative;
			margin: 0px;
			padding: 0px;
			top: 1px;
		}
		
		.bodyFrame {
			position: relative;
			margin: 0px;
			padding: 0px;
			overflow: auto;
			/*	border:1px solid #FF0000;*/
		}
		
		.footFrame {
			position: relative;
			margin: 0px;
			padding: 0px;
			height: 50px;
		}
	</style>
	<script type="text/javascript">
		function init() {
			frameResize();
		}
		browserName = navigator.appName;
		// 遷移する
		function submitFunc(parm){
			if (parm == "LOGOUT"){
				document.forms[0].target="_parent";
				document.forms[0].act.value='logout';// 隠し属性actをセット
				document.forms[0].submit();
				return;
			} else if (parm == "ADMINMENU"){
				// 管理メニューへ
				document.forms[0].act.value='adminmenu';// 隠し属性actをセット
				document.forms[0].submit();
				return;
			} else if (parm == "MASTERMAINTENANCEMENU"){
				// マスターメンテナンスメニューへ
				document.forms[0].act.value='masterMaintenanceMenu';// 隠し属性actをセット
				document.forms[0].submit();
				return;
			} else if (parm == "MENU"){
				// メニューへ
				document.forms[0].act.value='menu';// 隠し属性actをセット
				document.forms[0].submit();
				return;
			} else if (parm == "ONCHANGE"){
				// 更新時刻表示更新
				document.forms[0].act.value='onchange';// 隠し属性actをセット
				document.forms[0].submit();
				return;
			} else if (parm == "UPDATE"){
				// リスト更新
				document.forms[0].act.value='update';// 隠し属性actをセット
				document.forms[0].submit();
				return;
			} else {
				return;
			}
		}
		function changeValue(idx){
			// 更新時刻表示更新
			document.forms[0].act.value='onchange';// 隠し属性actをセット
			document.forms[0].updateIndex.value=idx;//
			var chkList=document.getElementsByName('adminSettingList['+idx+'].update');
		//			alert("checked="+chkList[0].checked);//
			chkList[0].checked=true;
		//			document.forms[0].submit();
			return;
		}
		// ログアウト処理
		function doLogout(){
		}
		function frameResize() {
		}
	</script>
</head>
<body bgcolor="#F5F5DC" onload="init()" onresize="frameResize()"
	bottommargin="0" leftmargin="0" topmargin="5" rightmargin="0"
	marginheight="0" marginwidth="0">
	<form action="<%=request.getContextPath()%>/adminSettingList"
		method="post">
		<input type="hidden" name="act" value="" /> <input type="hidden"
			name="updateIndex" value="" />

		<table id="ListHead" border="1" cellspacing="0" cellpadding="0"
			class="normal10">
			<colgroup span="5">
				<col style="width: 280px;"></col>
				<col style="width: 180px;"></col>
				<col style="width: 80px;"></col>
				<col style="width: 140px;"></col>
				<col style="width: 120px;"></col>
			</colgroup>
			<tr bgcolor="#A1A0C0">
				<td nowrap="nowrap" align="center">項目名称</td>
				<td nowrap="nowrap" align="center">設定値</td>
				<td nowrap="nowrap" align="center">ステータス</td>
				<td nowrap="nowrap" align="center">更新日</td>
				<td nowrap="nowrap" align="center">更新有／無</td>
			</tr>
			<c:forEach var="adminSettingListElement"
				items="${sessionScope.adminSettingListForm.adminSettingList}">
				<tr>
					<td nowrap="nowrap">&nbsp; <c:out
							value="${adminSettingListElement.itemName}" />&nbsp;
					</td>
					<td nowrap="nowrap">&nbsp; <input type="text" name="val"
						value="<c:out value='${adminSettingListElement.val}' />"
						onchange="changeValue(<c:out value='${status.index}' />)" />&nbsp;
					</td>
					<td align="center" nowrap="nowrap"><select name="status"
						onchange="changeValue(<c:out value='${status.index}' />)">
							<c:forEach var="status"
								items="${adminSettingListElement.statusList}">
								<option value="<c:out value='${status}' />"
									<c:if test="${status eq adminSettingListElement.status}">selected</c:if>><c:out
										value="${fn:toUpperCase(status)}" /></option>
							</c:forEach>
					</select></td>
					<td nowrap="nowrap">&nbsp; <c:out
							value="${adminSettingListElement.modifiedDate}" />&nbsp;
					</td>
					<td nowrap="nowrap" align="center">&nbsp; <input
						type="checkbox" name="update"
						<c:if test="${adminSettingListElement.update}">checked</c:if> />&nbsp;
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
					value="更新" onclick="submitFunc('UPDATE')" /></td>
			</tr>
		</table>
	</form>
</body>
</html>


