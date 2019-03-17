package client;

import com.proto.greet.*;
import io.grpc.*;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
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
//        doClientStreamingCall(channel);

//        doBiDiStreamingCall(channel);

        doUnaryWithDeadlineRPCCall(channel);
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

    private void doBiDiStreamingCall(ManagedChannel channel) {
        // Streaming Client is Async

        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<GreetEveryoneRequest> requestStreamObserver = asyncClient.greetEveryone(new StreamObserver<GreetEveryoneResponse>() {
            @Override
            public void onNext(GreetEveryoneResponse value) {
                System.out.println("Received response from Server: " + value.getResult());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Error: " + t.getMessage());
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("Server completed sending messages");
                latch.countDown();
            }
        });

        Arrays.asList("A", "B", "C", "D", "E").forEach(


                name -> {
                    System.out.println("Sending: " + name);

                    requestStreamObserver.onNext(
                            GreetEveryoneRequest.newBuilder()
                                    .setGreeting(Greeting.newBuilder().setFirstName(name)) // Create New Greeting
                                    .build());

                    // Sleep for 20mil
                    try {
                        Thread.sleep(750);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        );

        requestStreamObserver.onCompleted();

        try {
            latch.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void doUnaryWithDeadlineRPCCall(ManagedChannel channel) {
        // Create blocking stub
        GreetServiceGrpc.GreetServiceBlockingStub greetServiceBlockingStub = GreetServiceGrpc.newBlockingStub(channel);

        // First Call: 500ms deadling
        try {
            System.out.println("Sending a request with deadline 500ms");
            GreetWithDeadlineResponse greetWithDeadlineResponse =
                    greetServiceBlockingStub.withDeadline(Deadline.after(1000, TimeUnit.MILLISECONDS)).greetWithDeadline(
                            GreetWithDeadlineRequest.newBuilder()
                                    .setGreeting(Greeting.newBuilder().setFirstName("A: Deadline500ms"))
                                    .build()
                    );

            System.out.println("Response from Server: " + greetWithDeadlineResponse.getResult());
        } catch (StatusRuntimeException e) {
            if (e.getStatus() == Status.DEADLINE_EXCEEDED) {
                System.out.println("Deadline Exceeded: RPC is slow and Response will not be sent");
            } else {
                e.printStackTrace();
            }
        }

        // First Call: 300ms deadling
        try {
            System.out.println("Sending a request with deadline 2000ms");
            GreetWithDeadlineResponse greetWithDeadlineResponse =
                    greetServiceBlockingStub.withDeadlineAfter(2, TimeUnit.SECONDS).greetWithDeadline(
                            GreetWithDeadlineRequest.newBuilder()
                                    .setGreeting(Greeting.newBuilder().setFirstName("B: Deadline-2000ms"))
                                    .build()
                    );

            System.out.println("Response from Server: " + greetWithDeadlineResponse.getResult());
        } catch (StatusRuntimeException e) {
            if (e.getStatus() == Status.DEADLINE_EXCEEDED) {
                System.out.println("Deadline Exceeded: RPC is slow and Response will not be sent");
            } else {
                e.printStackTrace();
            }
        }

// First Call: 100ms deadling
        try {
            System.out.println("Sending a request with deadline 305ms");

            // Response fail if duration is 304 or less
            // RPC need 305 secs
            GreetWithDeadlineResponse greetWithDeadlineResponse =
                    greetServiceBlockingStub.withDeadline(Deadline.after(304, TimeUnit.MILLISECONDS)).greetWithDeadline(
                            GreetWithDeadlineRequest.newBuilder()
                                    .setGreeting(Greeting.newBuilder().setFirstName("C- Deadline:305ms"))
                                    .build()
                    );

            System.out.println("Response from Server: " + greetWithDeadlineResponse.getResult());
        } catch (StatusRuntimeException e) {
            if (e.getStatus() == Status.DEADLINE_EXCEEDED) {
                System.out.println("Deadline Exceeded: RPC is slow and Response will not be sent");
            } else {
                e.printStackTrace();
            }
        }
    }
}
