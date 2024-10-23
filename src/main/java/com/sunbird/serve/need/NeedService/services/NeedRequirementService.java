package com.sunbird.serve.need;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sunbird.serve.need.models.Need.NeedRequirement;
import com.sunbird.serve.need.models.Need.SkillDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

@Service
public class NeedRequirementService {

    private static final Logger logger = LoggerFactory.getLogger(NeedRequirementService.class);
    private final NeedRequirementRepository needRequirementRepository;
    private final SkillDetailsRepository skillDetailsRepository;

    @Autowired
    public NeedRequirementService(NeedRequirementRepository needRequirementRepository,
    SkillDetailsRepository skillDetailsRepository) {
        this.needRequirementRepository = needRequirementRepository;
        this.skillDetailsRepository = skillDetailsRepository;
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

    // Fetch skills based on needTypeId
    public Page<SkillDetails> getSkillDetailsByNeedType(String needTypeId, Pageable pageable) {
        try {
            return skillDetailsRepository.findAllByNeedTypeId(needTypeId, pageable);
        } catch (Exception e) {
            logger.error("Error fetching Skills by NeedTypeId: {}", needTypeId, e);
            throw new RuntimeException("Error fetching Skills by NeedTypeId", e);
        }
    }
}
