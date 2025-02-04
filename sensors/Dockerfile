FROM ubuntu:latest

RUN apt update && \
    apt install -y default-jdk

COPY ./sens/ /sens/
COPY ./sensorsSetup.sh /

RUN chmod +x ./sensorsSetup.sh

CMD ["./sensorsSetup.sh"]
