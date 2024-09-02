@echo off
REM �N���[��
cls

REM ���O�t�@�C���̃f�B���N�g���Ɩ��O��ݒ�
SET "LOG_DIR=D:\Tomcat9\DRASAP\logs"
IF NOT EXIST "%LOG_DIR%" (
    mkdir "%LOG_DIR%"
)

REM �^�C���X�^���v���擾���ă��O�t�@�C������ݒ�
FOR /F "tokens=2 delims==" %%A IN ('wmic os get localdatetime /value') DO SET "DT=%%A"
SET "LOG_FILE=%LOG_DIR%\DRASAP_Deploy-%DT:~0,4%%DT:~4,2%%DT:~6,2%%DT:~8,2%%DT:~10,2%%DT:~12,2%.log"

REM �J�n�������擾
SET "START_DATE=%DT:~0,4%-%DT:~4,2%-%DT:~6,2%"
SET "START_TIME=%DT:~8,2%:%DT:~10,2%:%DT:~12,2%"
CALL :LogAndEcho "%START_DATE% %START_TIME%�ɔz�u�o�b�`�������J�n���܂����B"

REM �o�b�`�t�@�C�������݂���f�B���N�g�����擾
SET "SOURCE_DIR=%~dp0DRASAP"
SET "DEST_DIR=D:\Tomcat9\DRASAP"

REM �����O�̃N���[���A�b�v
CALL :CleanUp "%DEST_DIR%" "%LOG_DIR%"
CALL :RemoveDir "D:\Tomcat9\work\Catalina\localhost\DRASAP"
CALL :RemoveDir "D:\Tomcat9\webapps\DRASAP"

REM �\�[�X�f�B���N�g�������݂��邩�m�F
IF NOT EXIST "%SOURCE_DIR%" (
    CALL :LogAndEcho "�R�s�[���̃t�H���_��������܂���: %SOURCE_DIR%"
    EXIT /B 1
)

REM �ړI�n�f�B���N�g�������݂��邩�m�F���A���݂��Ȃ��ꍇ�͍쐬
IF NOT EXIST "%DEST_DIR%" (
    CALL :LogAndEcho "�R�s�[��̃t�H���_�����݂��܂���B�쐬���܂�: %DEST_DIR%"
    mkdir "%DEST_DIR%"
)

REM DRASAP�f�B���N�g�����R�s�[�i�����t�@�C�����㏑���j
xcopy /E /I /Y "%SOURCE_DIR%" "%DEST_DIR%" >> "%LOG_FILE%" 2>&1
IF %ERRORLEVEL% NEQ 0 (
    CALL :LogAndEcho "�t�@�C���̃R�s�[���ɃG���[���������܂����B"
    EXIT /B 1
)

REM .war�t�@�C���̃p�X��ݒ�
SET "WAR_FILE=%DEST_DIR%\war\DRASAP.war"

REM �R�s�[��f�B���N�g����ݒ�
SET "WEBAPPS_DIR=D:\Tomcat9\webapps"

REM �R�s�[��f�B���N�g�������݂��邩�m�F���A���݂��Ȃ��ꍇ�͍쐬
IF NOT EXIST "%WEBAPPS_DIR%" (
    CALL :LogAndEcho "�R�s�[��f�B���N�g�������݂��܂���B�쐬���܂�: %WEBAPPS_DIR%"
    mkdir "%WEBAPPS_DIR%"
)

REM .war�t�@�C�����R�s�[�i�����t�@�C�����㏑���j
IF EXIST "%WAR_FILE%" (
    CALL :LogAndEcho "�R�s�[��: %WAR_FILE% -> %WEBAPPS_DIR%"
    copy /Y "%WAR_FILE%" "%WEBAPPS_DIR%" >> "%LOG_FILE%" 2>&1
    IF %ERRORLEVEL% NEQ 0 (
        CALL :LogAndEcho ".war�t�@�C���̃R�s�[���ɃG���[���������܂����B"
        EXIT /B 1
    )
    CALL :LogAndEcho "�R�s�[���������܂����B"
) ELSE (
    CALL :LogAndEcho "DRASAP.war�t�@�C����������܂���: %WAR_FILE%"
)

REM war�f�B���N�g���̃p�X��ݒ�
SET "WAR_DIR=%DEST_DIR%\war"

REM war�f�B���N�g�������݂��邩�m�F���A�폜
IF EXIST "%WAR_DIR%" (
    CALL :LogAndEcho "war�f�B���N�g�����폜��: %WAR_DIR%"
    rmdir /S /Q "%WAR_DIR%" >> "%LOG_FILE%" 2>&1
    IF %ERRORLEVEL% NEQ 0 (
        CALL :LogAndEcho "war�f�B���N�g���̍폜���ɃG���[���������܂����B"
        EXIT /B 1
    )
    CALL :LogAndEcho "war�f�B���N�g�����폜����܂����B"
) ELSE (
    CALL :LogAndEcho "war�f�B���N�g����������܂���: %WAR_DIR%"
)

REM .gitkeep�t�@�C�����폜
CALL :LogAndEcho ".gitkeep�t�@�C�����폜��..."
FOR /R "%DEST_DIR%" %%F IN (.gitkeep) DO (
    IF EXIST "%%F" (
        CALL :LogAndEcho "�폜��: %%F"
        del "%%F" >> "%LOG_FILE%" 2>&1
        IF %ERRORLEVEL% NEQ 0 (
            CALL :LogAndEcho ".gitkeep�t�@�C���̍폜���ɃG���[���������܂���: %%F"
        )
    )
)

CALL :LogAndEcho ".gitkeep�t�@�C���̍폜���������܂����B"

REM �I���������擾
FOR /F "tokens=2 delims==" %%A IN ('wmic os get localdatetime /value') DO SET "DT=%%A"
SET "END_DATE=%DT:~0,4%-%DT:~4,2%-%DT:~6,2%"
SET "END_TIME=%DT:~8,2%:%DT:~10,2%:%DT:~12,2%"
CALL :LogAndEcho "%END_DATE% %END_TIME%�ɔz�u�o�b�`�������������܂����B"

REM �I��
PAUSE
EXIT /B

:LogAndEcho
REM ���b�Z�[�W�����O�t�@�C���ƃR���\�[���ɏo��
echo %* >> "%LOG_FILE%"
echo %*
EXIT /B

:CleanUp
REM �w�肳�ꂽ�f�B���N�g�����̃t�@�C���ƃT�u�f�B���N�g�����폜�i�������w�肳�ꂽ���O�f�B���N�g���͏����j
SET "TARGET_DIR=%~1"
SET "EXCLUDE_DIR=%~2"

REM ���O�f�B���N�g���̃p�X�𐳋K��
FOR %%A IN ("%EXCLUDE_DIR%") DO SET "EXCLUDE_DIR=%%~fA"

REM ���O�f�B���N�g���Ƃ��̃T�u�f�B���N�g�����̃t�@�C�������O���č폜
FOR /F "delims=" %%A IN ('dir /B /A-D "%TARGET_DIR%"') DO (
    SET "FILE_PATH=%TARGET_DIR%\%%A"
    SETLOCAL ENABLEDELAYEDEXPANSION
    SET "FILE_PATH=!FILE_PATH!"
    IF /I NOT "!FILE_PATH!"=="!EXCLUDE_DIR!" (
        CALL :LogAndEcho "�폜��: !FILE_PATH!"
        del /Q "!FILE_PATH!" >> "%LOG_FILE%" 2>&1
    )
    ENDLOCAL
)

REM ���O�f�B���N�g�����c���ăT�u�f�B���N�g�����폜
FOR /F "delims=" %%A IN ('dir /B /AD "%TARGET_DIR%"') DO (
    SET "DIR_PATH=%TARGET_DIR%\%%A"
    SETLOCAL ENABLEDELAYEDEXPANSION
    SET "DIR_PATH=!DIR_PATH!"
    IF /I NOT "!DIR_PATH!"=="!EXCLUDE_DIR!" (
        CALL :LogAndEcho "�폜��: !DIR_PATH!"
        rmdir /S /Q "!DIR_PATH!" >> "%LOG_FILE%" 2>&1
    )
    ENDLOCAL
)

EXIT /B

:RemoveDir
REM �w�肳�ꂽ�f�B���N�g�����폜
SET "DIR_TO_REMOVE=%~1"
IF EXIST "%DIR_TO_REMOVE%" (
    CALL :LogAndEcho "�폜��: %DIR_TO_REMOVE%"
    rmdir /S /Q "%DIR_TO_REMOVE%" >> "%LOG_FILE%" 2>&1
    IF %ERRORLEVEL% NEQ 0 (
        CALL :LogAndEcho "�f�B���N�g���̍폜���ɃG���[���������܂���: %DIR_TO_REMOVE%"
        EXIT /B 1
    )
    CALL :LogAndEcho "�f�B���N�g�����폜����܂���: %DIR_TO_REMOVE%"
)
EXIT /B
