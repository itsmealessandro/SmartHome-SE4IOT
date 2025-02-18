#!/bin/bash

# NOTE: Give enough time for Node-RED to activate
sleep 5

cd ./act/

echo "I'm in:"
pwd

#compiling
echo -e "\e[1;33m compiling actuators \e[0m"
javac -cp ./org.eclipse.paho.client.mqttv3-1.2.0.jar ./CreateActuator.java

echo -e "\e[1;33m Activating BedRoom lightAct actuator \e[0m"
java -cp .:./org.eclipse.paho.client.mqttv3-1.2.0.jar CreateActuator SmartHome/bedroom/lightAct 1 &

PID1=$!
echo $PID1

echo -e "\e[1;33m Activating Livingroom temperatureAct actuator \e[0m"
java -cp .:./org.eclipse.paho.client.mqttv3-1.2.0.jar CreateActuator SmartHome/livingRoom/temperatureAct 2 &

PID2=$!
echo $PID2

echo -e "\e[1;33m Activating Livingroom lightAct actuator \e[0m"
java -cp .:./org.eclipse.paho.client.mqttv3-1.2.0.jar CreateActuator SmartHome/livingRoom/lightAct 3 &

PID3=$!
echo $PID3

echo -e "\e[1;33m Activating BedRoom temperatureAct actuator \e[0m"
java -cp .:./org.eclipse.paho.client.mqttv3-1.2.0.jar CreateActuator SmartHome/bedroom/temperatureAct 4 &

PID4=$!
echo $PID4

# WARNING: Begore shutting down this
# script and the related container we have to wait that both clients have finished
wait $PID1 $PID2 $PID3 $PID4

echo -e "\e[1;31m actuators container script is over \e[0m"
