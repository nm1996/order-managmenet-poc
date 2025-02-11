package com.nm.order.management.gateway.api.model.response.version;

import java.util.Map;

public record GetVersionResponse(
        Map<String, String> versions
) {
}
