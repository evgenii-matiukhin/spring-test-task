package io.github.geo_app.service;

import io.github.geo_app.exceptions.ExportFileNotFoundException;
import io.github.geo_app.exceptions.JobFailedException;
import io.github.geo_app.exceptions.JobRecordNotFoundException;
import io.github.geo_app.model.JobContext;
import io.github.geo_app.model.JobRecord;
import io.github.geo_app.model.JobStatus;
import io.github.geo_app.model.Section;
import io.github.geo_app.repository.SectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExportService extends BaseAsyncService {

    private SectionRepository sectionRepository;

    private XLSWriter xlsWriter;

    @Override
    @Async
    protected void runAsync(JobRecord jobRecord, JobContext jobContext) {
        List<Section> sections = sectionRepository.findAllWithGeoClasses();
        try (FileOutputStream fos = new FileOutputStream(getExportFileName(jobRecord.getId()))) {
            xlsWriter.write(fos, sections);
        } catch (IOException e) {
            log.error("Failed to write export file", e);
            try {
                log.debug("Trying to delete export file");
                Files.delete(new File(getExportFileName(jobRecord.getId())).toPath());
            } catch (IOException ex) {
                log.warn("Failed to delete broken export file", ex);
            }
            throw new JobFailedException(e.getMessage());
        }
    }

    public byte[] getExportFile(UUID id) {
        JobRecord jobRecord = jobRecordRepository.findById(id).orElse(null);
        if (jobRecord == null) {
            throw new JobRecordNotFoundException(id);
        }
        if (jobRecord.getStatus() != JobStatus.DONE) {
            throw new ExportFileNotFoundException();
        }
        File file = new File(getExportFileName(id));
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new JobFailedException(e.getMessage());
        }
    }

    static String getExportFileName(UUID id) {
        return "export/export-" + id + ".xls";
    }
}
