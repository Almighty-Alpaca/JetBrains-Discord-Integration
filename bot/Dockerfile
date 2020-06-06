FROM adoptopenjdk:14-jdk-hotspot

ENV DOCKER=true

COPY app.jar /app/app.jar

WORKDIR /

CMD ["java", "-jar", "/app/app.jar"]
