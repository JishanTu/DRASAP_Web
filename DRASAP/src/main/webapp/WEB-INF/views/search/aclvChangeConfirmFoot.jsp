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
<html>
<head>
	<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
	<title>Drawing Search and Print System [アクセスレベル変更]</title>
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<style type="text/css">@import url( <%=request.getContextPath()%>/resources/css/default.css );</style>
	<script type="text/javascript">
	<!--
		// 属性actをセットする
		function setAct(parm){
			document.forms[0].act.value=parm;// 隠し属性actにをセット
		}
	//-->
	</script>
</head>
<body bgcolor="#CCCCCC" bottommargin="0" leftmargin="0" topmargin="0" rightmargin="0" marginheight="0" marginwidth="0">
<%-- あらかじめターゲットは _top にしておく --%>
<form action="<%=request.getContextPath() %>/aclvChange" target="_top" method="post">
<input type="hidden" name="act" value="" />
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td>&nbsp;&nbsp;</td>
		<td align="center"><input type="submit" value="　変更　" onclick="setAct('CONFIRMED')" /></td>
		<td align="right"><input type="submit" value="　戻る　" onclick="setAct('BACK_INPUT')" /></td>
	</tr>
</table>
</form>
</body>
</html>
