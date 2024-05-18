package com.sunbird.serve.need.models.response;

import com.sunbird.serve.need.models.Need.DeliverableDetails;
import com.sunbird.serve.need.models.Need.InputParameters;
import com.sunbird.serve.need.models.Need.TimeSlot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliverableDetailsResponse {
    private List<DeliverableDetails> deliverableDetails;
    private List<InputParameters> inputParameters;
}
