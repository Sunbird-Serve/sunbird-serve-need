package com.sunbird.serve.need;

import com.sunbird.serve.need.models.Need.NeedDeliverable;
import com.sunbird.serve.need.models.enums.NeedDeliverableStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface NeedDeliverableRepository extends JpaRepository<NeedDeliverable, UUID> {

    List<NeedDeliverable> findByNeedPlanId(String needPlanId);

    List<NeedDeliverable> findByNeedPlanIdAndStatusAndDeliverableDateGreaterThanEqual(
        String needPlanId, NeedDeliverableStatus status, LocalDate date);
}
