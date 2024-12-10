FROM openjdk:23-jdk AS builder


ARG APP_DIR=/app
WORKDIR ${APP_DIR}

COPY mvnw .
COPY mvnw.cmd .
COPY pom.xml .
COPY .mvn .mvn
COPY src src


RUN chmod a+x ./mvnw && ./mvnw clean package -Dmaven.test.skip=true


## Stage 2 ##
FROM openjdk:23-jdk

ARG DEPLOY_DIR=/app
WORKDIR ${DEPLOY_DIR}

COPY --from=builder /app/target/august-2022-assessment-practice-0.0.1-SNAPSHOT.jar ssf-practice-assessment-3.jar

ENV SERVER_PORT=3000
EXPOSE ${SERVER_PORT}

ENTRYPOINT java -jar ssf-practice-assessment-3.jar