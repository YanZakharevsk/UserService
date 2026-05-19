package com.project.inno_online_store.dao;

import com.project.inno_online_store.jpa.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

public interface UserDao {
    User save(User user);
    Optional<User> findById(Long id);
    Page<User> findAll(Specification<User> specification, Pageable pageable);
    void delete(User user);
    void deleteAll();
}
