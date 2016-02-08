FROM index.docker.io/library/maven:3.3.3-jdk-7

ADD / /tmp/build/

RUN cd /tmp/build && mvn clean compile -Dmaven.test.skip=true

EXPOSE 8090

ENTRYPOINT cd /tmp/build && mvn test
