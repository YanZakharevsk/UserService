package com.project.inno_online_store.controller;

import com.project.inno_online_store.dto.mapper.PaymentCardMapper;
import com.project.inno_online_store.dto.request.CreatePaymentCardRequest;
import com.project.inno_online_store.dto.request.PaymentCardPageRequest;
import com.project.inno_online_store.dto.request.UpdatePaymentCardRequest;
import com.project.inno_online_store.dto.response.PageResponse;
import com.project.inno_online_store.dto.response.PaymentCardResponse;
import com.project.inno_online_store.dto.response.PaymentCardShortResponse;
import com.project.inno_online_store.service.PaymentCardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/store/cards")
public class PaymentCardController {
    private final PaymentCardService paymentCardService;
    private final PaymentCardMapper paymentCardMapper;

    public PaymentCardController(PaymentCardService paymentCardService, PaymentCardMapper paymentCardMapper) {
        this.paymentCardService = paymentCardService;
        this.paymentCardMapper = paymentCardMapper;
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal")
    public ResponseEntity<PaymentCardResponse> createPaymentCard(@PathVariable(name = "id") Long userId, @Valid @RequestBody CreatePaymentCardRequest paymentCardRequest){

        PaymentCardResponse response = paymentCardService.createPaymentCard(userId, paymentCardRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @paymentCardServiceImpl.isCardOwner(#cardId, authentication.principal)")
    public ResponseEntity<PaymentCardResponse> getPaymentCardById(@PathVariable(name = "id") Long cardId){
        PaymentCardResponse paymentCardResponse = paymentCardService.getPaymentCardById(cardId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(paymentCardResponse);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<PaymentCardResponse>> getAllPaymentCardsWithPagination(@Valid @RequestBody PaymentCardPageRequest pageRequest){
        PageResponse<PaymentCardResponse> paymentCardResponses = paymentCardService.getAllPaymentCardsWithPagination(pageRequest.getPage(), pageRequest.getSize());
        return ResponseEntity.status(HttpStatus.OK)
                .body(paymentCardResponses);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @paymentCardServiceImpl.isCardOwner(#cardId, authentication.principal)")
    public ResponseEntity<PaymentCardShortResponse> updatePaymentCard(@PathVariable(name = "id") Long cardId, @Valid @RequestBody UpdatePaymentCardRequest paymentCardRequest){
        PaymentCardShortResponse response = paymentCardService.updatePaymentCardById(cardId, paymentCardRequest);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentCardResponse> activateCard(@PathVariable(name = "id") Long cardId){
        PaymentCardResponse response = paymentCardService.activatePaymentCard(cardId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentCardResponse> deactivateCard(@PathVariable(name = "id") Long cardId){
        PaymentCardResponse response = paymentCardService.deactivatePaymentCard(cardId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @paymentCardServiceImpl.isCardOwner(#cardId, authentication.principal)")
    public ResponseEntity<Void> deleteCard(@PathVariable(name = "id") Long cardId){
        paymentCardService.deletePaymentCardById(cardId);
        return ResponseEntity.noContent().build();
    }

}
