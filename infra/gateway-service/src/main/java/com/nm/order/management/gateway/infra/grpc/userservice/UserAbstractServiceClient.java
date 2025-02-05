package com.nm.order.management.gateway.infra.grpc.userservice;

import com.nm.order.management.common.cloud.service.ServiceCloudOrchestrator;
import com.nm.order.management.common.grpc.AbstractServiceClient;

public abstract class UserAbstractServiceClient<T> extends AbstractServiceClient<T> {

    private static final String SERVICE_NAME = "USER-SERVICE";

    public UserAbstractServiceClient(ServiceCloudOrchestrator serviceCloudOrchestrator) {
        super(serviceCloudOrchestrator);
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }
}
