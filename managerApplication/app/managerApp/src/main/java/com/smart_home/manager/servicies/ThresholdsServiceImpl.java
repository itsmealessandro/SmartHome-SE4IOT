package com.smart_home.manager.servicies;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart_home.manager.model.Threshold;

@Service
public class ThresholdsServiceImpl implements ThresholdsService {

  private MemoryPersistence persistence;
  private final String MQTT_BROKER = "tcp://broker:1883";
  private final String TOPIC_DOMAIN = "SmartHome/thresholds"; // "/room/Sensor"
  private final String CLIENT_ID = "thresholds_publisher";
  private MqttClient MQTT_CLIENT;
  private final MqttConnectOptions CONN_OPT;
  private final int QOS = 1;

  // setup thresholds info
  public ThresholdsServiceImpl() {

    persistence = new MemoryPersistence();
    CONN_OPT = new MqttConnectOptions();
    CONN_OPT.setCleanSession(true);
    try {
      MQTT_CLIENT = new MqttClient(MQTT_BROKER, CLIENT_ID, persistence);

    } catch (MqttException e) {
      System.out.println("constructor oops...");
      e.printStackTrace();
      System.exit(1);
    }
  }

  @Override
  public List<Threshold> getThresholds() throws StreamReadException, DatabindException, IOException {

    ObjectMapper mapper = new ObjectMapper();

    File file = new File("/simulated_env/thresholds.json");

    List<Threshold> thresholds = mapper.readValue(
        file,
        mapper.getTypeFactory().constructCollectionType(List.class, Threshold.class));

    // Stampa i valori
    for (Threshold threshold : thresholds) {
      System.out.println("Room: " + threshold.getRoom());
      System.out.println("Sensor Type: " + threshold.getSensorType());
      System.out.println("Value: " + threshold.getValue());
      System.out.println("----");
    }
    return thresholds;
  }

  @Override
  public List<Threshold> updateThresholds(List<Threshold> thresholds) throws IOException {
    ObjectMapper mapper = new ObjectMapper();

    File file = new File("/simulated_env/thresholds.json");

    mapper.writerWithDefaultPrettyPrinter().writeValue(file, thresholds);

    System.out.println("[SERVER] Thresholds aggiornati:");
    for (Threshold threshold : thresholds) {
      System.out.println("Room: " + threshold.getRoom()
          + ", Sensor Type: " + threshold.getSensorType()
          + ", Value: " + threshold.getValue());
    }

    return thresholds;
  }

  @Override
  @Scheduled(fixedRate = 5000)
  public void publishThresholdsMQTT() {

    try {
      List<Threshold> thresholds = getThresholds();

      for (Threshold threshold : thresholds) {

        String content = String.valueOf(threshold.getValue());
        MqttMessage message = new MqttMessage(content.getBytes());

        message.setQos(QOS);
        message.setPayload(content.getBytes());
        String topic = TOPIC_DOMAIN + "/" + threshold.getRoom() + "/" + threshold.getSensorType();
        MQTT_CLIENT.connect();
        MQTT_CLIENT.publish(topic, message);
        MQTT_CLIENT.disconnect();

      }
    } catch (MqttException | IOException e) {
      System.out.println("oops scheduled");
      e.printStackTrace();
    }
  }

}
