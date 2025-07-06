// NeedDeliverableController.java
package com.sunbird.serve.need;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.sunbird.serve.need.models.Need.NeedDeliverable;
import com.sunbird.serve.need.models.Need.InputParameters;
import com.sunbird.serve.need.models.Need.OutputParameters;
import com.sunbird.serve.need.models.enums.NeedDeliverableStatus;
import com.sunbird.serve.need.models.request.NeedDeliverableRequest;
import com.sunbird.serve.need.models.request.DeliverableDetailsRequest;
import com.sunbird.serve.need.models.request.OutputParametersRequest;
import com.sunbird.serve.need.models.request.CreateInputParametersRequest;
import com.sunbird.serve.need.models.response.NeedDeliverableResponse;
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
import org.springframework.web.bind.annotation.CrossOrigin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class NeedDeliverableController {

    private static final Logger logger = LoggerFactory.getLogger(NeedDeliverableController.class);

    private final NeedDeliverableService needDeliverableService;

    public NeedDeliverableController(NeedDeliverableService needDeliverableService) {
        this.needDeliverableService = needDeliverableService;
    }

    //Fetch all needplan based on needId
    @Operation(summary = "Fetch Need Deliverable by providing NeedPlanId", description = "Fetch Need Deliverable by providing Need Plan Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Fetched Need Deliverable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @GetMapping("/need-deliverable/{needPlanId}")
    public ResponseEntity<NeedDeliverableResponse> getByNeedId(@PathVariable String needPlanId) {
        NeedDeliverableResponse needDeliverable = needDeliverableService.getByNeedPlanId(needPlanId);
        return ResponseEntity.ok(needDeliverable);
    }

    //Create Need Deliverable with Need Deliverable Request and Request Header
    @Operation(summary = "Create Need Deliverable", description = "Initiate the process of creating a Need Deliverable")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Created Need Deliverable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @PostMapping("/need-deliverable/create")
    public ResponseEntity<NeedDeliverable> createNeedDeliverable(@RequestBody NeedDeliverableRequest request, @RequestHeader Map<String, String> headers) {
        NeedDeliverable response = needDeliverableService.createNeedDeliverable(request, headers);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //Update Need Deliverable
    @Operation(summary = "Update a Need Deliverable with appropritate values", description = "Update an exsisting need deliverable")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Updated Need Deliverable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
     @PutMapping("/need-deliverable/update/{needDeliverableId}")
    public ResponseEntity<NeedDeliverable> updateNeedDeliverable(
            @PathVariable UUID needDeliverableId,
            @RequestBody NeedDeliverableRequest request,
            @RequestHeader Map<String, String> headers) {

        NeedDeliverable updatedNeedDeliverable = needDeliverableService.updateNeedDeliverable(needDeliverableId, request, headers);
        return ResponseEntity.ok(updatedNeedDeliverable);
    }

    //Update Need Deliverable Details
    @Operation(summary = "Update a Need Deliverable Details with appropritate values", description = "Update an exsisting need deliverable details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Updated Need Deliverable Details", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
     @PutMapping("/deliverable-details/update/{needDeliverableId}")
    public ResponseEntity<List<InputParameters>> updateDeliverableDetails(
            @PathVariable String needDeliverableId,
            @RequestBody DeliverableDetailsRequest request,
            @RequestHeader Map<String, String> headers) {

        List<InputParameters> updatedInputParameters = needDeliverableService.updateInputParameters(needDeliverableId, request, headers);
        return ResponseEntity.ok(updatedInputParameters);
    }

    //Update Need Deliverable Details
    @Operation(summary = "Update all Need Deliverables with appropritate values", description = "Update all need deliverable details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Updated all Need Deliverable Details", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
     @PutMapping("/all-deliverable-details/update/{needPlanId}")
    public ResponseEntity<List<NeedDeliverable>> updateNeedDeliverableDetails(
            @PathVariable String needPlanId,
            @RequestBody DeliverableDetailsRequest request,
            @RequestHeader Map<String, String> headers) {

        List<NeedDeliverable> updatedNeedDeliverables = needDeliverableService.updateNeedDeliverables(needPlanId, request, headers);
        return ResponseEntity.ok(updatedNeedDeliverables);
    }

    //Create Output Parameters for Deliverable Details
    @Operation(summary = "Create Output Parameters", description = "Create Output Parameters for Deliverable Details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Created Output Parameters", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @PostMapping("/deliverable-output/create")
    public ResponseEntity<OutputParameters> createOutputParameters(@RequestBody OutputParametersRequest request, @RequestHeader Map<String, String> headers) {
        OutputParameters response = needDeliverableService.createOutputParameters(request, headers);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Create Input Parameters", description = "Create Input Parameters for a Need Deliverable with default values")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Created Input Parameters", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @PostMapping("/deliverable-input/create")
    public ResponseEntity<InputParameters> createInputParameters(@RequestBody CreateInputParametersRequest request) {
        InputParameters response = needDeliverableService.createInputParameters(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
