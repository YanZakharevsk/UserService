package com.project.inno_online_store.exception;

public class PaymentCardNotFoundException extends RuntimeException {
    public PaymentCardNotFoundException(Long cardId) {
        super("Card with id " + cardId + " not found");
    }
}
