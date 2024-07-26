@echo off
SETLOCAL
rem -------------------------------------------------------------------------------------------
rem 
rem                         ＤＲＡＳＡＰ　テキストマージバッチファイル
rem 
rem 　　　　　　　Copyright (c) 2008, Oce-Japan Corporation, All Rights Reserved.
rem 
rem    使用法：
rem        textMerge.bat
rem    
rem    変数：
rem 
rem    戻り値：ERRORLEVEL
rem        = 0:正常終了
rem        = 1:異常終了
rem 
rem    備考：
rem        合成文字列はtextMerge.txtに定義されます。
rem        詳細なエラー情報はログファイルに出力されます。
rem 
REM -------------------------------------------------------------------------------------------
rem -------------------------
rem ＤＲＡＳＡＰベースパス
rem 2013.07.01 yamagishi modified.
rem -------------------------
if NOT "%BEA_HOME%"=="" (
	set BASE_PATH=%BEA_HOME%\DRASAP
) else (
	if NOT "%CATALINA_HOME%"=="" (
		set BASE_PATH=%CATALINA_HOME%\DRASAP
	) else (
		if NOT "%OCE_AP_SERVER_HOME%"=="" (
			set BASE_PATH=%OCE_AP_SERVER_HOME%\DRASAP
		) else (
			set BASE_PATH=E:\Tomcat7\DRASAP
		)
	)
)
rem echo BEA_HOME=%BEA_HOME% > E:\Tomcat7\DRASAP\oce_modules\bin\basePath.txt
rem echo OCE_BEA_HOME=%OCE_BEA_HOME% >> E:\Tomcat7\DRASAP\oce_modules\bin\basePath.txt
rem echo BASE_PATH=%BASE_PATH% >> E:\Tomcat7\DRASAP\oce_modules\bin\basePath.txt

rem -------------------------
rem TIFF変換モジュール
rem -------------------------
set RSTX3RS_PATH=%BASE_PATH%\oce_modules\bin

rem -------------------------
rem 入力属性ファイル
rem -------------------------
set ATR_IN=%BASE_PATH%\oce_modules\ras_atr\ras_in.atr

rem -------------------------
rem 出力属性ファイル
rem -------------------------
set ATR_OUT=%BASE_PATH%\oce_modules\ras_atr\ras_out.atr

set TXT_MERGE_TXT=%BASE_PATH%\oce_modules\tmp\textMerge.txt
rem -------------
rem ログファイル
rem -------------
set LOG=%BASE_PATH%\logs\textmerge.log

rem -------------
rem バナーファイル
rem -------------
rem set BANNER_WAKU=%BASE_PATH%\oce_modules\img\banner_YOKO_h8mm_w100mm.tif
set BANNER_WAKU=%BASE_PATH%\oce_modules\tmp\tmpStamp0.TIF
set BANNER_TMP=%BASE_PATH%\oce_modules\tmp\tmpStamp.TIF

rem -------------------------
rem コンバーターで変換
rem -------------------------
%RSTX3RS_PATH%\rstx3rs.exe -nodisp -noprog -HM800 %BANNER_WAKU% -O%BANNER_TMP% -U%ATR_IN% -Z%ATR_OUT% -TX%TXT_MERGE_TXT% -RIREKI%LOG% -maxrireki200 >Nul: 2>&1
set DATE_TIME=%DATE:~0,4%%DATE:~5,2%%DATE:~8,2%%TIME:~0,2%%TIME:~3,2%%TIME:~6,2%
if not %ERRORLEVEL% == 0 (
	copy %LOG% %LOG%.%DATE_TIME%
	ENDLOCAL
	EXIT /B 1
)
ENDLOCAL
EXIT /B 0

