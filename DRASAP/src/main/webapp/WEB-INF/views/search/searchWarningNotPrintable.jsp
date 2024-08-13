<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

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
	<title>Drawing Search and Print System [図面検索]</title>
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<style type="text/css">@import url( <%=request.getContextPath() %>/resources/css/<%=session.getAttribute("default_css")%> );</style>
	<script type="text/javascript">
		// 隠し属性actにセットする
		function setAct(param){
			document.forms[0].act.value=param;// 隠し属性actにセット
			document.forms[0].submit();
		}
		function onLoad() {
			if (parent.condition != null) {
				parent.condition.unLockButtons();
			}
		}
		function nowSearch(){
			var nowSearch;
			nowSearch = document.getElementById("nowSearch");
			nowSearch.style.visibility = "visible";
		}
	</script>
</head>
<body style="background-color: #FFFFFF; margin: 0;" onload="onLoad();">
<form action="<%=request.getContextPath() %>/searchWarningNotPrintable"  method = "post">
<input type = "hidden" name = "act" value = ""/>
<div style = "text-align:center;">
<br />
<c:choose>
    <c:when test="${user.language eq 'Japanese'}">
        <font color="#FF0000" style="font-size:12pt">警告</font>
        <hr style="width: 50%; color: #FF6600;" />
        <span class="normal10">
            指定した図番の中に、出力できない図番(Tiffでない、印刷権を持たない)が含まれます。<br />
            出力可能な図番のみ出力しますか?<br />
            図番の指定をやり直す場合は「戻る」をクリックして下さい。<br />
            <br />
            <button type="button" onclick="setAct('continue')">出力する</button>
            &nbsp;&nbsp;&nbsp;&nbsp;
            <button type="button" onclick="setAct('backResult')">戻る</button>
        </span>
    </c:when>
    <c:otherwise>
        <font color="#FF0000" style="font-size:12pt">Warning</font>
        <hr style="width: 50%; color: #FF6600;" />
        <span class="normal10">
            Among the requested drawings.<br />
            Unavailable drawings(non-TIFF or no printing right)are included.<br />
            Do you want available drawing only ?<br />
            Please click "return" if you want to start again.<br />
            <br />
            <button type="button" onclick="setAct('continue')">OUTPUT</button>
            &nbsp;&nbsp;&nbsp;&nbsp;
            <button type="button" onclick="setAct('backResult')">RETURN</button>
        </span>
    </c:otherwise>
</c:choose>


</div>
</form>
<table class="nowsearch" id="nowSearch" style="visibility:hidden">
<tr valign="middle">
<td align="center" style="font-size:18pt;color:#0000FF;">
<c:choose>
    <c:when test="${user.language eq 'Japanese'}">
        検索中・・・・
    </c:when>
    <c:otherwise>
        Now Searching...
    </c:otherwise>
</c:choose>
</td>
</tr>
</table>
</body>
</html>
