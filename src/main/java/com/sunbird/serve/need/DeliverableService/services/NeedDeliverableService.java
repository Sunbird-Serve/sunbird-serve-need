package com.sunbird.serve.need;

import com.sunbird.serve.need.models.Need.NeedDeliverable;
import com.sunbird.serve.need.models.Need.InputParameters;
import com.sunbird.serve.need.models.Need.OutputParameters;
import com.sunbird.serve.need.models.Need.Need;
import com.sunbird.serve.need.models.Need.NeedPlan;
import com.sunbird.serve.need.models.enums.NeedStatus;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class NeedDeliverableService {

    private final NeedDeliverableRepository needDeliverableRepository;
    private final InputParametersRepository inputParametersRepository;
    private final OutputParametersRepository outputParametersRepository;
    private final NeedRepository needRepository;
    private final NeedPlanRepository needPlanRepository;
    private static final Logger logger = LoggerFactory.getLogger(NeedDeliverableService.class);

    @Autowired
    public NeedDeliverableService(
            NeedDeliverableRepository needDeliverableRepository, 
            InputParametersRepository inputParametersRepository,
            OutputParametersRepository outputParametersRepository, 
            NeedRepository needRepository,
            NeedPlanRepository needPlanRepository) {
        this.needDeliverableRepository = needDeliverableRepository;
        this.inputParametersRepository = inputParametersRepository;
        this.outputParametersRepository = outputParametersRepository;
        this.needRepository = needRepository;
        this.needPlanRepository = needPlanRepository;
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

        // Save the updated need deliverable
        NeedDeliverable updatedNeedDeliverable = needDeliverableRepository.save(existingNeedDeliverable);

        // Fetch all need deliverables for the given need plan ID
        List<NeedDeliverable> needDeliverables = needDeliverableRepository.findByNeedPlanId(request.getNeedPlanId());

        // Check if any of the need deliverable statuses is "Not Started"
        boolean allNotStarted = needDeliverables.stream().anyMatch(nd -> nd.getStatus().toString().trim().equalsIgnoreCase("NotStarted"));
        
        // If none of the statuses are "Not Started", update the need status as "Fulfilled"
        if (!allNotStarted) {
          
            NeedPlan needPlan = needPlanRepository.findById(UUID.fromString(request.getNeedPlanId()))
                .orElseThrow(() -> new NoSuchElementException("Need Plan not found with ID: " + request.getNeedPlanId()));
        
            // Fetch the Need using the NeedId from the NeedPlan
            Need need = needRepository.findById(UUID.fromString(needPlan.getNeedId()))
                .orElseThrow(() -> new NoSuchElementException("Need not found with ID: " + needPlan.getNeedId()));
        
            // Update the Need status to "Fulfilled"
            need.setStatus(NeedStatus.Fulfilled);
            needRepository.save(need);
        }

    return updatedNeedDeliverable;
    }


   public List<NeedDeliverable> updateNeedDeliverables(String needPlanId, DeliverableDetailsRequest request, Map<String, String> headers) {
    // Fetch all deliverables based on needPlanId
    List<NeedDeliverable> needDeliverableList = needDeliverableRepository.findByNeedPlanId(needPlanId);

        List<NeedDeliverable> updatedDeliverables = new ArrayList<>();

        for (NeedDeliverable needDeliverables : needDeliverableList) {
            // new request object with details from the need deliverable
            DeliverableDetailsRequest newRequest = DeliverableDetailsRequest.builder()
                    .inputUrl(request.getInputUrl())
                    .softwarePlatform(request.getSoftwarePlatform())
                    .startTime(request.getStartTime())
                    .endTime(request.getEndTime())
                    .build();

            // Call updateInputParameters method for each deliverable
            List<InputParameters> updatedInputParameters = updateInputParameters(needDeliverables.getId().toString(), newRequest, headers);

            // Assuming updateInputParameters updates the needDeliverable and saves it
            updatedDeliverables.add(needDeliverables);
        }
        return updatedDeliverables;
}



    public List<InputParameters> updateInputParameters(String needDeliverableId, DeliverableDetailsRequest request, Map<String, String> headers) {
        // Check if the need with the given ID exists
        List<InputParameters> existingInputParameters = inputParametersRepository.findByNeedDeliverableId(needDeliverableId);

        if (existingInputParameters != null) {
            for (InputParameters parameter : existingInputParameters) {
                parameter.setNeedDeliverableId(needDeliverableId);
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
