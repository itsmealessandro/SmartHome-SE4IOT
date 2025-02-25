FROM ubuntu:latest

RUN apt update && \
    apt install -y default-jdk

COPY ./act/ /act/
COPY ./actuatorsSetup.sh /

RUN chmod +x ./actuatorsSetup.sh

CMD ["./actuatorsSetup.sh"]
