package io.workcale.spacity.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;

public class GrpcServer {

  public static void main(String[] args) throws InterruptedException, IOException {
    Server server = ServerBuilder
        .forPort(8980)
        .addService(new SpacityService())
        .build();

    server.start();
    System.out.println("Server started...");
    server.awaitTermination();
  }
}
