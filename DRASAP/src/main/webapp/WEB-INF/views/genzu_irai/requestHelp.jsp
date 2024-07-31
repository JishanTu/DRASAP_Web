<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page isELIgnored="false"%>

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
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<title>Drawing Search and Print System [Help 図面登録依頼]</title>
</head>
<body bgcolor="#FFFFCC"><font style="font-family: 'ＭＳ Ｐゴシック','ＭＳ ゴシック';">
図面登録依頼の使い方<br />
<ul>
	<li>Ｗｅｂ図面検索(DRASAP)に登録されていない図面を原図庫に登録してもらう依頼をする場合に使用します。</li>
	<li>図番（範囲指定が可能）は必須入力項目となります。</li>
	<li>進捗については『図面登録依頼詳細』画面を確認ください。</li>
</ul>
<br />
　　※番号の範囲指定について<br />
　　範囲指定するには１１桁の図番の先頭９桁が同じである必要があります。<br />
　　（ハイフンは含みません）<br />
　　１２ケタの図番について範囲指定をする事はできません。<br />
<br />
</font></body>
</html>
