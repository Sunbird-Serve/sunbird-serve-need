package com.sunbird.serve.need;

import com.sunbird.serve.need.models.Telemetry.TelemetryEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelemetryEventRepository extends JpaRepository<TelemetryEvent, Long> {
    // Custom query methods (if needed) can be added here
}
