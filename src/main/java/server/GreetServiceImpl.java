package server;

import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
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
}
