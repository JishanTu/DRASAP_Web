<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge" />
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<title>Drawing Search and Print System [ログイン]</title>
	<script type="text/javascript" src="<%=request.getContextPath() %>/resources/js/proxy.js"></script>
	<script type="text/javascript">
		// 2022.04.13 Windows Edge対応. ポータルから表示した際にblank画面が表示される問題を修正.
		const agent = window.navigator.userAgent.toLowerCase();
		
		// 右クリック禁止
		document.onmousedown = disableContextMenu;
		document.oncontextmenu = disableOnContextMenu;
		
		window.name = '_drasap_pre_login_page'

 	    if(agent.indexOf('chrome') !== -1) {
			var IP;
			var xhr = new XMLHttpRequest();
			xhr.open('GET', '/DRASAP/getip');
			xhr.send();
			
			xhr.onreadystatechange = function() {
				if(xhr.readyState === 4 && xhr.status === 200) {
					IP = xhr.responseText;
					let passIndex = proxy.findIndex(function(element){return element === IP;});
					
					if(passIndex < 0)
					{
						//ログイン画面呼び出し前の通知
						var broadcast = new BroadcastChannel("DRASAP_LOGIN");
						broadcast.postMessage('newlogin_' + IP);
						broadcast.close();
					}

					targetName = '_drasap_login_page';

					//		WO1=window.open('about:blank', targetName,
					//			'toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes,width=' + w + ',height=' + h);
					
					WO1=window.open('about:blank', targetName,
							'toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes');

					// 参照タイミングが早い(既にウィンドウが開かれている場合)と
					// ウィンドウ移動エラーとなるため、一瞬sleepする
					var interval = setInterval(function() {
						// Intervalを破棄
						clearInterval(interval);
						
						//			WO1.moveTo(xPos, yPos);//画面の位置指定
						WO1.focus();
						    //// 検索画面をログアウトした場合に表示されるログインページが表示されている場合は閉じる
						window.open("about:blank","_drasap_pre_login_page").close();
						 // 元の画面に戻す
				         history.back();
					},500);

				   <%-- リンクを自動クリック--%>
					var a = document.getElementById("login");
					a.click();
				}
			}
		}
 
		// 空の新しいウィンドウを開く
		function openNewWindow() {
			if(agent.indexOf('trident') != -1) {
				var WO1;
				/*  // ウィンドウサイズは変更しない
				    var w = screen.availWidth/2;
				    var h = screen.availHeight/2;
				    var xPos = (screen.availWidth- w)/2.0;
				    var yPos = (screen.availHeight - h)/2.0;
				*/
				
				targetName = '_drasap_login_page';
				
					WO1=window.open('about:blank', targetName,
						'toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes,width=' + w + ',height=' + h);
			}
		}

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

		function onLoad() {
			if(agent.indexOf('trident') != -1) {
				// 右クリック禁止
				//document.onmousedown = disableContextMenu;
				//document.oncontextmenu = disableOnContextMenu;
				
				<%-- リンクを自動クリック--%>
				var a = document.getElementById("login");
				a.click();
				
				// この画面を閉じる
				window.open("about:blank","_drasap_pre_login_page").close();
			}
		}
	</script>
</head>
<body onload="onLoad()">
	<%-- IEでReferrerが取得できない問題の対策
	空の新しいウィンドウを開いた後、ログインページに遷移する--%>
	<a id="login" type="hidden" href="<%=request.getContextPath() %>/login"
		target="_drasap_login_page" onclick="javascript:openNewWindow()"></a>
</body>
</html>
