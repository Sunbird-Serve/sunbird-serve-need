package com.sunbird.serve.need;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sunbird.serve.need.models.TelemetryRequest.TelemetryDataRequest;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/telemetry")  // Define the base path for telemetry
public class NeedTelemetryController {

    @Autowired
    private NeedTelemetryService needTelemetryService; // Assuming you will create this service

    @PostMapping("/events")
    public ResponseEntity<Void> collectTelemetryData(@RequestBody TelemetryDataRequest telemetryDataRequest) {
        needTelemetryService.saveTelemetryData(telemetryDataRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
