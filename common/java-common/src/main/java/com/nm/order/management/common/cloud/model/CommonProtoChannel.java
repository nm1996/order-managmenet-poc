package com.nm.order.management.common.cloud.model;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommonProtoChannel {
    private final ManagedChannel channel;


    public static CommonProtoChannel createCommonProtoChannel(String address) {
        ManagedChannel channel = ManagedChannelBuilder.forTarget(address)
                .usePlaintext()
                .build();

        return new CommonProtoChannel(channel);
    }
}
