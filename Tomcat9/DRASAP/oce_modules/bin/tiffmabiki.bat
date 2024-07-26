@echo off
SETLOCAL
rem -------------------------------------------------------------------------------------------
rem 
rem                         �c�q�`�r�`�o�@�s�h�e�e�Ԉ����o�b�`�t�@�C��
rem 
rem �@�@�@�@�@�@�@Copyright (c) 2008, Oce-Japan Corporation, All Rights Reserved.
rem 
rem    �g�p�@�F
rem        tiffmabiki.bat ���̓t�@�C�� �o�̓t�@�C�� �𑜓x
rem    
rem    �ϐ��F
rem        %1�i�����j	�F�Ԉ����Ώۂs�h�e�e�t�@�C���i�t���p�X�j
rem        %2�i�����j	�F�Ԉ�����̏o�͂s�h�e�e�t�@�C���i�t���p�X�j
rem        %3�i�����j	�F�𑜓x(100/200)
rem 
rem    �߂�l�FERRORLEVEL
rem        = 0:����I��
rem        = 1:�ُ�I��
rem 
rem    ���l�F
rem        �ڍׂȃG���[���̓��O�t�@�C���ɏo�͂���܂��B
rem 

rem -------------------------
rem �c�q�`�r�`�o�x�[�X�p�X
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
rem �𑜓x���Ƀt�@�C����I��
rem -------------------------
set ATR_OUT=%BASE_PATH%\oce_modules\ras_atr\ras_out_400.atr
if %3 == 100 set ATR_OUT=%BASE_PATH%\oce_modules\ras_atr\ras_out_100.atr
if %3 == 200 set ATR_OUT=%BASE_PATH%\oce_modules\ras_atr\ras_out_200.atr

rem -------------
rem ���O�t�@�C��
rem -------------
set LOG=%BASE_PATH%\logs\tiffmabiki.log

rem -------------------------
rem �R���o�[�^�[�ŕϊ�
rem -------------------------
%RSTX3RS_PATH%\rstx3rs.exe -nodisp -noprog -U%ATR_IN% -Z%ATR_OUT% -RIREKI%LOG% -maxrireki200 %1 -O%2 >Nul: 2>&1
set DATE_TIME=%DATE:~0,4%%DATE:~5,2%%DATE:~8,2%%TIME:~0,2%%TIME:~3,2%%TIME:~6,2%
if not %ERRORLEVEL% == 0 (
	copy %LOG% %LOG%.%DATE_TIME%
	ENDLOCAL
	EXIT /B 1
)
EXIT /B 0
