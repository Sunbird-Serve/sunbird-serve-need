package com.sunbird.serve.need;

import com.sunbird.serve.need.models.request.NeedRequest;
import com.sunbird.serve.need.models.request.NeedTypeRequest;
import com.sunbird.serve.need.models.request.NeedRequirementRequest;
import com.sunbird.serve.need.models.request.OccurrenceRequest;
import com.sunbird.serve.need.models.request.TimeSlotRequest;
import com.sunbird.serve.need.models.Need.Need;
import com.sunbird.serve.need.models.Need.NeedType;
import com.sunbird.serve.need.models.Need.NeedRequirement;
import com.sunbird.serve.need.models.Need.Occurrence;
import com.sunbird.serve.need.models.Need.TimeSlot;
import com.sunbird.serve.need.models.response.NeedResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

public class NeedMapper {

    public static Need mapToEntity(NeedRequest needRequest) {
        // Need request is mapped to the need entity
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
    }

    public static NeedRequirement mapToNeedRequirement(NeedRequirementRequest needRequirementRequest) {
        // Need Requirement Request is mapped to Need Requirement Entity
        NeedRequirement needRequirement = new NeedRequirement();
        needRequirement.setSkillDetails(needRequirementRequest.getSkillDetails());
        needRequirement.setOccurrenceId(null);
        needRequirement.setVolunteersRequired(needRequirementRequest.getVolunteersRequired());
        needRequirement.setPriority(needRequirementRequest.getPriority());

        return needRequirement;
    }

    public static Occurrence mapToOccurrence(OccurrenceRequest occurrenceRequest) {
        // Occurence request is mapped to the occurrence entity
        Occurrence occurrence = new Occurrence();
        occurrence.setStartDate(occurrenceRequest.getStartDate());
        occurrence.setEndDate(occurrenceRequest.getEndDate());
        occurrence.setDays(occurrenceRequest.getDays());
        occurrence.setFrequency(occurrenceRequest.getFrequency());

        return occurrence;
    }

    public static List<TimeSlot> mapToTimeSlots(UUID occurrenceId, List<TimeSlotRequest> timeSlotRequests) {
        // Time Slot which is a List is mapped to Time Slot entity
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
    }

    public static NeedType mapToNeedType(NeedTypeRequest needTypeRequest) {
        // Need request is mapped to the need entity
        NeedType needType = new NeedType();
        needType.setName(needTypeRequest.getName());
        needType.setTaxonomyId(needTypeRequest.getTaxonomyId());
        needType.setDescription(needTypeRequest.getDescription());
        needType.setStatus(needTypeRequest.getStatus());
        needType.setUserId(needTypeRequest.getUserId());
        needType.setOnboardingId(needTypeRequest.getOnboardingId());
        needType.setTaskType(needTypeRequest.getTaskType());

        return needType;
    }

}

