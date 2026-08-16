package com.ammarbhatkar.SPay.upi.mapper;

import com.ammarbhatkar.SPay.upi.dto.response.UpiHandleResponse;
import com.ammarbhatkar.SPay.upi.entity.UpiHandle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UpiHandleMapper {

    @Mapping(target = "bankAccountId", source = "bankAccount.id")
    @Mapping(target = "displayName", source = "user.fullName")
    @Mapping(target = "bankName", source = "bankAccount.bankName")
    @Mapping(target = "maskedAccountNumber", source = "bankAccount.maskedAccountNumber")
    @Mapping(target = "status", expression = "java(upiHandle.getStatus().name())")
    UpiHandleResponse toResponse(UpiHandle upiHandle);

    List<UpiHandleResponse> toResponseList(List<UpiHandle> upiHandles);
}