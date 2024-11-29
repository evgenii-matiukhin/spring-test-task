package io.github.geo_app.controller;

import io.github.geo_app.model.JobStatus;
import io.github.geo_app.service.ExportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestInstance(PER_CLASS)
class ExportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExportService exportService;

    private UUID jobId;

    @BeforeEach
    void setUp() {
        jobId = UUID.randomUUID();
    }

    @Test
    void runExportJob_ShouldReturnJobId() throws Exception {
        // Arrange
        Mockito.when(exportService.runJob(any())).thenReturn(jobId);

        // Act & Assert
        mockMvc.perform(post("/api/v1/export")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(jobId.toString())));
    }

    @Test
    void getExportJobStatus_ShouldReturnJobStatus() throws Exception {
        // Arrange
        Mockito.when(exportService.getJobStatus(eq(jobId))).thenReturn(JobStatus.DONE);

        // Act & Assert
        mockMvc.perform(get("/api/v1/export/{id}", jobId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(JobStatus.DONE.name())));
    }

    @Test
    void getExportFile_ShouldReturnFile() throws Exception {
        // Arrange
        byte[] fileContent = "sample excel content".getBytes();
        Mockito.when(exportService.getExportFile(eq(jobId))).thenReturn(fileContent);

        // Act & Assert
        mockMvc.perform(get("/api/v1/export/{id}/file", jobId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.ms-excel"))
                .andExpect(content().bytes(fileContent));
    }

    @Test
    void getExportFile_ShouldReturnNotFound_WhenFileDoesNotExist() throws Exception {
        // Arrange
        Mockito.when(exportService.getExportFile(eq(jobId)))
                .thenThrow(new RuntimeException("File not found"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/export/{id}/file", jobId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}
