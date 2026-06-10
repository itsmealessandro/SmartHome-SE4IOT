# Smart Home IoT System

Complete IoT stack for a simulated Smart Home: sensors publish MQTT data, Node-RED processes and stores it, Grafana visualizes it, and actuators close the control loop when thresholds are exceeded.

## Architecture

- **Mosquitto** — MQTT broker (`SmartHome/<room>/<sensor>`)
- **Sensors / Actuators** — Java simulators reading/writing `simulated_env/env.json`
- **Node-RED** — parsing, InfluxDB writes, threshold checks, Telegram alerts, actuator commands
- **InfluxDB** — time-series storage (`smart_home_iot` bucket)
- **Grafana** — provisioned dashboards for all 4 sensors
- **Manager App** — web UI to edit thresholds (`http://localhost:8089`)

## Requirements

- Docker and Docker Compose
- Telegram bot token and chat ID (optional, for alerts)

## Quick start

1. Clone the repository:
   ```bash
   git clone https://github.com/itsmealessandro/SmartHome-SE4IOT.git
   cd SmartHome-SE4IOT
   ```

2. Create environment file:
   ```bash
   cp .env.example .env
   ```
   Edit `.env` and set `TGBOT_TOKEN` and `TG_CHAT_ID` for Telegram alerts.

3. Start the stack:
   ```bash
   docker compose up -d --build
   ```

4. Open services:
   - Grafana: `http://localhost:3000` (default login `admin` / `admin`)
   - Node-RED: `http://localhost:1880`
   - Manager thresholds UI: `http://localhost:8089`
   - InfluxDB: `http://localhost:8086`

## First-time Node-RED setup

On first start, Node-RED installs npm dependencies from `nodered/package.json` (may take a minute).

Configure the InfluxDB node credentials in the Node-RED editor if writes fail:
- URL: `http://influxdb:8086`
- Org: `univaq`
- Bucket: `smart_home_iot`
- Token: same as `DOCKER_INFLUXDB_INIT_ADMIN_TOKEN` in `.env`

## Verify the stack

```bash
# InfluxDB health
curl http://localhost:8086/health

# MQTT messages (requires mosquitto clients)
docker exec mosquitto-IOT mosquitto_sub -t 'SmartHome/#' -v

# Container status
docker compose ps
```

## MQTT topics

| Topic | Description |
|-------|-------------|
| `SmartHome/<room>/<sensor>` | Sensor readings (e.g. `SmartHome/bedroom/temperature`) |
| `SmartHome/thresholds/<room>/<sensor>` | Threshold updates from Manager App |
| `SmartHome/<room>/<sensor>Act` | Actuator commands (e.g. `SmartHome/bedroom/lightAct`) |

## Actuator automation

When a sensor value exceeds its threshold, Node-RED:
1. Sends a Telegram alert
2. Publishes an actuator command (`light` → `0`, `temperature` → `20`)
3. The actuator updates `env.json`, which sensors read on the next cycle

## Known limitations

- MQTT broker allows anonymous connections (dev/demo only)
- Email alerts and motion sensors are not implemented
- Default credentials in `.env.example` are for local development only
