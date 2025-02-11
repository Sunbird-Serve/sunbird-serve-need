// NeedDiscoveryController.java
package com.sunbird.serve.need;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.sunbird.serve.need.models.Need.Need;
import com.sunbird.serve.need.models.Need.Entity;
import com.sunbird.serve.need.models.Need.EntityMapping;
import com.sunbird.serve.need.models.enums.NeedStatus;
import com.sunbird.serve.need.models.enums.EntityStatus;
import com.sunbird.serve.need.models.response.NeedEntityAndRequirement;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
public class EntityDiscoveryController {

    private static final Logger logger = LoggerFactory.getLogger(EntityDiscoveryController.class);

    private final EntityDiscoveryService entityDiscoveryService;

    public EntityDiscoveryController(EntityDiscoveryService entityDiscoveryService) {
        this.entityDiscoveryService = entityDiscoveryService;
    }

    

    //Fetch all entities
    @Operation(summary = "Fetch all entities", description = "Fetch all entities")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Fetched Entities", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @GetMapping("/entity/")
    public ResponseEntity<Page<Entity>> getAllEntity(
         @RequestParam(defaultValue = "0") @Parameter(description = "Page number (default: 0)") int page,
            @RequestParam(defaultValue = "10") @Parameter(description = "Page size (default: 10)") int size, 
            @RequestParam EntityStatus status)
    {
        Pageable pageable = PageRequest.of(page, size);
        Page<Entity> allEntity = entityDiscoveryService.getAllEntity(status, pageable);
        return ResponseEntity.ok(allEntity);
    }

    //Fetch entity by ID
    @Operation(summary = "Fetch entity by ID", description = "Fetch entity by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Fetched Entity", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @GetMapping("/entity/{entityId}")
    public ResponseEntity<Entity> getEntityById(
         @PathVariable UUID entityId)
    {
        Optional<Entity> entity = entityDiscoveryService.getEntityById(entityId);
        return entity.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


//Fetch entity details by providing Need Admin ID
    @Operation(summary = "Fetch all Entity Id by providing Need Admin Id ", description = "Fetch all Entity Id by providing Need Admin Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Fetched Entity Id", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @GetMapping("/entityDetails/{needAdminId}")
public ResponseEntity<Page<Entity>> getAllEntityDetails(
        @PathVariable(required = true) @Parameter(description = "Need Admin ID") String needAdminId,
            @RequestParam(required = false, defaultValue = "0")  Integer page,
            @RequestParam(required = false, defaultValue = "10")  Integer size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<Entity> entity;

    // Fetch entity id based on needAdminId
        entity = entityDiscoveryService.getEntitiesByNeedAdminId(needAdminId, pageable);

    return ResponseEntity.ok(entity);
}

// Fetch all needs based on needAdminId
    @Operation(summary = "Fetch all needs by Need Admin ID", description = "Fetch all needs by Need Admin ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched needs", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @GetMapping("/needs/{needAdminId}")
    public ResponseEntity<Page<Need>> getAllNeedsByNeedAdminId(
            @PathVariable @Parameter(description = "Need Admin ID") String needAdminId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Need> needs = entityDiscoveryService.getNeedsByNeedAdminId(needAdminId, pageable);
        return ResponseEntity.ok(needs);
    }

}
