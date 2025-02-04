package com.nm.order.management.common.cloud.service;

import com.nm.order.management.common.cloud.model.ServiceCloudInfoDto;
import org.springframework.cloud.client.ServiceInstance;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface OrchestratorConnection {

    ServiceCloudInfoDto connectAndCreateInfoDto(String serviceName,
                                                Function<String, List<ServiceInstance>> getServiceInstanceFunction,
                                                Predicate<ServiceInstance> filterServiceUp);

    ServiceCloudInfoDto maintenanceConnection(ServiceCloudInfoDto existingCloudInfoDto,
                                                            Function<String, List<ServiceInstance>> getServiceInstanceFunction,
                                                            Predicate<ServiceInstance> filterServiceUp);
}
