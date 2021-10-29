package io.workcale.spacity.grpc;

import io.grpc.stub.StreamObserver;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class SpacityService extends SpacityServiceGrpc.SpacityServiceImplBase {

  List<String> rocketNames = new ArrayList<>();

  @Override
  public void serverSideStreamingGetListRockets(LaunchSite request, StreamObserver<Rocket> responseObserver) {
    for (var i = 1; i <= 5; i++) {
      Rocket rocket = Rocket.newBuilder()
          .setConfiguration("Falcon-"+i)
          .setYear(new Random().nextInt(2020))
          .setDescription("Rocket launched on launch site "+ request.getName())
          .build();
      responseObserver.onNext(rocket);
    }
    responseObserver.onCompleted();
  }

  @Override
  public StreamObserver<LaunchSite> clientSideStreamingGetCommonRocket(StreamObserver<Rocket> responseObserver) {

    return new StreamObserver<>() {
      @Override
      public void onNext(LaunchSite launchSite) {
        for(var i=1; i<=5; i++){
          rocketNames.add("Falcon-"+new Random().nextInt(5));
        }
      }

      @Override
      public void onCompleted() {
        var mostPresentRocket = findTopFrequentRocket();
        responseObserver.onNext(Rocket.newBuilder()
            .setConfiguration(mostPresentRocket)
            .build());
        responseObserver.onCompleted();
      }

      @Override
      public void onError(Throwable throwable) {
        // handle onError() ...
      }
    };
  }

  @Override
  public StreamObserver<LaunchSite> bidirectionalStreamingGetListsRocket(StreamObserver<Rocket> responseObserver) {
    return new StreamObserver<>() {
      @Override
      public void onNext(LaunchSite request) {
        for (var i = 1; i <= 5; i++) {
            Rocket rocket = Rocket.newBuilder()
                .setConfiguration("Falcon-"+i)
                .setYear(new Random().nextInt(2020))
                .setDescription("Rocket launched on launch site "+ request.getName())
                .build();
            responseObserver.onNext(rocket);
        }
      }

      @Override
      public void onCompleted() {
        responseObserver.onCompleted();
      }

      @Override
      public void onError(Throwable throwable) {
        //handle OnError() ...
      }
    };
  }

  @Override
  public void simpleRPCgetMission(Rocket request, StreamObserver<Mission> responseObserver) {
    var missions = new String[] {
        "Inspiration4", "Transporter2", "Crew-2", "Starlink-023", "NROL-11" };
    var affectedMission = missions[new Random().nextInt(missions.length)];
    Mission response = Mission.newBuilder()
        .setName(affectedMission)
        .build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  private String findTopFrequentRocket() {
    var groupedRockets = rocketNames.stream().collect(Collectors.groupingBy(r -> r, Collectors.counting()));
    var topFrequent =  groupedRockets.values()
        .stream()
        .max(Long::compareTo)
        .get();
    return groupedRockets.entrySet().stream().filter(e -> e.getValue().equals(topFrequent)).findFirst().get().getKey();
  }
}
