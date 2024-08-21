<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<html>
<head>
	<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
	<title>図面登録依頼リスト</title>
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<style type="text/css">@import url( <%=request.getContextPath() %>/default.css );</style>
</head>
<body bgcolor="#FFFFFF" bottommargin="0" leftmargin="0" topmargin="0" rightmargin="0" marginheight="0" marginwidth="0">
<font color="red" size="4" >
	<logic:iterate id="errs" name="request_listForm" property="listErrors">
		<li><bean:write name="errs"/></li>
	</logic:iterate>
</font>
<html:form action="/req_list" >
<html:hidden property="action" />
<table border="0" align="center">
	<tr bgcolor="#CCCCCC">
		<td nowrap="nowrap" align="center"><span class="normal10">チェック</span></td>
		<td nowrap="nowrap" align="center"><span class="normal10">依頼ID</span></td>
		<td nowrap="nowrap" align="center"><span class="normal10">時間</span></td>
		<td nowrap="nowrap" align="center"><span class="normal10">依頼内容</span></td>
		<td nowrap="nowrap" align="center"><span class="normal10">図番</span></td>
<%-- // 2019.10.23 yamamoto modified. start
		<td nowrap="nowrap" align="center"><span class="normal10">号口・号機</span></td>
		<td nowrap="nowrap" align="center"><span class="normal10">原図内容</span></td>
		<td nowrap="nowrap" align="center"><span class="normal10">部数</span></td>
		<td nowrap="nowrap" align="center"><span class="normal10">縮小</span></td>
		<td nowrap="nowrap" align="center"><span class="normal10">サイズ</span></td>
// 2019.10.23 yamamoto modified. end --%>
		<td nowrap="nowrap" align="center"><span class="normal10">メッセージ</span></td>
		<td nowrap="nowrap" align="center"><span class="normal10">依頼者</span></td>
		<td nowrap="nowrap" align="left"><span class="normal10">部署名</span></td>
	</tr>
	<logic:iterate id="item" name="request_listForm" property="iraiList">
<%
		tyk.drasap.genzu_irai.Request_listElement e = (tyk.drasap.genzu_irai.Request_listElement)item;
		String job_id = e.getJob_id();//ジョブId
		String zikan = e.getZikan();//依頼日時
		String irai = e.getIrai();//依頼内容
		String zuban = e.getZuban();//図番
		if(zuban == null){
			zuban = "";
		}
		String gouki = e.getGouki();//号口・号機
		if(gouki == null){
			gouki = "";
		}
		String genzu = e.getGenzu();//原図内容
		if(genzu == null){
			genzu = "";
		}
		String busuu = e.getBusuu();//部数
		if(busuu == null){
			busuu = "";
		}
		String syukusyou = e.getSyukusyou();//縮小
		if(syukusyou == null){
			syukusyou = "";
		}
		String size1 = e.getSize();//サイズ
		if(size1 == null){
			size1 = "";
		}
		String iraisya = e.getUser_name();//依頼者
		String busyo = e.getBusyo_name();//部署名

%>


	<tr>
		<td>
			<html:select name="item" property="touroku" indexed="true">
				<html:options labelName="request_listForm" labelProperty="checkNameList" name="request_listForm" property="checkKeyList" />
			</html:select>
		</td>
		<td nowrap="nowrap" align="center"><span class="normal10">&nbsp;<%= job_id %>&nbsp;</span></td>
		<td nowrap="nowrap" align="center"><span class="normal10">&nbsp;<%= zikan %>&nbsp;</span></td>
		<td nowrap="nowrap" align="center"><span class="normal10">&nbsp;<%= irai %>&nbsp;</span></td>
		<td nowrap="nowrap" align="center"><span class="normal10">&nbsp;<%= zuban %>&nbsp;</span></td>
<%-- // 2019.10.23 yamamoto modified. start
		<td nowrap="nowrap" align="center"><span class="normal10">&nbsp;<%= gouki %>&nbsp;</span></td>
		<td nowrap="nowrap" align="center"><span class="normal10">&nbsp;<%= genzu %>&nbsp;</span></td>
		<td nowrap="nowrap" align="right"><span class="normal10">&nbsp;<%= busuu %>&nbsp;</span></td>
		<td nowrap="nowrap" align="center"><span class="normal10">&nbsp;<%= syukusyou %>&nbsp;</span></td>
		<td nowrap="nowrap" align="center"><span class="normal10">&nbsp;<%= size1 %>&nbsp;</span></td>
// 2019.10.23 yamamoto modified. end --%>
		<td nowrap="nowrap" align="center">
			<html:select name="item" property="message" indexed="true">
				<html:options labelName="request_listForm" labelProperty="messageNameList" name="request_listForm" property="messageNameList" />
			</html:select>
		</td>
		<td nowrap="nowrap" align="center"><span class="normal10">&nbsp;<%= iraisya %>&nbsp;</span></td>
		<td nowrap="nowrap" align="left"><span class="normal10">&nbsp;<%= busyo %>&nbsp;</span></td>
	</tr>
	<html:hidden name="item" property="job_id" indexed="true"/>
	<html:hidden name="item" property="zikan" indexed="true"/>
	<html:hidden name="item" property="zuban" indexed="true"/>
	<html:hidden name="item" property="gouki" indexed="true"/>
	<html:hidden name="item" property="genzu" indexed="true"/>
	<html:hidden name="item" property="busuu" indexed="true"/>
	<html:hidden name="item" property="syukusyou" indexed="true"/>
	<html:hidden name="item" property="size" indexed="true"/>
	<html:hidden name="item" property="busyo" indexed="true"/>
	<html:hidden name="item" property="rowNo" indexed="true"/>
	<html:hidden name="item" property="hiddenMessage" indexed="true"/>
	<html:hidden name="item" property="hiddenTouroku" indexed="true"/>
</logic:iterate>
</table>
</html:form>
</body>
</html>
