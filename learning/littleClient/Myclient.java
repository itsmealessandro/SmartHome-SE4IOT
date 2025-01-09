package it.univaq.disim.se4as.paho;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

public class Myclient {

  public static void main(String[] args) {

    try {
      MqttClientPersistence persistence;

      MqttClient mqttClient;

      MqttConnectOptions options;

      persistence = new MqttDefaultFilePersistence("/tmp");// persistence store

      mqttClient = new MqttClient("tcp://localhost:1883", "MQTTSub", persistence);

      options = new MqttConnectOptions();

      options.setKeepAliveInterval(480);
      options.setWill(mqttClient.getTopic("WillTopic"), "something bad happened".getBytes(), 1, true);

      mqttClient.connect(options);

      // createAndPublishMessage();

      // subscribe();

    } catch (MqttException e) {
      e.printStackTrace();
      System.out.println("Mqtt exception on persistence");
      System.exit(1);
    }
  }

  /*
   * void createAndPublishMessage() {
   * // Message creation
   * MqttMessage message = new MqttMessage("my message".getBytes());
   * message.setRetained(true);
   * message.setQos(2);
   * 
   * // Topic creation
   * MqttTopic topic = mqttClient.getTopic("x");
   * 
   * try {
   * MqttDeliveryToken deliveryToken = topic.publish(message);
   * } catch (MqttException e) {
   * e.printStackTrace();
   * System.out.println("Mqtt exception on delivery");
   * System.exit(1);
   * }
   * 
   * }
   * 
   * void subscribe() {
   * 
   * try {
   * mqttClient.subscribe("x");
   * } catch (MqttException e) {
   * e.printStackTrace();
   * System.out.println("Mqtt exception on delivery");
   * System.exit(1);
   * }
   * }
   */
}
