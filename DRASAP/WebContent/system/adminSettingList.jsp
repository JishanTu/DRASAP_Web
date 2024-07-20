<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>DRASAP [管理者設定変更]</title>
<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Cache-Control" content="no-cache" />
<style type="text/css">@import url( <%=request.getContextPath() %>/default.css );</style>
<style type="text/css">
	.headFrame {
		position:relative;
		margin:0px;
		padding:0px;
		height:100px;
	}
	.ListHeadFrame {
		position:absolute;
		width:100%;
		margin:0px;
		padding:0px;
	/*	margin-right:15px;*/
	/*	border:1px solid #FF0000;*/
		bottom:0px;
	}
	.ListHead {
		position:relative;
		margin:0px;
		padding:0px;
		top:1px;
	}
	.bodyFrame {
		position:relative;
		margin:0px;
		padding:0px;
		overflow:auto;
	/*	border:1px solid #FF0000;*/
	}
	.footFrame {
		position:relative;
		margin:0px;
		padding:0px;
		height:50px;
	}
</style>
<script type="text/javascript">
<!--
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

//--->
</script>
</head>
<body bgcolor="#F5F5DC" onload="init()" onresize="frameResize()" bottommargin="0" leftmargin="0" topmargin="5"
	rightmargin="0" marginheight="0" marginwidth="0">
<html:form action="/adminSettingList">
	<html:hidden property="act" />
	<html:hidden property="updateIndex" />

	<table id="ListHead" border="1" cellspacing="0" cellpadding="0" class="normal10">
		<colgroup span="5">
			<col style="width:280px;"></col>
			<col style="width:180px;"></col>
			<col style="width:80px;"></col>
			<col style="width:140px;"></col>
			<col style="width:120px;"></col>
		</colgroup>
		<tr bgcolor="#A1A0C0">
			<td nowrap="nowrap" align="center">項目名称</td>
			<td nowrap="nowrap" align="center">設定値</td>
			<td nowrap="nowrap" align="center">ステータス</td>
			<td nowrap="nowrap" align="center">更新日</td>
			<td nowrap="nowrap" align="center">更新有／無</td>
		</tr>
		<nested:root name="adminSettingListForm">
			<nested:iterate id="adminSettingListElement" type="tyk.drasap.system.AdminSettingListElement" indexId="idx"
				name="adminSettingListForm" property="adminSettingList" scope="session">
				<tr>
					<td nowrap="nowrap">&nbsp; <nested:write property="itemName" />&nbsp;</td>
					<td nowrap="nowrap">&nbsp; <nested:text property="val" onchange='<%= "changeValue(" + idx+ ")" %>' />&nbsp;</td>
					<td align="center" nowrap="nowrap"><nested:select property="status" onchange='<%= "changeValue(" + idx + ")" %>'>
						<html:options labelName="adminSettingListForm" labelProperty="statsuNameList" name="adminSettingListForm"
							property="statusList" />
					</nested:select></td>
					<td nowrap="nowrap">&nbsp; <nested:write property="modifiedDate" />&nbsp;</td>
					<td nowrap="nowrap" align="center">&nbsp; <nested:checkbox property="update" />&nbsp;</td>

				</tr>
			</nested:iterate>
		</nested:root>
	</table>
	<table align="center" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td align="center" nowrap="nowrap"><input type="button" value="更新" onclick="submitFunc('UPDATE')" /></td>
		</tr>
	</table>
</html:form>
</body>
</html>


