package com.example.homework.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "purchase")
public class PurchaseProperties {

    private BigDecimal minAmount = BigDecimal.valueOf(200);
    private BigDecimal maxAmount = BigDecimal.valueOf(5000);
    private int minPeriod = 6;
    private int maxPeriod = 24;

}
