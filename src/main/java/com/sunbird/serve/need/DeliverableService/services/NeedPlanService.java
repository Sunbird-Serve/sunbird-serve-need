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

import java.time.Instant;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class NeedPlanService {

    private final NeedPlanRepository needPlanRepository;
    private final OccurrenceRepository occurrenceRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final NeedDeliverableRepository needDeliverableRepository;
    private final DeliverableDetailsService deliverableDetailsService;
    private final DeliverableDetailsRepository deliverableDetailsRepository;
    private final InputParametersRepository inputParametersRepository;

    private static final Logger logger = LoggerFactory.getLogger(NeedPlanService.class);

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

    //Fetch need plan based on needPlanId
    public Optional<NeedPlanResponse> getNeedPlanById(UUID needPlanId) {
        Optional<NeedPlan> optionalNeedPlan = needPlanRepository.findById(needPlanId);
    
    return optionalNeedPlan.map(plan -> {
        Occurrence occurrence = occurrenceRepository.findById(UUID.fromString(plan.getOccurrenceId()))
                .orElseThrow(() -> new RuntimeException("Occurrence not found"));
        List<TimeSlot> slots = timeSlotRepository.findByOccurrenceId(plan.getOccurrenceId());
        
        return NeedPlanResponse.builder()
                .plan(plan)
                .occurrence(occurrence)
                .timeSlots(slots)
                .build();
    });
    }

    public NeedPlan createNeedPlan(NeedPlanRequest needPlanRequest, Map<String, String> headers) {
        // Convert RaiseNeedRequest to Need entity

        logger.info("Inside Need microservice, create need plan: {}", needPlanRequest);
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
        
        //List<NeedDeliverable> needDeliverable = needDeliverableRepository.findByNeedPlanId(needPlan.getNeedId());
        List<NeedDeliverable> needDeliverableList = needDeliverableRepository.findByNeedPlanId(needPlan.getId().toString());

        if (!needDeliverableList.isEmpty()) {
            for (NeedDeliverable needDeliverable : needDeliverableList) {
                // Create a new DeliverableDetails object for each NeedDeliverable
                DeliverableDetails deliverableDetails = new DeliverableDetails();
        
                // Set properties for DeliverableDetails
                deliverableDetails.setNeedDeliverableId(needDeliverable.getId().toString());
                deliverableDetails.setTaskType(TaskType.Session);

                // Save DeliverableDetails
                DeliverableDetails savedDeliverableDetails = deliverableDetailsRepository.save(deliverableDetails);

                //Occurrence occurrence = occurrenceRepository.findById(UUID.fromString(needPlan.getOccurrenceId())).get();
                //List<TimeSlot> timeSlots = timeSlotRepository.findByOccurrenceId(needPlan.getOccurrenceId());
                
                // Convert the start and end dates from Instant to LocalDate
                //LocalDate startDate = occurrence.getStartDate().atZone(ZoneId.systemDefault()).toLocalDate();
                //LocalDate endDate = occurrence.getEndDate().atZone(ZoneId.systemDefault()).toLocalDate();
    
                // Parse the days of the week
                /*List<DayOfWeek> daysOfWeek = Arrays.stream(occurrence.getDays().split(","))
                                           .map(String::trim)
                                           .map(day -> DayOfWeek.valueOf(day.toUpperCase()))
                                           .collect(Collectors.toList());*/


            /*for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            // Check if the current day is one of the specified days
                if (daysOfWeek.contains(date.getDayOfWeek())) {
                    for (TimeSlot timeSlot : timeSlots) {
                        // Set input parameters
                        InputParameters inputParameters = new InputParameters();
                        inputParameters.setDeliverableDetailsId(savedDeliverableDetails.getId().toString());
                        inputParameters.setInputUrl("To be added soon");
                        inputParameters.setSoftwarePlatform(SoftwarePlatform.GMEET);
                        inputParameters.setStartTime(timeSlot.getStartTime());
                        inputParameters.setEndTime(timeSlot.getEndTime());
                        Instant deliverableDate = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
                        inputParameters.setDeliverableDate(deliverableDate);
                    
                        // Save the input parameters
                        inputParametersRepository.save(inputParameters);
                    }
                }
            }*/
            }
        } else {
            System.out.println("No deliverables found for the given need plan ID.");
        }
}

}
