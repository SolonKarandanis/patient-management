FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /application
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM openjdk:21-jdk AS runner
WORKDIR /application
COPY --from=builder ./application/target/patient-service-0.0.1-SNAPSHOT.jar ./app.jar
EXPOSE 4000
ENV JAVA_OPTS=""
ENV APP_HOME /application
#Create folder to save configuration files
RUN mkdir $APP_HOME/config
#Create folder with application logs
RUN mkdir $APP_HOME/log

VOLUME $APP_HOME/log
VOLUME $APP_HOME/config

COPY --from=builder $APP_HOME/dependencies/ ./
COPY --from=builder $APP_HOME/snapshot-dependencies/ ./
COPY --from=builder $APP_HOME/spring-boot-loader/ ./
COPY --from=builder $APP_HOME/application/target/patient-service-0.0.1-SNAPSHOT.jar ./app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]