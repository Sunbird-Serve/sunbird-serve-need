package com.sunbird.serve.need.models.Need;

import com.sunbird.serve.need.models.enums.EntityStatus;
import com.sunbird.serve.need.models.enums.UserRole;
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
public class EntityMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID entityId;
    private String userId;

    @Enumerated(EnumType.STRING) 
    private UserRole userRole;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

}