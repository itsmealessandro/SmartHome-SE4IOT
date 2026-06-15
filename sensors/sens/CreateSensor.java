import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.util.Random;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class CreateSensor {

  public static void main(String[] args) {

    if (args.length < 2) {
      System.out.println("Usage: java CreateSensor <topic> <clientId>");
      System.out.println("Example: java CreateSensor SmartHome/bedroom/light s1");
      return;
    }

    String topic = args[0];
    String clientId = args[1];

    if (!topic.matches("^SmartHome/[a-zA-Z0-9_]+/[a-zA-Z0-9_]+$")) {
      System.out.println("Error: Topic must match pattern SmartHome/<room>/<sensor>");
      return;
    }

    int qos = 1;
    String broker = "tcp://broker:1883";
    MemoryPersistence persistence = new MemoryPersistence();
    boolean active = true;
    final String ANSI_RESET = "\u001B[0m";
    final String ANSI_RED = "\u001B[31m";
    final String ANSI_GREEN = "\u001B[32m";

    System.out.println(ANSI_GREEN + "Dynamic sensor activated: " + topic + ANSI_RESET);

    while (active) {
      try {
        MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        // System.out.println("------------------------------------------------------------");
        // System.out.println("Connecting to broker: " + broker);
        sampleClient.connect(connOpts);
        // System.out.println("Connected");
        // System.out.println("------------------------------------------------------------");
        Thread.sleep(2000);

        Random random = new Random();
        Set<String> knownSensors = new HashSet<>();

        while (active) {
          // NOTE: JSON

          ObjectMapper objectMapper = new ObjectMapper();

          File jsonFile = new File("/simulated_env/env.json");
          if (!jsonFile.exists()) {
            System.out.println("Errore: Il file JSON " + jsonFile.getAbsolutePath() + " non esiste.");
            return;
          }

          String[] splittedTopic = topic.split("/");

          JsonNode rootNode;
          try (RandomAccessFile raf = new RandomAccessFile(jsonFile, "r");
              FileChannel channel = raf.getChannel();
              FileLock lock = channel.lock(0, Long.MAX_VALUE, true)) {
            rootNode = objectMapper.readTree(jsonFile);
          }

          JsonNode room = rootNode.get(splittedTopic[1]);
          if (room == null) {
            try { Thread.sleep(1000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
            continue;
          }

          JsonNode sensorNode = room.get(splittedTopic[2]);
          if (sensorNode == null) {
            try { Thread.sleep(1000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
            continue;
          }

          if (sensorNode.isObject() && sensorNode.has("enabled") && !sensorNode.path("enabled").asBoolean(true)) {
            try { Thread.sleep(1000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
            continue;
          }

          int value = sensorNode.isObject() ? sensorNode.path("value").asInt(0) : sensorNode.asInt();

          int noise = random.nextInt(-3, 4);
          int noisyValue = value + noise;
          if (noisyValue < 0) noisyValue = 0;

          String content = null;
          int alertProb = random.nextInt(0, 100);
          if (alertProb <= 1) {

            System.out.println(ANSI_RED + " ALERT VALUE sensor:" + args[0]);
            content = String.valueOf(random.nextInt(6, 10));

          } else {
            content = String.valueOf(noisyValue);
          }

          Thread.sleep(500);

          MqttMessage message = new MqttMessage(content.getBytes());

          // System.out.println("Publishing message: " + content);
          message.setQos(qos);
          sampleClient.publish(topic, message);

          if (knownSensors.add(topic)) {
            System.out.println(ANSI_GREEN + "Sensor now active: " + topic + ANSI_RESET);
          }

          // System.out.println("Message published");

        }

        // Disconnecting
        // System.out.println("------------------------------------------------------------");
        sampleClient.disconnect();
        // System.out.println("Disconnected");
        System.exit(0);
      } catch (MqttException me) {
        // System.out.println("reason " + me.getReasonCode());
        // System.out.println("msg " + me.getMessage());
        // System.out.println("loc " + me.getLocalizedMessage());
        // System.out.println("cause " + me.getCause());
        // System.out.println("excep " + me);
        // me.printStackTrace();
        System.out.println(ANSI_RED + "MQTT connection lost for " + topic + ". Reconnecting in 5 seconds..." + ANSI_RESET);
        try { Thread.sleep(5000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
      }
    }  System.exit(1);
    } catch (InterruptedException | IOException e) {
      // System.out.println("time exeption");
      e.printStackTrace();
      System.exit(1);
    }
  }
}
