# Distributed Tracing

## Build and run project
Run project directly from build engine with full path to [a text file](./src/test/resources/test.txt):
```
./gradlew run --args="'"/projects/samples/test.txt"'"
```

or build project and execute `jar` file with similar arguments
```
./gradlew build
./java -jar ./build/libs/distributed-tracing-1.0.0-SNAPSHOT.jar "/projects/samples/test.txt"
```

