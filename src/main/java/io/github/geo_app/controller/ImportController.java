package io.github.geo_app.controller;

import io.github.geo_app.exceptions.BadFileTypeException;
import io.github.geo_app.model.JobContext;
import io.github.geo_app.model.JobStatus;
import io.github.geo_app.model.JobType;
import io.github.geo_app.service.ImportService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/import", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class ImportController {
    @Autowired
    private final ImportService importService;

    @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> runImportJob(@RequestParam("file") @NotNull MultipartFile file) {
        if (!file.getContentType().equals("application/vnd.ms-excel")) {
            throw new BadFileTypeException();
        }
        JobContext jobContext = new JobContext(JobType.IMPORT, file);
        UUID id = importService.runJob(jobContext);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobStatus> getImportJobStatus(@PathVariable("id") UUID id) {
        JobStatus status = importService.getJobStatus(id);
        return ResponseEntity.ok(status);
    }
}
