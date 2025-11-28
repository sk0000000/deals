package com.progressSoft.Bloomberg.services;

import com.progressSoft.Bloomberg.dtos.DealDto;

import java.util.List;

public class DealImportResult {
    private List<DealDto> successful;
    private List<String> failedIds;

    public DealImportResult(List<DealDto> successful, List<String> failedIds) {
        this.successful = successful;
        this.failedIds = failedIds;
    }

    public List<DealDto> getSuccessful() {
        return successful;
    }

    public void setSuccessful(List<DealDto> successful) {
        this.successful = successful;
    }

    public List<String> getFailedIds() {
        return failedIds;
    }

    public void setFailedIds(List<String> failedIds) {
        this.failedIds = failedIds;
    }
}
