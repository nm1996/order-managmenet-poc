package com.nm.order.management.gateway.infra.grpc.userservice.crud;

import com.nm.order.management.common.cloud.service.ServiceCloudOrchestrator;
import com.nm.order.management.common.grpc.AbstractServiceClient;
import com.nm.order.management.common.grpc.exception.ServiceUnavailableException;
import com.nm.order.management.gateway.infra.exception.GrpcMessageHolderException;
import com.nm.order.management.gateway.infra.exception.TargetUnavailableException;
import com.nm.order.management.proto_common.user.CreateUserRequest;
import com.nm.order.management.proto_common.user.CreateUserResponse;
import com.nm.order.management.proto_common.user.UserServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserCrudServiceClient extends AbstractServiceClient<UserServiceGrpc.UserServiceBlockingStub> {

    @Autowired
    public UserCrudServiceClient(ServiceCloudOrchestrator serviceCloudOrchestrator) {
        super(serviceCloudOrchestrator);
    }

    @Override
    public String getServiceName() {
        return "USER-SERVICE";
    }

    @Override
    public UserServiceGrpc.UserServiceBlockingStub createStub(ManagedChannel channel) {
        return UserServiceGrpc.newBlockingStub(channel);
    }


    public CreateUserResponse createUser(CreateUserRequest request) {
        try {
            return getNextServer().createUser(request);
        } catch (ServiceUnavailableException e) {
            log.warn("Target service unreachable [service={}]", getServiceName());
            throw new TargetUnavailableException("Target service not reachable", getServiceName());
        } catch (StatusRuntimeException e) {
            log.error("Error while creating user [msg={}]", e.getMessage());
            throw new GrpcMessageHolderException(e);
        }
    }

}
