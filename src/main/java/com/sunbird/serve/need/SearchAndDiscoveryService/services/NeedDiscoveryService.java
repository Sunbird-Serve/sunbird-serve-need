package com.sunbird.serve.need;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sunbird.serve.need.models.Need.Need;
import com.sunbird.serve.need.models.enums.NeedStatus;

import java.util.Optional;
import java.util.UUID;

import java.util.List;

import org.springframework.data.domain.Page; 
import org.springframework.data.domain.Pageable;

@Service
public class NeedDiscoveryService {

    private final NeedDiscoveryRepository needDiscoveryRepository;

    @Autowired
    public NeedDiscoveryService(NeedDiscoveryRepository needDiscoveryRepository) {
        this.needDiscoveryRepository = needDiscoveryRepository;
    }

    //Fetch all the needs 
    public Page<Need> getAllNeeds(Pageable pageable) {
        return needDiscoveryRepository.findAll(pageable);
    }

    //Fetch needs based on needId
    public Optional<Need> getNeedById(UUID needId) {
        return needDiscoveryRepository.findById(needId);
    }

    //Fetch need by status
     public Page<Need> getNeedsByStatus(NeedStatus status, Pageable pageable) {
        return needDiscoveryRepository.findAllByStatus(status, pageable);
    }

    //Fetch needs based on needTypeId
    public Page<Need> getNeedByNeedTypeId(String needTypeId, Pageable pageable) {
        return needDiscoveryRepository.findAllByNeedTypeId(needTypeId, pageable);
    }

    //Fetch needs based on userId
    public Page<Need> getNeedByUserId(String userId, Pageable pageable) {
        return needDiscoveryRepository.findAllByUserId(userId, pageable);
    }

    //Fetch needs based on userId
    public Page<Need> getNeedByUserIdAndNeedTypeId(String userId, String needTypeId, Pageable pageable) {
        return needDiscoveryRepository.findAllByUserIdAndNeedTypeId(userId, needTypeId, pageable);
    }
}
