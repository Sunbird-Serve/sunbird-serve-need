package com.sunbird.serve.need.models.Need;

import com.sunbird.serve.need.models.enums.EntityStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "entity")
public class NeedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String agencyId;
    private String name;
    private String registrationId;
    private String website;
    private String mobile;
    @Column(name = "address_line1")
    private String addressLine1;
    private String district;
    private String state;
    private String pincode;
    private String category;

    @Enumerated(EnumType.STRING)
    private EntityStatus status;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
