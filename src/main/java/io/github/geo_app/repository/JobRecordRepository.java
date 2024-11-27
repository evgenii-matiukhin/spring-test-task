package io.github.geo_app.repository;

import io.github.geo_app.model.JobRecord;
import io.github.geo_app.model.JobStatus;
import io.github.geo_app.model.JobType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JobRecordRepository extends JpaRepository<JobRecord, UUID> {

    List<JobRecord> findJobRecordsByTypeAndStatus(JobType type, JobStatus status);
}
