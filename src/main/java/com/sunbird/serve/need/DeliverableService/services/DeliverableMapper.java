package com.sunbird.serve.need;

import com.sunbird.serve.need.models.request.NeedPlanRequest;
import com.sunbird.serve.need.models.Need.NeedPlan;
import com.sunbird.serve.need.models.request.NeedDeliverableRequest;
import com.sunbird.serve.need.models.request.OutputParametersRequest;
import com.sunbird.serve.need.models.Need.NeedDeliverable;
import com.sunbird.serve.need.models.Need.OutputParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeliverableMapper {

    private static final Logger logger = LoggerFactory.getLogger(DeliverableMapper.class);

    public static NeedPlan mapToEntity(NeedPlanRequest needPlanRequest) {
        try {
            if (needPlanRequest == null) {
                throw new IllegalArgumentException("NeedPlanRequest cannot be null");
            }

            // Need request is mapped to the need entity
            NeedPlan needPlan = new NeedPlan();
            needPlan.setNeedId(needPlanRequest.getNeedId());
            needPlan.setName(needPlanRequest.getName());
            needPlan.setStatus(needPlanRequest.getStatus());
            needPlan.setOccurrenceId(needPlanRequest.getOccurrenceId());

            return needPlan;
        } catch (Exception e) {
            logger.error("Error mapping NeedPlanRequest to NeedPlan: " + needPlanRequest, e);
            throw new RuntimeException("Error mapping NeedPlanRequest to NeedPlan", e);
        }
    }

    public static NeedDeliverable mapToDeliverable(NeedDeliverableRequest needDeliverableRequest) {
        try {
            if (needDeliverableRequest == null) {
                throw new IllegalArgumentException("NeedDeliverableRequest cannot be null");
            }

            // Need request is mapped to the need entity
            NeedDeliverable needDeliverable = new NeedDeliverable();
            needDeliverable.setNeedPlanId(needDeliverableRequest.getNeedPlanId());
            needDeliverable.setComments(needDeliverableRequest.getComments());
            needDeliverable.setStatus(needDeliverableRequest.getStatus());
            needDeliverable.setDeliverableDate(needDeliverableRequest.getDeliverableDate());

            return needDeliverable;
        } catch (Exception e) {
            logger.error("Error mapping NeedDeliverableRequest to NeedDeliverable: " + needDeliverableRequest, e);
            throw new RuntimeException("Error mapping NeedDeliverableRequest to NeedDeliverable", e);
        }
    }

    public static OutputParameters mapToOutputParameters(OutputParametersRequest outputParametersRequest) {
        try {
            if (outputParametersRequest == null) {
                throw new IllegalArgumentException("OutputParametersRequest cannot be null");
            }

            // Need request is mapped to the need entity
            OutputParameters outputParameters = new OutputParameters();
            outputParameters.setNeedDeliverableId(outputParametersRequest.getNeedDeliverableId());
            outputParameters.setRemarks(outputParametersRequest.getRemarks());
            outputParameters.setNumberOfAttendees(outputParametersRequest.getNumberOfAttendees());
            outputParameters.setSubmittedUrl(outputParametersRequest.getSubmittedUrl());

            return outputParameters;
        } catch (Exception e) {
            logger.error("Error mapping OutputParametersRequest to OutputParameters: " + outputParametersRequest, e);
            throw new RuntimeException("Error mapping OutputParametersRequest to OutputParameters", e);
        }
    }
}
