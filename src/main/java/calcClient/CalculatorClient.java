package calcClient;

import com.proto.testy.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/*
1. Create Managed Channel with ip and port
2. Create stub with channel
3.
 */
public class CalculatorClient {
    public static void main(String[] args) {

        CalculatorClient main = new CalculatorClient();

        main.run();


    }

    private void run() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50052)
                .usePlaintext()
                .build();

//        doUnaryCall(channel);

//        doServerStreamingCall(channel);

        doClientStreamingCall(channel);

        // Shutdown the channel
        channel.shutdown();
    }


    private void doServerStreamingCall(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);


        // Server Streaming
        System.out.println("Server Streaming: Prime Number");

        Long number = 2345656655200L;

        stub.primeNumberDecomposition(PrimeNumberDecompositionRequest.newBuilder()
                .setNumber(number)
                .build())
                // Process each response
                .forEachRemaining(primeNumberDecompositionResponse -> {
                    System.out.println(primeNumberDecompositionResponse.getPrimeFactor());
                });

    }

    private void doUnaryCall(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);


        SumRequest sumRequest = SumRequest.newBuilder()
                .setFirstNumber(10)
                .setSecondNmumber(20)
                .build();

        SumResponse sumResponse = stub.sum(sumRequest);

        System.out.println(sumRequest.getFirstNumber() + " + " + sumRequest.getSecondNmumber() + " =  " + sumResponse.getSumResult());

        // Sub
        SubRequest subRequest = SubRequest.newBuilder()
                .setFirstNumber(20)
                .setSecondNmumber(5)
                .build();

        SubResponse subResponse = stub.sub(subRequest);

        System.out.println(subRequest.getFirstNumber() + " - " + subRequest.getSecondNmumber() + " = " + subResponse.getSubResult());
    }


    private void doClientStreamingCall(ManagedChannel channel) {
        // Async Stub
        CalculatorServiceGrpc.CalculatorServiceStub asyncStub = CalculatorServiceGrpc.newStub(channel);

        // Latch
        CountDownLatch latch = new CountDownLatch(1);

        // Compute AVG
        StreamObserver<ComputeAverageRequest> requestStreamObserver = asyncStub.computeAverage(new StreamObserver<ComputeAverageResponse>() {
            @Override
            public void onNext(ComputeAverageResponse value) {
                System.out.println(" Received a response from Server");
                System.out.println(value.getAverage());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println(t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Server has Completed sending data: Average");

                latch.countDown();
            }
        });

        // Sending Messages
        for (int i = 0; i < 100000; i++) {
            requestStreamObserver.onNext(ComputeAverageRequest.newBuilder()
                    .setNumber(i)
                    .build());
        }

        requestStreamObserver.onCompleted();

        try {
            latch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
