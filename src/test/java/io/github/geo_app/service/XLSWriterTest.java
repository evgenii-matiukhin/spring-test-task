package io.github.geo_app.service;

import io.github.geo_app.model.GeoClass;
import io.github.geo_app.model.Section;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class XLSWriterTest {

    private XLSWriter xlsWriter;

    @BeforeEach
    void setUp() {
        xlsWriter = new XLSWriter();
    }

    @Test
    void write_ShouldGenerateValidXLSFile() throws Exception {
        // Arrange
        List<Section> data = prepareMockData();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Act
        xlsWriter.write(outputStream, data);

        // Assert
        try (InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
             Workbook workbook = new HSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            assertNotNull(sheet, "Sheet should not be null");

            // Check headers
            Row headerRow = sheet.getRow(0);
            assertNotNull(headerRow, "Header row should exist");
            assertEquals("Section name", headerRow.getCell(0).getStringCellValue());
            assertEquals("Class 1 name", headerRow.getCell(1).getStringCellValue());
            assertEquals("Class 1 code", headerRow.getCell(2).getStringCellValue());

            // Check data rows
            Row dataRow = sheet.getRow(1);
            assertNotNull(dataRow, "Data row should exist");
            assertEquals("Section 1", dataRow.getCell(0).getStringCellValue());
            assertEquals("Geo Class 11", dataRow.getCell(1).getStringCellValue());
            assertEquals("GC11", dataRow.getCell(2).getStringCellValue());
        }
    }

    @Test
    void write_ShouldHandleEmptyData() throws Exception {
        // Arrange
        List<Section> emptyData = new ArrayList<>();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Act
        xlsWriter.write(outputStream, emptyData);

        // Assert
        try (InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
             Workbook workbook = new HSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            assertNotNull(sheet, "Sheet should not be null");

            // Check headers
            Row headerRow = sheet.getRow(0);
            assertNotNull(headerRow, "Header row should exist");
            assertEquals("Section name", headerRow.getCell(0).getStringCellValue());
        }
    }

    @Test
    void write_ShouldGenerateCorrectColumnsForMultipleClasses() throws Exception {
        // Arrange
        List<Section> data = prepareMockDataWithMultipleClasses();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Act
        xlsWriter.write(outputStream, data);

        // Assert
        try (InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
             Workbook workbook = new HSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            assertNotNull(sheet, "Sheet should not be null");

            // Check headers
            Row headerRow = sheet.getRow(0);
            assertNotNull(headerRow, "Header row should exist");
            assertEquals("Class 3 name", headerRow.getCell(5).getStringCellValue());
            assertEquals("Class 3 code", headerRow.getCell(6).getStringCellValue());

            // Check data rows
            Row dataRow = sheet.getRow(1);
            assertNotNull(dataRow, "Data row should exist");
            assertEquals("Geo Class 13", dataRow.getCell(5).getStringCellValue());
            assertEquals("GC13", dataRow.getCell(6).getStringCellValue());
        }
    }

    // Helper methods
    private List<Section> prepareMockData() {
        Section section1 = new Section();
        section1.setName("Section 1");

        GeoClass geoClass11 = new GeoClass();
        geoClass11.setName("Geo Class 11");
        geoClass11.setCode("GC11");

        GeoClass geoClass12 = new GeoClass();
        geoClass12.setName("Geo Class 12");
        geoClass12.setCode("GC12");

        Set<GeoClass> geoClasses1 = new HashSet<>(Arrays.asList(geoClass11, geoClass12));
        section1.setGeoClasses(geoClasses1);

        return Collections.singletonList(section1);
    }

    private List<Section> prepareMockDataWithMultipleClasses() {
        Section section1 = new Section();
        section1.setName("Section 1");

        GeoClass geoClass11 = new GeoClass();
        geoClass11.setName("Geo Class 11");
        geoClass11.setCode("GC11");

        GeoClass geoClass12 = new GeoClass();
        geoClass12.setName("Geo Class 12");
        geoClass12.setCode("GC12");

        GeoClass geoClass13 = new GeoClass();
        geoClass13.setName("Geo Class 13");
        geoClass13.setCode("GC13");

        Set<GeoClass> geoClasses1 = new HashSet<>(Arrays.asList(geoClass11, geoClass12, geoClass13));
        section1.setGeoClasses(geoClasses1);

        return Collections.singletonList(section1);
    }
}
