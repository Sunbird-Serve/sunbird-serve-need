package com.sunbird.serve.need;

import com.sunbird.serve.need.models.request.NeedRequest;
import com.sunbird.serve.need.models.request.NeedTypeRequest;
import com.sunbird.serve.need.models.request.NeedRequirementRequest;
import com.sunbird.serve.need.models.request.OccurrenceRequest;
import com.sunbird.serve.need.models.request.TimeSlotRequest;
import com.sunbird.serve.need.models.Need.Need;
import com.sunbird.serve.need.models.Need.Occurrence;
import com.sunbird.serve.need.models.Need.NeedRequirement;
import com.sunbird.serve.need.models.Need.TimeSlot;
import com.sunbird.serve.need.models.Need.NeedType;
import com.sunbird.serve.need.models.response.NeedResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

public class NeedMapper {

    private static final Logger logger = LoggerFactory.getLogger(NeedMapper.class);

    public static Need mapToEntity(NeedRequest needRequest) {
        try {
            Need need = new Need();
            need.setNeedTypeId(needRequest.getNeedTypeId());
            need.setName(needRequest.getName());
            need.setNeedPurpose(needRequest.getNeedPurpose());
            need.setDescription(needRequest.getDescription());
            need.setStatus(needRequest.getStatus());
            need.setUserId(needRequest.getUserId());
            need.setEntityId(needRequest.getEntityId());
            need.setRequirementId(needRequest.getRequirementId());

            return need;
        } catch (Exception e) {
            logger.error("Error mapping NeedRequest to Need", e);
            throw new RuntimeException("Error mapping NeedRequest to Need", e);
        }
    }

    public static NeedRequirement mapToNeedRequirement(NeedRequirementRequest needRequirementRequest) {
        try {
            NeedRequirement needRequirement = new NeedRequirement();
            needRequirement.setSkillDetails(needRequirementRequest.getSkillDetails());
            needRequirement.setOccurrenceId(null);
            needRequirement.setVolunteersRequired(needRequirementRequest.getVolunteersRequired());
            needRequirement.setPriority(needRequirementRequest.getPriority());

            return needRequirement;
        } catch (Exception e) {
            logger.error("Error mapping NeedRequirementRequest to NeedRequirement", e);
            throw new RuntimeException("Error mapping NeedRequirementRequest to NeedRequirement", e);
        }
    }

    public static Occurrence mapToOccurrence(OccurrenceRequest occurrenceRequest) {
        try {
            Occurrence occurrence = new Occurrence();
            occurrence.setStartDate(occurrenceRequest.getStartDate());
            occurrence.setEndDate(occurrenceRequest.getEndDate());
            occurrence.setDays(occurrenceRequest.getDays());
            occurrence.setFrequency(occurrenceRequest.getFrequency());

            return occurrence;
        } catch (Exception e) {
            logger.error("Error mapping OccurrenceRequest to Occurrence", e);
            throw new RuntimeException("Error mapping OccurrenceRequest to Occurrence", e);
        }
    }

    public static List<TimeSlot> mapToTimeSlots(UUID occurrenceId, List<TimeSlotRequest> timeSlotRequests) {
        try {
            return timeSlotRequests.stream()
                    .map(req -> {
                        TimeSlot timeSlot = new TimeSlot();
                        timeSlot.setOccurrenceId(occurrenceId.toString());
                        timeSlot.setStartTime(req.getStartTime());
                        timeSlot.setEndTime(req.getEndTime());
                        timeSlot.setDay(req.getDay());

                        return timeSlot;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error mapping TimeSlotRequest list to TimeSlot list", e);
            throw new RuntimeException("Error mapping TimeSlotRequest list to TimeSlot list", e);
        }
    }

    public static NeedType mapToNeedType(NeedTypeRequest needTypeRequest) {
        try {
            NeedType needType = new NeedType();
            needType.setName(needTypeRequest.getName());
            needType.setTaxonomyId(needTypeRequest.getTaxonomyId());
            needType.setDescription(needTypeRequest.getDescription());
            needType.setStatus(needTypeRequest.getStatus());
            needType.setUserId(needTypeRequest.getUserId());
            needType.setOnboardingId(needTypeRequest.getOnboardingId());
            needType.setTaskType(needTypeRequest.getTaskType());

            return needType;
        } catch (Exception e) {
            logger.error("Error mapping NeedTypeRequest to NeedType", e);
            throw new RuntimeException("Error mapping NeedTypeRequest to NeedType", e);
        }
    }
}
