package com.project.inno_online_store.dto.mapper;

import com.project.inno_online_store.dto.request.CreatePaymentCardRequest;
import com.project.inno_online_store.dto.request.UpdatePaymentCardRequest;
import com.project.inno_online_store.dto.response.PaymentCardResponse;
import com.project.inno_online_store.dto.response.PaymentCardShortResponse;
import com.project.inno_online_store.jpa.entity.PaymentCard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PaymentCardMapper {


    PaymentCard toEntity(CreatePaymentCardRequest cardRequest);

    void update(@MappingTarget PaymentCard paymentCard, UpdatePaymentCardRequest paymentCardRequest);

    @Mapping(source = "paymentCard.user.id", target = "userId")
    PaymentCardResponse toResponse(PaymentCard paymentCard);
    PaymentCardShortResponse toShortResponse(PaymentCard paymentCard);
}
