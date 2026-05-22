package com.project.inno_online_store.service.serviceImpl;

import com.project.inno_online_store.dto.mapper.UserMapper;
import com.project.inno_online_store.dto.request.CreateUserRequest;
import com.project.inno_online_store.dto.request.UpdateUserRequest;
import com.project.inno_online_store.dto.response.PageResponse;
import com.project.inno_online_store.dto.response.UserResponse;
import com.project.inno_online_store.dto.response.UserShortResponse;
import com.project.inno_online_store.exception.UserNotFoundException;
import com.project.inno_online_store.jpa.entity.User;
import com.project.inno_online_store.jpa.repository.UserRepository;
import com.project.inno_online_store.jpa.repository.filter.UserFilter;
import com.project.inno_online_store.jpa.repository.specification.UserSpecification;
import com.project.inno_online_store.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponse createUser(CreateUserRequest userRequest) {
        User user = userMapper.toEntity(userRequest);
        user.setIsActive(true);
        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    @Cacheable(value = "users", key = "#userId")
    public UserResponse getUserById(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        return userMapper.toResponse(user);
    }

    @Override
    public Set<UserResponse> getAllUsers() {
        Set<User> users = (Set<User>) userRepository.findAll();
        return users.stream().map(userMapper::toResponse).collect(Collectors.toSet());
    }

    @Override
    @Cacheable(value = "users_page", key = "#userFilter.toString() + '_' + #page + '_' + #size")
    public PageResponse<UserResponse> getAllUsersWithPaginationAndFilter(UserFilter userFilter, int page, int size) {

        Specification<User> specification = UserSpecification.filterBy(userFilter);

        Page<User> userPage = userRepository.findAll(
                specification,
                PageRequest.of(page, size)
        );

        List<UserResponse> content = userPage.getContent()
                .stream()
                .map(userMapper::toResponse)
                .toList();

        return new PageResponse<>(
                content,
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements()
        );
    }

    @Transactional
    @Override
    @CachePut(value = "users", key = "#userId")
    public UserShortResponse updateUserById(Long userId, UpdateUserRequest userRequest) {

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        userMapper.update(user, userRequest);
        userRepository.save(user);

        return userMapper.toShortResponse(user);
    }

    @Override
    @CachePut(value = "users", key = "#userId")
    public UserResponse activateUser(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        user.setIsActive(true);
        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    @CachePut(value = "users", key = "#userId")
    public UserResponse deactivateUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        user.setIsActive(false);
        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Transactional
    @Override
    @CacheEvict(value = "users", key = "#userId")
    public boolean deleteUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        userRepository.delete(user);
        return true;
    }
}
