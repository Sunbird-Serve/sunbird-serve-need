package com.sunbird.serve.need;

import com.sunbird.serve.need.models.Need.NeedType;
import com.sunbird.serve.need.models.enums.NeedTypeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NeedTypeRepository extends JpaRepository<NeedType, UUID> {

    
}
