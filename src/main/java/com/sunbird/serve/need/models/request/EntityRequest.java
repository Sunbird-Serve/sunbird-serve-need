package com.sunbird.serve.need.models.request;

import com.sunbird.serve.need.models.enums.EntityStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntityRequest {

    private String website;
    private String name;
    private String registrationId;
    private String mobile;
    private String addressLine1;
    private String district;
    private String state;
    private String pincode;
    private String category;
    private EntityStatus status;
}
