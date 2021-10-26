package io.workcale.spacity.grpc.server;

import io.grpc.stub.StreamObserver;
import io.workcale.spacity.grpc.RocketRequest;
import io.workcale.spacity.grpc.RocketResponse;
import java.util.Random;

public class RocketServiceImpl extends io.workcale.spacity.grpc.RocketServiceGrpc.RocketServiceImplBase{

  @Override
  public void mission(RocketRequest request, StreamObserver<RocketResponse> responseObserver) {

    String missions[] = new String[] {
        "Inspiration4", "Transporter2", "Crew-2", "Starlink-023", "NROL-11" };
    var affectedMission = missions[new Random().nextInt(missions.length)];
    String mission = new StringBuilder()
        .append("Rocket ")
        .append(request.getName())
        .append(" - ")
        .append(request.getSerialNumber())
        .append(" is affected to mission ")
        .append(affectedMission)
        .toString();

    RocketResponse response = RocketResponse.newBuilder()
        .setMission(mission)
        .build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
