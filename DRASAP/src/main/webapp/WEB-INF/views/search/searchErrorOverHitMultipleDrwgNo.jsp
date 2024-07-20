<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page isELIgnored="false"%>

<%-- ログイン情報の確認 --%>
<c:if test="${sessionScope.user == null}">
	<script>
        location.replace('<%=request.getContextPath() %>/timeout');
    </script>
</c:if>
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
			<c:when test="${sessionScope.user.language eq 'Japanese'}">
				<font color="#FF0000" style="font-size: 12pt">警告</font>
				<hr style="width: 50%; color: #FF6600;" />
				<span class="normal12"> 1度の検索で指定可能な図番の件数は <c:if
						test="${not empty sessionScope.drasapInfo}">
						<c:out value="${sessionScope.drasapInfo.multipleDrwgNoMax}" />
					</c:if> 件です。<br /> 図番の件数を <c:if
						test="${not empty sessionScope.drasapInfo}">
						<c:out value="${sessionScope.drasapInfo.multipleDrwgNoMax}" />
					</c:if> 件以下にしてください。<br />
				</span>
			</c:when>
			<c:otherwise>
				<font color="#FF0000" style="font-size: 12pt">Warning</font>
				<hr style="width: 50%; color: #FF6600;" />
				<span class="normal12"> Avaiable number of drawings for each
					search are <c:if test="${not empty sessionScope.drasapInfo}">
						<c:out value="${sessionScope.drasapInfo.multipleDrwgNoMax}" />
					</c:if> .<br /> Please specific multiple drowing no under <c:if
						test="${not empty sessionScope.drasapInfo}">
						<c:out value="${sessionScope.drasapInfo.multipleDrwgNoMax}" />
					</c:if> .<br />
				</span>
			</c:otherwise>
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
