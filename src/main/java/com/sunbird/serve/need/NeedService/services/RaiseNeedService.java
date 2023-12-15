package com.sunbird.serve.need;

import com.sunbird.serve.need.models.request.RaiseNeedRequest;
import com.sunbird.serve.need.models.Need.Need;
import com.sunbird.serve.need.models.request.NeedRequest;
import com.sunbird.serve.need.models.request.NeedRequirementRequest;
import com.sunbird.serve.need.models.request.OccurrenceRequest;
import com.sunbird.serve.need.models.request.TimeSlotRequest;
import com.sunbird.serve.need.models.Need.Need;
import com.sunbird.serve.need.models.Need.NeedRequirement;
import com.sunbird.serve.need.models.Need.Occurrence;
import com.sunbird.serve.need.models.Need.TimeSlot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.Map;
import java.util.List;
import java.util.NoSuchElementException;


@Service
public class RaiseNeedService {

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

    public Need raiseNeed(RaiseNeedRequest raiseNeedRequest, Map<String, String> headers) {
        // Convert RaiseNeedRequest to Need entity
        Need need = NeedMapper.mapToEntity(raiseNeedRequest.getNeedRequest());

        // Save the Need entity
        Need savedNeed = needRepository.save(need);

        // Convert NeedRequirementRequest to NeedRequirement entity
        NeedRequirement needRequirement = NeedMapper.mapToNeedRequirement(raiseNeedRequest.getNeedRequirementRequest());
        needRequirement.setOccurrenceId(savedNeed.getId().toString()); // Set the occurrence ID

        // Save the NeedRequirement entity
        NeedRequirement savedNeedRequirement = needRequirementRepository.save(needRequirement);

        // Convert OccurrenceRequest to Occurrence entity
        Occurrence occurrence = NeedMapper.mapToOccurrence(raiseNeedRequest.getNeedRequirementRequest().getOccurrence());
        occurrence.setId(UUID.fromString(savedNeedRequirement.getOccurrenceId())); // Set the occurrence ID

        // Save the Occurrence entity
        Occurrence savedOccurrence = occurrenceRepository.save(occurrence);

        // Convert TimeSlotRequest to List<TimeSlot> entities
        List<TimeSlot> timeSlots = NeedMapper.mapToTimeSlots(savedOccurrence.getId(), raiseNeedRequest.getNeedRequirementRequest().getOccurrence().getTimeSlots());

        // Save the list of TimeSlot entities
        timeSlotRepository.saveAll(timeSlots);

        // Return the saved Need entity
        return savedNeed;
    }

    public Need updateNeed(UUID needId, NeedRequest request, Map<String, String> headers) {
        // Check if the need with the given ID exists
        Need existingNeed = needRepository.findById(needId)
        .orElseThrow(() -> new NoSuchElementException("Need not found with ID: " + needId));

          // Update the existing need with new values
        if (request.getNeedTypeId() != null) {
            existingNeed.setNeedTypeId(request.getNeedTypeId());
        }
         // Update the existing need with new values
        if (request.getEntityId() != null) {
            existingNeed.setEntityId(request.getEntityId());
        }
        // Update the existing need with new values
        if (request.getUserId() != null) {
            existingNeed.setUserId(request.getUserId());
        }
        // Update the existing need with new values
        if (request.getStatus() != null) {
            existingNeed.setStatus(request.getStatus());
        }

        if (request.getNeedPurpose() != null) {
            existingNeed.setNeedPurpose(request.getNeedPurpose());
        }

        if (request.getDescription() != null) {
            existingNeed.setDescription(request.getDescription());
        }
    
        if (request.getName() != null) {
            existingNeed.setName(request.getName());
        }

        // Save the updated need
        return needRepository.save(existingNeed);
    }

}
