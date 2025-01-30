package com.example.homework.purchase;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping("/api/purchase")
@RequiredArgsConstructor
public class PurchaseController {
    private final PurchaseService purchaseService;

    @PostMapping("/apply")
    public ResponseEntity<ApprovalDecision> applyForPurchase(@RequestBody PurchaseApplicationRequest request) {
        ApprovalDecision approvalDecision = purchaseService.findMaxApprovedAmount(
                request.personalId(),
                request.requestedAmount(),
                request.paymentPeriodMonths()
        );
        return ok(approvalDecision);
    }
}
