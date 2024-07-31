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
	<title>Drawing Search and Print System [運用支援ツールログイン]</title>
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<style type="text/css">@import url( <%=request.getContextPath()%>/resources/css/default.css );</style>
	<script type="text/javascript">
		var browserName = navigator.appName;
		function onLoad() {
                    var w = screen.availWidth;
                    var h = screen.availHeight;
                    mx = 0;
                    my = 0;
                    window.resizeTo(w, h);
                    window.moveTo(mx,my);//画面の位置指定
                    onInitFocus();
		}
		// 遷移する
		function cancel(){
			// キャンセル
			self.close();
		}
		// 初期フォーカス位置
		function onInitFocus(){
			document.management_LoginForm.passwd.focus();
		}
	</script>
</head>
<body onload="onInitFocus()">

  <font color="RED">
<ul>
    <c:if test="${message != null}">
        <c:forEach var="msg" items="${message}">
            <li>${msg}</li>
        </c:forEach>
    </c:if>
</ul>
</font>	

<form action="<%=request.getContextPath() %>/management_Login" method = "post">

<table align="center" border="0" cellspacing="0" cellpadding="5">
    <caption align="center"><b>運用支援ツールログイン</b></caption>
    <tr>
	<td align="center"colspan="2">運用支援ツール用のパスワードを入力してください。</td>
    </tr>
    <tr style="background-color:#EEEEEE;">
	<td align="right">passwd : </td><td><input type="password" name="passwd" value="" style="width:180px;" /></td>
    </tr>
    <tr>
    <td align="center" colspan="2"style="background-color:#FFFFFF;">
	<input type="submit" value="ログイン" style="margin-right: 20px;">
	<input type="button" value="キャンセル" onclick="cancel()" />
    </td>
    </tr>
</table>
</form>
</body>
</html>
