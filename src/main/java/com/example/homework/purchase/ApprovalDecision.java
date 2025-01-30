package com.example.homework.purchase;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ApprovalDecision {
    private final DecisionStatus status;
    private final BigDecimal approvedAmount;

    private enum DecisionStatus {
        APPROVED, DENIED
    }

    private ApprovalDecision(DecisionStatus status, BigDecimal approvedAmount) {
        this.status = status;
        this.approvedAmount = approvedAmount;
    }

    public static ApprovalDecision approve(BigDecimal approvedAmount) {
        return new ApprovalDecision(DecisionStatus.APPROVED, approvedAmount);
    }

    public static ApprovalDecision reject() {
        return new ApprovalDecision(DecisionStatus.DENIED, BigDecimal.ZERO);
    }

    public boolean isApproved() {
        return status == DecisionStatus.APPROVED;
    }
}
