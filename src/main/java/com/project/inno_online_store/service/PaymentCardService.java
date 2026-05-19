package com.project.inno_online_store.service;

import com.project.inno_online_store.dto.request.CreatePaymentCardRequest;
import com.project.inno_online_store.dto.request.UpdatePaymentCardRequest;
import com.project.inno_online_store.dto.response.PageResponse;
import com.project.inno_online_store.dto.response.PaymentCardResponse;
import com.project.inno_online_store.dto.response.PaymentCardShortResponse;

import java.util.List;

/**
 * Service interface for managing payment cards in the online store.
 * Provides operations for card creation, retrieval, updating, status management,
 * and ownership validation.
 */
public interface PaymentCardService {

    /**
     * Creates a new payment card and associates it with a specific user.
     *
     * @param userId             the unique identifier of the user who will own the card
     * @param paymentCardRequest the data transfer object containing the payment card details
     * @return a {@link PaymentCardResponse} containing the created card's data
     * @throws RuntimeException if the user with the specified ID is not found
     */
    PaymentCardResponse createPaymentCard(Long userId, CreatePaymentCardRequest paymentCardRequest);

    /**
     * Retrieves a specific payment card by its unique identifier.
     *
     * @param cardId the unique identifier of the payment card to retrieve
     * @return a {@link PaymentCardResponse} containing the card's data
     * @throws RuntimeException if the card with the specified ID is not found
     */
    PaymentCardResponse getPaymentCardById(Long cardId);

    /**
     * Retrieves all payment cards associated with a specific user.
     *
     * @param userId the unique identifier of the user
     * @return a list of {@link PaymentCardResponse} objects representing the user's cards;
     * returns an empty list if the user has no cards
     */
    List<PaymentCardResponse> getPaymentCardsByUserId(Long userId);

    /**
     * Retrieves a paginated list of all payment cards in the system.
     * Typically used for administrative purposes.
     *
     * @param page the page number to retrieve (usually zero-based)
     * @param size the maximum number of cards per page
     * @return a {@link PageResponse} containing the list of {@link PaymentCardResponse} objects and pagination details
     */
    PageResponse<PaymentCardResponse> getAllPaymentCardsWithPagination(int page, int size);

    /**
     * Updates the details of an existing payment card.
     * Only the provided non-null fields in the request will be updated.
     *
     * @param cardId             the unique identifier of the payment card to update
     * @param paymentCardRequest the data transfer object containing the fields to update
     * @return a {@link PaymentCardShortResponse} containing the updated card's basic data
     * @throws RuntimeException if the card with the specified ID is not found
     */
    PaymentCardShortResponse updatePaymentCardById(Long cardId, UpdatePaymentCardRequest paymentCardRequest);

    /**
     * Activates a payment card, allowing it to be used for transactions.
     *
     * @param cardId the unique identifier of the payment card to activate
     * @return a {@link PaymentCardResponse} reflecting the card's updated active status
     * @throws RuntimeException if the card with the specified ID is not found
     */
    PaymentCardResponse activatePaymentCard(Long cardId);

    /**
     * Deactivates a payment card, preventing it from being used for transactions.
     *
     * @param cardId the unique identifier of the payment card to deactivate
     * @return a {@link PaymentCardResponse} reflecting the card's updated inactive status
     * @throws RuntimeException if the card with the specified ID is not found
     */
    PaymentCardResponse deactivatePaymentCard(Long cardId);

    /**
     * Permanently deletes a payment card from the system.
     *
     * @param cardId the unique identifier of the payment card to delete
     * @return {@code true} if the card was successfully deleted, {@code false} if the deletion failed
     */
    boolean deletePaymentCardById(Long cardId);

    /**
     * Checks whether a specific user is the owner of a specific payment card.
     * This method is often used for security and authorization checks.
     *
     * @param cardId the unique identifier of the payment card
     * @param userId the unique identifier of the user
     * @return {@code true} if the user owns the card, {@code false} otherwise
     */
    boolean isCardOwner(Long cardId, Long userId);
}