package com.example.homework.common;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.*;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class AuditableEntity {

    private LocalDateTime created;
    private LocalDateTime updated;

    @PrePersist
    public void onCreate() {
        this.created = now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updated = now();
    }

}
