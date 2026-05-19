package com.project.inno_online_store.dao;

import com.project.inno_online_store.jpa.entity.PaymentCard;
import com.project.inno_online_store.jpa.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PaymentCardDao {
    PaymentCard save(PaymentCard card);

    Long countByUserId(Long userId);

    Optional<PaymentCard> findById(Long cardId);

    List<PaymentCard> findPaymentCardsByUser(User user);

    Page<PaymentCard> findAll(Pageable pageable);

    void delete(PaymentCard card);

    boolean existsByIdAndUserId(Long cardId, Long userId);

    void deleteAll();
}
