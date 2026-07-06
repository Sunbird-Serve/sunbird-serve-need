package com.sunbird.serve.need;

import com.sunbird.serve.need.models.Need.*;
import com.sunbird.serve.need.models.enums.NeedStatus;
import com.sunbird.serve.need.models.enums.NeedDeliverableStatus;
import com.sunbird.serve.need.models.enums.SoftwarePlatform;
import com.sunbird.serve.need.models.request.*;
import com.sunbird.serve.need.models.response.NeedDeliverableResponse;
import com.sunbird.serve.need.models.dto.TimeSlotDTO;
import com.sunbird.serve.need.models.dto.InputParametersDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;

@Service
public class NeedDeliverableService {

    private final NeedDeliverableRepository needDeliverableRepository;
    private final InputParametersRepository inputParametersRepository;
    private final OutputParametersRepository outputParametersRepository;
    private final NeedRepository needRepository;
    private final NeedPlanRepository needPlanRepository;
    
    @Autowired
    private WebClient.Builder webClientBuilder;
    
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

    // Fetch need deliverable based on needPlanId
    public NeedDeliverableResponse getByNeedPlanId(String needPlanId) {
        try {
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
        } catch (Exception e) {
            logger.error("Error fetching NeedDeliverableResponse for needPlanId: " + needPlanId, e);
            throw new RuntimeException("Error fetching NeedDeliverableResponse", e);
        }
    }

    public NeedDeliverable createNeedDeliverable(NeedDeliverableRequest needDeliverableRequest, Map<String, String> headers) {
        try {
            // Convert Need Deliverable Request to Need Deliverable entity
            NeedDeliverable needDeliverable = DeliverableMapper.mapToDeliverable(needDeliverableRequest);

            // Save the Need Deliverable
            return needDeliverableRepository.save(needDeliverable);
        } catch (Exception e) {
            logger.error("Error creating NeedDeliverable with request: " + needDeliverableRequest, e);
            throw new RuntimeException("Error creating NeedDeliverable", e);
        }
    }

    public NeedDeliverable updateNeedDeliverable(UUID needDeliverableId, NeedDeliverableRequest request, Map<String, String> headers) {
        try {
            // Check if the need with the given ID exists
            NeedDeliverable existingNeedDeliverable = needDeliverableRepository.findById(needDeliverableId)
                .orElseThrow(() -> new NoSuchElementException("Need Deliverable not found with ID: " + needDeliverableId));

            // Update the existing need with new values
            existingNeedDeliverable.setNeedPlanId(request.getNeedPlanId());
            existingNeedDeliverable.setComments(request.getComments());
            existingNeedDeliverable.setStatus(request.getStatus());
            existingNeedDeliverable.setDeliverableDate(request.getDeliverableDate());
            if (request.getInputParameters() != null) {
                existingNeedDeliverable.setInputParameters(request.getInputParameters());
            }
            if (request.getOutputParameters() != null) {
                existingNeedDeliverable.setOutputParameters(request.getOutputParameters());
            }

            // Email notification for cancelled sessions
            if (request.getStatus() == NeedDeliverableStatus.Cancelled) {
                NeedPlan needPlan = needPlanRepository.findById(UUID.fromString(request.getNeedPlanId()))
                    .orElseThrow(() -> new NoSuchElementException("Need Plan not found with ID: " + request.getNeedPlanId()));
                Need need = needRepository.findById(UUID.fromString(needPlan.getNeedId()))
                    .orElseThrow(() -> new NoSuchElementException("Need not found with ID: " + needPlan.getNeedId()));

                String apiUrl = "http://serve-v1.evean.net/api/v1/serve-fulfill/fulfillment/sendEmail";

                Map<String, Object> apiRequestBody = new HashMap<>();
                apiRequestBody.put("scenarioType", "CancelSession");
                apiRequestBody.put("needId", need.getId());
                apiRequestBody.put("deliverableDetails", existingNeedDeliverable);

                webClientBuilder.build()
                    .post()
                    .uri(apiUrl)
                    .bodyValue(apiRequestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnSuccess(response -> logger.info("Email notification sent successfully."))
                    .doOnError(e -> {
                        logger.error("Error occurred while calling the fulfill microservice: " + e.getMessage(), e);
                        throw new RuntimeException("Error occurred while calling the fulfill microservice", e);
                    })
                    .block();  // Block if you want to make this a synchronous call
            }

            // Save the updated need deliverable
            NeedDeliverable updatedNeedDeliverable = needDeliverableRepository.save(existingNeedDeliverable);

            // Fetch all need deliverables for the given need plan ID
            List<NeedDeliverable> needDeliverables = needDeliverableRepository.findByNeedPlanId(request.getNeedPlanId());

            // Check if any of the need deliverable statuses is "Not Started"
            boolean allNotStarted = needDeliverables.stream().anyMatch(nd -> nd.getStatus().toString().trim().equalsIgnoreCase("Planned"));
            
            // If none of the statuses are "Not Started", update the need status as "Fulfilled"
            if (!allNotStarted) {
                NeedPlan needPlan = needPlanRepository.findById(UUID.fromString(request.getNeedPlanId()))
                    .orElseThrow(() -> new NoSuchElementException("Need Plan not found with ID: " + request.getNeedPlanId()));
                Need need = needRepository.findById(UUID.fromString(needPlan.getNeedId()))
                    .orElseThrow(() -> new NoSuchElementException("Need not found with ID: " + needPlan.getNeedId()));
                
                need.setStatus(NeedStatus.Fulfilled);
                needRepository.save(need);
            }

            return updatedNeedDeliverable;
        } catch (Exception e) {
            logger.error("Error updating NeedDeliverable with ID: " + needDeliverableId, e);
            throw new RuntimeException("Error updating NeedDeliverable", e);
        }
    }

    public List<NeedDeliverable> updateNeedDeliverables(String needPlanId, DeliverableDetailsRequest request, Map<String, String> headers) {
        try {
            List<NeedDeliverable> needDeliverableList = needDeliverableRepository.findByNeedPlanId(needPlanId);

            List<NeedDeliverable> updatedDeliverables = new ArrayList<>();

            for (NeedDeliverable needDeliverable : needDeliverableList) {
                DeliverableDetailsRequest newRequest = DeliverableDetailsRequest.builder()
                        .inputUrl(request.getInputUrl())
                        .softwarePlatform(request.getSoftwarePlatform())
                        .startTime(request.getStartTime())
                        .endTime(request.getEndTime())
                        .build();

                List<InputParameters> updatedInputParameters = updateInputParameters(needDeliverable.getId().toString(), newRequest, headers);
                updatedDeliverables.add(needDeliverable);
            }
            return updatedDeliverables;
        } catch (Exception e) {
            logger.error("Error updating NeedDeliverables for needPlanId: " + needPlanId, e);
            throw new RuntimeException("Error updating NeedDeliverables", e);
        }
    }

    public List<InputParameters> updateInputParameters(String needDeliverableId, DeliverableDetailsRequest request, Map<String, String> headers) {
        try {
            List<InputParameters> existingInputParameters = inputParametersRepository.findByNeedDeliverableId(needDeliverableId);

            if (existingInputParameters != null) {
                for (InputParameters parameter : existingInputParameters) {
                    parameter.setNeedDeliverableId(needDeliverableId);
                    parameter.setInputUrl(request.getInputUrl());
                    parameter.setSoftwarePlatform(request.getSoftwarePlatform());
                    parameter.setStartTime(request.getStartTime());
                    parameter.setEndTime(request.getEndTime());
                }
                return inputParametersRepository.saveAll(existingInputParameters);
            } else {
                throw new NoSuchElementException("No InputParameters found for NeedDeliverableId: " + needDeliverableId);
            }
        } catch (Exception e) {
            logger.error("Error updating InputParameters for needDeliverableId: " + needDeliverableId, e);
            throw new RuntimeException("Error updating InputParameters", e);
        }
    }

    public OutputParameters createOutputParameters(OutputParametersRequest outputParametersRequest, Map<String, String> headers) {
        try {
            OutputParameters outputParameters = DeliverableMapper.mapToOutputParameters(outputParametersRequest);
            return outputParametersRepository.save(outputParameters);
        } catch (Exception e) {
            logger.error("Error creating OutputParameters with request: " + outputParametersRequest, e);
            throw new RuntimeException("Error creating OutputParameters", e);
        }
    }

    public InputParameters createInputParameters(CreateInputParametersRequest request) {
        try {
            InputParameters inputParameters = InputParameters.builder()
                .needDeliverableId(request.getNeedDeliverableId())
                .inputUrl("To be added soon")
                .softwarePlatform(SoftwarePlatform.GMEET)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();
            return inputParametersRepository.save(inputParameters);
        } catch (Exception e) {
            logger.error("Error creating InputParameters with request: " + request, e);
            throw new RuntimeException("Error creating InputParameters", e);
        }
    }

    /**
     * Reschedule deliverables for a need plan.
     * - Future "Planned" deliverables on dropped days → mark as PlannedPause
     * - Create new deliverables for added days (for remaining dates in the plan's date range)
     * - Deliverables on kept days remain unchanged
     */
    public Map<String, Object> rescheduleDeliverables(String needPlanId, RescheduleRequest request, Map<String, String> headers) {
        try {
            // Get all deliverables for this plan
            List<NeedDeliverable> allDeliverables = needDeliverableRepository.findByNeedPlanId(needPlanId);

            // Parse new days
            Set<String> newDays = Arrays.stream(request.getDays().split(","))
                .map(String::trim)
                .collect(Collectors.toSet());

            // Find future planned deliverables
            LocalDate today = LocalDate.now();
            List<NeedDeliverable> futurePlanned = needDeliverableRepository
                .findByNeedPlanIdAndStatusAndDeliverableDateGreaterThanEqual(needPlanId, NeedDeliverableStatus.Planned, today);

            // Mark deliverables on dropped days as PlannedPause
            List<NeedDeliverable> paused = new ArrayList<>();
            for (NeedDeliverable deliverable : futurePlanned) {
                if (deliverable.getDeliverableDate() != null) {
                    String dayOfWeek = deliverable.getDeliverableDate().getDayOfWeek()
                        .getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                    if (!newDays.contains(dayOfWeek)) {
                        deliverable.setStatus(NeedDeliverableStatus.PlannedPause);
                        paused.add(deliverable);
                    }
                }
            }
            needDeliverableRepository.saveAll(paused);

            // Determine date range for new deliverables
            // Use the latest deliverable date as end date, or 3 months from now
            LocalDate endDate = allDeliverables.stream()
                .filter(d -> d.getDeliverableDate() != null)
                .map(NeedDeliverable::getDeliverableDate)
                .max(LocalDate::compareTo)
                .orElse(today.plusMonths(3));

            // Find which days are new (not in existing future deliverables)
            Set<String> existingFutureDays = futurePlanned.stream()
                .filter(d -> d.getDeliverableDate() != null && d.getStatus() != NeedDeliverableStatus.PlannedPause)
                .map(d -> d.getDeliverableDate().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH))
                .collect(Collectors.toSet());

            Set<String> addedDays = newDays.stream()
                .filter(day -> !existingFutureDays.contains(day))
                .collect(Collectors.toSet());

            // Create new deliverables for added days
            List<NeedDeliverable> created = new ArrayList<>();
            if (!addedDays.isEmpty()) {
                // Build inputParameters from the request's timeSlots
                InputParametersDTO inputParams = null;
                if (request.getTimeSlots() != null && !request.getTimeSlots().isEmpty()) {
                    inputParams = InputParametersDTO.builder()
                        .timeSlots(request.getTimeSlots())
                        .startTime(request.getTimeSlots().get(0).getStartTime())
                        .endTime(request.getTimeSlots().get(0).getEndTime())
                        .build();
                }

                LocalDate cursor = today.plusDays(1);
                while (!cursor.isAfter(endDate)) {
                    String cursorDay = cursor.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                    if (addedDays.contains(cursorDay)) {
                        NeedDeliverable newDeliverable = NeedDeliverable.builder()
                            .needPlanId(needPlanId)
                            .status(NeedDeliverableStatus.Planned)
                            .deliverableDate(cursor)
                            .inputParameters(inputParams)
                            .build();
                        created.add(newDeliverable);
                    }
                    cursor = cursor.plusDays(1);
                }
                needDeliverableRepository.saveAll(created);
            }

            // Also update inputParameters on existing future deliverables that are still Planned
            if (request.getTimeSlots() != null && !request.getTimeSlots().isEmpty()) {
                InputParametersDTO updatedParams = InputParametersDTO.builder()
                    .timeSlots(request.getTimeSlots())
                    .startTime(request.getTimeSlots().get(0).getStartTime())
                    .endTime(request.getTimeSlots().get(0).getEndTime())
                    .build();

                List<NeedDeliverable> keptDeliverables = futurePlanned.stream()
                    .filter(d -> d.getStatus() == NeedDeliverableStatus.Planned)
                    .collect(Collectors.toList());

                for (NeedDeliverable d : keptDeliverables) {
                    d.setInputParameters(updatedParams);
                }
                needDeliverableRepository.saveAll(keptDeliverables);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("paused", paused.size());
            result.put("created", created.size());
            result.put("pausedDeliverables", paused);
            result.put("createdDeliverables", created);
            return result;
        } catch (Exception e) {
            logger.error("Error rescheduling deliverables for needPlanId: " + needPlanId, e);
            throw new RuntimeException("Error rescheduling deliverables", e);
        }
    }
}
