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
public class EntityDiscoveryService {

    private static final Logger logger = LoggerFactory.getLogger(EntityDiscoveryService.class);

    private final EntitySearchRepository entitySearchRepository;
    private final EntityMappingRepository entityMappingRepository;

    @Autowired
    public EntityDiscoveryService(
            EntityMappingRepository entityMappingRepository,
            EntitySearchRepository entitySearchRepository) {
        this.entityMappingRepository = entityMappingRepository;
        this.entitySearchRepository = entitySearchRepository;
    }

    // Fetch all the entities 
    public Page<Entity> getAllEntity(EntityStatus status, Pageable pageable) {
        return entitySearchRepository.findAllByStatus(status, pageable);
    }

     // Fetch entity based on entityId
    public Optional<Entity> getEntityById(UUID entityId) {
        try {
            return entitySearchRepository.findById(entityId);
        } catch (Exception e) {
            logger.error("Error fetching Entity by ID: {}", entityId, e);
            throw new RuntimeException("Error fetching Entity by ID", e);
        }
    }

      // Fetch entity id based on needAdminId
    public Page<EntityMapping> getEntityByNeedAdminId(String needAdminId, Pageable pageable) {
        try {
            return entityMappingRepository.findAllByNeedAdminId(needAdminId, pageable);
        } catch (Exception e) {
            logger.error("Error fetching EntityMapping by NeedAdminId: {}", needAdminId, e);
            throw new RuntimeException("Error fetching EntityMapping by needAdminId", e);
        }
    }

   
}
