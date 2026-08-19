package com.sunbird.serve.need.AgencyVisibilityService.controllers;

import com.sunbird.serve.need.AgencyVisibilityService.services.AgencyScopeService;
import com.sunbird.serve.need.config.TenantContext;
import com.sunbird.serve.need.models.Need.AgencyScope;
import com.sunbird.serve.need.models.request.AgencyScopeRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/agency-scope")
public class AgencyScopeController {

    private final AgencyScopeService agencyScopeService;

    public AgencyScopeController(AgencyScopeService agencyScopeService) {
        this.agencyScopeService = agencyScopeService;
    }

    @Operation(summary = "Get scope config for an agency",
               description = "Returns the need/volunteer scope configuration for the specified agency. Defaults to 'all' if not configured.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched scope config", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Server Error")})
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{agencyId}")
    public ResponseEntity<AgencyScope> getScopeConfig(
            @PathVariable @Parameter(description = "Agency ID") String agencyId) {
        AgencyScope config = agencyScopeService.getScopeConfig(agencyId);
        return ResponseEntity.ok(config);
    }

    @Operation(summary = "Get scope config for current agency",
               description = "Returns the scope config for the authenticated user's agency (from JWT).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched scope config", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "No agency context available"),
            @ApiResponse(responseCode = "500", description = "Server Error")})
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<AgencyScope> getMyScopeConfig() {
        String agencyId = TenantContext.getAgencyId();
        if (agencyId == null || agencyId.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        AgencyScope config = agencyScopeService.getScopeConfig(agencyId);
        return ResponseEntity.ok(config);
    }

    @Operation(summary = "Create or update scope config",
               description = "Sets the need/volunteer scope rules for the current agency. Only nAdmin or sAdmin can modify.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully saved scope config", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "500", description = "Server Error")})
    @PreAuthorize("hasAnyRole('sAdmin', 'nAdmin')")
    @PostMapping
    public ResponseEntity<AgencyScope> saveScopeConfig(@RequestBody AgencyScopeRequest request) {
        String agencyId = TenantContext.getAgencyId();
        if (agencyId == null || agencyId.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        AgencyScope saved = agencyScopeService.saveScopeConfig(agencyId, request);
        return ResponseEntity.ok(saved);
    }

    @Operation(summary = "Resolve discoverable agencies for a volunteer",
               description = "Returns the list of agency IDs whose needs the current volunteer can discover. Null means all.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully resolved discoverable agencies", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "No agency context"),
            @ApiResponse(responseCode = "500", description = "Server Error")})
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/discoverable")
    public ResponseEntity<List<String>> getDiscoverableAgencies() {
        String agencyId = TenantContext.getAgencyId();
        if (agencyId == null || agencyId.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        List<String> agencies = agencyScopeService.resolveDiscoverableAgencies(agencyId);
        return ResponseEntity.ok(agencies);
    }
}
