#!/bin/bash

# NOTE: Give enough time for Node-RED to activate
sleep 5

cd ./sens/

echo -e "\e[1;33m Activating test sensor \e[0m"
echo "---------------------------------------------------------------------"
java -cp ./org.eclipse.paho.client.mqttv3-1.2.0.jar ./MyclientSample.java &
PID1=$!

echo -e "\e[1;33m Activating sensor1 \e[0m"
echo "---------------------------------------------------------------------"
java -cp ./org.eclipse.paho.client.mqttv3-1.2.0.jar ./Sensor1.java &
PID2=$!

echo -e "\e[1;33m Activating sensor2 \e[0m"
echo "---------------------------------------------------------------------"
java -cp ./org.eclipse.paho.client.mqttv3-1.2.0.jar ./Sensor2.java &
PID3=$!

echo "---------------------------------------------------------------------"
echo -e "\e[1;33m All sensors activated \e[0m"

# WARNING: Begore shutting down this
# script and the related container we have to wait that both clients have finished
wait $PID1 $PID2 $PID3

echo -e "\e[1;31m Sensors container script is over \e[0m"
