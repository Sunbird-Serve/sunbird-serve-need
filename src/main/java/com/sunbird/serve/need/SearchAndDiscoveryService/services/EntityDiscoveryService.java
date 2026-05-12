package com.sunbird.serve.need;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sunbird.serve.need.models.Need.*;
import com.sunbird.serve.need.models.enums.EntityStatus;
import com.sunbird.serve.need.models.enums.UserRole;
import com.sunbird.serve.need.models.request.EntityRequest;
import com.sunbird.serve.need.models.request.EntityMappingRequest;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.stream.Collectors;

@Service
public class EntityDiscoveryService {

    private static final Logger logger = LoggerFactory.getLogger(EntityDiscoveryService.class);

    private final EntitySearchRepository entitySearchRepository;
    private final EntityMappingRepository entityMappingRepository;
    private final NeedDiscoveryRepository needDiscoveryRepository;

    @Autowired
    public EntityDiscoveryService(
            EntityMappingRepository entityMappingRepository,
            EntitySearchRepository entitySearchRepository,
            NeedDiscoveryRepository needDiscoveryRepository) {
        this.entityMappingRepository = entityMappingRepository;
        this.entitySearchRepository = entitySearchRepository;
        this.needDiscoveryRepository = needDiscoveryRepository;
    }

    public Page<NeedEntity> getAllEntity(EntityStatus status, Pageable pageable) {
        return entitySearchRepository.findAllByStatus(status, pageable);
    }

    public Page<NeedEntity> getAllEntities(Pageable pageable) {
        return entitySearchRepository.findAll(pageable);
    }

    public Optional<NeedEntity> getEntityById(UUID entityId) {
        try {
            return entitySearchRepository.findById(entityId);
        } catch (Exception e) {
            logger.error("Error fetching Entity by ID: {}", entityId, e);
            throw new RuntimeException("Error fetching Entity by ID", e);
        }
    }

    public Page<NeedEntity> getEntitiesByUserId(String userId, Pageable pageable) {
        try {
            return entitySearchRepository.findEntitiesByUserId(userId, pageable);
        } catch (Exception e) {
            logger.error("Error fetching Entities by UserId: {}", userId, e);
            throw new RuntimeException("Error fetching Entities by UserId", e);
        }
    }

    public Page<UserMapping> getUsersByEntityId(UUID entityId, Pageable pageable) {
        try {
            return entityMappingRepository.findUsersByOrgId(entityId, pageable);
        } catch (Exception e) {
            logger.error("Error fetching Users by entityId: {}", entityId, e);
            throw new RuntimeException("Error fetching Users by entityId", e);
        }
    }

    public Page<Need> getNeedsByUserId(String userId, Pageable pageable) {
        try {
            List<NeedEntity> entities = entitySearchRepository.findEntitiesByUserId(userId, pageable).getContent();
            List<String> entityIds = entities.stream()
                .map(e -> e.getId().toString())
                .collect(Collectors.toList());
            return needDiscoveryRepository.findAllByEntityIds(entityIds, pageable);
        } catch (Exception e) {
            logger.error("Error fetching Needs by UserId: {}", userId, e);
            throw new RuntimeException("Error fetching Needs by UserId", e);
        }
    }

    public NeedEntity createEntity(EntityRequest request, Map<String, String> headers) {
        NeedEntity entity = NeedEntity.builder()
            .name(request.getName())
            .registrationId(request.getRegistrationId())
            .website(request.getWebsite())
            .addressLine1(request.getAddressLine1())
            .mobile(request.getMobile())
            .district(request.getDistrict())
            .state(request.getState())
            .pincode(request.getPincode())
            .category(request.getCategory())
            .status(request.getStatus())
            .agencyId(headers.get("x-agency-id"))
            .build();
        return entitySearchRepository.save(entity);
    }

    public NeedEntity editEntity(UUID id, EntityRequest request, Map<String, String> headers) {
        return entitySearchRepository.findById(id)
            .map(entity -> {
                if (request.getName() != null) entity.setName(request.getName());
                if (request.getRegistrationId() != null) entity.setRegistrationId(request.getRegistrationId());
                if (request.getWebsite() != null) entity.setWebsite(request.getWebsite());
                if (request.getAddressLine1() != null) entity.setAddressLine1(request.getAddressLine1());
                if (request.getMobile() != null) entity.setMobile(request.getMobile());
                if (request.getDistrict() != null) entity.setDistrict(request.getDistrict());
                if (request.getState() != null) entity.setState(request.getState());
                if (request.getPincode() != null) entity.setPincode(request.getPincode());
                if (request.getCategory() != null) entity.setCategory(request.getCategory());
                if (request.getStatus() != null) entity.setStatus(request.getStatus());
                return entitySearchRepository.save(entity);
            })
            .orElseThrow(() -> new RuntimeException("Entity not found with id: " + id));
    }

    public UserMapping assignEntity(EntityMappingRequest request, Map<String, String> headers) {
        UserMapping mapping = UserMapping.builder()
            .agencyId(request.getAgencyId() != null ? request.getAgencyId() : headers.get("x-agency-id"))
            .orgId(request.getEntityId())
            .userId(request.getUserId())
            .userRole(UserRole.valueOf(request.getUserRole()))
            .build();
        return entityMappingRepository.save(mapping);
    }

    public UserMapping editAssignedEntity(UUID id, EntityMappingRequest request, Map<String, String> headers) {
        return entityMappingRepository.findById(id)
            .map(mapping -> {
                if (request.getEntityId() != null) mapping.setOrgId(request.getEntityId());
                if (request.getUserId() != null) mapping.setUserId(request.getUserId());
                if (request.getUserRole() != null) mapping.setUserRole(UserRole.valueOf(request.getUserRole()));
                if (request.getAgencyId() != null) mapping.setAgencyId(request.getAgencyId());
                return entityMappingRepository.save(mapping);
            })
            .orElseThrow(() -> new RuntimeException("User Mapping not found with id: " + id));
    }

    public Page<NeedEntity> getEntitiesByAgencyId(String agencyId, Pageable pageable) {
        try {
            return entitySearchRepository.findAllByAgencyId(agencyId, pageable);
        } catch (Exception e) {
            logger.error("Error fetching Entities by AgencyId: {}", agencyId, e);
            throw new RuntimeException("Error fetching Entities by AgencyId", e);
        }
    }
}
