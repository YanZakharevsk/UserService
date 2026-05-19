package com.project.inno_online_store.dto.mapper;

import com.project.inno_online_store.controller.UserSearchCriteria;
import com.project.inno_online_store.dto.request.CreateUserRequest;
import com.project.inno_online_store.dto.request.UpdateUserRequest;
import com.project.inno_online_store.dto.response.UserResponse;
import com.project.inno_online_store.dto.response.UserShortResponse;
import com.project.inno_online_store.jpa.entity.User;
import com.project.inno_online_store.jpa.repository.filter.UserFilter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = PaymentCardMapper.class)
public interface UserMapper {
    User toEntity(CreateUserRequest request);

    void update(@MappingTarget User user, UpdateUserRequest request);

    @Mapping(source = "paymentCards", target = "paymentCards")
    UserResponse toResponse(User user);
    UserShortResponse toShortResponse(User user);

    UserFilter toFilter(UserSearchCriteria userSearchCriteria);
}
