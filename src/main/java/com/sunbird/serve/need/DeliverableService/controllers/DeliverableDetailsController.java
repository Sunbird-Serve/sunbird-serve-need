// NeedDeliverableController.java
package com.sunbird.serve.need;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.sunbird.serve.need.models.Need.NeedDeliverable;
import com.sunbird.serve.need.models.Need.DeliverableDetails;
import com.sunbird.serve.need.models.Need.InputParameters;
import com.sunbird.serve.need.models.enums.NeedDeliverableStatus;
import com.sunbird.serve.need.models.request.NeedDeliverableRequest;
import com.sunbird.serve.need.models.request.DeliverableDetailsRequest;
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
public class DeliverableDetailsController {

    private static final Logger logger = LoggerFactory.getLogger(DeliverableDetailsController.class);

    private final DeliverableDetailsService deliverableDetailsService;

    public DeliverableDetailsController(DeliverableDetailsService deliverableDetailsService) {
        this.deliverableDetailsService = deliverableDetailsService;
    }

    //Fetch all needplan based on needId
    @Operation(summary = "Fetch Deliverable Details by providing Need Deliverable Id", description = "Fetch Deliverable Details by providing Need Deliverable Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Fetched Deliverable Details", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @GetMapping("/deliverable-details/{needId}")
    public ResponseEntity<List<DeliverableDetails>> getByNeedId(@PathVariable String needId) {
        List<DeliverableDetails> deliverableDetails = deliverableDetailsService.getNDByNeedId(needId);
        return ResponseEntity.ok(deliverableDetails);
    }

    //Update Deliverable Details
    @Operation(summary = "Update a Deliverable Detail with appropritate values", description = "Update an exsisting deliverable details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Updated Deliverable Details", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
     @PutMapping("/deliverable-details/update/{needId}")
    public ResponseEntity<InputParameters> updateNeedDeliverable(
            @PathVariable String needId,
            @RequestBody DeliverableDetailsRequest request,
            @RequestHeader Map<String, String> headers) {

        InputParameters updatedNeedDeliverable = deliverableDetailsService.updateDeliverableDetails(needId, request, headers);
        return ResponseEntity.ok(updatedNeedDeliverable);
    }

    

}
