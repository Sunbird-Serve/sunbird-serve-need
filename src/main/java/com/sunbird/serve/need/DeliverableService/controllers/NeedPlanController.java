package com.sunbird.serve.need;

import com.sunbird.serve.need.models.Need.NeedPlan;
import com.sunbird.serve.need.models.enums.NeedStatus;
import com.sunbird.serve.need.models.request.NeedPlanRequest;
import com.sunbird.serve.need.models.response.NeedPlanResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
public class NeedPlanController {

    private static final Logger logger = LoggerFactory.getLogger(NeedPlanController.class);

    private final NeedPlanService needPlanService;

    public NeedPlanController(NeedPlanService needPlanService) {
        this.needPlanService = needPlanService;
    }

    @Operation(summary = "Fetch Need Plan by providing NeedId", description = "Fetch Need Plan by providing NeedId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Fetched Need Plan", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/need-plan/{needId}")
    public ResponseEntity<List<NeedPlanResponse>> getByNeedId(@PathVariable String needId) {
        List<NeedPlanResponse> needPlan = needPlanService.getByNeedId(needId);
        if (!needPlan.isEmpty()) {
            return ResponseEntity.ok(needPlan);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Create Need Plan by filling in request body", description = "Initiate the process of creating a Need Plan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Created Need Plan", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "403", description = "Access Denied"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @PreAuthorize("hasAnyRole('sAdmin', 'nAdmin', 'nCoordinator')")
    @PostMapping("/need-plan/create")
    public ResponseEntity<NeedPlan> createNeedPlan(@RequestBody NeedPlanRequest request,
                                                   @RequestHeader Map<String, String> headers) {
        NeedPlan response = needPlanService.createNeedPlan(request, headers);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Fetch a Need Plan by providing NeedPlanId", description = "Fetch a NeedPlan by providing NeedPlanId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Fetched Need Plan", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/need-plan/read/{needPlanId}")
    public ResponseEntity<NeedPlanResponse> getNeedPlanById(@PathVariable String needPlanId) {
        Optional<NeedPlanResponse> needPlan = needPlanService.getNeedPlanById(UUID.fromString(needPlanId));
        return needPlan.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update Need Plan Status", description = "Update the status of a Need Plan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Updated Need Plan Status", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "403", description = "Access Denied"),
            @ApiResponse(responseCode = "404", description = "Need Plan Not Found"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @PreAuthorize("hasAnyRole('sAdmin', 'nAdmin', 'nCoordinator')")
    @PutMapping("/need-plan/status/{needPlanId}")
    public ResponseEntity<NeedPlan> updateNeedPlanStatus(
            @PathVariable UUID needPlanId,
            @RequestParam(required = true) NeedStatus status,
            @RequestHeader Map<String, String> headers) {
        NeedPlan updatedPlan = needPlanService.updateNeedPlanStatus(needPlanId, status, headers);
        return ResponseEntity.ok(updatedPlan);
    }
}
