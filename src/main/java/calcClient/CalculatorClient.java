package calcClient;

import com.proto.testy.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
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

//        doClientStreamingCall(channel);

        doBiDiStreamingCall(channel);

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

    private void doBiDiStreamingCall(ManagedChannel channel) {
        // Async Stub
        CalculatorServiceGrpc.CalculatorServiceStub asyncStub = CalculatorServiceGrpc.newStub(channel);

        // Latch
        CountDownLatch latch = new CountDownLatch(1);

        // Compute AVG
        StreamObserver<findMaxRequest> findMaxRequestStreamObserver = asyncStub.findMax(new StreamObserver<findMaxResponse>() {
            @Override
            public void onNext(findMaxResponse value) {
                System.out.println("Received current Max: " + value.getMax());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println(t.getMessage());
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("Server is done sending messages: ");
            }
        });

        // Sending Messages
//        for (int i = 0; i < 100000; i++) {
//            findMaxRequestStreamObserver.onNext(findMaxRequest.newBuilder()
//                    .setNumber(i)
//                    .build());
//            System.out.println("Sending: "+ i);
//        }

        Arrays.asList(1, 3, 5, 25, 7, 30, 20, 32, 10).forEach(integer -> {
            System.out.println("Sending: " + integer);

            findMaxRequestStreamObserver.onNext(findMaxRequest.newBuilder()
                    .setNumber(integer)
                    .build());

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        findMaxRequestStreamObserver.onCompleted();

        try {
            latch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
