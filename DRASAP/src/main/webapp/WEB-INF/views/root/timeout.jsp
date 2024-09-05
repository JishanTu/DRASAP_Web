<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="tyk.drasap.search.*,tyk.drasap.common.*" %>
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
	<title>Drawing Search and Print System [タイムアウト]</title>
	<script type="text/javascript">
	window.top.name = '_drasap_timeout';

	var orgReq = null;		// 原図庫作業依頼
	var orgDetail = null;	// 原図庫作業依頼詳細
	var orgList = null; 	// 原図庫作業依頼リスト
	var accessUpdate = null; // アクセスレベル一括更新
	var accessResult = null; // アクセスレベル更新結果

	// 子画面を閉じる
	// (画面の有効領域外でopen -> closeする)
	var closeSubScreen = function(fn) {
		var w = 1;
		var h = 1;

		var subWindow = null;
		var targetName = null;
		var targetUrl = 'about:blank';

//		var option = 'toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes,width='
//					+ w + ',height=' + h
		var option = null;
		var position = screen.availHeight * 3; // 画面有効領域外

		if(fn == "1") {
			// 図面検索画面
			targetName = '_drasap_search';
			subWindow = window.open(targetUrl, targetName, "top=" + position).close();
		}
		else if(fn == "2") {
			// 原図庫作業依頼
			targetName = '_drasap_request';
			orgReq = window.open(targetUrl, targetName, "top=" + position).close();
		}
		else if(fn == "3") {
			// 原図庫作業依頼詳細
			targetName = '_drasap_request_ref';
			orgDetail = window.open(targetUrl, targetName, "top=" + position).close();
		}
		else if(fn == "4") {
			// 原図庫作業依頼リスト
			targetName = '_drasap_request_list';
			orgList = window.open(targetUrl, targetName, "top=" + position).close();
		}
		else if(fn == "5") {
			// アクセスレベル一括更新
			targetName = '_drasap_acl_batch_update';
			accessUpdate = window.open(targetUrl, targetName, "top=" + position).close();
		}
		else if(fn == "6") {
			// アクセスレベル更新結果
			targetName = '_drasap_acl_updated_result';
			accessResult = window.open(targetUrl, targetName, "top=" + position).close();
		}
	}

/*
	// 子画面を閉じる
	function closeSubScreen2() {
		// 原図庫作業依頼
		if ((orgReq != undefined) && (!orgReq.closed)){
			orgReq.close();
		}
		// 原図庫作業依頼詳細
		if ((orgDetail != undefined) && (!orgDetail.closed)){
			orgDetail.close();
		}
		// 原図庫作業依頼リスト
		if ((orgList != undefined) && (!orgList.closed)){
			orgList.close();
		}
		// アクセスレベル一括更新
		if ((accessUpdate != undefined) && (!accessUpdate.closed)){
			accessUpdate.close();
		}
		// アクセスレベル更新結果
		if ((accessResult != undefined) && (!accessResult.closed)){
			accessResult.close();
		}
	}
*/

	// ログインページを開く
	function reLogin() {

		<%-- javascriptはシングルスレッドのため、並列処理はできない --%>
		<%-- パスワード変更画面でタイムアウトするとモーダルウィンドウ処理が
		残ったままとなるため、検索画面だけは閉じるようにする --%>
		// 子画面を閉じる
		closeSubScreen('1');

		<%-- 下記は不要であれば削除する --%>
		closeSubScreen('2');
		closeSubScreen('3');
		closeSubScreen('4');
		closeSubScreen('5');
		closeSubScreen('6');

//		closeSubScreen2();

<%--
		// ログインページに遷移 (履歴クリア。戻るボタンで戻れない)
		location.replace('<%=request.getContextPath() %>');
--%>

		// ログインページに遷移 (履歴クリア。戻るボタンで戻れない)
		top.location.replace('<%=request.getContextPath() %>/login');

		// ウィンドウ移動
//		moveWindow();
	}

	// ウィンドウの移動
	function moveWindow(){
		var w = screen.availWidth/2;
		var h = screen.availHeight/2;
		var xPos = (screen.availWidth- w)/2.0;
		var yPos = (screen.availHeight - h)/2.0;
		top.resizeTo(w, h);
		top.moveTo(xPos,yPos);//画面の位置指定
	}
	</script>
</head>
<body>
<%		CookieManage langCookie = new CookieManage(); %>
<%		String lanKey = langCookie.getCookie (request, null, "Language"); %>
<%		if (lanKey.equals("English")) { %>
	Invalid Login. Please try again.<br />
	The followings are considered.<br />
	<ul>
		<li>Session is time out.</li>
		<li>Login is not done thru correct route.</li>
	</ul>
	<br />
	After pressing the button below, the screen changes to the login screen about 5 seconds later. <br />
	<br />
	<!-- 再ログインボタン -->
	<input type="button" value="Re-login" onclick="reLogin()" />

<%		} else { %>
	無効なログインです。もう一度ログインを行ってください。<br />
	次の原因が考えられます。<br />
	<ul>
		<li>Sessionタイムアウトした。</li>
		<li>正常なルートでログインしていない。</li>
	</ul>
	<br />
	下記ボタンを押下後、約5秒後にログイン画面に遷移します。	<br />
	<br />
	<!-- 再ログインボタン -->
	<input type="button" value="再ログイン" onclick="reLogin()" />
<%		} %>

</body>
</html>
