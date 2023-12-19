// NeedDiscoveryController.java
package com.sunbird.serve.need;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.sunbird.serve.need.models.Need.NeedPlan;
import com.sunbird.serve.need.models.enums.NeedStatus;
import com.sunbird.serve.need.models.request.NeedPlanRequest;
import com.sunbird.serve.need.models.response.NeedPlanResponse;
import org.springframework.http.HttpStatus;
import java.util.List;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;
import java.util.Map;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@CrossOrigin(origins = "*")
public class NeedPlanController {

    private static final Logger logger = LoggerFactory.getLogger(NeedPlanController.class);

    private final NeedPlanService needPlanService;

    public NeedPlanController(NeedPlanService needPlanService) {
        this.needPlanService = needPlanService;
    }

    //Fetch all needplan based on needId
    @Operation(summary = "Fetch Need Plan by providing NeedId", description = "Fetch Need Plan by providing NeedId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Fetched Need Plan", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @GetMapping("/need-plan/{needId}")
   public ResponseEntity<List<NeedPlanResponse>> getByNeedId(@PathVariable String needId) {
    List<NeedPlanResponse> needPlan = needPlanService.getByNeedId(needId);

    if (!needPlan.isEmpty()) {
        return ResponseEntity.ok(needPlan);
    } else {
        return ResponseEntity.notFound().build();
    }
}


     //Raise Need with Need Request and Request Header
    @Operation(summary = "Create Need Plan by filling in request body", description = "Initiate the process of creating a Need Plan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Created Need Plan", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @PostMapping("/need-plan/create")
    public ResponseEntity<NeedPlan> createNeedPlan(@RequestBody NeedPlanRequest request, @RequestHeader Map<String, String> headers) {
        NeedPlan response = needPlanService.createNeedPlan(request, headers);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


}
