package io.github.geo_app.service;

import io.github.geo_app.model.GeoClass;
import io.github.geo_app.model.Section;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class XLSWriter {

    public void write(OutputStream outputStream, List<Section> sections) throws IOException, EncryptedDocumentException {
        // Excel sheet
        try(Workbook workbook = new HSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sections");
            log.debug("Sheet created");

            // header row
            Row headerRow = sheet.createRow(0);
            int maxGeoClasses = sections.stream().mapToInt(s -> s.getGeoClasses().size()).max().orElse(0);
            ArrayList<String> headers = new ArrayList<>();
            headers.add("Section name");
            for (int i = 1; i <= maxGeoClasses; i++) {
                headers.add("Class " + i + " name");
                headers.add("Class " + i + " code");
            }

            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
            }
            log.debug("Headers created");

            // data rows
            int rowIdx = 1;
            for (Section section : sections) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(section.getName());

                Set<GeoClass> geoClasses = section.getGeoClasses();
                int cellIdx = 1;
                for (GeoClass geoClass : geoClasses) {
                    row.createCell(cellIdx++).setCellValue(geoClass.getName());
                    row.createCell(cellIdx++).setCellValue(geoClass.getCode());
                }
            }

            // write
            workbook.write(outputStream);
        }
    }
}
