package com.example.homework.customer;

import com.example.homework.common.AuditableEntity;
import com.example.homework.purchase.PurchaseApplication;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class Customer extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String personalId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    @Column
    private String address;

    @OneToMany(mappedBy = "customer", fetch = LAZY, cascade = ALL, orphanRemoval = true)
    private List<PurchaseApplication> purchaseApplications = new ArrayList<>();

    public void addPurchaseApplication(PurchaseApplication newApplication) {
        Optional.ofNullable(newApplication).ifPresent(application -> {
            application.setCustomer(this);
            purchaseApplications.add(application);
        });
    }
}
