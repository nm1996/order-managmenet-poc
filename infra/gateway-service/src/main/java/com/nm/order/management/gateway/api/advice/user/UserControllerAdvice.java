package com.nm.order.management.gateway.api.advice.user;

import com.nm.order.management.gateway.api.advice.GeneralErrorHandling;
import com.nm.order.management.gateway.api.controller.user.UserController;
import com.nm.order.management.gateway.api.model.response.error.GeneralErrorResponse;
import com.nm.order.management.gateway.infra.exception.GrpcMessageHolderException;
import io.grpc.StatusRuntimeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice(assignableTypes = UserController.class)
public class UserControllerAdvice extends GeneralErrorHandling {


    @ExceptionHandler(value = GrpcMessageHolderException.class)
    @ResponseBody
    public ResponseEntity<GeneralErrorResponse> handleException(GrpcMessageHolderException messageHolderException) {
        StatusRuntimeException grpcServerException = messageHolderException.getGrpcServerException();
        GeneralErrorResponse response = getGeneralErrorDto(grpcServerException);

        return ResponseEntity.status(response.statusCode())
                .body(response);
    }
}
