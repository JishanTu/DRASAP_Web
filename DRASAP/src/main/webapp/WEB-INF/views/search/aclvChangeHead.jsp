<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="tyk.drasap.search.*"%>
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
<html>
<head>
<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
<title>Drawing Search and Print System [アクセスレベル変更]</title>
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Cache-Control" content="no-cache" />
<style type="text/css">
@import
url(
<%=request.getContextPath()%>/resources/css/default.css
);
</style>
<script type="text/javascript">
	// 属性actをセットして、サブミット
	function setActSubmit(parm) {
		parent.aclv_change_body.document.forms[0].act.value = parm;// 隠し属性actにをセット
		parent.aclv_change_body.document.forms[0].target = "_top";// ターゲットは画面全体
		parent.aclv_change_body.document.forms[0].submit();
	}
	// ヘルプを表示する
	function help() {
		var targetName = '_help';//別の画面を開く
		var WO1;
		var w = screen.availWidth - 100;
		var h = screen.availHeight - 100;

		WO1 = window
				.open(
						"/DRASAP/search/aclvHelp.jsp",
						targetName,
						//"toolbar=no,resizable=yes,width=" + w + ",height=" + h);
						'toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes,width='
								+ w + ',height=' + h);
		WO1.window.moveTo(50, 50);//画面の位置指定
		WO1.focus();
	}
</script>
</head>
<body bgcolor="#CCCCCC" bottommargin="0" leftmargin="0" topmargin="0"
	rightmargin="0" marginheight="0" marginwidth="0">
	<!--===================== ヘッダ =====================-->
	<table border="0" cellspacing="0" cellpadding="0" width="100%">
		<tr>
			<td>
				<table border="0" bgcolor="#EEEEEE">
					<tr>
						<td nowrap="nowrap"><span class="normal18">アクセスレベル・使用禁止区分の変更</span></td>
					</tr>
				</table>
			</td>
			<td>
				<table border="0" bgcolor="#EEEEEE">
					<tr>
						<td nowrap="nowrap"><span class="normal10">変更後のアクセスレベル、使用禁止区分を入力して下さい。<br />
								変更したくない品番はチェックを外して下さい。「次へ...」で確認画面に遷移します。
						</span></td>
					</tr>
				</table>
			</td>
			<td align="right">&nbsp;&nbsp;&nbsp;&nbsp;<a
				href="javascript:help()"><span class="normal10blue">HELP</span></a></td>
		</tr>
	</table>
	<!--===================== ヘッダ =====================-->
	<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<!--===================== 表示内容の変更 =====================-->
			<td><table border="1">
					<tr>
						<td><span class="normal10">職番：<c:out
									value="${user.id}" /></span></td>
						<td><span class="normal10">氏名：<c:out
									value="${user.name}" /></span></td>
						<td><span class="normal10">部署名(店名)：<c:out
									value="${user.dept}" /></span></td>
					</tr>
				</table></td>
			<!--===================== 表示内容の変更 =====================-->
			<td><span class="normal10"> &nbsp;&nbsp;&nbsp;&nbsp; <c:set
						var="disabledString" value="" /> <c:if
						test="${empty aclvChangeForm.aclvChangeList}">
						<c:set var="disabledString" value="disabled='disabled'" />
					</c:if> <input type="button" value="全てチェック"
					onclick="setActSubmit('CHECK_ON')" ${disabledString} /> <input
					type="button" value="全て外す" onclick="setActSubmit('CHECK_OFF')"
					${disabledString} />
			</span></td>

		</tr>
	</table>
</body>
</html>
