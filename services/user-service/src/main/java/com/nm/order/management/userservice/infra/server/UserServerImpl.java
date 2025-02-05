package com.nm.order.management.userservice.infra.server;

import com.nm.order.management.common.grpc.factory.CommonGrpcExceptionFactory;
import com.nm.order.management.common.mapper.exception.MappingException;
import com.nm.order.management.proto_common.user.CreateUserRequest;
import com.nm.order.management.proto_common.user.CreateUserResponse;
import com.nm.order.management.proto_common.user.UserServiceGrpc;
import com.nm.order.management.userservice.service.UserService;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.dao.DataIntegrityViolationException;

@Slf4j
@RequiredArgsConstructor
@GrpcService
public class UserServerImpl extends UserServiceGrpc.UserServiceImplBase {

    private final UserService userService;

    @Override
    public void createUser(CreateUserRequest request, StreamObserver<CreateUserResponse> responseObserver) {
        log.info("Storing new user [request={}]", request);
        try {
            CreateUserResponse response = userService.saveUser(request);
            responseObserver.onNext(response);

            log.info("Finalizing storing user");
            responseObserver.onCompleted();
        } catch (MappingException e) {
            log.error("Error while mapping data, [msg={}]", e.getMessage(), e);

            StatusRuntimeException exception = CommonGrpcExceptionFactory.createExceptionEnvelope(Status.INVALID_ARGUMENT, "Mapping data error");
            responseObserver.onError(exception);
        } catch (DataIntegrityViolationException e) {
            log.error("Error while storing user [msg={}]", e.getMessage(), e);

            StatusRuntimeException exception = CommonGrpcExceptionFactory.createExceptionEnvelope(Status.ALREADY_EXISTS, e.getMessage());
            responseObserver.onError(exception);
        } catch (Exception e) {
            log.error("Exception [msg={}]", e.getMessage(), e);

            StatusRuntimeException exception = CommonGrpcExceptionFactory.createExceptionEnvelope(Status.INTERNAL, "Global exception");
            responseObserver.onError(exception);
        }
    }
}
