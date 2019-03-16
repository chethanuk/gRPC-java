package client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import com.proto.testy.*;

public class GreetingClient {

    public static void main(String[] args) {
        System.out.println("Hello, gRPC Client");

        // in Server: Created a Server builder
        // in Client: ManagedChannel
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
//                .usePlaintext() // Not preferred in prod
                .build();

        System.out.println("Creating Sync Stub");
        // Client: Creating Sync Client using Blocking Stub
        TestyServiceGrpc.TestyServiceBlockingStub syncClient = TestyServiceGrpc.newBlockingStub(channel);

        // Async Client:
        // TestyServiceGrpc.TestyServiceFutureStub asyncClient = TestyServiceGrpc.newFutureStub(channel);

        // Do API stuff

        // Shutdown the connection once Api stuff is done and clear the channel
        channel.shutdown();
        System.out.println("Shutdown the Channel");

    }
}
