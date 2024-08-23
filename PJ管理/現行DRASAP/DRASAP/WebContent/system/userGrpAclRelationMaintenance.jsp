<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%-- ログイン情報の確認 --%>
<logic:notPresent name="user" scope="session">
	<logic:redirect forward="timeout" />
</logic:notPresent>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>

<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Cache-Control" content="no-cache" />
<style type="text/css">@import url( <%=request.getContextPath() %>/default.css );</style>
<style type="text/css">
<!--
	.headFrame {
		position:relative;
		margin:0px;
		padding:0px;
		height:70px;
	}
	.listHeadFrame {
		position:absolute;
		width:100%;
		margin:0px;
		padding:0px;
		overflow:hidden;
	/*	border:10px solid #FF0000;*/
		bottom:0px;
	}
	.ListHead {
		table-layout:fixed;
		position:relative;
		margin:0px;
		padding:0px;
		top:1px;
	}
	.List {
		table-layout:fixed;
	}
	.bodyFrame {
		width:100%;
		position:relative;
		margin:0px;
		padding:0px;
		overflow:auto;
	/*	border-width:1px;*/
	}
	.footFrame {
		position:relative;
		margin:0px;
		padding:0px;
		height:50px;
	}
	th
	{
		font-weight:normal;
		white-space:nowrap;
	}
	tr
	{
		height:25px;
		padding:0px;
	}
	td {
		white-space:nowrap;
		padding:0px;
	}
-->
</style>
<script type="text/javascript">
<!--
	var browserName;
	var frameHead;
	var listHeadFrame;
	var ListHead;
	var List;
	var frameBody;
	var frameFoot;
	function onInit() {
		browserName = navigator.appName;

		frameHead = document.getElementById("headFrame");
		listHeadFrame = document.getElementById("listHeadFrame");
		ListHead = document.getElementById("ListHead");
		List= document.getElementById("List");
		frameBody = document.getElementById("bodyFrame");
		frameFoot = document.getElementById("footFrame");

		syncroScroll();

		frameBody.onscroll=syncroScroll;
		document.body.onresize=frameResize;
		frameResize();
	}
	if (browserName != "Netscape") focus();

	// 遷移する
	function backPage(parm){
		if (parm == "UPDATE"){
			// リスト更新
			document.forms[0].target="_parent";
			document.forms[0].act.value='update';// 隠し属性actをセット
			document.forms[0].submit();
			return;
		} else if (parm == "DELETE"){
			if(! confirm("利用者グループアクセスレベル関連を削除します。\nよろしいですか?")){
				//alert(parm);
				return;
			}
			// リスト削除
			document.forms[0].target="_parent";
			document.forms[0].act.value='delete';// 隠し属性actをセット
			document.forms[0].submit();
			return;
		} else if (parm == "SEARCH"){
			// リスト再表示
			document.forms[0].target="_parent";
			document.forms[0].act.value='search';// 隠し属性actをセット
			document.forms[0].submit();
			return;
		} else if (parm == "ADDRECORD"){
			// リスト再表示
			document.forms[0].target="_parent";
			document.forms[0].act.value='addrecord';// 隠し属性actをセット
			document.forms[0].submit();
			return;
		} else {
			return;
		}
	}
	function changeValue(orderBy){
		//
		document.forms[0].target="_self";
		document.forms[0].act.value='search';// 隠し属性actをセット
		document.forms[0].submit();
		return;
	}
	function changeFlg(idx){
		//
		document.forms[0].act.value='onchange';// 隠し属性actをセット
		var chkList=document.getElementsByName('recList['+idx+'].update');
		chkList[0].checked=true;
		return;
	}
	// リサイズ処理
	function frameResize() {
		var new_height = document.body.parentNode.clientHeight - frameHead.offsetHeight - frameFoot.offsetHeight - 16;
		if (new_height > 0) frameBody.style.height = new_height + "px";
		ListHead.style.left="-8px";
	window.status ="frameBody.offsetTop="+ frameBody.offsetTop;
	}
	function syncroScroll()
	{
		listHeadFrame.scrollLeft = frameBody.scrollLeft;

	}
-->
</script>
</head>
<body bgcolor="#F5F5DC" onload="onInit()" bottommargin="0" leftmargin="0" topmargin="0" rightmargin="0" marginheight="0" marginwidth="0">
<html:form action="/userGrpAclRelationMaintenance">
	<html:hidden property="act" />
	<!-- ============== ヘッダ ============== -->
	<div id="headFrame" class="headFrame">
	<table align="center" border="0" width="90%" cellspacing="0" cellpadding="0">
		<tr>
			<td align="left"><nested:radio property="orderBy" value="aclId" onclick="changeValue('aclId')">アクセスレベル順</nested:radio>
			<nested:radio property="orderBy" value="userGrpCode" onclick="changeValue('userGrpCode')">利用者グループ順</nested:radio></td>
		</tr>
	</table>

	<div id="listHeadFrame" align="center" class="listHeadFrame">
	<table id="ListHead" class="ListHead normal10" align="center" border="1" cellspacing="0" cellpadding="0">
		<colgroup span="4">
			<col style="width:140px;"></col>
			<col style="width:400px;"></col>
			<col style="width:80px;"></col>
			<col style="width:80px;"></col>
		</colgroup>
		<tr bgcolor="#A1A0C0">
			<th nowrap="nowrap">アクセスレベル</th>
			<th nowrap="nowrap">利用者グループ</th>
			<th nowrap="nowrap">権限</th>
			<th nowrap="nowrap">更新有／無</th>
		</tr>
	</table>
	</div>
	</div>
	<!-- ============== ＢＯＤＹ ============== -->
	<div id="bodyFrame" class="bodyFrame">
	<table id="List" class="List normal10" align="center" border="1" cellspacing="0" cellpadding="0">
		<colgroup span="4">
			<col style="width:140px;"></col>
			<col style="width:400px;"></col>
			<col style="width:80px;"></col>
			<col style="width:80px;"></col>
		</colgroup>
			<nested:iterate id="userGrpAclRelationMaintenanceElement"
				type="tyk.drasap.system.UserGrpAclRelationMaintenanceElement" indexId="idx" name="userGrpAclRelationMaintenanceForm"
				property="recList" scope="session">
				<tr>
					<td nowrap="nowrap">&nbsp; <nested:write property="aclName" />&nbsp; <nested:hidden property="aclId" /></td>
					<td nowrap="nowrap">&nbsp; <nested:write property="userGrpName" />&nbsp; <nested:hidden property="userGrpCode" /></td>
					<td align="center" nowrap="nowrap"><nested:select property="aclValue" onchange='<%= "changeValue(" + idx + ")" %>'>
						<html:options labelName="userGrpAclRelationMaintenanceForm" labelProperty="aclValueNameList"
							name="userGrpAclRelationMaintenanceForm" property="aclValueList" />
					</nested:select></td>
					<td align="center" nowrap="nowrap"><nested:checkbox property="update" /></td>

				</tr>
			</nested:iterate>
	</table>
	</div>
	<div id="footFrame" class="footFrame">
	<table align="center" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td align="center" nowrap="nowrap"></td>
			<td>&nbsp;&nbsp;&nbsp;</td>
			<td align="center" nowrap="nowrap"><input type="button" value="更新" onclick="backPage('UPDATE')" /></td>
			<td>&nbsp;&nbsp;&nbsp;</td>
			<td align="center" nowrap="nowrap"></td>
		</tr>
	</table>
	<table align="center" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><br />
			</td>
		</tr>
		<tr>
			<td><br />
			</td>
		</tr>
		<nested:iterate id="errorMessage" type="java.lang.String" name="userGrpAclRelationMaintenanceForm" property="errorMsg"
			scope="session">
			<tr>
				<td style="color:#FF0000"><bean:write name="errorMessage" /></td>
			</tr>
		</nested:iterate>
	</table>
	</div>
</html:form>
</body>
</html>


