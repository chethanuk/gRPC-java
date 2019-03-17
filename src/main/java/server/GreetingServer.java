package server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.File;
import java.io.IOException;

public class GreetingServer {

    // throws IOException
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Hello gRPC");

        // Server without SSL: PlainText
//        Server server = ServerBuilder.forPort(50051)
//                .addService(new GreetServiceImpl())
//                .build();
//
        // Secured SSL Server
        Server server = ServerBuilder.forPort(50051)
                .addService(new GreetServiceImpl())
                .useTransportSecurity(
                        // reference file
                        new File("./src/ssl/server.crt"),
                        new File("./src/ssl/server.pem")
                )
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
