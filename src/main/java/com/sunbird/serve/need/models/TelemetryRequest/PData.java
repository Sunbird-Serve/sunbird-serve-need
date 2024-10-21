package com.sunbird.serve.need.models.TelemetryRequest;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor


public class PData {
    private String pid;
    private String ver;
    private String platform;

    // Getters and Setters
}





