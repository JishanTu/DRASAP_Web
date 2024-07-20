<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="tyk.drasap.system.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/struts-nested" prefix="nested" %>
<%-- ログイン情報の確認 --%>
<logic:notPresent name="user" scope="session">
	<logic:redirect forward="timeout" />
</logic:notPresent>
<html:html>
<head>
<title>DRASAP [マスターテーブルメンテナンス]</title>
<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Cache-Control" content="no-cache" />
<style type="text/css">@import url( <%=request.getContextPath() %>/default.css );</style>
<script type="text/javascript">
<!--
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
//--->
</script>
</head>
<body bgcolor="#F5F5DC" bottommargin="0" leftmargin="0" topmargin="0" rightmargin="0" marginheight="0" marginwidth="0">
<html:form action="/tableMaintenance">
<html:hidden property="act" />
<html:hidden property="updateIndex" />
<!--------------- ヘッダ -------------------------------->
<nested:root name="tableMaintenanceForm">
<table align="center" border="0" cellspacing="0" cellpadding="0" class="normal12">
	<tr>
		<td>テーブル名称</td>
		<td><nested:select property="selectTable" onchange="submitFunc('SEARCH')">
			<html:options labelName="tableMaintenanceForm" labelProperty="tableList"
					name="tableMaintenanceForm" property="tableList" />
		</nested:select></td>
		<td>&nbsp;&nbsp;&nbsp;where&nbsp;&nbsp;</td>
		<td align="center" nowrap="nowrap">
		<nested:text property="whereStr" /></td>
		<td align="center" nowrap="nowrap">
		<input type="button" value="検索" onclick="submitFunc('WHERESEARCH')" /></td>
		<td><span class="normal12">&nbsp;&nbsp;&nbsp;&nbsp;</span></td>
		<td><span class="normal12">&nbsp;&nbsp;&nbsp;&nbsp;</span></td>
		<td><span class="normal12">&nbsp;&nbsp;&nbsp;&nbsp;</span></td>
		<td><span class="normal12">&nbsp;&nbsp;&nbsp;&nbsp;</span></td>
		<td align="center" nowrap="nowrap">
		<input type="button" value="前ページ" onclick="submitFunc('PREVPAGE')" /></td>
		<td>&nbsp;&nbsp;&nbsp;</td>
		<td align="center" nowrap="nowrap">
		<input type="button" value="次ページ" onclick="submitFunc('NEXTPAGE')" /></td>
		<td>&nbsp;&nbsp;&nbsp;</td>
		<td align="center" nowrap="nowrap">
		<nested:select property="selectPage" onchange="submitFunc('DIRECTPAGE')" > >
				<html:options labelName="tableMaintenanceForm" labelProperty="pageNameList"
						name="tableMaintenanceForm" property="pageList" />
		</nested:select></td>
		<td><span class="normal12">／</span></td>
		<td><span class="normal12"><nested:write property="recCount" /></span></td>
	</tr>
</table>
<br />
<table align="center" border="1" cellspacing="0" cellpadding="0" class="normal10">
		<tr>
		<nested:iterate id="attrList" type="tyk.drasap.system.TableMaintenanceElement"
			name="tableMaintenanceForm" property="attrList" scope="session">
			<td nowrap="nowrap"  bgcolor="#A1A0C0">
			<nested:write property="column_name" /></td>
		</nested:iterate>
		<nested:notEmpty property="recList" scope="session">
		<td nowrap="nowrap" bgcolor="#A1A0C0">選択</td>
		</nested:notEmpty>
		</tr>
		<nested:iterate id="RecList" type="tyk.drasap.system.TableMaintenanceRec" indexId="idx"
			name="tableMaintenanceForm" property="recList" scope="session">
		<tr>
			<nested:iterate id="ValList" type="tyk.drasap.system.TableMaintenanceVal" indexId="col_Idx"
			 property="valList" scope="session">
				<td nowrap="nowrap">
				<%if (((TableMaintenanceForm)session.getAttribute("tableMaintenanceForm")).getAttrList(col_Idx).isKey()) { %>
					<%if (((TableMaintenanceForm)session.getAttribute("tableMaintenanceForm")).getRecList(idx).isNew()) { %>
						<nested:text style="<%=ValList.getDispStyle()%>" property="val" onchange='<%= "changeValue(" + idx+ ")" %>' />
					<% } else { %>
						<nested:write property="val" />
					<% } %>
				<% } else { %>
					<nested:text style="<%=ValList.getDispStyle()%>" property="val" onchange='<%= "changeValue(" + idx+ ")" %>' />
				<% } %>
				</td>
			</nested:iterate>
			<td nowrap="nowrap">&nbsp;<nested:checkbox property="check" />&nbsp;</td>
			<nested:hidden property="new" />
		</tr>
		</nested:iterate>
</table>
<table align="center" border="0" cellspacing="0" cellpadding="0">
			<tr><td>&nbsp;</td></tr>
			<tr>
			<td align="center" nowrap="nowrap">
			<input type="button" value="レコード追加" onclick="submitFunc('ADDRECORD')" /></td>
			<td>&nbsp;&nbsp;&nbsp;</td>
			<td align="center" nowrap="nowrap">
			<input type="button" value="更新" onclick="submitFunc('UPDATE')" /></td>
			<td>&nbsp;&nbsp;&nbsp;</td>
			<td align="center" nowrap="nowrap">
			<input type="button" value="削除" onclick="submitFunc('DELETE')" /></td>
			<td>&nbsp;&nbsp;&nbsp;</td>
			<td align="center" nowrap="nowrap">
			<input type="button" value="エクスポート" onclick="submitFunc('EXPORT')" /></td>
			<td>&nbsp;&nbsp;&nbsp;</td>
			</tr>
</table>
</nested:root>
</html:form>
<html:form action="/tableMaintenance" enctype="multipart/form-data">
<html:hidden property="act" value="inport" />
<!--
<table align="center" border="0" cellspacing="0" cellpadding="0">
			<td>
  			<html:file property="fileUp" size="64" />
  			<input type="button" value="インポート" onclick="submitFunc('INPORT')" />
			</td>
</table>
-->
<table align="center" border="0" cellspacing="0" cellpadding="0">
<tr><td><br/></td></tr><tr><td><br/></td></tr>
<nested:iterate id="errorMessage" type="java.lang.String"
				name="tableMaintenanceForm" property="errorMsg" scope="session" >
	<tr><td style="color:#FF0000"><bean:write name="errorMessage" /></td></tr>
</nested:iterate>
</table>
</html:form>
</body>
</html:html>


