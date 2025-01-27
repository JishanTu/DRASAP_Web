<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page isELIgnored="false"%>

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<title>Drawing Search and Print System [エラー]</title>
	<style type="text/css">@import url( <%=request.getContextPath() %>/resources/css/<%=session.getAttribute("default_css")%> );</style>
	<script type="text/javascript">
		function backSearchResult(){
			//alert(parent.location.href);
			location.href = "<%=request.getContextPath() %>/switch.do?page=/search/searchResult.jsp";
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
<body onload="onLoad();">
<!-- エラーの表示 -->
<font color="RED">

	<c:if test="${message != null}">
		<hr style="border: none; height: 1px; background-color: orange;">
		<c:forEach var="msg" items="${message}">
			<ul>
				<li>${msg}</li>
			</ul>
		</c:forEach>
		<hr style="border: none; height: 1px; background-color: orange;">
	</c:if>

</font>					
<div align="center">
	<input type="button" value="戻る" onclick="backSearchResult()"  style="font-size:12pt;" />
</div>
<table class="nowsearch" id="nowSearch" style="visibility:hidden">
	<tr valign="middle">
		<td align="center" style="font-size:18pt;color:#0000FF;">
			<c:choose>
				<c:when test="${sessionScope.user.language == 'Japanese' }">
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
