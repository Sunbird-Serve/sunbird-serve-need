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
import com.sunbird.serve.need.AgencyVisibilityService.services.AgencyScopeService;

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
    private final AgencyScopeService agencyScopeService;

    @Autowired
    public NeedDiscoveryService(
            NeedDiscoveryRepository needDiscoveryRepository,
            NeedRequirementRepository needRequirementRepository,
            EntityRepository entityRepository,
            EntitySearchRepository entitySearchRepository,
            NeedTypeRepository needTypeRepository,
            OccurrenceRepository occurrenceRepository,
            TimeSlotRepository timeSlotRepository,
            AgencyScopeService agencyScopeService) {
        this.needDiscoveryRepository = needDiscoveryRepository;
        this.needRequirementRepository = needRequirementRepository;
        this.entityRepository = entityRepository;
        this.entitySearchRepository = entitySearchRepository;
        this.needTypeRepository = needTypeRepository;
        this.occurrenceRepository = occurrenceRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.agencyScopeService = agencyScopeService;
    }

    // Fetch all the needs 
    public Page<Need> getAllNeeds(Pageable pageable) {
        return needDiscoveryRepository.findAll(pageable);
    }

    // Fetch all the entities 
    public Page<NeedEntity> getAllEntity(EntityStatus status, Pageable pageable) {
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

    // Fetch need with full details (requirement, occurrence, timeslots) by needId
    public Optional<NeedEntityAndRequirement> getNeedDetailsById(UUID needId) {
        try {
            Optional<Need> need = needDiscoveryRepository.findById(needId);
            if (need.isEmpty()) return Optional.empty();

            Need n = need.get();
            Optional<NeedRequirement> needRequirement = Optional.empty();
            Optional<Occurrence> occurrence = Optional.empty();
            List<TimeSlot> slots = List.of();

            if (n.getRequirementId() != null) {
                needRequirement = needRequirementRepository.findById(UUID.fromString(n.getRequirementId()));
                if (needRequirement.isPresent() && needRequirement.get().getOccurrenceId() != null) {
                    String occurrenceId = needRequirement.get().getOccurrenceId();
                    occurrence = occurrenceRepository.findById(UUID.fromString(occurrenceId));
                    slots = timeSlotRepository.findByOccurrenceId(occurrenceId);
                }
            }

            return Optional.of(NeedEntityAndRequirement.builder()
                    .need(n)
                    .needRequirement(needRequirement)
                    .occurrence(occurrence)
                    .timeSlots(slots)
                    .build());
        } catch (Exception e) {
            logger.error("Error fetching Need details by ID: {}", needId, e);
            throw new RuntimeException("Error fetching Need details by ID", e);
        }
    }

    // Fetch need by status (agency-scoped if agencyId provided)
    public Page<NeedEntityAndRequirement> getNeedsByStatus(NeedStatus status, String agencyId, Pageable pageable) {
        try {
            Page<Need> needsPage;
            if (agencyId != null) {
                needsPage = needDiscoveryRepository.findAllByAgencyIdAndStatus(agencyId, status, pageable);
            } else {
                needsPage = needDiscoveryRepository.findAllByStatus(status, pageable);
            }

            return needsPage.map(need -> {
                try {
                    Optional<NeedRequirement> needRequirement = needRequirementRepository.findById(UUID.fromString(need.getRequirementId()));
                    Optional<NeedEntity> entity = entityRepository.findById(UUID.fromString(need.getEntityId()));
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

    // Fetch needs based on needTypeId (agency-scoped)
    public Page<Need> getNeedByNeedTypeId(String needTypeId, String agencyId, Pageable pageable) {
        try {
            if (agencyId != null && !agencyId.isBlank()) {
                return needDiscoveryRepository.findAllByAgencyIdAndNeedTypeId(agencyId, needTypeId, pageable);
            }
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

    // Fetch needs based on entityId (agency-scoped)
    public Page<Need> getNeedByEntityId(String entityId, String agencyId, Pageable pageable) {
        try {
            if (agencyId != null && !agencyId.isBlank()) {
                return needDiscoveryRepository.findAllByAgencyIdAndEntityId(agencyId, entityId, pageable);
            }
            return needDiscoveryRepository.findAllByEntityId(entityId, pageable);
        } catch (Exception e) {
            logger.error("Error fetching Needs by entityId: {}", entityId, e);
            throw new RuntimeException("Error fetching Needs by entityId", e);
        }
    }

public Page<Need> getNeedByEntityIds(List<String> entityIds, String agencyId, Pageable pageable) {
    // Logic to fetch needs based on multiple entityIds
    // Note: If agency-scoped, we filter the results. The repo doesn't have a combined method,
    // so we use the entityIds query and trust that entities are already agency-scoped.
    return needDiscoveryRepository.findAllByEntityIds(entityIds, pageable);
}

    // Fetch needs based on userId and status (agency-scoped)
    public Page<Need> getNeedByUserIdAndStatus(String userId, NeedStatus status, String agencyId, Pageable pageable) {
        try {
            if (agencyId != null && !agencyId.isBlank()) {
                return needDiscoveryRepository.findAllByAgencyIdAndUserId(agencyId, userId, pageable);
            }
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

    // Fetch needs by agencyId
    public Page<Need> getNeedsByAgencyId(String agencyId, Pageable pageable) {
        try {
            return needDiscoveryRepository.findAllByAgencyId(agencyId, pageable);
        } catch (Exception e) {
            logger.error("Error fetching Needs by AgencyId: {}", agencyId, e);
            throw new RuntimeException("Error fetching Needs by AgencyId", e);
        }
    }

    /**
     * Discover needs with cross-agency visibility rules applied.
     * Used by volunteers browsing available needs.
     *
     * @param volunteerAgencyId the agency the volunteer belongs to
     * @param status filter by need status (typically Approved)
     * @param pageable pagination
     * @return needs the volunteer is allowed to see
     */
    public Page<Need> discoverNeedsForVolunteer(String volunteerAgencyId, NeedStatus status, Pageable pageable) {
        try {
            List<String> allowedAgencies = agencyScopeService.resolveDiscoverableAgencies(volunteerAgencyId);

            if (allowedAgencies == null) {
                // No restriction — volunteer can see all agencies' needs
                return needDiscoveryRepository.findAllByStatus(status, pageable);
            }

            if (allowedAgencies.isEmpty()) {
                // Edge case — return empty
                return Page.empty(pageable);
            }

            return needDiscoveryRepository.findAllByAgencyIdInAndStatus(allowedAgencies, status, pageable);
        } catch (Exception e) {
            logger.error("Error discovering needs for volunteer agency: {}", volunteerAgencyId, e);
            throw new RuntimeException("Error discovering needs for volunteer", e);
        }
    }
}
