# Spacity Demo gRPC with Java 

## About this project

Hands on client-server communication using gRPC system  

### Built with

- Java
- gRPC

## Getting Started

### Prerequisites

To run this project locally, you have to install:
- Java JDK 11.
- Maven 3.x

### Generate the code 
Using the protobuf maven plugin we can generate the java files from the `spacity.proto` file. 

```bash
mvn clean package
```

### Set Generated sources as Source Folder
You need to declare the folder `target/generated-sources` as a source folder in `IntelliJ` to be able to resolve 
dependencies in the main java code.

### Run the server 

Run the class `io.workcale.spacity.grpc.server.GrpcServer.java` 

### Run the client 

Run the class `io.workcale.spacity.grpc.client.GrpcClient.java`. You should see in the console a log like the following 
`message helloResponse.getMission() = Rocket Hope - gRPC-001_XX is affected to mission Starlink-023`

