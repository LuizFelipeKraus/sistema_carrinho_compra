@echo off

for %%i in ("%~dp0.") do set "CURRENT_DIR=%%~fi"

set "EUREKA_DIR=%CURRENT_DIR%\eurekaserver"
set "NOTIFICATION_DIR=%CURRENT_DIR%\notification"
set "ORDER_DIR=%CURRENT_DIR%\order"
set "PRODUCT_DIR=%CURRENT_DIR%\product"
set "AUTH_DIR=%CURRENT_DIR%\auth"

echo Iniciando o EUREKA Server...
start cmd /c "cd /d %EUREKA_DIR% && mvn spring-boot:run"

echo Iniciando o NOTIFICATION Server...
start cmd /c "cd /d %NOTIFICATION_DIR% && mvn spring-boot:run"

echo Iniciando o ORDER Server...
start cmd /c "cd /d %ORDER_DIR% && mvn spring-boot:run"

echo Iniciando o PRODUCT Server...
start cmd /c "cd /d %PRODUCT_DIR% && mvn spring-boot:run"

echo Iniciando o AUTH Server...
start cmd /c "cd /d %AUTH_DIR% && mvn spring-boot:run"
