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
        Integer number = request.getNumber();

        Integer divisor = 2;
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
}
