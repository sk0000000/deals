package com.progressSoft.Bloomberg.services;

import com.progressSoft.Bloomberg.dtos.DealDto;
import com.progressSoft.Bloomberg.exceptions.DealNotFoundException;
import com.progressSoft.Bloomberg.exceptions.InvalidDealFieldException;
import com.progressSoft.Bloomberg.mappers.DealMapper;
import com.progressSoft.Bloomberg.models.Deal;
import com.progressSoft.Bloomberg.repositories.DealRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DealServiceTest {

    private DealRepository dealRepository;
    private DealMapper dealMapper;
    private DealService dealService;

    @BeforeEach
    void setUp() {
        dealRepository = mock(DealRepository.class);
        dealMapper = mock(DealMapper.class);
        dealService = new DealService(dealRepository, dealMapper);
    }

    @Test
    void createDeal_validDeal_success() throws InvalidDealFieldException {
        DealDto dto = new DealDto();
        dto.setDealUniqueId("D100");
        dto.setFromCurrency("USD");
        dto.setToCurrency("EUR");
        dto.setDealTimestamp(LocalDateTime.now());
        dto.setDealAmount(BigDecimal.valueOf(1000));

        Deal dealEntity = new Deal();
        dealEntity.setDealUniqueId(dto.getDealUniqueId());
        dealEntity.setFromCurrency(dto.getFromCurrency());
        dealEntity.setToCurrency(dto.getToCurrency());
        dealEntity.setDealAmount(dto.getDealAmount());
        dealEntity.setDealTimestamp(dto.getDealTimestamp());

        when(dealMapper.toEntity(dto)).thenReturn(dealEntity);
        when(dealMapper.toDTO(dealEntity)).thenReturn(dto);
        when(dealRepository.existsById(dto.getDealUniqueId())).thenReturn(false);
        when(dealRepository.save(dealEntity)).thenReturn(dealEntity);

        DealDto saved = dealService.createDeal(dto);
        assertEquals("D100", saved.getDealUniqueId());
        verify(dealRepository, times(1)).save(dealEntity);
    }

    @Test
    void getDealById_found() {
        Deal deal = new Deal();
        deal.setDealUniqueId("D102");

        when(dealRepository.findById("D102")).thenReturn(Optional.of(deal));
        when(dealMapper.toDTO(deal)).thenReturn(new DealDto());

        DealDto result = dealService.getDealById("D102");
        assertNotNull(result);
    }

    @Test
    void getDealById_notFound() {
        when(dealRepository.findById("D999")).thenReturn(Optional.empty());
        assertThrows(DealNotFoundException.class, () -> dealService.getDealById("D999"));
    }

    @Test
    void importDeals_successAndFailed() {
        DealDto dto1 = new DealDto();
        dto1.setDealUniqueId("D1");
        dto1.setFromCurrency("USD");
        dto1.setToCurrency("EUR");
        dto1.setDealAmount(BigDecimal.valueOf(1000));
        dto1.setDealTimestamp(LocalDateTime.now());

        DealDto dto2 = new DealDto();
        dto2.setDealUniqueId("D2");
        dto2.setFromCurrency("US"); // invalid
        dto2.setToCurrency("EUR");
        dto2.setDealAmount(BigDecimal.valueOf(500));
        dto2.setDealTimestamp(LocalDateTime.now());

        Deal d1 = new Deal();
        d1.setDealUniqueId("D1");
        d1.setFromCurrency("USD");
        d1.setToCurrency("EUR");
        d1.setDealAmount(BigDecimal.valueOf(1000));
        d1.setDealTimestamp(dto1.getDealTimestamp());

        when(dealMapper.toEntity(dto1)).thenReturn(d1);
        when(dealMapper.toEntity(dto2)).thenReturn(new Deal());
        when(dealMapper.toDTO(d1)).thenReturn(dto1);

        when(dealRepository.existsById("D1")).thenReturn(false);
        when(dealRepository.existsById("D2")).thenReturn(false);
        when(dealRepository.save(d1)).thenReturn(d1);

        DealImportResult result = dealService.importDeals(List.of(dto1, dto2));

        assertEquals(1, result.getSuccessful().size());
        assertEquals("D1", result.getSuccessful().get(0).getDealUniqueId());
        assertEquals(1, result.getFailedIds().size());
        assertEquals("D2", result.getFailedIds().get(0));
    }
}
