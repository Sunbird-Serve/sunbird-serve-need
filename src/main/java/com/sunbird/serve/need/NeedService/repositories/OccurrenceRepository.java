package com.sunbird.serve.need;

import com.sunbird.serve.need.models.Need.Occurrence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OccurrenceRepository extends JpaRepository<Occurrence, UUID> { }
