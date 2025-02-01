package com.nm.order.management.gateway.api.model.response;

import java.util.Map;

public record GetVersionResponse(
        Map<String, String> versions
) {
}
