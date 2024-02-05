package com.sunbird.serve.need;

import com.sunbird.serve.need.models.Need.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, UUID> {
    List<TimeSlot> findByOccurrenceId(String occurrenceId);
}
