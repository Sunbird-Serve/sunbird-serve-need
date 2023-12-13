package com.sunbird.serve.need;

import com.sunbird.serve.need.models.request.NeedPlanRequest;
import com.sunbird.serve.need.models.Need.NeedPlan;
import com.sunbird.serve.need.models.request.NeedDeliverableRequest;
import com.sunbird.serve.need.models.Need.NeedDeliverable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

public class DeliverableMapper {


    public static NeedPlan mapToEntity(NeedPlanRequest needPlanRequest) {
        // Need request is mapped to the need entity
        NeedPlan needPlan = new NeedPlan();
        needPlan.setAssignedUserId(needPlanRequest.getAssignedUserId());
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

}

