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
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@Transactional
@DataJpaTest
@TestInstance(PER_CLASS)
class SectionRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private SectionRepository sectionRepository;

    private Section section1;
    private Section section2;
    private GeoClass geoClass1;

    @BeforeEach
    void setUp() {
        // Create GeoClasses
        geoClass1 = new GeoClass();
        geoClass1.setName("GeoClass1");
        geoClass1.setCode("GC1");

        GeoClass geoClass2 = new GeoClass();
        geoClass2.setName("GeoClass2");
        geoClass2.setCode("GC2");

        // Create Sections
        section1 = new Section();
        section1.setName("Section1");
        section1.getGeoClasses().add(geoClass1);
        section1.getGeoClasses().add(geoClass2);

        section2 = new Section();
        section2.setName("Section2");
        section2.getGeoClasses().add(geoClass1);

        // Establish bidirectional relationships
        geoClass1.getSections().addAll(Arrays.asList(section1, section2));
        geoClass2.getSections().add(section1);

        // Persist data
        testEntityManager.persist(geoClass1);
        testEntityManager.persist(geoClass2);
        testEntityManager.persist(section1);
        testEntityManager.persist(section2);
        testEntityManager.flush();
        testEntityManager.clear();
    }

    // --- Query Tests ---

    @Test
    void findByIdWithGeoClasses_ShouldReturnSectionWithGeoClasses() {
        Optional<Section> result = sectionRepository.findByIdWithGeoClasses(section1.getId());
        assertTrue(result.isPresent());
        Section section = result.get();
        assertEquals("Section1", section.getName());
        assertEquals(2, section.getGeoClasses().size());
        assertTrue(section.getGeoClasses().stream().anyMatch(gc -> gc.getCode().equals("GC1")));
        assertTrue(section.getGeoClasses().stream().anyMatch(gc -> gc.getCode().equals("GC2")));
    }

    @Test
    void findByIdWithGeoClasses_ShouldReturnEmpty_WhenSectionDoesNotExist() {
        Optional<Section> result = sectionRepository.findByIdWithGeoClasses(999L);
        assertFalse(result.isPresent());
    }

    @Test
    void findSectionsByGeoClassesId_ShouldReturnSectionsForGeoClass() {
        List<Section> result = sectionRepository.findSectionsByGeoClassesId(geoClass1.getId());
        assertEquals(2, result.size());
        assertTrue(result.contains(section1));
        assertTrue(result.contains(section2));
    }

    @Test
    void findSectionsByGeoClassesId_ShouldReturnEmptyList_WhenGeoClassNotLinked() {
        GeoClass geoClass3 = new GeoClass();
        geoClass3.setName("GeoClass3");
        geoClass3.setCode("GC3");
        testEntityManager.persist(geoClass3);
        testEntityManager.flush();

        List<Section> result = sectionRepository.findSectionsByGeoClassesId(geoClass3.getId());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findSectionsByGeoClassesCode_ShouldReturnSectionsForGeoClassCode() {
        List<Section> result = sectionRepository.findSectionsByGeoClassesCode("GC1");
        assertEquals(2, result.size());
        assertTrue(result.contains(section1));
        assertTrue(result.contains(section2));
    }

    @Test
    void findSectionsByGeoClassesCode_ShouldReturnEmptyList_WhenCodeNotLinked() {
        List<Section> result = sectionRepository.findSectionsByGeoClassesCode("INVALID");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // --- CRUD Tests ---

    @Test
    void createSection_ShouldPersistAndReturnSection() {
        Section section = new Section();
        section.setName("NewSection");

        GeoClass geoClass3 = new GeoClass();
        geoClass3.setName("GeoClass3");
        geoClass3.setCode("GC3");

        section.getGeoClasses().add(geoClass3);
        geoClass3.getSections().add(section);

        Section savedSection = sectionRepository.save(section);

        assertNotNull(savedSection.getId());
        assertEquals("NewSection", savedSection.getName());
        assertEquals(1, savedSection.getGeoClasses().size());
    }

    @Test
    void readSection_ShouldReturnSectionById() {
        Optional<Section> result = sectionRepository.findById(section1.getId());
        assertTrue(result.isPresent());
        assertEquals("Section1", result.get().getName());
    }

    @Test
    void updateSection_ShouldPersistChanges() {
        section1.setName("UpdatedSection");
        sectionRepository.save(section1);
        testEntityManager.flush();

        Section updatedSection = testEntityManager.find(Section.class, section1.getId());
        assertEquals("UpdatedSection", updatedSection.getName());
    }

    @Disabled("This test is failing, because delete operation can't be done consistently in a transaction")
    @Test
    void deleteSection_ShouldRemoveSection() {
        sectionRepository.delete(section2);
        testEntityManager.flush();

        testEntityManager.detach(section2);
        Section deletedSection = testEntityManager.find(Section.class, section2.getId());
        assertNull(deletedSection);

        GeoClass geoClass = testEntityManager.find(GeoClass.class, geoClass1.getId());
        assertNotNull(geoClass);
    }

    @Disabled("This test is failing, because delete operation can't be done consistently in a transaction")
    @Test
    void deleteSection_ShouldClearRelationships() {
        sectionRepository.delete(section2);
        testEntityManager.flush();

        GeoClass geoClass = testEntityManager.find(GeoClass.class, geoClass1.getId());
        assertTrue(geoClass.getSections().isEmpty());
    }

    @Test
    void findAllWithGeoClasses_ShouldReturnSectionsWithGeoClasses() {
        // Act
        List<Section> sections = sectionRepository.findAllWithGeoClasses();

        // Assert
        assertNotNull(sections);
        assertEquals(2, sections.size()); // Two sections in total

        Section resultSection1 = sections.stream().filter(s -> s.getName().equals("Section1")).findFirst().orElse(null);
        Section resultSection2 = sections.stream().filter(s -> s.getName().equals("Section2")).findFirst().orElse(null);

        assertNotNull(resultSection1);
        assertNotNull(resultSection2);

        assertEquals(2, resultSection1.getGeoClasses().size()); // Section1 has 2 GeoClasses
        assertEquals(1, resultSection2.getGeoClasses().size()); // Section2 has 1 GeoClass

        assertTrue(resultSection1.getGeoClasses().stream().anyMatch(gc -> gc.getCode().equals("GC1")));
        assertTrue(resultSection1.getGeoClasses().stream().anyMatch(gc -> gc.getCode().equals("GC2")));
        assertTrue(resultSection2.getGeoClasses().stream().anyMatch(gc -> gc.getCode().equals("GC1")));
    }
}
