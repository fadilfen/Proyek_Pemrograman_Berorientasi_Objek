@echo off
echo ========================================
echo Compiling MindFull Application...
echo ========================================

cd src
javac -encoding UTF-8 -cp ".;../lib/mysql-connector.jar" -d ../bin tubes_a11/*.java database/*.java

if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Compilation failed!
    pause
    exit /b 1
)

echo.
echo [SUCCESS] Compilation completed!
echo Compiled files in: gui/bin/
pause
