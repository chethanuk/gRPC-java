package calcServer;

import com.proto.testy.*;
import io.grpc.stub.StreamObserver;

public class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {

    @Override
    public void sum(SumRequest request, StreamObserver<SumResponse> responseObserver) {
//        super.sum(request, responseObserver);

        // Add
        SumResponse sumResponse = SumResponse.newBuilder()
                .setSumResult(request.getFirstNumber() + request.getSecondNmumber())
                .build();

        // Add
        responseObserver.onNext(sumResponse);

        responseObserver.onCompleted();
    }

    @Override
    public void sub(SubRequest request, StreamObserver<SubResponse> responseObserver) {
//        super.sub(request, responseObserver);
        // Sub
        SubResponse subResponse = SubResponse.newBuilder()
                .setSubResult(request.getFirstNumber() - request.getSecondNmumber())
                .build();

        // Add
        responseObserver.onNext(subResponse);

        responseObserver.onCompleted();
    }

    @Override
    public void primeNumberDecomposition(PrimeNumberDecompositionRequest request, StreamObserver<PrimeNumberDecompositionResponse> responseObserver) {
//        super.primeNumberDecomposition(request, responseObserver);
        // Get the request number
        Long number = request.getNumber();

        Long divisor = 2L;
        // Prime Algorithm
        while (number > 1) {
            if (number % divisor == 0) {
                number = number / divisor;

                responseObserver.onNext(
                        PrimeNumberDecompositionResponse.newBuilder()
                                .setPrimeFactor(divisor)
                                .build());
            } else {
                divisor = divisor + 1;
            }
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<ComputeAverageRequest> computeAverage(StreamObserver<ComputeAverageResponse> responseObserver) {

        StreamObserver<ComputeAverageRequest> requestStreamObserver = new StreamObserver<ComputeAverageRequest>() {

            // Dynamic Sum and count
            int sum = 0;
            int count = 0;

            @Override
            public void onNext(ComputeAverageRequest value) {
//                System.out.println("");
                sum += value.getNumber();
                count += 1;
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Error: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                // Server Sending
                double avg = (double) sum / count;

                System.out.println(" Server sending data");
                responseObserver.onNext(
                        ComputeAverageResponse.newBuilder()
                                .setAverage(avg)
                                .build()
                );
                responseObserver.onCompleted();
            }

        };
        return requestStreamObserver;
    }

    @Override
    public StreamObserver<findMaxRequest> findMax(StreamObserver<findMaxResponse> responseObserver) {

        return new StreamObserver<findMaxRequest>() {
            int currentMax = 0;

            @Override
            public void onNext(findMaxRequest value) {
                int currentNum = value.getNumber();

                if (currentNum > currentMax) {
                    currentMax = currentNum;

                    // Return response
                    responseObserver.onNext(
                            findMaxResponse.newBuilder()
                                    .setMax(currentMax)
                                    .build()
                    );

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Do Nothing
                }

            }

            @Override
            public void onError(Throwable t) {
                System.out.println(t.getMessage());

                // Set Complete
                responseObserver.onCompleted();
            }

            @Override
            public void onCompleted() {
                // Client Completed Sending data

                // Send the final data
                System.out.println("Final Max: ");
                responseObserver.onNext(
                        findMaxResponse.newBuilder()
                                .setMax(currentMax)
                                .build());

                responseObserver.onCompleted();
            }
        };
    }
}
