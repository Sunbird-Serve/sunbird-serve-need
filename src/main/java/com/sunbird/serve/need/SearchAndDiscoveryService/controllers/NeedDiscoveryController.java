// NeedDiscoveryController.java
package com.sunbird.serve.need;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.sunbird.serve.need.models.Need.Need;
import com.sunbird.serve.need.models.Need.Entity;
import com.sunbird.serve.need.models.enums.NeedStatus;
import com.sunbird.serve.need.models.enums.EntityStatus;
import com.sunbird.serve.need.models.response.NeedEntityAndRequirement;
import com.sunbird.serve.need.models.request.EntityListRequest;
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
import org.springframework.web.bind.annotation.CrossOrigin;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import java.util.ArrayList;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
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
    @GetMapping("/need/{needId}")
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
    @GetMapping("/need/")
    public ResponseEntity<Page<NeedEntityAndRequirement>> getNeedsByStatus(
            @RequestParam(defaultValue = "0") @Parameter(description = "Page number (default: 0)") int page,
            @RequestParam(defaultValue = "10") @Parameter(description = "Page size (default: 10)") int size, 
            @RequestParam NeedStatus status){
        Pageable pageable = PageRequest.of(page, size);
        Page<NeedEntityAndRequirement> needsByStatus = needDiscoveryService.getNeedsByStatus(status, pageable);
        return ResponseEntity.ok(needsByStatus);
    }

    //Fetch all need by providing Need Type Id
    @Operation(summary = "Fetch all Needs by providing Need Type ", description = "Fetch a Need by providing NeedTypeId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Fetched Need", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @GetMapping("/need/need-type/{needTypeId}")
public ResponseEntity<Page<Need>> getAllNeeds(
        @PathVariable(required = true) @Parameter(description = "Need Type ID") String needTypeId,
            @RequestParam(required = false, defaultValue = "0")  Integer page,
            @RequestParam(required = false, defaultValue = "10")  Integer size,
            @RequestParam(required = true)  NeedStatus status) {

    Pageable pageable = PageRequest.of(page, size);
    Page<Need> needs;

    // Fetch needs based on needTypeId
        needs = needDiscoveryService.getNeedByNeedTypeId(needTypeId, pageable);

    return ResponseEntity.ok(needs);
}



    //Fetch all need by providing User Id of nCoord
        @Operation(summary = "Fetch all Needs by providing UserId of nCoordinator ", description = "Fetch a Need by providing UserId")
        @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully Fetched Need", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(responseCode = "400", description = "Bad Input"),
        @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @GetMapping("/need/user/{userId}")
    public ResponseEntity<Page<Need>> getAllNeedsByUserId(
        @PathVariable(required = true) @Parameter(description = "User ID") String userId,
        @RequestParam(defaultValue = "0") @Parameter(description = "Page number (default: 0)") int page,
        @RequestParam(defaultValue = "10") @Parameter(description = "Page size (default: 10)") int size, 
        @RequestParam(required = true) NeedStatus status) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Need> needs;


        // Fetch needs based on userId
        needs = needDiscoveryService.getNeedByUserIdAndStatus(userId, status, pageable);
    
        return ResponseEntity.ok(needs);
}

//Fetch all need by providing Entity Id
        @Operation(summary = "Fetch all Needs by providing Entity Id ", description = "Fetch a Need by providing Entity Id")
        @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully Fetched Need", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(responseCode = "400", description = "Bad Input"),
        @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @GetMapping("/need/entity/{entityId}")
    public ResponseEntity<Page<Need>> getAllNeedsByEntityId(
        @PathVariable(required = true) @Parameter(description = "Entity ID") String entityId,
        @RequestParam(defaultValue = "0") @Parameter(description = "Page number (default: 0)") int page,
        @RequestParam(defaultValue = "10") @Parameter(description = "Page size (default: 10)") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Need> needs;


        // Fetch needs based on userId
        needs = needDiscoveryService.getNeedByEntityId(entityId, pageable);
    
        return ResponseEntity.ok(needs);
}

@Operation(summary = "Fetch Needs by Entity Ids", description = "Fetch Needs based on list of Entity Ids")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully Fetched Needs", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(responseCode = "400", description = "Bad Input"),
        @ApiResponse(responseCode = "500", description = "Server Error")})
@PostMapping("/need/entities")
public ResponseEntity<Page<Need>> getAllNeedsByEntityIds(
        @RequestBody EntityListRequest entityIdsRequest, // Using the new request class
        @RequestParam(defaultValue = "0") @Parameter(description = "Page number (default: 0)") int page,
        @RequestParam(defaultValue = "10") @Parameter(description = "Page size (default: 10)") int size) {

    // Extract entityIds from the request body
    List<String> entityIds = entityIdsRequest.getEntityIds();

    // Log received entityIds
    logger.info("Received entityIds: {}", entityIds);

    // Pageable for pagination
    Pageable pageable = PageRequest.of(page, size);

    // Fetch needs based on the list of entityIds
    Page<Need> needs = needDiscoveryService.getNeedByEntityIds(entityIds, pageable);

    return ResponseEntity.ok(needs);
}




}
