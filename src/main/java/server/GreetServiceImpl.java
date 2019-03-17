package server;

import com.proto.greet.*;
import io.grpc.stub.StreamObserver;

// Greet Service Implimentation
// extend gen GreetServiceImpl
public class GreetServiceImpl extends GreetServiceGrpc.GreetServiceImplBase {

    // Overide greet
    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
//        super.greet(request, responseObserver);

        // Extract the fields from greet
        Greeting greet = request.getGreeting();
        String firstName = greet.getFirstName();

        // Create the response
        String res = "Hello " + firstName;

        // Gen Response
        GreetResponse response = GreetResponse.newBuilder()
                .setResult(res)
                .build();

        // Send response back to client
        responseObserver.onNext(response);

        // Complete the rpc Call
        responseObserver.onCompleted();
    }

    @Override
    public void greetManyTimes(GreetStreamRequest request, StreamObserver<GreetStreamResponse> responseObserver) {
//        super.greetManyTimes(request, responseObserver);
        String firstName = request.getGreeting().getFirstName();

        try {

            for (int i = 0; i < 10; i++) {
                String res = "Hello " + firstName + ", response" + i;

                GreetStreamResponse greetStreamResponse = GreetStreamResponse.newBuilder()
                        .setResult(res)
                        .build();
                responseObserver.onNext(greetStreamResponse);

                Thread.sleep(1000L);

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            responseObserver.onCompleted();
        }

    }

    @Override
    public StreamObserver<LongGreetRequest> longGreet(StreamObserver<LongGreetResponse> responseObserver) {
//        return super.longGreet(responseObserver);
        // This method unlike others return StreamObservor instead of void

        StreamObserver<LongGreetRequest> clientStreamObserver = new StreamObserver<LongGreetRequest>() {

            String result = "";

            @Override
            public void onNext(LongGreetRequest value) {
                // Client send the message

                // Every time client send the msg, append to result
                result += "Thanks " + value.getGreeting().getFirstName() + "! ";
            }

            @Override
            public void onError(Throwable t) {
                // Client send an error
            }

            @Override
            public void onCompleted() {
                // Client has sent and it's complete
                // Typically, Since the client is done. We return the response using responseObserver
                responseObserver.onNext(LongGreetResponse.newBuilder()
                        .setResult(result)
                        .build());

                responseObserver.onCompleted();
            }
        };
        return clientStreamObserver;
    }

    @Override
    public StreamObserver<GreetEveryoneRequest> greetEveryone(StreamObserver<GreetEveryoneResponse> responseObserver) {
//        return super.greetEveryone(responseObserver);
        StreamObserver<GreetEveryoneRequest> requestStreamObserver = new StreamObserver<GreetEveryoneRequest>() {
            @Override
            public void onNext(GreetEveryoneRequest value) {
                String result = "Hello" + value.getGreeting().getFirstName();

                GreetEveryoneResponse greetEveryoneResponse = GreetEveryoneResponse.newBuilder()
                        .setResult(result)
                        .build();

                // OnNext is called every time it receive a message
                responseObserver.onNext(greetEveryoneResponse);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Err :" + t.getMessage());
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
        return requestStreamObserver;
    }
}
