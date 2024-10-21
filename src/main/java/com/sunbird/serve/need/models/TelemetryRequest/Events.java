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


public class Events {
    private String eid;
    private long ets;
    private String ver;
    private String mid;
    private Actor actor;
    private Context context;
    private EData edata;
    private Object object; 
    private List<String> tags;

    // Getters and Setters
}


