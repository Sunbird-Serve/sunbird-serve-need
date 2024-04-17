package com.sunbird.serve.need;

import com.sunbird.serve.need.models.Need.NeedPlan;
import com.sunbird.serve.need.models.Need.Occurrence;
import com.sunbird.serve.need.models.Need.NeedDeliverable;
import com.sunbird.serve.need.models.Need.DeliverableDetails;
import com.sunbird.serve.need.models.Need.InputParameters;
import com.sunbird.serve.need.models.enums.NeedDeliverableStatus;
import com.sunbird.serve.need.models.enums.SoftwarePlatform;
import com.sunbird.serve.need.models.enums.TaskType;
import com.sunbird.serve.need.models.request.NeedPlanRequest;
import com.sunbird.serve.need.models.request.NeedDeliverableRequest;
import com.sunbird.serve.need.models.request.DeliverableDetailsRequest;
import com.sunbird.serve.need.models.response.NeedPlanResponse;
import com.sunbird.serve.need.models.Need.TimeSlot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.Map;
import java.util.List;
import java.time.*;
import java.util.*;
import java.util.NoSuchElementException;
import org.springframework.http.ResponseEntity;


@Service
public class NeedPlanService {

    private final NeedPlanRepository needPlanRepository;
    private final OccurrenceRepository occurrenceRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final NeedDeliverableRepository needDeliverableRepository;
    private final DeliverableDetailsService deliverableDetailsService;
    private final DeliverableDetailsRepository deliverableDetailsRepository;
    private final InputParametersRepository inputParametersRepository;

    @Autowired
    public NeedPlanService(
            NeedPlanRepository needPlanRepository,
            OccurrenceRepository occurrenceRepository,
            TimeSlotRepository timeSlotRepository, 
            NeedDeliverableRepository needDeliverableRepository,
            DeliverableDetailsRepository deliverableDetailsRepository,
            DeliverableDetailsService deliverableDetailsService,
            InputParametersRepository inputParametersRepository) {
        this.needPlanRepository = needPlanRepository;
        this.occurrenceRepository = occurrenceRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.needDeliverableRepository = needDeliverableRepository;
        this.deliverableDetailsRepository = deliverableDetailsRepository;
        this.deliverableDetailsService = deliverableDetailsService;
        this.inputParametersRepository = inputParametersRepository;
    }

    //Fetch needs based on needTypeId
    public List<NeedPlanResponse> getByNeedId(String needId) {
        //return needPlanRepository.findByNeedId(needId);
        List<NeedPlan> needPlans = needPlanRepository.findByNeedId(needId);
        List<NeedPlanResponse> response = needPlans.stream().map(plan -> {
            Occurrence occurrence = occurrenceRepository.findById(UUID.fromString(plan.getOccurrenceId())).get();
            List<TimeSlot> slots = timeSlotRepository.findByOccurrenceId(plan.getOccurrenceId());
            return NeedPlanResponse.builder()
                    .plan(plan)
                    .occurrence(occurrence)
                    .timeSlots(slots)
                    .build();
        }).toList();
        return response;
    }


    public NeedPlan createNeedPlan(NeedPlanRequest needPlanRequest, Map<String, String> headers) {
        // Convert RaiseNeedRequest to Need entity
        NeedPlan needPlan = DeliverableMapper.mapToEntity(needPlanRequest);

        // Save the Need entity
        NeedPlan savedNeedPlan = needPlanRepository.save(needPlan);

        createNeedDeliverableForPlan(savedNeedPlan, headers);

        createDeliverableDetails(savedNeedPlan, headers);

        // Return the saved Need entity
        return savedNeedPlan;
    }

    // New method to create NeedDeliverable for a given NeedPlan
    private void createNeedDeliverableForPlan(NeedPlan needPlan, Map<String, String> headers) {


        Optional<Occurrence> occurrence = occurrenceRepository.findById(UUID.fromString(needPlan.getOccurrenceId()));
        if (occurrence.isPresent()) {
            LocalDate startDate = occurrence.get().getStartDate().atZone(ZoneId.of("Asia/Kolkata")).toLocalDate();
            LocalDate endDate = occurrence.get().getEndDate().atZone(ZoneId.of("Asia/Kolkata")).toLocalDate();
            List<DayOfWeek> days = Arrays.stream(occurrence.get().getDays().split(",")).map((day) -> DayOfWeek.valueOf(day.trim().toUpperCase())).toList();

            List<LocalDate> deliverableDates = startDate.datesUntil(endDate, Period.ofDays(1))
                    .filter(localDate ->  days.contains(localDate.getDayOfWeek()))
                    .toList();

            for(LocalDate date: deliverableDates) {
                needDeliverableRepository.save(
                        NeedDeliverable.builder()
                                .needPlanId(needPlan.getId().toString())
                                .deliverableDate(date)
                                .status(NeedDeliverableStatus.NotStarted)
                                .build()
                );
            }
        }

}

private void createDeliverableDetails(NeedPlan needPlan, Map<String, String> headers) {

        DeliverableDetails deliverableDetails = new DeliverableDetails();
        InputParameters inputParameters = new InputParameters();
        
        List<NeedDeliverable> needDeliverable = needDeliverableRepository.findByNeedPlanId(needPlan.getNeedId());
        String needDeliverableId = needDeliverable.get(0).getId().toString();
        deliverableDetails.setNeedDeliverableId(needDeliverableId);
        deliverableDetails.setTaskType(TaskType.Session);

        inputParameters.setInputUrl("To be added soon");
        inputParameters.setSoftwarePlatform(SoftwarePlatform.GMEET);

        DeliverableDetails savedDeliverableDetails = deliverableDetailsRepository.save(deliverableDetails);

        InputParameters savedInputParameters = inputParametersRepository.save(inputParameters);

        

}

}
