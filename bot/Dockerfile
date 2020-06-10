FROM almightyalpaca/openjdk:11-jre-slim

ENV DOCKER=true

COPY app.jar /app/app.jar

WORKDIR /

CMD ["java", "-jar", "/app/app.jar"]
