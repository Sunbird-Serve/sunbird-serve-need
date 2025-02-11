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

    
    public Page<Entity> getEntitiesByNeedAdminId(String needAdminId, Pageable pageable) {
    try {
        return entitySearchRepository.findEntitiesByNeedAdminId(needAdminId, pageable);
    } catch (Exception e) {
        logger.error("Error fetching Entities by NeedAdminId: {}", needAdminId, e);
        throw new RuntimeException("Error fetching Entities by NeedAdminId", e);
    }
}

// Fetch all needs based on Need Admin ID
    public Page<Need> getNeedsByNeedAdminId(String needAdminId, Pageable pageable) {
        try {
            // Fetch entities associated with the needAdminId
            List<Entity> entities = entitySearchRepository.findEntitiesByNeedAdminId(needAdminId, pageable).getContent();
            
            // Extract entity IDs
            List<String> entityIds = entities.stream()
    .map(entity -> entity.getId().toString()) // Convert UUID to String
    .collect(Collectors.toList());

            
            // Fetch needs associated with the retrieved entity IDs
            return needDiscoveryRepository.findAllByEntityIds(entityIds, pageable);
        } catch (Exception e) {
            logger.error("Error fetching Needs by NeedAdminId: {}", needAdminId, e);
            throw new RuntimeException("Error fetching Needs by NeedAdminId", e);
        }
    }

   
}
