package io.github.geo_app.service;

import io.github.geo_app.exceptions.ExportFileNotFoundException;
import io.github.geo_app.exceptions.JobFailedException;
import io.github.geo_app.exceptions.JobRecordNotFoundException;
import io.github.geo_app.model.JobContext;
import io.github.geo_app.model.JobRecord;
import io.github.geo_app.model.JobStatus;
import io.github.geo_app.model.JobType;
import io.github.geo_app.model.Section;
import io.github.geo_app.repository.JobRecordRepository;
import io.github.geo_app.repository.SectionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ExportServiceTest {

    @InjectMocks
    private ExportService exportService;

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private JobRecordRepository jobRecordRepository;

    @Mock
    private XLSWriter xlsWriter;

    private JobRecord jobRecord;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock job record
        jobRecord = JobRecord.builder()
                .id(UUID.randomUUID())
                .status(JobStatus.IN_PROGRESS)
                .type(JobType.EXPORT)
                .startedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void runAsync_ShouldCompleteJobSuccessfully() throws IOException {
        // Arrange
        List<Section> mockSections = new ArrayList<>();
        mockSections.add(new Section(1L, "Section1", new HashSet<>()));

        when(sectionRepository.findAllWithGeoClasses()).thenReturn(mockSections);

        // Act
        exportService.runAsync(jobRecord, new JobContext(JobType.EXPORT, null));

        // Assert
        verify(xlsWriter, times(1)).write(any(FileOutputStream.class), eq(mockSections));
        File file = new File(ExportService.getExportFileName(jobRecord.getId()));
        assertTrue(file.exists());
        file.delete();
    }

    @Test
    void runAsync_ShouldMarkJobAsFailed_OnIOException() throws IOException {
        // Arrange
        List<Section> mockSections = new ArrayList<>();
        mockSections.add(new Section(1L, "Section1", new HashSet<>()));

        when(sectionRepository.findAllWithGeoClasses()).thenReturn(mockSections);

        doThrow(new IOException("Simulated IO Error")).when(xlsWriter).write(any(), eq(mockSections));

        // Act & Assert
        assertThrows(JobFailedException.class, () ->
                exportService.runAsync(jobRecord, new JobContext(JobType.EXPORT, null))
        );
    }

    @Test
    void getExportFile_ShouldReturnFileBytes_WhenJobIsDone() throws IOException {
        // Arrange
        UUID jobId = jobRecord.getId();
        jobRecord.setStatus(JobStatus.DONE);
        when(jobRecordRepository.findById(jobId)).thenReturn(Optional.of(jobRecord));

        File mockFile = ResourceUtils.getFile("classpath:test-data/sections-valid.xls");;
        File exportFile = new File(ExportService.getExportFileName(jobId));
        Files.copy(mockFile.toPath(), exportFile.toPath());
        byte[] expectedBytes = Files.readAllBytes(mockFile.toPath());

        // Act
        byte[] fileBytes = exportService.getExportFile(jobId);

        // Assert
        assertArrayEquals(expectedBytes, fileBytes);
        exportFile.delete();
    }

    @Test
    void getExportFile_ShouldThrowException_WhenJobIsNotDone() {
        // Arrange
        UUID jobId = jobRecord.getId();
        jobRecord.setStatus(JobStatus.IN_PROGRESS);
        when(jobRecordRepository.findById(jobId)).thenReturn(Optional.of(jobRecord));

        // Act & Assert
        assertThrows(ExportFileNotFoundException.class, () ->
                exportService.getExportFile(jobId)
        );
    }

    @Test
    void getExportFile_ShouldThrowException_WhenJobDoesNotExist() {
        // Arrange
        UUID jobId = UUID.randomUUID();
        when(jobRecordRepository.findById(jobId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(JobRecordNotFoundException.class, () ->
                exportService.getExportFile(jobId)
        );
    }
}
