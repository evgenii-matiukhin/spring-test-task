package io.github.geo_app.service;

import io.github.geo_app.exceptions.SectionNotFoundException;
import io.github.geo_app.model.GeoClass;
import io.github.geo_app.model.Section;
import io.github.geo_app.repository.GeoClassRepository;
import io.github.geo_app.repository.SectionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SectionServiceTest {

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private GeoClassRepository geoClassRepository;

    @InjectMocks
    private SectionService sectionService;

    private Section section;
    private GeoClass geoClass1;
    private GeoClass geoClass2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        geoClass1 = new GeoClass(1L, "GeoClass1", "GC1");
        geoClass2 = new GeoClass(2L, "GeoClass2", "GC2");

        section = new Section(1L, "Section1", new HashSet<>(Arrays.asList(geoClass1, geoClass2)));
    }

    @Test
    void getAllSections_ShouldReturnPagedSections() {
        // Arrange
        Section section2 = new Section(2L, "Section2", new HashSet<>(Collections.singletonList(geoClass1)));
        Section section3 = new Section(3L, "Section3", new HashSet<>(Collections.singletonList(geoClass1)));
        Page<Section> sectionsPage = new PageImpl<>(Arrays.asList(section, section2, section3));
        Pageable pageable = PageRequest.of(0, 10);

        when(sectionRepository.findAll(pageable)).thenReturn(sectionsPage);

        // Act
        Page<Section> result = sectionService.getAllSections(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        assertEquals("Section1", result.getContent().getFirst().getName());
        verify(sectionRepository, times(1)).findAll(pageable);
    }

    @Test
    void getSectionById_ShouldReturnSection() {
        // Arrange
        when(sectionRepository.findByIdWithGeoClasses(1L)).thenReturn(Optional.of(section));

        // Act
        Section result = sectionService.getSectionById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Section1", result.getName());
        assertEquals(2, result.getGeoClasses().size());
        verify(sectionRepository, times(1)).findByIdWithGeoClasses(1L);
    }

    @Test
    void getSectionById_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(sectionRepository.findByIdWithGeoClasses(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(SectionNotFoundException.class, () -> sectionService.getSectionById(1L));
        verify(sectionRepository, times(1)).findByIdWithGeoClasses(1L);
    }

    @Test
    void createSection_ShouldSaveAndReturnSection() {
        // Arrange
        when(geoClassRepository.findByCode("GC1")).thenReturn(geoClass1);
        when(geoClassRepository.findByCode("GC2")).thenReturn(geoClass2);
        when(sectionRepository.save(any(Section.class))).thenReturn(section);

        // Act
        Section result = sectionService.createSection(section);

        // Assert
        assertNotNull(result);
        assertEquals("Section1", result.getName());
        assertEquals(2, result.getGeoClasses().size());
        verify(sectionRepository, times(1)).save(any(Section.class));
    }

    @Test
    void updateSection_ShouldUpdateAndReturnSection() {
        // Arrange
        Section updatedSection = new Section(null, "UpdatedSection", new HashSet<>(Collections.singleton(geoClass1)));
        when(sectionRepository.findById(1L)).thenReturn(Optional.of(section));
        when(geoClassRepository.findByCode("GC1")).thenReturn(geoClass1);
        when(sectionRepository.save(any(Section.class))).thenReturn(updatedSection);

        // Act
        Section result = sectionService.updateSection(1L, updatedSection);

        // Assert
        assertNotNull(result);
        assertEquals("UpdatedSection", result.getName());
        assertEquals(1, result.getGeoClasses().size());
        verify(sectionRepository, times(1)).findById(1L);
        verify(sectionRepository, times(1)).save(any(Section.class));
    }

    @Test
    void deleteSection_ShouldDeleteSection() {
        // Act
        sectionService.deleteSection(1L);

        // Assert
        verify(sectionRepository, times(1)).deleteById(1L);
    }

    @Test
    void findSectionsByGeoClassCode_ShouldReturnSections() {
        // Arrange
        when(sectionRepository.findSectionsByGeoClassesCode("GC1")).thenReturn(Collections.singletonList(section));

        // Act
        List<Section> result = sectionService.findSectionsByGeoClassCode("GC1");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Section1", result.get(0).getName());
        verify(sectionRepository, times(1)).findSectionsByGeoClassesCode("GC1");
    }
}
