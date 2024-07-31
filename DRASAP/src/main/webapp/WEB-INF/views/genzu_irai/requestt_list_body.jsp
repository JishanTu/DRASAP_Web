<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
<title>図面登録依頼リスト</title>
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Cache-Control" content="no-cache" />
<style type="text/css">
@import url(<%=request.getContextPath()%>/resources/css/default.css);
</style>
</head>
<body bgcolor="#FFFFFF" bottommargin="0" leftmargin="0" topmargin="0"
    rightmargin="0" marginheight="0" marginwidth="0">
    <font color="red" size="4"> 
        <c:forEach var="err" items="${request_listForm.listErrors}">
            <li><c:out value="${err}" /></li>
        </c:forEach>
    </font>
    <form action="<%=request.getContextPath() %>/req_list" method="post">
        <input type="hidden" name="action" value="${action}" />
        <table border="0" align="center" name="iraiList">
            <tr bgcolor="#CCCCCC">
                <td nowrap="nowrap" align="center"><span class="normal10">チェック</span></td>
                <td nowrap="nowrap" align="center"><span class="normal10">依頼ID</span></td>
                <td nowrap="nowrap" align="center"><span class="normal10">時間</span></td>
                <td nowrap="nowrap" align="center"><span class="normal10">依頼内容</span></td>
                <td nowrap="nowrap" align="center"><span class="normal10">図番</span></td>
                <td nowrap="nowrap" align="center"><span class="normal10">メッセージ</span></td>
                <td nowrap="nowrap" align="center"><span class="normal10">依頼者</span></td>
                <td nowrap="nowrap" align="left"><span class="normal10">部署名</span></td>
            </tr>
            <c:forEach var="e" items="${request_listForm.iraiList}" varStatus="loop">
                <tr>
                    <td>
                        <select name="iraiList[${loop.index}].touroku">
                            <c:forEach items="${request_listForm.checkKeyList}" var="checkKey" varStatus="innerLoop">
                                <option value="${checkKey}">${request_listForm.checkNameList[innerLoop.index]}</option>
                            </c:forEach>
                        </select>
                    </td>
                    <td nowrap="nowrap" align="center"><span class="normal10">${e.job_id}</span></td>
                    <td nowrap="nowrap" align="center"><span class="normal10">${e.zikan}</span></td>
                    <td nowrap="nowrap" align="center"><span class="normal10">${e.irai}</span></td>
                    <td nowrap="nowrap" align="center"><span class="normal10">${e.zuban}</span></td>
                    <td nowrap="nowrap" align="center">
                        <select name="iraiList[${loop.index}].message">
                            <c:forEach items="${request_listForm.messageNameList}" var="messageName" varStatus="innerLoop">
                                <option value="${messageName}">${request_listForm.messageNameList[innerLoop.index]}</option>
                            </c:forEach>
                        </select>
                    </td>
                    <td nowrap="nowrap" align="center"><span class="normal10">${e.user_name}</span></td>
                    <td nowrap="nowrap" align="left"><span class="normal10">${e.busyo_name}</span></td>
                </tr>
                <input type="hidden" name="iraiList[${loop.index}].seq" value="${e.seq}" />
                <input type="hidden" name="iraiList[${loop.index}].job_id" value="${e.job_id}" />
                <input type="hidden" name="iraiList[${loop.index}].rowNo" value="${e.rowNo}" />
                <input type="hidden" name="iraiList[${loop.index}].zikan" value="${e.zikan}" />
                <input type="hidden" name="iraiList[${loop.index}].zuban" value="${e.zuban}" />
                <input type="hidden" name="iraiList[${loop.index}].irai" value="${e.irai}" />
                <input type="hidden" name="iraiList[${loop.index}].gouki" value="${empty e.gouki ? '' : e.gouki}" />
                <input type="hidden" name="iraiList[${loop.index}].genzu" value="${empty e.genzu ? '' : e.genzu}" />
                <input type="hidden" name="iraiList[${loop.index}].busuu" value="${empty e.busuu ? '' : e.busuu}" />
                <input type="hidden" name="iraiList[${loop.index}].syukusyou" value="${empty e.syukusyou ? '' : e.syukusyou}" />
                <input type="hidden" name="iraiList[${loop.index}].size" value="${empty e.size ? '' : e.size}" />
                <input type="hidden" name="iraiList[${loop.index}].busyo" value="${e.busyo_name}" />
                <input type="hidden" name="iraiList[${loop.index}].hiddenMessage" value="${e.hiddenMessage}" />
                <input type="hidden" name="iraiList[${loop.index}].hiddenTouroku" value="${e.hiddenTouroku}" />
            </c:forEach>
        </table>
    </form>
</body>
</html>
