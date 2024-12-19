@echo off

REM Créer les répertoires bin\Controller, bin\Vue, bin\Model s'ils n'existent pas
mkdir bin\Controller
mkdir bin\Vue
mkdir bin\Model

REM Définir le chemin vers le fichier .jar de FlatLaf (assurez-vous que le chemin est correct)
set FLATLAF_JAR=lib\flatlaf-2.x.jar

REM Compiler tous les fichiers Java et placer les fichiers .class dans leurs répertoires respectifs
javac -d bin -cp "lib\*;src" src\Controller\*.java src\Vue\*.java src\Model\*.java

REM Exécuter la classe principale (Controller) à partir de bin\Controller
java -cp "bin;%FLATLAF_JAR%" Controller.ImageController

pause
