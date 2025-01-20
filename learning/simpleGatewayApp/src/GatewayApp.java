import org.eclipse.paho.client.mqttv3.MqttException;

public class GatewayApp {

    public static void main(String[] args) {
        try {
            // Crea client MQTT
            MqttClientWrapper mqttClient = new MqttClientWrapper();

            // Crea sensori (esempio: temperatura e umidità)
            Sensor temperatureSensor = new Sensor("temperature");
            Sensor humiditySensor = new Sensor("humidity");

            // Simula la lettura dei dati e pubblica sui topic secondo la Homie convention
            while (true) {
                temperatureSensor.readData();
                humiditySensor.readData();

                // Pubblica i dati sui topic MQTT
                String temperatureValue = temperatureSensor.getValue();
                String humidityValue = humiditySensor.getValue();

                mqttClient.publish("homie/device1/temperature/value", temperatureValue);
                mqttClient.publish("homie/device1/humidity/value", humidityValue);

                // Attendi un po' prima della prossima lettura
                Thread.sleep(5000);
            }
        } catch (MqttException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
