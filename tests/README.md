# Test

## Panoramica

| Categoria | Cosa testa | Tecnologia |
|---|---|---|
| **Unit (Manager App)** | Model, Service, Controller | JUnit 5 + Mockito |
| **Smoke (Manager App)** | Avvio contesto Spring Boot | `@SpringBootTest` |
| **Infrastruttura** | `compose.yaml` valido, file referenziati esistono | Shell script |
| **Node-RED** | `flows.json` valido, wiring, sintassi JS | Node.js script |

---

## Test esistenti (precedenti)

### `ThresholdTest` — `model/ThresholdTest.java`
- Getter/setter, `equals()` (per room+type), `toString()`.

### `SensorTest` — `model/SensorTest.java`  
- Costruttori, getter/setter, health status correlato a enabled.

### `ThresholdsServiceImplTest` — `servicies/ThresholdsServiceImplTest.java`
- **GetThresholds**: lettura da file JSON, lista vuota, file mancante.
- **UpdateThresholds**: scrittura su file.
- **AddThreshold**: aggiunta, duplicato, trim/lowercase, aggiornamento env.json.
- **GetSensors**: parsing env.json strutturato, legacy formato plain int, stanze multiple.
- **ToggleSensorStatus**: enable/disable, conversione legacy, room/sconosciuti, file mancante.

### `ManagerControllerTest` — `controller/ManagerControllerTest.java`
- `GET /`: pagina con model attributes, errore IOException.
- `POST /changeThresholds`: successo, errore IO.
- `POST /addSensor`: successo, duplicato, errore IO.
- `POST /sensorEnabled`: accensione, spegnimento, errore IO.

---

## Test aggiunti

### `ThresholdsFormTest` — `model/ThresholdsFormTest.java`
**Cosa testa:** getter/setter di `ThresholdsForm` (wrapper di `List<Threshold>`).

**Senso:** La classe è usata dal controller per ricevere i dati del form Thymeleaf. Il test garantisce che funzioni anche con lista vuota.

### `ManagerApplicationTest` — `ManagerApplicationTest.java`
**Cosa testa:** che il contesto Spring Boot si avvii senza errori.

**Senso:** Smoke test base. Se una dipendenza manca o la configurazione è rotta, fallisce subito. Non richiede broker MQTT perché `MqttClient` è creato nel costruttore senza connettersi, e il `@Scheduled` gestisce silenziosamente gli errori di connessione.

### `PublishThresholdsMQTT` (nested in `ThresholdsServiceImplTest`)
**Cosa testa:** il metodo `@Scheduled publishThresholdsMQTT()`.

**Senso:** Era il percorso non testato più critico: publica i threshold su MQTT ogni 5 secondi. I test coprono:
- **Già connesso** → pubblica direttamente, senza tentare connect
- **Disconnesso** → connect + publish
- **Threshold multipli** → un topic MQTT per ognuno
- **Errore di connessione** → gestito senza throw
- **IOException** (file threshold mancante) → gestito senza throw

### `tests/compose_validate.sh`
**Cosa testa:** validazione sintattica e strutturale di `compose.yaml`.

**Senso:** Docker Compose è l'orchestratore del sistema. Errori nel compose.yaml bloccano l'avvio dell'intero sistema. I check coprono:
- Sintassi YAML valida (`docker compose config`)
- File `.env` presente
- Dockerfile per ogni servizio con `build:` esistono
- `mosquitto.conf` e tutte le directory montate esistono
- Ogni servizio definisce `image:` o `build:`
- Ogni servizio è sulla rete `net-test`

### `tests/flows_validate.js`
**Cosa testa:** validazione del file `nodered/flows.json`.

**Senso:** I flow Node-RED contengono logica di business (filtraggio, alert Telegram, comandi actuator, scrittura InfluxDB). Errori in `flows.json` causano malfunzionamenti silenziosi. I check coprono:
- JSON valido e struttura array
- Ogni nodo ha `id` e `type`
- I `wires` referenziano solo nodi esistenti (nessun collegamento interrotto)
- Il codice JavaScript di tutti i 9 Function node è sintatticamente valido
- I nodi MQTT referenziano un broker configurato
- I nodi InfluxDB referenziano una config valida

---

## Esecuzione

```bash
# Test Java (Manager App)
cd managerApplication/app/managerApp && ./mvnw test

# Validazione Docker Compose
./tests/compose_validate.sh

# Validazione Node-RED flows
node tests/flows_validate.js
```
