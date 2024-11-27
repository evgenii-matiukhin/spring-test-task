package io.github.geo_app.service;

import io.github.geo_app.exceptions.AlreadyPendingImportJobException;
import io.github.geo_app.exceptions.JobFailedException;
import io.github.geo_app.exceptions.JobRecordNotFoundException;
import io.github.geo_app.model.GeoClass;
import io.github.geo_app.model.JobRecord;
import io.github.geo_app.model.JobStatus;
import io.github.geo_app.model.JobType;
import io.github.geo_app.model.Section;
import io.github.geo_app.repository.GeoClassRepository;
import io.github.geo_app.repository.JobRecordRepository;
import io.github.geo_app.repository.SectionRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImportService {
    @Autowired
    private final SectionRepository sectionRepository;

    @Autowired
    private final GeoClassRepository geoClassRepository;

    @Autowired
    private final JobRecordRepository jobRecordRepository;

    @Autowired
    private final XLSReader xlsReader;

    public UUID importData(@NotNull MultipartFile file) {
        log.debug("Importing data from file " + file.getOriginalFilename());
        List<JobRecord> existingJobs = jobRecordRepository.findJobRecordsByTypeAndStatus(JobType.IMPORT, JobStatus.IN_PROGRESS);
        if (!existingJobs.isEmpty()) {
            log.debug("There is already an import job in progress");
            throw new AlreadyPendingImportJobException();
        }
        JobRecord jobRecord = JobRecord.builder()
                .status(JobStatus.IN_PROGRESS)
                .startedAt(LocalDateTime.now())
                .type(JobType.IMPORT)
                .build();
        jobRecordRepository.save(jobRecord);
        log.debug("Job record with id " + jobRecord.getId() + " is created");
        runImportJob(jobRecord, file);
        log.debug("Job record with id " + jobRecord.getId() + " is in progress");
        return jobRecord.getId();
    }

    public JobStatus getImportJobStatus(UUID jobId) {
        JobRecord jobRecord = jobRecordRepository.findById(jobId).orElse(null);
        if (jobRecord == null) {
            throw new JobRecordNotFoundException(jobId);
        }
        return jobRecord.getStatus();
    }

    @Async
    private void runImportJob(JobRecord jobRecord, @NotNull MultipartFile file) {
        CompletableFuture.runAsync(() -> {
            log.debug("Async job is in progress");
            try {
                List<Section> sections = xlsReader.parse(file);
                log.debug("Sections are parsed. Trying to save.");
                saveSectionsInTransaction(sections);
            } catch (IOException e) {
                throw new JobFailedException(e.getMessage());
            }

        }).handle((result, ex) -> {
            if (ex == null) {
                log.debug("Async job completed successfully");
                jobRecord.setStatus(JobStatus.DONE);
            } else {
                log.warn("Async job failed", ex);
                jobRecord.setStatus(JobStatus.ERROR);
            }

            jobRecord.setFinishedAt(LocalDateTime.now());
            jobRecordRepository.save(jobRecord);

            return null;
        });
    }

    @Transactional
    public void saveSectionsInTransaction(List<Section> sections) {
        for (Section section : sections) {
            Set<GeoClass> uniqueClasses = new HashSet<>();
            for (GeoClass gc : section.getGeoClasses()) {
                GeoClass existingClass = geoClassRepository.findByCode(gc.getCode());
                if (existingClass != null) {
                    uniqueClasses.add(existingClass);
                } else {
                    uniqueClasses.add(gc);
                }
            }
            section.setGeoClasses(uniqueClasses);
            sectionRepository.save(section);
            log.debug("Section with name " + section.getName() + " is saved");
        }
    }
}
