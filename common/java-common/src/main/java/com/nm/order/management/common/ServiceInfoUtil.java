package com.nm.order.management.common;

import lombok.experimental.UtilityClass;
import org.springframework.cloud.client.ServiceInstance;


@UtilityClass
public class ServiceInfoUtil {

    private final static String GRPC_PORT = "grpc-port";
    private final static String GRPC_HOST = "grpc-host";
    private final static String SERVICE_HOST_PORT = "service-host-port";


    public String getGrpcHost(ServiceInstance instance) {
        return instance.getMetadata().get(GRPC_HOST);
    }

    public String getGrpcPort(ServiceInstance instance) {
        return instance.getMetadata().get(GRPC_PORT);
    }

    public String getServiceHostPort(ServiceInstance instance) {
        return instance.getMetadata().get(SERVICE_HOST_PORT);
    }
}
