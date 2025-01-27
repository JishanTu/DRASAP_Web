<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="tyk.drasap.search.*,tyk.drasap.common.*" %>
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
	<meta content="text/html; charset=UTF-8" http-equiv="Content-type" />
	<meta content="no-cache" http-equiv="Pragma" />
	<meta content="no-cache" http-equiv="Cache-Control" />
	<style type="text/css">@import url( <%=request.getContextPath()%>/resources/css/default.css );</style>
	<style type="text/css">
		.deleteBtn {
			width:100%;
			position:absolute;
			top:10px;
			text-align:center;
		}
		.goBackBtn {
			width:100%;
			position:absolute;
			top:10px;
			text-align:right;
			padding-right:30px;
		}
	</style>
	<script type="text/javascript">
		browserName = navigator.appName;
		function onLoad() {
			parent.parent.condition.document.body.style.cursor="";
		}
		// 遷移する
		function submitFunc(parm){
			if (parm == "DELETE") {
				document.body.style.cursor="wait";
				parent.delete_head.document.body.style.cursor="wait";
				parent.delete_body.document.body.style.cursor="wait";
				parent.parent.condition.document.body.style.cursor="wait";
				var deleteButton = document.getElementById("deleteButton");
				var backButton = document.getElementById("backButton");
				deleteButton.disabled=true;
				backButton.disabled=true;
				parent.delete_body.document.forms[0].act.value='delete';// 隠し属性actをセット
				parent.delete_body.document.forms[0].target="_parent";
				parent.delete_body.document.forms[0].submit();
			}
		}
		function backSearchResult(){
		parent.parent.condition.document.forms[0].act.value="search";// 隠し属性actに'search'をセット
		parent.parent.condition.document.forms[0].target="result";// targetは'result'
		parent.parent.condition.document.forms[0].submit();
		}
	</script>
</head>
<body marginwidth="0" marginheight="0" bgcolor="#ffffff" style="margin: 0; padding: 0;" onload="onLoad()">
	<c:set var="deleteDwgForm" scope="session" value="${sessionScope.deleteDwgForm}" />
	<span class="deleteBtn">
		<input type="button" style="width:80px;" onclick="submitFunc('DELETE')" value="削除" id="deleteButton" ${empty deleteDwgForm or not deleteDwgForm.deleteOK ? 'disabled' : ''} />
	</span>
	<span class="goBackBtn">
		<input type="button" onclick="backSearchResult()" value="　戻る　" id="backButton" />
	</span>
</body>

</html>

