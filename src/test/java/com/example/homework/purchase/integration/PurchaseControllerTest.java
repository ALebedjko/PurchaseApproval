package com.example.homework.purchase.integration;

import com.example.homework.PurchaseApprovalApplication;
import com.example.homework.purchase.ApprovalDecision;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(classes = PurchaseApprovalApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PurchaseControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/purchase/apply";
    }

    /**
     * Case 1: Customer requests an amount that aligns with system calculations.
     * - Requested: **€4500**
     * - Approved: **€4500** (since it matches system-calculated approval)
     */
    @Test
    void shouldApproveRequestedAmountIfValid() {
        Map<String, Object> request = Map.of(
                "personalId", "12345678956",  // Use a new profile with factor = 375
                "requestedAmount", BigDecimal.valueOf(4500),  // ✅ Updated to 4500
                "paymentPeriodMonths", 12
        );

        ResponseEntity<ApprovalDecision> response = restTemplate.postForEntity(getBaseUrl(), request, ApprovalDecision.class);

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isApproved());
        assertEquals(BigDecimal.valueOf(4500), response.getBody().getApprovedAmount());  // ✅ Should exactly match
    }


    /**
     * Case 2: System approves a **higher** amount than requested.
     * - Requested: **€500**
     * - Approved: **€1100** (customer has a strong financial profile)
     */
    @Test
    void shouldApproveHigherAmountIfEligible() {
        Map<String, Object> request = Map.of(
                "personalId", "12345678945",
                "requestedAmount", BigDecimal.valueOf(500),
                "paymentPeriodMonths", 12
        );

        ResponseEntity<ApprovalDecision> response = restTemplate.postForEntity(getBaseUrl(), request, ApprovalDecision.class);

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isApproved());
        assertEquals(BigDecimal.valueOf(1100), response.getBody().getApprovedAmount());
    }

    /**
     * Case 3: System approves a **lower** amount when requested amount is too high.
     * - Requested: **€1000**
     * - Approved: **€900** (system adjusted to an amount within approval limits)
     */
    @Test
    void shouldApproveLowerAmountIfRequestedIsTooHigh() {
        Map<String, Object> request = Map.of(
                "personalId", "12345678999",
                "requestedAmount", BigDecimal.valueOf(1000),
                "paymentPeriodMonths", 12
        );

        ResponseEntity<ApprovalDecision> response = restTemplate.postForEntity(getBaseUrl(), request, ApprovalDecision.class);

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isApproved());
        assertEquals(BigDecimal.valueOf(900), response.getBody().getApprovedAmount());
    }

    /**
     *  Case 4: Customer is **ineligible** (flagged in the system).
     * - Requested: **€3000**
     * - Approved: **€0** (system denies approval)
     */
    @Test
    void shouldRejectIneligibleCustomer() {
        Map<String, Object> request = Map.of(
                "personalId", "12345678901",
                "requestedAmount", BigDecimal.valueOf(3000),
                "paymentPeriodMonths", 12
        );

        ResponseEntity<ApprovalDecision> response = restTemplate.postForEntity(getBaseUrl(), request, ApprovalDecision.class);

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isApproved());
        assertEquals(BigDecimal.ZERO, response.getBody().getApprovedAmount());
    }

    /**
     *  Case 5: Customer requests **too high an amount**, which cannot be approved.
     * - Requested: **€5000** (beyond approval capabilities)
     * - Approved: **€0** (system denies request)
     */
    @Test
    void shouldRejectIfRequestedAmountIsTooHigh() {
        Map<String, Object> request = Map.of(
                "personalId", "99999999999",  // ✅ Use a customer with an extremely low financial factor
                "requestedAmount", BigDecimal.valueOf(5000),
                "paymentPeriodMonths", 24  // ✅ Use the max period to ensure no approval is possible
        );

        ResponseEntity<ApprovalDecision> response = restTemplate.postForEntity(getBaseUrl(), request, ApprovalDecision.class);

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isApproved());  // ✅ Should reject
        assertEquals(BigDecimal.ZERO, response.getBody().getApprovedAmount());  // ✅ Should return ZERO
    }

    /**
     *  Case 6: System **caps the approved amount** at the max limit (€5000).
     * - Requested: **€6000**
     * - Approved: **€5000** (system enforces limit)
     */
    @Test
    void shouldCapApprovedAmountAtMaxLimit() {
        Map<String, Object> request = Map.of(
                "personalId", "12345678934",
                "requestedAmount", BigDecimal.valueOf(6000),
                "paymentPeriodMonths", 12
        );

        ResponseEntity<ApprovalDecision> response = restTemplate.postForEntity(getBaseUrl(), request, ApprovalDecision.class);

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isApproved());
        assertEquals(BigDecimal.valueOf(5000), response.getBody().getApprovedAmount());
    }

    /**
     *  Case 7: Customer requests **below minimum amount**, should be rejected.
     * - Requested: **€100** (system minimum is **€200**)
     * - Approved: **€0** (system rejects)
     */
    @Test
    void shouldRejectIfRequestedAmountIsBelowMinimum() {
        Map<String, Object> request = Map.of(
                "personalId", "12345678934",
                "requestedAmount", BigDecimal.valueOf(100),
                "paymentPeriodMonths", 12
        );

        ResponseEntity<ApprovalDecision> response = restTemplate.postForEntity(getBaseUrl(), request, ApprovalDecision.class);

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isApproved());
        assertEquals(BigDecimal.ZERO, response.getBody().getApprovedAmount());
    }
}
