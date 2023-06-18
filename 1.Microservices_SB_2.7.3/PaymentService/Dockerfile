FROM openjdk:17-oracle

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} paymentservice.jar

ENTRYPOINT ["java", "-jar", "/paymentservice.jar"]

EXPOSE 8081