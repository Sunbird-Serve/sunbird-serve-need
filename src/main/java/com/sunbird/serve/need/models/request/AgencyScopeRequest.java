package com.sunbird.serve.need.models.request;

import com.sunbird.serve.need.models.enums.VisibilityScope;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgencyScopeRequest {

    private VisibilityScope needVisibility;
    private List<String> selectedNeedAgencies;
    private VisibilityScope volunteerVisibility;
    private List<String> selectedVolunteerAgencies;
}
