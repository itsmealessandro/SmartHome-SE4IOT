import org.eclipse.paho.client.mqttv3.*;

public class MqttClientWrapper {

    private MqttClient client;
    private String brokerUrl = "tcp://localhost:1883";  // Indirizzo del broker MQTT
    private String clientId = "homie-gateway-client";

    public MqttClientWrapper() throws MqttException {
        client = new MqttClient(brokerUrl, clientId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setKeepAliveInterval(30);
        client.connect(options);
    }

    public void publish(String topic, String message) throws MqttException {
        MqttMessage mqttMessage = new MqttMessage(message.getBytes());
        mqttMessage.setQos(1);
        client.publish(topic, mqttMessage);
        System.out.println("Message published to topic: " + topic + " with message: " + message);
    }

    public void disconnect() throws MqttException {
        client.disconnect();
    }

    public MqttClient getClient() {
        return client;
    }
}
