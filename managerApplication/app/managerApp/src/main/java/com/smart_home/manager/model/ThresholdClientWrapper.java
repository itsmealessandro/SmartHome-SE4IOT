package com.smart_home.manager.model;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

public class ThresholdClientWrapper {

  private String topic;
  MqttClient threshold_client;
  MqttConnectOptions connOpts;
  Threshold bindedThreshold;

  public String getTopic() {
    return topic;
  }

  public void setTopic(String topic) {
    this.topic = topic;
  }

  public MqttClient getThreshold_client() {
    return threshold_client;
  }

  public void setThreshold_client(MqttClient threshold_client) {
    this.threshold_client = threshold_client;
  }

  public MqttConnectOptions getConnOpts() {
    return connOpts;
  }

  public void setConnOpts(MqttConnectOptions connOpts) {
    this.connOpts = connOpts;
  }

  public Threshold getBindedThreshold() {
    return bindedThreshold;
  }

  public void setBindedThreshold(Threshold bindedThresholds) {
    this.bindedThreshold = bindedThresholds;
  }

}
