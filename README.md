# Play Scala gRPC Example

The project was taken from https://github.com/playframework/play-samples/tree/2.8.x/play-scala-grpc-example
          

## Notes

Play 2.8.8, akka-grpc 2.1.1, scala 2.13.7

Had to start a separate gRPC server on 8081
                     

```

curl http://localhost:8080

grpcurl -plaintext localhost:8081 list
grpcurl -plaintext localhost:8081 list helloworld.GreeterService
grpcurl -plaintext -d '{"name":"alice"}' localhost:8081 helloworld.GreeterService/SayHello

```


