package com.sunbird.serve.need.models.Need;

import com.sunbird.serve.need.models.enums.EntityStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigInteger;
import java.time.Instant;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@jakarta.persistence.Entity
public class Entity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String website;
    private String name;
    private Long mobile;
    private String address_line1;
    private String district;
    private String state;
    private Integer pincode;
    private String category;

    @Enumerated(EnumType.STRING)
    private EntityStatus status;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

}