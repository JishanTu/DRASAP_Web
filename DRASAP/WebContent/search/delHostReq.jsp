<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="tyk.drasap.search.*,tyk.drasap.common.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/struts-nested" prefix="nested" %>
<%-- ログイン情報の確認 --%>
<logic:notPresent name="user" scope="session">
	<logic:redirect forward="timeout" />
</logic:notPresent>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Drawing Search and Print System [HOST依頼削除]</title>
<meta content="text/html; charset=UTF-8" http-equiv="Content-type" />
<meta content="no-cache" http-equiv="Pragma" />
<meta content="no-cache" http-equiv="Cache-Control" />
<style type="text/css">@import url( <%=request.getContextPath() %>/default.css );</style>
<style type="text/css">
    .condition {
    margin-left:30px;
    margin-right:30px;
    }
    .goBackBtn {
    position:absolute;
    bottom:30px;
    right:30px;
    }
    .msgBox {
    text-align:left;
    width:600px;
    border:none;
    }
    .keyLock {
    z-index:99;
    left:0;
    top:0;
    position:absolute;
    background-color:transparent;
    width:100%;
    height:100%;
    padding:0px;
    margin:0px;
    }
</style>
<script type="text/javascript">
<!--
    browserName = navigator.appName;
    var buttonPress = false;

    function onLoad(){
        onInitFocus();
    }
    // 初期フォーカス位置
    function onInitFocus(){
//        document.forms[0].seachKind[0].focus();
    }
    function backPage(){
        closeReq();
        buttonPress = true;
        self.close();
    }
    function clearVal() {
        var idx = 0;
		document.forms[0].seachKind[0].checked = false;
		document.forms[0].seachKind[1].checked = false;
        while((elm = getConditionElm(idx)) != null) {
            elm.style.color="#000000";
            elm.value = "";
            idx = idx + 1;
        }
	var msgList = document.getElementById("msgList");
        msgList.innerHTML = "";
    }
    function delReq() {
	var delCnt = inputChk();
        if (delCnt == 0) return;

        if(! confirm("この"+delCnt+"件の依頼を完全に削除します。\n本当によろしいですか?")){
            return;
        }
        buttonPress = true;
//        document.body.style.cursor="wait";
        keyLock();
	var deleteButton = document.getElementById("deleteButton");
	var clearButton = document.getElementById("clearButton");
	var msgList = document.getElementById("msgList");
	deleteButton.disabled=true;
	clearButton.disabled=true;
        msgList.innerHTML = "";
        msgList.innerHTML = "<li align='left' style='color:#0000FF;'>依頼の削除には時間がかかることがあります。しばらくお待ちください。</li><br/>";
	document.forms[0].act.value='delete';// 隠し属性actをセット
	document.forms[0].submit();
    }
    function closeReq() {
        if (!buttonPress) {
            document.forms[0].act.value='close';// 隠し属性actをセット
	    document.forms[0].submit();
        }
    }
    function searchCheck(value) {
        var example = document.getElementById("example");
        if (value == "delSeisan") {
            example.innerHTML = "(YYYYMMDD[A|C]nnnnn)";
        } else if (value == "delPrt") {
            example.innerHTML = "(YYYYMMDD[B|D]nnnnn)";
        }
//        seachKind = value;
    }
    function inputChk() {
        var seachKind = null;
        var cnt = 0;
        if (document.forms[0].seachKind[0].checked) seachKind = "delSeisan";
        if (document.forms[0].seachKind[1].checked) seachKind = "delPrt";
        if (seachKind == null) {
            alert("HOST依頼を選択してください。");
            document.forms[0].seachKind[0].focus();
            return 0;
        }
        var elm = null;
        var idx = 0;
        while((elm = getConditionElm(idx)) != null) {
            elm.style.color="#000000";
            if (elm.value.length > 0) {
                if (elm.value.length != 14) {
                    elm.style.color="#FF0000";
                    elm.focus();
                    if (seachKind == "delSeisan") {
                        alert("HOST生産出図依頼番号はYYYYMMDD[A|C]nnnnnで入力してください。")
                    } else {
                        alert("HOST帳票出力依頼番号はYYYYMMDD[B|D]nnnnnで入力してください。")
                    }
                    return 0;
                } else {
                    if (seachKind == "delSeisan" && elm.value.substr(8,1) != "A" && elm.value.substr(8,1) != "C") {
                        elm.style.color="#FF0000";
                        elm.focus();
                        alert("HOST生産出図依頼番号はYYYYMMDD[A|C]nnnnnで入力してください。")
                        return 0;
                    } else if (seachKind == "delPrt" && elm.value.substr(8,1) != "B" && elm.value.substr(8,1) != "D") {
                        elm.style.color="#FF0000";
                        elm.focus();
                        alert("HOST帳票出力依頼番号はYYYYMMDD[B|D]nnnnnで入力してください。")
                        return 0;
                    }
                }
                cnt = cnt + 1;
            }
            idx = idx + 1;
        }
        if (cnt == 0) {
            alert("依頼番号をひとつ以上指定してください。")
            elm = getConditionElm(0).focus();
            return 0;
        }
        return cnt;
    }
    function getConditionElm(idx) {
	return document.getElementsByName("condition["+idx+"]")[0];
    }
    function keyLock(){
        var keyLock;
        keyLock = document.getElementById("keyLock");
        keyLock.style.cursor="wait";
        keyLock.style.visibility = "visible";
        keyLock.style.height = document.body.parentNode.clientHeight + "px";
    }
//-->
</script>
</head>
<body bgcolor="#FFFFFF" onload="onInitFocus()" onunload="closeReq()" bottommargin="0" leftmargin="0" topmargin="0" rightmargin="0" marginheight="0" marginwidth="0">
<bean:define id="deleteHostReqForm" type="tyk.drasap.search.DeleteHostReqForm" name="deleteHostReqForm" scope="session" />
<html:form action="/delHostReq">
<html:hidden property="act" />
<nested:root name="deleteHostReqForm">
<!--============ ヘッダ ============-->


<table border="0" cellspacing="0" cellpadding="0" width="100%">
    <tr>
        <td>
            <table border="0" bgcolor="#FFFFFF">
                <tr><td nowrap="nowrap"><span class="normal18">HOST依頼削除</span></td>
                </tr>
            </table>
        </td>
    </tr>
</table>
<br/>
<table align="center" border="0" cellspacing="0" cellpadding="0" class="normal12">
    <tr>
        <td colspan="2">
            <b>依頼番号を指定してHOST依頼を削除します。</b>
        </td>
    </tr>
    <tr>
        <td>
            <br/>
        </td>
    </tr>
    <tr>
        <td>
            <fieldset align="center" style="padding:10px;width:200px;">
            <legend>HOST依頼選択</legend>
            <table align="center" border="0" cellspacing="0" cellpadding="0" class="normal12">
                <tr>
                    <td>
                        <html:radio property="seachKind" tabindex="1" value="delSeisan" onclick="searchCheck(this.value)"/>HOST生産出図依頼削除&nbsp;&nbsp;<br/>
                        <html:radio property="seachKind" tabindex="1" value="delPrt" onclick="searchCheck(this.value)"/>HOST帳票出力依頼削除&nbsp;&nbsp;<br/>
                    </td>
                </tr>
            </table>
            </fieldset>
        </td>
    </tr>
</table>
<br/>
<table align="center" border="0" cellspacing="0" cellpadding="0" class="normal12" style="margin-top:10px;background-color:#EEEEEE;">
    <tr><td align="center">削除依頼番号</td></tr>
    <tr><td align="center" style="font-size:10pt;"><span id="example">
    <nested:equal property="seachKind" value="delSeisan" scope="session">
    (YYYYMMDD[A|C]nnnnn)
    </nested:equal>
    <nested:equal property="seachKind" value="delPrt" scope="session">
    (YYYYMMDD[B|D]nnnnn)
    </nested:equal>
    <nested:empty property="seachKind" scope="session">
    <br/>
    </nested:empty>
    </span>
    </td>
    </tr>
    <nested:iterate id="condition" property="condition" indexId="idx_1">
        <tr><td align="center"><nested:text property="" styleClass="condition" errorStyle="color:red" tabindex="<%=Integer.valueOf(idx_1+2).toString()%>"/></td></tr>
    </nested:iterate>
    <tr><td align="center"><br/></td></tr>
</table>
<table align="center" border="0" cellspacing="10" cellpadding="0" class="normal12" style="margin-top:10px;">
    <tr>
        <td align="center">
            <input id="clearButton" type="button" value="クリア" onclick="clearVal()" tabindex="12" style="width:100px;margin-right:10px;"/>
            <input id="deleteButton" type="button" value="削除" onclick="delReq()" tabindex="13" style="width:100px;margin-left:10px;" <%=(deleteHostReqForm.isDeleteOK()?"":"disabled") %>/>
	</td>
    </tr>
    <tr>
        <td align="left">
            <!-- message -->
            <ul style="list-style:none;" id="msgList">
            <nested:iterate id="msgList" property="msgList" type="MsgInfo" indexId="idx_2">
                <li align="left" style="<%=msgList.getMsgStyle()%>"><nested:write property="msg" /></li><br/>
            </nested:iterate>
            </ul>
        </td>
    </tr>
    <tr>
	<td align="left" class="normal12blue">
    		<html:errors />
	</td>
    </tr>
</table>

</nested:root>
<table class="keyLock" id="keyLock" style="visibility:hidden">
<tr valign="middle">
<td align="center" style="font-size:18pt;color:#0000FF;">
削除中・・・・
</td>
</tr>
</table>
</html:form>
<span class="goBackBtn" ><input type="button" onclick="backPage()" value="　戻る　" id="backButton" tabindex="14" style="width:100px;"/></span>
</body>
</html>

