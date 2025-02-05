package com.nm.order.management.common.grpc.factory;

import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.experimental.UtilityClass;

import javax.annotation.Nullable;

@UtilityClass
public class CommonGrpcExceptionFactory {

    public StatusRuntimeException createExceptionEnvelope(Status status, String message) {
        Metadata metadata = new Metadata();
        metadata.put(Metadata.Key.of("error", Metadata.ASCII_STRING_MARSHALLER), message);

        return new CommonGrpcException(status, metadata);
    }


    public class CommonGrpcException extends StatusRuntimeException {

        public CommonGrpcException(Status status, @Nullable Metadata trailers) {
            super(status, trailers);
        }
    }
}
