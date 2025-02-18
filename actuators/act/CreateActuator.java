import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class CreateActuator {

    public static void main(String[] args) {

        System.out.println("helloooooooooooooooooooo");

        if (args.length < 2) {
            System.out.println("Usage: java CreateActuator <topic> <clientId>");
            return;
        }

        String topic = args[0];
        String clientId = args[1];
        int qos = 1;
        String broker = "tcp://broker:1883";
        MemoryPersistence persistence = new MemoryPersistence();

        final String ANSI_GREEN = "\u001B[32m";
        final String ANSI_RESET = "\u001B[0m";

        System.out.println(ANSI_GREEN + "Actuator activated: " + topic + ANSI_RESET);
        System.out.println("prima del try");

        try {

            System.out.println("inizia try catch");
            // Connessione al broker
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            sampleClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("Connessione persa! " + cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    System.out.println("Messaggio ricevuto su " + topic + ": " + new String(message.getPayload()));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // Non usato nel subscriber
                }
            });

            sampleClient.connect(connOpts);
            System.out.println("Connesso al broker: " + broker);

            sampleClient.subscribe(topic, qos);
            System.out.println("Sottoscritto al topic: " + topic);

            // Pubblica il messaggio di conferma
            String content = "actuator listening";
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            sampleClient.publish(topic, message);
            System.out.println("Messaggio pubblicato: " + content);

            // Mantieni il programma in esecuzione per ricevere i messaggi
            while (true) {
                Thread.sleep(1000);
            }

        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
