FROM maven:3.9.9-eclipse-temurin-21

RUN apt-get update && \
    apt-get install -y \
    xvfb \
    xauth \
    xorg \
    libgtk-3-0 \
    libxtst6 \
    libxrender1 \
    libxi6 \
    libxext6 \
    libgl1 \
    libasound2t64 \
    fontconfig && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY pom.xml .

RUN mvn -q dependency:resolve || true

COPY . .

RUN rm -f RUNNING_PID

EXPOSE 8080

CMD sh -c "xvfb-run -a mvn clean jpro:run -Djpro.port=${PORT:-8080} -Djava.awt.headless=false"FROM maven:3.9.9-eclipse-temurin-21

                                                                                              RUN apt-get update && \
                                                                                                  apt-get install -y \
                                                                                                  xvfb \
                                                                                                  xauth \
                                                                                                  xorg \
                                                                                                  libgtk-3-0 \
                                                                                                  libxtst6 \
                                                                                                  libxrender1 \
                                                                                                  libxi6 \
                                                                                                  libxext6 \
                                                                                                  libgl1 \
                                                                                                  libasound2t64 \
                                                                                                  fontconfig && \
                                                                                                  apt-get clean && \
                                                                                                  rm -rf /var/lib/apt/lists/*

                                                                                              WORKDIR /app

                                                                                              COPY pom.xml .

                                                                                              RUN mvn -q dependency:resolve || true

                                                                                              COPY . .

                                                                                              RUN rm -f RUNNING_PID

                                                                                              EXPOSE 8080

                                                                                              CMD sh -c "xvfb-run -a mvn clean jpro:run -Djpro.port=${PORT:-8080} -Djava.awt.headless=false"