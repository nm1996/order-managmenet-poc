package com.nm.order.management.gateway.infra.mapper;

import com.nm.order.management.common.mapper.DateTimeMapper;
import com.nm.order.management.common.mapper.exception.MappingException;
import com.nm.order.management.gateway.api.model.request.user.CreateUserRequestDto;
import com.nm.order.management.gateway.api.model.response.user.UserResponseDto;
import com.nm.order.management.proto_common.user.CreateUserRequest;
import com.nm.order.management.proto_common.user.CreateUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserMapper {

    private final DateTimeMapper dateTimeMapper;

    public UserResponseDto responseToDto(CreateUserResponse proto) throws MappingException {
        return new UserResponseDto(
                proto.getUsername(),
                proto.getEmail(),
                proto.getFullName(),
                dateTimeMapper.convertTimestampToDate(proto.getDateOfBirth()),
                proto.getRole()
        );
    }

    public CreateUserRequest requestToProto(CreateUserRequestDto dto){
        CreateUserRequest.Builder builder = CreateUserRequest.newBuilder();
        builder.setUsername(dto.username());
        builder.setEmail(dto.email());
        builder.setFirstName(dto.firstName());
        builder.setLastName(dto.lastName());
        builder.setPassword(dto.password());
        builder.setRole(dto.role());
        builder.setDateOfBirth(dateTimeMapper.convertLongToTimestamp(dto.dateOfBirthTimestamp()));

        return builder.build();
    }
}
