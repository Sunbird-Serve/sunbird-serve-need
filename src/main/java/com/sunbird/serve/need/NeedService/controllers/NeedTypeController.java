package com.sunbird.serve.need;

import com.sunbird.serve.need.models.Need.NeedType;
import com.sunbird.serve.need.models.request.NeedTypeRequest;
import com.sunbird.serve.need.models.request.CreateNeedTypeRequest;
import com.sunbird.serve.need.models.response.NeedResult;

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


@RestController
@CrossOrigin(origins = "*")
public class NeedTypeController {

    private final NeedTypeService needTypeService;

    @Autowired
    public NeedTypeController(NeedTypeService needTypeService) {
        this.needTypeService = needTypeService;
    }

    //Create Need Type with Need Type Request and Request Header
    @Operation(summary = "Create a Need Type by filling in request body", description = "Create a Need Type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Created Need Type", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
    @PostMapping("/need-type/create")
    public ResponseEntity<NeedType> createNeedType(@RequestBody CreateNeedTypeRequest request, @RequestHeader Map<String, String> headers) {
        NeedType response = needTypeService.createNeedType(request, headers);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //Update Need
    @Operation(summary = "Update a Need Type with appropritate values", description = "Update an exsisting need type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Updated Need Type", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Input"),
            @ApiResponse(responseCode = "500", description = "Server Error")}
    )
     @PutMapping("/need-type/update/{needTypeId}")
    public ResponseEntity<NeedType> updateNeedType(
            @PathVariable UUID needTypeId,
            @RequestBody NeedTypeRequest request,
            @RequestHeader Map<String, String> headers) {

        NeedType updatedNeedType = needTypeService.updateNeedType(needTypeId, request, headers);
        return ResponseEntity.ok(updatedNeedType);
    }

}
