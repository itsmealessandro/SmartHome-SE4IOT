Simple Gateway App for IoT - SE4IOT

Questo progetto è una semplice applicazione Gateway sviluppata per raccogliere dati da sensori come temperatura e umidità, e pubblicarli su un broker MQTT utilizzando la Homie convention. La Homie convention è un insieme di linee guida per organizzare i topic MQTT in modo che i dispositivi IoT siano facilmente riconoscibili e interoperabili.

Il progetto è stato creato come parte dell'esercizio pratico per imparare la gestione dei sensori IoT e l'uso di MQTT, con particolare attenzione alla struttura dei topic secondo le linee guida della Homie convention.

La struttura del progetto è la seguente:

/home/agostino/SmartHome-SE4IOT/learning/simpleGatewayApp
├── config
│   └── settings.json             # File di configurazione per l'applicazione
├── README.md                    # Documentazione del progetto
└── src
    ├── GatewayApp.java           # Logica principale dell'applicazione Gateway
    ├── MqttClientWrapper.java    # Gestione della connessione MQTT
    └── Sensor.java               # Simulazione dei sensori (es. temperatura, umidità)

File di configurazione

    config/settings.json: Questo file contiene le impostazioni di configurazione per l'applicazione. Qui è possibile definire parametri come l'URL del broker MQTT, l'ID del dispositivo e l'elenco dei sensori. L'applicazione leggerà queste configurazioni all'avvio per connettersi correttamente al broker MQTT e configurare il comportamento del gateway.

Codice sorgente

    src/GatewayApp.java: Questo è il file principale dell'applicazione. Contiene la logica che simula la raccolta dei dati dai sensori (temperatura e umidità), e pubblica questi dati sui topic MQTT secondo la Homie convention. La classe avvia la lettura dei sensori a intervalli regolari e invia i dati al broker MQTT.

    src/MqttClientWrapper.java: Questa classe gestisce la connessione MQTT al broker. Include funzioni per connettersi al broker, pubblicare i dati sui topic appropriati, e disconnettersi. Utilizza la libreria Eclipse Paho per la gestione della comunicazione MQTT.

    src/Sensor.java: Questa classe simula i sensori di temperatura e umidità. Ogni sensore ha un ID (ad esempio, "temperature" o "humidity") e un valore che rappresenta la lettura attuale del sensore. La classe fornisce metodi per "leggere" i dati e ottenere i valori correnti.

Come funziona

L'applicazione simula un gateway che raccoglie i dati da diversi sensori e li pubblica su un broker MQTT. I dati vengono inviati su topic organizzati secondo la Homie convention:

Esempio di topic:

    homie/device1/temperature/value: Rappresenta il valore della temperatura.
    homie/device1/humidity/value: Rappresenta il valore dell'umidità.

Ciclo dell'applicazione

    L'applicazione si connette al broker MQTT utilizzando le configurazioni nel file settings.json.
    I sensori di temperatura e umidità vengono simulati, e ogni sensore genera un valore casuale rappresentante una lettura del sensore.
    I dati vengono pubblicati sui topic MQTT con il formato specificato dalla Homie convention.
    Il gateway continua a raccogliere i dati e a pubblicarli su base periodica (ogni 5 secondi nel nostro esempio).
    L'applicazione può essere estesa facilmente per aggiungere altri sensori o per interagire con dispositivi IoT reali.

Requisiti

    Java 11 o superiore
    Mosquitto o altro broker MQTT
    Eclipse Paho MQTT Client (già incluso nel progetto)

Come eseguire il progetto

    Installare il broker MQTT (se non è già in esecuzione):
        Puoi usare Mosquitto:

    sudo apt install mosquitto
    mosquitto

    Alternativamente, puoi usare un broker MQTT pubblico o un altro broker di tua scelta.

Clonare il repository e navigare nella cartella del progetto:

git clone <https://github.com/itsmealessandro/SmartHome-SE4IOT/tree/main>
cd SmartHome-SE4IOT/learning/simpleGatewayApp

Compilare il progetto Java: Se stai usando Maven:

mvn clean install

Eseguire l'applicazione:

    java -cp target/simpleGatewayApp.jar GatewayApp

    Verificare i dati pubblicati: Usa un client MQTT come MQTT Explorer per connetterti al broker e osservare i topic pubblicati (ad esempio, homie/device1/temperature/value e homie/device1/humidity/value).

Conclusioni

Questo progetto è un esempio base per comprendere come raccogliere e pubblicare dati da sensori utilizzando MQTT e la Homie convention. È progettato per essere facilmente estensibile e può essere adattato per l'integrazione con sensori fisici o dispositivi IoT reali. La struttura dei topic secondo la Homie convention aiuta a garantire che i dispositivi IoT possano essere facilmente integrati e gestiti in una rete di dispositivi IoT.