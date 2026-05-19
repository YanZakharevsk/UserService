package com.project.inno_online_store.service;


import com.project.inno_online_store.jpa.entity.PaymentCard;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.Set;

public interface PaymentCardService {

    public PaymentCard createPaymentCard(Long userId,PaymentCard paymentCard);

    public Optional<PaymentCard> getPaymentCardById(Long cardId);

    public Set<PaymentCard> getAllPaymentCards();

    public Page<PaymentCard> getAllPaymentCardsWithPagination(int page, int size);

    public PaymentCard updatePaymentCardById(Long cardId, PaymentCard updatePaymentCard);

    public void activatePaymentCard(Long cardId);

    public void deactivatePaymentCard(Long cardId);
}
