package com.sunbird.serve.need;

import com.sunbird.serve.need.models.Need.NeedPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NeedPlanRepository extends JpaRepository<NeedPlan, UUID> {
    List<NeedPlan> findByNeedId(String needId);
}
