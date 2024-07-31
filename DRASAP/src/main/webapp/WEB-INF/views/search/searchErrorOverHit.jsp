<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page isELIgnored="false"%>

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
<title>Drawing Search and Print System [図面検索]</title>
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Cache-Control" content="no-cache" />
<style type="text/css">
@import
url(
<%=request.getContextPath()
%>/resources/css/<%=session.getAttribute(
"default_css"
)%>
);
</style>
<script type="text/javascript">
	<!--
	// 隠し属性actにセットする
	function setAct(param){
		document.forms[0].act.value=param;// 隠し属性actにセット
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
	//-->
	</script>
</head>
<body style="background-color: #FFFFFF; margin: 0;" onload="onLoad();">
	<div style="text-align: center;">
		<br />
		<c:choose>
			<c:when test="${sessionScope.user.language == 'Japanese'}">
				<font color="#FF0000" style="font-size: 12pt">警告</font>
				<hr style="width: 50%; color: #FF6600;" />
				<span class="normal12"> 指定した検索条件にヒットする図番は&nbsp; <c:if
						test="${not empty requestScope.hit}">
						<c:out value="${requestScope.hit}" />
					</c:if> 件です。<br /> 1度の検索で利用可能な件数は <c:if
						test="${not empty sessionScope.drasapInfo}">
						<c:out value="${sessionScope.drasapInfo.searchLimitCount}" />
					</c:if> 件です。<br /> 検索条件を絞り込んでください。<br />
				</span>
			</c:when>
		</c:choose>

		<c:choose>
			<c:when test="${sessionScope.user.language ne 'Japanese'}">
				<font color="#FF0000" style="font-size: 12pt">Warning</font>
				<hr style="width: 50%; color: #FF6600;" />
				<span class="normal12"> The drawings that can meet the search
					condition are&nbsp; <c:if test="${not empty requestScope.hit}">
						<c:out value="${requestScope.hit}" />
					</c:if> .<br /> Avaiable number of drawings for each search are <c:if
						test="${not empty sessionScope.drasapInfo}">
						<c:out value="${sessionScope.drasapInfo.searchLimitCount}" />
					</c:if> .<br /> Please specific search condition.<br />
				</span>
			</c:when>
		</c:choose>

	</div>
	<table class="nowsearch" id="nowSearch" style="visibility: hidden">
		<tr valign="middle">
			<td align="center" style="font-size: 18pt; color: #0000FF;"><c:choose>
					<c:when test="${sessionScope.user.language eq 'Japanese'}">
        検索中・・・・
    </c:when>
					<c:otherwise>
        Now Searching...
    </c:otherwise>
				</c:choose></td>
		</tr>
	</table>
</body>
</html>
