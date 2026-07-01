package com.project.inno_online_store.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.inno_online_store.BaseIntegrationTest;
import com.project.inno_online_store.dto.request.CreatePaymentCardRequest;
import com.project.inno_online_store.dto.response.PaymentCardResponse;
import com.project.inno_online_store.jpa.entity.PaymentCard;
import com.project.inno_online_store.jpa.entity.User;
import com.project.inno_online_store.jpa.repository.PaymentCardRepository;
import com.project.inno_online_store.jpa.repository.UserRepository;
import com.project.inno_online_store.service.PaymentCardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PaymentCardControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentCardRepository paymentCardRepository;

    @MockitoSpyBean
    private PaymentCardService paymentCardService;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private Long userId;
    private Long cardId;
    private static final String INTERNAL_KEY = "secret-test";

    @BeforeEach
    void setUp() {
        paymentCardRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setName("John");
        user.setSurname("Doe");
        user.setEmail("john@test.com");
        user.setBirthDate(LocalDate.of(1995, 1, 1));
        user.setIsActive(true);

        userId = userRepository.save(user).getId();

        PaymentCard card = new PaymentCard();
        card.setUser(user);
        card.setNumber("1234567812345678");
        card.setHolder("JOHN DOE");
        card.setExpirationDate(LocalDate.now().plusYears(2));
        card.setIsActive(true);

        cardId = paymentCardRepository.save(card).getId();

        reset(paymentCardService);
    }

    @Nested
    class CreateCardTests {
        @Test
        void createPaymentCardByUserId_AsStranger_Forbidden() throws Exception {
            CreatePaymentCardRequest request = new CreatePaymentCardRequest();
            request.setNumber("1111222233334444");
            request.setHolder("JOHN DOE");
            request.setExpirationDate(LocalDate.now().plusYears(3));

            mockMvc.perform(post("/api/cards/{id}", 999L)
                            .header("X-Internal-Key", INTERNAL_KEY)
                            .header("X-User-Id", "999")
                            .header("X-User-Role", "ROLE_USER")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    class GetCardByIdTests {
        @Test
        void getPaymentCardById_AsAdmin_Success() throws Exception {
            PaymentCardResponse response = new PaymentCardResponse();
            response.setId(cardId);
            doReturn(response).when(paymentCardService).getPaymentCardById(cardId);

            mockMvc.perform(get("/api/cards/{id}", cardId)
                            .header("X-Internal-Key", INTERNAL_KEY)
                            .header("X-User-Id", "999")
                            .header("X-User-Role", "ROLE_ADMIN"))
                    .andExpect(status().isOk());
        }

        @Test
        void getPaymentCardById_AsStranger_Forbidden() throws Exception {
            doReturn(false).when(paymentCardService).isCardOwner(cardId, 999L);

            mockMvc.perform(get("/api/cards/{id}", cardId)
                            .header("X-Internal-Key", INTERNAL_KEY)
                            .header("X-User-Id", "999")
                            .header("X-User-Role", "ROLE_USER"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    class GetAllCardsTests {
        @Test
        void getAllPaymentCards_AsUser_Forbidden() throws Exception {
            mockMvc.perform(get("/api/cards")
                            .header("X-Internal-Key", INTERNAL_KEY)
                            .header("X-User-Id", String.valueOf(userId))
                            .header("X-User-Role", "ROLE_USER"))
                    .andExpect(status().isForbidden());
        }

        @Test
        void getAllPaymentCards_InvalidPagination_BadRequest() throws Exception {
            mockMvc.perform(get("/api/cards")
                            .header("X-Internal-Key", INTERNAL_KEY)
                            .header("X-User-Id", String.valueOf(userId))
                            .header("X-User-Role", "ROLE_ADMIN")
                            .param("page", "-1")
                            .param("size", "500"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class ActivationTests {
        @Test
        void activateAndDeactivateCard_AsAdmin_Success() throws Exception {
            PaymentCardResponse activeResponse = new PaymentCardResponse();
            activeResponse.setIsActive(true);
            PaymentCardResponse inactiveResponse = new PaymentCardResponse();
            inactiveResponse.setIsActive(false);

            doReturn(inactiveResponse).when(paymentCardService).deactivatePaymentCard(cardId);
            doReturn(activeResponse).when(paymentCardService).activatePaymentCard(cardId);

            mockMvc.perform(put("/api/cards/{id}/deactivate", cardId)
                            .header("X-Internal-Key", INTERNAL_KEY)
                            .header("X-User-Id", String.valueOf(userId))
                            .header("X-User-Role", "ROLE_ADMIN"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isActive", is(false)));

            mockMvc.perform(put("/api/cards/{id}/activate", cardId)
                            .header("X-Internal-Key", INTERNAL_KEY)
                            .header("X-User-Id", String.valueOf(userId))
                            .header("X-User-Role", "ROLE_ADMIN"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isActive", is(true)));
        }
    }

    @Nested
    class DeleteCardTests {
        @Test
        void deleteCard_AsOwner_Success() throws Exception {
            doReturn(true).when(paymentCardService).isCardOwner(cardId, userId);

            mockMvc.perform(delete("/api/cards/{id}", cardId)
                            .header("X-Internal-Key", INTERNAL_KEY)
                            .header("X-User-Id", String.valueOf(userId))
                            .header("X-User-Role", "ROLE_USER"))
                    .andExpect(status().isNoContent());
        }
    }
}