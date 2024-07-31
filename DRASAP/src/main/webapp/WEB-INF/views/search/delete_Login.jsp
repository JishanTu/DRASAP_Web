<%@ page contentType="text/html;charset=UTF-8"%>
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
	<title>Drawing Search and Print System [削除ツールログイン]</title>
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<style type="text/css">@import url( <%=request.getContextPath()%>/resources/css/default.css );</style>
	<script type="text/javascript">
		var browserName = navigator.appName;
		function cancel(){
			// キャンセル
			// 2022.04.13 Windows Edge対応. 削除ツールログインの「キャンセル」で戻れない不具合修正.
            //location.href = "switch.do?prefix=/search&amp;page=/searchResult.jsp";
            parent.condition.document.forms[0].act.value="search";// 隠し属性actに'search'をセット
            parent.condition.document.forms[0].target="result";// targetは'result'
            parent.condition.document.forms[0].submit();
		}
		// 初期フォーカス位置
		function onInitFocus(){
			document.delete_LoginForm.passwd.focus();
		}
	</script>
</head>
<body onload="onInitFocus()">
<c:if test="${message != null}">
<hr/ style="border: none; height: 0.5px; background-color: red;">
	<c:forEach var="msg" items="${message}">
		<li style="margin-left: 30px; line-height: 1.5; color: red; border-lift: 0px'">${msg}</li>
</c:forEach>
	<hr/ style="border: none; height: 0.5px; background-color: red;">
</c:if>

    <form name="delete_LoginForm" action="<%=request.getContextPath() %>/delete_Login" method="post">
        <table align="center" border="0" cellspacing="0" cellpadding="5">
            <caption align="center"><b>削除ツールログイン</b></caption>
            <tr>
                <td align="center" colspan="2">図番削除用のパスワードを入力してください。</td>
            </tr>
            <tr style="background-color:#EEEEEE;">
                <td align="right">passwd : </td>
                <td><input type="password" id="passwd" name="passwd" value="" style="width:180px;" /></td>
            </tr>
            <tr>
                <td align="center" colspan="2" style="background-color:#FFFFFF;">
                    <input type="submit" style="margin-right:20px;" value="ログイン" />
                    <input type="button" value="キャンセル" onclick="cancel()" />
                </td>
            </tr>
        </table>
    </form>
</body>

</html>
