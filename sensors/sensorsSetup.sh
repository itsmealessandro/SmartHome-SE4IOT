#!/bin/bash

sleep 10

cd ./sens/

echo -e "\e[1;33m activating test sensor \e[0m"
echo "---------------------------------------------------------------------"
java -cp ./org.eclipse.paho.client.mqttv3-1.2.0.jar ./MyclientSample.java
echo -e "\e[1;33m test sensor activated \e[0m"

echo "---------------------------------------------------------------------"
echo -e "\e[1;33m activating sensor1 \e[0m"
echo "---------------------------------------------------------------------"
java -cp ./org.eclipse.paho.client.mqttv3-1.2.0.jar ./Sensor1.java
echo "---------------------------------------------------------------------"
echo -e "\e[1;33m sensor1 activated \e[0m"
echo "---------------------------------------------------------------------"
echo "---------------------------------------------------------------------"
echo "---------------------------------------------------------------------"
echo -e "\e[1;33m All sensors activated \e[0m"
