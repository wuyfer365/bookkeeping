@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM Maven Wrapper for Windows

@echo off
setlocal enabledelayedexpansion

set "WRAPPER_DIR=%~dp0.mvn\wrapper"
set "WRAPPER_JAR=%WRAPPER_DIR%\maven-wrapper.jar"
set "MAVEN_CONFIG=%USERPROFILE%\.m2"
set "JAVA_HOME_OVERRIDE=C:/Program Files/Eclipse Adoptium/jdk-17.0.19.10-hotspot"

if defined JAVA_HOME (
    set "MVNW_JAVA_HOME=%JAVA_HOME%"
) else if exist "%JAVA_HOME_OVERRIDE%" (
    set "MVNW_JAVA_HOME=%JAVA_HOME_OVERRIDE%"
)

if not defined MVNW_JAVA_HOME (
    echo ERROR: JAVA_HOME not set and no JDK found. Please set JAVA_HOME to a JDK 17+ installation.
    exit /b 1
)

echo Using Java: %MVNW_JAVA_HOME%

set "MVNW_CMD=%MVNW_JAVA_HOME%\bin\java.exe"
if not exist "%MVNW_CMD%" (
    echo ERROR: java.exe not found at %MVNW_CMD%
    exit /b 1
)

"%MVNW_CMD%" ^
  -classpath "%WRAPPER_JAR%" ^
  org.apache.maven.wrapper.MavenWrapperMain ^
  %*

endlocal
