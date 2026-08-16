package com.ammarbhatkar.SPay.payment.mapper;

import com.ammarbhatkar.SPay.payment.dto.response.PaymentTimelineResponse;
import com.ammarbhatkar.SPay.payment.entity.PaymentTimelineEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentTimelineMapper {

    @Mapping(target = "status", expression = "java(paymentTimelineEvent.getStatus().name())")
    PaymentTimelineResponse toResponse(PaymentTimelineEvent paymentTimelineEvent);

    List<PaymentTimelineResponse> toResponseList(List<PaymentTimelineEvent> paymentTimelineEvents);
}