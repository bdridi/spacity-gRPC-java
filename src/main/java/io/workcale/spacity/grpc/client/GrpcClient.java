package io.workcale.spacity.grpc.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.workcale.spacity.grpc.RocketRequest;
import io.workcale.spacity.grpc.RocketResponse;
import io.workcale.spacity.grpc.RocketServiceGrpc;

public class GrpcClient {
  public static void main(String[] args) {
    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
        .usePlaintext()
        .build();

    RocketServiceGrpc.RocketServiceBlockingStub stub
        = RocketServiceGrpc.newBlockingStub(channel);

    RocketResponse helloResponse = stub.mission(RocketRequest.newBuilder()
        .setName("Hope")
        .setSerialNumber("gRPC-001_XX")
        .build());

    System.out.println("helloResponse.getMission() = "+helloResponse.getMission());

    channel.shutdown();
  }
}