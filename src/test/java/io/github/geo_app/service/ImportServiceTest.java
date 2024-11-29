package io.github.geo_app.service;

import io.github.geo_app.exceptions.AlreadyPendingImportJobException;
import io.github.geo_app.exceptions.JobFailedException;
import io.github.geo_app.model.GeoClass;
import io.github.geo_app.model.JobContext;
import io.github.geo_app.model.JobRecord;
import io.github.geo_app.model.JobStatus;
import io.github.geo_app.model.JobType;
import io.github.geo_app.model.Section;
import io.github.geo_app.repository.GeoClassRepository;
import io.github.geo_app.repository.JobRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ImportServiceTest {

    @InjectMocks
    private ImportService importService;

    @Mock
    private SectionService sectionService;

    @Mock
    private GeoClassRepository geoClassRepository;

    @Mock
    private JobRecordRepository jobRecordRepository;

    @Mock
    private XLSReader xlsReader;

    private JobRecord jobRecord;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock job record
        jobRecord = JobRecord.builder()
                .id(UUID.randomUUID())
                .status(JobStatus.IN_PROGRESS)
                .type(JobType.IMPORT)
                .startedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void validate_ShouldThrowException_WhenJobAlreadyInProgress() {
        // Arrange
        when(jobRecordRepository.findJobRecordsByTypeAndStatus(eq(JobType.IMPORT), eq(JobStatus.IN_PROGRESS)))
                .thenReturn(Collections.singletonList(jobRecord));
        MultipartFile file = new MockMultipartFile("file", "test.xls", "application/vnd.ms-excel", new byte[]{});

        // Act & Assert
        assertThrows(AlreadyPendingImportJobException.class, () ->
                importService.validate(new JobContext(JobType.IMPORT, file))
        );
    }

    @Test
    void saveSectionsInTransaction_ShouldSaveSectionsAndGeoClasses() {
        // Arrange
        Section section1 = new Section(1L, "Section1", new HashSet<>());
        Section section2 = new Section(2L, "Section2", new HashSet<>());

        GeoClass geoClass1 = new GeoClass(null, "GeoClass1", "GC1");
        GeoClass geoClass2 = new GeoClass(null, "GeoClass2", "GC2");

        section1.getGeoClasses().add(geoClass1);
        section1.getGeoClasses().add(geoClass2);

        when(geoClassRepository.findByCode("GC1")).thenReturn(geoClass1);
        when(geoClassRepository.findByCode("GC2")).thenReturn(null);

        // Act
        importService.saveSectionsInTransaction(Arrays.asList(section1, section2));

        // Assert
        verify(sectionService, times(2)).createSection(any(Section.class));
    }

    @Test
    void runAsync_ShouldSaveParsedSections() throws IOException {
        // Arrange
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.xls", "application/vnd.ms-excel", new byte[]{});
        Section section = new Section(1L, "Section1", new HashSet<>());

        when(xlsReader.parse(any(ByteArrayInputStream.class))).thenReturn(Collections.singletonList(section));

        // Act
        importService.runAsync(jobRecord, new JobContext(JobType.IMPORT, mockFile));

        // Assert
        verify(sectionService, times(1)).createSection(any(Section.class));
    }

    @Test
    void runAsync_ShouldThrowException_OnIOException() throws IOException {
        // Arrange
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.xls", "application/vnd.ms-excel", new byte[]{});
        when(xlsReader.parse(any(ByteArrayInputStream.class))).thenThrow(new IOException("Simulated IO Error"));

        // Act & Assert
        JobFailedException exception = assertThrows(JobFailedException.class, () ->
                importService.runAsync(jobRecord, new JobContext(JobType.IMPORT, mockFile))
        );
        assertEquals("Simulated IO Error", exception.getMessage());
    }
}
