FROM eclipse-temurin:17-jdk-alpine



# Copia la directory 'sens'
COPY ./sens/ /sens/

# Copia lo script di setup nella root del container
COPY sensorsSetup.sh /

# Rende lo script eseguibile
RUN chmod +x /sensorsSetup.sh

# Debug: controlla che i file siano stati copiati correttamente
RUN echo "Contenuto della root:" && ls -l /
RUN echo "Contenuto di /sens:" && ls -l /sens/

# Definisce il comando da eseguire all'avvio
CMD ["/sensorsSetup.sh"]
