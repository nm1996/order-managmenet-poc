package com.nm.order.management.gateway.service.user;

import com.nm.order.management.gateway.api.model.request.user.CreateUserRequestDto;
import com.nm.order.management.gateway.api.model.response.user.UserResponseDto;
import com.nm.order.management.gateway.infra.grpc.userservice.crud.UserCrudServiceClient;
import com.nm.order.management.gateway.infra.mapper.UserMapper;
import com.nm.order.management.proto_common.user.CreateUserRequest;
import com.nm.order.management.proto_common.user.CreateUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserMapper userMapper;
    private final UserCrudServiceClient userCrudServiceClient;

    public UserResponseDto createUser(CreateUserRequestDto requestDto) {
        CreateUserRequest proto = userMapper.requestToProto(requestDto);
        CreateUserResponse protoResponse = userCrudServiceClient.createUser(proto);

        return userMapper.responseToDto(protoResponse);
    }
}
