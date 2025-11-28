package com.progressSoft.Bloomberg.services;

import com.progressSoft.Bloomberg.dtos.DealDto;
import com.progressSoft.Bloomberg.exceptions.DealNotFoundException;
import com.progressSoft.Bloomberg.exceptions.InvalidDealFieldException;
import com.progressSoft.Bloomberg.mappers.DealMapper;
import com.progressSoft.Bloomberg.models.Deal;
import com.progressSoft.Bloomberg.repositories.DealRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DealService {

    private final DealRepository dealRepository;
    private final DealMapper dealMapper;

    public DealService(DealRepository dealRepository, DealMapper dealMapper) {
        this.dealRepository = dealRepository;
        this.dealMapper = dealMapper;
    }

    public DealDto createDeal(DealDto dto) throws InvalidDealFieldException {
        Deal deal = dealMapper.toEntity(dto);

        if (deal.getDealUniqueId() == null || deal.getDealUniqueId().trim().isEmpty()) {
            throw new InvalidDealFieldException("dealUniqueId cannot be null or empty");
        }

        if (deal.getFromCurrency() == null || deal.getFromCurrency().length() != 3) {
            throw new InvalidDealFieldException("fromCurrency must be exactly 3 letters");
        }

        if (deal.getToCurrency() == null || deal.getToCurrency().length() != 3) {
            throw new InvalidDealFieldException("toCurrency must be exactly 3 letters");
        }

        if (deal.getDealAmount() == null || deal.getDealAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new InvalidDealFieldException("dealAmount must be > 0");
        }

        if (dealRepository.existsById(deal.getDealUniqueId())) {
            throw new InvalidDealFieldException("Deal with ID " + deal.getDealUniqueId() + " already exists.");
        }

        Deal saved = dealRepository.save(deal);
        return dealMapper.toDTO(saved);
    }

    public List<DealDto> getAllDeals() {
        List<Deal> deals = dealRepository.findAll();
        List<DealDto> dtos = new ArrayList<>();
        for (Deal d : deals) {
            dtos.add(dealMapper.toDTO(d));
        }
        return dtos;
    }

    public DealDto getDealById(String id) {
        Deal deal = dealRepository.findById(id)
                .orElseThrow(() -> new DealNotFoundException(id));
        return dealMapper.toDTO(deal);
    }

    public DealDto updateDeal(String id, DealDto dto) {
        Deal updatedDeal = dealMapper.toEntity(dto);
        Deal deal = dealRepository.findById(id)
                .map(d -> {
                    d.setFromCurrency(updatedDeal.getFromCurrency());
                    d.setToCurrency(updatedDeal.getToCurrency());
                    d.setDealAmount(updatedDeal.getDealAmount());
                    return dealRepository.save(d);
                })
                .orElseThrow(() -> new DealNotFoundException(id));
        return dealMapper.toDTO(deal);
    }

    public void deleteDeal(String id) {
        if (!dealRepository.existsById(id)) {
            throw new DealNotFoundException(id);
        }
        dealRepository.deleteById(id);
    }

    public DealImportResult importDeals(List<DealDto> dtos) {
        List<DealDto> successful = new ArrayList<>();
        List<String> failedIds = new ArrayList<>();

        for (DealDto dto : dtos) {
            try {
                Deal deal = dealMapper.toEntity(dto);
                if (dealRepository.existsById(deal.getDealUniqueId())
                        || deal.getFromCurrency().length() != 3
                        || deal.getToCurrency().length() != 3
                        || deal.getDealAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                    failedIds.add(dto.getDealUniqueId());
                    continue;
                }
                Deal saved = dealRepository.save(deal);
                successful.add(dealMapper.toDTO(saved));
            } catch (Exception e) {
                failedIds.add(dto.getDealUniqueId());
            }
        }

        return new DealImportResult(successful, failedIds);
    }
}
