@echo off
SETLOCAL
rem -------------------------------------------------------------------------------------------
rem 
rem                         �c�q�`�r�`�o�@�X�^���v�}�[�W�o�b�`�t�@�C��
rem 
rem �@�@�@�@�@�@�@Copyright (c) 2008, Oce-Japan Corporation, All Rights Reserved.
rem 
rem    �g�p�@�F
rem        stampMerge.bat ���̓t�@�C�� �o�̓t�@�C�� �𑜓x
rem    
rem    �ϐ��F
rem        %1�i�����j	�F�o�i�[�����Ώۂs�h�e�e�t�@�C���i�t���p�X�j
rem        %2�i�����j	�F�o�i�[������̏o�͂s�h�e�e�t�@�C���i�t���p�X�j
rem        %3�i�����j	�F�Z�x:(0:�s�����`255:����)
rem 
rem    �߂�l�FERRORLEVEL
rem        = 0:����I��
rem        = 1:�ُ�I��
rem 
rem    ���l�F
rem        �X�^���v����stampMerge.txt�ɒ�`����܂��B
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
			set BASE_PATH=D:\Tomcat9_TEST\DRASAP_TEST
		)
rem 	)
rem )

rem -------------------------
rem TIFF�ϊ����W���[���p�X
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

set STAMP_MERGE_TXT=%BASE_PATH%\oce_modules\tmp\stampMerge.txt
rem -------------
rem ���O�t�@�C��
rem -------------
set LOG=%BASE_PATH%\logs\stampmerge.log

rem -------------------------
rem �R���o�[�^�[�ŕϊ�
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

