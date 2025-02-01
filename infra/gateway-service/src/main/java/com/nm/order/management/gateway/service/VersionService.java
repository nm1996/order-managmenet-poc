package com.nm.order.management.gateway.service;

import com.nm.order.management.common.config.ServiceInfoConfig;
import com.nm.order.management.gateway.api.model.response.GetVersionResponse;
import com.nm.order.management.gateway.infra.grpc.analyticservice.AnalyticVersionServiceClient;
import com.nm.order.management.gateway.infra.grpc.notificationservice.NotificationVersionServiceClient;
import com.nm.order.management.gateway.infra.grpc.orderservice.OrderVersionServiceClient;
import com.nm.order.management.gateway.infra.grpc.productservice.ProductVersionServiceClient;
import com.nm.order.management.gateway.infra.grpc.userservice.UserVersionServiceClient;
import com.nm.order.management.proto_common.VersionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class VersionService {

    private final ServiceInfoConfig serviceInfoConfig;
    private final UserVersionServiceClient userVersionServiceClient;
    private final ProductVersionServiceClient productVersionServiceClient;
    private final OrderVersionServiceClient orderVersionServiceClient;
    private final NotificationVersionServiceClient notificationVersionServiceClient;
    private final AnalyticVersionServiceClient analyticVersionServiceClient;


    public GetVersionResponse getVersions() {
        Map<String, String> versions = new HashMap<>();

        setVersionToResponse(versions, serviceInfoConfig.getUserServiceName(), userVersionServiceClient.getVersion());
        setVersionToResponse(versions, serviceInfoConfig.getProductServiceName(), productVersionServiceClient.getVersion());
        setVersionToResponse(versions, serviceInfoConfig.getOrderServiceName(), orderVersionServiceClient.getVersion());
        setVersionToResponse(versions, serviceInfoConfig.getNotificationServiceName(), notificationVersionServiceClient.getVersion());
        setVersionToResponse(versions, serviceInfoConfig.getAnalyticServiceName(), analyticVersionServiceClient.getVersion());

        return new GetVersionResponse(versions);
    }


    private void setVersionToResponse(Map<String, String> response, String key, Optional<VersionResponse> value) {
        value.ifPresent(versionResponse -> response.put(key, versionResponse.getVersion()));
    }
}
