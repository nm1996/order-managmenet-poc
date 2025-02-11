package com.nm.order.management.gateway.api.controller.user;

import com.nm.order.management.gateway.api.model.request.user.CreateUserRequestDto;
import com.nm.order.management.gateway.api.model.response.user.UserResponseDto;
import com.nm.order.management.gateway.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@RequestBody CreateUserRequestDto requestDto) {
        UserResponseDto userResponseDto = userService.createUser(requestDto);

        return ResponseEntity.ok(userResponseDto);
    }

}
