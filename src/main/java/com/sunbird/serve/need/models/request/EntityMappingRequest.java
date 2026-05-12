package com.sunbird.serve.need.models.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntityMappingRequest {

    private String agencyId;
    private UUID entityId;
    private String userId;
    private String userRole;
}
