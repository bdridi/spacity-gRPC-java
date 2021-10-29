package io.workcale.spacity.grpc;

import static java.util.logging.Logger.getLogger;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import io.workcale.spacity.grpc.SpacityServiceGrpc.SpacityServiceBlockingStub;
import io.workcale.spacity.grpc.SpacityServiceGrpc.SpacityServiceStub;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class GrpcSpacityClient {

  private static final Logger
      logger = getLogger(GrpcSpacityClient.class.getName());

  private final SpacityServiceBlockingStub blockingStub;
  private final SpacityServiceStub nonBlockingStub;
  private List<LaunchSite> launchSites;

  public GrpcSpacityClient(Channel channel) {

    blockingStub = SpacityServiceGrpc.newBlockingStub(channel);
    nonBlockingStub = SpacityServiceGrpc.newStub(channel);
    initializeLaunchSite();
  }

  public void simpleGrpcCallGetMission(){
    Mission mission = blockingStub.simpleRPCgetMission(Rocket.newBuilder()
        .setConfiguration("Hope-gRPC-001_XX")
        .setYear(new Random().nextInt(2020))
        .build());

    logger.info("Mission affected to rocket Hope-gRPC-001_XX is :" + mission.getName());
  }

  public void serverSideStreamingListOfLaunchSitePrices() {
    LaunchSite request = LaunchSite.newBuilder()
        .setName("Boca Chica, Texas")
        .setDescription("Launch site Boca Chica, Texas")
        .build();
    Iterator<Rocket> rockets;
    try {
      logger.info("REQUEST - Launch Site" +request.getName());
      rockets = blockingStub.serverSideStreamingGetListRockets(request);
      for (var i = 1; rockets.hasNext(); i++) {
        Rocket rocket = rockets.next();
        logger.info("RESPONSE - Rocket #" + i + ": "+ rocket.getConfiguration());
      }
    } catch (StatusRuntimeException e) {
      logger.info("RPC failed:" + e.getStatus());
    }
  }

  public void clientSideStreamingGetMostFrequentRocket() {
    StreamObserver<Rocket> responseObserver = new StreamObserver<>() {
      @Override
      public void onNext(Rocket rocket) {
        logger.info("RESPONSE, got Most frequent rocket   - Rocket: "+rocket.getConfiguration()+", description: "+ rocket.getDescription());
      }

      @Override
      public void onCompleted() {
        logger.info("Finished clientSideStreamingGetMostFrequentRocket");
      }

      @Override
      public void onError(Throwable throwable) {
        // Override OnError ...
      }
    };

    StreamObserver<LaunchSite> requestObserver = nonBlockingStub.clientSideStreamingGetCommonRocket(responseObserver);
    try {
      for (LaunchSite LaunchSite : launchSites) {
        logger.info("REQUEST:" + LaunchSite.getName() + ", LaunchSite.getDescription(),"+ LaunchSite.getDescription());
        requestObserver.onNext(LaunchSite);
      }
    } catch (RuntimeException e) {
      requestObserver.onError(e);
      throw e;
    }
    requestObserver.onCompleted();
  }

  public void bidirectionalStreamingGetListsRockets() throws InterruptedException{
    StreamObserver<Rocket> responseObserver = new StreamObserver<>() {
      @Override
      public void onNext(Rocket rocket) {
        logger.info("RESPONSE configuration#"+rocket.getConfiguration()+" : , description:"+rocket.getDescription());
      }

      @Override
      public void onCompleted() {
        logger.info("Finished bidirectionalStreamingGetListsRockets");
      }

      @Override
      public void onError(Throwable throwable) {
        //Override onError() ...
      }

    };

    StreamObserver<LaunchSite> requestObserver = nonBlockingStub.bidirectionalStreamingGetListsRocket(responseObserver);
    try {
      for (LaunchSite LaunchSite : launchSites) {
        logger.info("REQUEST: "+LaunchSite.getName()+" , "+ LaunchSite.getDescription());
        requestObserver.onNext(LaunchSite);
        Thread.sleep(200);
      }
    } catch (RuntimeException e) {
      requestObserver.onError(e);
      throw e;
    }
    requestObserver.onCompleted();
  }

  public static void main(String[] args) throws InterruptedException {
    var target = "localhost:8980";
    if (args.length > 0) {
      target = args[0];
    }

    ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
        .usePlaintext()
        .build();
    try {

      var client = new GrpcSpacityClient(channel);

      client.simpleGrpcCallGetMission();

      client.serverSideStreamingListOfLaunchSitePrices();

      client.clientSideStreamingGetMostFrequentRocket();

      client.bidirectionalStreamingGetListsRockets();

    } finally {
      channel.shutdownNow()
          .awaitTermination(5, TimeUnit.SECONDS);
    }
  }

  private void initializeLaunchSite() {

    this.launchSites = Arrays
        .asList(LaunchSite.newBuilder().setName("Boca Chica, Texas").setCompanyName("SPACEX").setDescription("Aptitude Intel").build()
        , LaunchSite.newBuilder().setName("Boca Chica, California").setCompanyName("Bassel Corp").setDescription("Business Intel").build());
  }
}
