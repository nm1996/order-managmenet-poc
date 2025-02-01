package com.nm.order.management.userservice.server;

import com.nm.order.management.proto_common.VersionRequest;
import com.nm.order.management.proto_common.VersionResponse;
import com.nm.order.management.proto_common.VersionServiceGrpc.VersionServiceImplBase;
import com.nm.order.management.common.version.VersionConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@RequiredArgsConstructor
@GrpcService
public class VersionServerImpl extends VersionServiceImplBase {

    private final VersionConfig versionConfig;

    @Override
    public void getVersion(VersionRequest request, io.grpc.stub.StreamObserver<VersionResponse> responseObserver) {
        log.info("Sending service version [service={}]", "user-service");
        try {
            String version = versionConfig.getVersion();

            VersionResponse response = VersionResponse.newBuilder()
                    .setVersion(version)
                    .build();

            responseObserver.onNext(response);
        } catch (Exception e) {
            log.error("Error while sending service version [service = {}, msg ={}]", "user-service", e.getMessage());
            responseObserver.onError(e);
        } finally {
            responseObserver.onCompleted();
        }
    }

}
