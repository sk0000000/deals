package com.progressSoft.Bloomberg.exceptions;

public class DealNotFoundException extends RuntimeException {
    public DealNotFoundException(String dealId) {
        super("Deal with ID " + dealId + " not found.");
    }
}
