﻿<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>DRASAP [マスターメンテナンスメニュー]</title>
<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Cache-Control" content="no-cache" />
<style type="text/css">@import url( <%=request.getContextPath() %>/default.css );</style>
<style type="text/css">
	frameset {
		background-color: #F5F5DC;
		border:0px solid #F5F5DC;
		margine:0px;
	}
	frame {
		background-color: #F5F5DC;
		border:0px solid #F5F5DC;
		margine:0px;
	}
</style>
</head>
<frameset rows="50px,*">
	<frame name="_frameHead" src="system/masterMaintenanceMenuHead.jsp" border="0" frameborder="0" noresize="noresize"
		scrolling="no" />
	<frameset cols="240px,*">
		<frame name="_frameLeft" src="system/masterMaintenanceMenuLeft.jsp" border="0" frameborder="0" noresize="noresize"
			scrolling="no" />
		<frame name="_frameRight" src="system/blank.jsp" border="0" frameborder="0" noresize="noresize" scrolling="auto" />
	</frameset>
</frameset>
</html>


