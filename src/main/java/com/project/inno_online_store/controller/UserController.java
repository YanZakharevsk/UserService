package com.project.inno_online_store.controller;

import com.project.inno_online_store.dto.request.UpdateUserRequest;
import com.project.inno_online_store.dto.response.PageResponse;
import com.project.inno_online_store.dto.response.PaymentCardResponse;
import com.project.inno_online_store.dto.response.UserResponse;
import com.project.inno_online_store.dto.response.UserShortResponse;
import com.project.inno_online_store.jpa.repository.filter.UserFilter;
import com.project.inno_online_store.dto.response.PaymentCardResponse;
import com.project.inno_online_store.dto.response.UserResponse;
import com.project.inno_online_store.dto.response.UserShortResponse;
import com.project.inno_online_store.service.PaymentCardService;
import com.project.inno_online_store.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final PaymentCardService paymentCardService;

    public UserController(UserService userService, PaymentCardService paymentCardService) {
        this.userService = userService;
        this.paymentCardService = paymentCardService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal Long currentUserId) {
        UserResponse userResponse = userService.getUserById(currentUserId);
        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable(name = "id") Long userId) {
        UserResponse userResponse = userService.getUserById(userId);
        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<UserResponse>> getAllUsersWithPagination(
                                                                                @RequestParam(required = false) String name,
                                                                                @RequestParam(required = false) String surname,
                                                                                @Min(value = 0,message = "Page number can not less than 0")
                                                                                @RequestParam(defaultValue = "0") int page,
                                                                                @Min(value = 1, message = "Size number can not less than 1")
                                                                                    @Max(value = 100, message = "Size number can not more than 100")
                                                                                @RequestParam(defaultValue = "10") int size
    ){
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setName(name);
        criteria.setSurname(surname);
        criteria.setPage(page);
        criteria.setSize(size);

        UserFilter userFilter = new UserFilter(criteria.getName(), criteria.getSurname());
        PageResponse<UserResponse> userResponses = userService.getAllUsersWithPaginationAndFilter(userFilter, criteria.getPage(), criteria.getSize());
          return ResponseEntity.status(HttpStatus.OK)
                  .body(userResponses);
    }

    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UserShortResponse> updateCurrentUser(@AuthenticationPrincipal Long currentUserId,
                                                               @Valid @RequestBody UpdateUserRequest userRequest){

        UserShortResponse shortResponse = userService.updateUserById(currentUserId, userRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(shortResponse);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserShortResponse> updateUserById(
                                                            @PathVariable(name = "id") Long userId,
                                                            @Valid @RequestBody UpdateUserRequest userRequest){

        UserShortResponse shortResponse = userService.updateUserById(userId, userRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(shortResponse);
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> activateUser(@PathVariable(name = "id") Long userId){
        UserResponse userResponse = userService.activateUser(userId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(userResponse);
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> deactivateUser(@PathVariable(name = "id") Long userId){
        UserResponse userResponse = userService.deactivateUser(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(userResponse);
    }

    @GetMapping("/me/cards")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<PaymentCardResponse>> getPaymentCardsByCurrentUser(@AuthenticationPrincipal Long currentUserId){
        List<PaymentCardResponse> paymentCardResponses = paymentCardService.getPaymentCardsByUserId(currentUserId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(paymentCardResponses);
    }

    @GetMapping("/{id}/cards")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PaymentCardResponse>> getPaymentCardsByUserId(@PathVariable(name = "id") Long userId){
        List<PaymentCardResponse> paymentCardResponses = paymentCardService.getPaymentCardsByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(paymentCardResponses);
    }
}
