<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="tyk.drasap.search.*,tyk.drasap.common.*"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

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
    // <!--
    document.onkeydown = keys;
    function keys() {
        switch (event.keyCode) {
            case 116: // F5
                event.keyCode = 0;
                return false;
                break;
        }
    }
    <%-- 2013.09.06 yamagishi add. start --%>
    var userAgent;
    var appVersion;<%-- end --%>
    function onLoad() {
        if (parent.parent.condition.unLockButtons != null) {
            parent.parent.condition.unLockButtons();
        }
        <%-- 2013.07.16 yamagishi add. start --%>
        if (navigator.appName == "Netscape" && !(navigator.platform.indexOf("Mac") != -1)) {
            document.captureEvents(Event.MOUSEDOWN);
        }
        // 右クリック禁止
        document.onmousedown = disableContextMenu;
        document.oncontextmenu = disableOnContextMenu;
        <%-- 2013.07.16 yamagishi add. end --%>
        userAgent = window.navigator.userAgent.toLowerCase();<%-- 2013.09.06 yamagishi add. --%>
        appVersion = window.navigator.appVersion.toLowerCase();
    }
    function nowSearch(){
        var nowSearch;
        nowSearch = document.getElementById("nowSearch");
        nowSearch.style.visibility = "visible";
    }
    <%-- 2019.10.28 yamamoto add. start --%>
    // 画面ロック
    function screenLock(){
        var screen;
        screen = document.getElementById("screenLock");
        screen.style.visibility = "visible";
    }
    // 画面ロック解除
    function screenUnLock(){
        var screen;
        screen = document.getElementById("screenLock");
        screen.style.visibility = "hidden";
    }
    <%-- 2019.10.28 yamamoto add. end --%>
    <%-- 2013.07.16 yamagishi add. start --%>
    function disableOnContextMenu() {
        return false;
    }
    function disableContextMenu(ev) {
        if (ev) {
            if (ev.button && ev.button == 2) { // W3C DOM2
                return false;
            } else if (!ev.button && ev.which == 3) { // N4
                return false;
            } else if (navigator.platform.indexOf("Mac") != -1 && navigator.appName == "Netscape") {
                return false;
            }
        } else {
            if (event && event.button && event.button == 2) { // IE
                return false;
            }
        }
    }
    <%-- 2013.07.16 yamagishi add. end --%>
    <%-- 2013.09.06 yamagishi add. start --%>
    var dialogFlag = false; // 二重起動防止
    function openDLManagerDialog(idx) {
<%      // DLマネージャが利用可能な場合
        User me = (User) session.getAttribute("user");
        if (me.isDLManagerAvailable()) { %>
        if (!dialogFlag) {
            dialogFlag = true;
            var drwgNoLink = document.getElementById("drwgNoLink[" + idx + "]");
            drwgNoLink.href = void(0);
            var targetUrl = '<%= request.getContextPath() %>/switch.do?page=/search/DLManagerDialog.jsp';
            targetUrl = targetUrl + '&searchIndex=' + idx;
            var w = 300;
            var h = 100;
            if (userAgent.indexOf('msie') != -1 && appVersion.indexOf("msie 6.") != -1) {
                // IE6用設定
                w = 305;
                h = 130;
            }
            window.showModalDialog(targetUrl, null, 'center=yes;status=no;scroll=no;resizable=no;dialogWidth=' + w + 'px;dialogHeight=' + h + 'px;'); // ※IEのみ
            dialogFlag = false;
        }
        // hrefをキャンセル
        return false;
<%      } %>
    }
    <%-- 2013.09.06 yamagishi add. end --%>
    //-->
    </script>
</head>
<body bgcolor="#FFFFFF" style="margin: 0;" onload="onLoad();">
	<form action="<%=request.getContextPath() %>/result" method="post" >
		<input type="hidden" name="act" /> 
		<input type="hidden"name="dispAttr1" value="${sessionScope.searchResultForm.dispAttr1}"/>
		<%-- この隠し属性がないと、次のXX件をクリックすると、表示属性が消えてしまう --%>
		<input type="hidden" name="dispAttr2" value="${sessionScope.searchResultForm.dispAttr2}"/> 
		<input type="hidden" name="dispAttr3" value="${sessionScope.searchResultForm.dispAttr3}"/> 
		<input type="hidden" name="dispAttr4" value="${sessionScope.searchResultForm.dispAttr4}"/> 
		<input type="hidden" name="dispAttr5" value="${sessionScope.searchResultForm.dispAttr5}"/>
	    <input type="hidden" name="dispAttr6" value="${sessionScope.searchResultForm.dispAttr6}"/> 
	    <input type="hidden" name="outputPrinter" /> 
	    <input type="hidden" name="outCsvAll" />
		<%-- ファイル出力で全属性か --%>

		<c:set var="resultList"
			value="${sessionScope.searchResultForm.searchResultList}" />
		<c:choose>
			<c:when test="${empty resultList}">
				<ul style="color: red; font-size: 12pt;">
					<c:choose>
						<c:when test="${sessionScope.user.language == 'Japanese'}">
							<li>検索結果は0件です。</li>
						</c:when>
						<c:otherwise>
							<li>No items Found.</li>
						</c:otherwise>
					</c:choose>
				</ul>
			</c:when>
			<c:otherwise>
				<!-- その他処理 -->
			</c:otherwise>
		</c:choose>

		<table border="0" cellspacing="1" cellpadding="0">
			<%-- userを定義する --%>
			<c:set var="user" value="${sessionScope.user}" />
			<%-- iterateに必要なoffsetとlengthを定義する --%>
			<c:set var="iterateLength"
				value="${sessionScope.searchResultForm.dispNumberPerPage}" />
			<c:set var="iterateOffest"
				value="${sessionScope.searchResultForm.dispNumberOffest}" />
			<c:set var="dispAttr1Key"
				value="${sessionScope.searchResultForm.dispAttr1}" />
			<c:set var="dispAttr2Key"
				value="${sessionScope.searchResultForm.dispAttr2}" />
			<c:set var="dispAttr3Key"
				value="${sessionScope.searchResultForm.dispAttr3}" />
			<c:set var="dispAttr4Key"
				value="${sessionScope.searchResultForm.dispAttr4}" />
			<c:set var="dispAttr5Key"
				value="${sessionScope.searchResultForm.dispAttr5}" />
			<c:set var="dispAttr6Key"
				value="${sessionScope.searchResultForm.dispAttr6}" />

			<c:forEach var="item"
				items="${sessionScope.searchResultForm.getSearchResultList()}"
				varStatus="status" begin = "${iterateOffest}" end = "${iterateLength}">
				<c:if test="${((status.index - iterateOffest) % 15) == 0}">
					<tr>
						<td></td>
						<!--
                アクセスレベル1でもプリンタへ印刷指示は出来るように変更。by Hirata at '04.May.6
                <td></td>
                -->
						<td nowrap="nowrap" align="center" bgcolor="#CCCCCC"><span
							class="normal10">
								${sessionScope.searchResultForm.dispOutputSizeName}</span></td>
						<td nowrap="nowrap" align="center" bgcolor="#CCCCCC"><span
							class="normal10">
								${sessionScope.searchResultForm.dispCopiesName}</span></td>
						<td nowrap="nowrap" align="center" bgcolor="#CCCCCC"><span
							class="normal10">
								${sessionScope.searchResultForm.dispDwgNoName}</span></td>
						<td nowrap="nowrap" align="center" bgcolor="#CCCCCC"><span
							class="normal10">
								${sessionScope.searchResultForm.dispAttr1Name}</span></td>
						<td nowrap="nowrap" align="center" bgcolor="#CCCCCC"><span
							class="normal10">
								${sessionScope.searchResultForm.dispAttr2Name}</span></td>
						<td nowrap="nowrap" align="center" bgcolor="#CCCCCC"><span
							class="normal10">
								${sessionScope.searchResultForm.dispAttr3Name}</span></td>
						<td nowrap="nowrap" align="center" bgcolor="#CCCCCC"><span
							class="normal10">
								${sessionScope.searchResultForm.dispAttr4Name}</span></td>
						<td nowrap="nowrap" align="center" bgcolor="#CCCCCC"><span
							class="normal10">
								${sessionScope.searchResultForm.dispAttr5Name}</span></td>
						<td nowrap="nowrap" align="center" bgcolor="#CCCCCC"><span
							class="normal10">
								${sessionScope.searchResultForm.dispAttr6Name}</span></td>
					</tr>
				</c:if>

				<c:set var="bgcolor1" value="#FFFFFF" />
				<c:choose>
					<c:when test="${item.getAttrMap().get('PROHIBIT') == 'NG'}">
						<c:set var="bgcolor1" value="#FF66FF" />
					</c:when>
					<c:when
						test="${fn:length(item.getAttrMap().get('TWIN_DRWG_NO')) > 0}">
						<c:set var="bgcolor1" value="#FFFFA4" />
					</c:when>
				</c:choose>
				<tr bgcolor="${bgcolor1}">
					<%-- 2013.08.21 yamagishi modified. start
            <td><html:checkbox name="searchResultElement" property="selected" indexed="true" /></td> --%>
					<c:choose>
						<c:when test="${item.aclFlag == 1}">
							<td><input type="checkbox" name="${item.selected}"
								value="true"
								<c:if test="${item.selected}">
               checked="checked"
           </c:if> /></td>
						</c:when>
						<c:otherwise>
							<td />
						</c:otherwise>
					</c:choose>
					<%-- 2013.08.21 yamagishi modified. end --%>
					<!--
            アクセスレベル1でもプリンタへ印刷指示は出来るように変更。by Hirata at '04.May.6
            <%-- // この図面が、このユーザーにとって印刷可能なら
                if(user.isPrintable(searchResultElement)) { --%>
                <td></td>
            <%-- } else { --%>
                <td bgcolor="#FF0000"><span class="normal10white">×</span></td>
            <%-- } --%>
            -->
					<td align="center"><select name="${item.printSize}"
						id="printSize">
							<c:choose>
								<c:when test="${user.language == 'Japanese'}">
									<option value="ORG">原寸</option>
								</c:when>
								<c:otherwise>
									<option value="ORG">ORIGINAL</option>
								</c:otherwise>
							</c:choose>
							<option value="A0">A0</option>
							<option value="A1">A1</option>
							<option value="A2">A2</option>
							<option value="A3">A3</option>
							<option value="A4">A4</option>
							<option value="70.7%">70.7%</option>
							<option value="50%">50%</option>
							<option value="35.4%">35.4%</option>
							<option value="25%">25%</option>
					</select></td>
					<td><select name="${item.copies}" id="copies">
							<option value="1">1</option>
							<option value="2">2</option>
							<option value="3">3</option>
							<option value="4">4</option>
							<option value="5">5</option>
							<option value="6">6</option>
							<option value="7">7</option>
							<option value="8">8</option>
							<option value="9">9</option>
							<option value="10">10</option>
					</select></td>

					<%-- 2013.06.24 yamagishi modified. start
            <td nowrap="nowrap"><span class="normal12blue">
                <html:link forward="preview" name="searchResultElement" property="linkParmMap">
                    <bean:write name="searchResultElement" property="drwgNoFormated" />
                </html:link></span></td> --%>
					<c:choose>
						<c:when test="${item.aclFlag == 1 }">
							<td nowrap="nowrap"><span class="normal12blue"> <a
									id='<%= "drwgNoLink[" + "${status.index}" + "]" %>'
									href='<c:url value="/preview"/>'
									title='<c:out value="${item.aclBalloon}"/>'
									onclick='<%= "return openDLManagerDialog(" + "${status.index}" + ");" %>'>${item.drwgNoFormated}
								</a>

							</span></td>
						</c:when>
						<c:otherwise>
							<td nowrap="nowrap"><span class="normal12"
								title="${item.aclBalloon}"> ${item.drwgNoFormated}</span></td>
						</c:otherwise>
					</c:choose>
					<%-- 2013.06.24 yamagishi modified. end --%>
					<td nowrap="nowrap"><span class="normal12">&nbsp;
							${item.getAttr(dispAttr1Key)}&nbsp;</span></td>
					<td nowrap="nowrap"><span class="normal12">&nbsp;
							${item.getAttr(dispAttr2Key)}&nbsp;</span></td>
					<td nowrap="nowrap"><span class="normal12">&nbsp;
							${item.getAttr(dispAttr3Key)}&nbsp;</span></td>
					<td nowrap="nowrap"><span class="normal12">&nbsp;
							${item.getAttr(dispAttr4Key)}&nbsp;</span></td>
					<td nowrap="nowrap"><span class="normal12">&nbsp;
							${item.getAttr(dispAttr5Key)}&nbsp;</span></td>
					<td nowrap="nowrap"><span class="normal12">&nbsp;
							${item.getAttr(dispAttr6Key)}&nbsp;</span></td>
				</tr>
			</c:forEach>
		</table>
		<table class="nowsearch" id="nowSearch" style="visibility: hidden">
			<tr valign="middle">
				<td align="center" style="font-size: 18pt; color: #0000FF;"><c:choose>
						<c:when test="${sessionScope.user.language == 'Japanese' }">
検索中・・・・
</c:when>
						<c:otherwise>
Now Searching...
</c:otherwise>
					</c:choose></td>
			</tr>
		</table>
		<%-- IE互換性表示対策--%>
		<%-- パスワード変更画面起動中のクリック防止--%>
		<table class="nowsearch" id="screenLock" style="visibility: hidden">
			<tr>
				<td></td>
			</tr>
		</table>
	</form>
</body>
</html>
