package com.sunbird.serve.need;

import com.sunbird.serve.need.models.request.RaiseNeedRequest;
import com.sunbird.serve.need.models.request.NeedRequest;
import com.sunbird.serve.need.models.request.NeedRequirementRequest;
import com.sunbird.serve.need.models.request.OccurrenceRequest;
import com.sunbird.serve.need.models.request.TimeSlotRequest;
import com.sunbird.serve.need.models.Need.Need;
import com.sunbird.serve.need.models.Need.NeedRequirement;
import com.sunbird.serve.need.models.Need.Occurrence;
import com.sunbird.serve.need.models.Need.TimeSlot;
import com.sunbird.serve.need.models.enums.NeedStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.Map;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class RaiseNeedService {

    private static final Logger logger = LoggerFactory.getLogger(RaiseNeedService.class);

    private final NeedRepository needRepository;
    private final OccurrenceRepository occurrenceRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final NeedRequirementRepository needRequirementRepository;

    @Autowired
    public RaiseNeedService(
            NeedRepository needRepository,
            OccurrenceRepository occurrenceRepository,
            TimeSlotRepository timeSlotRepository,
            NeedRequirementRepository needRequirementRepository) {
        this.needRepository = needRepository;
        this.occurrenceRepository = occurrenceRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.needRequirementRepository = needRequirementRepository;
    }

    // Raise Need 
    public Need raiseNeed(RaiseNeedRequest request, Map<String, String> headers) {
        try {
            String requirementId = request.getNeedRequest().getRequirementId();

            // Save Occurrence
            Occurrence occurrence = occurrenceRepository.save(
                    Occurrence.builder()
                            .days(request.getNeedRequirementRequest().getOccurrence().getDays())
                            .frequency(request.getNeedRequirementRequest().getOccurrence().getFrequency())
                            .startDate(request.getNeedRequirementRequest().getOccurrence().getStartDate())
                            .endDate(request.getNeedRequirementRequest().getOccurrence().getEndDate())
                            .build()
            );

            // Save Time Slots
            timeSlotRepository.saveAll(
                    request.getNeedRequirementRequest()
                            .getOccurrence().getTimeSlots()
                            .stream().map(req -> TimeSlot.builder()
                                    .day(req.getDay())
                                    .startTime(req.getStartTime())
                                    .endTime(req.getEndTime())
                                    .occurrenceId(occurrence.getId().toString())
                                    .build()).toList()
            );

            if (requirementId == null) {
                // Save Need Requirement
                NeedRequirement needRequirement = needRequirementRepository.save(
                        NeedRequirement.builder()
                                .priority(request.getNeedRequirementRequest().getPriority())
                                .skillDetails(request.getNeedRequirementRequest().getSkillDetails())
                                .occurrenceId(occurrence.getId().toString())
                                .volunteersRequired(request.getNeedRequirementRequest().getVolunteersRequired())
                                .build()
                );

                requirementId = needRequirement.getId().toString();
            }

            // Save Need
            return needRepository.save(
                    Need.builder()
                            .needPurpose(request.getNeedRequest().getNeedPurpose())
                            .needTypeId(request.getNeedRequest().getNeedTypeId())
                            .entityId(request.getNeedRequest().getEntityId())
                            .description(request.getNeedRequest().getDescription())
                            .requirementId(requirementId)
                            .userId(request.getNeedRequest().getUserId())
                            .status(request.getNeedRequest().getStatus())
                            .name(request.getNeedRequest().getName())
                            .build()
            );
        } catch (Exception e) {
            logger.error("Error raising Need with request: " + request, e);
            throw new RuntimeException("Error raising Need", e);
        }
    }

    public Need updateNeed(UUID needId, RaiseNeedRequest request, Map<String, String> headers) {
        try {
            // Check if the need with the given ID exists
            Need existingNeed = needRepository.findById(needId)
                    .orElseThrow(() -> new NoSuchElementException("Need not found with ID: " + needId));

            UUID requirementId = UUID.fromString(existingNeed.getRequirementId());

            // Check if the NeedRequirement with the given ID exists
            NeedRequirement existingNeedRequirement = needRequirementRepository.findById(requirementId)
                    .orElseThrow(() -> new NoSuchElementException("Need Requirement not found with ID: " + requirementId));

            NeedRequest needRequest = request.getNeedRequest();
            NeedRequirementRequest needReqRequest = request.getNeedRequirementRequest();

            // Update the existing need with new values
            if (needRequest.getNeedTypeId() != null) {
                existingNeed.setNeedTypeId(needRequest.getNeedTypeId());
            }
            if (needRequest.getEntityId() != null) {
                existingNeed.setEntityId(needRequest.getEntityId());
            }
            if (needRequest.getUserId() != null) {
                existingNeed.setUserId(needRequest.getUserId());
            }
            if (needRequest.getStatus() != null) {
                existingNeed.setStatus(needRequest.getStatus());
            }
            if (needRequest.getNeedPurpose() != null) {
                existingNeed.setNeedPurpose(needRequest.getNeedPurpose());
            }
            if (needRequest.getDescription() != null) {
                existingNeed.setDescription(needRequest.getDescription());
            }
            if (needRequest.getName() != null) {
                existingNeed.setName(needRequest.getName());
            }

            // Update Need Requirement
            if (needReqRequest.getSkillDetails() != null) {
                existingNeedRequirement.setSkillDetails(needReqRequest.getSkillDetails());
            }
            if (needReqRequest.getPriority() != null) {
                existingNeedRequirement.setPriority(needReqRequest.getPriority());
            }
            if (needReqRequest.getVolunteersRequired() != null) {
                existingNeedRequirement.setVolunteersRequired(needReqRequest.getVolunteersRequired());
            }

            // Update Occurrence if provided
            OccurrenceRequest occurrenceRequest = needReqRequest.getOccurrence();
            if (occurrenceRequest != null) {
                Occurrence existingOccurrence = occurrenceRepository.findById(UUID.fromString(existingNeedRequirement.getOccurrenceId()))
                        .orElseThrow(() -> new NoSuchElementException("Occurrence not found with ID: " + existingNeedRequirement.getOccurrenceId()));

                existingOccurrence.setDays(occurrenceRequest.getDays());
                existingOccurrence.setFrequency(occurrenceRequest.getFrequency());
                existingOccurrence.setStartDate(occurrenceRequest.getStartDate());
                existingOccurrence.setEndDate(occurrenceRequest.getEndDate());

                // Save the updated occurrence
                occurrenceRepository.save(existingOccurrence);
            }

            // Update Time Slots if provided
            if (occurrenceRequest != null && occurrenceRequest.getTimeSlots() != null) {
                List<TimeSlotRequest> timeSlotRequests = occurrenceRequest.getTimeSlots();
                List<TimeSlot> existingTimeSlots = timeSlotRepository.findByOccurrenceId(existingNeedRequirement.getOccurrenceId());

                for (int i = 0; i < timeSlotRequests.size(); i++) {
                    TimeSlotRequest timeSlotRequest = timeSlotRequests.get(i);
                    TimeSlot existingTimeSlot = existingTimeSlots.get(i);

                    existingTimeSlot.setDay(timeSlotRequest.getDay());
                    existingTimeSlot.setStartTime(timeSlotRequest.getStartTime());
                    existingTimeSlot.setEndTime(timeSlotRequest.getEndTime());
                }

                // Save the updated time slots
                timeSlotRepository.saveAll(existingTimeSlots);
            }

            // Save the updated need
            return needRepository.save(existingNeed);
        } catch (NoSuchElementException e) {
            logger.error("Error updating Need with ID: " + needId, e);
            throw e;  // Re-throwing the exception as it is
        } catch (Exception e) {
            logger.error("Error updating Need with ID: " + needId, e);
            throw new RuntimeException("Error updating Need", e);
        }
    }

    public Need updateNeedStatus(UUID needId, NeedStatus status, Map<String, String> headers) {
        try {
            // Check if the need with the given ID exists
            Need existingNeed = needRepository.findById(needId)
                    .orElseThrow(() -> new NoSuchElementException("Need not found with ID: " + needId));

            existingNeed.setStatus(status);

            // Save the updated need
            return needRepository.save(existingNeed);
        } catch (NoSuchElementException e) {
            logger.error("Error updating Need status with ID: " + needId + ". Need not found.", e);
            throw e;  // Re-throwing the exception as it is
        } catch (Exception e) {
            logger.error("Error updating Need status with ID: " + needId, e);
            throw new RuntimeException("Error updating Need status", e);
        }
    }
}
