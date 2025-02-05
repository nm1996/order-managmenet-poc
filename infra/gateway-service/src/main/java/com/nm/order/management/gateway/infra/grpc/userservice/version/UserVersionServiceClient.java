package com.nm.order.management.gateway.infra.grpc.userservice.version;

import com.nm.order.management.common.cloud.service.implementation.ServiceCloudOrchestratorImpl;
import com.nm.order.management.gateway.infra.grpc.userservice.UserAbstractServiceClient;
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
public class UserVersionServiceClient extends UserAbstractServiceClient<VersionServiceGrpc.VersionServiceBlockingStub> {


    @Autowired
    public UserVersionServiceClient(ServiceCloudOrchestratorImpl serviceCloudOrchestrator) {
        super(serviceCloudOrchestrator);
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
            log.error("Error while fetching version [serviceTarget={}]", getServiceName());
            return Optional.empty();
        }
    }


}
