package com.example.homework.purchase;

import com.example.homework.common.AuditableEntity;
import com.example.homework.customer.Customer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class PurchaseApplication extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToOne(mappedBy = "purchaseApplication", cascade = CascadeType.ALL, orphanRemoval = true)
    private Purchase purchase;

    @Column(nullable = false)
    private BigDecimal requestedAmount;

    @Column(nullable = false)
    private Integer paymentPeriodMonths;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    public enum ApplicationStatus {
        PENDING,
        APPROVED,
        DENIED
    }
}
