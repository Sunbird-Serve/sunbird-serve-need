package com.sunbird.serve.need;

import com.sunbird.serve.need.models.Need.Need;
import com.sunbird.serve.need.models.request.RaiseNeedRequest;
import com.sunbird.serve.need.models.request.NeedRequest;
import com.sunbird.serve.need.models.response.NeedResult;
import com.sunbird.serve.need.models.enums.NeedStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;

import java.util.UUID;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class NeedController {

    private final RaiseNeedService raiseNeedService;

    @Autowired
    public NeedController(RaiseNeedService raiseNeedService) {
        this.raiseNeedService = raiseNeedService;
    }

    @Operation(summary = "Raise a Need by filling in request body", description = "Initiate the process of raising a new Need")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Raised Need", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "403", description = "Access Denied"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @PreAuthorize("hasAnyRole('sAdmin', 'nAdmin', 'nCoordinator')")
    @PostMapping("/need/raise")
    public ResponseEntity<Need> raiseNeed(@RequestBody RaiseNeedRequest request,
                                          @RequestHeader Map<String, String> headers) {
        Need response = raiseNeedService.raiseNeed(request, headers);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update a Need with appropriate values", description = "Update an existing need")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Updated Need", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "403", description = "Access Denied"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @PreAuthorize("hasAnyRole('sAdmin', 'nAdmin', 'nCoordinator')")
    @PutMapping("/need/update/{needId}")
    public ResponseEntity<Need> updateNeed(
            @PathVariable UUID needId,
            @RequestBody RaiseNeedRequest request,
            @RequestHeader Map<String, String> headers) {
        Need updatedNeed = raiseNeedService.updateNeed(needId, request, headers);
        return ResponseEntity.ok(updatedNeed);
    }

    @Operation(summary = "Update a Need Status", description = "Update need status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Updated Need Status", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "403", description = "Access Denied"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @PreAuthorize("hasAnyRole('sAdmin', 'nAdmin', 'nCoordinator')")
    @PutMapping("/need/status/{needId}")
    public ResponseEntity<Need> updateNeedStatus(
            @PathVariable UUID needId,
            @RequestParam(required = true) NeedStatus status,
            @RequestHeader Map<String, String> headers) {
        Need updatedNeed = raiseNeedService.updateNeedStatus(needId, status, headers);
        return ResponseEntity.ok(updatedNeed);
    }
}
