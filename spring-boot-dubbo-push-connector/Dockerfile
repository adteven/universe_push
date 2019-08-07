FROM java:8-jre
ADD ./target/spring-boot-dubbo-push-connector-1.0.0-SNAPSHOT.jar /app/
CMD ["java", "-Xmx300m", "-jar", "/app/spring-boot-dubbo-push-connector-1.0.0-SNAPSHOT.jar"]
EXPOSE 6789