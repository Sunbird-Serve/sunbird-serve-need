package com.sunbird.serve.need.models.request;

import com.sunbird.serve.need.models.enums.NeedDeliverableStatus;
import com.sunbird.serve.need.models.dto.InputParametersDTO;
import com.sunbird.serve.need.models.dto.OutputParametersDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NeedDeliverableRequest {
   private String needPlanId;
   private String comments;
   private NeedDeliverableStatus status;
   private LocalDate deliverableDate;
   private InputParametersDTO inputParameters;
   private OutputParametersDTO outputParameters;
}