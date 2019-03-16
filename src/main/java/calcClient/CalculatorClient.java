package calcClient;

import com.proto.testy.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/*
1. Create Managed Channel with ip and port
2. Create stub with channel
3.
 */
public class CalculatorClient {
    public static void main(String[] args) {

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50052)
                .usePlaintext()
                .build();

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

        // Shutdown the channel
        channel.shutdown();
    }
}
