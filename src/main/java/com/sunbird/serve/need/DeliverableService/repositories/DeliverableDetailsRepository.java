package com.sunbird.serve.need;

import com.sunbird.serve.need.models.Need.DeliverableDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DeliverableDetailsRepository extends JpaRepository<DeliverableDetails, UUID> {

    List<DeliverableDetails> findByNeedDeliverableId(String needDeliverableId);

}
