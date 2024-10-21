package com.sunbird.serve.need;

import com.sunbird.serve.need.models.Telemetry.TelemetryContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelemetryContextRepository extends JpaRepository<TelemetryContext, Long> {
    // Custom query methods (if needed) can be added here
}
