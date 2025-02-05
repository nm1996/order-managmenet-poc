package com.nm.order.management.gateway.api.advice;

import com.nm.order.management.common.grpc.message.CommonErrorReader;
import com.nm.order.management.common.grpc.message.model.GrpcErrorDto;
import com.nm.order.management.gateway.api.model.response.error.GeneralErrorResponse;
import io.grpc.StatusRuntimeException;


public abstract class GeneralErrorHandling {

    public GrpcErrorDto getGrpcErrorDto(StatusRuntimeException e) {
        return CommonErrorReader.mapGrpcStatusToErrorResponse(e);
    }

    public GeneralErrorResponse getGeneralErrorDto(StatusRuntimeException e) {
        return mapToGeneralError(getGrpcErrorDto(e));
    }

    private GeneralErrorResponse mapToGeneralError(GrpcErrorDto dto) {
        return new GeneralErrorResponse(dto.getMessage(), dto.getCode());
    }
}
