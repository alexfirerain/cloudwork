FROM openjdk:17
VOLUME /log
EXPOSE 9090
COPY build/libs/cloudwork-0.0.1-SNAPSHOT.jar /cloudwork.jar
ENTRYPOINT ["java", "-jar", "/cloudwork.jar"]