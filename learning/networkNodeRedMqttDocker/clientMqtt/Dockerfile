# Usa l'immagine ufficiale Temurin JRE 17 basata su Alpine.
# Questa immagine è molto leggera e include già un Java Runtime Environment (JRE).
FROM eclipse-temurin:17-jre-alpine

# Non è necessario passare a root o installare JRE/JDK, è già nella base image.
# Non è necessario installare software-properties-common o curl,
# né aggiungere repository o chiavi GPG, in quanto il JRE è pre-installato.

# Copia la tua directory 'littleClient' all'interno del container.
# Assicurati che 'littleClient/' si trovi nella stessa directory del Dockerfile
# o che il percorso relativo sia corretto rispetto al contesto di build.
COPY ./littleClient/ /littleClient/

# Copia lo script 'mqttsetup.sh' nella root del container.
# Assicurati che 'mqttsetup.sh' si trovi nella stessa directory del Dockerfile
# o che il percorso relativo sia corretto.
COPY ./mqttsetup.sh /

# Rende lo script eseguibile.
RUN chmod +x /mqttsetup.sh

# Definisce il comando da eseguire all'avvio del container.
# Questo eseguirà il tuo script di setup MQTT.
CMD ["/mqttsetup.sh"]

