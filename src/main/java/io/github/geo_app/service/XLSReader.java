package io.github.geo_app.service;

import io.github.geo_app.model.GeoClass;
import io.github.geo_app.model.Section;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class XLSReader {

    public List<Section> parse(InputStream inputStream) throws IOException, EncryptedDocumentException {
        List<Section> sections = new ArrayList<>();
        try (inputStream;
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            log.debug("XlS file opened");

            Sheet sheet = workbook.getSheetAt(0);
            // Reading Headers in first row
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IllegalArgumentException("The Excel file is empty or invalid.");
            }
            log.debug("Headers are read");

            // Reading rest rows for sections data
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                log.debug("Reading row " + i);
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                Section newSection = new Section();
                String sectionName = getCellValue(row, 0);
                if (sectionName.isEmpty()) {
                    continue;
                }
                newSection.setName(sectionName);
                log.debug("Section found with name " + newSection.getName());

                for (int j = 1; j < headerRow.getLastCellNum(); j += 2) {
                    log.debug("Reading cell " + j);
                    String className = getCellValue(row, j);
                    String classCode = getCellValue(row, j + 1);
                    if (className.isEmpty() || classCode.isEmpty()) {
                        continue;
                    }
                    GeoClass geoClass = new GeoClass();
                    geoClass.setName(className);
                    geoClass.setCode(classCode);
                    newSection.getGeoClasses().add(geoClass);
                    log.debug("GeoClass found with name " + geoClass.getName() + " and code " + geoClass.getCode());
                }
                sections.add(newSection);
            }
            log.debug("All sections are read");
        }
        return sections;
    }

    private String getCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) {
            return "";
        }
        return cell.getStringCellValue();
    }
}
