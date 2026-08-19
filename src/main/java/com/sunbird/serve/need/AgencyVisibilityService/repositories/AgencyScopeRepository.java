package com.sunbird.serve.need.AgencyVisibilityService.repositories;

import com.sunbird.serve.need.models.Need.AgencyScope;
import com.sunbird.serve.need.models.enums.VisibilityScope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgencyScopeRepository extends JpaRepository<AgencyScope, String> {

    // Find all agencies that have need_visibility = 'all' (their needs are open to everyone)
    List<AgencyScope> findAllByNeedVisibility(VisibilityScope needVisibility);

    // Find all agencies whose needs are visible to a specific agency
    // i.e., need_visibility = 'selected' AND the given agencyId is in selected_need_agencies
    @Query(value = "SELECT * FROM agency_scope WHERE need_visibility = 'selected' " +
                   "AND selected_need_agencies @> to_jsonb(:agencyId)::jsonb",
           nativeQuery = true)
    List<AgencyScope> findAgenciesWithNeedsVisibleTo(@Param("agencyId") String agencyId);
}
