package io.github.geo_app.service;

import io.github.geo_app.exceptions.GeoClassNotFoundException;
import io.github.geo_app.model.GeoClass;
import io.github.geo_app.repository.GeoClassRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GeoClassServiceTest {

    @Mock
    private GeoClassRepository geoClassRepository;

    @InjectMocks
    private GeoClassService geoClassService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllGeoClasses_ShouldReturnPageOfGeoClasses() {
        // Arrange
        PageRequest pageable = PageRequest.of(0, 10);
        GeoClass geoClass1 = new GeoClass(1L, "GeoClass1", "GC11");
        GeoClass geoClass2 = new GeoClass(2L, "GeoClass2", "GC21");
        Page<GeoClass> geoClassPage = new PageImpl<>(Arrays.asList(geoClass1, geoClass2));
        when(geoClassRepository.findAll(pageable)).thenReturn(geoClassPage);

        // Act
        Page<GeoClass> result = geoClassService.getAllGeoClasses(pageable);

        // Assert
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().contains(geoClass1));
        assertTrue(result.getContent().contains(geoClass2));
        verify(geoClassRepository, times(1)).findAll(pageable);
    }

    @Test
    void getGeoClassById_ShouldReturnGeoClass_WhenFound() {
        // Arrange
        GeoClass geoClass = new GeoClass(1L, "GeoClass1", "GC11");
        when(geoClassRepository.findById(1L)).thenReturn(Optional.of(geoClass));

        // Act
        GeoClass result = geoClassService.getGeoClassById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("GeoClass1", result.getName());
        verify(geoClassRepository, times(1)).findById(1L);
    }

    @Test
    void getGeoClassById_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(geoClassRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(GeoClassNotFoundException.class, () -> geoClassService.getGeoClassById(1L));
        verify(geoClassRepository, times(1)).findById(1L);
    }

    @Test
    void createGeoClass_ShouldSaveAndReturnGeoClass() {
        // Arrange
        GeoClass geoClass = new GeoClass(null, "GeoClass1", "GC11");
        GeoClass savedGeoClass = new GeoClass(1L, "GeoClass1", "GC11");
        when(geoClassRepository.save(geoClass)).thenReturn(savedGeoClass);

        // Act
        GeoClass result = geoClassService.createGeoClass(geoClass);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("GeoClass1", result.getName());
        verify(geoClassRepository, times(1)).save(geoClass);
    }

    @Test
    void updateGeoClass_ShouldUpdateAndReturnGeoClass() {
        // Arrange
        GeoClass existingGeoClass = new GeoClass(1L, "GeoClass1", "GC11");
        GeoClass updatedGeoClass = new GeoClass(null, "UpdatedGeoClass", "GC11");
        when(geoClassRepository.findById(1L)).thenReturn(Optional.of(existingGeoClass));
        when(geoClassRepository.save(existingGeoClass)).thenReturn(existingGeoClass);

        // Act
        GeoClass result = geoClassService.updateGeoClass(1L, updatedGeoClass);

        // Assert
        assertNotNull(result);
        assertEquals("UpdatedGeoClass", result.getName());
        verify(geoClassRepository, times(1)).findById(1L);
        verify(geoClassRepository, times(1)).save(existingGeoClass);
    }

    @Test
    void deleteGeoClass_ShouldDeleteGeoClass() {
        // Act
        geoClassService.deleteGeoClass(1L);

        // Assert
        verify(geoClassRepository, times(1)).deleteById(1L);
    }

    @Test
    void getGeoClassesBySectionId_ShouldReturnListOfGeoClasses() {
        // Arrange
        GeoClass geoClass1 = new GeoClass(1L, "GeoClass1", "GC11");
        GeoClass geoClass2 = new GeoClass(2L, "GeoClass2", "GC21");
        when(geoClassRepository.findGeoClassesBySectionsId(1L)).thenReturn(Arrays.asList(geoClass1, geoClass2));

        // Act
        List<GeoClass> result = geoClassService.getGeoClassesBySectionId(1L);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(geoClass1));
        assertTrue(result.contains(geoClass2));
        verify(geoClassRepository, times(1)).findGeoClassesBySectionsId(1L);
    }
}
