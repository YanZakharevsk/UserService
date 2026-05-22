package com.project.inno_online_store.service;


import com.project.inno_online_store.dto.request.CreatePaymentCardRequest;
import com.project.inno_online_store.dto.request.UpdatePaymentCardRequest;
import com.project.inno_online_store.dto.response.PaymentCardResponse;
import com.project.inno_online_store.dto.response.PaymentCardShortResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;

public interface PaymentCardService {

    public PaymentCardResponse createPaymentCard(Long userId, CreatePaymentCardRequest paymentCardRequest);

    public PaymentCardResponse getPaymentCardById(Long cardId);

    public List<PaymentCardResponse> getPaymentCardsByUserId(Long userId);

    public Set<PaymentCardResponse> getAllPaymentCards();

    public Page<PaymentCardResponse> getAllPaymentCardsWithPagination(int page, int size);

    public PaymentCardShortResponse updatePaymentCardById(Long cardId, UpdatePaymentCardRequest paymentCardRequest);

    public PaymentCardResponse activatePaymentCard(Long cardId);

    public PaymentCardResponse deactivatePaymentCard(Long cardId);

    public boolean deletePaymentCardById(Long cardId);
}
