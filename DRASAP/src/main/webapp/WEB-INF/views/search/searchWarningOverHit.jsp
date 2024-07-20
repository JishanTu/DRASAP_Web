<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

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
	<style type="text/css">@import url( <%=request.getContextPath() %>/resources/css/<%=session.getAttribute("default_css")%> );</style>
	<script type="text/javascript">
	<!--
	// 隠し属性actにセットする
	function setAct(param) {
		if (param == 'continue') {<%-- 2013.09.05 yamagishi.
			if (parent.condition != null) {
				parent.condition.lockButtons();
			} --%>
			nowSearch();
		}
		document.forms[0].act.value=param;// 隠し属性actにセット
	}
	function onLoad() {
		if (parent.condition != null) {
			parent.condition.unLockButtons();
		}
	}
	function nowSearch() {
		var nowSearch;
		nowSearch = document.getElementById("nowSearch");
		nowSearch.style.visibility = "visible";
	}<%-- 2013.09.05 yamagishi add. start --%>
	function lockButtons() {
		if (parent.condition != null) {
			parent.condition.lockButtons();
		}
	}<%-- 2013.09.05 yamagishi add. end --%>
	//-->
	</script>
</head>
<body bgcolor="#FFFFFF" bottommargin="0" leftmargin="0" topmargin="0" rightmargin="0" marginheight="0" marginwidth="0" onload="onLoad();">
<form action="<%=request.getContextPath() %>/warningOverHit" target="result">
<input type = "hidden" name = "act" value = ""/>
<%-- 次の実際に検索するActionで使用するためのデータを渡す --%>
<%--
	// 何故かWebLogicでは
	// <%= 式 %>を含んだhtml:hiddenタグが input type="hidden"に変換されなかったので
	// 直接<input type="hidden">で記述することにした。 by Hirata 2004.Mar.15
<html:hidden property="sqlWhere" value="<%= (String)request.getAttribute("SQL_WHERE") %>" />
<html:hidden property="sqlOrder" value="<%= (String)request.getAttribute("SQL_ORDER") %>" />
--%>
<input type="hidden" name="sqlWhere" value="<%=(String)request.getAttribute("SQL_WHERE")%>" />
<input type="hidden" name="sqlOrder" value="<%=(String)request.getAttribute("SQL_ORDER")%>" />
<div style = "text-align:center;">
<br />
<c:choose>
    <c:when test="${user.language eq 'Japanese'}">
        <font color="#FF0000" style="font-size:12pt">警告</font>
        <hr width="50%" color="#FF6600" />
        <span class="normal12">
            指定した検索条件にヒットする図番は&nbsp;
            <c:if test="${not empty hit}">
                <c:out value="${hit}" />
            </c:if>
            件です。<br />
            この件数の表示には長時間かかります。<br />
            このまま表示を続けますか?<br />
            <br />
            <button type="button" onclick="setAct('cancel')">中止する</button>&nbsp;&nbsp;&nbsp;&nbsp;
            <button type="button" onclick="setAct('continue')">続ける</button>
        </span>
    </c:when>
    <c:otherwise>
        <font color="#FF0000" style="font-size:12pt">Warning</font>
        <hr width="50%" color="#FF6600" />
        <span class="normal12">
            There are &nbsp;
            <c:if test="${not empty hit}">
                <c:out value="${hit}" />
            </c:if>
            drawings that meet the search condition.<br />
            It will take a while to view all of them.<br />
            Do you want to continue ?<br />
            <br />
            <button type="button" onclick="setAct('cancel'); form.submit();">STOP</button>&nbsp;&nbsp;&nbsp;&nbsp;
            <button type="button" onclick="setAct('continue'); form.submit(); lockButtons();">CONTINUE</button>
        </span>
    </c:otherwise>
</c:choose>


</div>
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
</form>
</body>
</html>
