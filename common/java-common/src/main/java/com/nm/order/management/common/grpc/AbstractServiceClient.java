package com.nm.order.management.common.grpc;


import com.nm.order.management.common.cloud.ServiceCloudOrchestrator;
import com.nm.order.management.common.exception.ServiceUnavailable;
import io.grpc.ManagedChannel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Getter
@Slf4j
public abstract class AbstractServiceClient<T> implements AutoCloseable {

    private final ServiceCloudOrchestrator serviceCloudOrchestrator;
    private final List<ManagedChannel> channels;
    private final Queue<T> stubs;


    public AbstractServiceClient(ServiceCloudOrchestrator serviceCloudOrchestrator) {
        this.serviceCloudOrchestrator = serviceCloudOrchestrator;
        channels = new ArrayList<>();
        stubs = new LinkedList<>();
    }


    public void getOrRefreshConnection() throws ServiceUnavailable {
        List<ManagedChannel> channelList = serviceCloudOrchestrator.getOrCreateGrpcClient(getServiceName());
        if (channelList.isEmpty()) {
            log.error("Service unavailable [service={}]", getServiceName());
            throw new ServiceUnavailable(STR."\{getServiceName()} unavailable");
        }

        channelList.forEach(chan -> {
            channels.add(chan);
            T stub = createStub(chan);
            stubs.add(stub);
        });
    }

    public boolean isServiceAvailable() {
        return !stubs.isEmpty();
    }

    //TODO: TRY TO RECONNECT TO OTHER SERVICES
    public T getNextServer() {
        synchronized (stubs) {
            if (stubs.isEmpty()) {
                throw new ServiceUnavailable("No servers available");
            }
            T server = stubs.poll();
            stubs.offer(server);
            return server;
        }
    }


    @Override
    public void close() throws Exception {
        channels.forEach(ManagedChannel::shutdown);
    }

    public abstract String getServiceName();

    public abstract T createStub(ManagedChannel channel);


}
