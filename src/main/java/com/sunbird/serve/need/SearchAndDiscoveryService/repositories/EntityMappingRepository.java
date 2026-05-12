package com.sunbird.serve.need;

import com.sunbird.serve.need.models.Need.UserMapping;
import com.sunbird.serve.need.models.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EntityMappingRepository extends JpaRepository<UserMapping, UUID> {

    Page<UserMapping> findAllByUserId(String needAdminId, Pageable pageable);
    Page<UserMapping> findUsersByOrgId(UUID orgId, Pageable pageable);
    Page<UserMapping> findAllByAgencyId(String agencyId, Pageable pageable);
    Page<UserMapping> findAllByAgencyIdAndUserRole(String agencyId, UserRole userRole, Pageable pageable);
}
