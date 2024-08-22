package com.sunbird.serve.need;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sunbird.serve.need.models.Need.NeedRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

@Service
public class NeedRequirementService {

    private static final Logger logger = LoggerFactory.getLogger(NeedRequirementService.class);
    private final NeedRequirementRepository needRequirementRepository;

    @Autowired
    public NeedRequirementService(NeedRequirementRepository needRequirementRepository) {
        this.needRequirementRepository = needRequirementRepository;
    }

    // Fetch needs based on needRequirementId
    public Optional<NeedRequirement> getNeedRequirementById(UUID needReqId) {
        try {
            return needRequirementRepository.findById(needReqId);
        } catch (Exception e) {
            logger.error("Error fetching NeedRequirement by ID: " + needReqId, e);
            throw new RuntimeException("Error fetching NeedRequirement", e);
        }
    }
}
