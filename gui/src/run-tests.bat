@echo off
REM Script untuk menjalankan JUnit tests
REM Kelompok A11 - Mental Wellbeing App Testing

echo ========================================
echo   Mental Wellbeing App - JUnit Tests
echo ========================================
echo.

REM Check if JUnit JAR exists
if not exist "junit-platform-console-standalone.jar" (
    echo ERROR: junit-platform-console-standalone.jar not found!
    echo.
    echo Please download from:
    echo https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.0/
    echo.
    pause
    exit /b 1
)

echo Compiling test files...
javac -cp ".;junit-platform-console-standalone.jar;../lib/*" testing/*.java tubes_a11/*.java database/*.java

if errorlevel 1 (
    echo.
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

echo.
echo Running all tests...
echo ----------------------------------------
java -jar junit-platform-console-standalone.jar --class-path ".;../lib/*" --scan-class-path --disable-banner

echo.
echo ========================================
echo   Test Execution Completed
echo ========================================
pause
