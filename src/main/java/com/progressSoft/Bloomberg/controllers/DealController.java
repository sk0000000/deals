package com.progressSoft.Bloomberg.controllers;

import com.progressSoft.Bloomberg.dtos.DealDto;
import com.progressSoft.Bloomberg.exceptions.InvalidDealFieldException;
import com.progressSoft.Bloomberg.services.DealService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/deals")
public class DealController {

    private final DealService dealService;;

    public DealController(DealService dealService) {
        this.dealService = dealService;
    }

    @PostMapping
    public DealDto createDeal(@RequestBody @Valid DealDto dto) throws InvalidDealFieldException {
        return dealService.createDeal(dto);
    }

    @GetMapping
    public List<DealDto> getAllDeals() {
        return dealService.getAllDeals();
    }

    @GetMapping("/{id}")
    public DealDto getDealById(@PathVariable("id") String id) {
        return dealService.getDealById(id);
    }

    @PutMapping("/{id}")
    public DealDto updateDeal(@PathVariable("id") String id, @RequestBody @Valid DealDto dto) {
        return dealService.updateDeal(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteDeal(@PathVariable("id") String id) {
        dealService.deleteDeal(id);
    }
}
