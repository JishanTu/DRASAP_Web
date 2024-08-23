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
<script type="text/javascript">
<!--
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
//--->
</script>
</head>
<body bgcolor="#F5F5DC" bottommargin="0" leftmargin="5" topmargin="5" rightmargin="5" marginheight="0" marginwidth="0">
<html:form action="/accessLevelMasterMaintenance">
	<html:hidden property="act" />
	<table name="List" align="center" border="1" cellspacing="0" cellpadding="0" class="normal12">
		<tr bgcolor="#A1A0C0">
			<td>アクセスレベルＩＤ</td>
			<td>アクセスレベル名</td>
			<td>更新有／無</td>
		</tr>
		<nested:iterate id="accessLevelMasterMaintenanceElement" type="tyk.drasap.system.AccessLevelMasterMaintenanceElement"
			indexId="idx" name="accessLevelMasterMaintenanceForm" property="recList" scope="session">
			<tr>
				<td nowrap="nowrap"><nested:equal value="true" property="new" scope="session">
					<nested:text style="<%=accessLevelMasterMaintenanceElement.getAclIdStyle()%>" property="aclId"
						onchange='<%= "changeValue(" + idx + ")" %>' />
				</nested:equal> <nested:equal value="false" property="new" scope="session">
					<nested:write property="aclId" />
				</nested:equal></td>
				<td nowrap="nowrap"><nested:text property="aclName" onchange='<%= "changeValue(" + idx + ")" %>' /></td>
				<td nowrap="nowrap"><nested:checkbox property="update" /> <nested:hidden property="new" /></td>

			</tr>
		</nested:iterate>
	</table>
	<table align="center" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td align="center" nowrap="nowrap"><input type="button" value="レコード追加" onclick="backPage('ADDRECORD')" /></td>
			<td>&nbsp;&nbsp;&nbsp;</td>
			<td align="center" nowrap="nowrap"><input type="button" value="更新" onclick="backPage('UPDATE')" /></td>
			<td>&nbsp;&nbsp;&nbsp;</td>
			<td align="center" nowrap="nowrap"><input type="button" value="削除" onclick="backPage('DELETE')" /></td>
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
		<nested:iterate id="errorMessage" type="java.lang.String" name="accessLevelMasterMaintenanceForm" property="errorMsg"
			scope="session">
			<tr>
				<td style="color:#FF0000"><bean:write name="errorMessage" /></td>
			</tr>
		</nested:iterate>
	</table>
</html:form>
</body>
</html>


