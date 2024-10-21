package com.sunbird.serve.need.models.Telemetry;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "telemetry_data")
public class TelemetryData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "eid", nullable = false)
    private String eid;

    @Column(name = "ets", nullable = false)
    private Long ets;

    @Column(name = "ver", nullable = false)
    private String ver;

    @Column(name = "mid", nullable = false)
    private String mid;

    @Column(name = "actor_id", nullable = false)
    private String actorId;

    @Column(name = "actor_type", nullable = false)
    private String actorType;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
