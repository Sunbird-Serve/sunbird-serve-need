package com.sunbird.serve.need.models.request;

import com.sunbird.serve.need.models.enums.NeedStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NeedPlanRequest {
   private String needId;
   private String name;
   private NeedStatus status;
   private String occurrenceId;
}