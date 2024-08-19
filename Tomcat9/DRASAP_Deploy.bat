@echo off
REM クリーン
cls

REM ログファイルのディレクトリと名前を設定
SET "LOG_DIR=D:\Tomcat9\DRASAP\logs"
IF NOT EXIST "%LOG_DIR%" (
    mkdir "%LOG_DIR%"
)

REM タイムスタンプを取得してログファイル名を設定
FOR /F "tokens=2 delims==" %%A IN ('wmic os get localdatetime /value') DO SET "DT=%%A"
SET "LOG_FILE=%LOG_DIR%\DRASAP_Deploy-%DT:~0,4%%DT:~4,2%%DT:~6,2%%DT:~8,2%%DT:~10,2%%DT:~12,2%.log"

REM 開始時刻を取得
SET "START_DATE=%DT:~0,4%-%DT:~4,2%-%DT:~6,2%"
SET "START_TIME=%DT:~8,2%:%DT:~10,2%:%DT:~12,2%"
CALL :LogAndEcho "%START_DATE% %START_TIME%に配置バッチ処理が開始しました。"

REM バッチファイルが存在するディレクトリを取得
SET "SOURCE_DIR=%~dp0DRASAP"
SET "DEST_DIR=D:\Tomcat9\DRASAP"

REM 処理前のクリーンアップ
CALL :CleanUp "%DEST_DIR%" "%LOG_DIR%"
CALL :RemoveDir "D:\Tomcat9\work\Catalina\localhost\DRASAP"
CALL :RemoveDir "D:\Tomcat9\webapps\DRASAP"

REM ソースディレクトリが存在するか確認
IF NOT EXIST "%SOURCE_DIR%" (
    CALL :LogAndEcho "コピー元のフォルダが見つかりません: %SOURCE_DIR%"
    EXIT /B 1
)

REM 目的地ディレクトリが存在するか確認し、存在しない場合は作成
IF NOT EXIST "%DEST_DIR%" (
    CALL :LogAndEcho "コピー先のフォルダが存在しません。作成します: %DEST_DIR%"
    mkdir "%DEST_DIR%"
)

REM DRASAPディレクトリをコピー（既存ファイルを上書き）
xcopy /E /I /Y "%SOURCE_DIR%" "%DEST_DIR%" >> "%LOG_FILE%" 2>&1
IF %ERRORLEVEL% NEQ 0 (
    CALL :LogAndEcho "ファイルのコピー中にエラーが発生しました。"
    EXIT /B 1
)

REM .warファイルのパスを設定
SET "WAR_FILE=%DEST_DIR%\war\DRASAP.war"

REM コピー先ディレクトリを設定
SET "WEBAPPS_DIR=D:\Tomcat9\webapps"

REM コピー先ディレクトリが存在するか確認し、存在しない場合は作成
IF NOT EXIST "%WEBAPPS_DIR%" (
    CALL :LogAndEcho "コピー先ディレクトリが存在しません。作成します: %WEBAPPS_DIR%"
    mkdir "%WEBAPPS_DIR%"
)

REM .warファイルをコピー（既存ファイルを上書き）
IF EXIST "%WAR_FILE%" (
    CALL :LogAndEcho "コピー中: %WAR_FILE% -> %WEBAPPS_DIR%"
    copy /Y "%WAR_FILE%" "%WEBAPPS_DIR%" >> "%LOG_FILE%" 2>&1
    IF %ERRORLEVEL% NEQ 0 (
        CALL :LogAndEcho ".warファイルのコピー中にエラーが発生しました。"
        EXIT /B 1
    )
    CALL :LogAndEcho "コピーが完了しました。"
) ELSE (
    CALL :LogAndEcho "DRASAP.warファイルが見つかりません: %WAR_FILE%"
)

REM warディレクトリのパスを設定
SET "WAR_DIR=%DEST_DIR%\war"

REM warディレクトリが存在するか確認し、削除
IF EXIST "%WAR_DIR%" (
    CALL :LogAndEcho "warディレクトリを削除中: %WAR_DIR%"
    rmdir /S /Q "%WAR_DIR%" >> "%LOG_FILE%" 2>&1
    IF %ERRORLEVEL% NEQ 0 (
        CALL :LogAndEcho "warディレクトリの削除中にエラーが発生しました。"
        EXIT /B 1
    )
    CALL :LogAndEcho "warディレクトリが削除されました。"
) ELSE (
    CALL :LogAndEcho "warディレクトリが見つかりません: %WAR_DIR%"
)

REM .gitkeepファイルを削除
CALL :LogAndEcho ".gitkeepファイルを削除中..."
FOR /R "%DEST_DIR%" %%F IN (.gitkeep) DO (
    IF EXIST "%%F" (
        CALL :LogAndEcho "削除中: %%F"
        del "%%F" >> "%LOG_FILE%" 2>&1
        IF %ERRORLEVEL% NEQ 0 (
            CALL :LogAndEcho ".gitkeepファイルの削除中にエラーが発生しました: %%F"
        )
    )
)

CALL :LogAndEcho ".gitkeepファイルの削除が完了しました。"

REM 終了時刻を取得
FOR /F "tokens=2 delims==" %%A IN ('wmic os get localdatetime /value') DO SET "DT=%%A"
SET "END_DATE=%DT:~0,4%-%DT:~4,2%-%DT:~6,2%"
SET "END_TIME=%DT:~8,2%:%DT:~10,2%:%DT:~12,2%"
CALL :LogAndEcho "%END_DATE% %END_TIME%に配置バッチ処理が完了しました。"

REM 終了
PAUSE
EXIT /B

:LogAndEcho
REM メッセージをログファイルとコンソールに出力
echo %* >> "%LOG_FILE%"
echo %*
EXIT /B

:CleanUp
REM 指定されたディレクトリ内のファイルとサブディレクトリを削除（ただし指定された除外ディレクトリは除く）
SET "TARGET_DIR=%~1"
SET "EXCLUDE_DIR=%~2"

REM 除外ディレクトリのパスを正規化
FOR %%A IN ("%EXCLUDE_DIR%") DO SET "EXCLUDE_DIR=%%~fA"

REM 除外ディレクトリとそのサブディレクトリ内のファイルを除外して削除
FOR /F "delims=" %%A IN ('dir /B /A-D "%TARGET_DIR%"') DO (
    SET "FILE_PATH=%TARGET_DIR%\%%A"
    SETLOCAL ENABLEDELAYEDEXPANSION
    SET "FILE_PATH=!FILE_PATH!"
    IF /I NOT "!FILE_PATH!"=="!EXCLUDE_DIR!" (
        CALL :LogAndEcho "削除中: !FILE_PATH!"
        del /Q "!FILE_PATH!" >> "%LOG_FILE%" 2>&1
    )
    ENDLOCAL
)

REM 除外ディレクトリを残してサブディレクトリを削除
FOR /F "delims=" %%A IN ('dir /B /AD "%TARGET_DIR%"') DO (
    SET "DIR_PATH=%TARGET_DIR%\%%A"
    SETLOCAL ENABLEDELAYEDEXPANSION
    SET "DIR_PATH=!DIR_PATH!"
    IF /I NOT "!DIR_PATH!"=="!EXCLUDE_DIR!" (
        CALL :LogAndEcho "削除中: !DIR_PATH!"
        rmdir /S /Q "!DIR_PATH!" >> "%LOG_FILE%" 2>&1
    )
    ENDLOCAL
)

EXIT /B

:RemoveDir
REM 指定されたディレクトリを削除
SET "DIR_TO_REMOVE=%~1"
IF EXIST "%DIR_TO_REMOVE%" (
    CALL :LogAndEcho "削除中: %DIR_TO_REMOVE%"
    rmdir /S /Q "%DIR_TO_REMOVE%" >> "%LOG_FILE%" 2>&1
    IF %ERRORLEVEL% NEQ 0 (
        CALL :LogAndEcho "ディレクトリの削除中にエラーが発生しました: %DIR_TO_REMOVE%"
        EXIT /B 1
    )
    CALL :LogAndEcho "ディレクトリが削除されました: %DIR_TO_REMOVE%"
)
EXIT /B
