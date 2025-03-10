﻿<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="tyk.drasap.common.*"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%-- ログイン情報の確認 --%>
<logic:notPresent name="user" scope="session">
	<logic:redirect forward="timeout" />
</logic:notPresent>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN" "http://www.w3.org/tr/xhtml1/DTD/xhtml1-frameset.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
<title>Drawing Search and Print System [図面検索]</title>
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Cache-Control" content="no-cache" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/<bean:write name="default_css" scope="session" />" />
<style type="text/css">
	.tooltip {
	    width:460px;
		height:120px;
	    text-align:center;
	    padding:0px;
	    margin:0px;
	    position:absolute;
	    cursor:default;
	    top:35px;
	    right:-440px;
/*	    font-size:16pt;*/
	    visibility: visible;
	    z-index:100;
	}
	img {
		background-color:#CCCCCC;
	    position:relative;
	    left:-10px;
		border:none;
/*		width:20px;*/
	    padding:0px;
	    margin:0px;
	}
	.slideBar {
		width:15px;
		height:120px;
		padding:0px;
		margin:0px;
		background-color:#CCCCCC;
		border-width:2px;
		border-left-color:#EEEEEE;
		border-top-color:#EEEEEE;
		border-right-color:#AAAAAA;
		border-bottom-color:#AAAAAA;
/*		border-color:#CCCCCC;*/
/*		border-style:ridge;*/
		border-style:solid;
	}
	.menubutton{
    	margin-right : 30px;
    	vertical-align : top;
	}
	.searchbutton{
    	margin-left : 5px;
    	vertical-align : top;
	}
	</style>
<script type="text/javascript">
	<!--
	document.onkeydown = keys;
	function keys(){
		switch (event.keyCode ){
			case 116: // F5
				event.keyCode = 0;
				return false;
				break;
		}
	}
	function onLoad(){
		onInitFocus();
//		var divisionTD = document.getElementById("divisionTD");
//		var division = document.getElementById("division");
//		division.offsetWidth = divisionTD.clientWidth;
		createToolTip();
		loadResultFrame();
		// 図番指定順のチェック
		isOrderDrwgNo();
	}
	// 昇順、降順をサイクリックに切り替える
	function changeOrder(obj){
		var ascStr;
		var descStr;
		<logic:equal value="Japanese" name="user" property="language" scope="session">
			ascStr = "昇順";
			descStr = "降順";
		</logic:equal>
		<logic:notEqual value="Japanese" name="user" property="language" scope="session">
			ascStr = "Asc";
			descStr = "Desc";
		</logic:notEqual>
		//alert(obj.value);
		if(obj.value==ascStr){
			obj.value=descStr;
		} else if(obj.value==descStr) {
			obj.value="　　　";
		} else {
			obj.value=ascStr;
		}
		//parent.location.href = "./aclvChange.html";
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
		document.forms[0].sortWay1.value=document.forms[0].sortWayButton1.value;// ボタンの値を隠し属性コピー
		document.forms[0].sortWay2.value=document.forms[0].sortWayButton2.value;
		document.forms[0].sortWay3.value=document.forms[0].sortWayButton3.value;
		document.forms[0].sortWay4.value=document.forms[0].sortWayButton4.value;
		document.forms[0].sortWay5.value=document.forms[0].sortWayButton5.value;
		// 表示属性を検索結果の画面から取得しsetする
		if(parent.result.result_head != null){// nullチェックを追加
			document.forms[0].dispAttr1.value=parent.result.result_head.document.forms[0].dispAttr1.value;
			document.forms[0].dispAttr2.value=parent.result.result_head.document.forms[0].dispAttr2.value;
			document.forms[0].dispAttr3.value=parent.result.result_head.document.forms[0].dispAttr3.value;
			document.forms[0].dispAttr4.value=parent.result.result_head.document.forms[0].dispAttr4.value;
			document.forms[0].dispAttr5.value=parent.result.result_head.document.forms[0].dispAttr5.value;
			document.forms[0].dispAttr6.value=parent.result.result_head.document.forms[0].dispAttr6.value;
		}
		// 図番指定順にチェックありの場合
		if(document.forms[0].orderDrwgNo.checked) {
			// ワイルドカードが使用されているか？
			if(isWildCard(document.forms[0].multipleDrwgNo)) {
				alert("<bean:write name="searchConditionForm" property="listOrderErrMsg" />");
				return false;
			}
		}
		document.forms[0].act.value="search";// 隠し属性actに'search'をセット
		document.forms[0].target="result";// targetは'result'

		document.forms[0].submit();
		screenLockbySearch();

	}
	// ヘルプを表示する
	function help(){
		var targetName = '_help';//別の画面を開く
		var WO1;
		var w = screen.availWidth - 100;
		var h = screen.availHeight - 100;

		WO1=window.open("<%=request.getContextPath() %>/search/searchHelp.jsp", targetName,
			//"toolbar=no,resizable=yes,width=" + w + ",height=" + h);
			'toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes,width=' + w + ',height=' + h);
		WO1.window.moveTo(50,50);//画面の位置指定
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
		document.searchConditionForm.condition1Value.focus();
	}
	function loadResultFrame() {
		<%if("multipreview".equals(request.getParameter("act"))){  %>
			parent.result.location.href = "switch.do?prefix=/search&page=/resultPre.do?task=multipreview";
		<% } %>
		}
	// 説明文スライド処理
	var rightVal=-440;
	var intHide = null;
	var intShow = null;
	var speed=20;
	function show_tool_tip(item_name, flg) {
		if (flg) {
			document.getElementById(item_name).style.visibility='visible';
			if (intHide != null) clearInterval(intHide);
			intHide = null;

			intShow=setInterval("slideToolTip('"+item_name+"', 'left')",10);
		} else {
//			alert("tight start");
			if (intShow != null) clearInterval(intShow);
			intShow = null;

			// 2019.10.02 yamamoto add
			// IEの互換性無しの場合、Tooltipが画面上に残り続けるため、非表示とする
			// intHide=setInterval("slideToolTip('"+item_name+"', 'right')",10);
			document.getElementById('toolotip').style.visibility = "hidden";
		}
	}
	function slideToolTip(tooltip, flg)
	{
		if (flg == "right") {
			if (rightVal>-440) {
				rightVal=rightVal-speed;
				document.getElementById(tooltip).style.right=rightVal;
			} else {
				if (intShow != null) clearInterval(intShow);
				intShow = null;
			}
		} else if (flg == "left") {
			if (rightVal<0) {
				rightVal=rightVal+speed;
				window.status=rightVal;
				document.getElementById(tooltip).style.right=rightVal;
			} else {
				if (intHide != null) clearInterval(intHide);
				intHide = null;
			}
		}
	}
	function createToolTip(){
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
		var w = screen.availWidth/2;
		var h = screen.availHeight/2;
		var targetUrl = null;

//		if (isIE() && isCompatibilityDisplay()) {
			// IEの互換性表示の場合、画面ロック処理がうまく動作しないため、
			// showModalDialogを使用する
<%--			targetUrl = "<%=request.getContextPath() %>/root/changePassword.jsp"; --%>
//			window.showModalDialog(targetUrl, targetName, 'center=yes;status=no;scroll=yes;resizable=yes;dialogWidth=' + w + 'px;dialogHeight=' + h + 'px');
//		} else {
			// その他の場合
			targetUrl = 'about:blank'
			WO1=window.open(targetUrl, targetName,
				'toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes,width=' + w + ',height=' + h);
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
//		}
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
		var w = screen.availWidth;
		var h = screen.availHeight-50;

		var targetName = null;
		var targetUrl = null;
		var option = 'toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes,width='
					+ w + ',height=' + h

		if(fn == "2") {
			// 原図庫作業依頼
			targetName = '_drasap_request';
			targetUrl = '/switch.do?prefix=/genzu_irai&page=/req.do';

			orgReq = window.open("<%=request.getContextPath() %>" + targetUrl, targetName, option, "sub");
			orgReq.window.moveTo(0,0);//画面の位置指定
			orgReq.focus();
		}
		else if(fn == "3") {
			// 原図庫作業依頼詳細
			targetName = '_drasap_request_ref';
			targetUrl = '/switch.do?prefix=/genzu_irai&page=/requestt_ref.jsp';

			orgDetail = window.open("<%=request.getContextPath() %>" + targetUrl, targetName, option, "sub");
			orgDetail.window.moveTo(0,0);//画面の位置指定
			orgDetail.focus();
		}
		else if(fn == "4") {
			// 原図庫作業依頼リスト
			targetName = '_drasap_request_list';
			targetUrl = '/switch.do?prefix=/genzu_irai&page=/requestt_list.jsp';

			orgList = window.open("<%=request.getContextPath() %>" + targetUrl, targetName, option, "sub");
			orgList.window.moveTo(0,0);//画面の位置指定
			orgList.focus();
		}
		else if(fn == "5") {
			// アクセスレベル一括更新
			targetName = '_drasap_acl_batch_update';
			targetUrl = '/switch.do?prefix=&page=/system/accessLevelBatchUpdate.jsp';

			accessUpdate = window.open("<%=request.getContextPath() %>" + targetUrl, targetName, option, "sub");
			accessUpdate.window.moveTo(0,0);//画面の位置指定
			accessUpdate.focus();
		}
		else if(fn == "6") {
			// アクセスレベル更新結果
			targetName = '_drasap_acl_updated_result';
			targetUrl = '/switch.do?prefix=&page=/system/accessLevelUpdatedResult.jsp';

			accessResult = window.open("<%=request.getContextPath() %>" + targetUrl, targetName, option, "sub");
			accessResult.window.moveTo(0,0);//画面の位置指定
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
		top.location.replace('<%=request.getContextPath() %>/root/login.jsp');
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
		<%-- 閉じるボタンでのセッション切断が不要となったため、
		コメントアウト --%>
		// 子画面を閉じる
		// closeSubScreen();
		// ログアウト
		// logout();
		// e.returnValue = "本当にページを閉じますか？";
	}
// 2019.09.25 yamamoto add end
// 2020.03.13 yamamoto add start
	// 図番指定順のON/OFFチェック
	function isOrderDrwgNo(){
		var ischecked = document.forms[0].orderDrwgNo.checked;
		if (ischecked == true) {
			// チェックが入っていたら無効化
			disableSearchForm();
		} else {
			// チェックが入って無いなら有効化
			enableSearchForm();
		}
	}

	// 検索条件を無効化する
	function disableSearchForm(){
		// 検索条件1
		document.forms[0].condition1.disabled = true;
		document.forms[0].condition1Value.disabled = true;
		document.forms[0].sortWayButton1.disabled = true;
		document.forms[0].sortOrder1.disabled = true;
		// 検索条件2
		document.forms[0].condition2.disabled = true;
		document.forms[0].condition2Value.disabled = true;
		document.forms[0].sortWayButton2.disabled = true;
		document.forms[0].sortOrder2.disabled = true;
		// 検索条件3
		document.forms[0].condition3.disabled = true;
		document.forms[0].condition3Value.disabled = true;
		document.forms[0].sortWayButton3.disabled = true;
		document.forms[0].sortOrder3.disabled = true;
		// 検索条件4
		document.forms[0].condition4.disabled = true;
		document.forms[0].condition4Value.disabled = true;
		document.forms[0].sortWayButton4.disabled = true;
		document.forms[0].sortOrder4.disabled = true;
		// 検索条件5
		document.forms[0].condition5.disabled = true;
		document.forms[0].condition5Value.disabled = true;
		document.forms[0].sortWayButton5.disabled = true;
		document.forms[0].sortOrder5.disabled = true;
		// 最新追番のみ表示
		document.forms[0].onlyNewest.disabled = true;
		// 全ての属性条件を
		document.forms[0].eachCondition[0].disabled = true;
		document.forms[0].eachCondition[1].disabled = true;

	}

	// 検索条件を有効化する
	function enableSearchForm(){
		// 検索条件1
		document.forms[0].condition1.disabled = false;
		document.forms[0].condition1Value.disabled = false;
		document.forms[0].sortWayButton1.disabled = false;
		document.forms[0].sortOrder1.disabled = false;
		// 検索条件2
		document.forms[0].condition2.disabled = false;
		document.forms[0].condition2Value.disabled = false;
		document.forms[0].sortWayButton2.disabled = false;
		document.forms[0].sortOrder2.disabled = false;
		// 検索条件3
		document.forms[0].condition3.disabled = false;
		document.forms[0].condition3Value.disabled = false;
		document.forms[0].sortWayButton3.disabled = false;
		document.forms[0].sortOrder3.disabled = false;
		// 検索条件4
		document.forms[0].condition4.disabled = false;
		document.forms[0].condition4Value.disabled = false;
		document.forms[0].sortWayButton4.disabled = false;
		document.forms[0].sortOrder4.disabled = false;
		// 検索条件5
		document.forms[0].condition5.disabled = false;
		document.forms[0].condition5Value.disabled = false;
		document.forms[0].sortWayButton5.disabled = false;
		document.forms[0].sortOrder5.disabled = false;
		// 最新追番のみ表示
		document.forms[0].onlyNewest.disabled = false;
		// 全ての属性条件を
		document.forms[0].eachCondition[0].disabled = false;
		document.forms[0].eachCondition[1].disabled = false;
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
	//-->
	</script>
</head>
<body style="background-color: #CCCCCC; margin: 0; overflow: hidden;" onload="onLoad()">
<html:form action="/searchCondition">
	<html:hidden property="act" />
	<%-- 処理を振り分けるための隠し属性 --%>
	<html:hidden property="dispAttr1" />
	<%-- 検索結果の表示属性。別フレームの値をJavaScriptでコピーする --%>
	<html:hidden property="dispAttr2" />
	<html:hidden property="dispAttr3" />
	<html:hidden property="dispAttr4" />
	<html:hidden property="dispAttr5" />
	<html:hidden property="dispAttr6" />
	<!-- 	<html:hidden property="language" /> -->


	<!-- ================= 職番などの表示エリア ======================= -->
	<table border="0" cellspacing="0" cellpadding="0" width="100%">
		<tr>
			<td>
			<table border="0">
				<tr>
					<td nowrap="nowrap" bgcolor="#EEEEEE"><span class="normal10"><b><bean:write name="searchConditionForm" property="c_label1" /></b></span></td>
					<td valign="top" nowrap="nowrap">
					<table cellspacing="0" cellpadding="0">
						<tr>
							<td nowrap="nowrap">
							<table border="1">
								<tr>
									<td nowrap="nowrap"><span class="normal10"><bean:write name="searchConditionForm" property="c_label6" />：<bean:write
										name="user" property="id" /></span></td>
									<td nowrap="nowrap"><span class="normal10"><bean:write name="searchConditionForm" property="c_label7" />：<bean:write
										name="user" property="name" /></span></td>
									<td id="divisionTD">
									<div id="division" class="normal10" style="height:14px;overflow:hidden;"><bean:write name="searchConditionForm"
										property="c_label8" />：<bean:write name="user" property="deptName" /></div>
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
			<td><img src="<%=request.getContextPath() %>/img/DRASAPTitle.JPG" width="150" height="33" /></td>
			<td align="right" nowrap="nowrap">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<%-- 2019.09.25 yamamoto add. start --%>
					<%-- パスワード変更 --%>
					<td>
						<html:button property="chgPasswd" onclick="changePassword()">
							<bean:write name="searchConditionForm" property="c_label10" />
						</html:button>
						<a id="chgPass" type="hidden" href="<%=request.getContextPath() %>/root/changePassword.jsp"
						target="_chgPass" onclick="javascript:openNewWindow()"></a>
					</td>
					<%-- 2019.09.25 yamamoto add. end --%>
					<%-- 言語切替 --%>
					<td><span class="normal12" style="margin-left:5px;"> <!--
					<b>Language :
					<a href="javascript:changeLang('Japanese');"	style="text-decoration:underline">Japanese</a> /
					<a href="javascript:changeLang('English');"	style="text-decoration:underline">English</a></b>
					 -->
					<html:select property="language" onchange="javascript:changeLang('English');" style="margin-right:10px;">
						<html:option value="Japanese">Japanese</html:option>
						<html:option value="English">English</html:option>
					</html:select> </span></td>
					<%-- ログアウト --%>
					<td>
						<html:button property="Logout" onclick="logout()">
							<bean:write name="searchConditionForm" property="c_label11" />
						</html:button>
					</td>
					<%-- 検索結果表示数 --%>
					<td nowrap="nowrap">
						<span class="normal10" style="padding-right:5px;">
							<bean:write name="searchConditionForm" property="c_label5" scope="session" />
						</span>
					</td>
					<td nowrap="nowrap">
						<span class="normal10">
							<html:select property="displayCount">
								<html:option value="20">20</html:option>
								<html:option value="50">50</html:option>
								<html:option value="100">100</html:option>
							</html:select>
							<logic:equal value="Japanese" name="user" property="language" scope="session">
								件
							</logic:equal>
						</span>
						<span style="position:relative;bottom:3px;"></span>
					</td>
					<%-- Help --%>
					<td>&nbsp;&nbsp;
						<logic:equal value="Japanese" name="user" property="language" scope="session">
							<a href="<%=request.getContextPath() %>/helppdf/searchHelp_jp.pdf" target="_blank">
							<span class="normal10blue" style="margin-right:5px;"> <b>HELP</b></span></a>
						</logic:equal>
						<logic:equal value="English" name="user" property="language" scope="session">
							<a href="<%=request.getContextPath() %>/helppdf/searchHelp_en.pdf" target="_blank">
							<span class="normal10blue" style="margin-right:5px;"> <b>HELP</b></span></a>
						</logic:equal>
					</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>


	<!--============ 検索条件と説明 ============-->
	<table border="0" cellspacing="0" cellpadding="0" width="100%" class="normal12">
		<tr>
			<!--============ 検索条件 ============-->
			<td style="width:500px;">
			<table border="0" cellspacing="0" cellpadding="0" style="font-size:12pt;margin:0px;padding:0px;">
				<tr>
					<td><html:select property="condition1">
						<html:options labelName="searchConditionForm" labelProperty="conditionNameList" name="searchConditionForm"
							property="conditionKeyList" />
					</html:select></td>
					<td><html:text property="condition1Value" size="40" styleClass="conditionStr" /></td>
					<td><input type="button" name="sortWayButton1" value="　　　" onclick="changeOrder(this)" style="width:60px;" /> <html:hidden
						property="sortWay1" /></td>
					<td><html:select property="sortOrder1">
						<html:options labelName="searchConditionForm" labelProperty="sortOrderNameList" name="searchConditionForm"
							property="sortOrderKeyList" />
					</html:select></td>
				</tr>
				<tr>
					<td><html:select property="condition2">
						<html:options labelName="searchConditionForm" labelProperty="conditionNameList" name="searchConditionForm"
							property="conditionKeyList" />
					</html:select></td>
					<td><html:text property="condition2Value" size="40" styleClass="conditionStr" /></td>
					<td><input type="button" name="sortWayButton2" value="　　　" onclick="changeOrder(this)" style="width:60px;" /> <html:hidden
						property="sortWay2" /></td>
					<td><html:select property="sortOrder2">
						<html:options labelName="searchConditionForm" labelProperty="sortOrderNameList" name="searchConditionForm"
							property="sortOrderKeyList" />
					</html:select></td>
				</tr>
				<tr>
					<td><html:select property="condition3">
						<html:options labelName="searchConditionForm" labelProperty="conditionNameList" name="searchConditionForm"
							property="conditionKeyList" />
					</html:select></td>
					<td><html:text property="condition3Value" size="40" styleClass="conditionStr" /></td>
					<td><input type="button" name="sortWayButton3" value="　　　" onclick="changeOrder(this)" style="width:60px;" /> <html:hidden
						property="sortWay3" /></td>
					<td><html:select property="sortOrder3">
						<html:options labelName="searchConditionForm" labelProperty="sortOrderNameList" name="searchConditionForm"
							property="sortOrderKeyList" />
					</html:select></td>
				</tr>
				<tr>
					<td><html:select property="condition4">
						<html:options labelName="searchConditionForm" labelProperty="conditionNameList" name="searchConditionForm"
							property="conditionKeyList" />
					</html:select></td>
					<td><html:text property="condition4Value" size="40" styleClass="conditionStr" /></td>
					<td><input type="button" name="sortWayButton4" value="　　　" onclick="changeOrder(this)" style="width:60px;" /> <html:hidden
						property="sortWay4" /></td>
					<td><html:select property="sortOrder4">
						<html:options labelName="searchConditionForm" labelProperty="sortOrderNameList" name="searchConditionForm"
							property="sortOrderKeyList" />
					</html:select></td>
				</tr>
				<tr>
					<td><html:select property="condition5">
						<html:options labelName="searchConditionForm" labelProperty="conditionNameList" name="searchConditionForm"
							property="conditionKeyList" />
					</html:select></td>
					<td><html:text property="condition5Value" size="40" styleClass="conditionStr" /></td>
					<td><input type="button" name="sortWayButton5" value="　　　" onclick="changeOrder(this)" style="width:60px;" /> <html:hidden
						property="sortWay5" /></td>
					<td><html:select property="sortOrder5">
						<html:options labelName="searchConditionForm" labelProperty="sortOrderNameList" name="searchConditionForm"
							property="sortOrderKeyList" />
					</html:select></td>
				</tr>
			</table>
			</td>
			<%-- 2013.06.26 yamagishi add. start --%>
			<%-- 複数図番 --%>
			<logic:equal value="Japanese" name="user" property="language" scope="session">
			<td style="width: 220px" align="left" class="normal12">
				<table style="margin-left: 10px;" align="left" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td style="width: 40px;">
						<div style="height: 90px; text-align: right; vertical-align: top; margin-right: 5px;">
							<bean:write name="searchConditionForm" property="c_label9" />
						</div>
					</td>
					<td style="width: 160px;">
						<%-- 2022.04.13 Windows Edge対応. 複数図番テキストエリアのリサイズを禁止. --%>
						<%--<html:textarea property="multipleDrwgNo" cols="20" rows="7" styleClass="conditionStr" style="height:120px;" />--%>
						<html:textarea property="multipleDrwgNo" cols="20" rows="7" styleClass="conditionStr" style="height:120px;resize: none;" />
					</td>
				</tr>
				</table>
			</td>
			</logic:equal>
			<logic:equal value="English" name="user" property="language" scope="session">
			<td style="width: 240px" align="left" class="normal12">
				<table style="margin-left: 10px;" align="left" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td style="width: 65px;">
						<div style="height: 90px; text-align: right; vertical-align: top; margin-right: 5px;">
							<bean:write name="searchConditionForm" property="c_label9" />
						</div>
					</td>
					<td style="width: 160px;">
						<%-- 2022.04.13 Windows Edge対応. 複数図番テキストエリアのリサイズを禁止. --%>
						<%--<html:textarea property="multipleDrwgNo" cols="20" rows="7" styleClass="conditionStr" style="height:120px;" /> --%>
						<html:textarea property="multipleDrwgNo" cols="20" rows="7" styleClass="conditionStr" style="height:120px;resize: none;" />
					</td>
				</tr>
				</table>
			</td>
			</logic:equal>
			<%-- 2013.06.26 yamagishi add. end --%>
			<td valign="bottom" nowrap="nowrap" align="left" class="normal12">
			<table align="left" border="0" cellspacing="0" cellpadding="0" class="searchbutton">
				<tr>
					<td align="left" nowrap="nowrap">
						<%-- 最新追番のみ表示 --%>
						<html:checkbox property="onlyNewest" />
						<bean:write name="searchConditionForm" property="c_label2" />
						<br />
						<%-- 図番指定順 --%>
						<!--  <input type="checkbox" name="orderDrwgNo" value="on" onclick="isOrderDrwgNo()"/>  -->
						<html:checkbox property="orderDrwgNo" onclick="isOrderDrwgNo()" />
						<bean:write name="searchConditionForm" property="c_label17" />
						<br /><br />
						<!-- 全ての属性条件を -->
						<bean:write name="searchConditionForm" property="c_label3" />
						<br />
						&emsp; <% // ラジオボタンの前に空白を入れる %>
						<html:radio property="eachCondition" value="OR" />OR
						<html:radio property="eachCondition" value="AND" />AND
						<br /><br />
						<%// ユーザーに検索権限があるか? なければ検索開始ボタンをロックする
						User user = (User) session.getAttribute("user");
						boolean hasAuth = (user.getMaxAclValue().compareTo("1") >= 0);
						%>
						<%-- 検索開始ボタン --%>
						&emsp; <% // ボタンの前に空白を入れる %>
						<html:submit onclick="return doSearch()" disabled="<%=!hasAuth%>" style="font-size:12pt;font-weight:bold">
							<bean:write name="searchConditionForm" property="c_label4" />
						</html:submit>
					</td>
				</tr>
			</table>
			</td>
			<td align="right" valign="top" nowrap="nowrap">
				<table border="0" cellspacing="0" cellpadding="0" class="menubutton">
					<tr>
						<td>
						<%-- admin_flag '1'、'2'もしくは DWG_REG_REQ_FLAG '1'
						のユーザのみボタンを表示する--%>
						<% User me = (User) session.getAttribute("user");
						if (me.isAdmin() || me.isdwgRegReqFlag()) { %>
							<%-- 原図庫作業依頼 --%>
							<html:button property="orgReq" onclick="openSubScreen('2')" style="height:25px;width:100%;">
								<bean:write name="searchConditionForm" property="c_label12" />
							</html:button>
						</td>
					</tr>
					<tr>
						<td>
							<%-- 原図庫作業依頼詳細 --%>
							<html:button property="orgDetail" onclick="openSubScreen('3')" style="height:25px;width:100%;">
								<bean:write name="searchConditionForm" property="c_label13" />
							</html:button>
						<% } %>
						</td>
					</tr>
					<tr>
						<td>
						<%-- admin_flag '1'、'2'もしくは REPRO_USER_FLAG '1'
						のユーザのみボタンを表示する--%>
						<% if (me.isAdmin() || me.isReproUser()) { %>
							<%-- 原図庫作業リスト --%>
							<html:button property="orgList" onclick="openSubScreen('4')" style="height:25px;width:100%;">
								<bean:write name="searchConditionForm" property="c_label14" />
							</html:button>
						<% } %>
						</td>
					</tr>
					<tr>
						<td>
						<%-- admin_flag '1'、'2'もしくは ACL_BATCH_UPDATE_FLAG '2'
						のユーザのみボタンを表示する--%>
						<% if (me.isAdmin() || me.isAclBatchUpdateFlag()) { %>
							<%-- アクセスレベル一括更新 --%>
							<html:button property="accessUpdate" onclick="openSubScreen('5')" style="height:25px;width:100%;">
								<bean:write name="searchConditionForm" property="c_label15" />
							</html:button>
						</td>
					</tr>
					<tr>
						<td>
							<%-- アクセスレベル更新結果 --%>
 							<html:button property="accessResult" onclick="openSubScreen('6')" style="height:25px;width:100%;">
								<bean:write name="searchConditionForm" property="c_label16" />
							</html:button>
						<% } %>
						</td>
					</tr>
				</table>
			</td>
			<%-- searchHelpMsg表示 --%>
  			<td nowrap="nowrap" class="slideBar" onmouseover="show_tool_tip('toolotip', true)">《</td>
		</tr>
	</table>
	<%-- searchHelpMsg表示領域定義 --%>
 	<iframe src="" frameborder="0" scrolling="no" id="toolotip" name="toolotip" class="tooltip"
		onmouseover="show_tool_tip('toolotip', true)" onmouseout="show_tool_tip('toolotip', false)" tabIndex="-1"></iframe>
	<div id="toolotipContents" style="visibility:hidden;">
	<table bgcolor="#CCCCCC" cellspacing="0" cellpadding="0">
		<tr>
			<td class="slideBar">《</td>
			<td bgcolor="#EEEEEE" valign="top" style="padding-left:10px;z-index:100;white-space:normal;" width="420">
			<html:textarea
				name="searchConditionForm" property="searchHelpMsg" rows="6" readonly="true"
				style="background-color:#EEEEEE;border-style:none;width:100%;height:120px;overflow:visible;font-size:10pt;" />
			</td>
		</tr>
	</table>
	</div>
</html:form>
</body>
</html>
