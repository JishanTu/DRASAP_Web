<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

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
	<title>図面登録依頼リスト</title>
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<style type="text/css">
	
	@import url( <%=request.getContextPath()%>/resources/css/default.css );
	
	td {
	white-space: nowrap;
     }
     
	</style>
	<script type="text/javascript">

		function doIraiKousin(){
			document.forms[0].target = 'list_body';//別の画面を開いたものを戻す
			document.forms[0].action.value="button_iraiKousin";//依頼更新ボタンを押した時のアクション
			document.forms[0].submit();
			//alert("アクション = " + document.forms[0].action.value);
		}
		// 印刷ボタン、作業依頼履歴ボタンで呼び出される。
		// 引数 targetName 準備するWindow名となり、処理を切り分ける。
		function goNext(targetName){
			//alert("アクション = " + targetName);
			if(targetName == '_printer'){
				//印刷画面ボタンを押した時のアクション
				document.forms[0].action.value="button_print";
				//alert("アクション = " + document.forms[0].action.value);

			} else if(targetName == '_irai_history'){
				//作業依頼履歴ボタンを押した時のアクション
				document.forms[0].action.value="button_history";
			}
			document.forms[0].target = targetName;
			var WO1;
			var w = screen.availWidth - 10;
			var h = screen.availHeight - 50;

			WO1=window.open("", targetName,
						'toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes,width=' + w + ',height=' + h);
			WO1.window.moveTo(0,0);
			WO1.focus();

			document.forms[0].submit();
		}

	</script>
</head>
<body bgcolor="#CCCCCC" bottommargin="0" leftmargin="0" topmargin="0" rightmargin="0" marginheight="0" marginwidth="0">
<!--=============== ヘッダ ===============-->
<form action="<%=request.getContextPath() %>/req_list" method="post">
<input type="hidden" name="action" value="${action}" />
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td>
			<table border="0" bgcolor="#EEEEEE">
				<tr><td><span class="normal18">図面登録依頼リスト</span></td></tr>
			</table></td>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td>
			<table border="0" bgcolor="#EEEEEE" align="left">
				<tr>
					<td><span class="normal10">処理が済んだらチェックを入力して下さい。<br />
					その後に更新するために「更新」をクリックして下さい。</span></td>
				</tr>
			</table>
		</td>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td width="100%" valign="bottom">
			<input type="submit" value="依頼更新" onclick="doIraiKousin()">
            <input type="submit" value="印刷画面" onclick="goNext('_printer')">
            <input type="submit" value="作業依頼履歴" onclick="goNext('_irai_history')">
		</td>
	</tr>
</table>
</form>
</body>
</html>
