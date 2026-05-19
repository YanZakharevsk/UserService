package com.project.inno_online_store.service;

import com.project.inno_online_store.dto.request.CreateUserRequest;
import com.project.inno_online_store.dto.request.UpdateUserRequest;
import com.project.inno_online_store.dto.response.PageResponse;
import com.project.inno_online_store.dto.response.UserResponse;
import com.project.inno_online_store.dto.response.UserShortResponse;
import com.project.inno_online_store.jpa.repository.filter.UserFilter;

/**
 * Service interface for managing user-related operations in the online store.
 * Provides methods for CRUD operations, filtering, pagination, and user status management.
 */
public interface UserService {

    /**
     * Creates a new user in the system based on the provided request data.
     *
     * @param userRequest the data transfer object containing the new user's details
     * @return a {@link UserResponse} containing the created user's data
     */
    UserResponse createUser(CreateUserRequest userRequest);

    /**
     * Retrieves a specific user by their unique identifier.
     *
     * @param userId the unique identifier of the user to retrieve
     * @return a {@link UserResponse} containing the user's data
     * @throws RuntimeException if the user with the specified ID is not found (e.g., EntityNotFoundException)
     */
    UserResponse getUserById(Long userId);

    /**
     * Retrieves a paginated list of users that match the specified filtering criteria.
     *
     * @param userFilter the criteria used to filter the users (e.g., by name, email, etc.)
     * @param page       the page number to retrieve (usually zero-based)
     * @param size       the maximum number of users per page
     * @return a {@link PageResponse} containing the list of {@link UserResponse} objects and pagination details
     */
    PageResponse<UserResponse> getAllUsersWithPaginationAndFilter(UserFilter userFilter, int page, int size);

    /**
     * Updates an existing user's details. Only the provided non-null fields in the request will be updated.
     *
     * @param userId      the unique identifier of the user to update
     * @param userRequest the data transfer object containing the fields to update
     * @return a {@link UserShortResponse} containing the updated user's basic data
     * @throws RuntimeException if the user with the specified ID is not found
     */
    UserShortResponse updateUserById(Long userId, UpdateUserRequest userRequest);

    /**
     * Activates a user account, allowing them to log in and use the system.
     *
     * @param userId the unique identifier of the user to activate
     * @return a {@link UserResponse} reflecting the user's updated active status
     * @throws RuntimeException if the user with the specified ID is not found
     */
    UserResponse activateUser(Long userId);

    /**
     * Deactivates a user account (soft delete or block), preventing them from accessing the system.
     *
     * @param userId the unique identifier of the user to deactivate
     * @return a {@link UserResponse} reflecting the user's updated inactive status
     * @throws RuntimeException if the user with the specified ID is not found
     */
    UserResponse deactivateUser(Long userId);

    /**
     * Permanently deletes a user from the system.
     *
     * @param userId the unique identifier of the user to delete
     * @return {@code true} if the user was successfully deleted, {@code false} if the deletion failed
     */
    boolean deleteUserById(Long userId);

}