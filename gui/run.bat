@echo off
echo ========================================
echo MindFull - Screen Time Tracker
echo Compiling...
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
echo ========================================
echo Compilation successful!
echo Running application...
echo ========================================
echo.

cd ..
java -cp "bin;lib/mysql-connector.jar" tubes_a11.Main

pause
