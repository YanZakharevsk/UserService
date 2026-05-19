package com.project.inno_online_store.jpa.repository;

import com.project.inno_online_store.jpa.entity.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long> {

    Set<PaymentCard> findByUserId(Long userId);

    @Query("SELECT pc from PaymentCard pc where pc.isActive = true")
    Set<PaymentCard> findActiveCards();
}
