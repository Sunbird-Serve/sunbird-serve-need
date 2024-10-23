package com.sunbird.serve.need;

import com.sunbird.serve.need.models.Need.NeedRequirement;
import com.sunbird.serve.need.models.Need.SkillDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.UUID;
import java.util.Map;
import java.util.Optional;


@RestController
@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping("/need-requirement")
public class NeedRequirementController {

    private final NeedRequirementService needRequirementService;

    public NeedRequirementController(NeedRequirementService needRequirementService) {
        this.needRequirementService = needRequirementService;
    }

    @Operation(summary = "Get a particular need requirement by its primary key", description = "Get Need Requirement by Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched Need Requirement", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @GetMapping(value = "/{needReqId}",
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    ResponseEntity<NeedRequirement> getNeedRequirementById(
            @PathVariable String needReqId,
            @Parameter() @RequestHeader Map<String, String> headers){
                Optional<NeedRequirement> needRequirement = needRequirementService.getNeedRequirementById(UUID.fromString(needReqId));
        return needRequirement.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
            }

    
    @Operation(summary = "Get list of Skills based on Need Type", description = "Get list of Skills based on Need Type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched Skill Details", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
         @GetMapping("/skilldetails/{needTypeId}")
    public ResponseEntity<Page<SkillDetails>> getSkillDetailsByNeedType(
        @PathVariable(required = true) @Parameter(description = "Need Type ID") String needTypeId,
            @RequestParam(required = false, defaultValue = "0")  Integer page,
            @RequestParam(required = false, defaultValue = "10")  Integer size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<SkillDetails> skillDetails;

    // Fetch skill details based on needTypeId
    skillDetails = needRequirementService.getSkillDetailsByNeedType(needTypeId, pageable);

    return ResponseEntity.ok(skillDetails);
}
}
