package io.github.geo_app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "jobs")
public class JobRecord {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Enumerated(EnumType.STRING)
    private JobType type;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    private LocalDateTime startedAt;

    private LocalDateTime finishedAt;
}
