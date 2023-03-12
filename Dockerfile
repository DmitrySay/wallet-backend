FROM eclipse-temurin:17
WORKDIR /opt
COPY /build/libs/wallet-0.0.1.jar .
ENV JAVA_OPTS=""
CMD java ${JAVA_OPTS} -jar wallet-0.0.1.jar
