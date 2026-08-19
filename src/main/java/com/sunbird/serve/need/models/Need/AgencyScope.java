package com.sunbird.serve.need.models.Need;

import com.sunbird.serve.need.models.enums.VisibilityScope;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "agency_scope")
public class AgencyScope {

    @Id
    @Column(name = "agency_id")
    private String agencyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "need_visibility")
    private VisibilityScope needVisibility;

    @Type(JsonType.class)
    @Column(name = "selected_need_agencies", columnDefinition = "jsonb")
    private List<String> selectedNeedAgencies;

    @Enumerated(EnumType.STRING)
    @Column(name = "volunteer_visibility")
    private VisibilityScope volunteerVisibility;

    @Type(JsonType.class)
    @Column(name = "selected_volunteer_agencies", columnDefinition = "jsonb")
    private List<String> selectedVolunteerAgencies;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
