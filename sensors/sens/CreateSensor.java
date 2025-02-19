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

public class CreateSensor {

  public static void main(String[] args) {

    String topic = args[0];
    int qos = 1;
    String broker = "tcp://broker:1883";
    String clientId = args[1];
    MemoryPersistence persistence = new MemoryPersistence();

    boolean active = true;
    final String ANSI_RESET = "\u001B[0m";
    final String ANSI_BLACK = "\u001B[30m";
    final String ANSI_RED = "\u001B[31m";
    final String ANSI_GREEN = "\u001B[32m";
    final String ANSI_YELLOW = "\u001B[33m";
    final String ANSI_BLUE = "\u001B[34m";
    final String ANSI_PURPLE = "\u001B[35m";
    final String ANSI_CYAN = "\u001B[36m";
    final String ANSI_WHITE = "\u001B[37m";

    System.out.println(ANSI_GREEN + "Dynamic sensor activated" + args[0]);

    try {
      // NOTE: enstablishing MQTT Connection
      MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
      MqttConnectOptions connOpts = new MqttConnectOptions();
      connOpts.setCleanSession(true);
      // System.out.println("------------------------------------------------------------");
      // System.out.println("Connecting to broker: " + broker);
      sampleClient.connect(connOpts);
      // System.out.println("Connected");
      // System.out.println("------------------------------------------------------------");
      Thread.sleep(2000);

      while (active) {

        String content = null;
        Random random = new Random();
        // 5% probability to trigger alarm
        int alertProb = random.nextInt(0, 100);
        if (alertProb <= 5) {

          // System.out.println("------------------------------------------------------------");
          System.out.println(ANSI_RED + " ALERT VALUE sensor:" + args[0]);
          // System.out.println("------------------------------------------------------------");
          content = String.valueOf(random.nextInt(6, 10));

        } else {
          // System.out.println("------------------------------------------------------------");
          // System.out.println(" normal VALUE sens1");
          // System.out.println("------------------------------------------------------------");
          content = String.valueOf(random.nextInt(5));
        }

        Thread.sleep(500);

        MqttMessage message = new MqttMessage(content.getBytes());

        // System.out.println("Publishing message: " + content);
        message.setQos(qos);
        sampleClient.publish(topic, message);
        // System.out.println("Message published");

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

        // Navigazione nel JSON
        JsonNode bedroom = rootNode.get("bedroom");
        JsonNode livingroom = rootNode.get("livingroom");

        int bedroomLight = bedroom.get("light").asInt();
        int bedroomTemp = bedroom.get("temperature").asInt();

        int livingroomLight = livingroom.get("light").asInt();
        int livingroomTemp = livingroom.get("temperature").asInt();

        // Stampa dei valori
        System.out.println("Bedroom - Light: " + bedroomLight + ", Temperature: " + bedroomTemp);
        System.out.println("Livingroom - Light: " + livingroomLight + ", Temperature: " + livingroomTemp);

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
      me.printStackTrace();
      System.exit(1);
    } catch (InterruptedException | IOException e) {
      // System.out.println("time exeption");
      e.printStackTrace();
      System.exit(1);
    }
  }
}
