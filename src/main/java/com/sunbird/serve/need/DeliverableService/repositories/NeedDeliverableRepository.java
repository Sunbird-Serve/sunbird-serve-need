package com.sunbird.serve.need;

import com.sunbird.serve.need.models.Need.NeedDeliverable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NeedDeliverableRepository extends JpaRepository<NeedDeliverable, UUID> {

    List<NeedDeliverable> findByNeedPlanId(String needPlanId);

}
