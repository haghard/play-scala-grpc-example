# Notes

POC that generates `routes` file and a Play controller from a protobuf definition file.


## Now it supppose to work Steps

1. We start with an emply Play project that has support for `play-grpc-generators` and `akka-grpc`.
2. Invoke
```

genPlayArtifacts
compile
run 

```

3. You should be able to call these and get responses back 
 
```
http --verbose POST http://127.0.0.1:8080/sayHello  name="alice" age=128
http --verbose POST http://127.0.0.1:8080/sayHello1 name="alice"
  
```  
    

## Notes 

Software we rely upon 

1. `play-grpc-generators` and `akka-grpc`.
2. `"com.thesamet.scalapb" %% "scalapb-json4s"` to parse json requests into protobuf messages.