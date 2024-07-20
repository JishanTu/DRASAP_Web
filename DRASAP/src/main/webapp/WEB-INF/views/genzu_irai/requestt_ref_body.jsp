<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%-- ログイン情報の確認 --%>
<c:if test="${empty sessionScope.user}">
	<script>
		location.replace('<%=request.getContextPath() %>/timeout');
	</script>
</c:if>
<html>
<head>
	<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
	<title>図面登録依頼詳細</title>
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<style type="text/css">
		@import
		url(
		<%=request.getContextPath()%>/resources/css/default.css
		);
	</style>
	<script type="text/javascript">
		// <!--
		function act(param1, param2) {
			document.forms[0].action.value = param1;//アクション
			document.forms[0].job.value = param2;//依頼ID,依頼内容,行番号のデータを取得する
			//alert("アクション = " + document.forms[0].action.value + ", job_id = " + document.forms[0].job_id.value);
			var targetName = '_messege';//別の画面を開く

			document.forms[0].target = targetName;
			var WO1;
			var w = screen.availWidth - 300;
			var h = screen.availHeight - 300;

			WO1 = window.open("", targetName, "toolbar=no,resizable=yes,width=" + w
					+ ",height=" + h);
			//'toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes,width=' + w + ',height=' + h);
			WO1.window.moveTo(150, 0);//画面の位置指定
			WO1.focus();

			document.forms[0].submit();
		}
		//-->
	</script>
</head>
<body bgcolor="#FFFFFF" bottommargin="0" leftmargin="0" topmargin="0"
	  rightmargin="0" marginheight="0" marginwidth="0">
<form action="<%=request.getContextPath()%>/req_ref" method="post">
	<input type="hidden" name="action" /> <input type="hidden" name="job" />
	<font color="red" size="4">
		<c:if test="${not empty request_refForm.listErrors}">
			<c:forEach var="error" items="${request_refForm.listErrors}">
				<li><c:out value="${error}"/></li>
			</c:forEach>
		</c:if>
	</font>
	<form:errors path="*" cssClass="error-message" />
	<table border="0" align="center">
		<tr bgcolor="#CCCCCC">
			<td nowrap="nowrap" align="center"><span class="normal10">依頼ID</span></td>
			<td nowrap="nowrap" align="center"><span class="normal10">状態</span></td>
			<td nowrap="nowrap" align="center"><span class="normal10">依頼内容</span></td>
			<%-- // 2019.10.23 yamamoto modified. start
    <td nowrap="nowrap" align="center"><span class="normal10">号口・号機</span></td>
    <td nowrap="nowrap" align="center"><span class="normal10">原図内容</span></td>
--%>
			<td nowrap="nowrap" colspan="3" align="center"><span
					class="normal10">図番</span></td>
			<%--
    <td nowrap="nowrap" align="center"><span class="normal10">部数</span></td>
    <td nowrap="nowrap" align="center"><span class="normal10">縮小</span></td>
    <td nowrap="nowrap" align="center"><span class="normal10">サイズ</span></td>
    <td nowrap="nowrap" align="center"><span class="normal10">出力先</span></td>
// 2019.10.23 yamamoto modified. end --%>
			<td nowrap="nowrap" align="center" bgcolor="#FFFFFF"></td>
		</tr>
		<c:forEach var="item" items="${request_refForm.iraiList}">

		<c:set var="e" value="${item}"/>
		<c:set var="stat" value=""/>
		<c:set var="job_id" value="${e.job_id}"/>
		<c:set var="job_stat" value="${e.job_stat}"/>
		<c:choose>
			<c:when test="${'0' eq job_stat}">
				<c:set var="job_stat" value="依頼中"/>
			</c:when>
			<c:otherwise>
				<c:set var="job_stat" value="完了"/>
			</c:otherwise>
		</c:choose>
		<c:set var="job_name" value="${e.job_name}"/>
		<c:set var="gouki" value="${e.gouki}"/>
		<c:if test="${empty gouki}">
			<c:set var="gouki" value=""/>
		</c:if>

		<c:set var="genzu" value="${e.genzu}"/>
		<c:if test="${empty genzu}">
			<c:set var="gouki" value=""/>
		</c:if>

		<c:set var="kaisi" value="${e.start}"/>
		<c:if test="${empty kaisi}">
			<c:set var="kaisi" value=""/>
		</c:if>

		<c:set var="end" value="${e.end}"/>
		<c:if test="${empty end}">
			<c:set var="end" value=""/>
		</c:if>

		<c:set var="busuu" value="${e.busuu}"/>
		<c:if test="${empty busuu}">
			<c:set var="busuu" value=""/>
		</c:if>

		<c:set var="syuku" value="${e.syuku}"/>
		<c:if test="${empty syuku}">
			<c:set var="syuku" value=""/>
		</c:if>

		<c:set var="size" value="${e.size}"/>
		<c:if test="${empty size}">
			<c:set var="size" value=""/>
		</c:if>

		<c:set var="printer" value="${e.printer}"/>
		<c:if test="${empty printer}">
			<c:set var="printer" value=""/>
		</c:if>

		<c:set var="messege" value="${e.messege}"/>
		<c:if test="${empty messege}">
			<c:set var="messege" value=""/>
		</c:if>

		<c:set var="exist" value="${e.exist}"/>
		<c:set var="tenkai_deta" value=""/>
		<c:set var="sagyo_deta" value=""/>

		<c:set var="tenkai_deta"
			   value="${('図面登録依頼' eq job_name or '図面出力指示' eq job_name) and (not empty messege or '0' eq exist) ? '1' : ''}"/>

		<c:set var="messege1"
			   value="${not empty e.messege1 ? e.messege1 : ''}"/>

		<c:set var="sagyo_deta"
			   value="${('原図借用依頼' eq job_name or '図面以外焼付依頼' eq job_name) and (not empty messege1 or '2' eq stat) ? '1' : ''}"/>


		<c:set var="seq" value="${e.seq}"/>
		<c:set var="rowNo" value="${e.rowNo}"/>

		<c:set var="job_list" value="${job_id}_${job_name}_${rowNo}"/>
		<c:set var="str_Messege"
			   value="act('button_Mtenkai', '${job_list}')"/>
		<c:set var="str_Messege1"
			   value="act('button_Msagyo', '${job_list}')"/>

		<c:choose>
		<c:when test="${'依頼中' eq job_stat}">
		<tr>
			</c:when>
			<c:otherwise>
		<tr bgcolor="#CCCCFF">
			</c:otherwise>
			</c:choose>

			<td nowrap="nowrap" align="center"><span class="normal10">${job_id}</span></td>
			<td nowrap="nowrap" align="center"><span class="normal10">${job_stat}</span></td>
			<td nowrap="nowrap" align="center"><span class="normal10">${job_name}</span></td>

			<td nowrap="nowrap" align="center"><span class="normal10">${kaisi}</span></td>
			<td nowrap="nowrap" align="center"><span class="normal10">～</span></td>
			<td nowrap="nowrap" align="center"><span class="normal10">${end}</span></td>

			<c:choose>
			<c:when test="${'1' eq tenkai_deta}">
			<td nowrap="nowrap" align="center" bgcolor="#FF3300"><a
					href="javascript:${str_Messege}"> <span class="normal10white">ﾒｯｾｰｼﾞ</span>
			</a></td>
			</c:when>
			<c:when test="${'1' eq sagyo_deta}">
			<td nowrap="nowrap" align="center" bgcolor="#FF3300"><a
					href="javascript:${str_Messege1}"> <span
					class="normal10white">ﾒｯｾｰｼﾞ</span>
			</a></td>
			</c:when>
			</c:choose>
			</c:forEach>
	</table>
</form>
</body>
</html>
