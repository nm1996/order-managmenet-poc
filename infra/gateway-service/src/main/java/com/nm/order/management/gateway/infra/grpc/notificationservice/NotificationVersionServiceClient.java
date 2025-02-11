package com.nm.order.management.gateway.infra.grpc.notificationservice;

import com.nm.order.management.common.cloud.service.implementation.ServiceCloudOrchestratorImpl;
import com.nm.order.management.common.grpc.AbstractServiceClient;
import com.nm.order.management.proto_common.common.VersionRequest;
import com.nm.order.management.proto_common.common.VersionResponse;
import com.nm.order.management.proto_common.common.VersionServiceGrpc;
import io.grpc.ManagedChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class NotificationVersionServiceClient extends AbstractServiceClient<VersionServiceGrpc.VersionServiceBlockingStub> {
    private static final String SERVICE_NAME = "NOTIFICATION-SERVICE";

    @Autowired
    public NotificationVersionServiceClient(ServiceCloudOrchestratorImpl serviceCloudOrchestrator) {
        super(serviceCloudOrchestrator);
    }



    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    @Override
    public VersionServiceGrpc.VersionServiceBlockingStub createStub(ManagedChannel channel) {
        return VersionServiceGrpc.newBlockingStub(channel);
    }

    public Optional<VersionResponse> getVersion() {
        try {
            VersionRequest request = VersionRequest.newBuilder().build();
            return Optional.ofNullable(getNextServer().getVersion(request));
        } catch (Exception e) {
            log.error("Error while fetching version [serviceTarget={}]", SERVICE_NAME);
            return Optional.empty();
        }
    }
}
