@echo off
SETLOCAL
rem -------------------------------------------------------------------------------------------
rem 
rem                         ＤＲＡＳＡＰ　テキストマージバッチファイル
rem 
rem 　　　　　　　Copyright (c) 2008, Oce-Japan Corporation, All Rights Reserved.
rem 
rem    使用法：
rem        setBannerWidth.bat 出力垂直サイズ(1/100mm)
rem    
rem    変数：
rem 
rem    戻り値：ERRORLEVEL
rem        = 0:正常終了
rem        = 1:異常終了
rem 
rem    備考：
rem        詳細なエラー情報はログファイルに出力されます。
rem 
rem -------------------------------------------------------------------------------------------

rem -------------------------------------------------------------------
rem ＤＲＡＳＡＰベースパス
rem 2013.07.01 yamagishi modified.
rem 2019.12.07 Tanaka modified. 
rem            テスト環境がCATALINA_HOMEの環境を参照しないようにした。
rem            本番環境用のコンバータを使うと%BANNER_TMP%で指定された
rem            固定のファイル名と同じなので、間違ったスタンプが合成
rem            される可能性があるので分離した。
rem -------------------------------------------------------------------
rem if NOT "%BEA_HOME%"=="" (
rem 	set BASE_PATH=%BEA_HOME%\DRASAP
rem ) else (
rem 	if NOT "%CATALINA_HOME%"=="" (
rem 		set BASE_PATH=%CATALINA_HOME%\DRASAP
rem 	) else (
		if NOT "%OCE_AP_SERVER_HOME%"=="" (
			set BASE_PATH=%OCE_AP_SERVER_HOME%\DRASAP
		) else (
			set BASE_PATH=D:\Tomcat9\DRASAP
		)
rem 	)
rem )

if "%1"== "" (
	set bannerWidth=2000
) else (
	set bannerWidth=%1
)
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

rem -------------
rem ログファイル
rem -------------
set LOG=%BASE_PATH%\logs\setBannerWidth.log

rem -------------
rem バナーファイル
rem -------------
set BANNER_WAKU=%BASE_PATH%\oce_modules\img\banner-correspond.tif
set BANNER_TMP=%BASE_PATH%\oce_modules\tmp\tmpStamp0.TIF

rem -------------------------
rem コンバーターで変換
rem -------------------------
%RSTX3RS_PATH%\rstx3rs.exe -nodisp -noprog -WM%bannerWidth% %BANNER_WAKU% -O%BANNER_TMP% -U%ATR_IN% -Z%ATR_OUT% -RIREKI%LOG% -maxrireki200 >Nul: 2>&1
set DATE_TIME=%DATE:~0,4%%DATE:~5,2%%DATE:~8,2%%TIME:~0,2%%TIME:~3,2%%TIME:~6,2%
if not %ERRORLEVEL% == 0 (
	copy %LOG% %LOG%.%DATE_TIME%
	ENDLOCAL
	EXIT /B 1
)
ENDLOCAL
EXIT /B 0

