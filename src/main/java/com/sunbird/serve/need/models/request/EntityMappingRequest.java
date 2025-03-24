package com.sunbird.serve.need.models.request;

import com.sunbird.serve.need.models.enums.EntityStatus;
import com.sunbird.serve.need.models.enums.UserRole;
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

    private UUID entityId;
    private String userId;
    private String userRole;
}