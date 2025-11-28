package com.progressSoft.Bloomberg.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.progressSoft.Bloomberg.dtos.DealDto;
import com.progressSoft.Bloomberg.exceptions.DealNotFoundException;
import com.progressSoft.Bloomberg.exceptions.InvalidDealFieldException;
import com.progressSoft.Bloomberg.services.DealService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DealController.class)
@Disabled
class DealControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DealService dealService;

    @Autowired
    private ObjectMapper objectMapper;

    private DealDto dealDto;

    @BeforeEach
    void setUp() {
        dealDto = new DealDto();
        dealDto.setDealUniqueId("D100");
        dealDto.setFromCurrency("USD");
        dealDto.setToCurrency("EUR");
        dealDto.setDealAmount(BigDecimal.valueOf(1000));
        dealDto.setDealTimestamp(LocalDateTime.now());
    }

    @Test
    void createDeal_success() throws Exception {
        Mockito.when(dealService.createDeal(any(DealDto.class))).thenReturn(dealDto);

        mockMvc.perform(post("/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dealDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dealUniqueId").value("D100"))
                .andExpect(jsonPath("$.fromCurrency").value("USD"))
                .andExpect(jsonPath("$.toCurrency").value("EUR"));
    }

    @Test
    void createDeal_duplicate_throwsException() throws Exception {
        Mockito.when(dealService.createDeal(any(DealDto.class)))
                .thenThrow(new InvalidDealFieldException("Deal with ID D100 already exists."));

        mockMvc.perform(post("/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dealDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Deal with ID D100 already exists."));
    }

    @Test
    void getDealById_found() throws Exception {
        Mockito.when(dealService.getDealById("D100")).thenReturn(dealDto);

        mockMvc.perform(get("/deals/D100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dealUniqueId").value("D100"));
    }

    @Test
    void getDealById_notFound() throws Exception {
        Mockito.when(dealService.getDealById("D999"))
                .thenThrow(new DealNotFoundException("Deal with ID D999 not found."));

        mockMvc.perform(get("/deals/D999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Deal with ID D999 not found."));
    }

    @Test
    void importDeals_successAndFailed() throws Exception {
        Mockito.when(dealService.importDeals(any()))
                .thenReturn(new com.progressSoft.Bloomberg.services.DealImportResult(
                        Collections.singletonList(dealDto),
                        Collections.singletonList("D101")
                ));

        mockMvc.perform(post("/deals/import")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Collections.singletonList(dealDto))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successful[0].dealUniqueId").value("D100"))
                .andExpect(jsonPath("$.failedIds[0]").value("D101"));
    }
}
