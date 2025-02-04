package com.nm.order.management.common.cloud.service;

import io.grpc.ManagedChannel;

import java.util.Set;

public interface ServiceCloudOrchestrator {

    void shutdownConnections(String serviceName);
    Set<ManagedChannel> getChannelsByServiceName(String serviceName);
}
