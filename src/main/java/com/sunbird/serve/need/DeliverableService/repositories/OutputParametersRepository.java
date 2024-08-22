package com.sunbird.serve.need;

import com.sunbird.serve.need.models.Need.InputParameters;
import com.sunbird.serve.need.models.Need.OutputParameters;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutputParametersRepository extends JpaRepository<OutputParameters, UUID> {

    List<OutputParameters> findByNeedDeliverableId(String needDeliverableId);

}
