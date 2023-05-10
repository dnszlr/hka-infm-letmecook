FROM openjdk:17-jdk-alpine
CMD java ${JAVA_OPTS} -jar hka-infm-letmecook-0.0.1-SNAPSHOT.jar
COPY build/libs/hka-infm-letmecook-0.0.1-SNAPSHOT.jar .