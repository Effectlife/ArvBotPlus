@echo off
FOR /f "tokens=3" %%g in ('java -version 2^>^&1 ^| findstr /i "version"') do (
        SET "JAVA_VERSION=%%g"
)
echo.%JAVA_VERSION%|findstr /C:"11" >nul 2>&1
echo Starting using java %JAVA_VERSION%
if not errorlevel 1 (
   java --module-path javafx-sdk-11.0.2\lib --add-modules javafx.base,javafx.controls,javafx.graphics,javafx.fxml,javafx.media,javafx.swing,javafx.web -jar arvbot_plus.jar
) else (
    java -jar arvbot_plus.jar
)
pause