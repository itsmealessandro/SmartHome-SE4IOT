import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.client.mqttv3.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class CreateActuator {
  private String topic;

  public static void main(String[] args) {

    if (args.length < 2) {
      System.out.println("Usage: java CreateActuator <topic> <clientId>");
      return;
    }

    final String ANSI_RESET = "\u001B[0m";
    final String ANSI_BLACK = "\u001B[30m";
    final String ANSI_RED = "\u001B[31m";
    final String ANSI_GREEN = "\u001B[32m";
    final String ANSI_YELLOW = "\u001B[33m";
    final String ANSI_BLUE = "\u001B[34m";
    final String ANSI_PURPLE = "\u001B[35m";
    final String ANSI_CYAN = "\u001B[36m";
    final String ANSI_WHITE = "\u001B[37m";

    String topic = args[0];
    String clientId = args[1];
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
          System.out.println("Connessione persa! " + cause.getMessage());
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) {
          System.out.println(ANSI_GREEN + "Message recived from my topic: " + topic);
          System.out.println(ANSI_GREEN + "Message value: " + message);
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

      // Pubblica il messaggio di conferma
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

  void overrideEnv() throws IOException {

    String[] splittedTopic = this.topic.split("/");

    // NOTE: JSON

    // Creazione del mapper JSON
    ObjectMapper objectMapper = new ObjectMapper();

    File jsonFile = new File("/simulated_env/env.json");
    if (!jsonFile.exists()) {
      System.out.println("Errore: Il file JSON " + jsonFile.getAbsolutePath() + " non esiste.");
      return;
    }
    // Lettura del file JSON
    JsonNode rootNode = objectMapper.readTree(jsonFile);

    // Navigazione nel JSON per arrivare alla stanza 'bedroom' e alla luce
    JsonNode room = rootNode.get(splittedTopic[1]); // 'bedroom'
    JsonNode sensNode = room.get(splittedTopic[2]);

    // Modifica del valore della luce (ad esempio, accendere la luce)
    if (sensNode != null) {
      ((ObjectNode) room).put(splittedTopic[2], 1); // Modifica il valore della luce (esempio: true per accesa)
    }

    // Scrittura del file JSON aggiornato
    objectMapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, rootNode);
  }
}
