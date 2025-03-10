﻿<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="tyk.drasap.common.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page isELIgnored="false"%>

<%-- ログイン情報の確認 --%>
<c:if test="${empty sessionScope.user}">
<script>
	location.replace('<%=request.getContextPath()%>/timeout');
</script>
</c:if>
<c:set var="searchConditionForm" value="${sessionScope.searchConditionForm}"/>

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
	<title>Drawing Search and Print System [図面検索]</title>
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<style type="text/css">@import url( <%=request.getContextPath() %>/resources/css/<%=session.getAttribute("default_css")%> );</style>
	<style type="text/css">
		.tooltip {
			width: 460px;
			height: 120px;
			text-align: center;
			padding: 0px;
			margin: 0px;
			position: absolute;
			cursor: default;
			top: 35px;
			right:0;
			/*font-size:16pt;*/
			visibility: visible;
			z-index: 100;
		}
		
		#toolotipContents {
			display: none;
		}
		
		img {
			background-color: #CCCCCC;
			position: relative;
			left: -10px;
			border: none;
			/*width:20px;*/
			padding: 0px;
			margin: 0px;
		}
		
		.slideBar {
			width: 15px;
			height: 120px;
			padding: 0px;
			margin: 0px;
			background-color: #CCCCCC;
			border-width: 2px;
			border-left-color: #EEEEEE;
			border-top-color: #EEEEEE;
			border-right-color: #AAAAAA;
			border-bottom-color: #AAAAAA;
			/*border-color:#CCCCCC;*/
			/*border-style:ridge;*/
			border-style: solid;
		}
		
		.menubutton {
			margin-right: 30px;
			vertical-align: top;
		}
		.searchbutton{
			margin-left : 5px;
			vertical-align : top;
		}
		
		.table-container {
			overflow-y: auto;
			/*height: 140px;*/
			width: 595px
		}
	</style>
<script type="text/javascript">
	document.onkeydown = keys;
	function keys(){
		switch (event.keyCode ) {
			case 116: // F5
				event.keyCode = 0;
				return false;
				break;
		}
	}

//	function setTableWidth() {
//			var availWidth = screen.availWidth;
//			var table1 = document.getElementById('conditionTable1');
//			var table2 = document.getElementById('conditionTable2');
//			table1.style.width = availWidth + 'px';
//			table2.style.width = availWidth + 'px';
//		}

	function onLoad(){
		onInitFocus();
//		var divisionTD = document.getElementById("divisionTD");
//		var division = document.getElementById("division");
//		division.offsetWidth = divisionTD.clientWidth;
		loadResultFrame();
		// 図番指定順のチェック
		isOrderDrwgNo();
		//setTableWidth();
	}
	// 昇順、降順をサイクリックに切り替える
	function changeOrder(obj,index){
		var ascStr;
		var descStr;
		var language = "${sessionScope.user.language}";
		if (language=="Japanese") {
			ascStr = "昇順";
			descStr = "降順";
		} else {
			ascStr = "Asc";
			descStr = "Desc";
		}
		//alert(obj.value);
		if(obj.value==ascStr){
			obj.value=descStr;
		} else if(obj.value==descStr) {
			obj.value="　　　";
		} else {
			obj.value=ascStr;
		}
	}

	// ボタンのロック
	function lockDocumentButtons(docObj) {
		if (!docObj) return;
		var buttons = docObj.getElementsByTagName("input");
		for (var i=0;i<buttons.length;i++){
			if (buttons[i].value != "Close")
				buttons[i].disabled = true;
		}
		buttons = docObj.getElementsByTagName("select");
		for (var i=0;i<buttons.length;i++){
				buttons[i].disabled = true;
		}
		buttons = docObj.getElementsByTagName("a");
		for (var i=0;i<buttons.length;i++){
				buttons[i].disabled = true;
		}
		buttons = docObj.getElementsByTagName("button");
		for (var i=0;i<buttons.length;i++){
				buttons[i].disabled = true;
		}
		buttons = docObj.getElementsByTagName("textarea");
		for (var i=0;i<buttons.length;i++){
				buttons[i].disabled = true;
		}
		if (!docObj.body) return;
			docObj.body.style.cursor = 'wait';
	}
	// ボタンのアンロック
	function unLockDocmentButtons(docObj) {
		if (!docObj) return;
		var buttons = docObj.getElementsByTagName("input");
		for (var i=0;i<buttons.length;i++){
				buttons[i].disabled = false;
		}
		var buttons = docObj.getElementsByTagName("select");
		for (var i=0;i<buttons.length;i++){
				buttons[i].disabled = false;
		}
		var buttons = docObj.getElementsByTagName("a");
		for (var i=0;i<buttons.length;i++){
				buttons[i].disabled = false;
		}
		buttons = docObj.getElementsByTagName("button");
		for (var i=0;i<buttons.length;i++){
				buttons[i].disabled = false;
		}
		buttons = docObj.getElementsByTagName("textarea");
		for (var i=0;i<buttons.length;i++){
				buttons[i].disabled = false;
		}
		if (!docObj.body) return;
			docObj.body.style.cursor = '';
	}
	// 親画面のボタンのロック
	function lockButtons() {
		lockDocumentButtons(document);
		if (parent.result != null) lockDocumentButtons(parent.result.document);
		if (parent.result.result_head != null) lockDocumentButtons(parent.result.result_head.document);
		if (parent.result.result_foot != null) lockDocumentButtons(parent.result.result_foot.document);
	}
	// 親画面のボタンのアンロック
	function unLockButtons() {
		unLockDocmentButtons(document);
		if (parent.result != null) unLockDocmentButtons(parent.result.document);
		if (parent.result.result_head != null) unLockDocmentButtons(parent.result.result_head.document);
		if (parent.result.result_foot != null) unLockDocmentButtons(parent.result.result_foot.document);
		// 図番指定順のチェック
		isOrderDrwgNo();
	}

	// 検索する
	function doSearch(){
		var currentForm = document.forms[0];
		for (var i = 1; i <= ${searchConditionForm.getSearchSelColNum()}; i++) {// ボタンの値を隠し属性コピー
			var sortWay = 'sortWay' + i;
			var sortWayButton = 'sortWayButton' + i;
			currentForm[sortWay].value = currentForm[sortWayButton].value;
		}

		// 表示属性を検索結果の画面から取得しsetする
		if(parent.result.result_head != null){// nullチェックを追加
			var parentForm = parent.result.result_head.document.forms[0];
			for (var j = 1; j <= ${searchConditionForm.getViewSelColNum()}; j++) {
				var dispAttr = 'dispAttr' + j;
				currentForm[dispAttr].value = parentForm[dispAttr].value;
			}
		}

		// 図番指定順にチェックありの場合
		if (currentForm.orderDrwgNocheckbox.checked) {
			// ワイルドカードが使用されているか？
			if(isWildCard(currentForm.multipleDrwgNo)) {
				alert("<c:out value='${searchConditionForm.listOrderErrMsg}' />");
				return false;
			}
		}

		currentForm.act.value="search";// 隠し属性actに'search'をセット
		currentForm.target="result";// targetは'result'

		currentForm.submit();
		screenLockbySearch();
	}

	// ヘルプを表示する
	function help(){
		var targetName = '_help';//別の画面を開く
		var WO1;
		
		var w = window.outerWidth  - 16;
		var h = window.outerHeight - 70;
		
		var screenWidth  = window.screen.availWidth;
		var screenHeight = window.screen.availHeight;
		
		var left = Math.max(0, (screenWidth  - w) / 2 - 8);
		var top  = Math.max(0, (screenHeight - h) / 2 - 35);

		WO1=window.open("<%=request.getContextPath() %>/search/searchHelp.jsp", targetName,
			//"toolbar=no,resizable=yes,width=" + w + ",height=" + h);
			'toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes,width=' + w + ',height=' + h +',top='+ top + ',left='+ left );
		//WO1.window.moveTo(50,50);//画面の位置指定
		WO1.focus();
	}
	// 言語変更
/*
	function changeLang_old(lang) {
		document.forms[0].act.value='CHANGELANGUAGE';// 隠し属性actにをセット
		document.forms[0].target="_top";// ターゲットは画面全体
		document.forms[0].language.value = lang;
		document.forms[0].submit();
	}
*/
	// 言語変更
	function changeLang() {
		document.forms[0].act.value='CHANGELANGUAGE';// 隠し属性actにをセット
		document.forms[0].target="_top";// ターゲットは画面全体
		document.forms[0].submit();
	}
	// 初期フォーカス位置
	function onInitFocus(){
		document.forms[0].conditionValue1.focus();
	}
	function loadResultFrame() {
		<%if("multipreview".equals(request.getParameter("act"))){  %>
			parent.result.location.href = "<%=request.getContextPath() %>/resultPre.do?task=multipreview";
		<% } %>
	}
	// 説明文スライド処理
	var rightVal=-440;
	var intHide = null;
	var intShow = null;
	var speed=20;
	function show_tool_tip(item_name, flg) {
		createToolTip();
		if (flg) {
			document.getElementById(item_name).style.visibility='visible';
			document.getElementById(item_name).style.display='block';
			if (intHide != null) clearInterval(intHide);
			intHide = null;

			intShow=setInterval("slideToolTip('"+item_name+"', 'left')",10);
		} else {
			if (intShow != null) clearInterval(intShow);
			intShow = null;

			// 2019.10.02 yamamoto add
			// IEの互換性無しの場合、Tooltipが画面上に残り続けるため、非表示とする
			// intHide=setInterval("slideToolTip('"+item_name+"', 'right')",10);
			document.getElementById('toolotip').style.visibility = "hidden";
			document.getElementById(item_name).style.display='none';
		}
	}
	function slideToolTip(tooltip, flg) {
		if (flg == "right") {
			if (rightVal > -440) {
				rightVal=rightVal-speed;
				document.getElementById(tooltip).style.right=rightVal;
			} else {
				if (intShow != null) clearInterval(intShow);
				intShow = null;
			}
		} else if (flg == "left") {
			if (rightVal < 0) {
				rightVal=rightVal+speed;
				window.status=rightVal;
				document.getElementById(tooltip).style.right=rightVal;
			} else {
				if (intHide != null) clearInterval(intHide);
				intHide = null;
			}
		}
	}
	function createToolTip() {
		var toolotipContents = document.getElementById("toolotipContents").innerHTML;
		window.frames["toolotip"].document.write(
			"<html><head>"+
			"<style type='text/css'>"+
			".slideBar {"+
			"width:15px;"+
			"height:120px;"+
			"padding:0px;"+
			"margin:0px;"+
			"background-color:#CCCCCC;"+
			"border-width:2px;"+
			"border-left-color:#EEEEEE;"+
			"border-top-color:#EEEEEE;"+
			"border-right-color:#AAAAAA;"+
			"border-bottom-color:#AAAAAA;"+
			"border-style:solid;"+
			"}"+
			"</style></head>"+
			"<body style='margin:0px;padding:0px;'>"+
			toolotipContents+
			"</body></html>");
	}
	// 2019.09.25 yamamoto add start

	// ブラウザがIEかどうか？
	function isIE() {
		var userAgent = window.navigator.userAgent.toLowerCase();
		//console.log("userAgent:" + userAgent);
		if ( userAgent.indexOf( 'msie' ) !== -1 || userAgent.indexOf( 'trident' ) !== -1 ) {
			return true;
		}
		return false;
	}

	// 互換性表示か？
	function isCompatibilityDisplay() {
		var appVersion = window.navigator.appVersion.toLowerCase();
		//console.log("appVersion:" + appVersion);
		if ( appVersion.indexOf( 'msie' ) !== -1 ) {
			return true;
		}
		return false;
	}

	// 新規ウィンドウ画面を表示する
	function openNewWindow(){
		var targetName = '_chgPass';//別の画面を開く
		var WO1;
		var w = window.outerWidth  - 16;
		var h = window.outerHeight - 70;
		
		var screenWidth  = window.screen.availWidth;
		var screenHeight = window.screen.availHeight;
		
		var left = Math.max(0, (screenWidth  - w) / 2 - 8);
		var top  = Math.max(0, (screenHeight - h) / 2 - 35);
		var targetUrl = null;

		// その他の場合
		targetUrl = 'about:blank'
		WO1=window.open(targetUrl, targetName,
			'toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes,width=' + w + ',height=' + h + ',left=' + left + ',top=' + top);
		//WO1.window.moveTo(50,50);//画面の位置指定
		WO1.focus();

		// 親画面にシェードをかける
		screenLock();

		// １秒間隔で子画面の状態を監視
		var interval = setInterval(function() {
			// 子画面が閉じていたら
			if(!WO1 || WO1.closed) {

				// Intervalを破棄
				clearInterval(interval);

				// 親画面のシェードを外す
				screenUnLock('screenLock');
			}
			// 画面が起動していたら
			else {
				// 子画面にフォーカスを当てる
				if(!WO1.document.hasFocus()) {
					WO1.focus();
				}
			}
		},1000);
	}

	// パスワード変更画面起動
	function changePassword() {
		<%-- リンクをクリック--%>
		var a = document.getElementById("chgPass");
		a.click();
	}

	// 画面ロックスタイル
	function screenLockStyle(id) {
		// ロック用のdivを生成
		var element = document.createElement('div');
		element.id = id;
		// ロック用のスタイル
		element.style.height = '100%';
		element.style.left = '0px';
		element.style.position = 'fixed';
		element.style.top = '0px';
		element.style.width = '100%';
		element.style.zIndex = '9999';
		element.style.opacity = '0';
		element.style.backgroundColor = 'gray';

		return element;
	}

	// 画面ロック
	function screenLock() {
		if (isIE() && isCompatibilityDisplay()) {
			// IEの互換性表示の場合、親画面にロックがかけられないため、
			// 各frameの画面内でロックをかける
			if (parent.result.result_body != null) {
				// 検索結果画面のロック
				parent.result.result_body.screenLock();
			}
		} else {
			// その他の場合
			// 親画面全体にシェードをかける
			var objBody = top.document.getElementsByTagName("html").item(0);
			objBody.appendChild(screenLockStyle('screenLock'));
		}

		// ボタンやリンクのロック
		lockButtons();
	}
	// 画面ロック (検索開始)
	function screenLockbySearch() {
		if (parent.result.result_body != null) {
			parent.result.result_body.nowSearch();
		} else if (parent.result != null) {
			if (parent.result.nowSearch != undefined)
				parent.result.nowSearch();
		}

		// ボタンやリンクのロック
		lockButtons();
	}

	// 画面ロック解除
	function screenUnLock( id_name ){
		if (isIE() && isCompatibilityDisplay()) {
			// IEの互換性表示の場合
			if (parent.result.result_body != null) {
				// 検索結果画面のアンロック
				parent.result.result_body.screenUnLock();
			}
		} else {
			// その他の場合
			// 親画面全体のシェードを解除
			var dom_obj = top.document.getElementById(id_name);
			var dom_obj_parent = dom_obj.parentNode;
			dom_obj_parent.removeChild(dom_obj);
		}

		// ボタンやリンクのロック解除
		unLockButtons();
	}

	var orgReq = null;		// 原図庫作業依頼
	var orgDetail = null;	// 原図庫作業依頼詳細
	var orgList = null; 	// 原図庫作業依頼リスト
	var accessUpdate = null; // アクセスレベル一括更新
	var accessResult = null; // アクセスレベル更新結果

	// 子画面を開く
	function openSubScreen(fn) {
		var w = window.outerWidth  - 16;
		var h = window.outerHeight - 70;

		var screenWidth  = window.screen.availWidth;
		var screenHeight = window.screen.availHeight;

		var left = Math.max(0, (screenWidth  - w) / 2 - 8);
		var top  = Math.max(0, (screenHeight - h) / 2 - 35);

		var targetName = null;
		var targetUrl = null;
		var option = 'toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes,width=' + w + ',height=' + h + ',left=' + left + ',top=' + top

		if (fn == "2") {
			// 原図庫作業依頼
			targetName = '_drasap_request';
			targetUrl = '/req.do';

			orgReq = window.open("<%=request.getContextPath() %>" + targetUrl, targetName, option, "sub");
			//orgReq.window.moveTo(left,top);//画面の位置指定
			orgReq.focus();
		}
		else if (fn == "3") {
			// 原図庫作業依頼詳細
			targetName = '_drasap_request_ref';
			targetUrl = '/switch.do?page=/genzu_irai/requestt_ref.jsp';

			orgDetail = window.open("<%=request.getContextPath() %>" + targetUrl, targetName, option, "sub");
			//orgDetail.window.moveTo(0,0);//画面の位置指定
			orgDetail.focus();
		}
		else if (fn == "4") {
			// 原図庫作業依頼リスト
			targetName = '_drasap_request_list';
			targetUrl = '/switch.do?page=/genzu_irai/requestt_list.jsp';

			orgList = window.open("<%=request.getContextPath() %>" + targetUrl, targetName, option, "sub");
			//orgList.window.moveTo(0,0);//画面の位置指定
			orgList.focus();
		}
		else if (fn == "5") {
			// アクセスレベル一括更新
			targetName = '_drasap_acl_batch_update';
			targetUrl = '/switch.do?page=/system/accessLevelBatchUpdate.jsp';

			accessUpdate = window.open("<%=request.getContextPath() %>" + targetUrl, targetName, option, "sub");
			//accessUpdate.window.moveTo(0,0);//画面の位置指定
			accessUpdate.focus();
		}
		else if (fn == "6") {
			// アクセスレベル更新結果
			targetName = '_drasap_acl_updated_result';
			targetUrl = '/switch.do?page=/system/accessLevelUpdatedResult.jsp';

			accessResult = window.open("<%=request.getContextPath() %>" + targetUrl, targetName, option, "sub");
			//accessResult.window.moveTo(0,0);//画面の位置指定
			accessResult.focus();
		}
	}
/*
	// 子画面があるか？
	function checkSubScreen() {
		var sub = true;
		var screenName = [];
		// 原図庫作業依頼
		if (orgReq != undefined) {
			// 子画面が閉じていればtrue
			sub = orgReq.closed;

			if(!sub) {
				// 子画面あり
				screenName.push("<bean:write name="searchConditionForm" property="c_label12" />");
			}
		}
		// 原図庫作業依頼詳細
		if (orgDetail != undefined) {
			// 子画面が閉じていればtrue
			sub = orgDetail.closed;

			if(!sub) {
				// 子画面あり
				screenName.push("<bean:write name="searchConditionForm" property="c_label13" />");
			}
		}
		// 原図庫作業依頼リスト
		if (orgList != undefined) {
			// 子画面が閉じていればtrue
			sub = orgList.closed;

			if(!sub) {
				// 子画面あり
				screenName.push("<bean:write name="searchConditionForm" property="c_label14" />");
			}
		}
		// アクセスレベル一括更新
		if (accessUpdate != undefined) {
			// 子画面が閉じていればtrue
			sub = accessUpdate.closed;

			if(!sub) {
				// 子画面あり
				screenName.push("<bean:write name="searchConditionForm" property="c_label15" />");
			}
		}
		// アクセスレベル更新結果
		if (accessResult != undefined) {
			// 子画面が閉じていればtrue
			sub = accessResult.closed;

			if(!sub) {
				// 子画面あり
				screenName.push("<bean:write name="searchConditionForm" property="c_label16" />");
			}
		}

		return screenName;
	}

	// 子画面を閉じる
	function closeSubScreen() {
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
	// ログアウト
	function logout() {
		// ログインページに遷移 (履歴クリア。戻るボタンで戻れない)
		top.location.replace('<%=request.getContextPath() %>/Logout');
		// ウィンドウ移動
		// moveWindow();
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

	// ウィンドウを閉じる前に呼び出し
	window.onbeforeunload = function(e) {
		<%-- 閉じるボタンでのセッション切断が不要となったため、コメントアウト --%>
		// 子画面を閉じる
		// closeSubScreen();
		// ログアウト
		// logout();
		// e.returnValue = "本当にページを閉じますか？";
	}
	// 2019.09.25 yamamoto add end
	// 2020.03.13 yamamoto add start
	// 図番指定順のON/OFFチェック
	function isOrderDrwgNo() {
		var checkElement = document.getElementById('orderDrwgNoHidden');
		var ischecked = document.forms[0].orderDrwgNocheckbox.checked;
		if (ischecked == true) {
			// チェックが入っていたら無効化
			disableSearchForm();
			checkElement.value = true
		} else {
			// チェックが入って無いなら有効化
			enableSearchForm();
			checkElement.value = false
		}
	}
	 // 最新追番のみ表示ON/OFFチェック
	function isOnlyNewest() {
		var checkElement = document.getElementById('onlyNewestHidden');
		var ischecked = document.forms[0].onlyNewestcheckbox.checked;
		if (ischecked == true) {
			checkElement.value = true
		} else {
			checkElement.value = false
		}
	}

	// 検索条件を無効化する
	function disableSearchForm() {
		var currentForm = document.forms[0];
		for (var i = 1; i <= ${searchConditionForm.getSearchSelColNum()}; i++) {
			var condition = 'condition' + i;
			var conditionValue = 'conditionValue' + i;
			var sortWayButton = 'sortWayButton' + i;
			var sortOrder = 'sortOrder' + i;
			
			currentForm[condition].disabled = true;
			currentForm[conditionValue].disabled = true;
			currentForm[sortWayButton].disabled = true;
			currentForm[sortOrder].disabled = true;
		}
		// 最新追番のみ表示
		currentForm.onlyNewest.disabled = true;
		document.getElementById('onlyNewestcheckbox').disabled = true;
		// 全ての属性条件を
		currentForm.eachCondition[0].disabled = true;
		currentForm.eachCondition[1].disabled = true;
	}

	// 検索条件を有効化する
	function enableSearchForm(){
		var currentForm = document.forms[0];
		for (var i = 1; i <= ${searchConditionForm.getSearchSelColNum()}; i++) {
			var condition = 'condition' + i;
			var conditionValue = 'conditionValue' + i;
			var sortWayButton = 'sortWayButton' + i;
			var sortOrder = 'sortOrder' + i;
			
			currentForm[condition].disabled = false;
			currentForm[conditionValue].disabled = false;
			currentForm[sortWayButton].disabled = false;
			currentForm[sortOrder].disabled = false;
		}
		// 最新追番のみ表示
		currentForm.onlyNewest.disabled = false;
		document.getElementById('onlyNewestcheckbox').disabled = false;
		// 全ての属性条件を
		currentForm.eachCondition[0].disabled = false;
		currentForm.eachCondition[1].disabled = false;
	}

	// ワイルドカード有無確認
	function isWildCard(obj) {
		var ret = false; // 結果
		var str = obj.value; // 入力値
		for(var i=0; i<str.length; i++) {
			var tmpValue = str.substr(i,1);
			if(tmpValue == "*") {
				// ワイルドカード有り
				ret = true;
				break;
			}
		}
		return ret;
	}
// 2020.03.13 yamamoto add end
</script>
</head>
<body style="background-color: #CCCCCC; margin: 0;overflow-y: hidden;overflow-x: auto;" onload="onLoad()">
	<form action="<%=request.getContextPath() %>/searchCondition" method="post" >
		<input type="hidden" name="act" />

		<%-- 処理を振り分けるための隠し属性 --%>
		<%-- 検索結果の表示属性。別フレームの値をJavaScriptでコピーする --%>
		<c:forEach begin="1" end="${searchConditionForm.getViewSelColNum()}" var="index">
			<input type="hidden"name="dispAttr${index}" value="${searchConditionForm.dispAttrList[index - 1]}"/>
		</c:forEach>
		<!-- 	<html:hidden property="language" /> -->

		<!-- ================= 職番などの表示エリア ======================= -->
		<table border="0" cellspacing="0" cellpadding="0"  style = "width:100%">
			<tr>
				<td >
					<table border="0" >
						<tr>
							<td nowrap="nowrap" bgcolor="#EEEEEE"><span class="normal10"><b>${searchConditionForm.c_label1}</b></span></td>
							<td valign="top" nowrap="nowrap">
								<table cellspacing="0" cellpadding="0">
									<tr>
										<td nowrap="nowrap">
											<table border="1">
												<tr>
													<td nowrap="nowrap">
														<span class="normal10">${searchConditionForm.c_label6}：${user.id}</span>
													</td>
													<td nowrap="nowrap">
														<span class="normal10">${searchConditionForm.c_label7}：${user.name}
														</span>
													</td>
													<td id="divisionTD">
														<div id="division" class="normal10"
															style="height: 14px;overflow:hidden">${searchConditionForm.c_label8}：${user.deptName}
														</div>
													</td>
												</tr>
											</table>
										</td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
				</td>
				<td>
					<img src="<%=request.getContextPath()%>/resources/img/DRASAPTitle.JPG" width="150" height="33" />
				</td>
				<td align="right" nowrap="nowrap">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<%-- 2019.09.25 yamamoto add. start --%>
							<%-- パスワード変更 --%>
							<td>
								<button type='button' onclick="changePassword()">
									<c:out value="${searchConditionForm.c_label10}" />
								</button>
								<a id="chgPass" type="hidden" href="<%=request.getContextPath()%>/switch.do?page=/root/changePassword.jsp" target="_chgPass" onclick="javascript:openNewWindow()"></a>
							</td>
							<%-- 2019.09.25 yamamoto add. end --%>
							<%-- 言語切替 --%>
							<td><span class="normal12" style="margin-left: 5px;">
								<!--
								<b>Language :
								<a href="javascript:changeLang('Japanese');"	style="text-decoration:underline">Japanese</a> /
								<a href="javascript:changeLang('English');"	style="text-decoration:underline">English</a></b>
								 -->
								<select name="language" onchange="changeLang('English');" style="margin-right: 10px;">
									<option value="Japanese" ${sessionScope.user.language == 'Japanese' ? 'selected' : ''}>Japanese</option>
									<option value="English" ${sessionScope.user.language == 'English' ? 'selected' : ''}>English</option>
								</select>
							</span></td>
							<%-- ログアウト --%>
							<td>
								<button type="button" onclick="logout()">
									<c:out value="${searchConditionForm.c_label11}" />
								</button>
							</td>
							<%-- 検索結果表示数 --%>
							<td nowrap="nowrap">
								<span class="normal10" style="padding-right: 5px;">
									<c:out value="${sessionScope.searchConditionForm.c_label5}" />
								</span>
							</td>
							<td nowrap="nowrap">
								<span class="normal10">
									<select name="displayCount">
										<option value="20" <c:if test="${searchConditionForm.displayCount == '20'}">selected</c:if>>20</option>
										<option value="50" <c:if test="${searchConditionForm.displayCount == '50'}">selected</c:if>>50</option>
											<option value="100" <c:if test="${searchConditionForm.displayCount == '100'}">selected</c:if>>100</option>
									</select>
									<c:choose>
										<c:when test="${sessionScope.user.language == 'Japanese'}">件</c:when>
									</c:choose>
								</span>
								<span style="position: relative; bottom: 3px;"></span>
							</td>
							<%-- Help --%>
							<td>&nbsp;&nbsp;
								<c:choose>
									<c:when test="${sessionScope.user.language == 'Japanese'}">
										<a href="${pageContext.request.contextPath}/resources/helppdf/searchHelp_jp.pdf" target="_blank">
											<span class="normal10blue" style="margin-right: 5px;"><b>HELP</b></span>
										</a>
									</c:when>
									<c:when test="${sessionScope.user.language == 'English'}">
										<a href="${pageContext.request.contextPath}/resources/helppdf/searchHelp_en.pdf" target="_blank">
										<span class="normal10blue"style="margin-right: 5px;"><b>HELP</b></span>
										</a>
									</c:when>
									<c:otherwise>
										<!-- その他処理 -->
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>

		<!--============ 検索条件と説明 ============-->
		<table border="0" cellspacing="0" cellpadding="0" class="normal12" style = "width:100%">
			<tr>
				<!--============ 検索条件 ============-->
				<td style="width: 550px;" valign="top">
				<div class="table-container" id="tableContainer">
					<table border="0" cellspacing="0" cellpadding="0" style="font-size: 12pt; margin: 0px; padding: 0px;">
						<c:forEach begin="1" end="${searchConditionForm.getSearchSelColNum()}" var="index">
							<tr>
								<td>
									<select name="condition${index}" style = "width: 141px;">
										<c:forEach items="${searchConditionForm.conditionKeyList}" var="conditionKey" varStatus="loop">
											<c:choose>
												<c:when test="${conditionKey == searchConditionForm.getCondition(index - 1)}">
													<option value="${conditionKey}" selected>${searchConditionForm.conditionNameList[loop.index]}</option>
												</c:when>
												<c:otherwise>
													<option value="${conditionKey}">${searchConditionForm.conditionNameList[loop.index]}</option>
												</c:otherwise>
											</c:choose>
										</c:forEach>
									</select>
								</td>
								<td>
									<input type="text" name="conditionValue${index}" size="40" class="conditionStr" value = "${searchConditionForm.conditionValueList[index-1]}"style="width: 353px;"/>
								</td>
								<td>
									<input type="button" name="sortWayButton${index}" value="${searchConditionForm.sortWayList[index-1]}" onclick="changeOrder(this)" style="width: 40px;" />
									<input type="hidden" name="sortWay${index}" />
								</td>
								<td>
									<select name="sortOrder${index}">
										<c:forEach items="${searchConditionForm.sortOrderKeyList}"
											var="sortOrderKey" varStatus="loop">
											<c:choose>
												<c:when test="${sortOrderKey == searchConditionForm.getSortOrder(index - 1)}">
													<option value="${sortOrderKey}" selected>${searchConditionForm.sortOrderNameList[loop.index]}</option>
												</c:when>
												<c:otherwise>
													<option value="${sortOrderKey}">${searchConditionForm.sortOrderNameList[loop.index]}</option>
												</c:otherwise>
											</c:choose>
										</c:forEach>
									</select>
								</td>
							</tr>
						</c:forEach>
					</table>
				</div>
				</td>

				<%-- 2013.06.26 yamagishi add. start --%>
				<%-- 複数図番 --%>
				<c:choose>
					<c:when test="${sessionScope.user.language == 'Japanese'}">
						<td style="width: 220px" align="left" class="normal12" valign="top">
							<table style="margin-left: 10px;" align="left" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td style="width: 40px;">
										<div style="height: 90px; text-align: right; vertical-align: top; margin-right: 5px;">
											${searchConditionForm.c_label9}
										</div>
									</td>
									<td style="width: 160px;">
										<%-- 2022.04.13 Windows Edge対応. 複数図番テキストエリアのリサイズを禁止. --%>
										<textarea name="multipleDrwgNo" cols="20" rows="7" class="conditionStr" style="height: 120px; resize:none;"></textarea>
									</td>
								</tr>
							</table>
						</td>
					</c:when>
				</c:choose>
				<c:choose>
					<c:when test="${sessionScope.user.language == 'English' }">
						<td style="width: 240px" align="left" class="normal12" valign="top">
							<table style="margin-left: 10px;" align="left" border="0"cellspacing="0" cellpadding="0">
								<tr>
									<td style="width: 65px;">
										<div style="height: 90px; text-align: right; vertical-align: top; margin-right: 5px;">
											${searchConditionForm.c_label9}
										</div>
									</td>
									<td style="width: 160px;">
										<%-- 2022.04.13 Windows Edge対応. 複数図番テキストエリアのリサイズを禁止. --%>
									    <textarea name="multipleDrwgNo" cols="20" rows="7" class="conditionStr"style="height: 120px;"></textarea>
									</td>
								</tr>
							</table>
						</td> 
					</c:when>
				</c:choose>
				<%-- 2013.06.26 yamagishi add. end --%>
				<td nowrap="nowrap" align="left" class="normal12" valign="top">
					<table align="left" border="0" cellspacing="0" cellpadding="0" class="searchbutton">
						<tr>
							<%-- 2013.06.27 yamagishi modified.
							<td align="center"><html:checkbox property="onlyNewest" /> <bean:write name="searchConditionForm"--%>
							<td align="left" nowrap="nowrap">
								<%-- 最新追番のみ表示 --%>
								<input type="hidden" id="onlyNewestHidden" name="onlyNewest" value="false"/>
								<input type="checkbox" id="onlyNewestcheckbox" value="true"  ${searchConditionForm.isOnlyNewest() ? 'checked' : ''} onclick="isOnlyNewest()"/>
								${searchConditionForm.c_label2}
								<br />
								<%-- 図番指定順 --%>
								<input type="hidden" id="orderDrwgNoHidden" name="orderDrwgNo" value="false"/>
								<input type="checkbox" id="orderDrwgNocheckbox" value="true" ${searchConditionForm.isOrderDrwgNo() ? 'checked' : ''} onclick="isOrderDrwgNo()"/>
								${searchConditionForm.c_label17} 
								<br />
								<br />
								<!-- 全ての属性条件を -->
								${searchConditionForm.c_label3}
								<br />
								&emsp; <% // ラジオボタンの前に空白を入れる %>
								<input type="radio"name="eachCondition" value="OR" ${searchConditionForm.eachCondition == "OR"  ? 'checked ' : ''}/> OR
								<input type="radio"name="eachCondition" value="AND" ${searchConditionForm.eachCondition == "AND"  ? 'checked ' : ''}/> AND <br /> <br /> 
								 <%
								 // ユーザーに検索権限があるか? なければ検索開始ボタンをロックする
								 User user = (User) session.getAttribute("user");
								 boolean hasAuth = (user.getMaxAclValue().compareTo("1") >= 0);
								 %>
								<%-- 検索開始ボタン --%>
								&emsp; <% // ボタンの前に空白を入れる %> 
								<button type="submit" onclick="event.preventDefault(); doSearch();" <%if (!hasAuth) {%> disabled="disabled" <%}%> style="font-size: 12pt; font-weight: bold;">
									${searchConditionForm.c_label4}
								</button>
							</td>
						</tr>
					</table>
				</td>
				<td align="right" valign="top" nowrap="nowrap">
					<table border="0" cellspacing="0" cellpadding="0"
						class="menubutton">
						<tr>
							<td>
								<%-- admin_flag '1'、'2'もしくは DWG_REG_REQ_FLAG '1'
								のユーザのみボタンを表示する--%>
<%
User me = (User) session.getAttribute("user");
if (me.isAdmin() || me.isdwgRegReqFlag()) {
%>
								<%-- 原図庫作業依頼 --%>
								<button type="button" onclick="openSubScreen('2')" style="height: 25px; width: 100%;">
									${searchConditionForm.c_label12}
								</button>
							</td>
						</tr>
						<tr>
							<td>
								<%-- 原図庫作業依頼詳細 --%>
								<button type="button" onclick="openSubScreen('3')" style="height: 25px; width: 100%;">
									${searchConditionForm.c_label13}
								</button>
<%
}
%>
							</td>
						</tr>
						<tr>
							<td>
								<%-- admin_flag '1'、'2'もしくは REPRO_USER_FLAG '1' のユーザのみボタンを表示する--%>
<%
if (me.isAdmin() || me.isReproUser()) {
%>
								<%-- 原図庫作業リスト --%>
								<button type="button" onclick="openSubScreen('4')" style="height: 25px; width: 100%;">
									${searchConditionForm.c_label14}
								</button>
<%
}
%>
							</td>
						</tr>
						<tr>
							<td>
								<%-- admin_flag '1'、'2'もしくは ACL_BATCH_UPDATE_FLAG '2'のユーザのみボタンを表示する--%>
<%
if (me.isAdmin() || me.isAclBatchUpdateFlag()) {
%>
								<%-- アクセスレベル一括更新 --%>
								<button type="button" onclick="openSubScreen('5')"  style="height: 25px; width: 100%;">
									${searchConditionForm.c_label15}
								</button>
							</td>
						</tr>
						<tr>
							<td>
								<%-- アクセスレベル更新結果 --%>
								<button type="button" onclick="openSubScreen('6')" style="height: 25px; width: 100%;">
									${searchConditionForm.c_label16}
								</button>
<%
}
%>
							</td>
						</tr>
					</table>
				</td>
				<%-- searchHelpMsg表示 --%>
				<td nowrap="nowrap" class="slideBar" onmouseover="show_tool_tip('toolotip', true)">《</td>
			</tr>
		</table>
		<%-- searchHelpMsg表示領域定義 --%>
		<iframe src="" frameborder="0" scrolling="no" id="toolotip" name="toolotip" class="tooltip"style="display: none;"></iframe>
		<script>
			document.getElementById("toolotip").addEventListener("mouseover", function() {
				show_tool_tip('toolotip', true);
			});
			
			document.getElementById("toolotip").addEventListener("mouseout", function() {
				show_tool_tip('toolotip', false);
			});
		</script>

		<script>
			function adjustTableContainerHeight() {
				const windowHeight = window.innerHeight;
				const tableContainer = document.getElementById('tableContainer');
				const height = windowHeight - 50;
				tableContainer.style.height =height+'px';
			}

			window.addEventListener('resize', adjustTableContainerHeight);
			adjustTableContainerHeight();
		</script>

		<div id="toolotipContents" style="visibility: hidden;">
			<table bgcolor="#CCCCCC" cellspacing="0" cellpadding="0">
				<tr>
					<td class="slideBar">《</td>
					<td bgcolor="#EEEEEE"valign="top" style=" padding-left:10px;margin: 0;z-index: 100; white-space: normal;" width="420px">
						<textarea name="searchConditionForm_searchHelpMsg" rows="6" readonly="readonly" style="background-color: #EEEEEE; border: none; width: 100%; height: 120px; overflow:visible; font-size: 10pt;text-align: left;">${searchConditionForm.searchHelpMsg}</textarea>
					</td>
				</tr>
			</table>
		</div>
	</form>
</body>
</html>
