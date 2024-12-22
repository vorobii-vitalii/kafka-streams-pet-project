FROM bellsoft/liberica-runtime-container:jdk-21-stream-musl AS builder

WORKDIR /home/app
ADD . /home/app/project
RUN cd project && ./gradlew build -x test
RUN ls .


FROM bellsoft/liberica-runtime-container:jre-21-musl

COPY --from=builder /home/app/project/build/libs/*.jar app.jar

WORKDIR /home/app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
