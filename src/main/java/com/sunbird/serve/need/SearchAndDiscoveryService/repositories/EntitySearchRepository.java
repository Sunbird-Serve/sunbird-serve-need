package com.sunbird.serve.need;

import com.sunbird.serve.need.models.Need.NeedEntity;
import com.sunbird.serve.need.models.enums.EntityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface EntitySearchRepository extends JpaRepository<NeedEntity, UUID> {

    Page<NeedEntity> findAllByStatus(EntityStatus status, Pageable pageable);

    Page<NeedEntity> findAll(Pageable pageable);

    Page<NeedEntity> findAllByAgencyId(String agencyId, Pageable pageable);

    Page<NeedEntity> findAllByAgencyIdAndStatus(String agencyId, EntityStatus status, Pageable pageable);

    @Query("SELECT e FROM NeedEntity e WHERE e.id IN (SELECT um.orgId FROM UserMapping um WHERE um.userId = :userId)")
    Page<NeedEntity> findEntitiesByUserId(@Param("userId") String userId, Pageable pageable);

    @Query("SELECT e FROM NeedEntity e WHERE e.agencyId = :agencyId AND e.id IN (SELECT um.orgId FROM UserMapping um WHERE um.userId = :userId)")
    Page<NeedEntity> findEntitiesByAgencyIdAndUserId(@Param("agencyId") String agencyId, @Param("userId") String userId, Pageable pageable);
}
