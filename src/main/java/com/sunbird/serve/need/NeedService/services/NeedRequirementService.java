package com.sunbird.serve.need;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sunbird.serve.need.models.Need.Need;
import com.sunbird.serve.need.models.Need.NeedType;
import com.sunbird.serve.need.models.enums.NeedStatus;
import com.sunbird.serve.need.models.Need.Entity;
import com.sunbird.serve.need.models.enums.EntityStatus;
import com.sunbird.serve.need.models.Need.Occurrence;
import com.sunbird.serve.need.models.Need.TimeSlot;
import com.sunbird.serve.need.models.response.NeedEntityAndRequirement;
import com.sunbird.serve.need.models.Need.NeedRequirement;
import com.sunbird.serve.need.*;


import java.util.Optional;
import java.util.UUID;

import java.util.List;

import org.springframework.data.domain.Page; 
import org.springframework.data.domain.Pageable;

@Service
public class NeedRequirementService {

    private final NeedRequirementRepository needRequirementRepository;

    @Autowired
    public NeedRequirementService(
            NeedRequirementRepository needRequirementRepository) {
        this.needRequirementRepository = needRequirementRepository;
    }


    //Fetch needs based on needRequirementId
    public Optional<NeedRequirement> getNeedRequirementById(UUID needReqId) {
        return needRequirementRepository.findById(needReqId);
    }

}
