package com.nm.order.management.common.grpc;


import com.nm.order.management.common.cloud.service.ServiceCloudOrchestrator;
import com.nm.order.management.common.exception.ServiceUnavailable;
import io.grpc.ManagedChannel;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.*;

@Getter
@Slf4j
public abstract class AbstractServiceClient<T> implements AutoCloseable {

    private final ServiceCloudOrchestrator serviceCloudOrchestrator;
    private final Queue<T> stubs;


    public AbstractServiceClient(ServiceCloudOrchestrator serviceCloudOrchestrator) {
        this.serviceCloudOrchestrator = serviceCloudOrchestrator;
        stubs = new LinkedList<>();
    }

    @PostConstruct
    public void establishConnection() {
        try {
            fetchAndCreateConnections();
        } catch (ServiceUnavailable e) {
            logErrorMessage();
        }
    }

    @Scheduled(fixedRate = 30_000)
    public void tryToEstablishConnection() {
        try {
            fetchAndCreateConnections();
        } catch (ServiceUnavailable e) {
            logErrorMessage();
        }
    }

    private void fetchAndCreateConnections() throws ServiceUnavailable {
        Set<ManagedChannel> channelList = serviceCloudOrchestrator.getChannelsByServiceName(getServiceName());
        if (channelList.isEmpty()) {
            log.error("Service unavailable [service={}]", getServiceName());
            throw new ServiceUnavailable(String.format("%s unavailable", getServiceName()));
        }

        channelList.stream()
                .filter(Objects::nonNull)
                .forEach(chan -> {
                    T stub = createStub(chan);
                    stubs.add(stub);
                });
    }


    public T getNextServer() throws ServiceUnavailable {
        if (stubs.isEmpty()) {
            logErrorMessage();
            throw new ServiceUnavailable("No server connections available");
        }

        synchronized (stubs) {
            T server = stubs.poll();
            stubs.offer(server);
            return server;
        }
    }


    @Override
    public void close() {
        stubs.clear();
        serviceCloudOrchestrator.shutdownConnections(getServiceName());
    }


    private void logErrorMessage() {
        log.warn("Service unavailable [service={}]", getServiceName());
    }

    public abstract String getServiceName();

    public abstract T createStub(ManagedChannel channel);


}
