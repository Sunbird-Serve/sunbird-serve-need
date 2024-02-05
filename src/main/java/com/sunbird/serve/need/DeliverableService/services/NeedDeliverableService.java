package com.sunbird.serve.need;

import com.sunbird.serve.need.models.Need.NeedDeliverable;
import com.sunbird.serve.need.models.request.NeedDeliverableRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.Map;
import java.util.List;
import java.util.NoSuchElementException;


@Service
public class NeedDeliverableService {

    private final NeedDeliverableRepository needDeliverableRepository;

    @Autowired
    public NeedDeliverableService(
            NeedDeliverableRepository needDeliverableRepository) {
        this.needDeliverableRepository = needDeliverableRepository;
    }

    //Fetch need deliverable based on needPlanId
    public List<NeedDeliverable> getByNeedPlanId(String needPlanId) {
        return needDeliverableRepository.findByNeedPlanId(needPlanId);
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
