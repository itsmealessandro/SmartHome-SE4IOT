#!/bin/bash
echo "Configurazione iniziale di InfluxDB..."

# Attendere che InfluxDB sia pronto
until curl -sS --fail "http://influxdb-container:8086/health" > /dev/null; do
  echo "In attesa che InfluxDB sia disponibile..."
  sleep 2
done

# Procedi con la configurazione
curl -sS --fail --request POST "http://influxdb-container:8086/api/v2/orgs" -H "Authorization: Token $INFLUX_TOKEN" -d '{"name": "univaq"}'
curl -sS --fail --request GET "http://influxdb-container:8086/api/v2/orgs?org=univaq" -H "Authorization: Token $INFLUX_TOKEN"
echo "Setup completato con successo."

# Verifica se il database è già configurato (ad esempio, cercando un bucket)
influx bucket list --token $INFLUX_TOKEN --org $INFLUX_ORG &> /dev/null
if [ $? -ne 0 ]; then
  echo "Configurazione iniziale di InfluxDB..."

  # Crea l'organizzazione, il bucket e il token
  influx org create --name $INFLUX_ORG --token $INFLUX_TOKEN

  influx bucket create --name $INFLUX_BUCKET --org $INFLUX_ORG --retention 30d --token $INFLUX_TOKEN

  echo "Setup completato con successo."
else
  echo "InfluxDB è già configurato, salto la configurazione."
fi
