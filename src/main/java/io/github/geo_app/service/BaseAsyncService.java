package io.github.geo_app.service;

import io.github.geo_app.exceptions.JobRecordNotFoundException;
import io.github.geo_app.model.JobContext;
import io.github.geo_app.model.JobRecord;
import io.github.geo_app.model.JobStatus;
import io.github.geo_app.repository.JobRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseAsyncService {
    @Autowired
    protected JobRecordRepository jobRecordRepository;

    public UUID runJob(JobContext jobContext) {
        validate(jobContext);
        JobRecord jobRecord = JobRecord.builder()
                .status(JobStatus.IN_PROGRESS)
                .startedAt(LocalDateTime.now())
                .type(jobContext.getType())
                .build();
        jobRecord = jobRecordRepository.save(jobRecord);
        log.debug("Job record with id " + jobRecord.getId() + " is created");
        wrapAsyncJob(jobRecord, jobContext);
        log.debug("Job record with id " + jobRecord.getId() + " is in progress");
        return jobRecord.getId();
    }

    protected void validate(JobContext jobContext) {}

    public JobStatus getJobStatus(UUID jobId) {
        JobRecord jobRecord = jobRecordRepository.findById(jobId).orElse(null);
        if (jobRecord == null) {
            throw new JobRecordNotFoundException(jobId);
        }
        return jobRecord.getStatus();
    }

    @Async
    void wrapAsyncJob(JobRecord jobRecord, JobContext jobContext) {
        Thread.startVirtualThread(() -> {
            try {
                log.debug("Async job is in progress");
                runAsync(jobRecord, jobContext);

                log.debug("Async job completed successfully");
                jobRecord.setStatus(JobStatus.DONE);
            } catch (Exception ex) {
                log.warn("Async job failed", ex);
                jobRecord.setStatus(JobStatus.ERROR);
            } finally {
                jobRecord.setFinishedAt(LocalDateTime.now());
                jobRecordRepository.save(jobRecord);
            }
        });
    }


    @Async
    protected abstract void runAsync(JobRecord jobRecord, JobContext jobContext);
}
