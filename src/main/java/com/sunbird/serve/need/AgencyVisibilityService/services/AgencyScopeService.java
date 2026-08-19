package com.sunbird.serve.need.AgencyVisibilityService.services;

import com.sunbird.serve.need.AgencyVisibilityService.repositories.AgencyScopeRepository;
import com.sunbird.serve.need.models.Need.AgencyScope;
import com.sunbird.serve.need.models.enums.VisibilityScope;
import com.sunbird.serve.need.models.request.AgencyScopeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class AgencyScopeService {

    private static final Logger logger = LoggerFactory.getLogger(AgencyScopeService.class);

    private final AgencyScopeRepository agencyScopeRepository;

    @Autowired
    public AgencyScopeService(AgencyScopeRepository agencyScopeRepository) {
        this.agencyScopeRepository = agencyScopeRepository;
    }

    /**
     * Get the scope config for a specific agency.
     * Returns default (all/all) if no config exists.
     */
    public AgencyScope getScopeConfig(String agencyId) {
        return agencyScopeRepository.findById(agencyId)
                .orElse(AgencyScope.builder()
                        .agencyId(agencyId)
                        .needVisibility(VisibilityScope.all)
                        .selectedNeedAgencies(List.of())
                        .volunteerVisibility(VisibilityScope.all)
                        .selectedVolunteerAgencies(List.of())
                        .build());
    }

    /**
     * Create or update scope config for an agency.
     */
    public AgencyScope saveScopeConfig(String agencyId, AgencyScopeRequest request) {
        Optional<AgencyScope> existing = agencyScopeRepository.findById(agencyId);

        AgencyScope config;
        if (existing.isPresent()) {
            config = existing.get();
            if (request.getNeedVisibility() != null) config.setNeedVisibility(request.getNeedVisibility());
            if (request.getSelectedNeedAgencies() != null) config.setSelectedNeedAgencies(request.getSelectedNeedAgencies());
            if (request.getVolunteerVisibility() != null) config.setVolunteerVisibility(request.getVolunteerVisibility());
            if (request.getSelectedVolunteerAgencies() != null) config.setSelectedVolunteerAgencies(request.getSelectedVolunteerAgencies());
        } else {
            config = AgencyScope.builder()
                    .agencyId(agencyId)
                    .needVisibility(request.getNeedVisibility() != null ? request.getNeedVisibility() : VisibilityScope.all)
                    .selectedNeedAgencies(request.getSelectedNeedAgencies() != null ? request.getSelectedNeedAgencies() : List.of())
                    .volunteerVisibility(request.getVolunteerVisibility() != null ? request.getVolunteerVisibility() : VisibilityScope.all)
                    .selectedVolunteerAgencies(request.getSelectedVolunteerAgencies() != null ? request.getSelectedVolunteerAgencies() : List.of())
                    .build();
        }

        return agencyScopeRepository.save(config);
    }

    /**
     * Resolve which agency IDs' needs a volunteer from the given agency can discover.
     *
     * Logic:
     * 1. Check the volunteer's agency config → volunteerVisibility
     *    - 'all' → volunteer can see needs from any agency (subject to that agency allowing it)
     *    - 'selected' → volunteer can only see needs from selectedVolunteerAgencies
     *    - 'none' → volunteer can only see their own agency's needs
     *
     * 2. For each candidate agency, check its needVisibility:
     *    - 'all' → its needs are visible to everyone
     *    - 'selected' → its needs are visible only if volunteer's agency is in selectedNeedAgencies
     *    - 'none' → its needs are visible only to itself
     *
     * Returns: list of agencyIds whose needs the volunteer can see.
     *          null means "all agencies" (no filtering needed).
     */
    public List<String> resolveDiscoverableAgencies(String volunteerAgencyId) {
        try {
            AgencyScope volunteerConfig = getScopeConfig(volunteerAgencyId);

            // Step 1: Determine candidate agencies based on volunteer's config
            Set<String> candidateAgencies;

            switch (volunteerConfig.getVolunteerVisibility()) {
                case none:
                    // Volunteer can only see their own agency's needs
                    return List.of(volunteerAgencyId);

                case selected:
                    // Volunteer can see needs from selected agencies + own
                    candidateAgencies = new HashSet<>(volunteerConfig.getSelectedVolunteerAgencies());
                    candidateAgencies.add(volunteerAgencyId); // always include own
                    break;

                case all:
                default:
                    // Volunteer wants to see all — but we still need to check each agency's needVisibility
                    candidateAgencies = null; // null = all
                    break;
            }

            // Step 2: Filter candidates by each agency's needVisibility
            Set<String> allowedAgencies = new HashSet<>();
            allowedAgencies.add(volunteerAgencyId); // always see own needs

            // Agencies with needVisibility = 'all' → everyone can see their needs
            List<AgencyScope> openAgencies = agencyScopeRepository.findAllByNeedVisibility(VisibilityScope.all);
            for (AgencyScope a : openAgencies) {
                if (candidateAgencies == null || candidateAgencies.contains(a.getAgencyId())) {
                    allowedAgencies.add(a.getAgencyId());
                }
            }

            // Agencies with needVisibility = 'selected' AND our agency is in their list
            List<AgencyScope> selectiveAgencies = agencyScopeRepository.findAgenciesWithNeedsVisibleTo(volunteerAgencyId);
            for (AgencyScope a : selectiveAgencies) {
                if (candidateAgencies == null || candidateAgencies.contains(a.getAgencyId())) {
                    allowedAgencies.add(a.getAgencyId());
                }
            }

            if (candidateAgencies == null) {
                // Volunteer wants all, and agencies not in the table default to 'all'
                return null; // null = no filtering needed
            }

            return new ArrayList<>(allowedAgencies);

        } catch (Exception e) {
            logger.error("Error resolving discoverable agencies for: {}", volunteerAgencyId, e);
            // Fail open: return own agency only for safety
            return List.of(volunteerAgencyId);
        }
    }
}
