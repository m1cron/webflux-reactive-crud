# Webflux-Reactive-CRUD

## Run
```
$ docker pull mvertes/alpine-mongo
$ docker build -t mvertes/alpine-mongo .
$ docker run -d --name mongo -p 27017:27017 mvertes/alpine-mongo
$ gradle wrapper
$ ./gradlew
$ java -jar ./service/build/libs/webflux-reactive-crud-1.0.jar
```

http://localhost:8080/swagger-ui.html