package com.example.homework.purchase;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FinancialProfileService {

    private static final Map<String, Integer> FINANCIAL_PROFILE = Map.of(
            "12345678901", -1,   //  Ineligible customer
            "99999999999", 8,    // Low capacity, cannot approve high requests
            "12345678923", 10,   // Low capacity, cannot approve high requests
            "12345678999", 40,   //  Lower than requested, but still approvable
            "12345678912", 50,   // Profile with moderate capacity
            "12345678956", 378,  // Strong profile, large approvals
            "12345678934", 500,  //  Strong profile, large approvals
            "12345678945", 100 //  Can approve higher than requested amounts
    );

    public Integer getFinancialCapacityFactor(String personalId) {
        Integer financialCapacityFactor = FINANCIAL_PROFILE.get(personalId);
        if (financialCapacityFactor == null) {
            throw new IllegalArgumentException("Financial profile not found for personal ID: " + personalId);
        }
        return financialCapacityFactor;
    }
}
