package io.github.geo_app.controller;

import io.github.geo_app.exceptions.SectionNotFoundException;
import io.github.geo_app.model.GeoClass;
import io.github.geo_app.model.Section;
import io.github.geo_app.service.GeoClassService;
import io.github.geo_app.service.SectionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestInstance(PER_CLASS)
class SectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SectionService sectionService;

    @MockBean
    private GeoClassService geoClassService;

    @Test
    void getSections_ShouldReturnPagedSections() throws Exception {
        // Arrange
        Section section = new Section(1L, "Section1", null);
        Page<Section> sectionPage = new PageImpl<>(Collections.singletonList(section));
        when(sectionService.getAllSections(any())).thenReturn(sectionPage);

        // Act & Assert
        mockMvc.perform(get("/api/v1/sections")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Section1"));

        verify(sectionService, times(1)).getAllSections(any());
    }

    @Test
    void getSectionById_ShouldReturnSection() throws Exception {
        // Arrange
        Section section = new Section(1L, "Section1", null);
        when(sectionService.getSectionById(1L)).thenReturn(section);

        // Act & Assert
        mockMvc.perform(get("/api/v1/sections/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Section1"));

        verify(sectionService, times(1)).getSectionById(1L);
    }

    @Test
    void getSectionById_ShouldReturnNotFound_WhenSectionNotFound() throws Exception {
        // Arrange
        Section section = new Section(1L, "Section1", null);
        when(sectionService.getSectionById(1L)).thenThrow(SectionNotFoundException.class);

        // Act & Assert
        mockMvc.perform(get("/api/v1/sections/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(sectionService, times(1)).getSectionById(1L);
    }

    @Test
    void getSectionById_ShouldReturnInternalServerError_WhenSomethingWentWrong() throws Exception {
        // Arrange
        Section section = new Section(1L, "Section1", null);
        when(sectionService.getSectionById(1L)).thenThrow(RuntimeException.class);

        // Act & Assert
        mockMvc.perform(get("/api/v1/sections/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(sectionService, times(1)).getSectionById(1L);
    }

    @Test
    void createSection_ShouldReturnCreatedSection() throws Exception {
        // Arrange
        Section section = new Section(null, "Section1", new HashSet<>());
        Section savedSection = new Section(1L, "Section1", new HashSet<>());
        when(sectionService.createSection(any())).thenReturn(savedSection);

        // Act & Assert
        mockMvc.perform(post("/api/v1/sections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Section1\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Section1"));

        verify(sectionService, times(1)).createSection(section);
    }

    @Test
    void createSection_ShouldReturnCreatedSectionWithGeoClasses() throws Exception {
        // Arrange
        GeoClass geoClass = new GeoClass(null, "GeoClass1", "GC1");
        GeoClass savedGeoClass = new GeoClass(1L, "GeoClass1", "GC1");
        Section section = new Section(null, "Section1", new HashSet<>(Collections.singletonList(geoClass)));
        Section savedSection = new Section(1L, "Section1", new HashSet<>(Collections.singletonList(savedGeoClass)));
        when(sectionService.createSection(any())).thenReturn(savedSection);

        // Act & Assert
        mockMvc.perform(post("/api/v1/sections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Section1\", \"geoClasses\": [{\"name\": \"GeoClass1\", \"code\": \"GC1\"}]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Section1"))
                .andExpect(jsonPath("$.geoClasses[0].name").value("GeoClass1"))
                .andExpect(jsonPath("$.geoClasses[0].code").value("GC1"));

        verify(sectionService, times(1)).createSection(section);
    }

    @Test
    void createSection_ShouldReturnBadRequestIfDataIsInvalid() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/sections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{INVALID}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSection_ShouldReturnBadRequestIfNameIsNull() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/sections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"{\\\"name\\\": \\\"\\\"}\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateSection_ShouldReturnUpdatedSection() throws Exception {
        // Arrange
        Section updatedSection = new Section(1L, "UpdatedSection", null);
        when(sectionService.updateSection(eq(1L), any())).thenReturn(updatedSection);

        // Act & Assert
        mockMvc.perform(put("/api/v1/sections/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"UpdatedSection\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("UpdatedSection"));

        verify(sectionService, times(1)).updateSection(eq(1L), any());
    }

    @Test
    void updateSection_ShouldReturnBadRequestIfDataIsInvalid() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/v1/sections/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{INVALID}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteSection_ShouldReturnNoContent() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/v1/sections/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(sectionService, times(1)).deleteSection(1L);
    }

    @Test
    void getGeoClassesBySectionId_ShouldReturnGeoClasses() throws Exception {
        // Arrange
        GeoClass geoClass1 = new GeoClass(1L, "GeoClass1", "GC1");
        GeoClass geoClass2 = new GeoClass(2L, "GeoClass2", "GC2");
        when(geoClassService.getGeoClassesBySectionId(1L)).thenReturn(Arrays.asList(geoClass1, geoClass2));

        // Act & Assert
        mockMvc.perform(get("/api/v1/sections/1/geo-classes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("GeoClass1"))
                .andExpect(jsonPath("$[1].name").value("GeoClass2"));

        verify(geoClassService, times(1)).getGeoClassesBySectionId(1L);
    }

    @Test
    void getSectionsByGeoClassCode_ShouldReturnSections() throws Exception {
        // Arrange
        Section section = new Section(1L, "Section1", Collections.EMPTY_SET);
        when(sectionService.findSectionsByGeoClassCode("GC1")).thenReturn(Collections.singletonList(section));

        // Act & Assert
        mockMvc.perform(get("/api/v1/sections/by-code")
                        .param("code", "GC1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Section1"));

        verify(sectionService, times(1)).findSectionsByGeoClassCode("GC1");
    }
}
