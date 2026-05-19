package com.project.inno_online_store.service.impl;

import com.project.inno_online_store.dao.PaymentCardDao;
import com.project.inno_online_store.dao.UserDao;
import com.project.inno_online_store.dto.mapper.PaymentCardMapper;
import com.project.inno_online_store.dto.request.CreatePaymentCardRequest;
import com.project.inno_online_store.dto.request.UpdatePaymentCardRequest;
import com.project.inno_online_store.dto.response.PageResponse;
import com.project.inno_online_store.dto.response.PaymentCardResponse;
import com.project.inno_online_store.dto.response.PaymentCardShortResponse;
import com.project.inno_online_store.exception.PaymentCardNotFoundException;
import com.project.inno_online_store.exception.UserCardLimitExceededException;
import com.project.inno_online_store.exception.UserNotFoundException;
import com.project.inno_online_store.jpa.entity.PaymentCard;
import com.project.inno_online_store.jpa.entity.User;
import com.project.inno_online_store.service.PaymentCardService;
import jakarta.transaction.Transactional;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentCardServiceImpl implements PaymentCardService {

    private final PaymentCardDao paymentCardDao;
    private final UserDao userDao;
    private final PaymentCardMapper paymentCardMapper;
    private final CacheManager cacheManager;

    public PaymentCardServiceImpl(PaymentCardDao paymentCardDao, UserDao userDao, PaymentCardMapper paymentCardMapper, CacheManager cacheManager) {
        this.paymentCardDao = paymentCardDao;
        this.userDao = userDao;
        this.paymentCardMapper = paymentCardMapper;
        this.cacheManager = cacheManager;
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = "cards_page", allEntries = true),
            @CacheEvict(value = "users_cards", key = "#userId"),
            @CacheEvict(value = "users", key = "#userId")
    })
    public PaymentCardResponse createPaymentCard(Long userId, CreatePaymentCardRequest paymentCardRequest) {

        User user = userDao.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

            if(paymentCardDao.countByUserId(userId) >= 5 ) {
                throw new UserCardLimitExceededException(userId);
            }
        PaymentCard paymentCard = paymentCardMapper.toEntity(paymentCardRequest);
            paymentCard.setUser(user);
            paymentCard.setIsActive(true);
        paymentCardDao.save(paymentCard);

        return paymentCardMapper.toResponse(paymentCard);
    }

    @Override
    @Cacheable(value = "cards", key = "#cardId")
    public PaymentCardResponse getPaymentCardById(Long cardId) {
        PaymentCard paymentCard = paymentCardDao.findById(cardId).orElseThrow(() -> new PaymentCardNotFoundException(cardId));

        return paymentCardMapper.toResponse(paymentCard);
    }

    @Override
    @Cacheable(value = "user_cards", key = "#userId")
    public List<PaymentCardResponse> getPaymentCardsByUserId(Long userId) {
        User user = userDao.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        List<PaymentCard> paymentCards = paymentCardDao.findPaymentCardsByUser(user);

        return paymentCards.stream().map(paymentCardMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "cards_page", key = "#page + '_' + #size")
    public PageResponse<PaymentCardResponse> getAllPaymentCardsWithPagination(int page, int size) {
        Page<PaymentCard> cardPage = paymentCardDao.findAll(
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
    public PaymentCardShortResponse updatePaymentCardById(Long cardId, UpdatePaymentCardRequest paymentCardRequest) {
        PaymentCard paymentCard = paymentCardDao.findById(cardId).orElseThrow(() -> new PaymentCardNotFoundException(cardId));
        Long userId = paymentCard.getUser().getId();

        paymentCardMapper.update(paymentCard, paymentCardRequest);
        paymentCardDao.save(paymentCard);

        evictCardCaches(cardId, userId);
        return paymentCardMapper.toShortResponse(paymentCard);
    }

    @Transactional
    @Override
    public PaymentCardResponse activatePaymentCard(Long cardId) {

        PaymentCard paymentCard = paymentCardDao.findById(cardId).orElseThrow(() -> new PaymentCardNotFoundException(cardId));
        Long userId = paymentCard.getUser().getId();

        paymentCard.setIsActive(true);
        paymentCardDao.save(paymentCard);

        evictCardCaches(cardId, userId);
        return paymentCardMapper.toResponse(paymentCard);
    }

    @Transactional
    @Override
    public PaymentCardResponse deactivatePaymentCard(Long cardId) {
        PaymentCard paymentCard = paymentCardDao.findById(cardId).orElseThrow(() -> new PaymentCardNotFoundException(cardId));
        Long userId = paymentCard.getUser().getId();

        paymentCard.setIsActive(false);
        paymentCardDao.save(paymentCard);

        evictCardCaches(cardId, userId);
        return paymentCardMapper.toResponse(paymentCard);
    }

    @Transactional
    @Override
    public boolean deletePaymentCardById(Long cardId) {

        PaymentCard paymentCard = paymentCardDao.findById(cardId).orElseThrow(() -> new PaymentCardNotFoundException(cardId));
        Long userId = paymentCard.getUser().getId();
        paymentCardDao.delete(paymentCard);

        evictCardCaches(cardId, userId);
        return true;
    }

    @Transactional
    public boolean isCardOwner(Long cardId, Long userId) {
        return paymentCardDao.existsByIdAndUserId(cardId, userId);
    }

    private void evictCardCaches(Long cardId, Long userId){
        Cache cardsCache = cacheManager.getCache("cards");
        Cache cardsPageCache = cacheManager.getCache("cards_page");
        Cache userCardsCache = cacheManager.getCache("user_cards");
        Cache usersCache = cacheManager.getCache("users");

        if(cardsCache != null) cardsCache.evict(cardId);
        if(cardsPageCache != null) cardsPageCache.clear();
        if(userCardsCache != null) userCardsCache.evict(userId);
        if(usersCache != null) usersCache.evict(userId);
    }
}

