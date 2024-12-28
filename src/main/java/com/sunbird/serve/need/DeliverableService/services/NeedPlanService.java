package com.sunbird.serve.need;

import com.sunbird.serve.need.models.Need.NeedPlan;
import com.sunbird.serve.need.models.Need.Occurrence;
import com.sunbird.serve.need.models.Need.NeedDeliverable;
import com.sunbird.serve.need.models.Need.InputParameters;
import com.sunbird.serve.need.models.Need.TimeSlot; // Ensure this is imported
import com.sunbird.serve.need.models.enums.NeedDeliverableStatus;
import com.sunbird.serve.need.models.enums.SoftwarePlatform;
import com.sunbird.serve.need.models.request.NeedPlanRequest;
import com.sunbird.serve.need.models.response.NeedPlanResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.Map;
import java.util.List;
import java.time.*;
import java.util.*;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.stream.Collectors; // Ensure this is imported

@Service
public class NeedPlanService {

    private final NeedPlanRepository needPlanRepository;
    private final OccurrenceRepository occurrenceRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final NeedDeliverableRepository needDeliverableRepository;
    private final InputParametersRepository inputParametersRepository;

    private static final Logger logger = LoggerFactory.getLogger(NeedPlanService.class);

    @Autowired
    public NeedPlanService(
            NeedPlanRepository needPlanRepository,
            OccurrenceRepository occurrenceRepository,
            TimeSlotRepository timeSlotRepository,
            NeedDeliverableRepository needDeliverableRepository,
            InputParametersRepository inputParametersRepository) {
        this.needPlanRepository = needPlanRepository;
        this.occurrenceRepository = occurrenceRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.needDeliverableRepository = needDeliverableRepository;
        this.inputParametersRepository = inputParametersRepository;
    }

    // Fetch needs based on needTypeId
    public List<NeedPlanResponse> getByNeedId(String needId) {
        try {
            List<NeedPlan> needPlans = needPlanRepository.findByNeedId(needId);
            return needPlans.stream().map(plan -> {
                try {
                    Occurrence occurrence = occurrenceRepository.findById(UUID.fromString(plan.getOccurrenceId()))
                            .orElseThrow(() -> new NoSuchElementException("Occurrence not found"));
                    List<TimeSlot> slots = timeSlotRepository.findByOccurrenceId(plan.getOccurrenceId());
                    return NeedPlanResponse.builder()
                            .plan(plan)
                            .occurrence(occurrence)
                            .timeSlots(slots)
                            .build();
                } catch (Exception e) {
                    logger.error("Error processing NeedPlan with ID: " + plan.getId(), e);
                    return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching NeedPlanResponse for needId: " + needId, e);
            throw new RuntimeException("Error fetching NeedPlanResponse", e);
        }
    }

    // Fetch need plan based on needPlanId
    public Optional<NeedPlanResponse> getNeedPlanById(UUID needPlanId) {
        try {
            Optional<NeedPlan> optionalNeedPlan = needPlanRepository.findById(needPlanId);
            return optionalNeedPlan.map(plan -> {
                try {
                    Occurrence occurrence = occurrenceRepository.findById(UUID.fromString(plan.getOccurrenceId()))
                            .orElseThrow(() -> new NoSuchElementException("Occurrence not found"));
                    List<TimeSlot> slots = timeSlotRepository.findByOccurrenceId(plan.getOccurrenceId());
                    return NeedPlanResponse.builder()
                            .plan(plan)
                            .occurrence(occurrence)
                            .timeSlots(slots)
                            .build();
                } catch (Exception e) {
                    logger.error("Error processing NeedPlan with ID: " + needPlanId, e);
                    throw new RuntimeException("Error processing NeedPlanResponse", e);
                }
            });
        } catch (Exception e) {
            logger.error("Error fetching NeedPlan by ID: " + needPlanId, e);
            throw new RuntimeException("Error fetching NeedPlan", e);
        }
    }

    public NeedPlan createNeedPlan(NeedPlanRequest needPlanRequest, Map<String, String> headers) {
        try {
            logger.info("Creating NeedPlan with request: {}", needPlanRequest);
            NeedPlan needPlan = DeliverableMapper.mapToEntity(needPlanRequest);
            NeedPlan savedNeedPlan = needPlanRepository.save(needPlan);
            createDeliverablesAndDetails(savedNeedPlan, headers);
            return savedNeedPlan;
        } catch (Exception e) {
            logger.error("Error creating NeedPlan with request: " + needPlanRequest, e);
            throw new RuntimeException("Error creating NeedPlan", e);
        }
    }

    private void createDeliverablesAndDetails(NeedPlan needPlan, Map<String, String> headers) {
        try {
            Optional<Occurrence> occurrenceOptional = occurrenceRepository.findById(UUID.fromString(needPlan.getOccurrenceId()));
            if (occurrenceOptional.isPresent()) {
                Occurrence occurrence = occurrenceOptional.get();
                LocalDate startDate = occurrence.getStartDate().atZone(ZoneId.of("Asia/Kolkata")).toLocalDate();
                LocalDate endDate = occurrence.getEndDate().atZone(ZoneId.of("Asia/Kolkata")).toLocalDate();
                List<TimeSlot> timeSlots = timeSlotRepository.findByOccurrenceId(needPlan.getOccurrenceId());
                
                List<DayOfWeek> daysOfWeek = Arrays.stream(occurrence.getDays().split(","))
                        .map(day -> DayOfWeek.valueOf(day.trim().toUpperCase()))
                        .collect(Collectors.toList());

                List<LocalDate> deliverableDates = startDate.datesUntil(endDate.plusDays(1), Period.ofDays(1))
                        .filter(localDate -> daysOfWeek.contains(localDate.getDayOfWeek()))
                        .collect(Collectors.toList());

                for (LocalDate date : deliverableDates) {
                    try {
                        NeedDeliverable needDeliverable = NeedDeliverable.builder()
                                .needPlanId(needPlan.getId().toString())
                                .deliverableDate(date)
                                .status(NeedDeliverableStatus.Planned)
                                .build();
                        needDeliverable = needDeliverableRepository.save(needDeliverable);

                        if (!timeSlots.isEmpty()) {
                            // Use the first time slot as a representative
                            TimeSlot timeSlot = timeSlots.get(0);

                            // Extract time component
                            LocalTime startTime = timeSlot.getStartTime().atZone(ZoneId.of("Asia/Kolkata")).toLocalTime();
                            LocalTime endTime = timeSlot.getEndTime().atZone(ZoneId.of("Asia/Kolkata")).toLocalTime();

                            // Combine with deliverableDate to create ZonedDateTime
                            ZonedDateTime startDateTime = ZonedDateTime.of(date, startTime, ZoneId.of("Asia/Kolkata"));
                            ZonedDateTime endDateTime = ZonedDateTime.of(date, endTime, ZoneId.of("Asia/Kolkata"));

                            InputParameters inputParameters = new InputParameters();
                            inputParameters.setNeedDeliverableId(needDeliverable.getId().toString());
                            inputParameters.setInputUrl("To be added soon");
                            inputParameters.setSoftwarePlatform(SoftwarePlatform.GMEET);
                            inputParameters.setStartTime(startDateTime.toInstant());
                            inputParameters.setEndTime(endDateTime.toInstant());

                            inputParametersRepository.save(inputParameters);
                        }
                    } catch (Exception e) {
                        logger.error("Error creating NeedDeliverable for NeedPlan ID: " + needPlan.getId(), e);
                        throw new RuntimeException("Error creating NeedDeliverable", e);
                    }
                }
            } else {
                throw new NoSuchElementException("Occurrence not found with ID: " + needPlan.getOccurrenceId());
            }
        } catch (Exception e) {
            logger.error("Error creating deliverables and details for NeedPlan ID: " + needPlan.getId(), e);
            throw new RuntimeException("Error creating deliverables and details", e);
        }
    }
}
