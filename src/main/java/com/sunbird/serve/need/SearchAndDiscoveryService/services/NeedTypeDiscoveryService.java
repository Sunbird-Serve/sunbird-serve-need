package com.sunbird.serve.need;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sunbird.serve.need.models.Need.NeedType;
import com.sunbird.serve.need.models.enums.NeedTypeStatus;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page; 
import org.springframework.data.domain.Pageable;

@Service
public class NeedTypeDiscoveryService {

    private static final Logger logger = LoggerFactory.getLogger(NeedTypeDiscoveryService.class);

    private final NeedTypeDiscoveryRepository needTypeDiscoveryRepository;

    @Autowired
    public NeedTypeDiscoveryService(NeedTypeDiscoveryRepository needTypeDiscoveryRepository) {
        this.needTypeDiscoveryRepository = needTypeDiscoveryRepository;
    }

    // Fetch all the need types
    public Page<NeedType> getAllNeedType(Pageable pageable) {
        try {
            return needTypeDiscoveryRepository.findAll(pageable);
        } catch (Exception e) {
            logger.error("Error fetching all NeedTypes", e);
            throw new RuntimeException("Error fetching all NeedTypes", e);
        }
    }

    // Fetch need type based on needTypeId
    public Optional<NeedType> getNeedTypeById(UUID needTypeId) {
        try {
            return needTypeDiscoveryRepository.findById(needTypeId);
        } catch (Exception e) {
            logger.error("Error fetching NeedType by ID: {}", needTypeId, e);
            throw new RuntimeException("Error fetching NeedType by ID", e);
        }
    }

    // Fetch need types based on status
    public Page<NeedType> getNeedTypeByStatus(NeedTypeStatus status, Pageable pageable) {
        try {
            return needTypeDiscoveryRepository.findAllNeedTypeByStatus(status, pageable);
        } catch (Exception e) {
            logger.error("Error fetching NeedTypes by Status: {}", status, e);
            throw new RuntimeException("Error fetching NeedTypes by Status", e);
        }
    }

    // Fetch need types based on userId
    public Page<NeedType> getNeedTypeByUserId(String userId, Pageable pageable) {
        try {
            return needTypeDiscoveryRepository.findNeedTypeByUserId(userId, pageable);
        } catch (Exception e) {
            logger.error("Error fetching NeedTypes by UserId: {}", userId, e);
            throw new RuntimeException("Error fetching NeedTypes by UserId", e);
        }
    }
}
