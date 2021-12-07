# Notes

POC that generates `routes` file and a Play controller from a protobuf definition file.


## Now it supposed to work. Steps

1. We start with an empty Play project that has support for `play-grpc-generators` and `akka-grpc`.
2. 
3. Invoke
```

genPlayArtifacts
compile
run 

```

3. These should go through 
 
```


http --verbose GET ":8080/v1/messages/jack/age/2?weight=11&status=true"
http --verbose GET ":8080/v1/messages2/jack?age=123&weight=11&status=true"

http --verbose GET ":8080/download"
http --verbose GET ":8080/helloworld"
  
```  
