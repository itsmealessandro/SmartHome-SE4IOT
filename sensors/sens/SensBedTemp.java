import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.util.Random;

public class SensBedTemp {

  public static void main(String[] args) {

    String topic = "SmartHome/bedroom/temperature";
    int qos = 1;
    String broker = "tcp://broker:1883";
    String clientId = "2";
    MemoryPersistence persistence = new MemoryPersistence();

    boolean active = true;
    final int alertValue = 21;
    final String ANSI_RESET = "\u001B[0m";
    final String ANSI_BLACK = "\u001B[30m";
    final String ANSI_RED = "\u001B[31m";
    final String ANSI_GREEN = "\u001B[32m";
    final String ANSI_YELLOW = "\u001B[33m";
    final String ANSI_BLUE = "\u001B[34m";
    final String ANSI_PURPLE = "\u001B[35m";
    final String ANSI_CYAN = "\u001B[36m";
    final String ANSI_WHITE = "\u001B[37m";

    try {
      // NOTE: enstablishing MQTT Connection
      MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
      MqttConnectOptions connOpts = new MqttConnectOptions();
      connOpts.setCleanSession(true);
//       System.out.println("------------------------------------------------------------");
//       System.out.println("Connecting to broker: " + broker);
      sampleClient.connect(connOpts);
//       System.out.println("Connected");
//       System.out.println("------------------------------------------------------------");
      Thread.sleep(2000);

      while (active) {

        String content = null;
        Random random = new Random();
        // 5% probability to trigger alarm
        int alertProb = random.nextInt(0, 100);
        if (alertProb <= 5) {

//           System.out.println("------------------------------------------------------------");
//           System.out.println(ANSI_RED + " ALERT VALUE sens1");
//           System.out.println("------------------------------------------------------------");
          content = String.valueOf(alertValue);

        } else {
//           System.out.println("------------------------------------------------------------");
//           System.out.println(" normal VALUE sens1");
//           System.out.println("------------------------------------------------------------");
          content = String.valueOf(random.nextInt(16, 20));
        }

        Thread.sleep(500);

        MqttMessage message = new MqttMessage(content.getBytes());

//         System.out.println("Publishing message: " + content);
        message.setQos(qos);
        sampleClient.publish(topic, message);
//         System.out.println("Message published");

      }

      // Disconnecting
//       System.out.println("------------------------------------------------------------");
      sampleClient.disconnect();
//       System.out.println("Disconnected");
      System.exit(0);
    } catch (MqttException me) {
//       System.out.println("reason " + me.getReasonCode());
//       System.out.println("msg " + me.getMessage());
//       System.out.println("loc " + me.getLocalizedMessage());
//       System.out.println("cause " + me.getCause());
//       System.out.println("excep " + me);
      me.printStackTrace();
      System.exit(1);
    } catch (InterruptedException i) {
//       System.out.println("time exeption");
      i.printStackTrace();
      System.exit(1);

    }
  }
}
