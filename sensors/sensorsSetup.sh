#!/bin/bash

# NOTE: Give enough time for Node-RED to activate
sleep 5

cd ./sens/

ls

# Definire la variabile classpath per evitare ripetizioni
LIBRARY_PATH="org.eclipse.paho.client.mqttv3-1.2.0.jar:jackson-core-2.18.2.jar:jackson-databind-2.18.2.jar:jackson-annotations-2.18.2.jar"

# Compilazione
echo -e "\e[1;33m compiling sensors \e[0m"
javac -cp "$LIBRARY_PATH" ./CreateSensor.java

echo -e "\e[1;33m Activating BedRoom Light Sensor \e[0m"
java -cp ".:$LIBRARY_PATH" CreateSensor SmartHome/bedroom/light s1 &

PID1=$!
echo $PID1

echo -e "\e[1;33m Activating Livingroom Temperature Sensor \e[0m"
java -cp ".:$LIBRARY_PATH" CreateSensor SmartHome/livingRoom/temperature s2 &

PID2=$!
echo $PID2

echo -e "\e[1;33m Activating Livingroom Light Sensor \e[0m"
java -cp ".:$LIBRARY_PATH" CreateSensor SmartHome/livingRoom/light s3 &

PID3=$!
echo $PID3

echo -e "\e[1;33m Activating BedRoom Temperature Sensor \e[0m"
java -cp ".:$LIBRARY_PATH" CreateSensor SmartHome/bedroom/temperature s4 &

PID4=$!
echo $PID4

# WARNING: Before shutting down this script and the related container, we have to wait that both clients have finished
wait $PID1 $PID2 $PID3 $PID4

echo -e "\e[1;31m Sensors container script is over \e[0m"
