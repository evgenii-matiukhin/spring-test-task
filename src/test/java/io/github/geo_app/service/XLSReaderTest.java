package io.github.geo_app.service;

import io.github.geo_app.model.Section;
import org.apache.poi.EncryptedDocumentException;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class XLSReaderTest {

    private final XLSReader xlsReader = new XLSReader();

    @Test
    void parse_ValidFile_ShouldReturnSections() throws IOException, EncryptedDocumentException {
        // Arrange
        File file = ResourceUtils.getFile("classpath:test-data/sections-valid.xls");
        try(FileInputStream fis = new FileInputStream(file)) {

            // Act
            List<Section> sections = xlsReader.parse(fis);

            // Assert
            assertNotNull(sections);
            assertFalse(sections.isEmpty());
            assertEquals(4, sections.size());

            Section section1 = sections.get(0);
            assertEquals("Section 1", section1.getName());
            assertEquals(3, section1.getGeoClasses().size());
            assertTrue(section1.getGeoClasses().stream()
                    .anyMatch(gc -> "GC11".equals(gc.getCode()) && "Geo Class 11".equals(gc.getName())));

            Section section2 = sections.get(1);
            assertEquals("Section 2", section2.getName());
            assertEquals(2, section2.getGeoClasses().size());
            assertTrue(section2.getGeoClasses().stream()
                    .anyMatch(gc -> "GC21".equals(gc.getCode()) && "Geo Class 21".equals(gc.getName())));

            Section section3 = sections.get(2);
            assertEquals("Section 3", section3.getName());
            assertEquals(2, section3.getGeoClasses().size());
            assertTrue(section3.getGeoClasses().stream()
                    .anyMatch(gc -> "GC3M".equals(gc.getCode()) && "Geo Class 3M".equals(gc.getName())));
        }
    }

    @Test
    void parse_EmptyFile_ShouldThrowException() throws IOException, EncryptedDocumentException {
        // Arrange
        File file = ResourceUtils.getFile("classpath:test-data/sections-empty.xls");
        try(FileInputStream fis = new FileInputStream(file)) {

            // Act & Assert
            assertThrows(Exception.class, () -> xlsReader.parse(fis));
        }
    }


    @Test
    void parse_NullInputStream_ShouldThrowException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> xlsReader.parse(null));
    }
}
