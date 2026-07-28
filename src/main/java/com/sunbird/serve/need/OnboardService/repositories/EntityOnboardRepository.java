package com.sunbird.serve.need.OnboardService.repositories;

import com.sunbird.serve.need.models.Need.EntityOnboard;
import com.sunbird.serve.need.models.enums.OnboardRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EntityOnboardRepository extends JpaRepository<EntityOnboard, UUID> {

    Page<EntityOnboard> findAllByAgencyId(String agencyId, Pageable pageable);

    Page<EntityOnboard> findAllByAgencyIdAndStatus(String agencyId, OnboardRequestStatus status, Pageable pageable);

    Optional<EntityOnboard> findByMobileAndEntityIdAndStatusIn(String mobile, UUID entityId, List<OnboardRequestStatus> statuses);

    List<EntityOnboard> findAllByMobileOrderByCreatedAtDesc(String mobile);

    List<EntityOnboard> findAllByEmailOrderByCreatedAtDesc(String email);
}
