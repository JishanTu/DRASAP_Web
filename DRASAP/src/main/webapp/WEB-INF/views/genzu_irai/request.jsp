<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%-- ログイン情報の確認 --%>
<c:if test="${empty sessionScope.user}">
<script>
	location.replace('<%=request.getContextPath()%>/timeout');
</script>
</c:if>

<c:set var="syutuList" value="${requestForm.list}" />

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<title>Drawing Search and Print System [図面登録依頼]</title>
	<style type="text/css">@import url( <%=request.getContextPath()%>/resources/css/default.css );</style>
	<script type="text/javascript">
		browserName = navigator.appName;
// 		var WO1;
// 		var w = screen.availWidth;
// 		var h = screen.availHeight;
// 		var xPos = (screen.availWidth- w)/2;
// 		var yPos = (screen.availHeight - h)/2;
// 		window.resizeTo(w, h);
// 		window.moveTo(xPos,yPos);//画面の位置指定
		if (browserName != "Netscape") focus();
	</script>
	<script type="text/javascript">
		// 依頼する
		function doIrai(){
			document.forms[0].action.value="button_irai";//依頼ボタンのアクション
			//alert(document.forms[0].action.value);
		}

		// 依頼内容を選択したら、呼び出される
		// 1) 図面出力指示の時のみ出力先のプルダウンを表示させる
		function actionOnSelectIrai(){
<%-- // 2019.10.23 yamamoto modified. start
			// 1) 図面出力指示の時のみ出力先のプルダウンを表示させる
			setSyutuContents();
			//
			setDisabledByIrai();
--%>
		}
<%--
		//依頼内容を選択して図面出力指示の時のみ出力先のプルダウンを表示させる
		function setSyutuContents(){
			jgb = document.forms[0].irai.value;//依頼内容
			jgkb = document.forms[0].hiddenSyutu.value;//出力先のhidden
			var targetName = "図面出力指示";
			var cnt = 0;
			// プルダウンの初期化
			document.forms[0].syutu.length = 0;
			document.forms[0].syutu.options[0] = new Option("                          ", "", true, false);
			// 依頼内容を取得。図面出力指示の場合のみ出力先を表示する
			if(targetName == jgb){
				document.forms[0].syutu.disabled = false;//出力先のプルダウンのロック解除
<%
				for(int i=0;i< syutuList.size(); i++ ){
					tyk.drasap.genzu_irai.RequestElement e = (tyk.drasap.genzu_irai.RequestElement)syutuList.get(i);
					String id = e.getId();//プリンタID
					String name = e.getName();//表示名
%>
					if(cnt == 0){
						document.forms[0].syutu.options[0] = new Option("                          ", "", false, false);
					}
					cnt = cnt + 1;
					document.forms[0].syutu.options[cnt] = new Option("<%= name %>", "<%= id %>", false, false);
					//出力先を表示した状態で依頼ボタンを押した時にプルダウンのデータを残すために出力先のhiddenを使う
					if(jgkb == document.forms[0].syutu.options[cnt].value){
						document.forms[0].syutu.options[cnt] = new Option("<%= name %>", "<%= id %>", true, true);
					}
<%
				}
%>
			}else{
				document.forms[0].syutu.disabled = true;//出力先のプルダウンをロックする
			}
		}
		function setDisabledByIrai(){
			jgb = document.forms[0].irai.value;//依頼内容
			// 最初に、全てを使用可にする
			// 号機は使用可
			document.forms[0].gouki1.disabled = false;
			document.forms[0].gouki2.disabled = false;
			document.forms[0].gouki3.disabled = false;
			document.forms[0].gouki4.disabled = false;
			document.forms[0].gouki5.disabled = false;
			// 原図内容は使用不可
			document.forms[0].genzu1.disabled = false;
			document.forms[0].genzu2.disabled = false;
			document.forms[0].genzu3.disabled = false;
			document.forms[0].genzu4.disabled = false;
			document.forms[0].genzu5.disabled = false;
			// 部数は使用不可
			document.forms[0].busuu1.disabled = false;
			document.forms[0].busuu2.disabled = false;
			document.forms[0].busuu3.disabled = false;
			document.forms[0].busuu4.disabled = false;
			document.forms[0].busuu5.disabled = false;
			// 縮小は使用不可
			document.forms[0].syukusyou1.disabled = false;
			document.forms[0].syukusyou2.disabled = false;
			document.forms[0].syukusyou3.disabled = false;
			document.forms[0].syukusyou4.disabled = false;
			document.forms[0].syukusyou5.disabled = false;
			// サイズは使用不可
			document.forms[0].size1.disabled = false;
			document.forms[0].size2.disabled = false;
			document.forms[0].size3.disabled = false;
			document.forms[0].size4.disabled = false;
			document.forms[0].size5.disabled = false;

			if(jgb=="図面登録依頼"){
				// 号機は使用不可
				setDisabledToText(document.forms[0].gouki1);
				setDisabledToText(document.forms[0].gouki2);
				setDisabledToText(document.forms[0].gouki3);
				setDisabledToText(document.forms[0].gouki4);
				setDisabledToText(document.forms[0].gouki5);
				// 原図内容は使用不可
				setDisabledToSelect(document.forms[0].genzu1);
				setDisabledToSelect(document.forms[0].genzu2);
				setDisabledToSelect(document.forms[0].genzu3);
				setDisabledToSelect(document.forms[0].genzu4);
				setDisabledToSelect(document.forms[0].genzu5);
				// 部数は使用不可
				setDisabledToText(document.forms[0].busuu1);
				setDisabledToText(document.forms[0].busuu2);
				setDisabledToText(document.forms[0].busuu3);
				setDisabledToText(document.forms[0].busuu4);
				setDisabledToText(document.forms[0].busuu5);
				// 縮小は使用不可
				setDisabledToSelect(document.forms[0].syukusyou1);
				setDisabledToSelect(document.forms[0].syukusyou2);
				setDisabledToSelect(document.forms[0].syukusyou3);
				setDisabledToSelect(document.forms[0].syukusyou4);
				setDisabledToSelect(document.forms[0].syukusyou5);
				// サイズは使用不可
				setDisabledToSelect(document.forms[0].size1);
				setDisabledToSelect(document.forms[0].size2);
				setDisabledToSelect(document.forms[0].size3);
				setDisabledToSelect(document.forms[0].size4);
				setDisabledToSelect(document.forms[0].size5);
			} else if(jgb=="図面出力指示"){
				// 号機は使用不可
				setDisabledToText(document.forms[0].gouki1);
				setDisabledToText(document.forms[0].gouki2);
				setDisabledToText(document.forms[0].gouki3);
				setDisabledToText(document.forms[0].gouki4);
				setDisabledToText(document.forms[0].gouki5);
				// 原図内容は使用不可
				setDisabledToSelect(document.forms[0].genzu1);
				setDisabledToSelect(document.forms[0].genzu2);
				setDisabledToSelect(document.forms[0].genzu3);
				setDisabledToSelect(document.forms[0].genzu4);
				setDisabledToSelect(document.forms[0].genzu5);
			} else if(jgb=="原図借用依頼"){
				// 部数は使用不可
				setDisabledToText(document.forms[0].busuu1);
				setDisabledToText(document.forms[0].busuu2);
				setDisabledToText(document.forms[0].busuu3);
				setDisabledToText(document.forms[0].busuu4);
				setDisabledToText(document.forms[0].busuu5);
				// 縮小は使用不可
				setDisabledToSelect(document.forms[0].syukusyou1);
				setDisabledToSelect(document.forms[0].syukusyou2);
				setDisabledToSelect(document.forms[0].syukusyou3);
				setDisabledToSelect(document.forms[0].syukusyou4);
				setDisabledToSelect(document.forms[0].syukusyou5);
				// サイズは使用不可
				setDisabledToSelect(document.forms[0].size1);
				setDisabledToSelect(document.forms[0].size2);
				setDisabledToSelect(document.forms[0].size3);
				setDisabledToSelect(document.forms[0].size4);
				setDisabledToSelect(document.forms[0].size5);
			} else if(jgb=="図面以外焼付依頼"){
				// 部数は使用不可
				setDisabledToText(document.forms[0].busuu1);
				setDisabledToText(document.forms[0].busuu2);
				setDisabledToText(document.forms[0].busuu3);
				setDisabledToText(document.forms[0].busuu4);
				setDisabledToText(document.forms[0].busuu5);
				// 縮小は使用不可
				setDisabledToSelect(document.forms[0].syukusyou1);
				setDisabledToSelect(document.forms[0].syukusyou2);
				setDisabledToSelect(document.forms[0].syukusyou3);
				setDisabledToSelect(document.forms[0].syukusyou4);
				setDisabledToSelect(document.forms[0].syukusyou5);
				// サイズは使用不可
				setDisabledToSelect(document.forms[0].size1);
				setDisabledToSelect(document.forms[0].size2);
				setDisabledToSelect(document.forms[0].size3);
				setDisabledToSelect(document.forms[0].size4);
				setDisabledToSelect(document.forms[0].size5);
			}

		}
		// 指定したテキストボックスを使用不可にして、中身もブランクにする。
		// 引数・・・テキストボックス
		function setDisabledToText(src){
			src.disabled = true;
			src.value="";
		}
		// 指定したプルダウンを使用不可にして、プルダウンの1つ目を選択した状態にする。
		// 引数・・・プルダウン
		function setDisabledToSelect(src){
			src.disabled = true;
			src.options[0].selected=true;
		}
// 2019.10.23 yamamoto modified. end --%>
		// ヘルプを表示する
		function help(){
			var targetName = '_help';//別の画面を開く
			var WO1;
			
			var w = window.outerWidth;
			var h = window.outerHeight;
			
			var screenWidth = window.screen.availWidth;
			var screenHeight = window.screen.availHeight;
			
			var left = (screenWidth - w) / 2;
			var top = (screenHeight - h) / 2;


			WO1=window.open("<%=request.getContextPath() %>/switch.do?page=/genzu_irai/requestHelp.jsp", targetName,
					//"toolbar=no,resizable=yes,width=" + w + ",height=" + h);
					'toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes,width=' + w + ',height=' + h +',top='+ top + ',left='+ left );
			//WO1.window.moveTo(50,50);//画面の位置指定
			WO1.focus();
		}
	</script>
</head>
<!-- エラーの表示 TODO-->
<c:if test="${message != null}">
	<hr color="sandybrown">
	<font color="red" size="4">
		<ul>
			<c:forEach var="msg" items="${message}">
				<li>${msg}</li>
			</c:forEach>
		</ul>
	</font>
	<hr color="sandybrown">
</c:if>
<body bgcolor="#CCCCCC" bottommargin="0" leftmargin="0" topmargin="0"
	rightmargin="0" marginheight="0" marginwidth="0"
	onload="actionOnSelectIrai()">
	<form action="<%=request.getContextPath()%>/req" method="post">
		<input type="hidden" name="action" />
		<input type="hidden" name="gouki1" />
		<input type="hidden" name="gouki2" />
		<input type="hidden" name="gouki3" />
		<input type="hidden" name="gouki4" />
		<input type="hidden" name="gouki5" />
		<input type="hidden" name="busuu1" />
		<input type="hidden" name="busuu2" />
		<input type="hidden" name="busuu3" />
		<input type="hidden" name="busuu4" />
		<input type="hidden" name="busuu5" />
		<input type="hidden" name="genzu1" />
		<input type="hidden" name="genzu2" />
		<input type="hidden" name="genzu3" />
		<input type="hidden" name="genzu4" />
		<input type="hidden" name="genzu5" />
		<input type="hidden" name="syukusyou1" />
		<input type="hidden" name="syukusyou2" />
		<input type="hidden" name="syukusyou3" />
		<input type="hidden" name="syukusyou4" />
		<input type="hidden" name="syukusyou5" />
		<input type="hidden" name="size1" />
		<input type="hidden" name="size2" />
		<input type="hidden" name="size3" />
		<input type="hidden" name="size4" />
		<input type="hidden" name="size5" />

		<!--======================= ヘッダ =======================-->
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td>
					<table border="0">
						<tr>
							<td nowrap="nowrap" bgcolor="#EEEEEE"><span class="normal18">図面登録依頼</span></td>
							<!--======================= 職番などの表示 =======================-->
							<td><table border="1">
									<tr>
										<td><span class="normal12">職番：<c:out
													value="${user.id}" /></span></td>
										<td><span class="normal12">氏名：<c:out
													value="${user.name}" /></span></td>
										<td><span class="normal12">部署名(店名)：<c:out
													value="${user.deptName}" /></span></td>
									</tr>
								</table></td>
						</tr>
					</table>
				</td>
				<td align="right">&nbsp;&nbsp;&nbsp;&nbsp;<a
					href="javascript:help()"><span class="normal10blue">HELP</span></a>&nbsp;&nbsp;&nbsp;&nbsp;
				</td>
			</tr>
		</table>
		<!--======================= 依頼内容の入力エリア =======================-->
		<table border="0" align="center">
			<%-- // 2019.10.23 yamamoto modified. start
	<!--======================= 依頼内容、出力先 =======================-->
	<tr bgcolor="#DDFFFF">
		<td><table border="0" align="left">
			<tr>
				<td><span class="normal10">依頼内容</span></td>
					<td>
						<form:select name="requestForm" property="irai" onchange="actionOnSelectIrai()" >
							<form:options labelName="requestForm" labelProperty="iraiList" name="requestForm" property="iraiList" />
			     		</form:select>
			     	</td>
				<td width="200" align="right"><span class="normal10">出力先</span></td>
				<td>
					<select name="syutu">
						<option value="">                    </option>
					</select>
					<html:hidden name="requestForm" property="hiddenSyutu"/>
				</td>
			</tr></table>
		</td>
	</tr>
// 2019.10.23 yamamoto modified. end --%>
			<tr>
				<td><input type="hidden" name="irai" value="図面登録依頼" /></td>
			</tr>
			<!--======================= 依頼の詳細 =======================-->
			<tr>
				<td><table border="0">
						<tr>
							<td align="center"><span class="normal10">No</span></td>
							<%-- // 2019.10.23 yamamoto modified. start
				<td align="center"><span class="normal10">号口・号機</span></td>
				<td align="center"><span class="normal10">原図内容</span></td>
--%>
							<td colspan="3" align="center"><span class="normal10">図番</span></td>
							<%--
				<td align="center"><span class="normal10">部数</span></td>
				<td align="center"><span class="normal10">縮小</span></td>
				<td align="center"><span class="normal10">サイズ</span></td>
// 2019.10.23 yamamoto modified. end --%>
						</tr>
						<!--	No1の行  -->
						<tr>

							<td><input type="hidden" name="hiddenNo1" /> <span
								class="normal10">1</span></td>
							<%-- // 2019.10.23 yamamoto modified. start
				<td><html:text name="requestForm" property="gouki1" maxlength="8" style="ime-mode:disabled" styleClass="normal12"/></td>
				<td>
					<form:select name="requestForm" property="genzu1" >
						<form:options labelName="requestForm" labelProperty="genzuNameList" name="requestForm" property="genzuNameList" />
			     	</form:select>
			    </td>
--%>
							<td><input type="text" name="kaisiNo1" maxlength="20"
								style="ime-mode: disabled" class="normal12" value="${sessionScope.requestForm.kaisiNo1}" /></td>
							<td><span class="normal10">～</span></td>
							<td><input type="text" name="syuuryouNo1" maxlength="20"
								style="ime-mode: disabled" class="normal12" value="${sessionScope.requestForm.syuuryouNo1}" /></td>

							<%--
				<td><html:text name="requestForm" property="busuu1" size="2"
					styleClass="normal12" maxlength="2" style="ime-mode:disabled;text-align:right;"/></td>
				<td>
					<form:select name="requestForm" property="syukusyou1" >
						<form:options labelName="requestForm" labelProperty="syukusyouList" name="requestForm" property="syukusyouList" />
			     	</form:select>
			     </td>

				<td>
					<form:select name="requestForm" property="size1" >
						<form:options labelName="requestForm" labelProperty="saizuList" name="requestForm" property="saizuList" />
			     	</form:select>
			     </td>
// 2019.10.23 yamamoto modified. end --%>
						</tr>
						<!--	No2の行  -->
						<tr>
							<td><input type="hidden" name="hiddenNo2" /> <span
								class="normal10">2</span></td>
							<%-- // 2019.10.23 yamamoto modified. start
				<td><html:text name="requestForm" property="gouki2" maxlength="8" style="ime-mode:disabled" styleClass="normal12"/></td>
				<td>
					<form:select name="requestForm" property="genzu2" >
						<form:options labelName="requestForm" labelProperty="genzuNameList" name="requestForm" property="genzuNameList" />
			     	</form:select>
			     </td>
--%>
							<td><input type="text" name="kaisiNo2" maxlength="20"
								style="ime-mode: disabled" class="normal12" value="${sessionScope.requestForm.kaisiNo2}"/></td>
							<td><span class="normal10">～</span></td>
							<td><input type="text" name="syuuryouNo2" maxlength="20"
								style="ime-mode: disabled" class="normal12" value="${sessionScope.requestForm.syuuryouNo2}"/></td>

							<%--
				<td><html:text name="requestForm" property="busuu2" size="2"
					styleClass="normal12" maxlength="2" style="ime-mode:disabled;text-align:right;"/></td>
				<td>
					<form:select name="requestForm" property="syukusyou2" >
						<form:options labelName="requestForm" labelProperty="syukusyouList" name="requestForm" property="syukusyouList" />
			     	</form:select>
			     </td>
				<td>
					<form:select name="requestForm" property="size2" >
						<form:options labelName="requestForm" labelProperty="saizuList" name="requestForm" property="saizuList" />
			     	</form:select>
			    </td>
// 2019.10.23 yamamoto modified. end --%>
						</tr>
						<!--	No3の行  -->
						<tr>
							<td><input type="hidden" name="hiddenNo3" /> <span
								class="normal10">3</span></td>
							<%-- // 2019.10.23 yamamoto modified. start
				<td><html:text name="requestForm" property="gouki3" maxlength="8" style="ime-mode:disabled" styleClass="normal12"/></td>
				<td>
					<form:select name="requestForm" property="genzu3" >
						<form:options labelName="requestForm" labelProperty="genzuNameList" name="requestForm" property="genzuNameList" />
			     	</form:select>
			    </td>
--%>
							<td><input type="text" name="kaisiNo3" maxlength="20"
								style="ime-mode: disabled" class="normal12" value="${sessionScope.requestForm.kaisiNo3}"/></td>
							<td><span class="normal10">～</span></td>
							<td><input type="text" name="syuuryouNo3" maxlength="20"
								style="ime-mode: disabled" class="normal12" value="${sessionScope.requestForm.syuuryouNo3}"/></td>

							<%--
				<td><html:text name="requestForm" property="busuu3" size="2"
					styleClass="normal12" maxlength="2" style="ime-mode:disabled;text-align:right;"/></td>
				<td>
					<form:select name="requestForm" property="syukusyou3" >
						<form:options labelName="requestForm" labelProperty="syukusyouList" name="requestForm" property="syukusyouList" />
			     	</form:select>
			    </td>
				<td>
					<form:select name="requestForm" property="size3" >
						<form:options labelName="requestForm" labelProperty="saizuList" name="requestForm" property="saizuList" />
			     	</form:select>
			     </td>
// 2019.10.23 yamamoto modified. end --%>
						</tr>
						<!--	No4の行  -->
						<tr>
							<td><input type="hidden" name="hiddenNo4" /> <span
								class="normal10">4</span></td>
							<%-- // 2019.10.23 yamamoto modified. start
				<td><html:text name="requestForm" property="gouki4" maxlength="8" style="ime-mode:disabled" styleClass="normal12"/></td>
				<td>
					<form:select name="requestForm" property="genzu4" >
						<form:options labelName="requestForm" labelProperty="genzuNameList" name="requestForm" property="genzuNameList" />
			     	</form:select>
			     </td>
--%>
							<td><input type="text" name="kaisiNo4" maxlength="20"
								style="ime-mode: disabled" class="normal12"  value="${sessionScope.requestForm.kaisiNo4}"/></td>
							<td><span class="normal10">～</span></td>
							<td><input type="text" name="syuuryouNo4" maxlength="20"
								style="ime-mode: disabled" class="normal12"  value="${sessionScope.requestForm.syuuryouNo4}"/></td>

							<%--
				<td><html:text name="requestForm" property="busuu4" size="2"
					styleClass="normal12" maxlength="2" style="ime-mode:disabled;text-align:right;"/></td>
				<td>
					<form:select name="requestForm" property="syukusyou4" >
						<form:options labelName="requestForm" labelProperty="syukusyouList" name="requestForm" property="syukusyouList" />
			     	</form:select>
			    </td>
				<td>
					<form:select name="requestForm" property="size4" >
						<form:options labelName="requestForm" labelProperty="saizuList" name="requestForm" property="saizuList" />
			     	</form:select>
			    </td>
// 2019.10.23 yamamoto modified. end --%>
						</tr>
						<!--	No5の行  -->
						<tr>
							<td><input type="hidden" name="hiddenNo4" /> <span
								class="normal10">5</span></td>
							<%-- // 2019.10.23 yamamoto modified. start
				<td><html:text name="requestForm" property="gouki5" maxlength="8" style="ime-mode:disabled" styleClass="normal12"/></td>
				<td>
					<form:select name="requestForm" property="genzu5" >
						<form:options labelName="requestForm" labelProperty="genzuNameList" name="requestForm" property="genzuNameList" />
			     	</form:select>
			    </td>
--%>
							<td><input type="text" name="kaisiNo5" maxlength="20"
								style="ime-mode: disabled" class="normal12"  value="${sessionScope.requestForm.kaisiNo5}"/></td>
							<td><span class="normal10">～</span></td>
							<td><input type="text" name="syuuryouNo5" maxlength="20"
								style="ime-mode: disabled" class="normal12"  value="${sessionScope.requestForm.kaisiNo5}"/></td>

							<%--
				<td><html:text name="requestForm" property="busuu5" size="2"
					styleClass="normal12" maxlength="2" style="ime-mode:disabled;text-align:right;"/></td>
				<td>
					<form:select name="requestForm" property="syukusyou5" >
						<form:options labelName="requestForm" labelProperty="syukusyouList" name="requestForm" property="syukusyouList" />
			     	</form:select>
			     </td>
				<td>
					<form:select name="requestForm" property="size5" >
						<form:options labelName="requestForm" labelProperty="saizuList" name="requestForm" property="saizuList" />
			     	</form:select>
			     </td>
// 2019.10.23 yamamoto modified. end --%>
						</tr>
					</table></td>
			</tr>
			<!--======================= 依頼ボタン =======================-->
			<tr>
				<td>
					<table border="0" align="center">
						<tr>
							<td><input type="submit" value="依　頼" onclick="doIrai()"
								style="font-size: 12px;" /></td>
						</tr>
					</table>

				</td>
				<td><input type="button" value="Close" style="font-size: 12px;"
					onclick="window.close()" /></td>
			</tr>
		</table>
		<!--======================= 操作説明 =======================-->
		<table align="center" border="0">
			<tr>
				<td bgcolor="#EEEEEE" valign="top"><span class="normal12">
						&lt;&lt;原図庫からのお願い&gt;&gt;<br /> ・ 登録されていない図面は『図面登録依頼』を起票してください。<br />
						『図面登録依頼詳細』で依頼の完了を確認し、DRASAPで閲覧・印刷をしてください。<br /> ・
						なお、大きいサイズで印刷したいなど、印刷ができない場合は原図庫へTEL/FAXにて依頼してください。<br /> <br />
						※番号の範囲指定について<br /> 範囲指定するには１１桁の図番の先頭９桁が同じである必要があります。<br />
						（ハイフンは含みません）<br /> １２ケタの図番について範囲指定をする事はできません。<br /> <br />
						★この画面に関する詳細は右上にある「HELP」を参照ください。<br />
				</span></td>
			</tr>
		</table>
	</form>
</body>
</html>
