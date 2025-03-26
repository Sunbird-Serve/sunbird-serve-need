package com.sunbird.serve.need;

import com.sunbird.serve.need.models.Need.Need;
import com.sunbird.serve.need.models.Need.EntityMapping;
import com.sunbird.serve.need.models.enums.EntityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;
import org.springframework.data.domain.Page; 
import org.springframework.data.domain.Pageable;
import java.util.Optional;

@Repository
public interface EntityMappingRepository extends JpaRepository<EntityMapping, UUID> {

    Page<EntityMapping> findAllByUserId(String needAdminId, Pageable pageable);
    Page<EntityMapping> findUsersByEntityId(UUID entityId, Pageable pageable);

    
}
