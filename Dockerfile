FROM index.docker.io/library/maven:3.3.3-jdk-7

EXPOSE 8089

CMD ["mvn test"]

