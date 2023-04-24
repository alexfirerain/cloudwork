FROM openjdk:17
LABEL authors="Александр Лукъянов"
VOLUME /log
EXPOSE 9090
COPY /build/libs/cloudwork-0.0.1-SNAPSHOT.jar cloudwork.jar
ENTRYPOINT ["java", "-jar", "cloudwork.jar"]