package com.project.inno_online_store.controller;

import com.project.inno_online_store.BaseIntegrationTest;
import com.project.inno_online_store.jpa.entity.User;
import com.project.inno_online_store.jpa.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoSpyBean
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldReturnFilteredUsersAndUseRedisCache() throws Exception {

        User john = new User();
        john.setName("John");
        john.setSurname("Doe");
        john.setEmail("john.doe@example.com");
        john.setBirthDate(LocalDate.of(1995, 5, 21));
        john.setIsActive(true);

        User alex = new User();
        alex.setName("Alex");
        alex.setSurname("Smith");
        alex.setEmail("alex.smith@example.com");
        alex.setBirthDate(LocalDate.of(1998, 10, 15));
        alex.setIsActive(true);

        userRepository.saveAll(List.of(john, alex));

        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setName("John");
        criteria.setPage(0);
        criteria.setSize(10);

        String requestBody = objectMapper.writeValueAsString(criteria);

        mockMvc.perform(get("/store/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("John")))
                .andExpect(jsonPath("$.totalElements", is(1)));

        verify(userRepository, times(1))
                .findAll(
                        any(Specification.class),
                        any(Pageable.class)
                );

        mockMvc.perform(get("/store/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name", is("John")))
                .andExpect(jsonPath("$.totalElements", is(1)));

        verify(userRepository, times(1))
                .findAll(
                        any(Specification.class),
                        any(Pageable.class)
                );
    }

    @Test
    void shouldGetUserById() throws Exception {

        User user = new User();
        user.setName("John");
        user.setSurname("Doe");
        user.setEmail("john@test.com");
        user.setBirthDate(LocalDate.of(1999, 5, 10));
        user.setIsActive(true);

        User saved = userRepository.save(user);

        mockMvc.perform(get("/store/users/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john@test.com"));
    }

    @Test
    void shouldDeleteUser() throws Exception {

        User user = new User();
        user.setName("Alex");
        user.setSurname("Smith");
        user.setEmail("alex@test.com");
        user.setBirthDate(LocalDate.of(1998, 10, 15));
        user.setIsActive(true);

        User saved = userRepository.save(user);

        mockMvc.perform(delete("/store/users/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        assert(userRepository.findById(saved.getId())).isEmpty();
    }
}