package io.github.geo_app.controller;

import io.github.geo_app.model.JobContext;
import io.github.geo_app.model.JobStatus;
import io.github.geo_app.model.JobType;
import io.github.geo_app.service.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/api/v1/export", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;

    @PostMapping
    public ResponseEntity<Object> runExportJob() {
        JobContext jobContext = new JobContext(JobType.EXPORT, null);
        UUID id = exportService.runJob(jobContext);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobStatus> getExportJobStatus(@PathVariable("id") UUID id) {
        JobStatus status = exportService.getJobStatus(id);
        return ResponseEntity.ok(status);
    }

    @GetMapping(path = "/{id}/file",
            produces = "application/vnd.ms-excel")
    public @ResponseBody byte[] getExportFile(@PathVariable("id") UUID id) {
            return exportService.getExportFile(id);
    }
}
