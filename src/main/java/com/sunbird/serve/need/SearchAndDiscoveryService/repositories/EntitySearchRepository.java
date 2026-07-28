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

    // Filtering for onboarding entity browse (native query to avoid type casting issues)
    @Query(value = "SELECT * FROM entity e WHERE " +
           "(:agencyId IS NULL OR e.agency_id = :agencyId) AND " +
           "(:district IS NULL OR e.district = :district) AND " +
           "(:block IS NULL OR e.block = :block) AND " +
           "(:state IS NULL OR e.state = :state) AND " +
           "(:name IS NULL OR LOWER(CAST(e.name AS TEXT)) LIKE LOWER(CONCAT('%', :name, '%')))",
           countQuery = "SELECT COUNT(*) FROM entity e WHERE " +
           "(:agencyId IS NULL OR e.agency_id = :agencyId) AND " +
           "(:district IS NULL OR e.district = :district) AND " +
           "(:block IS NULL OR e.block = :block) AND " +
           "(:state IS NULL OR e.state = :state) AND " +
           "(:name IS NULL OR LOWER(CAST(e.name AS TEXT)) LIKE LOWER(CONCAT('%', :name, '%')))",
           nativeQuery = true)
    Page<NeedEntity> findEntitiesForOnboarding(
            @Param("agencyId") String agencyId,
            @Param("district") String district,
            @Param("block") String block,
            @Param("state") String state,
            @Param("name") String name,
            Pageable pageable);
}
