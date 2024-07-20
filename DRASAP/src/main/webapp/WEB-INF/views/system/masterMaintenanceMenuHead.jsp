<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="tyk.drasap.common.*"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Cache-Control" content="no-cache" />
<style type="text/css">@import url( <%=request.getContextPath() %>/default.css );</style>
<style type="text/css">
	h1 {
		font-size:16pt;
		font-weight:bold;
		font-family: Georgia, Times, serif;
/*		font-family: "ＭＳ 明朝","ＭＳ ゴシック";*/
/*		font-family: "HGPｺﾞｼｯｸE","ＭＳ ゴシック";*/
	}
	img {
		border:none;
	}
</style>
<script type="text/javascript">
<!--
	browserName = navigator.appName;
	// 遷移する
	function backPage(parm){
		if (parm == "LOGOUT"){
			document.forms[0].target="_parent";
			document.forms[0].act.value='logout';//
			document.forms[0].submit();
			return;
		} else {
			return;
		}
	}
//--->
</script>
</head>
<body bgcolor="#F5F5DC" bottommargin="0" leftmargin="5" topmargin="5" rightmargin="5" marginheight="0" marginwidth="0">
<html:form action="/masterMaintenanceMenu">
	<html:hidden property="act" />
	<table style="width:100%">
		<tr>
			<td colspan="2"></td>
		</tr>
		<tr>
			<td>
			<table>
				<tr>
					<td nowrap="nowrap">
					<h1>DRASAP Master Maintenance</h1>
					</td>
				</tr>
			</table>
			</td>
			<td align="right" nowrap="nowrap" paddding-right="10px;"><input type="button" value="LOGOUT" onclick="backPage('LOGOUT')" />
			</td>
		</tr>
	</table>
</html:form>
</body>
</html>


