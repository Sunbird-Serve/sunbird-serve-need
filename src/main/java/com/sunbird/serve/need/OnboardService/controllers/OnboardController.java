package com.sunbird.serve.need.OnboardService.controllers;

import com.sunbird.serve.need.OnboardService.services.OnboardService;
import com.sunbird.serve.need.config.TenantContext;
import com.sunbird.serve.need.models.Need.EntityOnboard;
import com.sunbird.serve.need.models.Need.NeedEntity;
import com.sunbird.serve.need.models.enums.OnboardRequestStatus;
import com.sunbird.serve.need.models.request.OnboardRequest;
import com.sunbird.serve.need.models.request.OnboardReviewRequest;
import com.sunbird.serve.need.models.response.OnboardStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/entity-onboard")
public class OnboardController {

    private static final Logger logger = LoggerFactory.getLogger(OnboardController.class);

    private final OnboardService onboardService;

    public OnboardController(OnboardService onboardService) {
        this.onboardService = onboardService;
    }

    // === PUBLIC ENDPOINTS (no auth required) ===

    @Operation(summary = "Submit coordinator onboarding request",
            description = "Public endpoint. A prospective coordinator submits their details and selected entity.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Onboarding request created", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input or duplicate request"),
            @ApiResponse(responseCode = "404", description = "Entity not found"),
            @ApiResponse(responseCode = "500", description = "Server Error")})
    @PostMapping("/request")
    public ResponseEntity<EntityOnboard> submitOnboardRequest(@RequestBody OnboardRequest request) {
        try {
            EntityOnboard response = onboardService.submitOnboardRequest(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            logger.error("Error submitting onboard request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Browse preloaded entities for onboarding form",
            description = "Public endpoint. Returns entities that can be selected during onboarding.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched entities", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Server Error")})
    @GetMapping("/entities")
    public ResponseEntity<Page<NeedEntity>> getEntitiesForOnboarding(
            @RequestParam(required = false) @Parameter(description = "Agency ID") String agencyId,
            @RequestParam(required = false) @Parameter(description = "Filter by district") String district,
            @RequestParam(required = false) @Parameter(description = "Filter by block") String block,
            @RequestParam(required = false) @Parameter(description = "Filter by state") String state,
            @RequestParam(required = false) @Parameter(description = "Filter by name (partial)") String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<NeedEntity> entities = onboardService.getEntitiesForOnboarding(agencyId, district, block, state, name, pageable);
        return ResponseEntity.ok(entities);
    }

    @Operation(summary = "Check onboarding request status",
            description = "Public endpoint. Coordinator checks their request status by mobile or email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status retrieved", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Neither mobile nor email provided"),
            @ApiResponse(responseCode = "500", description = "Server Error")})
    @GetMapping("/status")
    public ResponseEntity<List<OnboardStatusResponse>> getOnboardStatus(
            @RequestParam(required = false) @Parameter(description = "Mobile number") String mobile,
            @RequestParam(required = false) @Parameter(description = "Email address") String email) {
        try {
            List<OnboardStatusResponse> responses = onboardService.getOnboardStatus(mobile, email);
            return ResponseEntity.ok(responses);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // === ADMIN ENDPOINTS (nAdmin / sAdmin) ===

    @Operation(summary = "List onboarding requests for review",
            description = "nAdmin/sAdmin can view all onboarding requests for their agency.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched requests", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "403", description = "Access Denied"),
            @ApiResponse(responseCode = "500", description = "Server Error")})
    @PreAuthorize("hasAnyRole('sAdmin', 'nAdmin')")
    @GetMapping("/requests")
    public ResponseEntity<Page<EntityOnboard>> getOnboardRequests(
            @RequestParam(required = false) @Parameter(description = "Filter by status") OnboardRequestStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String agencyId = TenantContext.getAgencyId();
        Pageable pageable = PageRequest.of(page, size);
        Page<EntityOnboard> requests = onboardService.getOnboardRequests(agencyId, status, pageable);
        return ResponseEntity.ok(requests);
    }

    @Operation(summary = "Get single onboarding request detail",
            description = "nAdmin/sAdmin can view full details of a specific onboarding request.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "404", description = "Request not found"),
            @ApiResponse(responseCode = "403", description = "Access Denied"),
            @ApiResponse(responseCode = "500", description = "Server Error")})
    @PreAuthorize("hasAnyRole('sAdmin', 'nAdmin')")
    @GetMapping("/requests/{requestId}")
    public ResponseEntity<EntityOnboard> getOnboardRequestById(@PathVariable UUID requestId) {
        try {
            EntityOnboard request = onboardService.getOnboardRequestById(requestId);
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Review onboarding request (Authorise / Clarify / Reject)",
            description = "nAdmin reviews the request and takes action. On Authorise, the system provisions the coordinator.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review action applied", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Invalid action"),
            @ApiResponse(responseCode = "403", description = "Access Denied"),
            @ApiResponse(responseCode = "404", description = "Request not found"),
            @ApiResponse(responseCode = "500", description = "Server Error")})
    @PreAuthorize("hasAnyRole('sAdmin', 'nAdmin')")
    @PutMapping("/requests/{requestId}/review")
    public ResponseEntity<EntityOnboard> reviewOnboardRequest(
            @PathVariable UUID requestId,
            @RequestBody OnboardReviewRequest reviewRequest) {
        try {
            EntityOnboard result = onboardService.reviewOnboardRequest(requestId, reviewRequest);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error reviewing onboard request: " + requestId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
