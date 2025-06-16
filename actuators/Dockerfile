# Usa l'immagine ufficiale Temurin JDK 17 basata su Alpine.
# Questa immagine include già un ambiente JDK completo e si basa su una distribuzione leggera.
FROM eclipse-temurin:17-jdk-alpine

# Non è necessario installare JRE/JDK o aggiornare i pacchetti, è già nella base image.
# Le dipendenze per Temurin sono pre-installate.

# Copia la tua directory 'act' (attuatori) all'interno del container.
# Assicurati che 'act/' si trovi nella stessa directory del Dockerfile
# o che il percorso relativo sia corretto rispetto al contesto di build.
COPY ./act/ /act/

# Copia lo script di setup degli attuatori nella root del container.
# Assicurati che 'actuatorsSetup.sh' si trovi nella stessa directory del Dockerfile
# o che il percorso relativo sia corretto.
COPY ./actuatorsSetup.sh /

# Rende lo script eseguibile.
RUN chmod +x /actuatorsSetup.sh

# Definisce il comando da eseguire all'avvio del container.
# Questo eseguirà il tuo script di setup per gli attuatori.
CMD ["/actuatorsSetup.sh"]
