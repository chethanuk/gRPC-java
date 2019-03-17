package client;

import com.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Steps:
 * <p>
 * 1. Create a Channel and client(Sync or Async)
 * 2. Create a ProtoBuff Greeting Message and build
 * 3. Create a Request with Message
 * 4. Create a Response and get the response from RPC call (endpoint)
 * 5. Print or process the response
 */
public class GreetingClient {

    ManagedChannel channel;

    public static void main(String[] args) {
        System.out.println("Hello, gRPC Client");

        // Creating new Greeting Client Object
        GreetingClient main = new GreetingClient();
        // Run the run Function
        main.run();
    }

    // Create a Channel and Call the Unary, ServerStreaming,.. and Shutdown the Channel
    private void run() {
        channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext() // Not preferred in prod
                .build();
        System.out.println("Successfully, Created a Channel");

        // Calling Unary RPC Call Funtion
//        doUnaryRPCCall(channel);

        // Calling Server Streaming RPC Call
//        doServerStreamingCall(channel);

        // Calling Client Streaming RPC Call
        doClientStreamingCall(channel);
        // Shutdown the connection once Api stuff is done and clear the channel
        channel.shutdown();
        System.out.println("Shutdown the Channel");
    }

    private void doUnaryRPCCall(ManagedChannel channel) {

        // GreetService sync Client
        GreetServiceGrpc.GreetServiceBlockingStub syncGreetClient = GreetServiceGrpc.newBlockingStub(channel);

        // Create ProtoBuff Greeting Message
        Greeting greeting = Greeting.newBuilder()
                .setFirstName("A")
                .setLastName("B")
                .build();

        // Create a Greet Request
        GreetRequest greetRequest = GreetRequest.newBuilder()
                .setGreeting(greeting)
                .build();

        // Call the RPC and get back a Response (protoBuffs)
        // gRPC: Make it easy just call func with Request
        GreetResponse response = syncGreetClient.greet(greetRequest);

        // Print Response
        System.out.println(response.getResult());
    }

    private void doServerStreamingCall(ManagedChannel channel) {
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

    }

    /**
     * Steps:
     * 1. Create a Async Client with newStub()
     * 2. Create a Countdown latch and set it with 1, since it we only process once when Server send message. Also, await the latch.
     * 3. Create requestObserver and Implement onNext, onErr and onComplete (countdown the latch after processing)
     * 4. Send the messages using onNext on requestObserver (requestStreamObserver.onNext)
     * 5. Once sending is done, call OnCompleted
     **/
    private void doClientStreamingCall(ManagedChannel channel) {
        // Create a client
        // Streaming Client is Async

        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<LongGreetRequest> requestStreamObserver = asyncClient.longGreet(new StreamObserver<LongGreetResponse>() {
            @Override
            public void onNext(LongGreetResponse value) {
                // get Response from the Server
                System.out.println(" Received a response from Server");
                System.out.println(value.getResult());
            }

            @Override
            public void onError(Throwable t) {
                // error from Server
            }

            @Override
            public void onCompleted() {
                // Server Completed Sending Data
                System.out.println("Server sent the data successfully");

                // Set Countdown: As the job is done call countdown: it set to zero from 1
                latch.countDown();
            }
        });


        // Send 3 messages
        System.out.println("Sending Message");
        requestStreamObserver.onNext(LongGreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .setFirstName("A"))
                .build());

        requestStreamObserver.onNext(LongGreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .setFirstName("B"))
                .build());

        requestStreamObserver.onNext(LongGreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .setFirstName("C"))
                .build());

        // Set as Completed: Client is done sending data
        requestStreamObserver.onCompleted();

        // Wait for response from Server
        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

