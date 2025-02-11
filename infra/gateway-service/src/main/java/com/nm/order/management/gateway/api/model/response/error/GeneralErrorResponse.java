package com.nm.order.management.gateway.api.model.response.error;

import java.util.List;

public record GeneralErrorResponse (String message, int statusCode) {
}
