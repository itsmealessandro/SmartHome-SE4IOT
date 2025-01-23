#!bin/bash

mosquitto &

java -cp org.eclipse.paho.client.mqttv3-1.2.0.jar Myclient.java &

java -cp org.eclipse.paho.client.mqttv3-1.2.0.jar MyclientSample.java
