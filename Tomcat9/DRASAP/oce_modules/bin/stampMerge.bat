@echo off
SETLOCAL
rem -------------------------------------------------------------------------------------------
rem 
rem                         ＤＲＡＳＡＰ　スタンプマージバッチファイル
rem 
rem 　　　　　　　Copyright (c) 2008, Oce-Japan Corporation, All Rights Reserved.
rem 
rem    使用法：
rem        stampMerge.bat 入力ファイル 出力ファイル 解像度
rem    
rem    変数：
rem        %1（引数）	：バナー合成対象ＴＩＦＦファイル（フルパス）
rem        %2（引数）	：バナー合成後の出力ＴＩＦＦファイル（フルパス）
rem        %3（引数）	：濃度:(0:不透明～255:透明)
rem 
rem    戻り値：ERRORLEVEL
rem        = 0:正常終了
rem        = 1:異常終了
rem 
rem    備考：
rem        スタンプ情報はstampMerge.txtに定義されます。
rem        詳細なエラー情報はログファイルに出力されます。
rem 
REM -------------------------------------------------------------------------------------------

rem -------------------------
rem DRASAPベースパス
rem 2013.07.01 yamagishi modified.
rem -------------------------
if NOT "%BEA_HOME%"=="" (
	set BASE_PATH=%BEA_HOME%\DRASAP
) else (
	if NOT "%CATALINA_HOME%"=="" (
		set BASE_PATH=%CATALINA_HOME%\DRASAP
	) else (
		if NOT "%OCE_BEA_HOME%"=="" (
			set BASE_PATH=%OCE_AP_SERVER_HOME%\DRASAP
		) else (
			set BASE_PATH=E:\Tomcat7\DRASAP
		)
	)
)

rem -------------------------
rem TIFF変換モジュールパス
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

set STAMP_MERGE_TXT=%BASE_PATH%\oce_modules\tmp\stampMerge.txt
rem -------------
rem ログファイル
rem -------------
set LOG=%BASE_PATH%\logs\stampmerge.log

rem -------------------------
rem コンバーターで変換
rem -------------------------
%RSTX3RS_PATH%\rstx3rs.exe -nodisp -noprog %1 -O%2 -U%ATR_IN% -Z%ATR_OUT% -MERGE%STAMP_MERGE_TXT% -RIREKI%LOG% -maxrireki200 >Nul: 2>&1
set DATE_TIME=%DATE:~0,4%%DATE:~5,2%%DATE:~8,2%%TIME:~0,2%%TIME:~3,2%%TIME:~6,2%
if not %ERRORLEVEL% == 0 (
	copy %LOG% %LOG%.%DATE_TIME%
	ENDLOCAL
	EXIT /B 1
)
ENDLOCAL
EXIT /B 0

