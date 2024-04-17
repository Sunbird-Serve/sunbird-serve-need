package com.sunbird.serve.need;

import com.sunbird.serve.need.models.Need.InputParameters;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InputParametersRepository extends JpaRepository<InputParameters, UUID> {

    List<InputParameters> findByDeliverableDetailsId(String deliverableDetailsId);

}
