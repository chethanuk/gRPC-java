# gRPC Java Basics

### gRPC Unary

Unary RPC calls are basic Request - Response

- Client send one message to server and will receive one response from server
- Suited for small data
- Unary Calls defined using Protocol Buffers
- For each RPC, define a **Request** message and **Response** message
- Naming Convention: Add 'Service' in service name of proto file


### gRPC Server Streaming

- uses HTTP/2 to enable Streaming
- When server need to send lot of data, 
- In proto use stream

### gRPC Streaming Client

- Client Streaming RPC API: Client will **send many messages** to server and will recieve one response from the server at any time
- client sends a stream of requests to the server instead of a single request
- Server sends back a single response, typically but not necessarily after it has received all the client’s requests, along with its status details and optional trailing metadata.
- Example: When Client want to **PUSH** data to server
- in proto file, add word **stream** along with the Request
- Imp: Client should be ASYNC stub instead of blocking stub(sync)
    - Instead of newBlockingStub use `GreetServiceGrpc.newStub`
    - In Client: Have to handle `StreamObserver`. i.e Handling the response from the server    
        - onNext: Will be called once since it's client Streaming
        - Hence, onCompleted will be called just after onNext()
        - Since, it's aysnc stub: Add a CountDown latch
        
### gRPC Bi-Directional Streaming API

- Client will **send many messages** to the server and will **receive many responses** from the server
- Number of requests and responses **does not have to match**
- Best suited when Client and Server need to send lot of data async
- In proto, use **stream** on both request and response


#### Error Codes:

- [Main Errors](https://grpc.io/docs/guides/error.html)
- [A handy guide to gRPC errors](http://avi.im/grpc-errors/) & [Codes](https://github.com/avinassh/grpc-errors)

#### Deadlines

- Deadlines allow gRPC clients to specify how long they are willing to wait for an RPC to complete before the RPC is terminated with the **error DEADLINE_EXCEEDED**
- Recommended to **set Deadline for all client** RPC calls
- gRPC allows clients to specify how long they are willing to wait for an RPC to complete before the RPC is terminated with the error DEADLINE_EXCEEDED
- On the server side, the server can query to see if a particular RPC has timed out, or how much time is left to complete the RPC.
- Deadlines are propagated accross multiple RPC calls when calls are chained 
- [Blog on gRPC Deadlines](https://grpc.io/blog/deadlines)
    - Sets the deadline to **100ms** from when the client RPC is set to when the response is picked up by the client: </br>
        `response = blockingStub.withDeadlineAfter(deadlineMs, TimeUnit.MILLISECONDS).sayHello(request);`
    - When you don’t set a deadline, resources will be held for all in-flight requests, and all requests can potentially reach the maximum timeout. This puts the service at risk of running out of resources, like memory, which would increase the latency of the service, or could crash the entire process in the worst case.
    - To avoid this, services should specify the longest default deadline they technically support, and clients should wait until the response is no longer useful to them
    - Create a Context.current() and check if `!currentContext.isCancelled()`      
