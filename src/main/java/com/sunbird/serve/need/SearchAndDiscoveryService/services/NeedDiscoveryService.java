package com.sunbird.serve.need;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sunbird.serve.need.models.Need.Need;
import com.sunbird.serve.need.models.Need.NeedType;
import com.sunbird.serve.need.models.enums.NeedStatus;
import com.sunbird.serve.need.models.Need.Entity;
import com.sunbird.serve.need.models.enums.EntityStatus;
import com.sunbird.serve.need.models.Need.Occurrence;
import com.sunbird.serve.need.models.Need.TimeSlot;
import com.sunbird.serve.need.models.response.NeedEntityAndRequirement;
import com.sunbird.serve.need.models.Need.NeedRequirement;
import com.sunbird.serve.need.*;


import java.util.Optional;
import java.util.UUID;

import java.util.List;

import org.springframework.data.domain.Page; 
import org.springframework.data.domain.Pageable;

@Service
public class NeedDiscoveryService {

    private final NeedDiscoveryRepository needDiscoveryRepository;
    private final NeedRequirementRepository needRequirementRepository;
    private final EntityRepository entityRepository;
    private final EntitySearchRepository entitySearchRepository;
    private final NeedTypeRepository needTypeRepository;
    private final OccurrenceRepository occurrenceRepository;
    private final TimeSlotRepository timeSlotRepository;

    @Autowired
    public NeedDiscoveryService(
            NeedDiscoveryRepository needDiscoveryRepository,
            NeedRequirementRepository needRequirementRepository,
            EntityRepository entityRepository,
            EntitySearchRepository entitySearchRepository,
            NeedTypeRepository needTypeRepository,
            OccurrenceRepository occurrenceRepository,
            TimeSlotRepository timeSlotRepository) {
        this.needDiscoveryRepository = needDiscoveryRepository;
        this.needRequirementRepository = needRequirementRepository;
        this.entityRepository = entityRepository;
        this.entitySearchRepository = entitySearchRepository;
        this.needTypeRepository = needTypeRepository;
        this.occurrenceRepository = occurrenceRepository;
        this.timeSlotRepository = timeSlotRepository;
    }

    //Fetch all the needs 
    public Page<Need> getAllNeeds(Pageable pageable) {
        return needDiscoveryRepository.findAll(pageable);
    }

    //Fetch all the entities 
    public Page<Entity> getAllEntity(EntityStatus status,Pageable pageable) {
        return entityRepository.findAllByStatus(status, pageable);
    }

    //Fetch needs based on needId
    public Optional<Need> getNeedById(UUID needId) {
        return needDiscoveryRepository.findById(needId);
    }

    //Fetch need by status
     public Page<NeedEntityAndRequirement> getNeedsByStatus(NeedStatus status, Pageable pageable) {
        //return needDiscoveryRepository.findAllByStatus(status, pageable);

        Page<Need> needsPage = needDiscoveryRepository.findAllByStatus(status, pageable);
        

    return needsPage.map(need -> {
            try {
                Optional<NeedRequirement> needRequirement = needRequirementRepository.findById(UUID.fromString(need.getRequirementId()));
                Optional<Entity> entity = entityRepository.findById(UUID.fromString(need.getEntityId()));
                Optional<NeedType> needType = needTypeRepository.findById((UUID.fromString(need.getNeedTypeId())));

                Optional<Occurrence> occurrence = Optional.empty();
                List<TimeSlot> slots = List.of();
                if (needRequirement.isPresent()) {
                    String occurrenceId = needRequirement.get().getOccurrenceId();
                    if (occurrenceId != null) {
                        occurrence = occurrenceRepository.findById(UUID.fromString(occurrenceId));
                        slots = timeSlotRepository.findByOccurrenceId(occurrenceId);
                    }
                }

                return NeedEntityAndRequirement.builder()
                        .need(need)
                        .needRequirement(needRequirement)
                        .occurrence(occurrence)
                        .timeSlots(slots)
                        .entity(entity)
                        .needType(needType)
                        .build();
            } catch (Exception e) {
                return null;
            }
        });
    }

    //Fetch needs based on needTypeId
    public Page<Need> getNeedByNeedTypeId(String needTypeId, Pageable pageable) {
        return needDiscoveryRepository.findAllByNeedTypeId(needTypeId, pageable);
    }

    //Fetch needs based on userId
    public Page<Need> getNeedByUserId(String userId, Pageable pageable) {
        return needDiscoveryRepository.findAllByUserId(userId, pageable);
    }

    //Fetch needs based on userId
    public Page<Need> getNeedByUserIdAndStatus(String userId, NeedStatus status, Pageable pageable) {
        return needDiscoveryRepository.findAllByUserIdAndStatus(userId, status, pageable);
    }

    //Fetch needs based on userId
    public Page<Need> getNeedByUserIdAndNeedTypeId(String userId, String needTypeId,  Pageable pageable) {
        return needDiscoveryRepository.findAllByUserIdAndNeedTypeId(userId, needTypeId, pageable);
    }
}
