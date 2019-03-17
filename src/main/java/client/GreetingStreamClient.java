package client;

import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.GreetStreamRequest;
import com.proto.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * Steps:
 * <p>
 * 1. Create a Channel and client(Sync or Async)
 * 2. Create a ProtoBuff Greeting Message and build
 * 3. Create a Request with Message
 * 4. Create a Response and get the response from RPC call (endpoint)
 * 5. Print or process the response
 */
public class GreetingStreamClient {

    public static void main(String[] args) {
        System.out.println("Hello, gRPC Client");

        // in Server: Created a Server builder
        // in Client: ManagedChannel
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext() // Not preferred in prod
                .build();

        System.out.println("Creating Sync Stub");
        /**
         * Steps:
         *
         * 1. Create a Channel and client(Sync or Async)
         * 2. Create a ProtoBuff Greeting Message and build
         * 3. Create a Request with Message
         * 4. Create a Response and get the response from RPC call (endpoint)
         * 5. Print or process the response
         */

        // GreetService sync Client
        GreetServiceGrpc.GreetServiceBlockingStub syncGreetClient = GreetServiceGrpc.newBlockingStub(channel);


        GreetStreamRequest greetStreamResponse = GreetStreamRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .setFirstName("ABC"))
                .build();

        // Server Streaming: stream response in a blocking manner
        syncGreetClient.greetManyTimes(greetStreamResponse)
                .forEachRemaining(greetStreamResponse1 -> {
                    System.out.println(greetStreamResponse1.getResult());
                });

        // Shutdown the connection once Api stuff is done and clear the channel
        channel.shutdown();
        System.out.println("Shutdown the Channel");

    }
}
