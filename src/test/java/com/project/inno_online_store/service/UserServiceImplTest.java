package com.project.inno_online_store.service;

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
import com.project.inno_online_store.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John");
        user.setSurname("Doe");
        user.setIsActive(true);

        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setName("John");
        userResponse.setSurname("Doe");
    }

    @Test
    void createUser_Success() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("John");

        when(userMapper.toEntity(request)).thenReturn(user);
        when(userDao.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.createUser(request);

        assertNotNull(result);
        assertEquals("John", result.getName());
        verify(userDao, times(1)).save(user);
        assertTrue(user.getIsActive());
    }

    @Test
    void getUserById_Success() {
        when(userDao.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userDao, times(1)).findById(1L);
    }

    @Test
    void getUserById_ThrowsUserNotFoundException() {
        when(userDao.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
        verify(userDao, times(1)).findById(1L);
    }

    @Test
    void getAllUsersWithPaginationAndFilter_Success() {
        UserFilter filter = new UserFilter("John", "Doe");
        Page<User> page = new PageImpl<>(List.of(user));

        doReturn(page)
                .when(userDao)
                .findAll(any(Specification.class), any(org.springframework.data.domain.Pageable.class));

        when(userMapper.toResponse(user)).thenReturn(userResponse);
        PageResponse<UserResponse> result = userService.getAllUsersWithPaginationAndFilter(filter, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(0, result.getPage());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void updateUserById_Success() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("Jane");

        UserShortResponse shortResponse = new UserShortResponse();
        shortResponse.setName("Jane");

        when(userDao.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userMapper).update(user, request);
        when(userDao.save(user)).thenReturn(user);
        when(userMapper.toShortResponse(user)).thenReturn(shortResponse);

        UserShortResponse result = userService.updateUserById(1L, request);

        assertNotNull(result);
        assertEquals("Jane", result.getName());
        verify(userDao, times(1)).save(user);
    }

    @Test
    void updateUserById_ThrowsUserNotFoundException() {
        UpdateUserRequest request = new UpdateUserRequest();
        when(userDao.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUserById(1L, request));
        verify(userDao, times(1)).findById(1L);
        verify(userDao, never()).save(any(User.class));
    }

    @Test
    void activateUser_Success() {
        user.setIsActive(false);
        when(userDao.findById(1L)).thenReturn(Optional.of(user));
        when(userDao.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        userService.activateUser(1L);

        assertTrue(user.getIsActive());
        verify(userDao, times(1)).save(user);
    }

    @Test
    void activateUser_ThrowsUserNotFoundException() {
        when(userDao.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.activateUser(1L));
        verify(userDao, times(1)).findById(1L);
        verify(userDao, never()).save(any(User.class));
    }

    @Test
    void deactivateUser_Success() {
        user.setIsActive(true); // изначально активен
        when(userDao.findById(1L)).thenReturn(Optional.of(user));
        when(userDao.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.deactivateUser(1L);

        assertNotNull(result);
        assertFalse(user.getIsActive());
        verify(userDao, times(1)).save(user);
    }

    @Test
    void deactivateUser_ThrowsUserNotFoundException() {
        when(userDao.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deactivateUser(1L));
        verify(userDao, times(1)).findById(1L);
        verify(userDao, never()).save(any(User.class));
    }

    @Test
    void deleteUserById_Success() {
        when(userDao.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userDao).delete(user);

        boolean result = userService.deleteUserById(1L);

        assertTrue(result);
        verify(userDao, times(1)).delete(user);
    }

    @Test
    void deleteUserById_ThrowsUserNotFoundException() {
        when(userDao.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUserById(1L));
        verify(userDao, times(1)).findById(1L);
        verify(userDao, never()).delete(any(User.class));
    }
}
