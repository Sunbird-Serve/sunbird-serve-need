package com.sunbird.serve.need;

import com.sunbird.serve.need.models.request.NeedPlanRequest;
import com.sunbird.serve.need.models.Need.NeedPlan;
import com.sunbird.serve.need.models.request.NeedDeliverableRequest;
import com.sunbird.serve.need.models.request.OutputParametersRequest;
import com.sunbird.serve.need.models.Need.NeedDeliverable;
import com.sunbird.serve.need.models.Need.OutputParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

public class DeliverableMapper {


    public static NeedPlan mapToEntity(NeedPlanRequest needPlanRequest) {
        // Need request is mapped to the need entity
        NeedPlan needPlan = new NeedPlan();
        needPlan.setNeedId(needPlanRequest.getNeedId());
        needPlan.setName(needPlanRequest.getName());
        needPlan.setStatus(needPlanRequest.getStatus());
        needPlan.setOccurrenceId(needPlanRequest.getOccurrenceId());

        return needPlan;
    }

    public static NeedDeliverable mapToDeliverable(NeedDeliverableRequest needDeliverableRequest) {
        // Need request is mapped to the need entity
        NeedDeliverable needDeliverable = new NeedDeliverable();
        needDeliverable.setNeedPlanId(needDeliverableRequest.getNeedPlanId());
        needDeliverable.setComments(needDeliverableRequest.getComments());
        needDeliverable.setStatus(needDeliverableRequest.getStatus());
        needDeliverable.setDeliverableDate(needDeliverableRequest.getDeliverableDate());

        return needDeliverable;
    }

    public static OutputParameters mapToOutputParameters(OutputParametersRequest outputParametersRequest) {
        // Need request is mapped to the need entity
        OutputParameters outputParameters = new OutputParameters();
        outputParameters.setNeedDeliverableId(outputParametersRequest.getNeedDeliverableId());
        outputParameters.setRemarks(outputParametersRequest.getRemarks());
        outputParameters.setNumberOfAttendees(outputParametersRequest.getNumberOfAttendees());
        outputParameters.setSubmittedUrl(outputParametersRequest.getSubmittedUrl());

        return outputParameters;
    }
}

