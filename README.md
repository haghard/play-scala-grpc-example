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

do not.




#### How I tried to enable gRPC reflection feature.

Created `AbstractGreeterServiceRouter2` and replaced `AbstractGreeterServiceRouter` with `AbstractGreeterServiceRouter2` in `HelloWorldRouter`.


Now this call ```grpcurl -plaintext localhost:8080 list``` results into:
On the `Play` side  `a.h.i.e.h.Http2ServerDemux - handleOutgoingEnded received unexpectedly in state Closed. This indicates a bug in Akka HTTP, please report it to the issue tracker.`
On the `grpcurl` side `Failed to list services: rpc error: code = Canceled desc = stream terminated by RST_STREAM with error code: CANCEL`




### Logs

```
grpcurl -plaintext localhost:8080 list

[debug] a.i.TcpListener - New connection accepted
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from Idle to WaitingForNetworkToSendControlFrames
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from WaitingForNetworkToSendControlFrames to Idle
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from Idle to WaitingForData
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from WaitingForData to Idle
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from Idle to WaitingForData
[debug] a.h.i.e.h.Http2ServerDemux - Got unhandled event SettingsAckFrame(List())
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from WaitingForData to Idle
[debug] a.h.i.e.h.Http2ServerDemux - Received DATA 8 for stream [1], remaining window space now 65527, buffered: 8
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from Idle to WaitingForData
[debug] a.h.i.e.h.Http2ServerDemux - Dispatched chunk of 8 for stream [1], remaining window space now 65527, buffered: 0
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from WaitingForData to Idle
[debug] a.h.i.e.h.Http2ServerDemux - adjusting con-level window by 0, stream-level window by 446473, remaining window space now 512000, buffered: 0, remaining connection window space now 9999992, total buffered: 0
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from Idle to WaitingForData
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from WaitingForData to Idle
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from Idle to WaitingForData
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from WaitingForData to Idle
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from Idle to WaitingForData
[debug] a.h.i.e.h.Http2ServerDemux - [1] buffered 4980 bytes
[debug] a.h.i.e.h.Http2ServerDemux - [1] sending 4980 bytes, endStream = false, remaining buffer [0], remaining stream-level WINDOW [60555]
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from WaitingForData to Idle
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from Idle to WaitingForData
[warn] a.h.i.e.h.Http2ServerDemux - handleOutgoingEnded received unexpectedly in state Closed. This indicates a bug in Akka HTTP, please report it to the issue tracker.
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from WaitingForData to Idle
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from Idle to WaitingForData
[debug] a.h.i.e.h.Http2ServerDemux - Received DATA 8 for stream [3], remaining window space now 65527, buffered: 8
[debug] a.h.i.e.h.Http2ServerDemux - Updating outgoing connection window by 4980 to 65535
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from WaitingForData to Idle
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from Idle to WaitingForData
[debug] a.h.i.e.h.Http2ServerDemux - Dispatched chunk of 8 for stream [3], remaining window space now 65527, buffered: 0
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from WaitingForData to Idle
[debug] a.h.i.e.h.Http2ServerDemux - adjusting con-level window by 0, stream-level window by 446473, remaining window space now 512000, buffered: 0, remaining connection window space now 9999984, total buffered: 0
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from Idle to WaitingForData
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from WaitingForData to Idle
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from Idle to WaitingForData
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from WaitingForData to Idle
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from Idle to WaitingForData
[debug] a.h.i.e.h.Http2ServerDemux - [3] buffered 4980 bytes
[debug] a.h.i.e.h.Http2ServerDemux - [3] sending 4980 bytes, endStream = false, remaining buffer [0], remaining stream-level WINDOW [60555]
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from WaitingForData to Idle
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from Idle to WaitingForData
[warn] a.h.i.e.h.Http2ServerDemux - handleOutgoingEnded received unexpectedly in state Closed. This indicates a bug in Akka HTTP, please report it to the issue tracker.
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from WaitingForData to Idle
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from Idle to WaitingForData
[debug] a.i.TcpIncomingConnection - Closing connection due to IO error java.io.IOException: Broken pipe
```
            

```
grpcurl -plaintext localhost:8080 helloworld.GreeterService/SayHello


[debug] a.i.TcpListener - New connection accepted
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from Idle to WaitingForNetworkToSendControlFrames
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from WaitingForNetworkToSendControlFrames to Idle
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from Idle to WaitingForData
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from WaitingForData to Idle
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from Idle to WaitingForData
[debug] a.h.i.e.h.Http2ServerDemux - Got unhandled event SettingsAckFrame(List())
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from WaitingForData to Idle
[debug] a.h.i.e.h.Http2ServerDemux - Received DATA 5 for stream [1], remaining window space now 65530, buffered: 5
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from Idle to WaitingForData
[debug] a.h.i.e.h.Http2ServerDemux - Dispatched chunk of 5 for stream [1], remaining window space now 65530, buffered: 0
[debug] a.h.i.e.h.Http2ServerDemux - adjusting con-level window by 0, stream-level window by 446470, remaining window space now 65530, buffered: 0, remaining connection window space now 9999995, total buffered: 0
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from WaitingForData to Idle
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from Idle to WaitingForData
[debug] a.h.i.e.h.Http2ServerDemux - [1] buffered 15 bytes
[debug] a.h.i.e.h.Http2ServerDemux - [1] sending 15 bytes, endStream = false, remaining buffer [0], remaining stream-level WINDOW [65520]
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from WaitingForData to Idle
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from Idle to WaitingForData
[debug] a.h.i.e.h.Http2ServerDemux - [1] buffered 0 bytes
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from WaitingForData to Idle
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from Idle to WaitingForData
[debug] a.h.i.e.h.Http2ServerDemux - Updating outgoing connection window by 15 to 65535
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from WaitingForData to Idle
[debug] a.h.i.e.h.Http2ServerDemux - Changing state from Idle to WaitingForData
[debug] a.i.TcpIncomingConnection - Closing connection due to IO error java.net.SocketException: Connection reset

```

