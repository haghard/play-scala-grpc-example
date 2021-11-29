# Play Scala gRPC Example

The project was taken from https://github.com/playframework/play-samples/tree/2.8.x/play-scala-grpc-example


# Notes

I disabled https and left http only.



### How to run in dev mode

```
  sbt run 
```
   

These works

```
   curl http://localhost:8080   
   grpcurl -plaintext -import-path ./play-scala-grpc-example/app/protobuf -proto helloworld.proto localhost:8080 helloworld.GreeterService/SayHello
                  
``` 
   

but calls that rely on the reflection feature  

```
   grpcurl -plaintext localhost:8080 list
   grpcurl -plaintext localhost:8080 helloworld.GreeterService/SayHello
    
```

do not

            


#### How I tried to enable gRPC reflection feature.

Created `AbstractGreeterServiceRouter2` and replaced `AbstractGreeterServiceRouter` with `AbstractGreeterServiceRouter2` in `HelloWorldRouter`.


Now this call ```grpcurl -plaintext localhost:8080 list``` results into:
On the `Play` side  `a.h.i.e.h.Http2ServerDemux - handleOutgoingEnded received unexpectedly in state Closed. This indicates a bug in Akka HTTP, please report it to the issue tracker.`
On the `grpcurl` side `Failed to list services: rpc error: code = Canceled desc = stream terminated by RST_STREAM with error code: CANCEL`