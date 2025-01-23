public class GatewayApp {
    public static void main(String[] args) {
        try {
            // Crea client MQTT
            MqttClientWrapper mqttClient = new MqttClientWrapper();

            // Crea sensori (esempio: temperatura e umidità)
            Sensor temperatureSensor = new Sensor("temperature", "temperature", "°C", "device1", mqttClient);
            Sensor humiditySensor = new Sensor("humidity", "humidity", "%", "device1", mqttClient);

            // Simula la lettura dei dati e pubblica sui topic secondo la Homie convention
            while (true) {
                temperatureSensor.publishAll();
                humiditySensor.publishAll();

                // Attendi un po' prima della prossima lettura
                Thread.sleep(5000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
