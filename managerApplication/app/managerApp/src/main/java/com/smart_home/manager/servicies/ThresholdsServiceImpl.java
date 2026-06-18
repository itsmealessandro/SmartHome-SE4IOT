package com.smart_home.manager.servicies;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

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
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.smart_home.manager.model.Sensor;
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

  private volatile boolean isConnected = false;

  File thresholdsFile = new File("/simulated_env/thresholds.json");
  File envFile = new File("/simulated_env/env.json");

  // setup thresholds info
  public ThresholdsServiceImpl() {

    persistence = new MemoryPersistence();
    CONN_OPT = new MqttConnectOptions();
    CONN_OPT.setCleanSession(true);
    CONN_OPT.setAutomaticReconnect(true);
    CONN_OPT.setConnectionTimeout(30);
    CONN_OPT.setKeepAliveInterval(60);
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

    List<Threshold> thresholds = mapper.readValue(
        thresholdsFile,
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

    mapper.writerWithDefaultPrettyPrinter().writeValue(thresholdsFile, thresholds);

    System.out.println("[SERVER] Thresholds aggiornati:");
    for (Threshold threshold : thresholds) {
      System.out.println("Room: " + threshold.getRoom()
          + ", Sensor Type: " + threshold.getSensorType()
          + ", Value: " + threshold.getValue());
    }

    return thresholds;
  }

  @Override
  public List<Threshold> addThreshold(Threshold threshold) throws IOException {
    threshold.setRoom(threshold.getRoom().trim().toLowerCase());
    threshold.setSensorType(threshold.getSensorType().trim().toLowerCase());
    List<Threshold> thresholds = new ArrayList<>(getThresholds());
    if (thresholds.contains(threshold)) {
      throw new IllegalArgumentException("Sensor '" + threshold.getSensorType() + "' in room '" + threshold.getRoom() + "' already exists!");
    }
    thresholds.add(threshold);
    updateThresholds(thresholds);
    updateEnvValue(threshold);
    publishBootstrapReading(threshold);
    return thresholds;
  }

  private void updateEnvValue(Threshold threshold) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode root = (ObjectNode) mapper.readTree(envFile);
    ObjectNode room = root.with(threshold.getRoom());
    com.fasterxml.jackson.databind.JsonNode sensorNode = room.get(threshold.getSensorType());
    if (sensorNode != null && sensorNode.isObject()) {
        ((ObjectNode) sensorNode).put("value", Math.round(threshold.getValue()));
    } else {
        ObjectNode newNode = mapper.createObjectNode();
        newNode.put("value", Math.round(threshold.getValue()));
        newNode.put("enabled", true);
        room.set(threshold.getSensorType(), newNode);
    }
    mapper.writerWithDefaultPrettyPrinter().writeValue(envFile, root);
  }

  private void publishBootstrapReading(Threshold threshold) {
    try {
      if (!isConnected || !MQTT_CLIENT.isConnected()) {
        MQTT_CLIENT.connect(CONN_OPT);
        isConnected = true;
      }
      String topic = "SmartHome/" + threshold.getRoom() + "/" + threshold.getSensorType();
      MqttMessage message = new MqttMessage(String.valueOf(threshold.getValue()).getBytes());
      message.setQos(QOS);
      MQTT_CLIENT.publish(topic, message);
      System.out.println("[MQTT] Bootstrap sensor published to " + topic);
    } catch (MqttException e) {
      System.out.println("[MQTT] Bootstrap publish failed: " + e.getMessage());
    }
  }

  @Override
  @Scheduled(fixedRate = 5000)
  public void publishThresholdsMQTT() {

    try {
      if (!isConnected || !MQTT_CLIENT.isConnected()) {
        System.out.println("[MQTT] Connecting to broker...");
        MQTT_CLIENT.connect(CONN_OPT);
        isConnected = true;
        System.out.println("[MQTT] Connected successfully");
      }

      List<Threshold> thresholds = getThresholds();

      for (Threshold threshold : thresholds) {

        String content = String.valueOf(threshold.getValue());
        MqttMessage message = new MqttMessage(content.getBytes());

        message.setQos(QOS);
        message.setPayload(content.getBytes());
        String topic = TOPIC_DOMAIN + "/" + threshold.getRoom() + "/" + threshold.getSensorType();
        MQTT_CLIENT.publish(topic, message);
        System.out.println("[MQTT] Published to " + topic + ": " + content);

      }
    } catch (MqttException | IOException e) {
      System.out.println("[MQTT] Error: " + e.getMessage());
      isConnected = false;
      try {
        if (MQTT_CLIENT.isConnected()) {
          MQTT_CLIENT.disconnect();
        }
      } catch (MqttException ex) {
        System.out.println("[MQTT] Error disconnecting");
      }
    }
  }

  @Override
  public List<Sensor> getSensors() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(envFile);
    List<Sensor> sensors = new ArrayList<>();

    if (root != null && root.isObject()) {
        java.util.Iterator<java.util.Map.Entry<String, com.fasterxml.jackson.databind.JsonNode>> rooms = root.fields();
        while (rooms.hasNext()) {
            java.util.Map.Entry<String, com.fasterxml.jackson.databind.JsonNode> roomEntry = rooms.next();
            String roomName = roomEntry.getKey();
            com.fasterxml.jackson.databind.JsonNode roomNode = roomEntry.getValue();

            if (roomNode != null && roomNode.isObject()) {
                java.util.Iterator<java.util.Map.Entry<String, com.fasterxml.jackson.databind.JsonNode>> roomSensors = roomNode.fields();
                while (roomSensors.hasNext()) {
                    java.util.Map.Entry<String, com.fasterxml.jackson.databind.JsonNode> sensorEntry = roomSensors.next();
                    String sensorType = sensorEntry.getKey();
                    com.fasterxml.jackson.databind.JsonNode sensorNode = sensorEntry.getValue();

                    boolean enabled = true;
                    if (sensorNode != null && sensorNode.isObject() && sensorNode.has("enabled")) {
                        enabled = sensorNode.get("enabled").asBoolean(true);
                    }

                    String health = enabled ? "Good" : "Offline";
                    sensors.add(new Sensor(roomName, sensorType, health, enabled));
                }
            }
        }
    }
    return sensors;
  }

  @Override
  public void toggleSensorStatus(String room, String sensorType, boolean enabled) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    if (!envFile.exists()) return;

    ObjectNode root = (ObjectNode) mapper.readTree(envFile);
    ObjectNode roomNode = (ObjectNode) root.get(room);
    if (roomNode != null) {
        com.fasterxml.jackson.databind.JsonNode sensorNode = roomNode.get(sensorType);
        if (sensorNode != null) {
            int value = 0;
            if (sensorNode.isObject()) {
                value = sensorNode.path("value").asInt(0);
            } else {
                value = sensorNode.asInt();
            }

            ObjectNode newSensorNode = mapper.createObjectNode();
            newSensorNode.put("value", value);
            newSensorNode.put("enabled", enabled);

            roomNode.set(sensorType, newSensorNode);
            mapper.writerWithDefaultPrettyPrinter().writeValue(envFile, root);
        }
    }
  }

}
