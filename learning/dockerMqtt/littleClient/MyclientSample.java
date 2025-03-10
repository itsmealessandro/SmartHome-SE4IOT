import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttPublishSample {

  public static void main(String[] args) {

    String topic = "SmartHome/sensors/livingRoom";
    String content = "Message from MqttPublishSample";
    int qos = 2;
    String broker = "tcp://test.mosquitto.org:1883";
    String clientId = "sensor1";
    MemoryPersistence persistence = new MemoryPersistence();

    /* JSON
     * Sensors1{
     * id=1
     * value:x
     * }
     * 
     */

    try {
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
      System.exit(0);
    } catch (MqttException me) {
      System.out.println("reason " + me.getReasonCode());
      System.out.println("msg " + me.getMessage());
      System.out.println("loc " + me.getLocalizedMessage());
      System.out.println("cause " + me.getCause());
      System.out.println("excep " + me);
      me.printStackTrace();
    }
  }
}
