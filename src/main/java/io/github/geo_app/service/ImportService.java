package io.github.geo_app.service;

import io.github.geo_app.exceptions.AlreadyPendingImportJobException;
import io.github.geo_app.exceptions.JobFailedException;
import io.github.geo_app.model.JobContext;
import io.github.geo_app.model.JobRecord;
import io.github.geo_app.model.JobStatus;
import io.github.geo_app.model.JobType;
import io.github.geo_app.model.Section;
import io.github.geo_app.repository.JobRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImportService extends BaseAsyncService {
    private final SectionService sectionService;

    private final JobRecordRepository jobRecordRepository;

    private final XLSReader xlsReader;

    @Transactional
    void saveSectionsInTransaction(List<Section> sections) {
        for (Section section : sections) {
            sectionService.createSection(section);
            log.debug("Section with name " + section.getName() + " is saved");
        }
    }

    @Override
    protected void runAsync(JobRecord jobRecord, JobContext jobContext) {
        try {
            List<Section> sections = xlsReader.parse(jobContext.getFile().getInputStream());
            log.debug("Sections are parsed. Trying to save.");
            saveSectionsInTransaction(sections);
        } catch (IOException e) {
            throw new JobFailedException(e.getMessage());
        }
    }

    @Override
    protected void validate(JobContext jobContext) {
        log.debug("Importing data from file " + jobContext.getFile().getOriginalFilename());
        List<JobRecord> existingJobs = jobRecordRepository.findJobRecordsByTypeAndStatus(JobType.IMPORT, JobStatus.IN_PROGRESS);
        if (!existingJobs.isEmpty()) {
            log.debug("There is already an import job in progress");
            throw new AlreadyPendingImportJobException();
        }
    }
}
