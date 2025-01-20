public class Sensor {
    private String id;
    private String type;
    private double value;
    private String unit;
    private String deviceId;
    private MqttClientWrapper mqttClient;

    public Sensor(String id, String type, String unit, String deviceId, MqttClientWrapper mqttClient) {
        this.id = id;
        this.type = type;
        this.unit = unit;
        this.deviceId = deviceId;
        this.mqttClient = mqttClient;
    }

    // Metodo per ottenere il valore attuale del sensore
    public double getValue() {
        return value;
    }

    // Metodo per aggiornare il valore del sensore (simulando una lettura dal sensore)
    public void updateValue() {
        // Simuliamo un valore casuale per il sensore
        // Questo valore sarà sostituito da una lettura reale in un'applicazione reale
        if (this.type.equals("temperature")) {
            this.value = 20 + Math.random() * 5; // temperatura tra 20 e 25 °C
        } else if (this.type.equals("humidity")) {
            this.value = 40 + Math.random() * 20; // umidità tra 40% e 60%
        }
    }

    // Metodo per pubblicare il valore del sensore su MQTT
    public void publishValue() {
        String topic = "homie/" + deviceId + "/" + id + "/value";
        mqttClient.publish(topic, String.valueOf(value));
    }

    // Metodo per pubblicare l'unità di misura del sensore su MQTT
    public void publishUnit() {
        String topic = "homie/" + deviceId + "/" + id + "/unit";
        mqttClient.publish(topic, unit);
    }

    // Metodo per pubblicare il nome del sensore su MQTT
    public void publishName() {
        String topic = "homie/" + deviceId + "/" + id + "/name";
        mqttClient.publish(topic, type.substring(0, 1).toUpperCase() + type.substring(1));
    }

    // Metodo per pubblicare tutte le informazioni del sensore (valore, unità, nome)
    public void publishAll() {
        updateValue();  // Aggiorniamo il valore del sensore
        publishValue(); // Pubbliciamo il valore
        publishUnit();  // Pubbliciamo l'unità di misura
        publishName();  // Pubbliciamo il nome del sensore
    }
}
