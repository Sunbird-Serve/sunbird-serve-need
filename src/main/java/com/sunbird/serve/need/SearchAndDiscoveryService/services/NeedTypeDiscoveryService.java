package com.sunbird.serve.need;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sunbird.serve.need.models.Need.NeedType;
import com.sunbird.serve.need.models.enums.NeedTypeStatus;

import java.util.Optional;
import java.util.UUID;

import java.util.List;

import org.springframework.data.domain.Page; 
import org.springframework.data.domain.Pageable;

@Service
public class NeedTypeDiscoveryService {

    private final NeedTypeDiscoveryRepository needTypeDiscoveryRepository;

    @Autowired
    public NeedTypeDiscoveryService(NeedTypeDiscoveryRepository needTypeDiscoveryRepository) {
        this.needTypeDiscoveryRepository = needTypeDiscoveryRepository;
    }

    //Fetch all the needs 
    public Page<NeedType> getAllNeedType(Pageable pageable) {
        return needTypeDiscoveryRepository.findAll(pageable);
    }

    //Fetch needs based on needId
    public Optional<NeedType> getNeedTypeById(UUID needTypeId) {
        return needTypeDiscoveryRepository.findById(needTypeId);
    }

    //Fetch need by status
     public Page<NeedType> getNeedTypeByStatus(NeedTypeStatus status, Pageable pageable) {
        return needTypeDiscoveryRepository.findAllNeedTypeByStatus(status, pageable);
    }

    //Fetch needs based on needTypeId
    /*public Page<NeedType> findAllByNeedTypeId(String needTypeId, Pageable pageable) {
        return needDiscoveryRepository.findAllByNeedTypeId(needTypeId, pageable);
    }*/

    //Fetch needs based on userId
    public Page<NeedType> getNeedTypeByUserId(String userId, Pageable pageable) {
        return needTypeDiscoveryRepository.findNeedTypeByUserId(userId, pageable);
    }

    //Fetch needs based on userId
    /*public Page<NeedType> findAllByUserIdAndNeedTypeId(String userId, String needTypeId, Pageable pageable) {
        return needDiscoveryRepository.findAllByUserIdAndNeedTypeId(userId, needTypeId, pageable);
    }*/
}
