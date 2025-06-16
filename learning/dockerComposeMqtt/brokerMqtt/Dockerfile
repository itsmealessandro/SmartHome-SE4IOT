# Usa l'immagine ufficiale Temurin JDK 17 basata su Alpine.
# Questo include già OpenJDK Temurin e si basa su una distribuzione leggera.
FROM eclipse-temurin:17-jdk-alpine

# Non è necessario passare a root o installare JDK, è già nella base image.
# Non è necessario installare software-properties-common o curl,
# né aggiungere repository o chiavi GPG, in quanto il JDK è pre-installato.

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
