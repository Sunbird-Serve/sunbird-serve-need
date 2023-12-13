package com.sunbird.serve.need;

import com.sunbird.serve.need.models.Need.NeedPlan;
import com.sunbird.serve.need.models.Need.Occurrence;
import com.sunbird.serve.need.models.request.NeedPlanRequest;
import com.sunbird.serve.need.models.Need.TimeSlot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.Map;
import java.util.List;
import java.util.NoSuchElementException;


@Service
public class NeedPlanService {

    private final NeedPlanRepository needPlanRepository;
    private final OccurrenceRepository occurrenceRepository;
    private final TimeSlotRepository timeSlotRepository;

    @Autowired
    public NeedPlanService(
            NeedPlanRepository needPlanRepository,
            OccurrenceRepository occurrenceRepository,
            TimeSlotRepository timeSlotRepository) {
        this.needPlanRepository = needPlanRepository;
        this.occurrenceRepository = occurrenceRepository;
        this.timeSlotRepository = timeSlotRepository;
    }

    //Fetch needs based on needTypeId
    public List<NeedPlan> getByNeedId(String needId) {
        return needPlanRepository.findByNeedId(needId);
    }


    public NeedPlan createNeedPlan(NeedPlanRequest needPlanRequest, Map<String, String> headers) {
        // Convert RaiseNeedRequest to Need entity
        NeedPlan needPlan = DeliverableMapper.mapToEntity(needPlanRequest);

        // Save the Need entity
        NeedPlan savedNeedPlan = needPlanRepository.save(needPlan);

        // Return the saved Need entity
        return savedNeedPlan;
    }


}
