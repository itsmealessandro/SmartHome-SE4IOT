#!/bin/bash

# NOTE: Give enough time for Node-RED to activate
sleep 5

cd ./sens/

ls

#compiling
echo -e "\e[1;33m compiling sensors \e[0m"
javac -cp ./org.eclipse.paho.client.mqttv3-1.2.0.jar ./DynamicSensor.java

echo -e "\e[1;33m Activating BedRoom Light Sensor \e[0m"
java -cp .:./org.eclipse.paho.client.mqttv3-1.2.0.jar DynamicSensor SmartHome/bedroom/light 1 &

PID1=$!
echo $PID1

echo -e "\e[1;33m Activating Livingroom Temperature Sensor \e[0m"
java -cp .:./org.eclipse.paho.client.mqttv3-1.2.0.jar DynamicSensor SmartHome/livingRoom/temperature 2 &

PID2=$!
echo $PID2

echo -e "\e[1;33m Activating Livingroom Light Sensor \e[0m"
java -cp .:./org.eclipse.paho.client.mqttv3-1.2.0.jar DynamicSensor SmartHome/livingRoom/light 3 &

PID3=$!
echo $PID3

echo -e "\e[1;33m Activating BedRoom Temperature Sensor \e[0m"
java -cp .:./org.eclipse.paho.client.mqttv3-1.2.0.jar DynamicSensor SmartHome/bedroom/temperature 4 &

PID4=$!
echo $PID4

#echo -e "\e[1;33m Activating test sensor \e[0m"
#echo "---------------------------------------------------------------------"
#java -cp ./org.eclipse.paho.client.mqttv3-1.2.0.jar ./MyclientSample.java &
#PID1=$!
#
#echo -e "\e[1;33m Activating Livingroom Temperature Sensor \e[0m"
#echo "---------------------------------------------------------------------"
#java -cp ./org.eclipse.paho.client.mqttv3-1.2.0.jar ./SensLivTemp.java &
#PID2=$!
#
#echo -e "\e[1;33m Activating BedRoom Temperature Sensor \e[0m"
#echo "---------------------------------------------------------------------"
#java -cp ./org.eclipse.paho.client.mqttv3-1.2.0.jar ./SensBedTemp.java &
#PID3=$!
#
#echo -e "\e[1;33m Activating Livingroom Light Sensor \e[0m"
#echo "---------------------------------------------------------------------"
#java -cp ./org.eclipse.paho.client.mqttv3-1.2.0.jar ./SensLivLight.java &
#PID4=$!
#
#echo -e "\e[1;33m Activating Livingroom Light Sensor \e[0m"
#echo "---------------------------------------------------------------------"
#java -cp ./org.eclipse.paho.client.mqttv3-1.2.0.jar ./SensBedLight.java &
#PID5=$!
#
#echo "---------------------------------------------------------------------"
#echo -e "\e[1;33m All sensors activated \e[0m"

# WARNING: Begore shutting down this
# script and the related container we have to wait that both clients have finished
wait $PID1 $PID2 $PID3 $PID4

echo -e "\e[1;31m Sensors container script is over \e[0m"
