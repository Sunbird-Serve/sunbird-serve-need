package com.sunbird.serve.need.models.Need;

import com.sunbird.serve.need.models.enums.NeedStatus;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
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
public class Need {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String needTypeId;
    private String description;
    private String needPurpose;
    private String entityId;
    private String userId;
    private String requirementId;
    private String name;

    @Enumerated(EnumType.STRING)
    private NeedStatus status;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
