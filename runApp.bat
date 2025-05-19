@echo off
cd /d "%~dp0"
echo Compiling...
javac -cp "lib/sqlite-jdbc-3.49.1.0.jar;javafx-sdk-17.0.15/lib/*" --module-path javafx-sdk-17.0.15/lib --add-modules javafx.controls,javafx.fxml -d out src\model\*.java src\App.java

echo Running...
java --module-path javafx-sdk-17.0.15/lib --add-modules javafx.controls,javafx.fxml -cp "out;lib/sqlite-jdbc-3.49.1.0.jar" App
pause
