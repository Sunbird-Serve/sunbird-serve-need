package com.sunbird.serve.need;

import com.sunbird.serve.need.models.Need.NeedDeliverable;
import com.sunbird.serve.need.models.Need.InputParameters;
import com.sunbird.serve.need.models.request.NeedDeliverableRequest;
import com.sunbird.serve.need.models.response.NeedDeliverableResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.NoSuchElementException;


@Service
public class NeedDeliverableService {

    private final NeedDeliverableRepository needDeliverableRepository;
    private final InputParametersRepository inputParametersRepository;

    @Autowired
    public NeedDeliverableService(
            NeedDeliverableRepository needDeliverableRepository, 
            InputParametersRepository inputParametersRepository) {
        this.needDeliverableRepository = needDeliverableRepository;
        this.inputParametersRepository = inputParametersRepository;
    }

   //Fetch need deliverable based on needPlanId
public NeedDeliverableResponse getByNeedPlanId(String needPlanId) {
    List<NeedDeliverable> needDeliverables = needDeliverableRepository.findByNeedPlanId(needPlanId);
    
    List<InputParameters> inputParameters = new ArrayList<>();
    for (NeedDeliverable needDeliverable : needDeliverables) {
        List<InputParameters> params = inputParametersRepository.findByNeedDeliverableId(needDeliverable.getId().toString());
        inputParameters.addAll(params);
    }

    return NeedDeliverableResponse.builder()
            .needDeliverable(needDeliverables)
            .inputParameters(inputParameters)
            .build();
}

    public NeedDeliverable createNeedDeliverable(NeedDeliverableRequest needDeliverableRequest, Map<String, String> headers) {
        // Convert Need Deliverable Request to Need Deliverable entity
        NeedDeliverable needDeliverable = DeliverableMapper.mapToDeliverable(needDeliverableRequest);

        // Save the Need Deliverable
        NeedDeliverable savedNeedDeliverable = needDeliverableRepository.save(needDeliverable);

        // Return the saved Need Deliverable
        return savedNeedDeliverable;
    }

    public NeedDeliverable updateNeedDeliverable(UUID needDeliverableId, NeedDeliverableRequest request, Map<String, String> headers) {
        // Check if the need with the given ID exists
        NeedDeliverable existingNeedDeliverable = needDeliverableRepository.findById(needDeliverableId)
        .orElseThrow(() -> new NoSuchElementException("Need Delibverable not found with ID: " + needDeliverableId));

        // Update the existing need with new values
        existingNeedDeliverable.setNeedPlanId(request.getNeedPlanId());
        existingNeedDeliverable.setComments(request.getComments());
        existingNeedDeliverable.setStatus(request.getStatus());
        existingNeedDeliverable.setDeliverableDate(request.getDeliverableDate());

        // Save the updated need
        return needDeliverableRepository.save(existingNeedDeliverable);
    }

}
