package com.sunbird.serve.need.models.Need;

import com.sunbird.serve.need.models.enums.OnboardRequestStatus;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "entity_onboard")
public class EntityOnboard {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String agencyId;

    @Column(name = "entity_id")
    private UUID entityId;

    private String coordinatorName;
    private String mobile;
    private String email;
    private String designation;

    @Type(JsonType.class)
    @Column(name = "infra_details", columnDefinition = "jsonb")
    private Map<String, Object> infraDetails;

    @Enumerated(EnumType.STRING)
    private OnboardRequestStatus status;

    @Column(columnDefinition = "TEXT")
    private String reviewerNotes;

    private String reviewedBy;
    private Instant reviewedAt;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
