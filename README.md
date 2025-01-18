# Smart Home IoT System

## Description

This project implements a complete IoT system for a Smart Home. The goal is to collect, process, store, and visualize data from smart sensors to automate lighting, heating, and security systems.

The system leverages modern technologies to ensure flexibility, scalability, and portability while adhering to IoT design principles.

## Features

1. **Sensor Integration**:
   - Simulated or real sensors to monitor:
     - Ambient temperature.
     - Motion (for security systems).
     - Lighting state (on/off).

2. **Communication Protocol**:
   - Use **MQTT** as the primary communication protocol.
   - Topic structure:
     - `/smart_home/<room>/<sensor>` (e.g., `/smart_home/kitchen/temperature`).

3. **Data Processing**:
   - Middleware based on **Node-RED**:
     - Aggregate and transform data into JSON format.
     - Perform threshold calculations and trigger alerts (e.g., temperature > 28°C).

4. **Data Storage**:
   - Use **InfluxDB** as a time-series database to store sensor data with:
     - Timestamps.
     - Sensor type and location.

5. **Visualization**:
   - Interactive dashboard powered by **Grafana** to:
     - Display real-time data using graphs and gauges.
     - Highlight threshold violations with visual alerts.
     - Filter data by sensor, room, and time period.

6. **Alert Mechanisms**:
   - Configure customizable alerts for critical conditions.
   - Notifications via:
     - Telegram.
     - Email.

7. **Containerization**:
   - All components are containerized using **Docker Compose** for seamless deployment.

## Technologies Used

- **Sensor Simulation**: Python scripts to generate realistic data.
- **Communication Protocol**: MQTT (Eclipse Mosquitto broker).
- **Middleware**: Node-RED.
- **Database**: InfluxDB for time-series data storage.
- **Dashboard**: Grafana for visualization.
- **Alerting**: Telegram Bot API and SMTP server for notifications.
- **Containerization**: Docker and Docker Compose for deployment.

## Requirements

- Docker and Docker Compose installed.
- Configured MQTT broker (e.g., Eclipse Mosquitto).
- Telegram account for notifications.

## Installation

1. Clone this repository:
   ```bash
   git clone https://github.com/your-username/smart-home-iot.git
   cd smart-home-iot
   ```

2. Start the containers with Docker Compose:
   ```bash
   docker-compose up -d
   ```

3. Access the Grafana dashboard at `http://localhost:3000`.

## Usage

1. Configure the sensors (simulated or real) to send data to the MQTT broker.
2. Use Node-RED to customize data processing flows.
3. Monitor real-time data through Grafana.
4. Receive notifications for configured threshold violations.

## Contributions

Contributions are welcome! Please open an issue or submit a pull request to suggest improvements or add features.
