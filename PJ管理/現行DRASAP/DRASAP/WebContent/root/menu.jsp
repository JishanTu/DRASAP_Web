<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%-- ログイン情報の確認 --%>
<logic:notPresent name="user" scope="session">
	<logic:redirect forward="timeout" />
</logic:notPresent>
<html:html>
<head>
	<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<title>Drawing Search and Print System [メニュー]</title>
	<script type="text/javascript">
	<!--
		// それぞれのFunctionへ遷移する
		function showFunction(functionNo){
			// Window名とそのURL・・・functionNoに応じて変更させる
			var targetName = null;
			var targetUrl = null;
			if(functionNo == "1"){
				// 図面検索
				targetName = '_drasap_search';
				targetUrl = '/switch.do?prefix=/search&page=/searchMain.jsp';
			} else if(functionNo == "2"){
				// 原図庫作業依頼
				targetName = '_drasap_request';
				targetUrl = '/switch.do?prefix=/genzu_irai&page=/req.do';
			} else if(functionNo == "3"){
				// 原図庫作業依頼リスト
				targetName = '_drasap_request_list';
				targetUrl = '/switch.do?prefix=/genzu_irai&page=/requestt_list.jsp';
			} else if(functionNo == "4"){
				// 原図庫作業依頼詳細
				targetName = '_drasap_request_ref';
				targetUrl = '/switch.do?prefix=/genzu_irai&page=/requestt_ref.jsp';
			<%-- 2013.06.14 yamagishi add. start --%>
			} else if(functionNo == "5"){
				// アクセスレベル一括更新
				targetName = '_drasap_acl_batch_update';
				targetUrl = '/switch.do?prefix=&page=/system/accessLevelBatchUpdate.jsp';
			} else if(functionNo == "6"){
				// アクセスレベル更新結果
				targetName = '_drasap_acl_updated_result';
				targetUrl = '/switch.do?prefix=&page=/system/accessLevelUpdatedResult.jsp';
			<%-- 2013.06.14 yamagishi add. end --%>
			}
			var WO1;
			var w = screen.availWidth;
			var h = screen.availHeight-50;

			WO1=window.open("<%=request.getContextPath() %>" + targetUrl, targetName,
						//"toolbar=no,resizable=yes,width=" + w + ",height=" + h);
					'toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes,width=' + w + ',height=' + h);
			WO1.window.moveTo(0,0);//画面の位置指定
			WO1.focus();
		}
	//-->
	</script>
</head>
<body>
<bean:write name="user" property="name" scope="session" />さん　いらっしゃい<br />
<ul>
	<li><a href="javascript:showFunction('1')">図面検索</a></li>
	<li><a href="javascript:showFunction('2')">原図庫作業依頼</a>・・・原図庫へ依頼する</li>
	<li><a href="javascript:showFunction('3')">原図庫作業依頼リスト</a>・・・原図庫作業者が使用する</li>
	<li><a href="javascript:showFunction('4')">原図庫作業依頼詳細</a>・・・依頼者が確認(ポータルから遷移)</li>
	<%-- 2013.06.14 yamagishi add. --%>
	<li><a href="javascript:showFunction('5')">アクセスレベル一括更新</a></li>
	<li><a href="javascript:showFunction('6')">アクセスレベル更新結果</a></li>
</ul>
<input type="button" value="Close" onclick="self.close()" />
</body>
</html:html>
