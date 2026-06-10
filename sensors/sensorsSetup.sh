#!/bin/sh

# NOTE: Give enough time for Node-RED to activate
sleep 5

cd ./sens/

ls

# Definire la variabile classpath per evitare ripetizioni
LIBRARY_PATH="org.eclipse.paho.client.mqttv3-1.2.0.jar:jackson-core-2.18.2.jar:jackson-databind-2.18.2.jar:jackson-annotations-2.18.2.jar"

# Compilazione
echo -e "\e[1;33m compiling sensors \e[0m"
javac -cp "$LIBRARY_PATH" ./CreateSensor.java ./DynamicSensors.java

echo -e "\e[1;33m Activating dynamic sensors \e[0m"
java -cp ".:$LIBRARY_PATH" DynamicSensors

echo -e "\e[1;31m Sensors container script is over \e[0m"
