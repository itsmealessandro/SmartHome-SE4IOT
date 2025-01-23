# Docker Compose: Mosquitto, Node-RED, and MQTT Client

Questo file `docker-compose.yml` configura un sistema Docker composto da tre container:

## Struttura dei Servizi

### 1. Broker (Eclipse Mosquitto)
- **Immagine**: `eclipse-mosquitto`
- **Porte**: Espone la porta `1883` per le comunicazioni MQTT.
- **Rete**: Connesso alla rete bridge `nodered-mosquitto`.
- **Volumi**:
  - `./config/mosquitto.conf` montato come file di configurazione in sola lettura.
  - `./log` per salvare i log generati dal broker.
- **Riavvio automatico**: Configurato con `unless-stopped` per garantire la continuità del servizio.

### 2. Node-RED
- **Immagine**: `nodered/node-red:latest`
- **Porte**: Espone la porta `1880` per accedere all'interfaccia web di Node-RED.
- **Rete**: Connesso alla rete bridge `nodered-mosquitto`.
- **Volumi**:
  - `shared-data` per la persistenza dei dati di Node-RED.

### 3. Client MQTT
- **Immagine**: Costruita a partire dalla directory `./clientMqtt/` basata su `ubuntu:latest`.
- **Rete**: Connesso alla rete bridge `nodered-mosquitto`.

## Volumi
- **shared-data**: Volume per la persistenza dei dati condivisi di Node-RED.
- **config**: Volume che contiene il file di configurazione di Mosquitto.
- **log**: Volume che salva i log generati da Mosquitto.

## Reti
- **nodered-mosquitto**: Rete di tipo bridge che permette la comunicazione tra i container.

## Dettagli del Codice

### Configurazione del Broker Mosquitto
Il container del broker MQTT utilizza un file di configurazione specifico montato da `./config/mosquitto.conf` e salva i log nella directory `./log`. La porta `1883` è mappata per consentire connessioni esterne.

### Configurazione di Node-RED
Node-RED utilizza un volume denominato `shared-data` per mantenere i dati persistenti. L'interfaccia web è accessibile tramite la porta `1880`.

### Configurazione del Client MQTT
Il client MQTT è costruito utilizzando un Dockerfile presente nella directory `./clientMqtt/`. Questo container è connesso alla rete `nodered-mosquitto` per comunicare con Mosquitto e Node-RED.

## Note
- Il file di configurazione `mosquitto.conf` deve essere adattato in base alle esigenze specifiche del sistema.
- Assicurarsi che le directory `./config` e `./log` abbiano i permessi adeguati per consentire ai container di accedere ai file.
- La rete bridge `nodered-mosquitto` garantisce una comunicazione fluida tra i servizi.

## Problemi Comuni
1. **Porte in conflitto**: Verificare che le porte `1883` e `1880` non siano utilizzate da altri processi sul sistema host.
2. **Permessi sui volumi**: Assicurarsi che le directory montate abbiano i permessi corretti per evitare errori di accesso.

## Risorse Utili
- [Documentazione di Eclipse Mosquitto](https://mosquitto.org/documentation/)
- [Documentazione di Node-RED](https://nodered.org/docs/)
- [Documentazione di Docker Compose](https://docs.docker.com/compose/)
