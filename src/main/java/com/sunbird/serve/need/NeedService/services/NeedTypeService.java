package com.sunbird.serve.need;

import com.sunbird.serve.need.models.request.NeedTypeRequest;
import com.sunbird.serve.need.models.request.RaiseNeedRequest;
import com.sunbird.serve.need.models.request.CreateNeedTypeRequest;
import com.sunbird.serve.need.models.Need.NeedType;
import com.sunbird.serve.need.models.request.NeedRequirementRequest;
import com.sunbird.serve.need.models.request.OccurrenceRequest;
import com.sunbird.serve.need.models.request.TimeSlotRequest;
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
public class NeedTypeService {

    private final NeedTypeRepository needTypeRepository;
    private final OccurrenceRepository occurrenceRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final NeedRequirementRepository needRequirementRepository;

    @Autowired
    public NeedTypeService(
            NeedTypeRepository needTypeRepository,
            OccurrenceRepository occurrenceRepository,
            TimeSlotRepository timeSlotRepository,
            NeedRequirementRepository needRequirementRepository) {
        this.needTypeRepository = needTypeRepository;
        this.occurrenceRepository = occurrenceRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.needRequirementRepository = needRequirementRepository;
    }

    public NeedType createNeedType(CreateNeedTypeRequest createNeedTypeRequest, Map<String, String> headers) {
        // Convert RaiseNeedRequest to Need entity
        NeedType needType = NeedMapper.mapToNeedType(createNeedTypeRequest.getNeedTypeRequest());

        // Save the Need entity
        NeedType savedNeedType = needTypeRepository.save(needType);

        // Convert NeedRequirementRequest to NeedRequirement entity
        NeedRequirement needRequirement = NeedMapper.mapToNeedRequirement(createNeedTypeRequest.getNeedRequirementRequest());
        needRequirement.setOccurrenceId(savedNeedType.getId().toString()); // Set the occurrence ID

        // Save the NeedRequirement entity
        NeedRequirement savedNeedRequirement = needRequirementRepository.save(needRequirement);

        // Convert OccurrenceRequest to Occurrence entity
        Occurrence occurrence = NeedMapper.mapToOccurrence(createNeedTypeRequest.getNeedRequirementRequest().getOccurrence());
        occurrence.setId(UUID.fromString(savedNeedRequirement.getOccurrenceId())); // Set the occurrence ID

        // Save the Occurrence entity
        Occurrence savedOccurrence = occurrenceRepository.save(occurrence);

        // Convert TimeSlotRequest to List<TimeSlot> entities
        List<TimeSlot> timeSlots = NeedMapper.mapToTimeSlots(savedOccurrence.getId(), createNeedTypeRequest.getNeedRequirementRequest().getOccurrence().getTimeSlots());

        // Save the list of TimeSlot entities
        timeSlotRepository.saveAll(timeSlots);

        // Return the saved Need entity
        return savedNeedType;
    }

    public NeedType updateNeedType(UUID needTypeId, NeedTypeRequest request, Map<String, String> headers) {
        // Check if the need with the given ID exists
        NeedType existingNeedType = needTypeRepository.findById(needTypeId)
        .orElseThrow(() -> new NoSuchElementException("Need Type not found with ID: " + needTypeId));


        // Update the existing need with new values
        existingNeedType.setUserId(request.getUserId());
        existingNeedType.setTaxonomyId(request.getTaxonomyId());
        existingNeedType.setDescription(request.getDescription());
        existingNeedType.setStatus(request.getStatus());
        existingNeedType.setName(request.getName());
        existingNeedType.setOnboardingId(request.getOnboardingId());
        existingNeedType.setTaskType(request.getTaskType());

        // Save the updated need
        return needTypeRepository.save(existingNeedType);
    }

}
