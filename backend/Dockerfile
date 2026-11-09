# Build from this folder:  docker build -t food-delivery-backend .
# On Render: Root Directory = backend, Runtime = Docker

FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /src
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /src/target/app.jar app.jar
# Seed photos for the starter menu. New uploads also land here.
COPY uploads ./uploads
ENV UPLOAD_DIR=/app/uploads
# Render free tier has 512MB RAM; keep the heap well under that.
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -XX:+UseSerialGC"
EXPOSE 4000
CMD ["java", "-jar", "app.jar"]
