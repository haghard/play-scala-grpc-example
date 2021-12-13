# Notes


Generated routes based on ./app/protibuf/messaging.proto schema.

 
```

http --verbose GET ":8080/v1/messages/jack/age/1?weight=11&status=true&height=3456"
http --verbose GET ":8080/v1/messages/jack?age=123&weight=11&status=true&height=3456"
http --verbose GET ":8080/v1/messages2/jack?age=123&weight=11&status=true&height=3456"


http --verbose POST :8080/v1/messages  authorId="alice" regionId=1 text=adfgadfg when=1323
http --verbose POST :8080/v1/messages/alice/region/1 authorId="alice" regionId=1 text=adfgadfg when=1323                   

  
```
