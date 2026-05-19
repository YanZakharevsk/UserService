package com.project.inno_online_store.jpa.repository;

import com.project.inno_online_store.jpa.entity.PaymentCard;
import com.project.inno_online_store.jpa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long> {

    List<PaymentCard> findByUserId(Long userId);

    @Query("SELECT pc from PaymentCard pc where pc.isActive = true")
    List<PaymentCard> findActiveCards();

    @Query(value = "SELECT COUNT(*) FROM public.payment_cards WHERE user_id = :userId", nativeQuery = true)
    Long countByUserId(Long userId);

    List<PaymentCard> findPaymentCardsByUser(User user);

    boolean existsByIdAndUserId(Long cardId, Long userId);
}
