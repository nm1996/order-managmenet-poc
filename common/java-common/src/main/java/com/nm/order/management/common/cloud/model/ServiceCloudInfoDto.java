package com.nm.order.management.common.cloud.model;

import io.grpc.ManagedChannel;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class ServiceCloudInfoDto {

    private final String serviceName;
    private final Map<String, ServiceInstanceInfoDto> infoDtoMap;


    private ServiceCloudInfoDto(String serviceName, Map<String, ServiceInstanceInfoDto> infoDtoMap) {
        this.serviceName = serviceName;
        this.infoDtoMap = infoDtoMap;
    }

    public static ServiceCloudInfoDto createUpServiceInfo(String serviceName, Map<String, ServiceInstanceInfoDto> infoDtoMap) {
        return new ServiceCloudInfoDto(serviceName, infoDtoMap);
    }

    public static ServiceCloudInfoDto createDownServiceInfo(String serviceName) {
        return new ServiceCloudInfoDto(serviceName, new HashMap<>());
    }

    public Set<ManagedChannel> getEstablishedConnections() {
        return infoDtoMap.values()
                .stream()
                .map(ServiceInstanceInfoDto::getChannel)
                .map(CommonProtoChannel::getChannel)
                .collect(Collectors.toSet());
    }

    public Set<String> getServiceAddresses() {
        return infoDtoMap.keySet();
    }


}
