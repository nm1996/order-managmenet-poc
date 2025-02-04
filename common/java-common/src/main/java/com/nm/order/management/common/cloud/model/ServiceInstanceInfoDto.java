package com.nm.order.management.common.cloud.model;

import io.grpc.ManagedChannel;
import lombok.*;
import org.springframework.cloud.client.ServiceInstance;

import java.util.function.Function;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ServiceInstanceInfoDto {

    private CommonProtoChannel channel;
    private String grpcAddress;


    public static ServiceInstanceInfoDto createServiceInfoDto(ServiceInstance instance,
                                                              Function<ServiceInstance, String> createGrpcServerAddressFunction,
                                                              Function<String, CommonProtoChannel> establishGrpcClientFunction) {
        String grpcServerAddress = createGrpcServerAddressFunction.apply(instance);
        CommonProtoChannel channel = establishGrpcClientFunction.apply(grpcServerAddress);

        return ServiceInstanceInfoDto.builder()
                .channel(channel)
                .grpcAddress(grpcServerAddress)
                .build();
    }
}
