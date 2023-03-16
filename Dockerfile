FROM eclipse-temurin:17
WORKDIR /opt
COPY /build/libs/wallet-*.jar ./wallet.jar
ENV JAVA_OPTS=""
CMD java ${JAVA_OPTS} -jar wallet.jar
