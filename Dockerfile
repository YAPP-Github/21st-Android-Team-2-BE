FROM openjdk:17-jdk-slim-buster

ARG SPRING_PROFILES_ACTIVE
ENV SPRING_PROFILES_ACTIVE $SPRING_PROFILES_ACTIVE
ENV TZ "Asia/Seoul"
ENV JAR_FILE build/libs/item-finder-0.0.1-SNAPSHOT.jar
ENV JAVA_OPTS="-Dspring.profiles.active=$SPRING_PROFILES_ACTIVE"

WORKDIR /app
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
COPY ${JAR_FILE} app.jar

ENTRYPOINT java $JAVA_OPTS -jar app.jar
