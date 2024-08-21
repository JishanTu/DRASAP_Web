<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%-- ログイン情報の確認 --%>
<logic:notPresent name="user" scope="session">
	<logic:redirect forward="timeout" />
</logic:notPresent>
<html:html>
<head>
	<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
	<title>図面登録依頼詳細</title>
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<style type="text/css">@import url( <%=request.getContextPath() %>/default.css );</style>
	<script type="text/javascript">
	<!--
		function act(param1,param2){
			document.forms[0].action.value=param1;//アクション
			document.forms[0].job.value=param2;//依頼ID,依頼内容,行番号のデータを取得する
			//alert("アクション = " + document.forms[0].action.value + ", job_id = " + document.forms[0].job_id.value);
			var targetName = '_messege';//別の画面を開く

			document.forms[0].target = targetName;
			var WO1;
			var w = screen.availWidth - 300;
			var h = screen.availHeight - 300;

			WO1=window.open("", targetName,
						"toolbar=no,resizable=yes,width=" + w + ",height=" + h);
						//'toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes,width=' + w + ',height=' + h);
			WO1.window.moveTo(150,0);//画面の位置指定
			WO1.focus();

			document.forms[0].submit();
		}
	//-->
	</script>
</head>
<body bgcolor="#FFFFFF" bottommargin="0" leftmargin="0" topmargin="0" rightmargin="0" marginheight="0" marginwidth="0">
<html:form action="/req_ref">
<input type="hidden" name="action"/>
<input type="hidden" name="job"/>
<font color="red" size="4" >
	<logic:iterate id="errs" name="request_refForm" property="listErrors">
		<li><bean:write name="errs" /></li>
	</logic:iterate>
</font>
<html:errors />
<table border="0" align="center">
	<tr bgcolor="#CCCCCC">
		<td nowrap="nowrap" align="center"><span class="normal10">依頼ID</span></td>
		<td nowrap="nowrap" align="center"><span class="normal10">状態</span></td>
		<td nowrap="nowrap" align="center"><span class="normal10">依頼内容</span></td>
<%-- // 2019.10.23 yamamoto modified. start
		<td nowrap="nowrap" align="center"><span class="normal10">号口・号機</span></td>
		<td nowrap="nowrap" align="center"><span class="normal10">原図内容</span></td>
--%>
		<td nowrap="nowrap" colspan="3" align="center"><span class="normal10">図番</span></td>
<%--
		<td nowrap="nowrap" align="center"><span class="normal10">部数</span></td>
		<td nowrap="nowrap" align="center"><span class="normal10">縮小</span></td>
		<td nowrap="nowrap" align="center"><span class="normal10">サイズ</span></td>
		<td nowrap="nowrap" align="center"><span class="normal10">出力先</span></td>
// 2019.10.23 yamamoto modified. end --%>
		<td nowrap="nowrap" align="center" bgcolor="#FFFFFF"></td>
	</tr>
<logic:iterate id="items" name="request_refForm" property="iraiList">
<%
	tyk.drasap.genzu_irai.Request_RefElement e = (tyk.drasap.genzu_irai.Request_RefElement)items;
	String stat = "";
	String job_id =e.getJob_id();//依頼ID
	String job_stat =e.getJob_stat();//状態
	stat = job_stat;
	if("0".equals(job_stat)){
		job_stat = "依頼中";
	}else{
		job_stat = "完了";
	}
	String job_name =e.getJob_name();//依頼内容
	String gouki =e.getGouki();//号口・号機
	if(gouki == null){
		gouki = "";
	}
	String genzu =e.getGenzu();//原図内容
	if(genzu == null){
		genzu = "";
	}
	String kaisi =e.getStart();//開始番号
	if(kaisi == null){
		kaisi = "";
	}
	String end =e.getEnd();//終了番号
	if(end == null){
		end = "";
	}
	String busuu =e.getBusuu();//部数
	if(busuu == null){
		busuu = "";
	}
	String syuku =e.getSyuku();//縮小
	if(syuku == null){
		syuku = "";
	}
	String sizu =e.getSize();//サイズ
	if(sizu == null){
		sizu = "";
	}
	String printer =e.getPrinter();//出力先
	if(printer == null){
		printer = "";
	}
	String messege =e.getMessege();//メッセージ(図面登録依頼、図面出力指示で使用)
	if(messege == null){
		messege = "";
	}
	String exist =e.getExist();//登録有無(図面登録依頼、図面出力指示で使用)
	String tenkai_deta = "";
	String sagyo_deta = "";

	if("図面登録依頼".equals(job_name) || "図面出力指示".equals(job_name)){
		if(!"".equals(messege) || "0".equals(exist)){
			tenkai_deta = "1";
		}
	}
	String messege1 =e.getMessege1();//メッセージ(原図借用依頼、図面以外焼付で使用)
	if(messege1 == null){
		messege1 = "";
	}
	if("原図借用依頼".equals(job_name) || "図面以外焼付依頼".equals(job_name)){
		if(!"".equals(messege1) || "2".equals(stat)){
			sagyo_deta = "1";
		}
	}
	String seq = e.getSeq();//シーケンス番号
	String rowNo = e.getRowNo();//行番号

	String job_list = job_id + "_" + job_name + "_" + rowNo;//メッセージありのリンクのデータ(図面登録依頼、図面出力指示で使用)
	String str_Messege = "act('button_Mtenkai', '" + job_list + "')";//図面登録依頼、図面出力指示で使用
	String str_Messege1 = "act('button_Msagyo', '" + job_list + "')";//原図借用依頼、図面以外焼付依頼で使用

%>

<% 	if("依頼中".equals(job_stat)){ %>
	<tr>
<%	}else{ %>
	<tr bgcolor="#CCCCFF">
<%	} %>
		<td nowrap="nowrap" align="center"><span class="normal10"><%= job_id %></span></td>
		<td nowrap="nowrap" align="center"><span class="normal10"><%= job_stat %></span></td>
		<td nowrap="nowrap" align="center"><span class="normal10">&nbsp;<%= job_name %>&nbsp;</span></td>
<%-- // 2019.10.23 yamamoto modified. start
		<td nowrap="nowrap" align="center"><span class="normal10">&nbsp;<%= gouki %>&nbsp;</span></td>
		<td nowrap="nowrap" align="center"><span class="normal10">&nbsp;<%= genzu %>&nbsp;</span></td>
--%>
		<td nowrap="nowrap" align="center"><span class="normal10">&nbsp;<%= kaisi %>&nbsp;</span></td>
		<td nowrap="nowrap" align="center"><span class="normal10">～</span></td>
		<td nowrap="nowrap" align="center"><span class="normal10">&nbsp;<%= end %>&nbsp;</span></td>
<%--
		<td nowrap="nowrap" align="right"><span class="normal10">&nbsp;<%= busuu %>&nbsp;</span></td>
		<td nowrap="nowrap" align="center"><span class="normal10">&nbsp;<%= syuku %>&nbsp;</span></td>
		<td nowrap="nowrap" align="center"><span class="normal10">&nbsp;<%= sizu %>&nbsp;</span></td>
		<td nowrap="nowrap" align="center"><span class="normal10">&nbsp;<%= printer %>&nbsp;</span></td>
// 2019.10.23 yamamoto modified. end --%>
<%	if("1".equals(tenkai_deta)){ %>
		<td nowrap="nowrap" align="center" bgcolor="#FF3300"><a href="javascript:<%=str_Messege%>"><span class="normal10white">ﾒｯｾｰｼﾞ</span></a></td>
	</tr>
<%	}else if("1".equals(sagyo_deta)){ %>
		<td nowrap="nowrap" align="center" bgcolor="#FF3300"><a href="javascript:<%=str_Messege1%>"><span class="normal10white">ﾒｯｾｰｼﾞ</span></a></td>
	</tr>
<%	} %>
</logic:iterate>
</table>
</html:form>
</body>
</html:html>
