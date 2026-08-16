package com.ammarbhatkar.SPay.payment.mapper;

import com.ammarbhatkar.SPay.payment.dto.response.PaymentResponse;
import com.ammarbhatkar.SPay.payment.entity.PaymentTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentMapper {

    @Mapping(target = "type", expression = "java(paymentTransaction.getType().name())")
    @Mapping(target = "status", expression = "java(paymentTransaction.getStatus().name())")
    PaymentResponse toResponse(PaymentTransaction paymentTransaction);

    List<PaymentResponse> toResponseList(List<PaymentTransaction> paymentTransactions);
}