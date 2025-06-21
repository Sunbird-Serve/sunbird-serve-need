package com.sunbird.serve.need.models.Need;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.sunbird.serve.need.models.enums.SoftwarePlatform;

import java.time.Instant;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class InputParameters {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String needDeliverableId;
    private String inputUrl;
    
    @Enumerated(EnumType.STRING)
    private SoftwarePlatform softwarePlatform;
    
    private Instant startTime;
    private Instant endTime;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
