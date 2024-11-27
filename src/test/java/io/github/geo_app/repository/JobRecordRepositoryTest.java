package io.github.geo_app.repository;

import io.github.geo_app.model.JobRecord;
import io.github.geo_app.model.JobStatus;
import io.github.geo_app.model.JobType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@Transactional
@DataJpaTest
@TestInstance(PER_CLASS)
class JobRecordRepositoryTest {

    @Autowired
    private JobRecordRepository jobRecordRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private JobRecord job1;

    @BeforeEach
    void setUp() {
        job1 = JobRecord.builder()
                .type(JobType.IMPORT)
                .status(JobStatus.IN_PROGRESS)
                .startedAt(LocalDateTime.now())
                .build();

        JobRecord job2 = JobRecord.builder()
                .type(JobType.EXPORT)
                .status(JobStatus.DONE)
                .startedAt(LocalDateTime.now().minusHours(1))
                .finishedAt(LocalDateTime.now())
                .build();

        testEntityManager.persist(job1);
        testEntityManager.persist(job2);
        testEntityManager.flush();
    }

    @Test
    void testFindJobRecordsByTypeAndStatus_ShouldReturnMatchingJobs() {
        // Act
        List<JobRecord> result = jobRecordRepository.findJobRecordsByTypeAndStatus(JobType.IMPORT, JobStatus.IN_PROGRESS);

        // Assert
        assertEquals(1, result.size());
        assertEquals(job1.getId(), result.getFirst().getId());
    }

    @Test
    void testFindJobRecordsByTypeAndStatus_ShouldReturnEmptyList() {
        // Act
        List<JobRecord> result = jobRecordRepository.findJobRecordsByTypeAndStatus(JobType.IMPORT, JobStatus.DONE);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testCreateJobRecord_ShouldPersistJobRecord() {
        // Arrange
        JobRecord newJob = JobRecord.builder()
                .id(UUID.randomUUID())
                .type(JobType.IMPORT)
                .status(JobStatus.ERROR)
                .startedAt(LocalDateTime.now())
                .build();

        // Act
        JobRecord savedJob = jobRecordRepository.save(newJob);

        // Assert
        assertNotNull(savedJob.getId());
        JobRecord foundJob = testEntityManager.find(JobRecord.class, savedJob.getId());
        assertNotNull(foundJob);
        assertEquals(JobStatus.ERROR, foundJob.getStatus());
    }

    @Test
    void testReadJobRecord_ShouldRetrieveJobRecord() {
        // Act
        JobRecord foundJob = jobRecordRepository.findById(job1.getId()).orElse(null);

        // Assert
        assertNotNull(foundJob);
        assertEquals(job1.getId(), foundJob.getId());
    }

    @Test
    void testUpdateJobRecord_ShouldModifyExistingRecord() {
        // Act
        job1.setStatus(JobStatus.DONE);
        JobRecord updatedJob = jobRecordRepository.save(job1);

        // Assert
        JobRecord foundJob = testEntityManager.find(JobRecord.class, updatedJob.getId());
        assertNotNull(foundJob);
        assertEquals(JobStatus.DONE, foundJob.getStatus());
    }

    @Test
    void testDeleteJobRecord_ShouldRemoveJobRecord() {
        // Act
        jobRecordRepository.delete(job1);
        testEntityManager.flush();

        // Assert
        JobRecord deletedJob = testEntityManager.find(JobRecord.class, job1.getId());
        assertNull(deletedJob);
    }

    @Test
    void testFindJobRecordById_ShouldReturnEmptyWhenNotFound() {
        // Act
        JobRecord foundJob = jobRecordRepository.findById(UUID.randomUUID()).orElse(null);

        // Assert
        assertNull(foundJob);
    }
}
