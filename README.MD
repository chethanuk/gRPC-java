# gRPC Java Basics

### gRPC Unary

Unary RPC calls are basic Request - Response

- Client send one message to server and will receive one response from server
- Suited for small data
- Unary Calls defined using Protocol Buffers
- For each RPC, define a **Request** message and **Response** message
- Naming Convention: Add 'Service' in service name of proto file
