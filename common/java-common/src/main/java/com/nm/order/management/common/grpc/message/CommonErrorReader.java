package com.nm.order.management.common.grpc.message;

import com.nm.order.management.common.grpc.message.model.GrpcErrorDto;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang.StringUtils;

@UtilityClass
public class CommonErrorReader {

    public GrpcErrorDto mapGrpcStatusToErrorResponse(StatusRuntimeException grpcServerException) {
        GrpcErrorDto errorDto = new GrpcErrorDto();

        Status grpcStatus = grpcServerException.getStatus();

        String message = null;
        if (grpcServerException.getTrailers() != null) {
            message = grpcServerException.getTrailers().get(Metadata.Key.of("error", Metadata.ASCII_STRING_MARSHALLER));
        }
        if (StringUtils.isEmpty(message)) {
            message = "ERROR";
        }
        errorDto.setMessage(message);

        if (grpcStatus == Status.CANCELLED) {
            errorDto.setCode(499);
        } else if (grpcStatus == Status.UNKNOWN) {
            errorDto.setCode(500);
        } else if (grpcStatus == Status.INVALID_ARGUMENT) {
            errorDto.setCode(400);
        } else if (grpcStatus == Status.DEADLINE_EXCEEDED) {
            errorDto.setCode(408);
        } else if (grpcStatus == Status.NOT_FOUND) {
            errorDto.setCode(404);
        } else if (grpcStatus == Status.ALREADY_EXISTS) {
            errorDto.setCode(409);
        } else if (grpcStatus == Status.PERMISSION_DENIED) {
            errorDto.setCode(403);
        } else if (grpcStatus == Status.UNAUTHENTICATED) {
            errorDto.setCode(401);
        } else if (grpcStatus == Status.RESOURCE_EXHAUSTED) {
            errorDto.setCode(429);
        } else if (grpcStatus == Status.FAILED_PRECONDITION) {
            errorDto.setCode(400);
        } else if (grpcStatus == Status.ABORTED) {
            errorDto.setCode(409);
        } else if (grpcStatus == Status.OUT_OF_RANGE) {
            errorDto.setCode(400);
        } else if (grpcStatus == Status.UNIMPLEMENTED) {
            errorDto.setCode(501);
        } else if (grpcStatus == Status.INTERNAL) {
            errorDto.setCode(500);
        } else if (grpcStatus == Status.UNAVAILABLE) {
            errorDto.setCode(503);
        } else if (grpcStatus == Status.DATA_LOSS) {
            errorDto.setCode(500);
        } else {
            errorDto.setCode(500);
        }

        return errorDto;
    }
}
