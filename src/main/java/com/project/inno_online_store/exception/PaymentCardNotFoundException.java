package com.project.inno_online_store.exception;

import org.springframework.http.HttpStatus;

public class PaymentCardNotFoundException extends BaseException {
    public PaymentCardNotFoundException(Long cardId) {
        super("Card with id " + cardId + " not found","CARD_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
