package com.sunbird.serve.need;

import com.sunbird.serve.need.models.Need.Need;
import com.sunbird.serve.need.models.Need.Entity;
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

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface EntitySearchRepository extends JpaRepository<Entity, UUID> {

    Page<Entity> findAllByStatus(EntityStatus status, Pageable pageable);


    @Query("SELECT e FROM Entity e WHERE e.id IN (SELECT em.entityId FROM EntityMapping em WHERE em.needAdminId = :needAdminId)")
    Page<Entity> findEntitiesByNeedAdminId(@Param("needAdminId") String needAdminId, Pageable pageable);
}
