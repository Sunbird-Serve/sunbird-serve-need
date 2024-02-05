package com.sunbird.serve.need;

import com.sunbird.serve.need.models.Need.Need;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface NeedRepository extends JpaRepository<Need, UUID> {
   
}
