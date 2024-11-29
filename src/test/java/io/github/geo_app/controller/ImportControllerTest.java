package io.github.geo_app.controller;

import io.github.geo_app.exceptions.JobRecordNotFoundException;
import io.github.geo_app.model.JobStatus;
import io.github.geo_app.service.ImportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestInstance(PER_CLASS)
class ImportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImportService importService;

    @Test
    void testImportData_Success() throws Exception {
        // Arrange
        MockMultipartFile validFile = new MockMultipartFile(
                "file",
                "import.xls",
                "application/vnd.ms-excel",
                "dummy content".getBytes()
        );
        UUID jobId = UUID.randomUUID();
        when(importService.runJob(any())).thenReturn(jobId);

        // Act & Assert
        mockMvc.perform(multipart("/api/v1/import")
                        .file(validFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(jobId.toString())));

        // Verify service interaction
        verify(importService, times(1)).runJob(any());
    }

    @Test
    void testImportData_InvalidFileType() throws Exception {
        // Arrange
        MockMultipartFile invalidFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "dummy content".getBytes()
        );

        // Act & Assert
        mockMvc.perform(multipart("/api/v1/import")
                        .file(invalidFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());

        // Verify no service interaction
        verify(importService, times(0)).runJob(any());
    }

    @Test
    void testGetImportStatus_Success() throws Exception {
        // Arrange
        UUID jobId = UUID.randomUUID();
        JobStatus jobStatus = JobStatus.IN_PROGRESS;
        when(importService.getJobStatus(jobId)).thenReturn(jobStatus);

        // Act & Assert
        mockMvc.perform(get("/api/v1/import/{id}", jobId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(jobStatus.name())));

        // Verify service interaction
        verify(importService, times(1)).getJobStatus(jobId);
    }

    @Test
    void testGetImportStatus_JobNotFound() throws Exception {
        // Arrange
        UUID jobId = UUID.randomUUID();
        when(importService.getJobStatus(jobId))
                .thenThrow(new JobRecordNotFoundException(jobId));

        // Act & Assert
        mockMvc.perform(get("/api/v1/import/{id}", jobId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // Verify service interaction
        verify(importService, times(1)).getJobStatus(jobId);
    }
}
