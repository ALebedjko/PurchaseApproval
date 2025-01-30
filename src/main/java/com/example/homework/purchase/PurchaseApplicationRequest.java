package com.example.homework.purchase;

import java.math.BigDecimal;

public record PurchaseApplicationRequest(String personalId, BigDecimal requestedAmount, int paymentPeriodMonths) {

}