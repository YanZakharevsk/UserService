package com.project.inno_online_store.controller;

import com.project.inno_online_store.dto.request.CreatePaymentCardRequest;
import com.project.inno_online_store.dto.request.PaymentCardPageRequest;
import com.project.inno_online_store.dto.request.UpdatePaymentCardRequest;
import com.project.inno_online_store.dto.response.PageResponse;
import com.project.inno_online_store.dto.response.PaymentCardResponse;
import com.project.inno_online_store.dto.response.PaymentCardShortResponse;
import com.project.inno_online_store.service.PaymentCardService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards")
public class PaymentCardController {

    private final PaymentCardService paymentCardService;

    public PaymentCardController(PaymentCardService paymentCardService) {
        this.paymentCardService = paymentCardService;
    }

    @PostMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PaymentCardResponse> createPaymentCardByCurrentUser(@AuthenticationPrincipal Long currentUserId,
                                                                 @Valid @RequestBody CreatePaymentCardRequest paymentCardRequest){
        PaymentCardResponse response = paymentCardService.createPaymentCard(currentUserId, paymentCardRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentCardResponse> createPaymentCardByUserId(@PathVariable(name = "id") Long userId,
                                                                 @Valid @RequestBody CreatePaymentCardRequest paymentCardRequest){
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
    public ResponseEntity<PageResponse<PaymentCardResponse>> getAllPaymentCardsWithPagination(@Min(value = 0,message = "Page number can not less than 0")
                                                                                              @RequestParam(defaultValue = "0") int page,
                                                                                              @Min(value = 1, message = "Size number can not less than 1")
                                                                                                  @Max(value = 100, message = "Size number can not more than 100")
                                                                                              @RequestParam(defaultValue = "10") int size
    ){
        PaymentCardPageRequest pageRequest = new PaymentCardPageRequest(page, size);

        PageResponse<PaymentCardResponse> paymentCardResponses = paymentCardService.getAllPaymentCardsWithPagination(pageRequest.getPage(), pageRequest.getSize());
        return ResponseEntity.status(HttpStatus.OK)
                .body(paymentCardResponses);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @paymentCardServiceImpl.isCardOwner(#cardId, authentication.principal)")
    public ResponseEntity<PaymentCardShortResponse> updatePaymentCardById(
                                                                      @PathVariable(name = "id") Long cardId,
                                                                      @Valid @RequestBody UpdatePaymentCardRequest paymentCardRequest){
        PaymentCardShortResponse response = paymentCardService.updatePaymentCardById(cardId, paymentCardRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentCardResponse> activateCard(@PathVariable(name = "id") Long cardId){
        PaymentCardResponse response = paymentCardService.activatePaymentCard(cardId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PutMapping("/{id}/deactivate")
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
