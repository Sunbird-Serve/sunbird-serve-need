package com.sunbird.serve.need.OnboardService.services;

import com.sunbird.serve.need.OnboardService.repositories.EntityOnboardRepository;
import com.sunbird.serve.need.models.Need.EntityOnboard;
import com.sunbird.serve.need.models.Need.NeedEntity;
import com.sunbird.serve.need.models.Need.UserMapping;
import com.sunbird.serve.need.models.enums.EntityStatus;
import com.sunbird.serve.need.models.enums.OnboardRequestStatus;
import com.sunbird.serve.need.models.enums.UserRole;
import com.sunbird.serve.need.models.request.OnboardRequest;
import com.sunbird.serve.need.models.request.OnboardReviewRequest;
import com.sunbird.serve.need.models.response.OnboardStatusResponse;
import com.sunbird.serve.need.EntitySearchRepository;
import com.sunbird.serve.need.EntityMappingRepository;
import com.sunbird.serve.need.config.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OnboardService {

    private static final Logger logger = LoggerFactory.getLogger(OnboardService.class);

    private final EntityOnboardRepository entityOnboardRepository;
    private final EntitySearchRepository entitySearchRepository;
    private final EntityMappingRepository entityMappingRepository;

    @Autowired
    public OnboardService(
            EntityOnboardRepository entityOnboardRepository,
            EntitySearchRepository entitySearchRepository,
            EntityMappingRepository entityMappingRepository) {
        this.entityOnboardRepository = entityOnboardRepository;
        this.entitySearchRepository = entitySearchRepository;
        this.entityMappingRepository = entityMappingRepository;
    }

    /**
     * Submit a new onboarding request (public, no auth).
     */
    public EntityOnboard submitOnboardRequest(OnboardRequest request) {
        // Validate entity exists
        NeedEntity entity = entitySearchRepository.findById(request.getEntityId())
                .orElseThrow(() -> new NoSuchElementException("Entity not found with ID: " + request.getEntityId()));

        // Check for duplicate pending/clarification request
        List<OnboardRequestStatus> activeStatuses = List.of(
                OnboardRequestStatus.Pending, OnboardRequestStatus.Clarification);
        Optional<EntityOnboard> existing = entityOnboardRepository
                .findByMobileAndEntityIdAndStatusIn(request.getMobile(), request.getEntityId(), activeStatuses);

        if (existing.isPresent()) {
            throw new IllegalStateException("An active onboarding request already exists for this mobile and entity.");
        }

        // Use agency from entity if not provided in request
        String agencyId = request.getAgencyId() != null ? request.getAgencyId() : entity.getAgencyId();

        EntityOnboard onboardRecord = EntityOnboard.builder()
                .agencyId(agencyId)
                .entityId(request.getEntityId())
                .coordinatorName(request.getCoordinatorName())
                .mobile(request.getMobile())
                .email(request.getEmail())
                .designation(request.getDesignation())
                .infraDetails(request.getInfraDetails())
                .status(OnboardRequestStatus.Pending)
                .build();

        return entityOnboardRepository.save(onboardRecord);
    }

    /**
     * List preloaded entities for onboarding form (public, filtered).
     */
    public Page<NeedEntity> getEntitiesForOnboarding(String agencyId, String district, String block,
                                                      String state, String name, Pageable pageable) {
        return entitySearchRepository.findEntitiesForOnboarding(agencyId, district, block, state, name, pageable);
    }

    /**
     * List onboarding requests for nAdmin review (agency-scoped).
     */
    public Page<EntityOnboard> getOnboardRequests(String agencyId, OnboardRequestStatus status, Pageable pageable) {
        if (status != null) {
            return entityOnboardRepository.findAllByAgencyIdAndStatus(agencyId, status, pageable);
        }
        return entityOnboardRepository.findAllByAgencyId(agencyId, pageable);
    }

    /**
     * Get a single onboarding request by ID.
     */
    public EntityOnboard getOnboardRequestById(UUID requestId) {
        return entityOnboardRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("Onboard request not found with ID: " + requestId));
    }

    /**
     * Review an onboarding request: Authorise, Clarification, or Reject.
     */
    public EntityOnboard reviewOnboardRequest(UUID requestId, OnboardReviewRequest reviewRequest) {
        EntityOnboard onboard = entityOnboardRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("Onboard request not found with ID: " + requestId));

        String action = reviewRequest.getAction();
        String reviewerId = TenantContext.getUserId();

        switch (action) {
            case "Authorise":
                if (reviewRequest.getUserId() == null || reviewRequest.getUserId().isBlank()) {
                    throw new IllegalArgumentException("userId is required for Authorise action.");
                }
                onboard.setStatus(OnboardRequestStatus.Authorised);
                onboard.setReviewerNotes(reviewRequest.getNotes());
                onboard.setReviewedBy(reviewerId);
                onboard.setReviewedAt(Instant.now());
                entityOnboardRepository.save(onboard);

                // Provision coordinator with the serve userId passed from UI
                provisionCoordinator(onboard, reviewRequest.getUserId());
                break;

            case "Clarification":
                onboard.setStatus(OnboardRequestStatus.Clarification);
                onboard.setReviewerNotes(reviewRequest.getNotes());
                onboard.setReviewedBy(reviewerId);
                onboard.setReviewedAt(Instant.now());
                entityOnboardRepository.save(onboard);
                break;

            case "Reject":
                onboard.setStatus(OnboardRequestStatus.Rejected);
                onboard.setReviewerNotes(reviewRequest.getNotes());
                onboard.setReviewedBy(reviewerId);
                onboard.setReviewedAt(Instant.now());
                entityOnboardRepository.save(onboard);
                break;

            default:
                throw new IllegalArgumentException("Invalid action: " + action + ". Must be Authorise, Clarification, or Reject.");
        }

        return onboard;
    }

    /**
     * Check onboarding request status by mobile or email (public).
     */
    public List<OnboardStatusResponse> getOnboardStatus(String mobile, String email) {
        List<EntityOnboard> requests;

        if (mobile != null && !mobile.isBlank()) {
            requests = entityOnboardRepository.findAllByMobileOrderByCreatedAtDesc(mobile);
        } else if (email != null && !email.isBlank()) {
            requests = entityOnboardRepository.findAllByEmailOrderByCreatedAtDesc(email);
        } else {
            throw new IllegalArgumentException("Either mobile or email must be provided.");
        }

        return requests.stream().map(req -> {
            String entityName = entitySearchRepository.findById(req.getEntityId())
                    .map(NeedEntity::getName)
                    .orElse("Unknown");

            return OnboardStatusResponse.builder()
                    .requestId(req.getId())
                    .entityName(entityName)
                    .status(req.getStatus())
                    .reviewerNotes(req.getStatus() == OnboardRequestStatus.Clarification ? req.getReviewerNotes() : null)
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * Provision the coordinator on authorisation.
     * This service only handles its own domain:
     * 1. Create UserMapping (nCoordinator → entity) using the serve osid
     * 2. Mark entity as Active
     *
     * Keycloak user creation and serve-volunteering User/UserProfile creation
     * are handled by the UI/orchestration layer before calling this endpoint.
     *
     * @param onboard The authorised onboard request
     * @param userId  The serve osid (from RC/volunteering), passed by the UI
     */
    private void provisionCoordinator(EntityOnboard onboard, String userId) {
        try {
            // Step 1: Create UserMapping
            UserMapping mapping = UserMapping.builder()
                    .agencyId(onboard.getAgencyId())
                    .orgId(onboard.getEntityId())
                    .userId(userId)
                    .userRole(UserRole.nCoordinator)
                    .build();
            entityMappingRepository.save(mapping);

            // Step 2: Mark entity as Active
            entitySearchRepository.findById(onboard.getEntityId()).ifPresent(entity -> {
                if (entity.getStatus() != EntityStatus.Active) {
                    entity.setStatus(EntityStatus.Active);
                    entitySearchRepository.save(entity);
                }
            });

            logger.info("Coordinator provisioned. onboardId={}, entityId={}, userId={}",
                    onboard.getId(), onboard.getEntityId(), userId);

        } catch (Exception e) {
            logger.error("Error provisioning coordinator for onboard request: " + onboard.getId(), e);
            throw new RuntimeException("Error provisioning coordinator", e);
        }
    }
}
