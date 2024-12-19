#!/bin/bash

# Créer les répertoires bin/Controller, bin/Vue, bin/Model s'ils n'existent pas
mkdir -p bin/Controller
mkdir -p bin/Vue
mkdir -p bin/Model

# Compiler tous les fichiers Java et placer les fichiers .class dans leurs répertoires respectifs
javac -d bin -cp "lib/*" src/Controller/*.java src/Vue/*.java src/Model/*.java

# Exécuter la classe principale (Controller) à partir de bin/Controller
java -cp "bin:lib/*" Controller.ImageController
