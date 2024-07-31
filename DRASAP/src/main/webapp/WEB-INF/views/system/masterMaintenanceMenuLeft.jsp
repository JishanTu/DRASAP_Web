<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Cache-Control" content="no-cache" />
<style type="text/css">@import url( <%=request.getContextPath()%>/resources/css/default.css );</style>
<style type="text/css">
	.leftMenuItem {
		background-color:#7A79A7;
		border-color:#CCCCCC;
		border-style:solid;
		border-width:1px;
		color:#FFFFFF;
	}
	.leftMenuItemRev {
		background-color:#A1A0C0;
		border-color:#CCCCCC;
		border-style:solid;
		border-width:1px;
		color:#AA5957;
	}
	.leftMenuItem a {
		color:#FFFFFF;
	}
	img {
		border:none;
	}
</style>
<script type="text/javascript">
<!--
	browserName = navigator.appName;
	// それぞれのFunctionへ遷移する
	function showFunction(param){
		document.forms[0].act.value=param;//
		document.forms[0].target="_frameRight";
		document.forms[0].submit();
		return;
	}
	function invert_sub_item(item, flg) {
		if (flg) {
			item.className = "leftMenuItemRev"
		} else {
			item.className = "leftMenuItem"
		}
	}
//-->
</script>
</head>
<body bgcolor="#F5F5DC" bottommargin="0" leftmargin="5" topmargin="5" rightmargin="5" marginheight="0" marginwidth="0">
<form action="<%=request.getContextPath() %>/masterMaintenanceMenu" method = "post">
<input type = "hidden" name = "act" value = ""/>
<!-- left side -->
<table align="left" border="0" cellspacing="0" cellpadding="0" style="width:200px;cursor:pointer;">
	<tr><td>
		<table border="0" cellspacing="0" cellpadding="5" class="normal10">
			<tr>
				<td nowrap="nowrap" class="leftMenuItem" onmouseover="invert_sub_item(this,true)" onmouseout="invert_sub_item(this,false)" onclick="showFunction('adminSettingList')">
					<img src="../img/point.gif"/>管理者設定変更</td>
			</tr>
			<tr>
				<td nowrap="nowrap" class="leftMenuItem" onmouseover="invert_sub_item(this,true)" onmouseout="invert_sub_item(this,false)" onclick="showFunction('userGroupMaster')">
					<img src="../img/point.gif"/>利用者グループマスター</td>
			</tr>
			<tr>
				<td nowrap="nowrap" class="leftMenuItem" onmouseover="invert_sub_item(this,true)" onmouseout="invert_sub_item(this,false)" onclick="showFunction('deptMaster')">
					<img src="../img/point.gif"/>部門マスター</td>
			</tr>
			<tr>
				<td nowrap="nowrap" class="leftMenuItem" onmouseover="invert_sub_item(this,true)" onmouseout="invert_sub_item(this,false)" onclick="showFunction('userMaster')">
					<img src="../img/point.gif"/>ユーザー管理マスター</td>
			</tr>
			<tr>
				<td nowrap="nowrap" class="leftMenuItem" onmouseover="invert_sub_item(this,true)" onmouseout="invert_sub_item(this,false)" onclick="showFunction('accessLevelMaster')">
					<img src="../img/point.gif"/>アクセスレベルマスター</td>
			</tr>
			<tr>
				<td nowrap="nowrap" class="leftMenuItem" onmouseover="invert_sub_item(this,true)" onmouseout="invert_sub_item(this,false)" onclick="showFunction('userGroupAclRelation')">
					<img src="../img/point.gif"/>利用者グループアクセスレベル関連</td>
			</tr>
			<tr>
				<td nowrap="nowrap" class="leftMenuItem" onmouseover="invert_sub_item(this,true)" onmouseout="invert_sub_item(this,false)" onclick="showFunction('tableMaintenance')">
					<img src="../img/point.gif"/>テーブルメンテナンス</td>
			</tr>
		</table>
	</td></tr>
</table>
</form>
</body>
</html>


