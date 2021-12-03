# Notes

POC that generates `routes` file and a Play controller from a protobuf definition file.


## Now it supposed to work. Steps

1. We start with an emply Play project that has support for `play-grpc-generators` and `akka-grpc`.
2. Invoke
```

genPlayArtifacts
compile
run 

```

3. These should go through 
 
```
http --verbose POST :8080/sayHello  name="alice" age=128
http --verbose POST :8080/sayHello2 name="alice"
  
```  
    

## Notes 

Currently, we support only http post requests.


Software we rely upon 

1. `play-grpc-generators` and `akka-grpc`.
2. `"com.thesamet.scalapb" %% "scalapb-json4s"` to parse json requests into protobuf messages.

                          
### Next steps

1.  gRPC Transcoding (parse and codegen)
2. `PlayArtifactsGenerator` should be moved to a separate project or create sbt plugin.