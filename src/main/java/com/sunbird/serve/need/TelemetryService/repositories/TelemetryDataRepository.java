package com.sunbird.serve.need;

import com.sunbird.serve.need.models.Telemetry.TelemetryData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TelemetryDataRepository extends JpaRepository<TelemetryData, Long> {
    // Custom query methods (if needed) can be added here
}
