@ECHO OFF
SETLOCAL

SET MAVEN_PROJECTBASEDIR=%~dp0
SET MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR:~0,-1%

SET WRAPPER_JAR=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar
SET WRAPPER_PROPS=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties

IF EXIST "%WRAPPER_JAR%" GOTO execute

ECHO Descargando Maven Wrapper 3.3.2...
powershell -Command "& { [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar' -OutFile '%WRAPPER_JAR%' }"
IF NOT EXIST "%WRAPPER_JAR%" (
    ECHO ERROR: No se pudo descargar maven-wrapper.jar
    ECHO Instala Maven manualmente: https://maven.apache.org/download.cgi
    EXIT /B 1
)

:execute
IF NOT "%JAVA_HOME%"=="" (
    SET JAVA_CMD="%JAVA_HOME%\bin\java"
) ELSE (
    SET JAVA_CMD=java
)

%JAVA_CMD% %MAVEN_OPTS% ^
  -classpath "%WRAPPER_JAR%" ^
  "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" ^
  org.apache.maven.wrapper.MavenWrapperMain %MAVEN_CONFIG% %*

IF %ERRORLEVEL% NEQ 0 EXIT /B %ERRORLEVEL%
ENDLOCAL
