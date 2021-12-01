# Play Scala gRPC Example

The project was taken from https://github.com/playframework/play-samples/tree/2.8.x/play-scala-grpc-example


```
  sbt run 
```


```
    
   curl http://localhost:8080   
   grpcurl -plaintext -import-path ./play-scala-grpc-example/app/protobuf -proto helloworld.proto localhost:8080 helloworld.GreeterService/SayHello                       
    
```


Calls that rely on the reflection feature

```
   grpcurl -plaintext localhost:8080 list
   grpcurl -plaintext localhost:8080 helloworld.GreeterService/SayHello
   grpcurl -plaintext -d '{"name":"alice"}' localhost:8080 helloworld.GreeterService/SayHello
    
```