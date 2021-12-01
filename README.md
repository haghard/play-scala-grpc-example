# Play Scala gRPC Example

The project was taken from https://github.com/playframework/play-samples/tree/2.8.x/play-scala-grpc-example


# Notes

I disabled https and left http only.


### How to run in dev mode

```
  sbt run 
```

curl http://localhost:8080


Without refliction

```      
   grpcurl -plaintext -import-path ./play-scala-grpc-example/app/protobuf -proto helloworld.proto localhost:8080 helloworld.GreeterService/SayHello
```



With refliction

```
   grpcurl -plaintext localhost:8080 list
   grpcurl -plaintext localhost:8080 helloworld.GreeterService/SayHello
``` 
    

http --verbose POST http://192.168.77.106:8080/sayHello name="alice" age=128
    
    

