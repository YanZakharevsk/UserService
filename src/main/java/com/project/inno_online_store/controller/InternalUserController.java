package com.project.inno_online_store.controller;

import com.project.inno_online_store.dto.request.CreateUserRequest;
import com.project.inno_online_store.dto.response.UserResponse;
import com.project.inno_online_store.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/users")
public class InternalUserController {

    private final UserService userService;

    public InternalUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest userRequest){
        UserResponse userResponse = userService.createUser(userRequest);
        return ResponseEntity .status(HttpStatus.CREATED)
                .body(userResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable(name = "id") Long userId){
        userService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }
}
