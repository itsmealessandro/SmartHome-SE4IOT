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
    File file = new File("/simulated_env/env.json");
    ObjectNode root = (ObjectNode) mapper.readTree(file);
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
    mapper.writerWithDefaultPrettyPrinter().writeValue(file, root);
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
        MQTT_CLIENT.setCallback(new org.eclipse.paho.client.mqttv3.MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) { isConnected = false; }
            @Override
            public void messageArrived(String topic, org.eclipse.paho.client.mqttv3.MqttMessage message) throws Exception {
                if (topic.startsWith("SmartHome/alerts/")) {
                    String[] parts = topic.split("/");
                    if (parts.length == 4) {
                        String room = parts[2];
                        String sensorType = parts[3];
                        String alertMsg = new String(message.getPayload());
                        updateSensorAlert(room, sensorType, alertMsg);
                    }
                }
            }
            @Override
            public void deliveryComplete(org.eclipse.paho.client.mqttv3.IMqttDeliveryToken token) {}
        });
        MQTT_CLIENT.subscribe("SmartHome/alerts/#");
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
    File file = new File("/simulated_env/env.json");
    com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(file);
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
                    String sensorVal = "0";
                    if (sensorNode != null) {
                        if (sensorNode.isObject()) {
                            if (sensorNode.has("enabled")) {
                                enabled = sensorNode.get("enabled").asBoolean(true);
                            }
                            if (sensorNode.has("value")) {
                                sensorVal = sensorNode.get("value").asText();
                            }
                        } else {
                            sensorVal = sensorNode.asText();
                        }
                    }

                    List<String> alertHistory = new ArrayList<>();
                    if (sensorNode != null && sensorNode.isObject() && sensorNode.has("alertHistory") && sensorNode.get("alertHistory").isArray()) {
                        for (com.fasterxml.jackson.databind.JsonNode element : sensorNode.get("alertHistory")) {
                            alertHistory.add(element.asText());
                        }
                    }

                    String health = enabled ? "Good" : "Offline";
                    
                    boolean isActuatorRunning = false;
                    if (!alertHistory.isEmpty()) {
                        String lastLog = alertHistory.get(0);
                        if (lastLog.contains("Exceeded threshold")) {
                            isActuatorRunning = true;
                        }
                    }

                    String deviceName = "Attuatore Generico";
                    String activeStatus = "In funzione ⚙️";
                    String idleStatus = "Standby";

                    if ("temperature".equals(sensorType)) {
                        deviceName = "Condizionatore";
                        activeStatus = "Acceso ❄️";
                        idleStatus = "Spento";
                    } else if ("light".equals(sensorType)) {
                        deviceName = "Tapparelle/Luci";
                        activeStatus = "In funzione 💡";
                        idleStatus = "Spento";
                    } else if ("humidity".equals(sensorType)) {
                        deviceName = "Deumidificatore";
                        activeStatus = "Acceso 💧";
                        idleStatus = "Spento";
                    } else if ("co2".equals(sensorType)) {
                        deviceName = "Finestre (Smart)";
                        activeStatus = "Aperte 🌬️";
                        idleStatus = "Chiuse";
                    }

                    String actuatorName = deviceName;
                    String actuatorStatus = !enabled ? "Disabilitato" : (isActuatorRunning ? activeStatus : idleStatus);

                    sensors.add(new Sensor(roomName, sensorType, sensorVal, health, enabled, alertHistory, actuatorName, actuatorStatus));
                }
            }
        }
    }
    return sensors;
  }

  private void updateSensorAlert(String room, String sensorType, String alertMsg) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    File file = new File("/simulated_env/env.json");
    if (!file.exists()) return;
    ObjectNode root = (ObjectNode) mapper.readTree(file);
    ObjectNode roomNode = (ObjectNode) root.get(room);
    if (roomNode != null) {
        com.fasterxml.jackson.databind.JsonNode sensorNode = roomNode.get(sensorType);
        if (sensorNode != null) {
            ObjectNode newSensorNode;
            if (sensorNode.isObject()) {
                newSensorNode = (ObjectNode) sensorNode;
            } else {
                newSensorNode = mapper.createObjectNode();
                newSensorNode.put("value", sensorNode.asInt(0));
                newSensorNode.put("enabled", true);
            }
            com.fasterxml.jackson.databind.node.ArrayNode historyArray;
            if (newSensorNode.has("alertHistory") && newSensorNode.get("alertHistory").isArray()) {
                historyArray = (com.fasterxml.jackson.databind.node.ArrayNode) newSensorNode.get("alertHistory");
            } else {
                historyArray = mapper.createArrayNode();
            }
            historyArray.insert(0, alertMsg);
            while (historyArray.size() > 10) {
                historyArray.remove(historyArray.size() - 1);
            }
            newSensorNode.set("alertHistory", historyArray);

            roomNode.set(sensorType, newSensorNode);
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, root);
        }
    }
  }

  @Override
  public void toggleSensorStatus(String room, String sensorType, boolean enabled) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    File file = new File("/simulated_env/env.json");
    if (!file.exists()) return;

    ObjectNode root = (ObjectNode) mapper.readTree(file);
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
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, root);
        }
    }
  }

}
