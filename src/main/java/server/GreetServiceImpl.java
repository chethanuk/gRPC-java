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
}
