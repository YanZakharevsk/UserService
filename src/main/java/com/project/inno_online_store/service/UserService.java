package com.project.inno_online_store.service;

import com.project.inno_online_store.dto.request.CreateUserRequest;
import com.project.inno_online_store.dto.request.UpdateUserRequest;
import com.project.inno_online_store.dto.response.PageResponse;
import com.project.inno_online_store.dto.response.UserResponse;
import com.project.inno_online_store.dto.response.UserShortResponse;
import com.project.inno_online_store.jpa.entity.User;
import com.project.inno_online_store.jpa.repository.UserRepository;
import com.project.inno_online_store.jpa.repository.filter.UserFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.Set;

public interface UserService {

    public UserResponse createUser(CreateUserRequest userRequest);

    public UserResponse getUserById(Long userId);

    public Set<UserResponse> getAllUsers();

    public PageResponse<UserResponse> getAllUsersWithPaginationAndFilter(UserFilter userFilter, int page, int size);

    public UserShortResponse updateUserById(Long userId, UpdateUserRequest userRequest);

    public UserResponse activateUser(Long userId);

    public UserResponse deactivateUser(Long userId);

    public boolean deleteUserById(Long userId);

}
