import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.client.mqttv3.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class CreateActuator {

  private static final String ENV_PATH = "/simulated_env/env.json";
  private static final ObjectMapper MAPPER = new ObjectMapper();

  public static void main(String[] args) {

    if (args.length < 2) {
      System.out.println("Usage: java CreateActuator <topic> <clientId>");
      return;
    }

    String topic = args[0];
    String clientId = args[1];

    if (!topic.matches("^SmartHome/[a-zA-Z0-9_+]+/[a-zA-Z0-9_+]+Act$")) {
      System.out.println("Error: Topic must match pattern SmartHome/<room>/<sensor>Act or use + for wildcards");
      System.out.println("Example: java CreateActuator SmartHome/+/+Act a1");
      return;
    }

    final String ANSI_RESET = "\u001B[0m";
    final String ANSI_GREEN = "\u001B[32m";
    final String ANSI_RED = "\u001B[31m";

    int qos = 1;
    String broker = "tcp://broker:1883";
    MemoryPersistence persistence = new MemoryPersistence();

    System.out.println(ANSI_GREEN + "Actuator activated: " + topic + ANSI_RESET);
    System.out.println("prima del try");

    try {

      System.out.println("inizia try catch");
      // Connessione al broker
      MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
      MqttConnectOptions connOpts = new MqttConnectOptions();
      connOpts.setCleanSession(true);

      sampleClient.setCallback(new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {
          System.out.println("Connessione persa! Tentativo di riconnessione...");
          int maxRetries = 10;
          int retryCount = 0;
          while (!sampleClient.isConnected() && retryCount < maxRetries) {
            try {
              Thread.sleep(5000);
              sampleClient.connect(connOpts);
              sampleClient.subscribe(topic, qos);
              System.out.println("Riconnesso al broker!");
            } catch (Exception e) {
              retryCount++;
              System.out.println("Riconnessione fallita (" + retryCount + "/" + maxRetries + "), riprovo tra 5 secondi...");
            }
          }
          if (!sampleClient.isConnected()) {
            System.out.println("Riconnessione fallita dopo " + maxRetries + " tentativi. Uscita.");
            System.exit(1);
          }
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) {
          String msgStr = message.toString();
          if ("actuator listening".equals(msgStr)) {
            System.out.println("Skipping initial confirmation message");
            return;
          }
          System.out.println(ANSI_GREEN + "Message received from my topic: " + topic);
          System.out.println(ANSI_GREEN + "Message value: " + msgStr);
          try {
            overrideEnv(msgStr, topic);
          } catch (Exception e) {
            e.printStackTrace();
            System.out.println(ANSI_RED + "Error overriding environment");
          }
          System.out.println(ANSI_GREEN + "Overriding environment data");

        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
          // Non usato nel subscriber
        }
      });

      sampleClient.connect(connOpts);
      System.out.println("Connesso al broker: " + broker);

      sampleClient.subscribe(topic, qos);
      System.out.println("Sottoscritto al topic: " + topic);

      Thread.sleep(1000);

      String content = "actuator listening";
      MqttMessage message = new MqttMessage(content.getBytes());
      message.setQos(qos);
      sampleClient.publish(topic, message);
      System.out.println("Messaggio pubblicato: " + content);

      // NOTE: Mantieni il programma in esecuzione per ricevere i messaggi
      while (true) {
        Thread.sleep(1000);
      }

    } catch (Exception e) {
      System.out.println("Errore: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public static void overrideEnv(String arrived_msg, String topic) throws IOException {
    String[] splittedTopic = topic.split("/");
    File jsonFile = new File(ENV_PATH);
    if (!jsonFile.exists()) {
      System.out.println("Errore: Il file JSON " + jsonFile.getAbsolutePath() + " non esiste.");
      return;
    }

    try (RandomAccessFile raf = new RandomAccessFile(jsonFile, "rw");
        FileChannel channel = raf.getChannel();
        FileLock lock = channel.lock()) {

      JsonNode rootNode = MAPPER.readTree(jsonFile);
      String textRoom = splittedTopic[1];
      String textSensAct = splittedTopic[2];
      String textSens = textSensAct.endsWith("Act")
          ? textSensAct.substring(0, textSensAct.length() - 3)
          : textSensAct;

      JsonNode room = rootNode.get(textRoom);
      if (room == null || !room.isObject()) {
        System.out.println("Room not found: " + textRoom);
        return;
      }

      JsonNode sensNode = room.get(textSens);
      if (sensNode == null) {
        System.out.println("value is null");
        return;
      }

      try {
        int intValue = Integer.parseInt(arrived_msg.trim());
        ObjectNode newSensorNode;
        if (sensNode.isObject()) {
          newSensorNode = (ObjectNode) sensNode;
        } else {
          newSensorNode = MAPPER.createObjectNode();
          newSensorNode.put("enabled", true);
        }
        newSensorNode.put("value", intValue);
        addResolutionToHistory(newSensorNode, String.valueOf(intValue));
        ((ObjectNode) room).set(textSens, newSensorNode);
      } catch (NumberFormatException e) {
        ObjectNode newSensorNode;
        if (sensNode.isObject()) {
          newSensorNode = (ObjectNode) sensNode;
        } else {
          newSensorNode = MAPPER.createObjectNode();
          newSensorNode.put("enabled", true);
        }
        newSensorNode.put("value", arrived_msg);
        addResolutionToHistory(newSensorNode, arrived_msg);
        ((ObjectNode) room).set(textSens, newSensorNode);
      }

      File tempFile = new File(ENV_PATH + ".tmp");
      MAPPER.writerWithDefaultPrettyPrinter().writeValue(tempFile, rootNode);
      Files.move(
          tempFile.toPath(),
          jsonFile.toPath(),
          StandardCopyOption.ATOMIC_MOVE,
          StandardCopyOption.REPLACE_EXISTING);

      System.out.println(MAPPER.readTree(jsonFile).toPrettyString());
    }
  }

  private static void addResolutionToHistory(ObjectNode sensNode, String resolvedValue) {
    String resolvedMsg = "[" + new java.util.Date().toLocaleString() + "] \uD83D\uDEE0\uFE0F Resolved by actuator! Value reset to: " + resolvedValue;
    com.fasterxml.jackson.databind.node.ArrayNode historyArray;
    if (sensNode.has("alertHistory") && sensNode.get("alertHistory").isArray()) {
        historyArray = (com.fasterxml.jackson.databind.node.ArrayNode) sensNode.get("alertHistory");
    } else {
        historyArray = MAPPER.createArrayNode();
    }
    historyArray.insert(0, resolvedMsg);
    while (historyArray.size() > 10) {
        historyArray.remove(historyArray.size() - 1);
    }
    sensNode.set("alertHistory", historyArray);
  }
}
