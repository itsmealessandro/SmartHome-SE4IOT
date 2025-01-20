public class Sensor {
    private String id;
    private String type;
    private double value;
    private String unit;
    private String deviceId;
    private MqttClientWrapper mqttClient;

    // Costruttore
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

    // Metodo per aggiornare il valore del sensore (simulando una lettura)
    public void updateValue() {
        if (this.type.equals("temperature")) {
            this.value = 20 + Math.random() * 5; // Simula una temperatura tra 20 e 25 °C
        } else if (this.type.equals("humidity")) {
            this.value = 40 + Math.random() * 20; // Simula un'umidità tra 40% e 60%
        }
    }

    // Metodo per pubblicare il valore del sensore su MQTT
    public void publishValue() {
        String topic = "homie/" + deviceId + "/" + id + "/value";
        mqttClient.publish(topic, String.valueOf(value));
    }

    // Metodo per pubblicare l'unità di misura su MQTT
    public void publishUnit() {
        String topic = "homie/" + deviceId + "/" + id + "/unit";
        mqttClient.publish(topic, unit);
    }

    // Metodo per pubblicare il nome del sensore su MQTT
    public void publishName() {
        String topic = "homie/" + deviceId + "/" + id + "/name";
        mqttClient.publish(topic, type.substring(0, 1).toUpperCase() + type.substring(1));
    }

    // Metodo per pubblicare tutte le informazioni del sensore
    public void publishAll() {
        updateValue(); // Aggiorna il valore del sensore
        publishValue(); // Pubblica il valore
        publishUnit();  // Pubblica l'unità di misura
        publishName();  // Pubblica il nome del sensore
    }
}
