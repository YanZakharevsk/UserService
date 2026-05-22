package com.project.inno_online_store.service.serviceImpl;

import com.project.inno_online_store.dto.mapper.PaymentCardMapper;
import com.project.inno_online_store.dto.request.CreatePaymentCardRequest;
import com.project.inno_online_store.dto.request.UpdatePaymentCardRequest;
import com.project.inno_online_store.dto.response.PageResponse;
import com.project.inno_online_store.dto.response.PaymentCardResponse;
import com.project.inno_online_store.dto.response.PaymentCardShortResponse;
import com.project.inno_online_store.dto.response.UserResponse;
import com.project.inno_online_store.exception.PaymentCardNotFoundException;
import com.project.inno_online_store.exception.UserCardLimitExceededException;
import com.project.inno_online_store.exception.UserNotFoundException;
import com.project.inno_online_store.jpa.entity.PaymentCard;
import com.project.inno_online_store.jpa.entity.User;
import com.project.inno_online_store.jpa.repository.PaymentCardRepository;
import com.project.inno_online_store.jpa.repository.UserRepository;
import com.project.inno_online_store.service.PaymentCardService;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PaymentCardServiceImpl implements PaymentCardService {

    private final PaymentCardRepository paymentCardRepository;
    private final UserRepository userRepository;
    private final PaymentCardMapper paymentCardMapper;

    public PaymentCardServiceImpl(PaymentCardRepository paymentCardRepository, UserRepository userRepository, PaymentCardMapper paymentCardMapper) {
        this.paymentCardRepository = paymentCardRepository;
        this.userRepository = userRepository;
        this.paymentCardMapper = paymentCardMapper;
    }

    @Override
    public PaymentCardResponse createPaymentCard(Long userId, CreatePaymentCardRequest paymentCardRequest) {

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
            if(user.getPaymentCards().size() >= 5 ) {
                throw new UserCardLimitExceededException(userId);
            }
        PaymentCard paymentCard = paymentCardMapper.toEntity(paymentCardRequest);
            paymentCard.setUser(user);
            paymentCard.setIsActive(true);
            paymentCardRepository.save(paymentCard);

        return paymentCardMapper.toResponse(paymentCard);
    }

    @Override
    @Cacheable(value = "cards", key = "#cardId")
    public PaymentCardResponse getPaymentCardById(Long cardId) {
        PaymentCard paymentCard = paymentCardRepository.findById(cardId).orElseThrow(() -> new PaymentCardNotFoundException(cardId));

        return paymentCardMapper.toResponse(paymentCard);
    }

    @Override
    @Cacheable(value = "cards", key = "#userId")
    public List<PaymentCardResponse> getPaymentCardsByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        List<PaymentCard> paymentCards = paymentCardRepository.findPaymentCardsByUser(user);

        return paymentCards.stream().map(paymentCardMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public Set<PaymentCardResponse> getAllPaymentCards() {
        Set<PaymentCard> paymentCards =  (Set<PaymentCard>) paymentCardRepository.findAll();

        return paymentCards.stream().map(paymentCardMapper::toResponse).collect(Collectors.toSet());
    }

    @Override
    @Cacheable(value = "cards_page", key = "#page + '_' + #size")
    public PageResponse<PaymentCardResponse> getAllPaymentCardsWithPagination(int page, int size) {
        Page<PaymentCard> cardPage = paymentCardRepository.findAll(
                PageRequest.of(page, size)
        );
        List<PaymentCardResponse> content = cardPage.getContent()
                .stream()
                .map(paymentCardMapper::toResponse)
                .toList();

        return new PageResponse<>(
                content,
                cardPage.getNumber(),
                cardPage.getSize(),
                cardPage.getTotalElements()
        );
    }

    @Transactional
    @Override
    @CachePut(value = "cards", key = "#cardId")
    public PaymentCardShortResponse updatePaymentCardById(Long cardId, UpdatePaymentCardRequest paymentCardRequest) {

        PaymentCard paymentCard = paymentCardRepository.findById(cardId).orElseThrow(() -> new PaymentCardNotFoundException(cardId));
        paymentCardMapper.update(paymentCard, paymentCardRequest);
        paymentCardRepository.save(paymentCard);

        return paymentCardMapper.toShortResponse(paymentCard);
    }

    @Override
    @CachePut(value = "cards", key = "#cardId")
    public PaymentCardResponse activatePaymentCard(Long cardId) {
        PaymentCard paymentCard = paymentCardRepository.findById(cardId).orElseThrow(() -> new PaymentCardNotFoundException(cardId));
        paymentCard.setIsActive(true);
        paymentCardRepository.save(paymentCard);
        return paymentCardMapper.toResponse(paymentCard);
    }

    @Override
    @CachePut(value = "cards", key = "#cardId")
    public PaymentCardResponse deactivatePaymentCard(Long cardId) {
        PaymentCard paymentCard = paymentCardRepository.findById(cardId).orElseThrow(() -> new PaymentCardNotFoundException(cardId));
        paymentCard.setIsActive(false);
        paymentCardRepository.save(paymentCard);
        return paymentCardMapper.toResponse(paymentCard);
    }

    @Transactional
    @Override
    @CacheEvict(value = "cards", key = "#cardId")
    public boolean deletePaymentCardById(Long cardId) {

        PaymentCard paymentCard = paymentCardRepository.findById(cardId).orElseThrow(() -> new PaymentCardNotFoundException(cardId));
        paymentCardRepository.delete(paymentCard);
        return true;
    }
}
