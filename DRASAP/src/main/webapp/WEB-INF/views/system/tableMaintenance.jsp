<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="tyk.drasap.system.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page import="tyk.drasap.system.TableMaintenanceForm"%>

<%-- ログイン情報の確認 --%>
<c:if test="${empty sessionScope.user}">
<script>
	location.replace('<%=request.getContextPath()%>/timeout');
</script>
</c:if>

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>DRASAP [マスターテーブルメンテナンス]</title>
	<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<style type="text/css">@import url( <%=request.getContextPath()%>/resources/css/default.css );</style>
	<script type="text/javascript">
		browserName = navigator.appName;
		// 遷移する
		function submitFunc(parm){
			if (parm == "LOGOUT"){
				document.forms[0].target="_parent";
				document.forms[0].act.value='logout';// 隠し属性actをセット
				document.forms[0].submit();
				return;
			} else if (parm == "MASTERMAINTENANCEMENU"){
				// マスターメンテナンスメニューへ
				document.forms[0].act.value='masterMaintenanceMenu';// 隠し属性actをセット
				document.forms[0].submit();
				return;
			} else if (parm == "ADMINMENU"){
				// 管理メニューへ
				document.forms[0].act.value='adminmenu';// 隠し属性actをセット
				document.forms[0].submit();
				return;
			} else if (parm == "MENU"){
				// メニューへ
				document.forms[0].act.value='menu';// 隠し属性actをセット
				document.forms[0].submit();
				return;
			} else if (parm == "UPDATE"){
				// リスト更新
				document.forms[0].act.value='update';// 隠し属性actをセット
				document.forms[0].submit();
				return;
			} else if (parm == "DELETE"){
				// リスト更新
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
			} else if (parm == "PREVPAGE"){
				// リスト再表示
				document.forms[0].act.value='prevpage';// 隠し属性actをセット
				document.forms[0].submit();
				return;
			} else if (parm == "NEXTPAGE"){
				// リスト再表示
				document.forms[0].act.value='nextpage';// 隠し属性actをセット
				document.forms[0].submit();
				return;
			} else if (parm == "DIRECTPAGE"){
				// リスト再表示
				document.forms[0].act.value='directpage';// 隠し属性actをセット
				document.forms[0].submit();
				return;
			} else if (parm == "EXPORT"){
				// ＣＳＶ出力
				document.forms[0].act.value='export';// 隠し属性actをセット
				document.forms[0].submit();
				return;
			} else if (parm == "INPORT"){
				// ＣＳＶ出力
				document.forms[1].act.value='inport';// 隠し属性actをセット
				document.forms[1].submit();
				return;
			} else if (parm == "WHERESEARCH"){
				// 条件検索
				document.forms[0].act.value='wheresearch';// 隠し属性actをセット
				document.forms[0].submit();
				return;
			} else {
				return;
			}
		}
		function changeValue(idx){
			// 更新フラグ設定
			document.forms[0].act.value='onchange';// 隠し属性actをセット
			document.forms[0].updateIndex.value=idx;//
			var chkList=document.getElementsByName('recList['+idx+'].check');
			chkList[0].checked=true;
			return;
		}
		// ログアウト処理
		function doLogout(){
		}
	</script>
</head>
<body bgcolor="#F5F5DC" bottommargin="0" leftmargin="0" topmargin="0"
	rightmargin="0" marginheight="0" marginwidth="0">
	<form action="<%=request.getContextPath()%>/tableMaintenance"
		method="post">
		<input type="hidden" name="act" value="" /> <input type="hidden"
			name="updateIndex" value="" />
		<!--------------- ヘッダ -------------------------------->

		<table align="center" border="0" cellspacing="0" cellpadding="0"
			class="normal12">
			<tr>
				<td>テーブル名称</td>
				<td><select name="selectTable" onchange="submitFunc('SEARCH')">
						<c:forEach var="table"
							items="${sessionScope.tableMaintenanceForm.tableList}">
							<option value="<c:out value='${table}' />"><c:out
									value='${table}' /></option>
						</c:forEach>
				</select></td>
				<td>&nbsp;&nbsp;&nbsp;where&nbsp;&nbsp;</td>
				<td align="center" nowrap="nowrap"><input type="text"
					name="whereStr"
					value="<c:out value='${tableMaintenanceForm.whereStr}' />" /></td>
				<td align="center" nowrap="nowrap"><input type="button"
					value="検索" onclick="submitFunc('WHERESEARCH')" /></td>
				<td><span class="normal12">&nbsp;&nbsp;&nbsp;&nbsp;</span></td>
				<td><span class="normal12">&nbsp;&nbsp;&nbsp;&nbsp;</span></td>
				<td><span class="normal12">&nbsp;&nbsp;&nbsp;&nbsp;</span></td>
				<td><span class="normal12">&nbsp;&nbsp;&nbsp;&nbsp;</span></td>
				<td align="center" nowrap="nowrap"><input type="button"
					value="前ページ" onclick="submitFunc('PREVPAGE')" /></td>
				<td>&nbsp;&nbsp;&nbsp;</td>
				<td align="center" nowrap="nowrap"><input type="button"
					value="次ページ" onclick="submitFunc('NEXTPAGE')" /></td>
				<td>&nbsp;&nbsp;&nbsp;</td>
				<td align="center" nowrap="nowrap"><select name="selectPage"
					onchange="submitFunc('DIRECTPAGE')">
						<c:forEach var="page" items="${tableMaintenanceForm.pageList}">
							<option value="<c:out value='${page.value}' />"><c:out
									value='${page.label}' /></option>
						</c:forEach>
				</select></td>
				<td><span class="normal12">／</span></td>
				<td><span class="normal12"><c:out
							value="${tableMaintenanceForm.recCount}" /></span></td>
			</tr>
		</table>
		<br />
		<table align="center" border="1" cellspacing="0" cellpadding="0"
			class="normal10">
			<tr>
				<c:forEach var="attr"
					items="${sessionScope.tableMaintenanceForm.attrList}">
					<td nowrap="nowrap" bgcolor="#A1A0C0"><c:out
							value="${attr.column_name}" /></td>
				</c:forEach>
				<c:if test="${not empty sessionScope.tableMaintenanceForm.recList}">
					<td nowrap="nowrap" bgcolor="#A1A0C0">選択</td>
				</c:if>
			</tr>
			<c:forEach var="rec"
				items="${sessionScope.tableMaintenanceForm.recList}"
				varStatus="recStatus">
				<tr>
					<c:forEach var="val" items="${rec.valList}" varStatus="valStatus">
						<td nowrap="nowrap"><c:choose>
								<c:when
									test="${sessionScope.tableMaintenanceForm.attrList[valStatus.index].key && rec.new}">
									<input type="text" style="${val.dispStyle}" name="val"
										value="${val}" onchange="changeValue(${recStatus.index})" />
								</c:when>
								<c:otherwise>
									<c:out value="${val}" />
								</c:otherwise>
							</c:choose></td>
					</c:forEach>
					<td nowrap="nowrap">&nbsp;<input type="checkbox" name="check"
						value="${check}" />&nbsp;
					</td>
					<input type="hidden" name="new" value="${new}" />
				</tr>
			</c:forEach>


		</table>
		<table align="center" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td align="center" nowrap="nowrap"><input type="button"
					value="レコード追加" onclick="submitFunc('ADDRECORD')" /></td>
				<td>&nbsp;&nbsp;&nbsp;</td>
				<td align="center" nowrap="nowrap"><input type="button"
					value="更新" onclick="submitFunc('UPDATE')" /></td>
				<td>&nbsp;&nbsp;&nbsp;</td>
				<td align="center" nowrap="nowrap"><input type="button"
					value="削除" onclick="submitFunc('DELETE')" /></td>
				<td>&nbsp;&nbsp;&nbsp;</td>
				<td align="center" nowrap="nowrap"><input type="button"
					value="エクスポート" onclick="submitFunc('EXPORT')" /></td>
				<td>&nbsp;&nbsp;&nbsp;</td>
			</tr>
		</table>
	</form>
	<form action="<%=request.getContextPath()%>/tableMaintenance"
		enctype="multipart/form-data">
		<input type="hidden" name="act" value="inport" />
		<!--
<table align="center" border="0" cellspacing="0" cellpadding="0">
			<td>
  			<html:file property="fileUp" size="64" />
  			<input type="button" value="インポート" onclick="submitFunc('INPORT')" />
			</td>
</table>
-->
		<table align="center" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td><br /></td>
			</tr>
			<tr>
				<td><br /></td>
			</tr>
			<c:forEach var="errorMessage"
				items="${sessionScope.tableMaintenanceForm.errorMsg}">
				<tr>
					<td style="color: #FF0000">${errorMessage}</td>
				</tr>
			</c:forEach>

		</table>
	</form>
</body>
</html>


