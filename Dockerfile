FROM eclipse-temurin:25-jdk-jammy AS builder

WORKDIR /workspace

COPY ssbackend/ ssbackend/
COPY sscommon/ sscommon/

WORKDIR /workspace/ssbackend
RUN chmod +x ./gradlew && ./gradlew --no-daemon clean bootWar -x test

RUN cp build/libs/*.war /tmp/ssbackend.war

FROM eclipse-temurin:25-jre-jammy AS runtime

WORKDIR /app

COPY --from=builder /tmp/ssbackend.war /app/ssbackend.war

ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/ssbackend.war"]
