package com.sunbird.serve.need;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sunbird.serve.need.models.Need.*;
import com.sunbird.serve.need.models.enums.NeedStatus;
import com.sunbird.serve.need.models.enums.EntityStatus;
import com.sunbird.serve.need.models.enums.UserRole;
import com.sunbird.serve.need.models.request.EntityRequest;
import com.sunbird.serve.need.models.request.EntityMappingRequest;
import com.sunbird.serve.need.models.response.NeedEntityAndRequirement;
import com.sunbird.serve.need.models.Need.NeedRequirement;
import org.springframework.web.bind.annotation.*;



import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.Map;
import java.time.Instant;
import org.springframework.data.domain.Page; 
import org.springframework.data.domain.Pageable;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;


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

    // Fetch all the entities 
    public Page<Entity> getAllEntities(Pageable pageable) {
        return entitySearchRepository.findAll(pageable);
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

    
    public Page<Entity> getEntitiesByUserId(String userId, Pageable pageable) {
    try {
        return entitySearchRepository.findEntitiesByUserId(userId, pageable);
    } catch (Exception e) {
        logger.error("Error fetching Entities by UserId: {}", userId, e);
        throw new RuntimeException("Error fetching Entities by UserId", e);
    }
}

public Page<EntityMapping> getUsersByEntityId(UUID entityId, Pageable pageable) {
    try {
        return entityMappingRepository.findUsersByEntityId(entityId, pageable);
    } catch (Exception e) {
        logger.error("Error fetching Entities by entityId: {}", entityId, e);
        throw new RuntimeException("Error fetching Entities by entityId", e);
    }
}

// Fetch all needs based on Need Admin ID
    public Page<Need> getNeedsByUserId(String userId, Pageable pageable) {
        try {
            // Fetch entities associated with the needAdminId
            List<Entity> entities = entitySearchRepository.findEntitiesByUserId(userId, pageable).getContent();
            
            // Extract entity IDs
            List<String> entityIds = entities.stream()
    .map(entity -> entity.getId().toString()) // Convert UUID to String
    .collect(Collectors.toList());

            
            // Fetch needs associated with the retrieved entity IDs
            return needDiscoveryRepository.findAllByEntityIds(entityIds, pageable);
        } catch (Exception e) {
            logger.error("Error fetching Needs by UserId: {}", userId, e);
            throw new RuntimeException("Error fetching Needs by UserId", e);
        }
    }

    public Entity createEntity(EntityRequest request, Map<String, String> headers) {
        // Convert EntityRequest to Entity
        Entity entity = new Entity();
        entity.setName(request.getName());
        entity.setWebsite(request.getWebsite());
        entity.setAddress_line1(request.getAddress_line1());
        entity.setMobile(request.getMobile());
        entity.setDistrict(request.getDistrict());
        entity.setState(request.getState());
        entity.setCategory(request.getCategory());
        entity.setStatus(request.getStatus());
       
        
        // Additional logic for processing headers if needed
        
        // Save entity to repository
        return entitySearchRepository.save(entity);
    }



public Entity editEntity(UUID id, EntityRequest request, Map<String, String> headers) {
        return entitySearchRepository.findById(id)
            .map(entity -> {
                if (request.getName() != null) entity.setName(request.getName());
                if (request.getWebsite() != null) entity.setWebsite(request.getWebsite());
                if (request.getAddress_line1() != null) entity.setAddress_line1(request.getAddress_line1());
                if (request.getMobile() != null) entity.setMobile(request.getMobile());
                if (request.getDistrict() != null) entity.setDistrict(request.getDistrict());
                if (request.getState() != null) entity.setState(request.getState());
                if (request.getCategory() != null) entity.setCategory(request.getCategory());
                if (request.getStatus() != null) entity.setStatus(request.getStatus());

                entity.setUpdatedAt(Instant.now()); // Always update timestamp
                return entitySearchRepository.save(entity);
            })
            .orElseThrow(() -> new EntityNotFoundException("Entity not found with id: " + id));
    }



    public EntityMapping assignEntity(EntityMappingRequest request, Map<String, String> headers) {
        // Convert EntityRequest to Entity
        EntityMapping entityMapping = new EntityMapping();
        entityMapping.setEntityId(request.getEntityId());
        entityMapping.setUserId(request.getUserId());
        entityMapping.setUserRole(UserRole.valueOf(request.getUserRole()));
       
        
        // Additional logic for processing headers if needed
        
        // Save entity to repository
        return entityMappingRepository.save(entityMapping);
    }

    public EntityMapping editAssignedEntity(UUID id, EntityMappingRequest request, Map<String, String> headers) {
        return entityMappingRepository.findById(id)
            .map(entityMapping -> {
                if (request.getEntityId() != null) entityMapping.setEntityId(request.getEntityId());
                if (request.getUserId() != null) entityMapping.setUserId(request.getUserId());
                if (request.getUserRole() != null) entityMapping.setUserRole(UserRole.valueOf(request.getUserRole()));
                
                entityMapping.setUpdatedAt(Instant.now()); // Always update timestamp
                return entityMappingRepository.save(entityMapping);
            })
            .orElseThrow(() -> new EntityNotFoundException("Entity Mapping not found with id: " + id));
    }
   
}
