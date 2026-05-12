@ECHO OFF
ECHO ═══════════════════════════════════════
ECHO   Cinema Monarca — Iniciar servidor
ECHO ═══════════════════════════════════════
ECHO.

REM ── Intentar con Maven del sistema primero ──
WHERE mvn >NUL 2>&1
IF %ERRORLEVEL% EQU 0 (
    ECHO Usando Maven del sistema...
    mvn spring-boot:run -f pom.xml
    GOTO :EOF
)

REM ── Intentar con Maven de IntelliJ ──
SET IDEA_MVN="C:\Program Files\JetBrains\IntelliJ IDEA 2026.1.1\plugins\maven\lib\maven3\bin\mvn.cmd"
IF EXIST %IDEA_MVN% (
    ECHO Usando Maven de IntelliJ...
    %IDEA_MVN% spring-boot:run -f pom.xml
    GOTO :EOF
)

REM ── Intentar descargar Maven Wrapper 3.9.9 ──
ECHO Descargando Maven 3.9.9...
SET WRAPPER_JAR=.mvn\wrapper\maven-wrapper.jar
powershell -Command "& { [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar' -OutFile '%WRAPPER_JAR%' }"
IF EXIST "%WRAPPER_JAR%" (
    java -classpath "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain spring-boot:run
) ELSE (
    ECHO.
    ECHO ERROR: No se encontro Maven.
    ECHO Opciones:
    ECHO  1. En IntelliJ: Run - Edit Configurations - + - Spring Boot - CinemaMonarcaApplication
    ECHO  2. Instalar Maven: https://maven.apache.org/download.cgi
    PAUSE
)
