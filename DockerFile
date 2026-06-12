FROM maven:3.9.9-eclipse-temurin-21

RUN apt-get update && \
    apt-get install -y xorg libgtk-3-0 libxtst6 libxrender1 libxi6 && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY pom.xml .

RUN mvn -q dependency:resolve

COPY . .

EXPOSE 8080

CMD sh -c "mvn clean jpro:run -Djpro.port=${PORT:-8080}"