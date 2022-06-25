FROM ubuntu:16.04

MAINTAINER korateli05@gmail.com

# Обвновление списка пакетов
RUN apt-get -y update

#
# Установка postgresql
#
ENV PGVER 9.5
RUN apt-get update
RUN apt-get install -y postgresql-$PGVER

USER postgres

RUN /etc/init.d/postgresql start &&\
    psql --command "CREATE USER docker WITH SUPERUSER PASSWORD 'docker';" &&\
    createdb -E UTF8 -T template0 -O docker docker &&\
    /etc/init.d/postgresql stop


RUN echo "host all  all    0.0.0.0/0  md5" >> /etc/postgresql/$PGVER/main/pg_hba.conf

RUN echo "listen_addresses='*'" >> /etc/postgresql/$PGVER/main/postgresql.conf
RUN echo "synchronous_commit = off" >> /etc/postgresql/$PGVER/main/postgresql.conf
RUN echo "shared_buffers = 256MB" >> /etc/postgresql/$PGVER/main/postgresql.conf
RUN echo "autovacuum = off" >> /etc/postgresql/$PGVER/main/postgresql.conf


EXPOSE 5432

VOLUME  ["/etc/postgresql", "/var/log/postgresql", "/var/lib/postgresql"]

USER root

RUN apt-get update
RUN apt-get install -y openjdk-8-jdk-headless

ENV WORK /opt/DBServer
ADD src/ $WORK/src/
COPY target/DBServer-1.0-SNAPSHOT.jar $WORK/target/

WORKDIR $WORK

EXPOSE 8080

CMD service postgresql start && java -jar target/DBServer-1.0-SNAPSHOT.jar application.Application --database=jdbc:postgresql://localhost5432/docker --username=docker --password=docker