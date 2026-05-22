package com.project.inno_online_store.controller;

import com.project.inno_online_store.BaseIntegrationTest;
import com.project.inno_online_store.dao.PaymentCardDao;
import com.project.inno_online_store.dao.UserDao;
import com.project.inno_online_store.jpa.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentCardDao paymentCardDao;

    @MockitoSpyBean
    private UserDao userDao;

    private User johnDoe;
    private User janeSmith;
    private static final String INTERNAL_KEY = "secret-test";

    @BeforeEach
    void setUp() {
        paymentCardDao.deleteAll();
        userDao.deleteAll();

        User user1 = new User();
        user1.setName("John");
        user1.setSurname("Doe");
        user1.setEmail("john.doe@example.com");
        user1.setBirthDate(LocalDate.of(1995, 5, 21));
        user1.setIsActive(true);
        johnDoe = userDao.save(user1);

        User user2 = new User();
        user2.setName("Jane");
        user2.setSurname("Smith");
        user2.setEmail("alex.smith@example.com");
        user2.setBirthDate(LocalDate.of(1998, 10, 15));
        user2.setIsActive(true);
        janeSmith = userDao.save(user2);

        clearInvocations(userDao);
    }

    @Nested
    class GetByIdEndpointTests {
        @Test
        void getUserById_AsAdmin_Success() throws Exception {
            mockMvc.perform(get("/api/users/{id}", janeSmith.getId())
                            .header("X-Internal-Key", INTERNAL_KEY)
                            .with(user(johnDoe.getId().toString()).roles("ADMIN")))
                    .andExpect(status().isOk());
        }

        @Test
        void getUserById_AsUser_Forbidden() throws Exception {
            mockMvc.perform(get("/api/users/{id}", johnDoe.getId())
                            .header("X-Internal-Key", INTERNAL_KEY)
                            .with(user(janeSmith.getId().toString()).roles("USER")))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    class GetAllWithPaginationTests {
        @Test
        void getAllUsers_AsAdmin_Success_AndVerifiesRedisCache() throws Exception {
            mockMvc.perform(get("/api/users")
                            .header("X-Internal-Key", INTERNAL_KEY)
                            .with(user(johnDoe.getId().toString()).roles("ADMIN"))
                            .param("name", "John"))
                    .andExpect(status().isOk());

            verify(userDao, times(1)).findAll(any(Specification.class), any(Pageable.class));
        }

        @Test
        void getAllUsers_AsUser_Forbidden() throws Exception {
            mockMvc.perform(get("/api/users")
                            .header("X-Internal-Key", INTERNAL_KEY)
                            .with(user(johnDoe.getId().toString()).roles("USER")))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    class ActivationTests {
        @Test
        void activateAndDeactivate_AsAdmin_Success() throws Exception {
            mockMvc.perform(put("/api/users/{id}/activate", janeSmith.getId())
                            .header("X-Internal-Key", INTERNAL_KEY)
                            .with(user(johnDoe.getId().toString()).roles("ADMIN")))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class UserCardsTests {

        @Test
        void getPaymentCardsByUser_AsStranger_Forbidden() throws Exception {
            mockMvc.perform(get("/api/users/{id}/cards", janeSmith.getId())
                            .header("X-Internal-Key", INTERNAL_KEY)
                            .with(user(johnDoe.getId().toString()).roles("USER")))
                    .andExpect(status().isForbidden());
        }
    }
}