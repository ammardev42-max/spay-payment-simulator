package com.ammarbhatkar.SPay.bank.mapper;

import com.ammarbhatkar.SPay.bank.dto.response.BankAccountResponse;
import com.ammarbhatkar.SPay.bank.entity.BankAccount;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BankAccountMapper {

    BankAccountResponse toResponse(BankAccount bankAccount);

    List<BankAccountResponse> toResponseList(List<BankAccount> bankAccounts);
}