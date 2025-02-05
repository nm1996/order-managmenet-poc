package com.nm.order.management.gateway.infra.exception;

import io.grpc.StatusRuntimeException;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class GrpcMessageHolderException extends RuntimeException {

    StatusRuntimeException grpcServerException;




}


