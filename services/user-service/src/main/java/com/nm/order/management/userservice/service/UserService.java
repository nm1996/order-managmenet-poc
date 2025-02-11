package com.nm.order.management.userservice.service;

import com.nm.order.management.proto_common.user.CreateUserRequest;
import com.nm.order.management.proto_common.user.CreateUserResponse;
import com.nm.order.management.userservice.domain.User;
import com.nm.order.management.common.mapper.exception.MappingException;
import com.nm.order.management.userservice.infra.mapper.UserMapper;
import com.nm.order.management.userservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public CreateUserResponse saveUser(CreateUserRequest createUserRequest) throws MappingException {
        User user = userMapper.toEntity(createUserRequest);
        User persistUser = userRepository.saveAndFlush(user);
        return userMapper.toResponse(persistUser);
    }
}
