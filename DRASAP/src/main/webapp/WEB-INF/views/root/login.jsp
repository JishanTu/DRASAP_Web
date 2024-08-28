<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="tyk.drasap.common.*" %>
<%@ page import="tyk.drasap.common.UserDef" %>
<%@ page import="java.nio.file.Files" %>
<%@ page import="java.nio.charset.Charset" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page isELIgnored="false"%>

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Cache-Control" content="no-cache" />
<title>Drawing Search and Print System [ログイン]</title>
<style type="text/css">
.errMsg {
	padding-left: 30px;
	padding-right: 10px;
	vertical-align: top;
}

td {
	white-space: nowrap;
}

.container {
    white-space: nowrap;
}
</style>
<script type="text/javascript">
	window.name='login';
	// 直リンク禁止
	var refinfo = document.referrer;
	if (!refinfo) {
		location.replace('<%=request.getContextPath()%>/timeout');
	}

	// メッセージ１読み込み
	function loadMessage1() {
		var div = document.getElementById('message1');

		var result = '<%= new UserDef().loadMessage(
				DrasapPropertiesFactory.getDrasapProperties(this).getProperty("tyk.login.message1.path")
				,Charset.forName("Shift_JIS")) %>'

		// テキストファイルの改行を置き換える
		// htmlタグが使用されている場合は何もしない
		result = replaceLineBreaksInTextFiles(result);
		div.innerHTML = result;
	}

	// メッセージ２読み込み
	function loadMessage2() {
		var ret = null;
		var div = document.getElementById('message2');

		var result = '<%= new UserDef().loadMessage(
				DrasapPropertiesFactory.getDrasapProperties(this).getProperty("tyk.login.message2.path")
				,Charset.forName("Shift_JIS")) %>'

		// テキストファイルの改行を置き換える
		// htmlタグが使用されている場合は何もしない
		result = replaceLineBreaksInTextFiles(result);
		//console.log("result" + result);
		div.innerHTML = result;
	}

	// メッセージ３読み込み
	function loadMessage3() {
		var div = document.getElementById('message3');

		var result = '<%= new UserDef().loadMessage(
				DrasapPropertiesFactory.getDrasapProperties(this).getProperty("tyk.login.message3.path")
				,Charset.forName("Shift_JIS")) %>'

		// テキストファイルの改行を置き換える
		// htmlタグが使用されている場合は何もしない
		result = replaceLineBreaksInTextFiles(result);
		div.innerHTML = result;
	}

	// テキストファイルの改行を置き換える
	// htmlタグが使用されている場合は何もしない
	function replaceLineBreaksInTextFiles(str) {
		// htmlタグ使用チェック (htmlコメントは除外)
		var pattern = /<(".*?"|'.*?'|[^'"!])*?>/g;
		var findRet = str.match(pattern);
		if(findRet == null) {
			// htmlタグ未使用
			// 改行コードを置換する
			str = str.replace(/&#010;/g, '<br>');
		}

		return str;
	}
	</script>
	<script type="text/javascript">
        // IEの場合、Broadcastはエラーになるので、Edgeのみ処理
        const agent = window.navigator.userAgent.toLowerCase();
        if(agent.indexOf('chrome') !== -1)
        {
            var IP;
            var xhr = new XMLHttpRequest();
            xhr.open('GET', '/DRASAP/getip');
            xhr.send();

            xhr.onreadystatechange = function()
            {
                if(xhr.readyState === 4 && xhr.status === 200)
                {
                    IP = xhr.responseText;
                }
            }

            var broadcast = new BroadcastChannel("DRASAP_LOGIN");
            broadcast.addEventListener('message',({data})=>{
                if(data === 'newlogin_' +IP)
                {
                    var login = window.open('about:blank','_self');
                    login.close();
                 }
            });
        }
    </script>
</head>
<body>
	<!-- drasap.propertiesの「web.container」で判定して、
		「weblogic」なら、このページを使用不可なホストなので、
		loginNotEnabled.jspへリダイレクトする -->
	<%	// 2013.08/27 yamagishi modified. start
	// String container = (String) DrasapPropertiesFactory.getDrasapProperties(this).get("web.container");
	// String hostName = request.getServerName();
	// if(!"mrdbsv01".equalsIgnoreCase(hostName) && "weblogic".equals(container)){
	String dev = DrasapPropertiesFactory.getDrasapProperties(this).getProperty("environment.dev");
	if (!"true".equals(dev)) {	// 2013.08.27 yamagishi modifiled. end %>
	<script>
		<%--	<logic:redirect page="/root/loginNotEnabled" />--%>
		location.replace('<%=request.getContextPath() %>/switch.do?page=/root/loginNotEnabled.jsp');
	</script>

	<%	} %>
	<div class = "container">
	<img src="<%=request.getContextPath() %>/resources/img/DRASAPBanner.JPG" width="333" height="70" />
    <img src="<%=request.getContextPath() %>/resources/img/CONFIDENTIALBanner.JPG" width="167" height="70" />
	</div>
	<!-- message1 -->
	<div id="message1"></div>
	<br />
	<form action="<%=request.getContextPath() %>/login" method = "post">
		<table>
			<tr>
				<td>
					<label for="id" >ユーザーＩＤ<br /></label>
					<label for="id" >User ID</label>
					<br /><br />
				</td>
				<td>
					<input type="text" name="id" maxlength="20" style="width:180px;" tabindex="1" />
					<%-- <html:text property="id" maxlength="10" tabindex="1" style="width:180px;" /> --%>
					<br /><br />
				</td>
				<td rowspan="3" class="errMsg">
					<!-- エラーの表示 -->
					<font color="RED">
					<ul>
						<c:if test="${loginId != null}">
							<c:forEach var="idmsg" items="${loginId}">
								<li>${idmsg}</li>
							</c:forEach>
						</c:if>
						<c:if test="${passwd != null}">
							<c:forEach var="passwdmsg" items="${passwd}">
								<li>${passwdmsg}</li>
							</c:forEach>
						</c:if>
						<c:if test="${message != null}">
							<c:forEach var="msg" items="${message}">
								<li>${msg}</li>
							</c:forEach>
						</c:if>
					</ul>
					</font>
				</td>
			</tr>
			<tr>
				<td>
					<label for="passwd" >パスワード<br /></label>
					<label for="passwd" >Password</label>
					<br /><br />
				</td>
				<td>
					<input type="password" name="passwd" maxlength="20" style="width:180px;" tabindex="2" />
					<%-- <html:password property="passwd" maxlength="20" tabindex="2" style="width:180px;"/> --%>
					<br /><br />
				</td>
			</tr>
			<tr>
				<td>
					<input type="submit" value="ログイン / login" tabindex="3">
				</td>
				<td>
					<!-- <input type="button" value="閉じる" onclick="self.close()" /> -->
				</td>
			</tr>
		</table>
	</form>
	<br />
	<!-- message2 -->
	<div id="message2"></div>
	<br />
	<!-- message3 -->
	<div id="message3"></div>

	<script defer type="text/javascript">
		// HTMLを最後まで読み終わってから実行する処理
		loadMessage1();
		loadMessage2();
		loadMessage3();
	</script>
</body>
</html>
