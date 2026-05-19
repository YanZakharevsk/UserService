package com.project.inno_online_store.service.serviceImpl;

import com.project.inno_online_store.jpa.entity.User;
import com.project.inno_online_store.jpa.repository.UserRepository;
import com.project.inno_online_store.jpa.repository.filter.UserFilter;
import com.project.inno_online_store.jpa.repository.specification.UserSpecification;
import com.project.inno_online_store.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        user.setIsActive(true);
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public Set<User> getAllUsers() {
        return (Set<User>) userRepository.findAll();
    }

    @Override
    public Page<User> getAllUsersWithPaginationAndFilter(UserFilter userFilter, int page, int size) {
        Specification<User> specification = UserSpecification.filterBy(userFilter);

        return userRepository.findAll(specification, PageRequest.of(page, size));
    }

    @Override
    public User updateUserById(Long userId, User updatedUser) {
        Optional<User> optionalUser = getUserById(userId);

        if(optionalUser != null) {
            User user = optionalUser.get();
            user.setName(updatedUser.getName());
            user.setSurname(updatedUser.getSurname());
            user.setBirthDate(updatedUser.getBirthDate());
            user.setEmail(updatedUser.getEmail());
            return userRepository.save(user);
        }
        return null;
    }

    @Override
    public void activateUser(Long userId) {
        Optional<User> optionalUser = getUserById(userId);
        if(!optionalUser.isEmpty()){
            User user = optionalUser.get();
            user.setIsActive(true);
            userRepository.save(user);
        }
    }

    @Override
    public void deactivateUser(Long userId) {
        Optional<User> optionalUser = getUserById(userId);
        if(!optionalUser.isEmpty()){
            User user = optionalUser.get();
            user.setIsActive(false);
            userRepository.save(user);
        }
    }
}
