import org.eclipse.paho.client.mqttv3.*;

public class MqttClientWrapper {

    private MqttClient client;
    private String brokerUrl = "tcp://localhost:1883";  // Indirizzo del broker MQTT
    private String clientId = "homie-gateway-client";

    public MqttClientWrapper() {
        try {
            client = new MqttClient(brokerUrl, clientId);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setKeepAliveInterval(30);
            client.connect(options);
            System.out.println("Connected to the broker: " + brokerUrl);
        } catch (MqttException e) {
            System.err.println("Error connecting to the broker: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void publish(String topic, String message) {
        try {
            if (client.isConnected()) {
                MqttMessage mqttMessage = new MqttMessage(message.getBytes());
                mqttMessage.setQos(1);
                client.publish(topic, mqttMessage);
                System.out.println("Message published to topic: " + topic + " with message: " + message);
            } else {
                System.err.println("Client is not connected. Cannot publish message.");
            }
        } catch (MqttException e) {
            System.err.println("Error publishing message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (client.isConnected()) {
                client.disconnect();
                System.out.println("Disconnected from the broker.");
            } else {
                System.out.println("Client is already disconnected.");
            }
        } catch (MqttException e) {
            System.err.println("Error disconnecting from the broker: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public MqttClient getClient() {
        return client;
    }
}
