<%@ page contentType="text/html;charset=UTF-8" %>

<%-- ログイン情報の確認 --%>
<c:if test="${empty sessionScope.user}">
<script>
		location.replace('<%=request.getContextPath()%>/timeout');
</script>
</c:if>

<%--<logic:notPresent name="parentPage" scope="session">--%>
<%--    <logic:redirect forward="timeout" />--%>
<%--</logic:notPresent>--%>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
    <meta http-equiv="Pragma" content="no-cache" />
    <meta http-equiv="Cache-Control" content="no-cache" />
    <title>Drawing Search and Print System [パスワード変更]</title>
    <script type="text/javascript">
    // <!--
    // 直リンク禁止
    var refinfo=document.referrer;
    if (!refinfo){
        location.replace('<%=request.getContextPath()%>/timeout');
    }

    function onLoad() {
        alert("パスワード変更が成功しました\nPassword change succeeded.");
        <%-- ログイン画面から遷移した場合--%>
        if (${sessionScope.parentPage == 'Login'}) {
            <%-- 図面検索画面へ遷移 --%>
            <%-- 2022.04.13 Windows Edge対応. ログイン画面からのパスワード変更時の画面遷移不具合対応. --%>
            location.replace('<%=request.getContextPath()%>/login');
        } else {
            <%-- 図面検索画面から呼び出した場合 --%>
            // ウィンドウを閉じる
            self.close();
        }
    }
    //-->
    </script>
</head>
<body onload="onLoad()" />
</html>