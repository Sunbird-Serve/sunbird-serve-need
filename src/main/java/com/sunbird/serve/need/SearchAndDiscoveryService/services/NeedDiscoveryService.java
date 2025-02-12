package com.sunbird.serve.need;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sunbird.serve.need.models.Need.*;
import com.sunbird.serve.need.models.enums.NeedStatus;
import com.sunbird.serve.need.models.enums.EntityStatus;
import com.sunbird.serve.need.models.response.NeedEntityAndRequirement;
import com.sunbird.serve.need.models.Need.NeedRequirement;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import org.springframework.data.domain.Page; 
import org.springframework.data.domain.Pageable;

@Service
public class NeedDiscoveryService {

    private static final Logger logger = LoggerFactory.getLogger(NeedDiscoveryService.class);

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

    // Fetch all the needs 
    public Page<Need> getAllNeeds(Pageable pageable) {
        return needDiscoveryRepository.findAll(pageable);
    }

    // Fetch all the entities 
    public Page<Entity> getAllEntity(EntityStatus status, Pageable pageable) {
        return entityRepository.findAllByStatus(status, pageable);
    }

    // Fetch needs based on needId
    public Optional<Need> getNeedById(UUID needId) {
        try {
            return needDiscoveryRepository.findById(needId);
        } catch (Exception e) {
            logger.error("Error fetching Need by ID: {}", needId, e);
            throw new RuntimeException("Error fetching Need by ID", e);
        }
    }

    // Fetch need by status
    public Page<NeedEntityAndRequirement> getNeedsByStatus(NeedStatus status, Pageable pageable) {
        try {
            Page<Need> needsPage = needDiscoveryRepository.findAllByStatus(status, pageable);

            return needsPage.map(need -> {
                try {
                    Optional<NeedRequirement> needRequirement = needRequirementRepository.findById(UUID.fromString(need.getRequirementId()));
                    Optional<Entity> entity = entityRepository.findById(UUID.fromString(need.getEntityId()));
                    Optional<NeedType> needType = needTypeRepository.findById(UUID.fromString(need.getNeedTypeId()));

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
                    logger.error("Error processing NeedEntityAndRequirement for Need ID: {}", need.getId(), e);
                    return NeedEntityAndRequirement.builder()
                            .need(need)
                            .build(); // Return partial data or empty object
                }
            });
        } catch (Exception e) {
            logger.error("Error fetching Needs by Status: {}", status, e);
            throw new RuntimeException("Error fetching Needs by Status", e);
        }
    }

    // Fetch needs based on needTypeId
    public Page<Need> getNeedByNeedTypeId(String needTypeId, Pageable pageable) {
        try {
            return needDiscoveryRepository.findAllByNeedTypeId(needTypeId, pageable);
        } catch (Exception e) {
            logger.error("Error fetching Needs by NeedTypeId: {}", needTypeId, e);
            throw new RuntimeException("Error fetching Needs by NeedTypeId", e);
        }
    }

    // Fetch needs based on userId
    public Page<Need> getNeedByUserId(String userId, Pageable pageable) {
        try {
            return needDiscoveryRepository.findAllByUserId(userId, pageable);
        } catch (Exception e) {
            logger.error("Error fetching Needs by UserId: {}", userId, e);
            throw new RuntimeException("Error fetching Needs by UserId", e);
        }
    }

// Fetch needs based on userId
    public Page<Need> getNeedByEntityId(String entityId, Pageable pageable) {
        try {
            return needDiscoveryRepository.findAllByEntityId(entityId, pageable);
        } catch (Exception e) {
            logger.error("Error fetching Needs by entityId: {}", entityId, e);
            throw new RuntimeException("Error fetching Needs by entityId", e);
        }
    }

public Page<Need> getNeedByEntityIds(List<String> entityIds, Pageable pageable) {
    // Logic to fetch needs based on multiple entityIds
    return needDiscoveryRepository.findAllByEntityIds(entityIds, pageable);
}

    // Fetch needs based on userId and status
    public Page<Need> getNeedByUserIdAndStatus(String userId, NeedStatus status, Pageable pageable) {
        try {
            return needDiscoveryRepository.findAllByUserIdAndStatus(userId, status, pageable);
        } catch (Exception e) {
            logger.error("Error fetching Needs by UserId and Status: {} {}", userId, status, e);
            throw new RuntimeException("Error fetching Needs by UserId and Status", e);
        }
    }

    // Fetch needs based on userId and needTypeId
    public Page<Need> getNeedByUserIdAndNeedTypeId(String userId, String needTypeId, Pageable pageable) {
        try {
            return needDiscoveryRepository.findAllByUserIdAndNeedTypeId(userId, needTypeId, pageable);
        } catch (Exception e) {
            logger.error("Error fetching Needs by UserId and NeedTypeId: {} {}", userId, needTypeId, e);
            throw new RuntimeException("Error fetching Needs by UserId and NeedTypeId", e);
        }
    }
}
