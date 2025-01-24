FROM ubuntu:latest

RUN apt update && \
    apt install -y default-jre

COPY ./littleClient/ /littleClient/
COPY ./mqttsetup.sh /

RUN chmod +x ./mqttsetup.sh

CMD ["./mqttsetup.sh"]
