package com.nm.order.management.common.cloud.service.implementation;

import com.nm.order.management.common.cloud.model.ServiceCloudInfoDto;
import com.nm.order.management.common.cloud.model.ServiceInstanceInfoDto;
import com.nm.order.management.common.cloud.service.OrchestratorConnection;
import com.nm.order.management.common.cloud.service.util.OrchestratorFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ConnectionOrchestratorImpl implements OrchestratorConnection {


    public ServiceCloudInfoDto connectAndCreateInfoDto(String serviceName,
                                                       Function<String, List<ServiceInstance>> getServiceInstanceFunction,
                                                       Predicate<ServiceInstance> filterServiceUp) {
        Map<String, ServiceInstanceInfoDto> serviceInstancesMap = fetchServiceInstances(serviceName, getServiceInstanceFunction, filterServiceUp)
                .stream()
                .collect(Collectors.toMap(
                        OrchestratorFunction::createServiceAddress,
                        instance -> ServiceInstanceInfoDto.createServiceInfoDto(
                                instance,
                                OrchestratorFunction::createGrpcServerAddress,
                                OrchestratorFunction::createGrpcClientChannel
                        ),
                        (existing, _) -> existing
                ));

        if (serviceInstancesMap.isEmpty()) {
            return ServiceCloudInfoDto.createDownServiceInfo(serviceName);
        }

        return ServiceCloudInfoDto.createUpServiceInfo(serviceName, serviceInstancesMap);
    }

    public ServiceCloudInfoDto maintenanceConnection(
            ServiceCloudInfoDto existingCloudInfoDto,
            Function<String, List<ServiceInstance>> getServiceInstanceFunction,
            Predicate<ServiceInstance> filterServiceUp) {

        String serviceName = existingCloudInfoDto.getServiceName();
        Set<String> alreadyKnownServiceAddresses = existingCloudInfoDto.getServiceAddresses();
        List<ServiceInstance> serviceInstances = fetchServiceInstances(serviceName, getServiceInstanceFunction, filterServiceUp);
        Map<String, ServiceInstance> discoveredServices = mapDiscoveredServices(serviceInstances);

        removeUnavailableServices(existingCloudInfoDto, alreadyKnownServiceAddresses, discoveredServices);
        updateServiceCloudInfo(existingCloudInfoDto, discoveredServices);

        return existingCloudInfoDto;
    }

    private List<ServiceInstance> fetchServiceInstances(
            String serviceName,
            Function<String, List<ServiceInstance>> getServiceInstanceFunction,
            Predicate<ServiceInstance> filterServiceUp) {
        return getServiceInstanceFunction.apply(serviceName).stream()
                .filter(filterServiceUp)
                .toList();
    }

     Map<String, ServiceInstance> mapDiscoveredServices(List<ServiceInstance> serviceInstances) {
        return serviceInstances.stream()
                .collect(Collectors.toMap(
                        OrchestratorFunction::createServiceAddress, Function.identity()
                ));
    }

    void removeUnavailableServices(ServiceCloudInfoDto existingCloudInfoDto,
                                           Set<String> alreadyKnownServiceAddresses,
                                           Map<String, ServiceInstance> discoveredServices) {
        Set<String> unavailableServiceAddresses = new HashSet<>(alreadyKnownServiceAddresses);
        unavailableServiceAddresses.removeAll(discoveredServices.keySet());
        OrchestratorFunction.removeUnavailableServices(existingCloudInfoDto, unavailableServiceAddresses);
    }

    void updateServiceCloudInfo(ServiceCloudInfoDto existingCloudInfoDto,
                                        Map<String, ServiceInstance> discoveredServices) {
        Map<String, ServiceInstanceInfoDto> serviceCloudInfoDtoMap = existingCloudInfoDto.getInfoDtoMap();

        discoveredServices.forEach((key, serviceInstance) -> {
            String grpcAddress = OrchestratorFunction.createGrpcServerAddress(serviceInstance);
            serviceCloudInfoDtoMap.compute(key, (k, existingEntry) -> {
                if (existingEntry == null || !grpcAddress.equals(existingEntry.getGrpcAddress()) || existingEntry.getChannel() == null) {
                    return ServiceInstanceInfoDto.builder()
                            .grpcAddress(grpcAddress)
                            .channel(OrchestratorFunction.createGrpcClientChannel(grpcAddress))
                            .build();
                }
                return existingEntry;
            });
        });
    }


}
