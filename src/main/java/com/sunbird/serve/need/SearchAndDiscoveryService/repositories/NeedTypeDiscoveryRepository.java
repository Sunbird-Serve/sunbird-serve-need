package com.sunbird.serve.need;

import com.sunbird.serve.need.models.Need.NeedType;
import com.sunbird.serve.need.models.enums.NeedTypeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;
import org.springframework.data.domain.Page; 
import org.springframework.data.domain.Pageable;
import java.util.Optional;

@Repository
public interface NeedTypeDiscoveryRepository extends JpaRepository<NeedType, UUID> {

    //Fetch all the needs
    Page<NeedType> findAll(Pageable pageable);

    //Fetch Need by status
    Page<NeedType> findAllNeedTypeByStatus(NeedTypeStatus status, Pageable pageable);
    //Page<Need> findAllByUserIdAndStatus(String userId, NeedStatus status, Pageable pageable);
    //Page<NeedType> findAllByNeedTypeId(String needTypeId, Pageable pageable );

    Page<NeedType> findNeedTypeByUserId(String userId, Pageable pageable);

    //Page<NeedType> findAllByUserIdAndNeedTypeId(String userId, String needTypeId, Pageable pageable);
}
