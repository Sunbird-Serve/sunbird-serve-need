package com.sunbird.serve.need;

import com.sunbird.serve.need.models.Need.SkillDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface SkillDetailsRepository extends JpaRepository<SkillDetails, UUID> {
   
   Page<SkillDetails> findAllByNeedTypeId(String needTypeId, Pageable pageable );

}
