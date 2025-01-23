FROM ubuntu:latest

RUN apt update && \
    apt install -y default-jdk mosquitto

COPY ./littleClient/ /
COPY ./mqttsetup.sh /

RUN chmod +x ./mqttsetup.sh

CMD ["./mqttsetup.sh"]
