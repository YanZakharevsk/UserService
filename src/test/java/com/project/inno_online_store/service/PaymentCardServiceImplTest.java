package com.project.inno_online_store.service;

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
import com.project.inno_online_store.service.impl.PaymentCardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentCardServiceImplTest {

    @Mock
    private PaymentCardDao paymentCardDao;

    @Mock
    private UserDao userDao;

    @Mock
    private PaymentCardMapper paymentCardMapper;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private PaymentCardServiceImpl paymentCardService;

    private User user;
    private PaymentCard card;
    private PaymentCardResponse cardResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        card = new PaymentCard();
        card.setId(10L);
        card.setUser(user);
        card.setIsActive(true);

        cardResponse = new PaymentCardResponse();
        cardResponse.setId(10L);
    }

    @Test
    void createPaymentCard_Success() {
        CreatePaymentCardRequest request = new CreatePaymentCardRequest();
        request.setNumber("1234567890123456");

        when(userDao.findById(1L)).thenReturn(Optional.of(user));
        when(paymentCardMapper.toEntity(request)).thenReturn(card);
        when(paymentCardDao.save(any(PaymentCard.class))).thenReturn(card);
        when(paymentCardMapper.toResponse(card)).thenReturn(cardResponse);

        PaymentCardResponse result = paymentCardService.createPaymentCard(1L, request);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertTrue(card.getIsActive());
        assertEquals(user, card.getUser());
        verify(paymentCardDao, times(1)).save(card);
    }

    @Test
    void createPaymentCard_ThrowsUserNotFoundException() {
        CreatePaymentCardRequest request = new CreatePaymentCardRequest();
        when(userDao.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> paymentCardService.createPaymentCard(1L, request));
        verifyNoInteractions(paymentCardDao);
        verifyNoInteractions(paymentCardMapper);
    }

    @Test
    void createPaymentCard_ThrowsUserCardLimitExceededException() {
        CreatePaymentCardRequest request = new CreatePaymentCardRequest();

        when(userDao.findById(1L)).thenReturn(Optional.of(user));
        when(paymentCardDao.countByUserId(1L)).thenReturn(5L);

        assertThrows(UserCardLimitExceededException.class, () -> paymentCardService.createPaymentCard(1L, request));
        verify(paymentCardDao, never()).save(any(PaymentCard.class));
    }

    @Test
    void getPaymentCardById_Success() {
        when(paymentCardDao.findById(10L)).thenReturn(Optional.of(card));
        when(paymentCardMapper.toResponse(card)).thenReturn(cardResponse);

        PaymentCardResponse result = paymentCardService.getPaymentCardById(10L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        verify(paymentCardDao, times(1)).findById(10L);
    }

    @Test
    void getPaymentCardById_ThrowsPaymentCardNotFoundException() {
        when(paymentCardDao.findById(10L)).thenReturn(Optional.empty());

        assertThrows(PaymentCardNotFoundException.class, () -> paymentCardService.getPaymentCardById(10L));
    }

    @Test
    void getPaymentCardsByUserId_Success() {
        when(userDao.findById(1L)).thenReturn(Optional.of(user));
        when(paymentCardDao.findPaymentCardsByUser(user)).thenReturn(List.of(card));
        when(paymentCardMapper.toResponse(card)).thenReturn(cardResponse);

        List<PaymentCardResponse> result = paymentCardService.getPaymentCardsByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(paymentCardDao, times(1)).findPaymentCardsByUser(user);
    }

    @Test
    void getPaymentCardsByUserId_ThrowsUserNotFoundException() {
        when(userDao.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> paymentCardService.getPaymentCardsByUserId(1L));
        verify(paymentCardDao, never()).findPaymentCardsByUser(any());
    }

    @Test
    void getAllPaymentCardsWithPagination_Success() {
        Page<PaymentCard> cardPage = new PageImpl<>(List.of(card));
        when(paymentCardDao.findAll(any(PageRequest.class))).thenReturn(cardPage);
        when(paymentCardMapper.toResponse(card)).thenReturn(cardResponse);

        PageResponse<PaymentCardResponse> result = paymentCardService.getAllPaymentCardsWithPagination(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(0, result.getPage());
        assertEquals(1, result.getTotalElements());
        verify(paymentCardDao, times(1)).findAll(PageRequest.of(0, 10));
    }

    @Test
    void updatePaymentCardById_Success() {
        UpdatePaymentCardRequest request = new UpdatePaymentCardRequest();
        PaymentCardShortResponse shortResponse = new PaymentCardShortResponse();
        shortResponse.setId(10L);

        when(paymentCardDao.findById(10L)).thenReturn(Optional.of(card));
        doNothing().when(paymentCardMapper).update(card, request);
        when(paymentCardDao.save(card)).thenReturn(card);
        when(paymentCardMapper.toShortResponse(card)).thenReturn(shortResponse);

        PaymentCardShortResponse result = paymentCardService.updatePaymentCardById(10L, request);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        verify(paymentCardDao, times(1)).save(card);
    }

    @Test
    void updatePaymentCardById_ThrowsPaymentCardNotFoundException() {
        UpdatePaymentCardRequest request = new UpdatePaymentCardRequest();
        when(paymentCardDao.findById(10L)).thenReturn(Optional.empty());

        assertThrows(PaymentCardNotFoundException.class, () -> paymentCardService.updatePaymentCardById(10L, request));
        verify(paymentCardDao, never()).save(any(PaymentCard.class));
    }

    @Test
    void activatePaymentCard_Success() {
        card.setIsActive(false);
        when(paymentCardDao.findById(10L)).thenReturn(Optional.of(card));
        when(paymentCardDao.save(card)).thenReturn(card);
        when(paymentCardMapper.toResponse(card)).thenReturn(cardResponse);

        paymentCardService.activatePaymentCard(10L);

        assertTrue(card.getIsActive());
        verify(paymentCardDao, times(1)).save(card);
    }

    @Test
    void activatePaymentCard_ThrowsPaymentCardNotFoundException() {
        when(paymentCardDao.findById(10L)).thenReturn(Optional.empty());

        assertThrows(PaymentCardNotFoundException.class, () -> paymentCardService.activatePaymentCard(10L));
        verify(paymentCardDao, never()).save(any(PaymentCard.class));
    }

    @Test
    void deactivatePaymentCard_Success() {
        card.setIsActive(true);
        when(paymentCardDao.findById(10L)).thenReturn(Optional.of(card));
        when(paymentCardDao.save(card)).thenReturn(card);
        when(paymentCardMapper.toResponse(card)).thenReturn(cardResponse);

        PaymentCardResponse result = paymentCardService.deactivatePaymentCard(10L);

        assertNotNull(result);
        assertFalse(card.getIsActive());
        verify(paymentCardDao, times(1)).save(card);
    }

    @Test
    void deactivatePaymentCard_ThrowsPaymentCardNotFoundException() {
        when(paymentCardDao.findById(10L)).thenReturn(Optional.empty());

        assertThrows(PaymentCardNotFoundException.class, () -> paymentCardService.deactivatePaymentCard(10L));
        verify(paymentCardDao, never()).save(any(PaymentCard.class));
    }

    @Test
    void deletePaymentCardById_Success() {
        when(paymentCardDao.findById(10L)).thenReturn(Optional.of(card));
        doNothing().when(paymentCardDao).delete(card);

        boolean result = paymentCardService.deletePaymentCardById(10L);

        assertTrue(result);
        verify(paymentCardDao, times(1)).delete(card);
    }

    @Test
    void deletePaymentCardById_ThrowsPaymentCardNotFoundException() {
        when(paymentCardDao.findById(10L)).thenReturn(Optional.empty());

        assertThrows(PaymentCardNotFoundException.class, () -> paymentCardService.deletePaymentCardById(10L));
        verify(paymentCardDao, never()).delete(any(PaymentCard.class));
    }

    @Test
    void isCardOwner_ReturnsTrue() {
        when(paymentCardDao.existsByIdAndUserId(10L, 1L)).thenReturn(true);

        boolean result = paymentCardService.isCardOwner(10L, 1L);

        assertTrue(result);
        verify(paymentCardDao, times(1)).existsByIdAndUserId(10L, 1L);
    }

    @Test
    void isCardOwner_ReturnsFalse() {
        when(paymentCardDao.existsByIdAndUserId(10L, 1L)).thenReturn(false);

        boolean result = paymentCardService.isCardOwner(10L, 1L);

        assertFalse(result);
        verify(paymentCardDao, times(1)).existsByIdAndUserId(10L, 1L);
    }
}
