# Notes

POC that generates `routes` file and a Play controller from a protobuf definition file.

## Sbt commands

```

genPlayArtifacts
compile
run 

```

Right after that you should be able to call these 
 
```
http --verbose POST http://127.0.0.1:8080/sayHello  name="alice" age=128
http --verbose POST http://127.0.0.1:8080/sayHello1 name="alice"
  
```  
    

