FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /build

COPY pom.xml .
COPY src ./src

RUN mvn -B -DskipTests clean package

FROM eclipse-temurin:21-jre
WORKDIR /app

RUN addgroup --system spring && adduser --system --ingroup spring spring

COPY --from=build /build/target/*.jar /app/app.jar

ENV SERVER_PORT=8081
ENV APP_UPLOAD_DIR=/data/uploads

EXPOSE 8081

USER spring

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
