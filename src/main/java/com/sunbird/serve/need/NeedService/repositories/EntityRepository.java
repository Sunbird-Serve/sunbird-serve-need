package com.sunbird.serve.need;

import com.sunbird.serve.need.models.Need.Entity;
import com.sunbird.serve.need.models.enums.EntityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EntityRepository extends JpaRepository<Entity, UUID> {

    Page<Entity> findAllByStatus(EntityStatus status, Pageable pageable);

}
