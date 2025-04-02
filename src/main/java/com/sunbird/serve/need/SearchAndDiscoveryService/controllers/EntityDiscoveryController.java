// NeedDiscoveryController.java
package com.sunbird.serve.need;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.sunbird.serve.need.models.Need.Need;
import com.sunbird.serve.need.models.Need.Entity;
import com.sunbird.serve.need.models.request.EntityRequest;
import com.sunbird.serve.need.models.request.EntityMappingRequest;
import com.sunbird.serve.need.models.Need.EntityMapping;
import com.sunbird.serve.need.models.enums.NeedStatus;
import com.sunbird.serve.need.models.enums.EntityStatus;
import com.sunbird.serve.need.models.response.NeedEntityAndRequirement;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.*;
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
import org.springframework.http.HttpStatus;

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

    @GetMapping("/entity/all")
public ResponseEntity<Page<Entity>> getAllEntities(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size) 
{
    Pageable pageable = PageRequest.of(page, size);
    Page<Entity> allEntities = entityDiscoveryService.getAllEntities(pageable);
    return ResponseEntity.ok(allEntities);
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
    @Operation(summary = "Fetch all Users Id by providing Entity Id ", description = "Fetch all Users by providing Entity Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Fetched nCoordinators Id", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @GetMapping("/userList/{entityId}")
public ResponseEntity<Page<EntityMapping>> getAllUserId(
        @PathVariable(required = true) @Parameter(description = "Entity ID") UUID entityId,
            @RequestParam(required = false, defaultValue = "0")  Integer page,
            @RequestParam(required = false, defaultValue = "10")  Integer size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<EntityMapping> entityMapping;

    // Fetch entity id based on needAdminId
        entityMapping = entityDiscoveryService.getUsersByEntityId(entityId, pageable);

    return ResponseEntity.ok(entityMapping);
}


//Fetch entity details by providing Need Admin ID
    @Operation(summary = "Fetch all Entity Id by providing Need Admin Id ", description = "Fetch all Entity Id by providing Need Admin Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Fetched Entity Id", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @GetMapping("/entityDetails/{userId}")
public ResponseEntity<Page<Entity>> getAllEntityDetails(
        @PathVariable(required = true) @Parameter(description = "Need Admin ID") String userId,
            @RequestParam(required = false, defaultValue = "0")  Integer page,
            @RequestParam(required = false, defaultValue = "10")  Integer size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<Entity> entity;

    // Fetch entity id based on needAdminId
        entity = entityDiscoveryService.getEntitiesByUserId(userId, pageable);

    return ResponseEntity.ok(entity);
}

  //Create Entity
    @Operation(summary = "Create an Entity", description = "Initiate the process of creating a new Entity")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Created Entity", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @PostMapping("/entity/create")
    public ResponseEntity<Entity> createEntity(@RequestBody EntityRequest request, @RequestHeader Map<String, String> headers) {
        Entity response = entityDiscoveryService.createEntity(request, headers);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Edit an Entity", description = "Update an existing Entity")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Updated Entity", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "404", description = "Entity Not Found"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @PutMapping("entity/edit/{id}")
    public ResponseEntity<Entity> editEntity(@PathVariable UUID id, @RequestBody EntityRequest request, @RequestHeader Map<String, String> headers) {
        Entity response = entityDiscoveryService.editEntity(id, request, headers);
        return ResponseEntity.ok(response);
    }

     //Register Entity
    @Operation(summary = "Assign an Entity", description = "Assign an Entity to nAdmin or nCoordinator")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Assigned Entity", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @PostMapping("/entity/assign")
    public ResponseEntity<EntityMapping> assignEntity(@RequestBody EntityMappingRequest request, @RequestHeader Map<String, String> headers) {
        EntityMapping response = entityDiscoveryService.assignEntity(request, headers);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Edit an assigned Entity", description = "Modify an assigned Entity mapping")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated Entity Mapping", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "404", description = "Entity Mapping Not Found"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @PutMapping("/assign/edit/{id}")
    public ResponseEntity<EntityMapping> editAssignedEntity(@PathVariable UUID id, @RequestBody EntityMappingRequest request, @RequestHeader Map<String, String> headers) {
        EntityMapping updatedMapping = entityDiscoveryService.editAssignedEntity(id, request, headers);
        return ResponseEntity.ok(updatedMapping);
    }

// Fetch all needs based on needAdminId
    @Operation(summary = "Fetch all needs by User Id", description = "Fetch all needs by User Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched needs", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @GetMapping("/needs/{userId}")
    public ResponseEntity<Page<Need>> getAllNeedsByUserId(
            @PathVariable @Parameter(description = "Need Admin ID") String userId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Need> needs = entityDiscoveryService.getNeedsByUserId(userId, pageable);
        return ResponseEntity.ok(needs);
    }

}
