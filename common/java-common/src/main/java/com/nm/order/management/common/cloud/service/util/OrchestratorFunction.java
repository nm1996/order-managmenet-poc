package com.nm.order.management.common.cloud.service.util;

import com.nm.order.management.common.ServiceInfoUtil;
import com.nm.order.management.common.cloud.model.CommonProtoChannel;
import com.nm.order.management.common.cloud.model.ServiceCloudInfoDto;
import com.nm.order.management.common.cloud.model.ServiceInstanceInfoDto;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@UtilityClass
public class OrchestratorFunction {


    public CommonProtoChannel createGrpcClientChannel(String address) {
        log.info("Creating gRPC Client for: {}", address);
        return CommonProtoChannel.createCommonProtoChannel(address);
    }

    public String createServiceAddress(ServiceInstance instance) {
        return ServiceInfoUtil.getServiceHostPort(instance);
    }

    public String createGrpcServerAddress(ServiceInstance instance) {
        return String.format("%s:%s", ServiceInfoUtil.getGrpcHost(instance), ServiceInfoUtil.getGrpcPort(instance));
    }

    public void shutdown(ServiceCloudInfoDto cloudInfoDto) {
        cloudInfoDto.getInfoDtoMap().values().forEach(OrchestratorFunction::shutdownChannel);
        cloudInfoDto.getInfoDtoMap().clear();
    }

    public void removeUnavailableServices(ServiceCloudInfoDto cloudInfoDto, Set<String> serviceAddressForRemove) {
        Map<String, ServiceInstanceInfoDto> infoDtoMap = cloudInfoDto.getInfoDtoMap();

        serviceAddressForRemove.forEach(
                serviceName -> {
                    shutdownChannel(infoDtoMap.get(serviceName));
                    infoDtoMap.remove(serviceName);
                }
        );
    }

    private void shutdownChannel(ServiceInstanceInfoDto infoDto) {
        try {
            infoDto.getChannel()
                    .getChannel()
                    .shutdown()
                    .awaitTermination(3, TimeUnit.SECONDS);
        } catch (Exception ignoredException) {
        }
    }


}
