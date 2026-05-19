package com.project.inno_online_store.service;

import com.project.inno_online_store.jpa.entity.User;
import com.project.inno_online_store.jpa.repository.UserRepository;
import com.project.inno_online_store.jpa.repository.filter.UserFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.Set;

public interface UserService {

    public User createUser(User user);

    public Optional<User> getUserById(Long userId);

    public Set<User> getAllUsers();

    public Page<User> getAllUsersWithPaginationAndFilter(UserFilter userFilter, int page, int size);

    public User updateUserById(Long userId, User updatedUser);

    public void activateUser(Long userId);

    public void deactivateUser(Long userId);
}
