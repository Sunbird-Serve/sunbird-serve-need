package com.sunbird.serve.need;

import com.sunbird.serve.need.models.Need.NeedDeliverable;
import com.sunbird.serve.need.models.Need.InputParameters;
import com.sunbird.serve.need.models.Need.OutputParameters;
import com.sunbird.serve.need.models.request.NeedDeliverableRequest;
import com.sunbird.serve.need.models.request.DeliverableDetailsRequest;
import com.sunbird.serve.need.models.request.OutputParametersRequest;
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
    private final OutputParametersRepository outputParametersRepository;

    @Autowired
    public NeedDeliverableService(
            NeedDeliverableRepository needDeliverableRepository, 
            InputParametersRepository inputParametersRepository,
            OutputParametersRepository outputParametersRepository) {
        this.needDeliverableRepository = needDeliverableRepository;
        this.inputParametersRepository = inputParametersRepository;
        this.outputParametersRepository = outputParametersRepository;
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

    public List<InputParameters> updateInputParameters(String needDeliverableId, DeliverableDetailsRequest request, Map<String, String> headers) {
        // Check if the need with the given ID exists
        List<InputParameters> existingInputParameters = inputParametersRepository.findByNeedDeliverableId(needDeliverableId);

        if (existingInputParameters != null) {
            for (InputParameters parameter : existingInputParameters) {
                parameter.setNeedDeliverableId(request.getNeedDeliverableId());
                parameter.setInputUrl(request.getInputUrl());
                parameter.setSoftwarePlatform(request.getSoftwarePlatform());
                parameter.setStartTime(request.getStartTime());
                parameter.setEndTime(request.getEndTime());
            }

            
        }
        // Save the updated need
            return inputParametersRepository.saveAll(existingInputParameters);
    }

    public OutputParameters createOutputParameters(OutputParametersRequest outputParametersRequest, Map<String, String> headers) {
        // Convert Output Parameters Request to Output Parameters entity
        OutputParameters outputParameters = DeliverableMapper.mapToOutputParameters(outputParametersRequest);

        // Save the outputParameters
        OutputParameters savedOutputParameters = outputParametersRepository.save(outputParameters);

        // Return the saved Need Deliverable
        return savedOutputParameters;
    }

}
