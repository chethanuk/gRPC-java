package server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GreetingServer {

    // throws IOException
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Hello gRPC");

        // Server
        Server server = ServerBuilder.forPort(50051)
                .build();

        // start
        server.start();

        // Shutdown: Using Runtime shutdown server [Imp: before await Termination]
        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            System.out.println("Received Shutdown Request");
            server.shutdown();
            System.out.println("Successfully, Stopped Shutdown the server");
        }));

        // await for Termination of Program
        server.awaitTermination();
    }
}
