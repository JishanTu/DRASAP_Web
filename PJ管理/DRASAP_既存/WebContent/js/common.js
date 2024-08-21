function isNumber(chkstr){
	var numberArray = "0123456789";
	for (var cnt=0;cnt<chkstr.length;cnt++) {
		if (numberArray.indexOf(chkstr.charAt(cnt)) == -1) return false;
	}
	return true;
}
function isDate(yearObj, monthObj, dayObj){
	if (yearObj.value.length != 4) {
		alert("４桁の年を入力してください。");
		yearObj.focus();
		yearObj.style.backgroundColor="#FF0000";
		return false;
	}
	if (parseInt(monthObj.value) < 1 || parseInt(monthObj.value) > 12) {
		alert("入力した月が不正です。");
		monthObj.focus();
		monthObj.style.backgroundColor="#FF0000";
		return false;
	}
	if (parseInt(dayObj.value) < 1 || parseInt(dayObj.value) > 31) {
		alert("入力した日が不正です。");
		dayObj.focus();
		dayObj.style.backgroundColor="#FF0000";
		return false;
	}
	var d = new Date();
	d.setFullYear(parseInt(yearObj.value), parseInt(monthObj.value)-1, parseInt(dayObj.value));
	if (d.getDate() != parseInt(dayObj.value)) {
		alert("入力した日が不正です。");
		dayObj.focus();
		dayObj.style.backgroundColor="#FF0000";
		return false;
	}
	return true;
}
function isIE() {
	browserName = navigator.appName;
	if ((browserName.indexOf("Microsoft") > 0) ||
	    (browserName.indexOf("Internet") > 0) ||
	    (browserName.indexOf("Explorer") > 0)) {
		return true;
	} else {
		return false;
	}
}
function setWinFocus() {
	if (isIE()) window.focus();
}
function commonInit() {
	var anchors = document.getElementsByTagName("a");
	for (var i = 0; i < anchors.length; i++) {
		anchors[i].onmouseover=function() {anchorOnmouseover(this);};
		anchors[i].onmouseout=function() {anchorOnmouseout(this);};
	}
}
function anchorOnmouseover(obj)
{
	obj.style.textDecoration="underline";
	obj.style.color="#0000FF";
}
function anchorOnmouseout(obj)
{
	obj.style.textDecoration="none";
	obj.style.textcolor="";
}
function createDialog(targetName, targetUrl, width, height) {
	var WO1;
	if (isIE()) {
		var ret = window.showModalDialog(targetUrl, '',
		"center=yes;status=no;scroll=no;resizable=no;dialogWidth="+width+"px; dialogHeight="+height+"px;help=no");
	} else {
		WO1=window.open(targetUrl, targetName,
			'toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no');
	}
}
function showPopup()
{
	wo = window.createPopup();
	popObj = wo.document.body;
	popObj.style.border = "solid blue 2px";
	popObj.innerHTML = "????";
	wo.show(-100,80,320,32,document.body);
}
function nowSearch() {
	var nowSearch;
	nowSearch = document.getElementById("nowSearch");
	nowSearch.style.visibility = "visible";
}
