package com.project.inno_online_store.dao.impl;

import com.project.inno_online_store.dao.PaymentCardDao;
import com.project.inno_online_store.jpa.entity.PaymentCard;
import com.project.inno_online_store.jpa.repository.PaymentCardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import com.project.inno_online_store.jpa.entity.User;
import java.util.List;
import java.util.Optional;

@Component
public class PaymentCardDaoImpl implements PaymentCardDao {

    private final PaymentCardRepository repository;

    public PaymentCardDaoImpl(PaymentCardRepository repository) {
        this.repository = repository;
    }

    @Override
    public PaymentCard save(PaymentCard card) {
        return repository.save(card);
    }

    @Override
    public Long countByUserId(Long userId) {
        return repository.countByUserId(userId);
    }

    @Override
    public Optional<PaymentCard> findById(Long cardId) {
        return repository.findById(cardId);
    }

    @Override
    public List<PaymentCard> findPaymentCardsByUser(User user) {
        return repository.findPaymentCardsByUser(user);
    }

    @Override
    public Page<PaymentCard> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public void delete(PaymentCard card) {
      repository.delete(card);
    }

    @Override
    public boolean existsByIdAndUserId(Long cardId, Long userId) {
        return repository.existsByIdAndUserId(cardId, userId);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }
}
