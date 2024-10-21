package com.sunbird.serve.need.models.Telemetry;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;
import javax.persistence.*;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "telemetry_event")
public class TelemetryEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    private String type;
    private String subtype;
    private String mode;
    private String pageid;
    private String uri;
    private int visits;

    @Column(name = "telemetry_id", nullable = false)
    private Long telemetryId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    // Getters and Setters
}
