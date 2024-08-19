<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

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
    <meta http-equiv="Pragma" content="no-cache" />
    <meta http-equiv="Cache-Control" content="no-cache" />
    <title>Drawing Search and Print System [パスワード変更]</title>
    <script type="text/javascript">

    // 直リンク禁止
    var refinfo=document.referrer;
    if (!refinfo){
        location.replace('<c:url value="/root/timeout.jsp" />');
    }

    function onLoad() {
        alert("パスワード変更が成功しました\nPassword change succeeded.");

        <c:choose>
            <c:when test="${sessionScope.parentPage == 'Login'}">
                // 図面検索画面へ遷移
                // 2022.04.13 Windows Edge対応. ログイン画面からのパスワード変更時の画面遷移不具合対応.
                location.replace('<c:url value="switch.do?page=/search/searchMain.jsp" />');
            </c:when>
            <c:otherwise>
                // 図面検索画面から呼び出した場合
                self.close();
            </c:otherwise>
        </c:choose>
    }

    </script>
</head>
<body onload="onLoad()">
</body>
</html>
