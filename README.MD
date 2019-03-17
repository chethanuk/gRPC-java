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

#### SSL & AUTH

- gRPC has SSL/TLS integration and promotes the use of SSL/TLS to authenticate the server, and to encrypt all the data exchanged between the client and the server. 
- Optional mechanisms are available for clients to provide certificates for mutual authentication.
- Client send the encrypted message, no one can decrypt except the RPC server
- TLS: Transport Layer Security
- Two ways of SSL in gRPC:
    - 1-way (Encryption): Browser: Verify Webserver
    - 2-way (Authentication): SSL authentication
- Steps:
    - Create Certificate Authority (CA)
    - Server create PEM private key
    - Server send OEM and receive CRT from CA: Using private key, request the CA, CA will sign if valid and send back signed cert: CRT
    - Client has CA Root Public Certificate
    - During SSL handshake, 
        1. gRPC server send signed SSL certificate
        2. gRPC client will verify SSL certificate from Server
        3. Secure SSL encrypted Communication b/w Client and Server will be established
- In gRPC, we need to setup Server PEM, CRT, Certificate Authority and Route: gRPC handle rest
     - Setup a certificate authority
     - Setup a server certificate 
     - Sign a server certificate
     - setup the Server to use TLS
     - setup the client to connect securely over TLS

- [Docs](https://grpc.io/docs/guides/auth.html)
- [Java Auth Docs](https://grpc.io/docs/guides/auth.html#java)
- For dev, use normal channel with `.usePlainText()`
- For prod, use secured channel with `.sslContext()`
- Most dependency versioning problems can be solved by using `io.grpc:grpc-netty-shaded` instead of io.grpc:grpc-netty in build.gradle
    - Remove io.grpc:grpc-netty dependency
    - Add io.grpc:grpc-netty-shaded dependency
- Check `./ssl/instructions.sh` and install openssl: `sudo apt-get install libssl-dev` or from [link](https://stackoverflow.com/a/51565653/11021362)
    - Steps:
        1. Cd into ssl dir: `cd src/ssl/`
        2. Make sh executable: `chmod +x instructions.sh `
        3. Execute to generate all files: `./instructions.sh`
        
        
        

####  Server Reflections

- gRPC Server Reflection provides information about publicly-accessible gRPC services on a server, and assists clients at runtime with constructing RPC requests and responses without precompiled service information
- [Docs](https://github.com/grpc/grpc-java/blob/master/documentation/server-reflection-tutorial.md)
- gRPC command line tool (gRPC CLI), which can be used to introspect server protos and send/receive test RPCs
- Reflection is only supported for proto-based services.
     
  
     