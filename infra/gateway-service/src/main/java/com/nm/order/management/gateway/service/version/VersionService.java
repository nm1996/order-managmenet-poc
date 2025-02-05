package com.nm.order.management.gateway.service.version;

import com.nm.order.management.gateway.api.model.response.version.GetVersionResponse;
import com.nm.order.management.gateway.infra.grpc.analyticservice.AnalyticVersionServiceClient;
import com.nm.order.management.gateway.infra.grpc.notificationservice.NotificationVersionServiceClient;
import com.nm.order.management.gateway.infra.grpc.orderservice.OrderVersionServiceClient;
import com.nm.order.management.gateway.infra.grpc.productservice.ProductVersionServiceClient;
import com.nm.order.management.gateway.infra.grpc.userservice.version.UserVersionServiceClient;
import com.nm.order.management.proto_common.common.VersionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class VersionService {

    private final UserVersionServiceClient userVersionServiceClient;
    private final ProductVersionServiceClient productVersionServiceClient;
    private final OrderVersionServiceClient orderVersionServiceClient;
    private final NotificationVersionServiceClient notificationVersionServiceClient;
    private final AnalyticVersionServiceClient analyticVersionServiceClient;


    public GetVersionResponse getVersions() {
        Map<String, String> versions = new HashMap<>();

        setVersionToResponse(versions, "USER-SERVICE", userVersionServiceClient.getVersion());
        setVersionToResponse(versions, "PRODUCT-SERVICE", productVersionServiceClient.getVersion());
        setVersionToResponse(versions, "ORDER-SERVICE", orderVersionServiceClient.getVersion());
        setVersionToResponse(versions, "NOTIFICATION-SERVICE", notificationVersionServiceClient.getVersion());
        setVersionToResponse(versions, "ANALYTICS-SERVICE", analyticVersionServiceClient.getVersion());

        return new GetVersionResponse(versions);
    }


    private void setVersionToResponse(Map<String, String> response, String key, Optional<VersionResponse> value) {
        value.ifPresent(versionResponse -> response.put(key, versionResponse.getVersion()));
    }
}
