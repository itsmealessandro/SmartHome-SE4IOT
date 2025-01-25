import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttPublishSample {

  public static void main(String[] args) {

    String topic = "testTopic";
    String content = "test content";
    int qos = 1;
    String broker = "tcp://broker:1883";
    String clientId = "testId";
    MemoryPersistence persistence = new MemoryPersistence();

    try {
      Thread.sleep(2000);

      for (int i = 0; i < 1000; i++) {
        Thread.sleep(500);
        MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        System.out.println("------------------------------------------------------------");
        System.out.println("Connecting to broker: " + broker);
        sampleClient.connect(connOpts);
        System.out.println("Connected");
        System.out.println("------------------------------------------------------------");

        System.out.println("Publishing message: " + content);
        MqttMessage message = new MqttMessage(content.getBytes());
        message.setQos(qos);
        sampleClient.publish(topic, message);
        System.out.println("Message published");

        System.out.println("------------------------------------------------------------");
        sampleClient.disconnect();
        System.out.println("Disconnected");
      }
      System.exit(0);
    } catch (MqttException me) {
      System.out.println("reason " + me.getReasonCode());
      System.out.println("msg " + me.getMessage());
      System.out.println("loc " + me.getLocalizedMessage());
      System.out.println("cause " + me.getCause());
      System.out.println("excep " + me);
      me.printStackTrace();
      System.exit(1);
    } catch (InterruptedException i) {
      System.out.println("time exeption");
      i.printStackTrace();
      System.exit(1);

    }
  }
}
