// NeedTypeDiscoveryController.java
package com.sunbird.serve.need;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.sunbird.serve.need.models.Need.NeedType;
import com.sunbird.serve.need.models.enums.NeedTypeStatus;
import com.sunbird.serve.need.models.enums.TaskType;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;
import io.swagger.v3.oas.annotations.Parameter;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

@RestController
public class NeedTypeDiscoveryController {

    private static final Logger logger = LoggerFactory.getLogger(NeedTypeDiscoveryController.class);

    private final NeedTypeDiscoveryService needTypeDiscoveryService;

    public NeedTypeDiscoveryController(NeedTypeDiscoveryService needTypeDiscoveryService) {
        this.needTypeDiscoveryService = needTypeDiscoveryService;
    }

    //Fetch all needs based on needId
    @GetMapping("/serve-need/needType/{needTypeId}")
    public ResponseEntity<NeedType> getNeedTypeById(@PathVariable String needTypeId) {
        Optional<NeedType> needType = needTypeDiscoveryService.getNeedTypeById(UUID.fromString(needTypeId));
        return needType.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/serve-need/needType/status/{status}")
    public ResponseEntity<Page<NeedType>> getNeedTypeByStatus(
            @PathVariable NeedTypeStatus status,
            @RequestParam(defaultValue = "0") @Parameter(description = "Page number (default: 0)") int page,
            @RequestParam(defaultValue = "10") @Parameter(description = "Page size (default: 10)") int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<NeedType> needTypeByStatus = needTypeDiscoveryService.getNeedTypeByStatus(status, pageable);
        return ResponseEntity.ok(needTypeByStatus);
    }

    @GetMapping("/serve-need/needType/read")
public ResponseEntity<Page<NeedType>> getAllNeedType(
        @RequestParam(defaultValue = "0") @Parameter(description = "Page number (default: 0)") int page,
        @RequestParam(defaultValue = "10") @Parameter(description = "Page size (default: 10)") int size,
        @RequestParam(required = false) @Parameter(description = "User ID") String userId) {

    Pageable pageable = PageRequest.of(page, size);
    Page<NeedType> needType;

    if (userId != null) {
        // Fetch needs based on userId
        needType = needTypeDiscoveryService.getNeedTypeByUserId(userId, pageable);
    } else {
        // Fetch all needs if no specific parameters are provided
        needType = needTypeDiscoveryService.getAllNeedType(pageable);
    }

    return ResponseEntity.ok(needType);
}


}
