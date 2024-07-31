<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page isELIgnored="false"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Cache-Control" content="no-cache" />
<title>Drawing Search and Print System [エラー]</title>
<style type="text/css">@import url( <%=request.getContextPath() %>/resources/css/<%=session.getAttribute("default_css")%> );</style>
<script type="text/javascript">
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
 <c:if test="${not empty requestScope['errors']}">
    <div style="color: red;">
        ${requestScope['errors'].message}
    </div>
</c:if>

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
