FROM openjdk:21-slim
LABEL authors="Emir"
EXPOSE 8081
ADD target/GreenStreak-1.0.0.jar GreenStreak.jar
ENTRYPOINT ["java", "-jar", "GreenStreak.jar"]