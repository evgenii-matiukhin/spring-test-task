package io.github.geo_app.service;

import io.github.geo_app.exceptions.AlreadyPendingImportJobException;
import io.github.geo_app.exceptions.JobRecordNotFoundException;
import io.github.geo_app.model.JobRecord;
import io.github.geo_app.model.JobStatus;
import io.github.geo_app.model.JobType;
import io.github.geo_app.model.Section;
import io.github.geo_app.repository.GeoClassRepository;
import io.github.geo_app.repository.JobRecordRepository;
import io.github.geo_app.repository.SectionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ImportServiceTest {

    @InjectMocks
    private ImportService importService;

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private GeoClassRepository geoClassRepository;

    @Mock
    private JobRecordRepository jobRecordRepository;

    @Mock
    private XLSReader xlsReader;

    @Mock
    private MultipartFile multipartFile;

    @Captor
    private ArgumentCaptor<JobRecord> jobRecordCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testImportData_Success() {
        // Arrange
        when(jobRecordRepository.findJobRecordsByTypeAndStatus(JobType.IMPORT, JobStatus.IN_PROGRESS))
                .thenReturn(Collections.emptyList());
        UUID expectedJobId = UUID.randomUUID();
        JobRecord jobRecord = JobRecord.builder()
                .id(expectedJobId)
                .type(JobType.IMPORT)
                .status(JobStatus.IN_PROGRESS)
                .startedAt(LocalDateTime.now())
                .build();
        when(jobRecordRepository.save(any()))
                .thenReturn(jobRecord);

        // Act
        UUID jobId = importService.importData(multipartFile);

        // Assert
        assertEquals(expectedJobId, jobId);
        verify(jobRecordRepository, times(1)).save(jobRecord);
    }

    @Test
    void testImportData_AlreadyPendingJob() {
        // Arrange
        JobRecord existingJob = new JobRecord();
        existingJob.setId(UUID.randomUUID());
        existingJob.setStatus(JobStatus.IN_PROGRESS);
        existingJob.setType(JobType.IMPORT);

        when(jobRecordRepository.findJobRecordsByTypeAndStatus(JobType.IMPORT, JobStatus.IN_PROGRESS))
                .thenReturn(Collections.singletonList(existingJob));

        // Act & Assert
        assertThrows(AlreadyPendingImportJobException.class, () -> {
            importService.importData(multipartFile);
        });

        verify(jobRecordRepository, never()).save(any(JobRecord.class));
    }

    @Test
    void testGetImportJobStatus_Success() {
        // Arrange
        UUID jobId = UUID.randomUUID();
        JobRecord jobRecord = JobRecord.builder()
                .id(jobId)
                .type(JobType.IMPORT)
                .status(JobStatus.DONE)
                .startedAt(LocalDateTime.now())
                .finishedAt(LocalDateTime.now())
                .build();

        when(jobRecordRepository.findById(jobId)).thenReturn(Optional.of(jobRecord));

        // Act
        JobStatus status = importService.getImportJobStatus(jobId);

        // Assert
        assertEquals(JobStatus.DONE, status);
        verify(jobRecordRepository, times(1)).findById(jobId);
    }

    @Test
    void testGetImportJobStatus_NotFound() {
        // Arrange
        UUID jobId = UUID.randomUUID();

        when(jobRecordRepository.findById(jobId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(JobRecordNotFoundException.class, () -> {
            importService.getImportJobStatus(jobId);
        });

        verify(jobRecordRepository, times(1)).findById(jobId);
    }

    @Test
    void testRunImportJob_Success() throws Exception {
        // Arrange
        JobRecord jobRecord = new JobRecord();
        jobRecord.setId(UUID.randomUUID());
        jobRecord.setStatus(JobStatus.IN_PROGRESS);

        Section section = new Section();
        section.setName("Section 1");
        section.setGeoClasses(new HashSet<>());

        List<Section> sections = Collections.singletonList(section);

        when(xlsReader.parse(any())).thenReturn(sections);

        // Act
        CompletableFuture<Void> future = importService.runImportJob(jobRecord, multipartFile);

        // Wait for async process to complete
        future.join();

        // Assert
        verify(sectionRepository, times(1)).save(any(Section.class));
        verify(jobRecordRepository, atLeastOnce()).save(jobRecordCaptor.capture());

        // Verify that jobRecord status was updated to DONE
        List<JobRecord> savedJobRecords = jobRecordCaptor.getAllValues();
        assertTrue(savedJobRecords.stream().anyMatch(jr -> jr.getStatus() == JobStatus.DONE));
    }

    @Test
    void testRunImportJob_FailureDuringParsing() throws Exception {
        // Arrange
        JobRecord jobRecord = new JobRecord();
        jobRecord.setId(UUID.randomUUID());
        jobRecord.setStatus(JobStatus.IN_PROGRESS);

        when(xlsReader.parse(multipartFile)).thenThrow(new IOException("Parsing error"));
        when(jobRecordRepository.save(any(JobRecord.class))).thenReturn(jobRecord);

        // Act
        CompletableFuture<Void> future = importService.runImportJob(jobRecord, multipartFile);

        // Wait for async process to complete
        future.join();

        // Assert
        verify(sectionRepository, never()).save(any(Section.class));
        verify(jobRecordRepository, atLeastOnce()).save(jobRecordCaptor.capture());

        // Verify that jobRecord status was updated to ERROR
        List<JobRecord> savedJobRecords = jobRecordCaptor.getAllValues();
        assertTrue(savedJobRecords.stream().anyMatch(jr -> jr.getStatus() == JobStatus.ERROR));
    }

//    @Test
//    void testSaveSectionsInTransaction_Success() {
//        // Arrange
//        GeoClass newGeoClass = new GeoClass();
//        newGeoClass.setCode("GC1");
//        newGeoClass.setName("Geo Class 1");
//
//        GeoClass existingGeoClass = new GeoClass();
//        existingGeoClass.setId(UUID.randomUUID());
//        existingGeoClass.setCode("GC2");
//        existingGeoClass.setName("Geo Class 2");
//
//        Section section = new Section();
//        section.setName("Section 1");
//        section.setGeoClasses(new HashSet<>(Arrays.asList(newGeoClass, existingGeoClass)));
//
//        when(geoClassRepository.findByCode("GC1")).thenReturn(null);
//        when(geoClassRepository.findByCode("GC2")).thenReturn(existingGeoClass);
//        when(sectionRepository.save(any(Section.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        // Act
//        importService.saveSectionsInTransaction(Collections.singletonList(section));
//
//        // Assert
//        verify(geoClassRepository, times(1)).findByCode("GC1");
//        verify(geoClassRepository, times(1)).findByCode("GC2");
//        verify(sectionRepository, times(1)).save(any(Section.class));
//
//        // Verify that section's geoClasses contain the correct instances
//        assertEquals(2, section.getGeoClasses().size());
//        assertTrue(section.getGeoClasses().contains(existingGeoClass));
//        assertTrue(section.getGeoClasses().stream().anyMatch(gc -> gc.getCode().equals("GC1")));
//    }
}
