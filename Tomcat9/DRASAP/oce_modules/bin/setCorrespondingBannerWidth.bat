@echo off
SETLOCAL
rem -------------------------------------------------------------------------------------------
rem 
rem                         �c�q�`�r�`�o�@�e�L�X�g�}�[�W�o�b�`�t�@�C��
rem 
rem �@�@�@�@�@�@�@Copyright (c) 2008, Oce-Japan Corporation, All Rights Reserved.
rem 
rem    �g�p�@�F
rem        setBannerWidth.bat �o�͐����T�C�Y(1/100mm)
rem    
rem    �ϐ��F
rem 
rem    �߂�l�FERRORLEVEL
rem        = 0:����I��
rem        = 1:�ُ�I��
rem 
rem    ���l�F
rem        �ڍׂȃG���[���̓��O�t�@�C���ɏo�͂���܂��B
rem 
rem -------------------------------------------------------------------------------------------

rem -------------------------------------------------------------------
rem �c�q�`�r�`�o�x�[�X�p�X
rem 2013.07.01 yamagishi modified.
rem 2019.12.07 Tanaka modified. 
rem            �e�X�g����CATALINA_HOME�̊����Q�Ƃ��Ȃ��悤�ɂ����B
rem            �{�Ԋ��p�̃R���o�[�^���g����%BANNER_TMP%�Ŏw�肳�ꂽ
rem            �Œ�̃t�@�C�����Ɠ����Ȃ̂ŁA�Ԉ�����X�^���v������
rem            �����\��������̂ŕ��������B
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
rem TIFF�ϊ����W���[��
rem -------------------------
set RSTX3RS_PATH=%BASE_PATH%\oce_modules\bin

rem -------------------------
rem ���͑����t�@�C��
rem -------------------------
set ATR_IN=%BASE_PATH%\oce_modules\ras_atr\ras_in.atr

rem -------------------------
rem �o�͑����t�@�C��
rem -------------------------
set ATR_OUT=%BASE_PATH%\oce_modules\ras_atr\ras_out.atr

rem -------------
rem ���O�t�@�C��
rem -------------
set LOG=%BASE_PATH%\logs\setBannerWidth.log

rem -------------
rem �o�i�[�t�@�C��
rem -------------
set BANNER_WAKU=%BASE_PATH%\oce_modules\img\banner-correspond.tif
set BANNER_TMP=%BASE_PATH%\oce_modules\tmp\tmpStamp0.TIF

rem -------------------------
rem �R���o�[�^�[�ŕϊ�
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

