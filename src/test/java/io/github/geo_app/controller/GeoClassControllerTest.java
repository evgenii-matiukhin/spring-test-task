package io.github.geo_app.controller;

import io.github.geo_app.exceptions.GeoClassNotFoundException;
import io.github.geo_app.model.GeoClass;
import io.github.geo_app.service.GeoClassService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Base64;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestInstance(PER_CLASS)
class GeoClassControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GeoClassService geoClassService;

    @Test
    void createGeoClass_ShouldReturnUnauthorizedIfNoAuthHeader() throws Exception {
        mockMvc.perform(post("/api/v1/geo-classes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"GeoClass1\", \"code\": \"GC11\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createGeoClass_ShouldReturnUnauthorizedIfInvalidAuthHeader() throws Exception {
        mockMvc.perform(post("/api/v1/geo-classes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("invalid:password".getBytes()))
                        .content("{\"name\": \"GeoClass1\", \"code\": \"GC11\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createGeoClass_ShouldReturnForbiddenIfUserHasNoRole() throws Exception {
        mockMvc.perform(post("/api/v1/geo-classes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("user:password".getBytes()))
                        .content("{\"name\": \"GeoClass1\", \"code\": \"GC11\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getGeoClasses_ShouldReturnPagedGeoClasses() throws Exception {
        // Arrange
        GeoClass geoClass1 = new GeoClass(1L, "GeoClass1", "GC11");
        GeoClass geoClass2 = new GeoClass(2L, "GeoClass2", "GC21");
        Page<GeoClass> geoClassPage = new PageImpl<>(Arrays.asList(geoClass1, geoClass2));

        when(geoClassService.getAllGeoClasses(any(Pageable.class))).thenReturn(geoClassPage);

        // Act & Assert
        mockMvc.perform(get("/api/v1/geo-classes")
                        .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("admin:admin".getBytes()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("GeoClass1"))
                .andExpect(jsonPath("$.content[0].code").value("GC11"))
                .andExpect(jsonPath("$.content[1].name").value("GeoClass2"))
                .andExpect(jsonPath("$.content[1].code").value("GC21"));

        verify(geoClassService, times(1)).getAllGeoClasses(any(Pageable.class));
    }

    @Test
    void getGeoClassById_ShouldReturnGeoClass() throws Exception {
        // Arrange
        GeoClass geoClass = new GeoClass(1L, "GeoClass1", "GC11");

        when(geoClassService.getGeoClassById(1L)).thenReturn(geoClass);

        // Act & Assert
        mockMvc.perform(get("/api/v1/geo-classes/1")
                        .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("admin:admin".getBytes()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("GeoClass1"))
                .andExpect(jsonPath("$.code").value("GC11"));

        verify(geoClassService, times(1)).getGeoClassById(1L);
    }

    @Test
    void getGeoClassById_ShouldReturnNotFound_WhenGeoClassNotFound() throws Exception {
        // Arrange
        GeoClass geoClass = new GeoClass(1L, "GeoClass1", "GC11");

        when(geoClassService.getGeoClassById(1L)).thenThrow(GeoClassNotFoundException.class);

        // Act & Assert
        mockMvc.perform(get("/api/v1/geo-classes/1")
                        .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("admin:admin".getBytes()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(geoClassService, times(1)).getGeoClassById(1L);
    }

    @Test
    void createGeoClass_ShouldReturnCreatedGeoClass() throws Exception {
        // Arrange
        GeoClass geoClass = new GeoClass(null, "GeoClass1", "GC11");
        GeoClass savedGeoClass = new GeoClass(1L, "GeoClass1", "GC11");

        when(geoClassService.createGeoClass(any(GeoClass.class))).thenReturn(savedGeoClass);

        // Act & Assert
        mockMvc.perform(post("/api/v1/geo-classes")
                        .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("admin:admin".getBytes()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"GeoClass1\", \"code\": \"GC11\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("GeoClass1"))
                .andExpect(jsonPath("$.code").value("GC11"));

        verify(geoClassService, times(1)).createGeoClass(any(GeoClass.class));
    }

    @Test
    void createGeoClass_ShouldReturnBadRequestIfDataIsInvalid() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/geo-classes")
                        .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("admin:admin".getBytes()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"GeoClass1\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateGeoClass_ShouldReturnUpdatedGeoClass() throws Exception {
        // Arrange
        GeoClass updatedGeoClass = new GeoClass(1L, "UpdatedGeoClass", "GC11");

        when(geoClassService.updateGeoClass(eq(1L), any(GeoClass.class))).thenReturn(updatedGeoClass);

        // Act & Assert
        mockMvc.perform(put("/api/v1/geo-classes/1")
                        .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("admin:admin".getBytes()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"UpdatedGeoClass\", \"code\": \"GC11\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("UpdatedGeoClass"))
                .andExpect(jsonPath("$.code").value("GC11"));

        verify(geoClassService, times(1)).updateGeoClass(eq(1L), any(GeoClass.class));
    }

    @Test
    void updateGeoClass_ShouldReturnBadRequestIfDataIsInvalid() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/v1/geo-classes/1")
                        .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("admin:admin".getBytes()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"GeoClass1\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteGeoClass_ShouldReturnNoContent() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/v1/geo-classes/1")
                        .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("admin:admin".getBytes()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(geoClassService, times(1)).deleteGeoClass(1L);
    }
}
