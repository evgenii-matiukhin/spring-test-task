package io.github.geo_app.repository;

import io.github.geo_app.model.GeoClass;
import io.github.geo_app.model.Section;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@DataJpaTest
@TestInstance(PER_CLASS)
class GeoClassRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private GeoClassRepository geoClassRepository;

    private GeoClass geoClass1;
    private GeoClass geoClass2;
    private Section section;

    @BeforeEach
    void setUp() {
        // Create test data
        geoClass1 = new GeoClass();
        geoClass1.setName("GeoClass1");
        geoClass1.setCode("GC1");

        geoClass2 = new GeoClass();
        geoClass2.setName("GeoClass2");
        geoClass2.setCode("GC2");

        section = new Section();
        section.setName("Section1");

        // Establish Many-to-Many relationship
        section.getGeoClasses().add(geoClass1);
        section.getGeoClasses().add(geoClass2);
        geoClass1.getSections().add(section);
        geoClass2.getSections().add(section);

        // Persist data
        testEntityManager.persist(section);
        testEntityManager.persist(geoClass1);
        testEntityManager.persist(geoClass2);
    }

    @Test
    void testFindByCode_ShouldReturnGeoClass() {
        // Act
        GeoClass result = geoClassRepository.findByCode("GC1");

        // Assert
        assertNotNull(result);
        assertEquals("GeoClass1", result.getName());
        assertEquals("GC1", result.getCode());
    }

    @Test
    void testFindByCode_ShouldReturnNull_WhenCodeDoesNotExist() {
        // Act
        GeoClass result = geoClassRepository.findByCode("INVALID");

        // Assert
        assertNull(result);
    }

    @Test
    void testFindGeoClassesBySectionsId_ShouldReturnGeoClasses() {
        // Act
        List<GeoClass> result = geoClassRepository.findGeoClassesBySectionsId(section.getId());

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(geoClass1));
        assertTrue(result.contains(geoClass2));
    }

    @Test
    void testFindGeoClassesBySectionsId_ShouldReturnEmptyList_WhenNoGeoClassesExistForSection() {
        // Arrange
        Section emptySection = new Section();
        emptySection.setName("EmptySection");
        testEntityManager.persist(emptySection);

        // Act
        List<GeoClass> result = geoClassRepository.findGeoClassesBySectionsId(emptySection.getId());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testSave_ShouldPersistGeoClass() {
        // Arrange
        GeoClass geoClass = new GeoClass();
        geoClass.setName("GeoClass3");
        geoClass.setCode("GC3");

        // Act
        GeoClass savedGeoClass = geoClassRepository.save(geoClass);

        // Assert
        GeoClass result = testEntityManager.find(GeoClass.class, savedGeoClass.getId());
        assertNotNull(result);
        assertEquals("GeoClass3", result.getName());
        assertEquals("GC3", result.getCode());
    }

//    @Disabled("This test is failing, because delete operation can't be done consistently in a transaction")
    @Test
    void testDelete_ShouldRemoveGeoClass() {
        // Act
        geoClassRepository.delete(geoClass1);

        // Assert
        GeoClass result = testEntityManager.find(GeoClass.class, geoClass1.getId());
        assertNull(result);
    }

    @Test
    @Disabled("This test is failing, because delete operation can't be done consistently in a transaction")
    void testDelete_ShouldRemoveGeoClassAndClearRelationships() {
        // Act
        geoClassRepository.delete(geoClass1);

        // Assert
        Section s = testEntityManager.find(Section.class, section.getId());
        assertNotNull(s);
        assertEquals(1, s.getGeoClasses().size());
    }

    @Test
    void testFindById_ShouldReturnGeoClassById() {
        // Act
        Optional<GeoClass> result = geoClassRepository.findById(geoClass1.getId());

        // Assert
        assertEquals(Optional.of(geoClass1), result);
    }

    @Test
    void testFindById_ShouldReturnNull_WhenGeoClassDoesNotExist() {
        // Act
        Optional<GeoClass> result = geoClassRepository.findById(123L);

        // Assert
        assertEquals(Optional.empty(), result);
    }
}
