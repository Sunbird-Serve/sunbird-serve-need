// NeedDiscoveryController.java
package com.sunbird.serve.need;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.sunbird.serve.need.models.Need.Need;
import com.sunbird.serve.need.models.enums.NeedStatus;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;
import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

@RestController
public class NeedDiscoveryController {

    private static final Logger logger = LoggerFactory.getLogger(NeedDiscoveryController.class);

    private final NeedDiscoveryService needDiscoveryService;

    public NeedDiscoveryController(NeedDiscoveryService needDiscoveryService) {
        this.needDiscoveryService = needDiscoveryService;
    }

    //Fetch all needs based on needId
    @Operation(summary = "Fetch a Need by providing NeedId", description = "Fetch a Need by providing NeedId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Fetched Need", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @GetMapping("/serve-need/need/{needId}")
    public ResponseEntity<Need> getNeedById(@PathVariable String needId) {
        Optional<Need> need = needDiscoveryService.getNeedById(UUID.fromString(needId));
        return need.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //Fetch all needs based on its status
    @Operation(summary = "Fetch Needs based on Status", description = "Fetch need details based on its status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Fetched Needs", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @GetMapping("/serve-need/need/status/{status}")
    public ResponseEntity<Page<Need>> getNeedsByStatus(
            @PathVariable NeedStatus status,
            @RequestParam(defaultValue = "0") @Parameter(description = "Page number (default: 0)") int page,
            @RequestParam(defaultValue = "10") @Parameter(description = "Page size (default: 10)") int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Need> needsByStatus = needDiscoveryService.getNeedsByStatus(status, pageable);
        return ResponseEntity.ok(needsByStatus);
    }

    //Fetch all need by providing User Id of nCoord or Need Type Id
    @Operation(summary = "Fetch all Needs by providing UserId of nCoordinator or Need Type or all the needs. ", description = "Fetch a Need by providing NeedId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Fetched Need", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @GetMapping("/serve-need/need/read")
public ResponseEntity<Page<Need>> getAllNeeds(
        @RequestParam(defaultValue = "0") @Parameter(description = "Page number (default: 0)") int page,
        @RequestParam(defaultValue = "10") @Parameter(description = "Page size (default: 10)") int size,
        @RequestParam(required = false) @Parameter(description = "User ID") String userId,
        @RequestParam(required = false) @Parameter(description = "Need Type ID") String needTypeId) {

    Pageable pageable = PageRequest.of(page, size);
    Page<Need> needs;

    if (userId != null && needTypeId != null) {
        // Fetch needs based on both userId and needTypeId
        needs = needDiscoveryService.getNeedByUserIdAndNeedTypeId(userId, needTypeId, pageable);
    } else if (userId != null) {
        // Fetch needs based on userId
        needs = needDiscoveryService.getNeedByUserId(userId, pageable);
    } else if (needTypeId != null) {
        // Fetch needs based on needTypeId
        needs = needDiscoveryService.getNeedByNeedTypeId(needTypeId, pageable);
    } else {
        // Fetch all needs if no specific parameters are provided
        needs = needDiscoveryService.getAllNeeds(pageable);
    }

    return ResponseEntity.ok(needs);
}



}
