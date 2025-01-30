package com.example.homework.purchase;

import com.example.homework.config.PurchaseProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class PurchaseServiceTest {

    @Mock
    private FinancialProfileService financialProfileService;

    @Mock
    private PurchaseProperties purchaseProperties;

    @InjectMocks
    private PurchaseService purchaseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(purchaseProperties.getMinAmount()).thenReturn(BigDecimal.valueOf(200));
        when(purchaseProperties.getMaxAmount()).thenReturn(BigDecimal.valueOf(5000));
        when(purchaseProperties.getMinPeriod()).thenReturn(6);
        when(purchaseProperties.getMaxPeriod()).thenReturn(24);
    }

    @Test
    void shouldApproveHigherThanRequestedAmount() {
        // Given: Profile 1, financial factor = 100
        when(financialProfileService.getFinancialCapacityFactor("12345678912")).thenReturn(100);

        // When: Finding the max approval
        ApprovalDecision decision = purchaseService.findMaxApprovedAmount("12345678912", BigDecimal.valueOf(500), 12);

        assertTrue(decision.isApproved());

        // Expect the system to approve **higher than requested**, but within valid limits.
        assertEquals(BigDecimal.valueOf(1100), decision.getApprovedAmount());
    }

    @Test
    void shouldIncreaseApprovedAmountBeyondRequested() {
        // Given: Profile with a higher financial factor (100)
        when(financialProfileService.getFinancialCapacityFactor("12345678945")).thenReturn(100);

        // When: Finding the max approval
        ApprovalDecision decision = purchaseService.findMaxApprovedAmount("12345678945", BigDecimal.valueOf(500), 12);

        assertTrue(decision.isApproved());

        // Expect **€1100**, as the system increases to the maximum possible amount
        assertEquals(BigDecimal.valueOf(1100), decision.getApprovedAmount());
    }

    @Test
    void shouldApproveLowerAmountIfRequestedIsTooHigh() {
        // Given: Even lower financial factor (40) to ensure reduction is necessary
        when(financialProfileService.getFinancialCapacityFactor("12345678999")).thenReturn(40);

        // When: Finding the maximum approval
        ApprovalDecision decision = purchaseService.findMaxApprovedAmount("12345678999", BigDecimal.valueOf(1000), 12);

        // Then: The system **should approve a lower amount**, since the requested is too high.
        assertTrue(decision.isApproved());

        // Expected: The system **should return the highest amount it could approve**.
        assertEquals(BigDecimal.valueOf(900), decision.getApprovedAmount());
    }

    @Test
    void shouldRejectIfCustomerIsIneligible() {
        when(financialProfileService.getFinancialCapacityFactor("12345678901")).thenReturn(-1);

        ApprovalDecision decision = purchaseService.findMaxApprovedAmount("12345678901", BigDecimal.valueOf(3000), 12);

        assertFalse(decision.isApproved());
        assertEquals(BigDecimal.ZERO, decision.getApprovedAmount());
    }

    @Test
    void shouldRejectIfRequestedAmountIsTooHigh() {
        // Given: Very low financial factor (8)
        when(financialProfileService.getFinancialCapacityFactor("12345678923")).thenReturn(8);

        //  When: Finding approval for **€5000**, which is too high
        ApprovalDecision decision = purchaseService.findMaxApprovedAmount("12345678923", BigDecimal.valueOf(5000), 24);

        //  Expect system rejected **too high an amount**, which cannot be approved.
        assertEquals(BigDecimal.ZERO, decision.getApprovedAmount());
        }

    @Test
    void shouldApproveExactlyRequestedAmountIfValid() {
        when(financialProfileService.getFinancialCapacityFactor("12345678934")).thenReturn(378);

        // When: Finding approval for **exactly** requested amount
        ApprovalDecision decision = purchaseService.findMaxApprovedAmount("12345678934", BigDecimal.valueOf(4500), 12);

        // Then: Approval should be exactly as requested (no increase)
        assertTrue(decision.isApproved());
        assertEquals(BigDecimal.valueOf(4500), decision.getApprovedAmount());
    }

    @Test
    void shouldCapApprovedAmountAtMaxLimit() {

        when(financialProfileService.getFinancialCapacityFactor("12345678934")).thenReturn(5000);

        // When: The customer requests an amount exceeding the system's max limit
        ApprovalDecision decision = purchaseService.findMaxApprovedAmount("12345678934", BigDecimal.valueOf(6000), 12);

        // Then: The system should cap the approved amount at the max allowed limit (5000)
        assertTrue(decision.isApproved(), "Approval should be granted within the max limit.");
        assertEquals(BigDecimal.valueOf(5000), decision.getApprovedAmount(), "Approved amount should be capped at system max.");
    }

    @Test
    void shouldRejectIfRequestedAmountIsBelowMinimum() {
        // Given: A customer with a financial capacity factor of 500
        when(financialProfileService.getFinancialCapacityFactor("12345678934")).thenReturn(500);

        // When: The customer requests an amount below the system's minimum threshold (€100 < €200)
        ApprovalDecision decision = purchaseService.findMaxApprovedAmount("12345678934", BigDecimal.valueOf(100), 12);

        // Then: The system should reject the request
        assertFalse(decision.isApproved(), "Approval should be denied for requests below the minimum threshold.");
        assertEquals(BigDecimal.ZERO, decision.getApprovedAmount(), "Approved amount should be €0 since the request is below the minimum.");
    }
}
