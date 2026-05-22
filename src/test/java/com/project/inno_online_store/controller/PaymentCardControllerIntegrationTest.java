package com.project.inno_online_store.controller;

import com.project.inno_online_store.BaseIntegrationTest;
import com.project.inno_online_store.jpa.entity.PaymentCard;
import com.project.inno_online_store.jpa.entity.User;
import com.project.inno_online_store.jpa.repository.PaymentCardRepository;
import com.project.inno_online_store.jpa.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PaymentCardControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentCardRepository paymentCardRepository;

    private Long userId;
    private Long cardId;

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

        user = userRepository.save(user);
        userId = user.getId();

        PaymentCard card = new PaymentCard();
        card.setUser(user);
        card.setNumber("1234567812345678");
        card.setHolder("JOHN DOE");
        card.setExpirationDate(LocalDate.now().plusYears(2));
        card.setIsActive(true);

        card = paymentCardRepository.save(card);
        cardId = card.getId();
    }

    @Test
    void shouldCreatePaymentCard() throws Exception {
        mockMvc.perform(post("/store/cards/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "number": "1111222233334444",
                          "holder": "JOHN DOE",
                          "expirationDate": "2030-12-12"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.number", is("1111222233334444")))
                .andExpect(jsonPath("$.holder", is("JOHN DOE")));
    }

    @Test
    void shouldGetPaymentCardById() throws Exception {
        mockMvc.perform(get("/store/cards/{id}", cardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(cardId.intValue())))
                .andExpect(jsonPath("$.number", is("1234567812345678")));
    }

    @Test
    void shouldUpdatePaymentCard() throws Exception {
        mockMvc.perform(patch("/store/cards/{id}", cardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "holder": "UPDATED HOLDER",
                          "expirationDate": "2035-01-01"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.holder", is("UPDATED HOLDER")));
    }

    @Test
    void shouldActivateAndDeactivateCard() throws Exception {

        mockMvc.perform(patch("/store/cards/{id}/deactivate", cardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive", is(false)));

        mockMvc.perform(patch("/store/cards/{id}/activate", cardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive", is(true)));
    }
}