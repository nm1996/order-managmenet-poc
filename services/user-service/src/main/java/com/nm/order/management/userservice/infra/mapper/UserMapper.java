package com.nm.order.management.userservice.infra.mapper;

import com.nm.order.management.common.mapper.DateTimeMapper;
import com.nm.order.management.proto_common.user.CreateUserRequest;
import com.nm.order.management.proto_common.user.CreateUserResponse;
import com.nm.order.management.userservice.domain.User;
import com.nm.order.management.common.mapper.exception.MappingException;
import com.nm.order.management.userservice.infra.util.PasswordHashUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@RequiredArgsConstructor
@Component
public class UserMapper {

    private final RoleMapper roleMapper;
    private final DateTimeMapper dateTimeMapper;

    public User toEntity(CreateUserRequest proto) throws MatchException {
        User.UserBuilder userBuilder = User.builder();
        userBuilder.username(proto.getUsername());
        userBuilder.password(PasswordHashUtil.hashPassword(proto.getPassword()));
        userBuilder.email(proto.getEmail());
        userBuilder.firstName(proto.getFirstName());
        userBuilder.lastName(proto.getLastName());
        userBuilder.dateOfBirth(dateTimeMapper.convertTimestampToDate(proto.getDateOfBirth()));
        userBuilder.createdAt(new Date());
        userBuilder.role(roleMapper.toEnum(proto.getRole()));

        return userBuilder.build();
    }

    public CreateUserResponse toResponse(User entity) throws MappingException {
        CreateUserResponse.Builder protoBuilder = CreateUserResponse.newBuilder();
        protoBuilder.setUsername(entity.getUsername());
        protoBuilder.setEmail(entity.getEmail());
        protoBuilder.setFullName(createFullName(entity.getFirstName(), entity.getLastName()));
        protoBuilder.setDateOfBirth(dateTimeMapper.convertDateToTimestamp(entity.getDateOfBirth()));
        protoBuilder.setRole(roleMapper.toString(entity.getRole()));

        return protoBuilder.build();
    }

    private String createFullName(String firstName, String lastName) {
        return String.format("%s %s", firstName, lastName);
    }


}
