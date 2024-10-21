package com.sunbird.serve.need.models.Telemetry;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "telemetry_context")
public class TelemetryContext {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String channel;
    private String env;
    private String sid;
    private String did;
    private String pid;
    private String version;
    private String platform;
    @Column(name = "telemetry_id", nullable = false)
    private Long telemetryId;

   @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
