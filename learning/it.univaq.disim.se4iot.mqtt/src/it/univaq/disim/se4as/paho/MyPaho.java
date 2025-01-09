package it.univaq.disim.se4as.paho;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

public class MyPaho {

  MqttClientPersistence persistence;

  MqttClient mqttClient;

  MqttConnectOptions options;

  /**
   * this constructor defines the persistence and the connection options
   */

  public MyPaho() {
    persistence = new MqttDefaultFilePersistence("/tmp");// persistence store

    try {
      mqttClient = new MqttClient("tcp://localhost:1883", "MQTTSub", persistence);
    } catch (MqttException e) {
      e.printStackTrace();
      System.out.println("Mqtt exception on persistence");
      System.exit(1);
    }

    options = new MqttConnectOptions();

    options.setKeepAliveInterval(480);

    // TODO: study

    /*
     * QoS-0: at most once (fire-and-forget)
     * it is the fastest mode, where the client doesn't wait for an
     * acknowledgement. In case of disconnection or server failure, the
     * message may be lost
     * • QoS-1: deliver at least once
     * The broker stores messages on disk and retries until clients have
     * acknowledged their delivery. (Possibly with duplicates.)
     * • QoS-2: deliver exact once
     * It uses two pairs of exchanges, first to transfer the message and then
     * to ensure only one copy has been received and is being processed.
     * This does make Exactly Once the slower but most reliable QoS setting.
     */
    options.setWill(mqttClient.getTopic("WillTopic"), "something bad happened".getBytes(), 1, true);

    try {

      mqttClient.connect(options);
    } catch (MqttException e) {
      e.printStackTrace();
      System.out.println("Mqtt exception on connection");
      System.exit(1);
    }

    createAndPublishMessage();

    subscribe();
  }

  /**
   * create and publish a mqttmessage
   */
  void createAndPublishMessage() {
    // Message creation
    MqttMessage message = new MqttMessage("my message".getBytes());
    message.setRetained(true);
    message.setQos(2);

    // Topic creation
    MqttTopic topic = mqttClient.getTopic("x");

    try {
      MqttDeliveryToken deliveryToken = topic.publish(message);
    } catch (MqttException e) {
      e.printStackTrace();
      System.out.println("Mqtt exception on delivery");
      System.exit(1);
    }

  }

  void subscribe() {

    try {
      mqttClient.subscribe("x");
    } catch (MqttException e) {
      e.printStackTrace();
      System.out.println("Mqtt exception on delivery");
      System.exit(1);
    }
  }

  public static void main(String[] args) {
    MyPaho paho = new MyPaho();

  }
}
