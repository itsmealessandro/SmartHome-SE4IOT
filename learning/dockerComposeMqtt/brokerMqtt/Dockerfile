FROM ubuntu:latest

RUN apt update && apt install -y mosquitto

# Copia il file di configurazione nel container
COPY mosquitto.conf /etc/mosquitto/mosquitto.conf

CMD ["mosquitto" ,"-c", "/etc/mosquitto/mosquitto.conf"]
