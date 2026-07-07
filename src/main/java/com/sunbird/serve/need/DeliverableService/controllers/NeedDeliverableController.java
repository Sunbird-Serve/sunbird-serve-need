package com.sunbird.serve.need;

import com.sunbird.serve.need.models.Need.NeedDeliverable;
import com.sunbird.serve.need.models.Need.InputParameters;
import com.sunbird.serve.need.models.Need.OutputParameters;
import com.sunbird.serve.need.models.request.NeedDeliverableRequest;
import com.sunbird.serve.need.models.request.DeliverableDetailsRequest;
import com.sunbird.serve.need.models.request.OutputParametersRequest;
import com.sunbird.serve.need.models.request.CreateInputParametersRequest;
import com.sunbird.serve.need.models.request.RescheduleRequest;
import com.sunbird.serve.need.models.response.NeedDeliverableResponse;
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
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
public class NeedDeliverableController {

    private static final Logger logger = LoggerFactory.getLogger(NeedDeliverableController.class);

    private final NeedDeliverableService needDeliverableService;

    public NeedDeliverableController(NeedDeliverableService needDeliverableService) {
        this.needDeliverableService = needDeliverableService;
    }

    @Operation(summary = "Fetch Need Deliverable by providing NeedPlanId", description = "Fetch Need Deliverable by providing Need Plan Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Fetched Need Deliverable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/need-deliverable/{needPlanId}")
    public ResponseEntity<NeedDeliverableResponse> getByNeedId(@PathVariable String needPlanId) {
        NeedDeliverableResponse needDeliverable = needDeliverableService.getByNeedPlanId(needPlanId);
        return ResponseEntity.ok(needDeliverable);
    }

    @Operation(summary = "Create Need Deliverable", description = "Initiate the process of creating a Need Deliverable")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Created Need Deliverable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "403", description = "Access Denied"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @PreAuthorize("hasAnyRole('sAdmin', 'nAdmin', 'nCoordinator')")
    @PostMapping("/need-deliverable/create")
    public ResponseEntity<NeedDeliverable> createNeedDeliverable(@RequestBody NeedDeliverableRequest request,
                                                                  @RequestHeader Map<String, String> headers) {
        NeedDeliverable response = needDeliverableService.createNeedDeliverable(request, headers);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update a Need Deliverable with appropriate values", description = "Update an existing need deliverable")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Updated Need Deliverable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "403", description = "Access Denied"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @PreAuthorize("hasAnyRole('sAdmin', 'nAdmin', 'nCoordinator', 'Volunteer')")
    @PutMapping("/need-deliverable/update/{needDeliverableId}")
    public ResponseEntity<NeedDeliverable> updateNeedDeliverable(
            @PathVariable UUID needDeliverableId,
            @RequestBody NeedDeliverableRequest request,
            @RequestHeader Map<String, String> headers) {
        NeedDeliverable updatedNeedDeliverable = needDeliverableService.updateNeedDeliverable(needDeliverableId, request, headers);
        return ResponseEntity.ok(updatedNeedDeliverable);
    }

    @Operation(summary = "Update a Need Deliverable Details with appropriate values", description = "Update an existing need deliverable details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Updated Need Deliverable Details", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "403", description = "Access Denied"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @PreAuthorize("hasAnyRole('sAdmin', 'nAdmin', 'nCoordinator')")
    @PutMapping("/deliverable-details/update/{needDeliverableId}")
    public ResponseEntity<List<InputParameters>> updateDeliverableDetails(
            @PathVariable String needDeliverableId,
            @RequestBody DeliverableDetailsRequest request,
            @RequestHeader Map<String, String> headers) {
        List<InputParameters> updatedInputParameters = needDeliverableService.updateInputParameters(needDeliverableId, request, headers);
        return ResponseEntity.ok(updatedInputParameters);
    }

    @Operation(summary = "Update all Need Deliverables with appropriate values", description = "Update all need deliverable details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Updated all Need Deliverable Details", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "403", description = "Access Denied"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @PreAuthorize("hasAnyRole('sAdmin', 'nAdmin', 'nCoordinator')")
    @PutMapping("/all-deliverable-details/update/{needPlanId}")
    public ResponseEntity<List<NeedDeliverable>> updateNeedDeliverableDetails(
            @PathVariable String needPlanId,
            @RequestBody DeliverableDetailsRequest request,
            @RequestHeader Map<String, String> headers) {
        List<NeedDeliverable> updatedNeedDeliverables = needDeliverableService.updateNeedDeliverables(needPlanId, request, headers);
        return ResponseEntity.ok(updatedNeedDeliverables);
    }

    @Operation(summary = "Create Output Parameters", description = "Create Output Parameters for Deliverable Details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Created Output Parameters", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "403", description = "Access Denied"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @PreAuthorize("hasAnyRole('sAdmin', 'nAdmin', 'nCoordinator')")
    @PostMapping("/deliverable-output/create")
    public ResponseEntity<OutputParameters> createOutputParameters(@RequestBody OutputParametersRequest request,
                                                                    @RequestHeader Map<String, String> headers) {
        OutputParameters response = needDeliverableService.createOutputParameters(request, headers);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Create Input Parameters", description = "Create Input Parameters for a Need Deliverable with default values")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Created Input Parameters", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "403", description = "Access Denied"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @PreAuthorize("hasAnyRole('sAdmin', 'nAdmin', 'nCoordinator')")
    @PostMapping("/deliverable-input/create")
    public ResponseEntity<InputParameters> createInputParameters(@RequestBody CreateInputParametersRequest request) {
        InputParameters response = needDeliverableService.createInputParameters(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Reschedule deliverables for a need plan", description = "Pauses future planned deliverables on dropped days and creates new ones for added days")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Rescheduled Deliverables", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "403", description = "Access Denied"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @PreAuthorize("hasAnyRole('sAdmin', 'nAdmin', 'nCoordinator')")
    @PutMapping("/need-plan/{needPlanId}/reschedule")
    public ResponseEntity<Map<String, Object>> rescheduleDeliverables(
            @PathVariable String needPlanId,
            @RequestBody RescheduleRequest request,
            @RequestHeader Map<String, String> headers) {
        Map<String, Object> result = needDeliverableService.rescheduleDeliverables(needPlanId, request, headers);
        return ResponseEntity.ok(result);
    }
}
