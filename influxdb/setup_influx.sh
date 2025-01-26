#!/bin/bash

echo "Setting up InfluxDB..."

# Attendi che il servizio sia attivo
until influx ping --host http://localhost:8086; do
  echo "Waiting for InfluxDB to start..."
  sleep 2
done

# Configura l'utente admin, l'organizzazione e il bucket di default
influx setup \
  --username admin \
  --password admin123 \
  --org univaq \
  --bucket default_bucket \
  --retention 30d \
  --force

echo "InfluxDB setup completed!"
