package com.sunbird.serve.need;

import com.sunbird.serve.need.models.Need.NeedDeliverable;
import com.sunbird.serve.need.models.Need.DeliverableDetails;
import com.sunbird.serve.need.models.Need.NeedPlan;
import com.sunbird.serve.need.models.request.NeedDeliverableRequest;
import com.sunbird.serve.need.models.request.DeliverableDetailsRequest;
import com.sunbird.serve.need.models.response.DeliverableDetailsResponse;
import com.sunbird.serve.need.models.Need.InputParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;


@Service
public class DeliverableDetailsService {

    private final DeliverableDetailsRepository deliverableDetailsRepository;
    private final NeedDeliverableRepository needDeliverableRepository;
    private final NeedPlanRepository needPlanRepository;
    private final InputParametersRepository inputParametersRepository;

    @Autowired
    public DeliverableDetailsService(
            DeliverableDetailsRepository deliverableDetailsRepository,
            NeedDeliverableRepository needDeliverableRepository,
            NeedPlanRepository needPlanRepository, 
            InputParametersRepository inputParametersRepository) {
        this.deliverableDetailsRepository = deliverableDetailsRepository;
        this.needDeliverableRepository = needDeliverableRepository;
        this.needPlanRepository = needPlanRepository;
        this.inputParametersRepository = inputParametersRepository;
    }

    //Fetch need deliverable based on needPlanId
    public List<DeliverableDetailsResponse> getNDByNeedId(String needId) {
        //NeedPlan needPlans = needPlanRepository.findByNeedId(needId);
        //UUID needPlanId = needPlans.getId();
        //NeedDeliverable needDeliverable = needDeliverableRepository.findByNeedPlanId(needPlanId);
        //UUID needDeliverableId = needDeliverable.getId();
        List<NeedPlan> needPlans = needPlanRepository.findByNeedId(needId);
        List<DeliverableDetails> deliverableDetailsList = new ArrayList<>();
        List<DeliverableDetailsResponse> result = new ArrayList<>();

        for (NeedPlan needPlan : needPlans) {
            String needPlanId = needPlan.getId().toString();
            List<NeedDeliverable> needDeliverable = needDeliverableRepository.findByNeedPlanId(needPlanId);
            for (NeedDeliverable deliverable : needDeliverable) {
                String needDeliverableId = deliverable.getId().toString();
                deliverableDetailsList.addAll(deliverableDetailsRepository.findByNeedDeliverableId(needDeliverableId));
                List<InputParameters> inputParametersList = inputParametersRepository.findByDeliverableDetailsId(deliverableDetailsList.get(0).getId().toString());
                // Create a response object with both lists
                DeliverableDetailsResponse response = new DeliverableDetailsResponse(deliverableDetailsList, inputParametersList);
                result.add(response);
            }
        }

        return result;
    }

public InputParameters updateDeliverableDetails(String needId, DeliverableDetailsRequest request, Map<String, String> headers) {

        List<NeedPlan> needPlan = needPlanRepository.findByNeedId(needId);
        List<NeedDeliverable> needDeliverable = needDeliverableRepository.findByNeedPlanId(needPlan.get(0).getId().toString());
        // Check if the need with the given ID exists
        List<DeliverableDetails> existingDeliverableDetails = deliverableDetailsRepository.findByNeedDeliverableId(needDeliverable.get(0).getId().toString());

        List<InputParameters> existingInputParametersList = inputParametersRepository.findByDeliverableDetailsId(existingDeliverableDetails.get(0).getId().toString());

        InputParameters existingInputParameters = existingInputParametersList.get(0);
        // Update the existing need with new values
        existingInputParameters.setInputUrl(request.getInputUrl());
        existingInputParameters.setSoftwarePlatform(request.getSoftwarePlatform());
        existingInputParameters.setDeliverableDate(request.getDeliverableDate());
        existingInputParameters.setStartTime(request.getStartTime());
        existingInputParameters.setEndTime(request.getEndTime());
        
        // Save the updated need
        return inputParametersRepository.save(existingInputParameters);
    }

}
