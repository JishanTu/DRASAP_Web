<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%-- ログイン情報の確認 --%>
<c:if test="${empty sessionScope.user}">
<script>
	location.replace('<%=request.getContextPath()%>/timeout');
</script>
</c:if>

<c:set var="accessLevelBatchUpdateForm" value="${sessionScope.accessLevelBatchUpdateForm}" />

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
	<title>アクセスレベル一括更新</title>
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<style type="text/css">@import url( <%=request.getContextPath() %>/resources/css/<%=session.getAttribute("default_css")%> );</style>
	<style type="text/css">
		.management {
		    position: absolute;
		    top:0px;
		    right:0px;
		    margin-right:10px;
		}
		 .file-input-container input[type="file"] {
            display: none;
        }
         .file-input-container input[type="text"] {
            width: 600px;
        }
	</style>
	<script type="text/javascript">
		document.onkeydown = keys;
		function keys() {
			switch (event.keyCode) {
				case 116: // F5
					event.keyCode = 0;
					return false;
					break;
			}
		}
		function lockDocumentButtons(docObj) {
			if (!docObj) return;
			var buttons = docObj.getElementsByTagName("input");
			for (var i=0; i < buttons.length; i++) {
				if (buttons[i].value != "Close")
					buttons[i].disabled = true;
			}
			buttons = docObj.getElementsByTagName("select");
			for (var i=0; i < buttons.length; i++) {
					buttons[i].disabled = true;
			}
			buttons = docObj.getElementsByTagName("a");
			for (var i=0; i < buttons.length; i++) {
					buttons[i].disabled = true;
			}
			if (!docObj.body) return;
				docObj.body.style.cursor = 'wait';
		}
		function unLockDocmentButtons(docObj) {
			if (!docObj) return;
			var buttons = docObj.getElementsByTagName("input");
			for (var i=0; i < buttons.length; i++) {
					buttons[i].disabled = false;
			}
			var buttons = docObj.getElementsByTagName("select");
			for (var i=0; i < buttons.length; i++) {
					buttons[i].disabled = false;
			}
			var buttons = docObj.getElementsByTagName("a");
			for (var i=0; i < buttons.length; i++) {
					buttons[i].disabled = false;
			}
			if (!docObj.body) return;
				docObj.body.style.cursor = '';
		}
		function lockButtons() {
			if (parent.acl_list != null) {
				parent.acl_list.nowProcessing();
			} else if (parent != null) {
				if (parent.nowProcessing != undefined)
					parent.nowProcessing();
			}
			lockDocumentButtons(document);
			if (parent != null) lockDocumentButtons(parent.document);
			if (parent.parent.acl_head != null) lockDocumentButtons(parent.parent.acl_head.document);
		}
		function unLockButtons() {
			unLockDocmentButtons(document);
			if (parent != null) unLockDocmentButtons(parent.document);
			if (parent.parent.acl_head != null) unLockDocmentButtons(parent.parent.acl_head.document);
		}
		// 更新（事前）
		function preUpdate() {
			if (confirm("図面アクセスレベルを一括更新します。\nよろしいですか?")) {
				doUpdate();
				lockButtons();
			}
		}
		// 更新
		function doUpdate() {
			document.forms[0].action = '<%=request.getContextPath() %>/accessLevelBatchUpdate';
			document.forms[0].act.value = 'update';// 隠し属性actにをセット
			document.forms[0].target = "_top";// ターゲットは画面全体
			document.forms[0].submit();
		}
		// アップロード
		function doUpload() {
			document.forms[0].action = '<%=request.getContextPath() %>/accessLevelBatchUpdate';
			document.forms[0].act.value = 'upload';// 隠し属性actにをセット
			document.forms[0].target = "_top";// ターゲットは画面全体
			document.forms[0].submit();
		}
		
		function upload() {
			    document.getElementById('fileUpload').click();
		}
		// ダウンロード
		function doDownload() {
			document.forms[0].action = '<%=request.getContextPath() %>/accessLevelDownload';<%--//DL用のURLをセット--%>
			document.forms[0].dlFileType.value = "1";// 表示内容をExcelファイルでダウンロード
			document.forms[0].target = "acl_list";// ターゲットは一覧表示部
			document.forms[0].submit();
		}
	</script>
</head>
<body bgcolor="#CCCCCC" style="margin: 0;">
<form action="<%=request.getContextPath() %>/accessLevelBatchUpdate" enctype = "multipart/form-data" method = "post">
    <input type = "hidden" name = "act" value = ""/>
    <input type = "hidden" name = "aclUpdateNo" value = "${accessLevelBatchUpdateForm.aclUpdateNo}"/>
	<input type="hidden" name="dlFileType" value = ""/>
<!--================ ヘッダ ==================================-->
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td width="100%" height="20px;" align="center" colspan="3">
			<a href="<%=request.getContextPath() %>/accessLevelDownload.do?dlFileType=0" target="acl_list" class="normal10blue" style = "text-decoration: underline;">雛形ファイルのダウンロード</a>
		</td>
		<td />
	</tr>
	<tr>
		<td width="40%" colspan="3">
			<table border="0">
				<tr>
					<td nowrap="nowrap">
					 <div class="file-input-container">
						<label class="normal10">アップロードファイル</label>
						<input type="text" id="filePath" readonly placeholder="ファイルを選択されていません">
						<input type="file" id="fileUpload" name="uploadFile">
						<input type="button" value="参照" onclick="upload()" />
        				
        				</div>
        				
					</td>
					<td>
						&nbsp;<input type="button" value="アップロード" onclick="doUpload();lockButtons();" style="width: 100px;" />
					</td>
				</tr>
			</table>
		</td>
		<td width="60%" />
	</tr>
	<tr>
		<td width="5%">
<%  // 2019.12.26 yamamoto mod start
	//		<logic:equal name="user" property="aclBatchUpdateFlag" value="1" scope="session">
%>
			<input type="button" value="　更新　" onclick="preUpdate()" />
<%	//		</logic:equal>
    // 2019.12.26 yamamoto mod end %>
		</td>
		<td width="25%">
			<span style="background-color: #EEEEEE;" class="normal12">管理NO</span>&nbsp;<c:out value="${accessLevelBatchUpdateForm.aclUpdateNo}" />&nbsp;&nbsp;
            <span style="background-color: #EEEEEE;" class="normal12">品番数</span>&nbsp;<c:out value="${accessLevelBatchUpdateForm.itemNoCount}" />&nbsp;
        </td>
		<td width="10%" align="right">
				<table border="0">
					<tr>
						<td><input type="button" value="ダウンロード" onclick="doDownload()" style="width: 100px;" /></td>
					</tr>
				</table>
		</td>
		<td width="60%" />
	</tr>
</table>
</form>
<script>

        document.getElementById('fileUpload').addEventListener('change', function() {
            var fileInput = this;
            var filePathInput = document.getElementById('filePath');

            // Display file name in the input box
            if (fileInput.files.length > 0) {
                filePathInput.value = fileInput.files[0].name;
            } else {
                filePathInput.value = 'ファイルを選択されていません';
            }
        });
        
       
    </script>
</body>
</html>
