package com.progressSoft.Bloomberg.mappers;

import com.progressSoft.Bloomberg.dtos.DealDto;
import com.progressSoft.Bloomberg.models.Deal;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class DealMapper {

    public Deal toEntity(DealDto dto) {
        Deal deal = new Deal();
        deal.setFromCurrency(dto.getFromCurrency());
        deal.setToCurrency(dto.getToCurrency());
        deal.setDealAmount(dto.getDealAmount());

        deal.setDealUniqueId(UUID.randomUUID().toString());
        deal.setDealTimestamp(LocalDateTime.now());

        return deal;
    }

    public DealDto toDTO(Deal deal) {
        DealDto dto = new DealDto();
        dto.setDealUniqueId(deal.getDealUniqueId());
        dto.setDealTimestamp(deal.getDealTimestamp());
        dto.setFromCurrency(deal.getFromCurrency());
        dto.setToCurrency(deal.getToCurrency());
        dto.setDealAmount(deal.getDealAmount());
        return dto;
    }
}
