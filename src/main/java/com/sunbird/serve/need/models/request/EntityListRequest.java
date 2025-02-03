package com.sunbird.serve.need.models.request;

import com.sunbird.serve.need.models.enums.SoftwarePlatform;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

import java.time.Instant;
import java.util.List;



@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntityListRequest {
    private List<String> entityIds;

    // Getters and setters
    public List<String> getEntityIds() {
        return entityIds;
    }

    public void setEntityIds(List<String> entityIds) {
        this.entityIds = entityIds;
    }
}
