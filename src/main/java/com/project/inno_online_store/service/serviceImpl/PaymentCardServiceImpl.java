package com.project.inno_online_store.service.serviceImpl;

import com.project.inno_online_store.jpa.entity.PaymentCard;
import com.project.inno_online_store.jpa.entity.User;
import com.project.inno_online_store.jpa.repository.PaymentCardRepository;
import com.project.inno_online_store.jpa.repository.UserRepository;
import com.project.inno_online_store.service.PaymentCardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class PaymentCardServiceImpl implements PaymentCardService {

    private final PaymentCardRepository paymentCardRepository;
    private final UserRepository userRepository;

    public PaymentCardServiceImpl(PaymentCardRepository paymentCardRepository, UserRepository userRepository) {
        this.paymentCardRepository = paymentCardRepository;
        this.userRepository = userRepository;
    }

    @Override
    public PaymentCard createPaymentCard(Long userId,PaymentCard paymentCard) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if(!optionalUser.isEmpty()){
            User user = optionalUser.get();

            if(user.getPaymentCards().size() > 5 ) {
                throw new RuntimeException("User can not have more than 5 cards");
            }

            paymentCard.setUser(user);
            paymentCard.setIsActive(true);
            return paymentCardRepository.save(paymentCard);
        }
        return null;
    }

    @Override
    public Optional<PaymentCard> getPaymentCardById(Long cardId) {
        return paymentCardRepository.findById(cardId);
    }

    @Override
    public Set<PaymentCard> getAllPaymentCards() {
        return (Set<PaymentCard>) paymentCardRepository.findAll();
    }

    @Override
    public Page<PaymentCard> getAllPaymentCardsWithPagination(int page, int size) {
        return paymentCardRepository.findAll(PageRequest.of(page, size));
    }

    @Override
    public PaymentCard updatePaymentCardById(Long cardId, PaymentCard updatePaymentCard) {
        Optional<PaymentCard> optionalPaymentCard = paymentCardRepository.findById(cardId);
        if(!optionalPaymentCard.isEmpty()){
            PaymentCard paymentCard = optionalPaymentCard.get();
            paymentCard.setHolder(updatePaymentCard.getHolder());
            paymentCard.setExpirationDate(updatePaymentCard.getExpirationDate());
            return paymentCardRepository.save(paymentCard);
        }
        return null;
    }

    @Override
    public void activatePaymentCard(Long cardId) {
        Optional<PaymentCard> optionalPaymentCard = getPaymentCardById(cardId);
        if(!optionalPaymentCard.isEmpty()){
            PaymentCard paymentCard = optionalPaymentCard.get();
            paymentCard.setIsActive(true);
            paymentCardRepository.save(paymentCard);
        }
    }

    @Override
    public void deactivatePaymentCard(Long cardId) {
        Optional<PaymentCard> optionalPaymentCard = getPaymentCardById(cardId);
        if(!optionalPaymentCard.isEmpty()){
            PaymentCard paymentCard = optionalPaymentCard.get();
            paymentCard.setIsActive(false);
            paymentCardRepository.save(paymentCard);
        }
    }
}
