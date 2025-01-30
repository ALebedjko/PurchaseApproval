package com.example.homework.purchase;

import com.example.homework.config.PurchaseProperties;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.math.BigDecimal.*;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final FinancialProfileService financialProfileService;
    private final PurchaseProperties purchaseProperties;

    private static final BigDecimal REDUCTION_STEP = valueOf(100);
    private static final BigDecimal APPROVAL_THRESHOLD = BigDecimal.ONE;
    private static final BigDecimal APPROVED_AMOUNT_INCREMENT = valueOf(100);
    private static final int INELIGIBLE_CUSTOMER_FACTOR = -1;

    private static final Logger logger = LoggerFactory.getLogger(PurchaseService.class);

    public ApprovalDecision findMaxApprovedAmount(String personalId, BigDecimal requestedAmount, int initialPaymentPeriod) {
        if (requestedAmount.compareTo(purchaseProperties.getMinAmount()) < 0) {
            logFinalDecision(false, ZERO);
            return ApprovalDecision.reject();
        }

        Integer financialCapacityFactor = financialProfileService.getFinancialCapacityFactor(personalId);

        if (isIneligibleCustomer(financialCapacityFactor)) {
            return ApprovalDecision.reject();
        }

        BigDecimal maxApprovedAmount = ZERO;
        int selectedPeriod = initialPaymentPeriod;
        boolean foundApproval = false;

        // Start from requested amount but do not exceed max limit
        BigDecimal currentAmount = requestedAmount.min(purchaseProperties.getMaxAmount());

        logger.info("Starting Approval Process...");
        logApprovalDetails(personalId, requestedAmount, initialPaymentPeriod, financialCapacityFactor);

        // Try increasing the period for the requested amount
        for (int period = initialPaymentPeriod; period <= purchaseProperties.getMaxPeriod(); period++) {
            BigDecimal approvalScore = calculateApprovalScore(financialCapacityFactor, currentAmount, period);

            logApprovalAttempt(currentAmount, period, approvalScore);

            if (approvalScore.compareTo(ONE) >= 0) {
                maxApprovedAmount = currentAmount;
                selectedPeriod = period;
                foundApproval = true;

                logApprovalSuccess(maxApprovedAmount, selectedPeriod);
                break;
            }
        }

        // If no approval was found by increasing the period, start decreasing the amount
        while (!foundApproval && currentAmount.compareTo(purchaseProperties.getMinAmount()) >= 0) {
            for (int period = initialPaymentPeriod; period <= purchaseProperties.getMaxPeriod(); period++) {
                BigDecimal approvalScore = calculateApprovalScore(financialCapacityFactor, currentAmount, period);

                logApprovalAttempt(currentAmount, period, approvalScore);

                if (approvalScore.compareTo(ONE) >= 0) {
                    maxApprovedAmount = currentAmount;
                    selectedPeriod = period;
                    foundApproval = true;
                    logApprovalSuccess(maxApprovedAmount, selectedPeriod);
                    break;
                }
            }

            if (!foundApproval) {
                logReductionAttempt(currentAmount);
                currentAmount = currentAmount.subtract(REDUCTION_STEP);
            }
        }

        // If approval was found, try increasing the amount **ONLY IF the request was lower than possible approval**
        if (foundApproval && maxApprovedAmount.compareTo(requestedAmount) >= 0) {
            maxApprovedAmount = increaseApprovedAmount(financialCapacityFactor, maxApprovedAmount, selectedPeriod);
        }

        // If no valid amount was found, return failure
        if (!foundApproval) {
            logFinalDecision(false, ZERO);
            return ApprovalDecision.reject();
        }

        logFinalDecision(true, maxApprovedAmount);
        return ApprovalDecision.approve(maxApprovedAmount);
    }

    private static boolean isIneligibleCustomer(Integer financialCapacityFactor) {
        return financialCapacityFactor == INELIGIBLE_CUSTOMER_FACTOR;
    }

    private BigDecimal calculateApprovalScore(int financialFactor, BigDecimal amount, int period) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero for approval score calculation.");
        }

        return BigDecimal.valueOf(financialFactor)
                .divide(amount, 10, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(period));
    }

    private void logApprovalDetails(String personalId, BigDecimal requestedAmount, int initialPaymentPeriod, int financialCapacityFactor) {
        logger.info("--------------------------------------------------");
        logger.info("Personal ID: {}", personalId);
        logger.info("Initial Requested Amount: {}", requestedAmount);
        logger.info("Initial Payment Period: {}", initialPaymentPeriod);
        logger.info("Financial Capacity Factor: {}", financialCapacityFactor);
        logger.info("--------------------------------------------------");
    }

    private void logApprovalAttempt(BigDecimal amount, int period, BigDecimal score) {
        logger.debug("Trying Amount: {} | Period: {} | Calculated Approval Score: {}", amount, period, score);
    }

    private void logApprovalSuccess(BigDecimal amount, int period) {
        logger.debug("APPROVED: Amount = {} | Period = {}", amount, period);
    }

    private void logReductionAttempt(BigDecimal amount) {
        logger.debug("NOT APPROVED: Reducing Amount by 100 to {}", amount.subtract(REDUCTION_STEP));
    }

    private void logFinalDecision(boolean approved, BigDecimal amount) {
        if (approved) {
            logger.info("✅ FINAL DECISION: APPROVED {}", amount);
        } else {
            logger.info("❌ FINAL DECISION: REJECTED");
        }
    }

    private BigDecimal increaseApprovedAmount(int financialFactor, BigDecimal approvedAmount, int period) {
        while (approvedAmount.add(APPROVED_AMOUNT_INCREMENT).compareTo(purchaseProperties.getMaxAmount()) <= 0) {
            BigDecimal newAmount = approvedAmount.add(APPROVED_AMOUNT_INCREMENT);
            BigDecimal approvalScore = calculateApprovalScore(financialFactor, newAmount, period);

            logger.debug("Trying to increase amount to {} | Approval Score: {}", newAmount, approvalScore);

            if (approvalScore.compareTo(APPROVAL_THRESHOLD) >= 0) {
                approvedAmount = newAmount;
                logger.debug("Increase successful! New approved amount: {}", approvedAmount);
            } else {
                logger.debug("Increase failed. Cannot approve amount: {}", newAmount);
                break;
            }
        }
        return approvedAmount;
    }
}
