package com.project.inno_online_store.service.impl;

import com.project.inno_online_store.dao.UserDao;
import com.project.inno_online_store.dto.mapper.UserMapper;
import com.project.inno_online_store.dto.request.CreateUserRequest;
import com.project.inno_online_store.dto.request.UpdateUserRequest;
import com.project.inno_online_store.dto.response.PageResponse;
import com.project.inno_online_store.dto.response.UserResponse;
import com.project.inno_online_store.dto.response.UserShortResponse;
import com.project.inno_online_store.exception.UserNotFoundException;
import com.project.inno_online_store.jpa.entity.User;
import com.project.inno_online_store.jpa.repository.filter.UserFilter;
import com.project.inno_online_store.jpa.repository.specification.UserSpecification;
import com.project.inno_online_store.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final UserMapper userMapper;

    public UserServiceImpl(UserDao userDao, UserMapper userMapper) {
        this.userDao = userDao;
        this.userMapper = userMapper;
    }

    @Transactional
    @Override
    @CacheEvict(value = "users_page", allEntries = true)
    public UserResponse createUser(CreateUserRequest userRequest) {
        User user = userMapper.toEntity(userRequest);
        user.setIsActive(true);
        userDao.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    @Cacheable(value = "users", key = "#userId")
    public UserResponse getUserById(Long userId) {

        User user = userDao.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        return userMapper.toResponse(user);
    }

    @Override
    @Cacheable(value = "users_page", key = "#userFilter.toString() + '_' + #page + '_' + #size")
    public PageResponse<UserResponse> getAllUsersWithPaginationAndFilter(UserFilter userFilter, int page, int size) {

        Specification<User> specification = UserSpecification.filterBy(userFilter);

        Page<User> userPage = userDao.findAll(
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
    @Caching(evict = {
            @CacheEvict(value = "users_page", allEntries = true),
            @CacheEvict(value = "users", key = "#userId")
    })
    public UserShortResponse updateUserById(Long userId, UpdateUserRequest userRequest) {

        User user = userDao.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        userMapper.update(user, userRequest);
        userDao.save(user);

        return userMapper.toShortResponse(user);
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = "users_page", allEntries = true),
            @CacheEvict(value = "users", key = "#userId")
    })
    public UserResponse activateUser(Long userId) {

        User user = userDao.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        user.setIsActive(true);
        userDao.save(user);
        return userMapper.toResponse(user);
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = "users_page", allEntries = true),
            @CacheEvict(value = "users", key = "#userId")
    })
    public UserResponse deactivateUser(Long userId) {
        User user = userDao.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        user.setIsActive(false);
        userDao.save(user);
        return userMapper.toResponse(user);
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = "users_page", allEntries = true),
            @CacheEvict(value = "users", key = "#userId")
    })
    public boolean deleteUserById(Long userId) {
        User user = userDao.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        userDao.delete(user);
        return true;
    }
}
